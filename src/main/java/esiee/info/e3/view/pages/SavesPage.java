package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.RoutesEnum;
import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.IGameController;
import esiee.info.e3.domain.GameSnapshot;
import esiee.info.e3.domain.SaveSlot;
import esiee.info.e3.manager.ISaveManager;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.*;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SavesPage implements IPage {
  private final ViewMain context;
  private final IGameController controller;
  private final UIContainer rootContainer;
  private final ISaveManager saveManager;

  private final List<SaveSlot> allSaves;
  private final int itemsPerPage;
  private int currentPageIndex;
  private long lastRefreshTime;

  private String overlayMessage;
  private Color overlayColor;
  private Runnable onOverlayClose;

  public SavesPage(ViewMain context, IGameController controller, ISaveManager saveManager) {
    this.context = Objects.requireNonNull(context);
    this.controller = Objects.requireNonNull(controller);
    this.saveManager = Objects.requireNonNull(saveManager);

    this.allSaves = new ArrayList<>();
    this.itemsPerPage = 4;
    this.currentPageIndex = 0;
    this.lastRefreshTime = 0;
    this.overlayMessage = null;
    this.overlayColor = null;
    this.onOverlayClose = null;

    this.loadSavesFromJson();
    var rootStyle = new UIStyle.Builder().bg(new Color(15, 25, 20)).margin(15).build();
    this.rootContainer = new UIContainer(1, 1, rootStyle);

    buildSavesUI();
  }

  private void sortSavesDesc(List<SaveSlot> saves) {
    var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    saves.sort(
        (s1, s2) -> {
          try {
            var d1 = LocalDateTime.parse(s1.date(), formatter);
            var d2 = LocalDateTime.parse(s2.date(), formatter);
            return d2.compareTo(d1);
          } catch (Exception e) {
            return s2.id().compareTo(s1.id());
          }
        });
  }

  private void loadSavesFromJson() {
    allSaves.clear();
    var loadedSaves = this.saveManager.loadAllSaves();
    sortSavesDesc(loadedSaves);
    this.allSaves.addAll(loadedSaves);
  }

  private void checkAndRefreshSaves() {
    if (System.currentTimeMillis() - lastRefreshTime < 500) return;
    lastRefreshTime = System.currentTimeMillis();

    var newSaves = this.saveManager.loadAllSaves();
    sortSavesDesc(newSaves);

    if (!newSaves.equals(this.allSaves)) {
      this.allSaves.clear();
      this.allSaves.addAll(newSaves);

      var maxPage = Math.max(0, (this.allSaves.size() - 1) / itemsPerPage);
      if (currentPageIndex > maxPage) {
        currentPageIndex = maxPage;
      }

      buildSavesUI();
    }
  }

  private void buildSavesUI() {
    this.rootContainer.clearChildren();

    var panelStyle =
        new UIStyle.Builder().bg(new Color(24, 34, 29)).padding(20).margin(20).radius(15).build();
    var savesPanel = new UIContainer(100, 100, panelStyle);

    var titleStyle =
        new UIStyle.Builder()
            .text(Color.YELLOW)
            .font(context.getGameFont().deriveFont(40f))
            .build();
    savesPanel.addComponent(
        new UIText(TextConstant.TEXT_SAVES_TITLE.getText(), titleStyle), 2, 0, 0.15, 1.0);

    var listContainer = new UIContainer(itemsPerPage, 1, new UIStyle.Builder().build());

    var font20 = context.getGameFont().deriveFont(20f);
    var font18 = context.getGameFont().deriveFont(18f);

    if (allSaves.isEmpty()) {
      var emptyStyle =
          new UIStyle.Builder()
              .text(Color.LIGHT_GRAY)
              .font(context.getGameFont().deriveFont(26f))
              .build();
      listContainer.addComponent(
          new UIText(TextConstant.TEXT_SAVES_EMPTY.getText(), emptyStyle), 0, 0, 1.0, 1.0);
    } else {
      var start = currentPageIndex * itemsPerPage;
      var end = Math.min(start + itemsPerPage, allSaves.size());
      var row = 0;

      for (var i = start; i < end; i++) {
        var save = allSaves.get(i);

        var slotStyle =
            new UIStyle.Builder().bg(new Color(38, 28, 22)).radius(8).margin(5).padding(5).build();
        var slotRow = new UIContainer(100, 100, slotStyle);

        var animBox = new UIContainer(1, 1, new UIStyle.Builder().build());
        animBox.addComponent(
            UIGenericAnimation.createBlindChipAnimation(context, save::blindId), 0, 0, 1.0, 1.0);
        slotRow.addComponent(animBox, 10, 2, 0.80, 0.08);

        var dateStyle = new UIStyle.Builder().text(Color.LIGHT_GRAY).font(font18).build();
        slotRow.addComponent(new UIText(save.date(), dateStyle), 0, 12, 1.0, 0.15);

        var modeText =
            save.infiniteMode()
                ? TextConstant.TEXT_SAVES_MODE_INFINITE.getText().formatted(save.loopCount())
                : TextConstant.TEXT_SAVES_MODE_CLASSIC.getText();
        var modeColor = save.infiniteMode() ? new Color(186, 85, 211) : Color.GRAY;
        var modeStyle = new UIStyle.Builder().text(modeColor).font(font18).build();
        slotRow.addComponent(new UIText(modeText, modeStyle), 0, 28, 1.0, 0.12);

        var nameStyle = new UIStyle.Builder().text(Color.WHITE).font(font20).build();
        slotRow.addComponent(new UIText(save.lastBlindName(), nameStyle), 0, 41, 1.0, 0.18);

        var scoreStyle = new UIStyle.Builder().text(new Color(255, 215, 0)).font(font20).build();
        var scoreText = TextConstant.TEXT_SAVES_SCORE_SUFFIX.getText().formatted(save.score());
        slotRow.addComponent(new UIText(scoreText, scoreStyle), 0, 60, 1.0, 0.12);

        var statusColor =
            save.status().equals(TextConstant.TEXT_SAVES_STATUS_VICTORY.getText())
                ? new Color(50, 220, 50)
                : save.status().equals(TextConstant.TEXT_SAVES_STATUS_DEFEAT.getText())
                    ? new Color(220, 50, 50)
                    : new Color(50, 150, 255);
        var statusStyle = new UIStyle.Builder().text(statusColor).font(font18).build();
        slotRow.addComponent(new UIText(save.status(), statusStyle), 0, 73, 1.0, 0.12);

        if (save.canResume()) {
          var btnStyle =
              new UIStyle.Builder()
                  .bg(new Color(70, 130, 180))
                  .text(Color.WHITE)
                  .radius(8)
                  .margin(8)
                  .font(font18)
                  .build();
          var resumeBtn =
              new UIButton(
                  TextConstant.TEXT_SAVES_RESUME.getText(),
                  btnStyle,
                  () -> controller.loadGameFromJson(save.id()));
          slotRow.addComponent(resumeBtn, 0, 86, 1.0, 0.12);
        } else {
          slotRow.addComponent(
              new UIText("-", new UIStyle.Builder().text(Color.DARK_GRAY).font(font20).build()),
              0,
              86,
              1.0,
              0.12);
        }

        listContainer.addComponent(slotRow, row++, 0, 1.0 / itemsPerPage, 1.0);
      }
    }
    savesPanel.addComponent(listContainer, 17, 5, 0.65, 0.90);

    var navRow = new UIContainer(100, 100, new UIStyle.Builder().build());
    var navFont = context.getGameFont().deriveFont(22f);

    var btnStyle =
        new UIStyle.Builder()
            .bg(new Color(100, 100, 100))
            .text(Color.WHITE)
            .radius(8)
            .font(navFont)
            .build();
    var backStyle =
        new UIStyle.Builder()
            .bg(new Color(139, 0, 0))
            .text(Color.WHITE)
            .radius(8)
            .font(navFont)
            .build();

    if (currentPageIndex > 0) {
      navRow.addComponent(
          new UIButton(
              TextConstant.TEXT_SAVES_PREVIOUS.getText(),
              btnStyle,
              () -> {
                currentPageIndex--;
                buildSavesUI();
              }),
          10,
          10,
          0.80,
          0.20);
    }

    navRow.addComponent(
        new UIButton(
            TextConstant.TEXT_SAVES_RETURN.getText(),
            backStyle,
            () -> controller.goTo(RoutesEnum.HOME, false)),
        10,
        40,
        0.80,
        0.20);

    if ((currentPageIndex + 1) * itemsPerPage < allSaves.size()) {
      navRow.addComponent(
          new UIButton(
              TextConstant.TEXT_SAVES_NEXT.getText(),
              btnStyle,
              () -> {
                currentPageIndex++;
                buildSavesUI();
              }),
          10,
          70,
          0.80,
          0.20);
    }

    savesPanel.addComponent(navRow, 85, 0, 0.12, 1.0);

    this.rootContainer.addComponent(savesPanel, 0, 0, 1.0, 1.0);
  }

  @Override
  public void render(Graphics2D g, float sw, float sh) {
    checkAndRefreshSaves();

    this.rootContainer.render(g, 0, 0, (int) sw, (int) sh);

    var margin = 20;
    var borderX = margin;
    var borderY = margin;
    var borderW = (int) sw - (margin * 2);
    var borderH = (int) sh - (margin * 2);

    var thickBlack = 6;
    var thickBrown = 4;

    g.setColor(Color.BLACK);
    g.setStroke(new BasicStroke(thickBlack));
    g.drawRect(borderX, borderY, borderW, borderH);

    g.setColor(new Color(90, 60, 40));
    g.setStroke(new BasicStroke(thickBrown));
    g.drawRect(
        borderX + thickBlack / 2 + 2,
        borderY + thickBlack / 2 + 2,
        borderW - thickBlack - 4,
        borderH - thickBlack - 4);

    g.setColor(Color.BLACK);
    g.setStroke(new BasicStroke(2));
    g.drawRect(
        borderX + thickBlack + thickBrown,
        borderY + thickBlack + thickBrown,
        borderW - (thickBlack + thickBrown) * 2,
        borderH - (thickBlack + thickBrown) * 2);

    if (this.overlayMessage != null) {
      g.setColor(new Color(0, 0, 0, 200));
      g.fillRect(0, 0, (int) sw, (int) sh);
      g.setFont(context.getGameFont().deriveFont(32f));
      var metrics = g.getFontMetrics();
      var msgWidth = metrics.stringWidth(this.overlayMessage);
      var msgAscent = metrics.getAscent();

      g.setColor(Objects.requireNonNullElse(this.overlayColor, Color.WHITE));
      g.drawString(this.overlayMessage, (int) ((sw - msgWidth) / 2), (int) ((sh + msgAscent) / 2));

      g.setFont(context.getGameFont().deriveFont(16f));
      var clickMsg = TextConstant.TEXT_SAVES_CLICK_CLOSE.getText();
      g.setColor(Color.LIGHT_GRAY);
      g.drawString(
          clickMsg,
          (int) ((sw - metrics.stringWidth(clickMsg)) / 2),
          (int) ((sh + msgAscent) / 2) + 40);
    }
  }

  @Override
  public void handlePointerClick(int mx, int my, float sw, float sh) {
    if (this.overlayMessage != null) {
      this.overlayMessage = null;
      if (this.onOverlayClose != null) {
        var action = this.onOverlayClose;
        this.onOverlayClose = null;
        action.run();
      }
      return;
    }

    this.rootContainer.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
  }

  @Override
  public void update(GameSnapshot snapshot) {}

  @Override
  public void showOverlay(String message, Color color, Runnable onClose) {
    this.overlayMessage = message;
    this.overlayColor = color;
    this.onOverlayClose = onClose;
  }
}
