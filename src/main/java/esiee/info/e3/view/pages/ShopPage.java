package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.domain.ShopItem;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.model.IGameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.UIButton;
import esiee.info.e3.view.components.UIContainer;
import esiee.info.e3.view.components.UIJoker;
import esiee.info.e3.view.components.UIText;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class ShopPage implements IPage {
  private final ViewMain context;
  private final List<ShopItem> currentShopItems;
  private final List<ShopItem> sessionPurchases;
  private final double CARD_RATIO;
  private IGameState currentState;
  private UIContainer rootContainer;
  private UIContainer shopItemsContainer;
  private UIContainer inventoryContainer;
  private JokerType pendingPurchaseJoker;
  private int pendingPurchasePrice;
  private UIContainer swapModalContainer;
  private UIContainer previewModalContainer;
  private String errorMessage;
  private long errorMessageTime;

  public ShopPage(ViewMain context) {
    this.context = Objects.requireNonNull(context);

    this.currentShopItems = new ArrayList<>();
    this.sessionPurchases = new ArrayList<>();
    this.CARD_RATIO = 71.0 / 95.0;
    this.pendingPurchaseJoker = null;
    this.pendingPurchasePrice = 0;
    this.errorMessage = null;
    this.errorMessageTime = 0;

    this.buildLayout();
  }

  private void buildLayout() {
    var rootStyle = new UIStyle.Builder().bg(new Color(25, 35, 45)).build();
    this.rootContainer = new UIContainer(100, 100, rootStyle);

    var titleStyle =
        new UIStyle.Builder()
            .text(Color.ORANGE)
            .font(this.context.getGameFont().deriveFont(36f))
            .shadow(Color.BLACK, 3)
            .build();
    this.rootContainer.addComponent(
        new UIText(TextConstant.TEXT_SHOP_TITLE.getText(), titleStyle), 2, 5, 0.1, 0.4);

    var moneyStyle =
        new UIStyle.Builder()
            .text(Color.YELLOW)
            .font(this.context.getGameFont().deriveFont(30f))
            .shadow(Color.BLACK, 3)
            .build();
    this.rootContainer.addComponent(
        new UIText(
            () ->
                TextConstant.TEXT_SHOP_MONEY
                    .getText()
                    .formatted(this.currentState != null ? this.currentState.getMoney() : 0),
            moneyStyle),
        2,
        70,
        0.1,
        0.25);

    var boxStyle =
        new UIStyle.Builder()
            .bg(new Color(0, 0, 0, 120))
            .radius(15)
            .border(Color.DARK_GRAY, 3f)
            .build();
    this.shopItemsContainer = new UIContainer(100, 100, boxStyle);
    this.rootContainer.addComponent(this.shopItemsContainer, 15, 5, 0.50, 0.90);

    this.inventoryContainer = new UIContainer(100, 100, boxStyle);
    this.rootContainer.addComponent(this.inventoryContainer, 68, 5, 0.20, 0.65);

    var nextStyle =
        new UIStyle.Builder()
            .bg(new Color(40, 150, 40))
            .text(Color.WHITE)
            .radius(15)
            .font(this.context.getGameFont().deriveFont(26f))
            .build();
    this.rootContainer.addComponent(
        new UIButton(
            TextConstant.TEXT_SHOP_NEXT_LEVEL.getText(),
            nextStyle,
            () -> {
              this.currentShopItems.clear();
              this.sessionPurchases.clear();
              this.context.navigateTo(RoutesEnum.GAME, false);
            }),
        70,
        75,
        0.15,
        0.2);

    var rerollStyle =
        new UIStyle.Builder()
            .bg(new Color(150, 80, 40))
            .text(Color.WHITE)
            .radius(10)
            .font(this.context.getGameFont().deriveFont(20f))
            .build();
    this.rootContainer.addComponent(
        new UIButton(
            TextConstant.TEXT_SHOP_REROLL.getText(),
            rerollStyle,
            () -> {
              if (this.currentState != null && this.currentState.getMoney() >= 5) {
                this.currentState.spendMoney(5);
                this.rollShopItems();
                this.rebuildShopUI();
              } else {
                this.showError(TextConstant.TEXT_SHOP_ERROR_NO_MONEY_REROLL.getText());
              }
            }),
        10,
        80,
        0.05,
        0.12);
  }

  private void rollShopItems() {
    this.currentShopItems.clear();
    var random = new Random();

    var availableJokers = new ArrayList<>(List.of(JokerType.values()));
    if (this.currentState != null) {
      availableJokers.removeAll(this.currentState.getActiveJokers());
    }

    for (var i = 0; i < 4 && !availableJokers.isEmpty(); i++) {
      var j = availableJokers.remove(random.nextInt(availableJokers.size()));
      var price = 5 + random.nextInt(6);
      this.currentShopItems.add(new ShopItem(j, price));
    }
  }

  private void rebuildShopUI() {
    this.shopItemsContainer.clearChildren();
    var subtitleStyle =
        new UIStyle.Builder()
            .text(Color.LIGHT_GRAY)
            .font(this.context.getGameFont().deriveFont(22f))
            .build();
    this.shopItemsContainer.addComponent(
        new UIText(TextConstant.TEXT_SHOP_ITEMS_FOR_SALE.getText(), subtitleStyle), 5, 2, 0.1, 0.3);

    var startX = 5;
    for (var i = 0; i < this.currentShopItems.size(); i++) {
      var si = this.currentShopItems.get(i);
      var idx = i;

      if (si.item() instanceof JokerType joker) {
        var uiJoker =
            new UIJoker(joker, this.context, () -> this.showPreview(idx), this.CARD_RATIO);
        this.shopItemsContainer.addComponent(uiJoker, 20, startX, 0.60, 0.15);

        var btnStyle =
            new UIStyle.Builder()
                .bg(new Color(50, 50, 180))
                .text(Color.WHITE)
                .radius(8)
                .font(this.context.getGameFont().deriveFont(18f))
                .build();
        this.shopItemsContainer.addComponent(
            new UIButton(
                TextConstant.TEXT_SHOP_PRICE_TAG.getText().formatted(si.price()),
                btnStyle,
                () -> this.showPreview(idx)),
            85,
            startX,
            0.12,
            0.15);
      }
      startX += 22;
    }
  }

  private void showPreview(int index) {
    if (index < 0 || index >= this.currentShopItems.size()) return;
    var si = this.currentShopItems.get(index);

    if (this.currentState.getMoney() < si.price()) {
      this.showError(TextConstant.TEXT_SHOP_ERROR_NO_MONEY_BUY.getText());
      return;
    }

    if (si.item() instanceof JokerType joker) {
      var modalStyle =
          new UIStyle.Builder()
              .bg(new Color(20, 20, 30, 245))
              .radius(15)
              .border(Color.CYAN, 3f)
              .padding(15)
              .build();
      this.previewModalContainer = new UIContainer(100, 100, modalStyle);

      var nameStyle =
          new UIStyle.Builder()
              .text(Color.CYAN)
              .font(context.getGameFont().deriveFont(32f))
              .build();
      this.previewModalContainer.addComponent(
          new UIText(joker.getJokerName(), nameStyle), 8, 5, 0.15, 0.90);

      var uiJoker = new UIJoker(joker, this.context, null, this.CARD_RATIO);
      this.previewModalContainer.addComponent(uiJoker, 25, 10, 0.50, 0.25);

      var descStyle =
          new UIStyle.Builder()
              .text(Color.WHITE)
              .font(context.getGameFont().deriveFont(18f))
              .build();
      this.previewModalContainer.addComponent(
          new UIText(
              TextConstant.TEXT_SHOP_EFFECT.getText().formatted(joker.getDescription()), descStyle),
          30,
          40,
          0.4,
          0.55);

      var priceStyle =
          new UIStyle.Builder()
              .text(Color.YELLOW)
              .font(context.getGameFont().deriveFont(26f))
              .build();
      this.previewModalContainer.addComponent(
          new UIText(
              TextConstant.TEXT_SHOP_PRICE_LABEL.getText().formatted(si.price()), priceStyle),
          65,
          40,
          0.1,
          0.4);

      var buyStyle =
          new UIStyle.Builder()
              .bg(new Color(40, 150, 40))
              .text(Color.WHITE)
              .radius(10)
              .font(context.getGameFont().deriveFont(24f))
              .build();
      this.previewModalContainer.addComponent(
          new UIButton(
              TextConstant.TEXT_SHOP_BUY.getText(),
              buyStyle,
              () -> {
                this.previewModalContainer = null;
                this.executePurchase(si, index);
              }),
          80,
          20,
          0.12,
          0.25);

      var cancelStyle =
          new UIStyle.Builder()
              .bg(Color.RED)
              .text(Color.WHITE)
              .radius(10)
              .font(context.getGameFont().deriveFont(24f))
              .build();
      this.previewModalContainer.addComponent(
          new UIButton(
              TextConstant.TEXT_SHOP_CANCEL_PREVIEW.getText(),
              cancelStyle,
              () -> {
                this.previewModalContainer = null;
              }),
          80,
          55,
          0.12,
          0.25);
    }
  }

  private void executePurchase(ShopItem si, int shopIndex) {
    if (si.item() instanceof JokerType joker) {
      if (this.currentState.isJokersFull()) {
        this.triggerJokerSwap(joker, si.price(), shopIndex);
      } else {
        this.currentState.spendMoney(si.price());
        this.currentState.addJoker(joker);
        this.currentShopItems.remove(shopIndex);
        this.sessionPurchases.add(si);
        this.rebuildShopUI();
        this.rebuildInventoryUI();
      }
    }
  }

  private void showRefundPreview(ShopItem si) {
    if (si.item() instanceof JokerType joker) {
      var modalStyle =
          new UIStyle.Builder()
              .bg(new Color(20, 20, 20, 245))
              .radius(15)
              .border(Color.ORANGE, 3f)
              .padding(15)
              .build();
      this.previewModalContainer = new UIContainer(100, 100, modalStyle);

      var nameStyle =
          new UIStyle.Builder()
              .text(Color.ORANGE)
              .font(context.getGameFont().deriveFont(32f))
              .build();
      this.previewModalContainer.addComponent(
          new UIText(
              TextConstant.TEXT_SHOP_REFUND_QUESTION.getText().formatted(joker.getJokerName()),
              nameStyle),
          10,
          5,
          0.1,
          0.90);

      var uiJoker = new UIJoker(joker, this.context, null, this.CARD_RATIO);
      this.previewModalContainer.addComponent(uiJoker, 30, 42, 0.40, 0.16);

      var descStyle =
          new UIStyle.Builder()
              .text(Color.WHITE)
              .font(context.getGameFont().deriveFont(22f))
              .build();
      this.previewModalContainer.addComponent(
          new UIText(
              TextConstant.TEXT_SHOP_REFUND_LABEL.getText().formatted(si.price()), descStyle),
          75,
          30,
          0.1,
          0.4);

      var buyStyle =
          new UIStyle.Builder()
              .bg(new Color(150, 40, 40))
              .text(Color.WHITE)
              .radius(10)
              .font(context.getGameFont().deriveFont(20f))
              .build();
      this.previewModalContainer.addComponent(
          new UIButton(
              TextConstant.TEXT_SHOP_REFUND_CONFIRM.getText(),
              buyStyle,
              () -> {
                this.previewModalContainer = null;
                this.currentState.addMoney(si.price());
                this.currentState.removeJoker(joker);

                if (si.replacedJoker() != null) {
                  this.currentState.addJoker(si.replacedJoker());
                }

                this.sessionPurchases.remove(si);

                this.currentShopItems.add(new ShopItem(si.item(), si.price()));
                this.rebuildShopUI();
                this.rebuildInventoryUI();
              }),
          85,
          20,
          0.15,
          0.30);

      var cancelStyle =
          new UIStyle.Builder()
              .bg(Color.DARK_GRAY)
              .text(Color.WHITE)
              .radius(10)
              .font(context.getGameFont().deriveFont(20f))
              .build();
      this.previewModalContainer.addComponent(
          new UIButton(
              TextConstant.TEXT_SHOP_REFUND_KEEP.getText(),
              cancelStyle,
              () -> {
                this.previewModalContainer = null;
              }),
          85,
          55,
          0.10,
          0.25);
    }
  }

  private void triggerJokerSwap(JokerType newJoker, int price, int shopIndex) {
    this.pendingPurchaseJoker = newJoker;
    this.pendingPurchasePrice = price;

    var modalStyle =
        new UIStyle.Builder()
            .bg(new Color(20, 20, 20, 245))
            .radius(15)
            .border(Color.ORANGE, 3f)
            .padding(15)
            .build();
    this.swapModalContainer = new UIContainer(100, 100, modalStyle);

    var headerStyle =
        new UIStyle.Builder().text(Color.RED).font(context.getGameFont().deriveFont(24f)).build();
    this.swapModalContainer.addComponent(
        new UIText(TextConstant.TEXT_SHOP_ERROR_INVENTORY_FULL.getText(), headerStyle),
        5,
        2,
        0.1,
        0.90);

    var actives = this.currentState.getActiveJokers();
    var itemX = 5;
    for (var oldJoker : actives) {
      var actJokerComp =
          new UIJoker(
              oldJoker,
              this.context,
              () -> {
                this.currentState.spendMoney(this.pendingPurchasePrice);
                this.currentState.removeJoker(oldJoker);
                this.currentState.addJoker(this.pendingPurchaseJoker);
                this.sessionPurchases.add(
                    new ShopItem(this.pendingPurchaseJoker, this.pendingPurchasePrice, oldJoker));
                this.currentShopItems.remove(shopIndex);
                this.pendingPurchaseJoker = null;
                this.swapModalContainer = null;
                this.rebuildShopUI();
                this.rebuildInventoryUI();
              },
              this.CARD_RATIO);
      this.swapModalContainer.addComponent(actJokerComp, 25, itemX, 0.45, 0.16);
      itemX += 19;
    }

    var cancelStyle =
        new UIStyle.Builder()
            .bg(Color.RED)
            .text(Color.WHITE)
            .radius(10)
            .font(context.getGameFont().deriveFont(20f))
            .build();
    this.swapModalContainer.addComponent(
        new UIButton(
            TextConstant.TEXT_SHOP_CANCEL_SWAP.getText(),
            cancelStyle,
            () -> {
              this.pendingPurchaseJoker = null;
              this.swapModalContainer = null;
            }),
        80,
        40,
        0.12,
        0.20);
  }

  private void rebuildInventoryUI() {
    this.inventoryContainer.clearChildren();
    var subtitleStyle =
        new UIStyle.Builder()
            .text(Color.LIGHT_GRAY)
            .font(this.context.getGameFont().deriveFont(18f))
            .build();
    this.inventoryContainer.addComponent(
        new UIText(TextConstant.TEXT_SHOP_YOUR_JOKERS.getText(), subtitleStyle), 5, 2, 0.15, 0.3);

    if (this.currentState != null) {
      var actives = this.currentState.getActiveJokers();
      var jokerX = 5;
      for (var joker : actives) {
        var refundable =
            this.sessionPurchases.stream()
                .filter(si -> si.item().equals(joker))
                .findFirst()
                .orElse(null);

        var uiJoker =
            new UIJoker(
                joker,
                this.context,
                () -> {
                  if (refundable != null) {
                    this.showRefundPreview(refundable);
                  }
                },
                this.CARD_RATIO);
        this.inventoryContainer.addComponent(uiJoker, 25, jokerX, 0.70, 0.16);
        jokerX += 18;
      }
    }
  }

  @Override
  public void update(GameSnapshot gameSnapshot) {
    this.currentState = gameSnapshot.state();

    if (this.currentShopItems.isEmpty()) {
      this.rollShopItems();
      this.rebuildShopUI();
    }

    this.rebuildInventoryUI();
  }

  private void showError(String message) {
    this.errorMessage = message;
    this.errorMessageTime = System.currentTimeMillis();
  }

  @Override
  public void showOverlay(String m, Color c, Runnable o) {}

  @Override
  public void render(Graphics2D g, float sw, float sh) {
    this.rootContainer.render(g, 0, 0, (int) sw, (int) sh);

    if (this.previewModalContainer != null) {
      g.setColor(new Color(0, 0, 0, 200));
      g.fillRect(0, 0, (int) sw, (int) sh);
      var mw = (int) (sw * 0.80);
      var mh = (int) (sh * 0.65);
      var mx = (int) (sw - mw) / 2;
      var my = (int) (sh - mh) / 2;
      this.previewModalContainer.render(g, mx, my, mw, mh);
    } else if (this.swapModalContainer != null) {
      g.setColor(new Color(0, 0, 0, 200));
      g.fillRect(0, 0, (int) sw, (int) sh);
      var mw = (int) (sw * 0.85);
      var mh = (int) (sh * 0.75);
      var mx = (int) (sw - mw) / 2;
      var my = (int) (sh - mh) / 2;
      this.swapModalContainer.render(g, mx, my, mw, mh);
    }

    if (this.errorMessage != null) {
      var elapsed = System.currentTimeMillis() - this.errorMessageTime;
      if (elapsed > 2000) {
        this.errorMessage = null;
      } else {
        g.setFont(this.context.getGameFont().deriveFont(28f));
        var metrics = g.getFontMetrics();
        var textW = metrics.stringWidth(this.errorMessage);
        var msgX = (int) (sw - textW) / 2;
        var msgY = (int) (sh * 0.15);

        var padding = 15;
        g.setColor(new Color(180, 20, 20, 220));
        g.fillRoundRect(
            msgX - padding,
            msgY - metrics.getAscent() - padding,
            textW + padding * 2,
            metrics.getHeight() + padding * 2,
            10,
            10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(
            msgX - padding,
            msgY - metrics.getAscent() - padding,
            textW + padding * 2,
            metrics.getHeight() + padding * 2,
            10,
            10);

        g.setColor(Color.WHITE);
        g.drawString(this.errorMessage, msgX, msgY);
      }
    }
  }

  @Override
  public void handlePointerClick(int mx, int my, float sw, float sh) {
    if (this.previewModalContainer != null) {
      var mw = (int) (sw * 0.80);
      var mh = (int) (sh * 0.65);
      var rx = (int) (sw - mw) / 2;
      var ry = (int) (sh - mh) / 2;
      this.previewModalContainer.handlePointerClick(mx, my, rx, ry, mw, mh);
      return;
    }
    if (this.swapModalContainer != null) {
      var mw = (int) (sw * 0.85);
      var mh = (int) (sh * 0.75);
      var rx = (int) (sw - mw) / 2;
      var ry = (int) (sh - mh) / 2;
      this.swapModalContainer.handlePointerClick(mx, my, rx, ry, mw, mh);
      return;
    }
    this.rootContainer.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
  }
}
