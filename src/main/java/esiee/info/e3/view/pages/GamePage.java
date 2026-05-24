package esiee.info.e3.view.pages;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.JokerContext;
import esiee.info.e3.domain.enums.JokerRarity;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.*;
import esiee.info.e3.view.interfaces.IPage;
import esiee.info.e3.view.utils.UIStyle;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GamePage implements IPage {
    private final ViewMain context;
    private final GameController controller;
    private final double CARD_RATIO;
    private GameState currentState;
    private List<Card> currentHand;
    private List<Card> selectedCards;
    private EvaluatedHand currentEvaluation;

    private UIContainer rootContainer;
    private UIContainer handContainer;
    private UIContainer playedCardsContainer;
    private UIContainer jokersContainer;

    private JokerType incomingJoker = null;
    private Runnable onCancelIncoming = null;
    private java.util.function.Consumer<JokerType> onConfirmReplacement = null;
    private UIContainer swapModalContainer;

    private String centerMessage;
    private Color centerColor;
    private Runnable onCenterClose;

    private String fullScreenMessage;
    private Color fullScreenColor;
    private Runnable onFullScreenClose;
    private String rewardImagePath;
    private int rewardBlindId = -1;
    private JokerType overlayWonJoker = null;
    private boolean isRewardOverlay = false;

    private final UIAnimatedImage overlayBlindAnim;

    private boolean isPaused = false;
    private final UIContainer pauseContainer;

    public GamePage(ViewMain context, GameController controller) {
        this.context = Objects.requireNonNull(context);
        this.controller = Objects.requireNonNull(controller);
        this.currentHand = List.of();
        this.selectedCards = List.of();
        this.CARD_RATIO = 71.0 / 95.0;

        this.overlayBlindAnim = new UIAnimatedImage(() -> this.rewardBlindId, context);
        this.buildLayout();

        UIStyle pauseStyle = new UIStyle.Builder().bg(new Color(25, 25, 25, 240)).padding(20).radius(15).border(new Color(139, 0, 0), 4).build();
        this.pauseContainer = new UIContainer(3, 1, pauseStyle);
        UIStyle titleStyle = new UIStyle.Builder().text(Color.YELLOW).font(this.context.getGameFont().deriveFont(35f)).build();
        this.pauseContainer.addComponent(new UIText("JEU EN PAUSE", titleStyle), 0, 0, 0.4, 1.0);
        UIStyle resumeStyle = new UIStyle.Builder().bg(new Color(70, 130, 180)).hoverBg(new Color(100, 149, 237)).text(Color.WHITE).radius(8).margin(10).font(this.context.getGameFont().deriveFont(25f)).build();
        this.pauseContainer.addComponent(new UIButton("REPRENDRE", resumeStyle, () -> this.isPaused = false), 1, 0, 0.3, 1.0);
        UIStyle quitStyle = new UIStyle.Builder().bg(new Color(139, 0, 0)).hoverBg(new Color(205, 92, 92)).text(Color.WHITE).radius(8).margin(10).font(this.context.getGameFont().deriveFont(25f)).build();
        this.pauseContainer.addComponent(new UIButton("RETOUR AU MENU", quitStyle, () -> { this.isPaused = false; controller.saveAndQuit(); }), 2, 0, 0.3, 1.0);
    }

    public void triggerJokerSelectionForReplacement(JokerType newJoker, Runnable onCancel, java.util.function.Consumer<JokerType> onReplace) {
        this.incomingJoker = newJoker;
        this.onCancelIncoming = onCancel;
        this.onConfirmReplacement = onReplace;
        this.buildSwapModal();
    }

    private void buildSwapModal() {
        UIStyle modalStyle = new UIStyle.Builder().bg(new Color(20, 20, 20, 245)).radius(15).border(Color.ORANGE, 3f).padding(15).build();
        this.swapModalContainer = new UIContainer(100, 100, modalStyle);

        if (this.incomingJoker == null) return;

        UIStyle headerStyle = new UIStyle.Builder().text(Color.RED).font(context.getGameFont().deriveFont(28f)).build();
        this.swapModalContainer.addComponent(new UIText("INVENTAIRE DE JOKERS PLEIN (5/5) !", headerStyle), 5, 2, 0.08, 0.90);

        UIStyle subStyle = new UIStyle.Builder().text(Color.WHITE).font(context.getGameFont().deriveFont(16f)).build();
        this.swapModalContainer.addComponent(new UIText("Cliquez sur l'un de vos Jokers actuels pour le remplacer, ou rejetez le nouveau venu.", subStyle), 15, 2, 0.06, 0.90);

        if (this.currentState != null) {
            var actives = this.currentState.getActiveJokers();
            int itemX = 3;
            for (var oldJoker : actives) {
                var actJokerComp = new UIJoker(oldJoker, this.context, () -> {
                    var action = this.onConfirmReplacement;
                    this.incomingJoker = null;
                    if (action != null) action.accept(oldJoker);
                }, this.CARD_RATIO);
                this.swapModalContainer.addComponent(actJokerComp, 30, itemX, 0.38, 0.16);
                itemX += 19;
            }
        }

        UIStyle incTitleStyle = new UIStyle.Builder().text(Color.YELLOW).font(context.getGameFont().deriveFont(18f)).build();
        this.swapModalContainer.addComponent(new UIText("Nouveau Joker reçu :", incTitleStyle), 72, 10, 0.05, 0.30);

        var incJokerComp = new UIJoker(this.incomingJoker, this.context, null, this.CARD_RATIO);
        this.swapModalContainer.addComponent(incJokerComp, 78, 10, 0.35, 0.16);

        UIStyle discardBtnStyle = new UIStyle.Builder().bg(new Color(150, 20, 20)).hoverBg(new Color(210, 40, 40)).text(Color.WHITE).radius(10).font(context.getGameFont().deriveFont(18f)).build();
        this.swapModalContainer.addComponent(new UIButton("REJETER / PASSER", discardBtnStyle, () -> {
            var action = this.onCancelIncoming;
            this.incomingJoker = null;
            if (action != null) action.run();
        }), 82, 35, 0.12, 0.35);
    }

    private void buildLayout() {
        var rootStyle = new UIStyle.Builder().bg(new Color(30, 70, 40)).build();
        this.rootContainer = new UIContainer(100, 100, rootStyle);
        this.rootContainer.addComponent(this.buildSidebar(), 0, 0, 1.0, 0.25);
        this.rootContainer.addComponent(this.buildGameplayArea(), 0, 25, 1.0, 0.75);
    }

    private UIContainer buildGameplayArea() {
        var transStyle = new UIStyle.Builder().build();
        var gameplayArea = new UIContainer(100, 100, transStyle);

        var pauseBtnStyle = new UIStyle.Builder().bg(new Color(40, 40, 40, 200)).hoverBg(new Color(80, 80, 80, 255)).text(Color.WHITE).radius(10).border(Color.GRAY, 2f).font(this.context.getGameFont().deriveFont(22f)).build();
        gameplayArea.addComponent(new UIButton("PAUSE", pauseBtnStyle, () -> this.isPaused = true), 2, 83, 0.08, 0.15);

        this.setupJokersZone(gameplayArea);
        this.setupScorePreview(gameplayArea);
        this.setupPlayedCardsZone(gameplayArea);

        var bottomBox = new UIContainer(100, 100, transStyle);
        this.setupHandZone(bottomBox);
        this.setupDeckZone(bottomBox);

        gameplayArea.addComponent(bottomBox, 66, 0, 0.34, 1.0);
        return gameplayArea;
    }

    private void setupJokersZone(UIContainer area) {
        var zoneStyle = new UIStyle.Builder().bg(new Color(0, 0, 0, 100)).radius(15).build();
        var counterStyle = new UIStyle.Builder().text(Color.WHITE).font(this.context.getGameFont().deriveFont(22f)).build();

        this.jokersContainer = new UIContainer(100, 100, zoneStyle);
        area.addComponent(this.jokersContainer, 1, 4, 0.25, 0.70);
        area.addComponent(new UIText(() -> this.getActiveJokersCount() + "/5", counterStyle), 23, 0, 0.10, 0.15);
    }

    private void setupScorePreview(UIContainer area) {
        var previewStyle = new UIStyle.Builder().text(Color.WHITE).font(this.context.getGameFont().deriveFont(20f)).build();
        area.addComponent(new UIText(() -> {
            if (this.centerMessage != null || this.fullScreenMessage != null) return "";
            if (this.selectedCards.isEmpty() || this.currentEvaluation == null) return "";
            var constraint = this.currentState != null ? this.currentState.getCurrentConstraint() : null;
            if (constraint != null && constraint.isHidden()) return "Prévision : ??? pts";
            return "Prévision : " + this.controller.getExpectedScore() + " pts";
        }, previewStyle), 28, 2, 0.05, 0.30);
    }

    private UIContainer buildSidebar() {
        var sidebarStyle = new UIStyle.Builder().bg(new Color(40, 35, 30)).border(new Color(100, 80, 50), 3f).radius(20).padding(10).build();
        var sidebar = new UIContainer(100, 100, sidebarStyle);
        this.addBlindHeader(sidebar);
        this.addConstraintSection(sidebar);
        this.addGoalSection(sidebar);
        this.addComboSection(sidebar);
        this.addCurrentScoreSection(sidebar);
        this.addStatsSection(sidebar);
        this.addActionButtons(sidebar);
        return sidebar;
    }

    private void addBlindHeader(UIContainer sidebar) {
        var style = new UIStyle.Builder().bg(new Color(220, 50, 50)).radius(10).text(Color.WHITE).font(this.context.getGameFont().deriveFont(32f)).build();
        sidebar.addComponent(new UIText(this::getBlindName, style), 2, 5, 0.08, 0.90);
    }

    private void addConstraintSection(UIContainer sidebar) {
        sidebar.addComponent(new esiee.info.e3.view.interfaces.UIComponent() {
            @Override
            public void render(Graphics2D g, int x, int y, int width, int height) {
                if (currentState == null) return;
                var constraint = currentState.getCurrentConstraint();
                if (constraint == null || constraint.name().equals("NONE")) return;

                g.setColor(new Color(60, 20, 40)); g.fillRoundRect(x, y, width, height, 8, 8);
                g.setColor(Color.MAGENTA); g.setStroke(new BasicStroke(2f)); g.drawRoundRect(x, y, width, height, 8, 8);
                g.setColor(Color.WHITE); g.setFont(context.getGameFont().deriveFont(22f));
                String title = constraint.getLabel(); g.drawString(title, x + (width - g.getFontMetrics().stringWidth(title)) / 2, y + height / 2 - 5);
                g.setColor(Color.ORANGE); g.setFont(context.getGameFont().deriveFont(16f));
                String desc = constraint.getDescription(); g.drawString(desc, x + (width - g.getFontMetrics().stringWidth(desc)) / 2, y + height / 2 + 18);
            }
            @Override public boolean handlePointerClick(int mx, int my, int x, int y, int width, int height) { return false; }
            @Override public void handlePointerMove(int mx, int my, int x, int y, int width, int height) {}
        }, 11, 5, 0.07, 0.90);
    }

    private void addGoalSection(UIContainer sidebar) {
        var box = this.createStandardBox();
        box.addComponent(new UIAnimatedImage(this::getBlindId, this.context), 5, 5, 0.55, 0.90);
        box.addComponent(new UIText(TextConstant.TEXT_CONSTANT_SCORE_TO_ACHIEVE.getText(), this.createLabelStyle(22f)), 62, 0, 0.15, 1.0);
        box.addComponent(new UIText(() -> String.valueOf(this.getBlindScore()), this.createValueStyle(38f), this::getScoreColor), 78, 0, 0.20, 1.0);
        sidebar.addComponent(box, 19, 5, 0.18, 0.90);
    }

    private void addComboSection(UIContainer sidebar) {
        var box = this.createStandardBox();
        box.addComponent(new UIImage(this::getCurrentPlanetPath, this.context, this.CARD_RATIO), 5, 5, 0.45, 0.90);
        box.addComponent(new UIText(this::getComboLevelText, this.createLabelStyle(22f)), 50, 0, 0.12, 1.0);
        box.addComponent(new UIText(this::getComboLabel, this.createLabelStyle(22f)), 62, 0, 0.15, 1.0);
        box.addComponent(new UIText(this::getComboStats, this.createValueStyle(28f)), 78, 0, 0.20, 1.0);
        sidebar.addComponent(box, 38, 5, 0.18, 0.90);
    }

    private void addCurrentScoreSection(UIContainer sidebar) {
        var box = new UIContainer(100, 100, new UIStyle.Builder().bg(Color.BLACK).border(Color.ORANGE, 2f).radius(10).build());
        box.addComponent(new UIText(TextConstant.TEXT_CONSTANT_ACTUAL_SCORE.getText(), this.createLabelStyle(22f)), 10, 0, 0.40, 1.0);
        box.addComponent(new UIText(() -> String.valueOf(this.getCurrentScoreValue()), this.createValueStyle(28f)), 50, 0, 0.40, 1.0);
        sidebar.addComponent(box, 57, 5, 0.08, 0.90);
    }

    private void addStatsSection(UIContainer sidebar) {
        var mBox = new UIContainer(100, 100, new UIStyle.Builder().bg(new Color(40, 80, 200)).radius(10).build());
        mBox.addComponent(new UIText(TextConstant.TEXT_CONSTANT_HAND.getText(), this.createLabelStyle(20f)), 15, 0, 0.35, 1.0);
        mBox.addComponent(new UIText(() -> String.valueOf(this.getHandsLeftValue()), this.createValueStyle(26f)), 45, 0, 0.45, 1.0);

        var dBox = new UIContainer(100, 100, new UIStyle.Builder().bg(new Color(200, 50, 50)).radius(10).build());
        dBox.addComponent(new UIText(TextConstant.TEXT_CONSTANT_DEFAUSSE.getText(), this.createLabelStyle(20f)), 15, 0, 0.35, 1.0);
        dBox.addComponent(new UIText(() -> String.valueOf(this.getDiscardsLeftValue()), this.createValueStyle(26f)), 45, 0, 0.45, 1.0);

        sidebar.addComponent(mBox, 66, 5, 0.07, 0.42);
        sidebar.addComponent(dBox, 66, 53, 0.07, 0.42);
    }

    private void addActionButtons(UIContainer sidebar) {
        sidebar.addComponent(new UIButton(TextConstant.TEXT_CONSTANT_DEFAUSSED.getText(), new UIStyle.Builder().bg(new Color(180, 40, 40)).radius(10).text(Color.WHITE).font(this.context.getGameFont().deriveFont(30f)).build(), this.controller::handleDiscard), 74, 5, 0.08, 0.90);
        sidebar.addComponent(new UIButton(TextConstant.TEXT_CONSTANT_PLAY_HAND.getText(), new UIStyle.Builder().bg(new Color(255, 160, 0)).radius(15).text(Color.WHITE).font(this.context.getGameFont().deriveFont(32f)).build(), () -> this.controller.handlePlay(Optional.empty())), 84, 5, 0.14, 0.90);
    }

    private void setupPlayedCardsZone(UIContainer area) {
        this.playedCardsContainer = new UIContainer(100, 100, new UIStyle.Builder().bg(new Color(0, 0, 0, 100)).radius(15).build());
        area.addComponent(this.playedCardsContainer, 33, 10, 0.30, 0.80);
    }

    private void setupHandZone(UIContainer bottom) {
        this.handContainer = new UIContainer(100, 100, new UIStyle.Builder().bg(new Color(0, 0, 0, 100)).radius(15).build());
        bottom.addComponent(this.handContainer, 10, 5, 0.75, 0.70);
        bottom.addComponent(new UIText(() -> this.getHandSize() + TextConstant.TEXT_CONSTANT_BY.formatBy("8"), new UIStyle.Builder().text(Color.WHITE).font(this.context.getGameFont().deriveFont(22f)).build()), 86, 5, 0.14, 0.15);
    }

    private void setupDeckZone(UIContainer bottom) {
        bottom.addComponent(new UICard(null, false, () -> false, null, this.context, this.CARD_RATIO), 10, 80, 0.65, 0.15);
        bottom.addComponent(new UIText(() -> this.getDeckRemainingCards() + TextConstant.TEXT_CONSTANT_BY.formatBy("52"), new UIStyle.Builder().text(Color.WHITE).font(this.context.getGameFont().deriveFont(22f)).build()), 78, 80, 0.15, 0.15);
    }

    @Override
    public void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {
        this.currentState = Objects.requireNonNull(state);
        this.currentHand = Objects.requireNonNull(hand);
        this.selectedCards = Objects.requireNonNull(selectedCards);
        this.currentEvaluation = eval;
        this.refreshContainers();
    }

    private void refreshContainers() {
        Objects.requireNonNull(this.handContainer).clearChildren();
        int handX = 2;
        boolean isHidden = (this.currentState != null && this.currentState.getCurrentConstraint() != null && this.currentState.getCurrentConstraint().isHidden());

        for (var card : this.currentHand) {
            if (!this.selectedCards.contains(card)) {
                this.handContainer.addComponent(new UICard(card, !isHidden, () -> false, () -> this.controller.toggleCardSelection(card), this.context, this.CARD_RATIO), 10, handX, 0.80, 0.11);
                handX += 12;
            }
        }

        Objects.requireNonNull(this.jokersContainer).clearChildren();
        if (this.currentState != null) {
            var jokers = this.currentState.getActiveJokers();
            int jokerX = 3;
            for (var joker : jokers) {
                var uiJoker = new UIJoker(joker, this.context, () -> {

                    String rarity = this.controller.getJokerRarity(joker);
                    Color rarityColor = Color.MAGENTA;

                    if (JokerRarity.CLASSIQUE.name().equals(rarity)) rarityColor = Color.LIGHT_GRAY;
                    else if (JokerRarity.MOYEN.name().equals(rarity)) rarityColor = new Color(100, 200, 255);
                    else if (JokerRarity.LEGENDAIRE.name().equals(rarity)) rarityColor = new Color(255, 215, 0);

                    String overlayTitle = joker.getJokerName() + " [" + rarity + "]";
                    this.showOverlay(overlayTitle + "\n\n" + joker.getDescription(), rarityColor, null);

                }, this.CARD_RATIO);

                this.jokersContainer.addComponent(uiJoker, 5, jokerX, 0.90, 0.16);
                jokerX += 19;
            }
        }
        this.updateCenterContainer();
    }

    private void updateCenterContainer() {
        Objects.requireNonNull(this.playedCardsContainer).clearChildren();
        if (this.centerMessage != null) { this.renderCenterMessage(); return; }

        int playedX = 15;
        boolean isHidden = (this.currentState != null && this.currentState.getCurrentConstraint() != null && this.currentState.getCurrentConstraint().isHidden());

        for (var card : this.currentHand) {
            if (this.selectedCards.contains(card)) {
                this.playedCardsContainer.addComponent(new UICard(card, !isHidden, () -> true, () -> this.controller.toggleCardSelection(card), this.context, this.CARD_RATIO), 10, playedX, 0.80, 0.12);
                playedX += 14;
            }
        }
    }

    private void renderCenterMessage() {
        this.playedCardsContainer.addComponent(new UIText(this.centerMessage, new UIStyle.Builder().text(this.centerColor).font(this.context.getGameFont().deriveFont(28f)).shadow(Color.BLACK, 3).build()), 10, 10, 0.80, 0.80);
    }

    private UIContainer createStandardBox() { return new UIContainer(100, 100, new UIStyle.Builder().bg(new Color(30, 30, 30)).shadow(new Color(0, 0, 0, 150), 5).radius(12).build()); }
    private UIStyle createLabelStyle(float size) { return new UIStyle.Builder().text(Color.WHITE).font(this.context.getGameFont().deriveFont(size)).build(); }
    private UIStyle createValueStyle(float size) { return new UIStyle.Builder().text(Color.WHITE).font(this.context.getGameFont().deriveFont(size)).shadow(new Color(0, 0, 0, 150), 3).build(); }

    private int getHandSize() { return this.currentHand.size() - this.selectedCards.size(); }
    private int getActiveJokersCount() { return this.currentState != null ? this.currentState.getActiveJokers().size() : 0; }
    private String getComboLabel() { return this.currentEvaluation != null ? this.currentEvaluation.combo().getLabel() : TextConstant.TEXT_CONSTANT_SELECT.getText(); }
    private String getComboStats() { return this.currentEvaluation != null ? this.currentEvaluation.chips() + " x " + this.currentEvaluation.multiplier() : TextConstant.TEXT_CONSTANT_DEFAULT_0X0.getText(); }
    private String getComboLevelText() { return (this.currentEvaluation == null || this.currentState == null) ? "Lvl 1" : "Lvl " + this.currentState.getLevel(this.currentEvaluation.combo()); }
    private long getCurrentScoreValue() { return this.currentState.getCurrentScore(); }
    private int getHandsLeftValue() { return this.currentState.getHandsLeft(); }
    private int getDiscardsLeftValue() { return this.currentState.getDiscardsLeft(); }
    private int getBlindId() { return this.currentState.getCurrentBlind().id(); }
    private String getBlindName() { return this.currentState.getCurrentBlind().name(); }
    private long getBlindScore() { return this.currentState.getCurrentBlind().score(); }
    private int getDeckRemainingCards() { return this.currentState.getDeckSize(); }
    private String getCurrentPlanetPath() {
        if (this.currentEvaluation == null || this.currentState == null) return null;
        var planet = Planet.fromCombination(this.currentEvaluation.combo());
        return planet != null && this.currentState.hasPlanet(planet) ? "/planets/" + planet.getFileName() : null;
    }

    private Color getScoreColor() {
        long score = this.getBlindScore();
        if (score >= 10000) return new Color(255, 50, 50);
        if (score >= 5000) return new Color(255, 150, 0);
        return new Color(255, 215, 0);
    }

    @Override
    public void showOverlay(String m, Color c, Runnable o) {
        this.centerMessage = null; this.fullScreenMessage = null; this.rewardImagePath = null; this.rewardBlindId = -1;
        this.overlayWonJoker = null;
        this.isRewardOverlay = false;

        if (m.startsWith("[SCORE]")) {
            this.centerMessage = m.substring(7); this.centerColor = c; this.onCenterClose = o;
        } else if (m.startsWith("[REWARD:")) {
            this.isRewardOverlay = true;
            int endIndex = m.indexOf("]");
            if (endIndex != -1) {
                String payload = m.substring(8, endIndex);
                if (payload.contains("|")) {
                    String[] parts = payload.split("\\|");
                    this.rewardImagePath = parts[0];
                    this.rewardBlindId = Integer.parseInt(parts[1]);
                    if (parts.length > 2 && !parts[2].equals("NONE")) {
                        try {
                            this.overlayWonJoker = JokerType.valueOf(parts[2]);
                        } catch (Exception e) {
                            this.overlayWonJoker = null;
                        }
                    }
                } else {
                    this.rewardImagePath = payload;
                }
                this.fullScreenMessage = m.substring(endIndex + 1);
            } else {
                this.fullScreenMessage = m;
            }
            this.fullScreenColor = c; this.onFullScreenClose = o;
        } else {
            this.fullScreenMessage = m; this.fullScreenColor = c; this.onFullScreenClose = o;
        }
        this.updateCenterContainer();
    }

    @Override
    public void render(Graphics2D g, float sw, float sh) {
        this.rootContainer.render(g, 0, 0, (int) sw, (int) sh);

        if (this.isPaused) {
            g.setColor(new Color(0, 0, 0, 150)); g.fillRect(0, 0, (int) sw, (int) sh);
            this.pauseContainer.render(g, (int) (sw - 450) / 2, (int) (sh - 300) / 2, 450, 300);
        }

        if (this.incomingJoker != null && this.swapModalContainer != null) {
            g.setColor(new Color(0, 0, 0, 230)); g.fillRect(0, 0, (int) sw, (int) sh);
            int mw = (int)(sw * 0.85); int mh = (int)(sh * 0.75);
            int mx = (int)(sw - mw) / 2; int my = (int)(sh - mh) / 2;
            this.swapModalContainer.render(g, mx, my, mw, mh);
            return;
        }

        if (this.fullScreenMessage != null) {
            g.setColor(new Color(12, 18, 14, 235));
            g.fillRect(0, 0, (int) sw, (int) sh);

            if (this.isRewardOverlay) {
                int pW = 900, pH = 440;
                int pX = ((int) sw - pW) / 2;
                int pY = ((int) sh - pH) / 2;

                g.setColor(new Color(28, 44, 36));
                g.fillRoundRect(pX, pY, pW, pH, 15, 15);
                g.setColor(Color.ORANGE);
                g.setStroke(new BasicStroke(3f));
                g.drawRoundRect(pX, pY, pW, pH, 15, 15);

                int cardY = pY + 80;
                int planetX = pX + 50;

                g.setColor(new Color(30, 50, 100));
                g.fillRoundRect(planetX, cardY, 110, 160, 10, 10);
                g.setColor(Color.WHITE);
                g.drawRoundRect(planetX, cardY, 110, 160, 10, 10);
                g.setFont(context.getGameFont().deriveFont(13f));
                g.drawString("PLANÈTE", planetX + 22, cardY + 90);

                if (this.rewardImagePath != null) {
                    var img = context.getImage(this.rewardImagePath);
                    if (img != null) {
                        g.setClip(new java.awt.geom.RoundRectangle2D.Float(planetX, cardY, 110, 160, 10, 10));
                        g.drawImage(img, planetX, cardY, 110, 160, null);
                        g.setClip(null);
                    }
                }

                int textStartX = planetX + 150;
                if (this.overlayWonJoker != null) {
                    int jokerX = planetX + 140;
                    textStartX = jokerX + 150;

                    g.setColor(new Color(130, 35, 35));
                    g.fillRoundRect(jokerX, cardY, 110, 160, 10, 10);
                    g.setColor(Color.YELLOW);
                    g.drawRoundRect(jokerX, cardY, 110, 160, 10, 10);

                    var jokerImg = context.getImage(this.overlayWonJoker.getImagePath());
                    if (jokerImg != null) {
                        g.setClip(new java.awt.geom.RoundRectangle2D.Float(jokerX, cardY, 110, 160, 10, 10));
                        g.drawImage(jokerImg, jokerX, cardY, 110, 160, null);
                        g.setClip(null);
                    } else {
                        g.setFont(context.getGameFont().deriveFont(12f));
                        g.drawString("JOKER", jokerX + 25, cardY + 30);
                        g.setColor(Color.WHITE);
                        g.setFont(context.getGameFont().deriveFont(10f));
                        g.drawString(this.overlayWonJoker.getJokerName(), jokerX + 10, cardY + 85);
                    }
                }

                g.setColor(Color.WHITE);
                g.setFont(context.getGameFont().deriveFont(18f));
                int currentTextY = cardY + 15;
                for (String line : this.fullScreenMessage.split("\n")) {
                    g.drawString(line, textStartX, currentTextY);
                    currentTextY += 24;
                }

                if (this.rewardBlindId != -1) {
                    this.overlayBlindAnim.render(g, textStartX, currentTextY + 10, 80, 80);
                }

                g.setFont(context.getGameFont().deriveFont(14f));
                g.setColor(Color.GRAY);
                g.drawString("(Cliquez n'importe où pour continuer)", pX + (pW / 2) - 130, pY + pH - 25);

            } else {
                g.setFont(context.getGameFont().deriveFont(38f));
                var metrics = g.getFontMetrics();

                String[] lines = this.fullScreenMessage.split("\n");
                int totalTextHeight = lines.length * (metrics.getHeight() + 10);
                int y = (int) ((sh - totalTextHeight) / 2) + metrics.getAscent();

                g.setColor(Objects.requireNonNullElse(this.fullScreenColor, Color.WHITE));
                for (String line : lines) {
                    g.drawString(line, (int) ((sw - metrics.stringWidth(line)) / 2), y);
                    y += metrics.getHeight() + 10;
                }

                g.setFont(context.getGameFont().deriveFont(18f));
                String clickMsg = "(Cliquez n'importe où pour fermer)";
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(clickMsg, (int) ((sw - g.getFontMetrics().stringWidth(clickMsg)) / 2), y + 30);
            }
        }
    }

    @Override
    public void handlePointerClick(int mx, int my, float sw, float sh) {
        if (this.incomingJoker != null && this.swapModalContainer != null) {
            int mw = (int)(sw * 0.85); int mh = (int)(sh * 0.75);
            int rx = (int)(sw - mw) / 2; int ry = (int)(sh - mh) / 2;
            this.swapModalContainer.handlePointerClick(mx, my, rx, ry, mw, mh);
            return;
        }

        if (this.fullScreenMessage != null) {
            this.fullScreenMessage = null; this.rewardImagePath = null; this.rewardBlindId = -1;
            this.overlayWonJoker = null;
            if (this.onFullScreenClose != null) { Runnable action = this.onFullScreenClose; this.onFullScreenClose = null; action.run(); }
            else { this.updateCenterContainer(); }
            return;
        }

        if (this.centerMessage != null) {
            this.centerMessage = null;
            if (this.onCenterClose != null) { Runnable action = this.onCenterClose; this.onCenterClose = null; action.run(); }
            else { this.updateCenterContainer(); }
            return;
        }

        if (this.isPaused) {
            this.pauseContainer.handlePointerClick(mx, my, (int) (sw - 450) / 2, (int) (sh - 300) / 2, 450, 300);
            return;
        }

        this.rootContainer.handlePointerClick(mx, my, 0, 0, (int) sw, (int) sh);
    }

    @Override
    public void handlePointerMove(int mx, int my, float sw, float sh) {
        if (this.incomingJoker != null && this.swapModalContainer != null) {
            int mw = (int)(sw * 0.85); int mh = (int)(sh * 0.75);
            int rx = (int)(sw - mw) / 2; int ry = (int)(sh - mh) / 2;
            this.swapModalContainer.handlePointerMove(mx, my, rx, ry, mw, mh);
            return;
        }

        if (this.fullScreenMessage != null || this.centerMessage != null) return;

        if (this.isPaused) {
            this.pauseContainer.handlePointerMove(mx, my, (int) (sw - 450) / 2, (int) (sh - 300) / 2, 450, 300);
            return;
        }
        this.rootContainer.handlePointerMove(mx, my, 0, 0, (int) sw, (int) sh);
    }
}