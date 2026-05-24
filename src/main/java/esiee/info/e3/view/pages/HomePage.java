package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.UIButton;
import esiee.info.e3.view.components.UICard;
import esiee.info.e3.view.components.UIContainer;
import esiee.info.e3.view.components.UIText;
import esiee.info.e3.view.interfaces.IPage;
import esiee.info.e3.view.utils.BackgroundCard;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class HomePage implements IPage {
    private final UIContainer root;
    private final List<BackgroundCard> backgroundCards = new ArrayList<>();
    private final Random random = new Random();
    private final ViewMain context;
    private boolean initialized = false;
    private final GameController controller;

    public HomePage(ViewMain context, GameController controller) {
        this.context = Objects.requireNonNull(context);
        this.controller = Objects.requireNonNull(controller);

        var font = context.getGameFont();

        var rootStyle = new UIStyle.Builder().bg(new Color(0,0,0,0)).build();

        var classicBtnStyle = new UIStyle.Builder()
                .bg(new Color(34, 112, 63))
                .hoverBg(new Color(46, 139, 87))
                .text(Color.WHITE)
                .font(font.deriveFont(28f))
                .radius(5)
                .border(Color.WHITE, 3f)
                .margin(10)
                .shadow(new Color(0, 0, 0, 100), 8)
                .maxWidth(450)
                .maxHeight(70)
                .build();

        var infiniteBtnStyle = new UIStyle.Builder()
                .bg(new Color(148, 0, 211))
                .hoverBg(new Color(186, 85, 211))
                .text(Color.WHITE)
                .font(font.deriveFont(28f))
                .radius(5)
                .border(Color.WHITE, 3f)
                .margin(10)
                .shadow(new Color(0, 0, 0, 100), 8)
                .maxWidth(450)
                .maxHeight(70)
                .build();

        var btnStyle = new UIStyle.Builder()
                .bg(new Color(40, 150, 40))
                .hoverBg(new Color(60, 180, 60))
                .text(Color.WHITE)
                .font(font.deriveFont(28f))
                .radius(5)
                .border(Color.WHITE, 3f)
                .margin(10)
                .shadow(new Color(0, 0, 0, 100), 8)
                .maxWidth(450)
                .maxHeight(70)
                .build();

        var quitBtnStyle = new UIStyle.Builder()
                .bg(new Color(180, 40, 40))
                .hoverBg(new Color(220, 60, 60))
                .text(Color.WHITE)
                .font(font.deriveFont(28f))
                .radius(5)
                .border(Color.WHITE, 3f)
                .margin(10)
                .shadow(new Color(0, 0, 0, 100), 8)
                .maxWidth(450)
                .maxHeight(70)
                .build();

        var titleStyle = new UIStyle.Builder()
                .text(new Color(150, 150, 150))
                .font(font.deriveFont(160f))
                .shadow(new Color(0, 0, 0, 240), 8)
                .build();

        var versionStyle = new UIStyle.Builder()
                .text(new Color(150, 150, 150))
                .font(font.deriveFont(12f))
                .build();

        this.root = new UIContainer(10, 1, rootStyle);

        this.root.addComponent(new UIText("BALATRI", titleStyle), 1, 0, 0.4, 1.0);

        this.root.addComponent(
                new UIButton("MODE CLASSIQUE", classicBtnStyle, () -> {
                    context.navigateTo("game", true);
                    controller.startGame(false);
                }), 4, 0, 0.12, 1.0);

        this.root.addComponent(
                new UIButton("MODE INFINI", infiniteBtnStyle, () -> {
                    context.navigateTo("game", true);
                    controller.startGame(true);
                }), 5, 0, 0.12, 1.0);

        this.root.addComponent(
                new UIButton("LISTE DES PARTIES", btnStyle,
                        () -> context.navigateTo("saves", true)), 6, 0, 0.12, 1.0);

        this.root.addComponent(
                new UIButton("QUITTER LE JEU", quitBtnStyle,
                        this.controller::exitGame), 7, 0, 0.12, 1.0);

        this.root.addComponent(
                new UIText("v0.0.1-STABLE // BUILD_ESIEE_E3", versionStyle), 8, 0, 0.1, 1.0);
    }

    private void initBackgroundCards(float sw, float sh) {
        for (int i = 0; i < 15; i++) {
            createRandomCard(sw, sh, true);
        }
        initialized = true;
    }

    private void createRandomCard(float sw, float sh, boolean randomY) {
        Rank r = Rank.values()[random.nextInt(Rank.values().length)];
        Suit s = Suit.values()[random.nextInt(Suit.values().length)];
        Card card = new Card(r, s);

        float x = random.nextFloat() * sw;
        float y = randomY ? random.nextFloat() * sh : sh + 200;
        float speed = 1.0f + random.nextFloat() * 2.5f;
        float rot = random.nextFloat() * 360;
        float rotS = -1f + random.nextFloat() * 2f;

        UICard ui = new UICard(card, true, () -> false, null, context, 71.0/95.0);
        backgroundCards.add(new BackgroundCard(x, y, speed, rot, rotS, ui));
    }

    @Override
    public void render(Graphics2D g, float sw, float sh) {
        if (!initialized) initBackgroundCards(sw, sh);

        g.setColor(new Color(15, 25, 20));
        g.fillRect(0, 0, (int) sw, (int) sh);

        for (int i = backgroundCards.size() - 1; i >= 0; i--) {
            BackgroundCard bc = backgroundCards.get(i);

            float newY = bc.y() - bc.speed();
            float newRot = bc.rotation() + bc.rotSpeed();

            if (newY < -200) {
                backgroundCards.remove(i);
                createRandomCard(sw, sh, false);
                continue;
            }

            var updatedCard = new BackgroundCard(bc.x(), newY, bc.speed(), newRot, bc.rotSpeed(), bc.uiComponent());
            backgroundCards.set(i, updatedCard);

            var oldTransform = g.getTransform();
            g.translate(updatedCard.x(), updatedCard.y());
            g.rotate(Math.toRadians(updatedCard.rotation()));

            updatedCard.uiComponent().render(g, -50, -70, 100, 140);

            g.setTransform(oldTransform);
        }

        this.root.render(g, 0, 0, (int) sw, (int) sh);
    }

    @Override
    public void handlePointerClick(int mx, int my, float sw, float sh) {
        this.root.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
    }

    @Override
    public void handlePointerMove(int mx, int my, float sw, float sh) {
        this.root.handlePointerMove(mx, my, 0, 0, (int) sw, (int) sh);
    }

    @Override
    public void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {}

    @Override
    public void showOverlay(String message, Color color, Runnable onClose) {}
}