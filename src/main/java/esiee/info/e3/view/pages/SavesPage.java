package esiee.info.e3.view.pages;

import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.manager.SaveManager;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.components.UIAnimatedImage;
import esiee.info.e3.view.components.UIButton;
import esiee.info.e3.view.components.UIContainer;
import esiee.info.e3.view.components.UIText;
import esiee.info.e3.view.interfaces.IPage;
import esiee.info.e3.view.utils.UIStyle;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavesPage implements IPage {
    private final ViewMain context;
    private final GameController controller;
    private final UIContainer rootContainer;

    private final List<SaveSlot> allSaves = new ArrayList<>();
    private int currentPageIndex = 0;
    private final int itemsPerPage = 4;

    private long lastRefreshTime = 0;

    private String overlayMessage;
    private Color overlayColor;
    private Runnable onOverlayClose;

    public record SaveSlot(
            String id, String date, String lastBlindName, int blindId,
            long score, String status, boolean canResume, boolean infiniteMode, int loopCount
    ) {}

    public SavesPage(ViewMain context, GameController controller) {
        this.context = Objects.requireNonNull(context);
        this.controller = Objects.requireNonNull(controller);

        loadSavesFromJson();

        UIStyle rootStyle = new UIStyle.Builder().bg(new Color(15, 25, 20)).margin(15).build();
        this.rootContainer = new UIContainer(1, 1, rootStyle);

        buildSavesUI();
    }

    private void sortSavesDesc(List<SaveSlot> saves) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        saves.sort((s1, s2) -> {
            try {
                LocalDateTime d1 = LocalDateTime.parse(s1.date(), formatter);
                LocalDateTime d2 = LocalDateTime.parse(s2.date(), formatter);
                return d2.compareTo(d1);
            } catch (Exception e) {
                return s2.id().compareTo(s1.id());
            }
        });
    }

    private void loadSavesFromJson() {
        allSaves.clear();
        List<SaveSlot> loadedSaves = SaveManager.loadAllSaves();
        sortSavesDesc(loadedSaves);
        this.allSaves.addAll(loadedSaves);
    }

    private void checkAndRefreshSaves() {
        if (System.currentTimeMillis() - lastRefreshTime < 500) return;
        lastRefreshTime = System.currentTimeMillis();

        List<SaveSlot> newSaves = SaveManager.loadAllSaves();
        sortSavesDesc(newSaves);

        if (!newSaves.equals(this.allSaves)) {
            this.allSaves.clear();
            this.allSaves.addAll(newSaves);

            int maxPage = Math.max(0, (this.allSaves.size() - 1) / itemsPerPage);
            if (currentPageIndex > maxPage) {
                currentPageIndex = maxPage;
            }

            buildSavesUI();
        }
    }

    private void buildSavesUI() {
        this.rootContainer.clearChildren();

        UIStyle panelStyle = new UIStyle.Builder().bg(new Color(24, 34, 29)).padding(20).margin(20).radius(15).build();
        UIContainer savesPanel = new UIContainer(100, 100, panelStyle);

        UIStyle titleStyle = new UIStyle.Builder().text(Color.YELLOW).font(context.getGameFont().deriveFont(40f)).build();
        savesPanel.addComponent(new UIText("SAUVEGARDES", titleStyle), 2, 0, 0.15, 1.0);

        UIContainer listContainer = new UIContainer(itemsPerPage, 1, new UIStyle.Builder().build());

        Font font20 = context.getGameFont().deriveFont(20f);
        Font font18 = context.getGameFont().deriveFont(18f);

        if (allSaves.isEmpty()) {
            UIStyle emptyStyle = new UIStyle.Builder().text(Color.LIGHT_GRAY).font(context.getGameFont().deriveFont(26f)).build();
            listContainer.addComponent(new UIText("Aucune sauvegarde disponible...", emptyStyle), 0, 0, 1.0, 1.0);
        } else {
            int start = currentPageIndex * itemsPerPage;
            int end = Math.min(start + itemsPerPage, allSaves.size());
            int row = 0;

            for (int i = start; i < end; i++) {
                SaveSlot save = allSaves.get(i);

                UIStyle slotStyle = new UIStyle.Builder()
                        .bg(new Color(38, 28, 22)).hoverBg(new Color(54, 40, 32))
                        .radius(8).margin(5).padding(5).build();
                UIContainer slotRow = new UIContainer(100, 100, slotStyle);

                UIContainer animBox = new UIContainer(1, 1, new UIStyle.Builder().build());
                animBox.addComponent(new UIAnimatedImage(() -> save.blindId(), context), 0, 0, 1.0, 1.0);
                slotRow.addComponent(animBox, 10, 2, 0.80, 0.08);

                UIStyle dateStyle = new UIStyle.Builder().text(Color.LIGHT_GRAY).font(font18).build();
                slotRow.addComponent(new UIText(save.date(), dateStyle), 0, 12, 1.0, 0.15);

                String modeText = save.infiniteMode() ? "Infini (B." + save.loopCount() + ")" : "Classique";
                Color modeColor = save.infiniteMode() ? new Color(186, 85, 211) : Color.GRAY;
                UIStyle modeStyle = new UIStyle.Builder().text(modeColor).font(font18).build();
                slotRow.addComponent(new UIText(modeText, modeStyle), 0, 28, 1.0, 0.12);

                UIStyle nameStyle = new UIStyle.Builder().text(Color.WHITE).font(font20).build();
                slotRow.addComponent(new UIText(save.lastBlindName(), nameStyle), 0, 41, 1.0, 0.18);

                UIStyle scoreStyle = new UIStyle.Builder().text(new Color(255, 215, 0)).font(font20).build();
                slotRow.addComponent(new UIText(save.score() + " pts", scoreStyle), 0, 60, 1.0, 0.12);

                Color statusColor = save.status().equals("VICTOIRE") ? new Color(50, 220, 50) :
                        save.status().equals("DÉFAITE") ? new Color(220, 50, 50) : new Color(50, 150, 255);
                UIStyle statusStyle = new UIStyle.Builder().text(statusColor).font(font18).build();
                slotRow.addComponent(new UIText(save.status(), statusStyle), 0, 73, 1.0, 0.12);

                if (save.canResume()) {
                    UIStyle btnStyle = new UIStyle.Builder().bg(new Color(70, 130, 180)).hoverBg(new Color(100, 149, 237)).text(Color.WHITE).radius(8).margin(8).font(font18).build();
                    UIButton resumeBtn = new UIButton("REPRENDRE", btnStyle, () -> {
                        context.navigateTo("game", true);
                        controller.loadGameFromJson(save.id());
                    });
                    slotRow.addComponent(resumeBtn, 0, 86, 1.0, 0.12);
                } else {
                    slotRow.addComponent(new UIText("-", new UIStyle.Builder().text(Color.DARK_GRAY).font(font20).build()), 0, 86, 1.0, 0.12);
                }

                listContainer.addComponent(slotRow, row++, 0, 1.0 / itemsPerPage, 1.0);
            }
        }
        savesPanel.addComponent(listContainer, 17, 5, 0.65, 0.90);

        UIContainer navRow = new UIContainer(100, 100, new UIStyle.Builder().build());
        Font navFont = context.getGameFont().deriveFont(22f);

        UIStyle btnStyle = new UIStyle.Builder().bg(new Color(100, 100, 100)).hoverBg(new Color(150, 150, 150)).text(Color.WHITE).radius(8).font(navFont).build();
        UIStyle backStyle = new UIStyle.Builder().bg(new Color(139, 0, 0)).hoverBg(new Color(205, 92, 92)).text(Color.WHITE).radius(8).font(navFont).build();

        if (currentPageIndex > 0) {
            navRow.addComponent(new UIButton("< PRECEDENT", btnStyle, () -> { currentPageIndex--; buildSavesUI(); }), 10, 10, 0.80, 0.20);
        }

        navRow.addComponent(new UIButton("RETOUR", backStyle, () -> context.navigateTo("home")), 10, 40, 0.80, 0.20);

        if ((currentPageIndex + 1) * itemsPerPage < allSaves.size()) {
            navRow.addComponent(new UIButton("SUIVANT >", btnStyle, () -> { currentPageIndex++; buildSavesUI(); }), 10, 70, 0.80, 0.20);
        }

        savesPanel.addComponent(navRow, 85, 0, 0.12, 1.0);

        this.rootContainer.addComponent(savesPanel, 0, 0, 1.0, 1.0);
    }

    @Override
    public void render(Graphics2D g, float sw, float sh) {
        checkAndRefreshSaves();

        this.rootContainer.render(g, 0, 0, (int) sw, (int) sh);

        int margin = 20;
        int borderX = margin;
        int borderY = margin;
        int borderW = (int) sw - (margin * 2);
        int borderH = (int) sh - (margin * 2);

        int thickBlack = 6;
        int thickBrown = 4;

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(thickBlack));
        g.drawRect(borderX, borderY, borderW, borderH);

        g.setColor(new Color(90, 60, 40));
        g.setStroke(new BasicStroke(thickBrown));
        g.drawRect(borderX + thickBlack/2 + 2, borderY + thickBlack/2 + 2,
                borderW - thickBlack - 4, borderH - thickBlack - 4);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(borderX + thickBlack + thickBrown, borderY + thickBlack + thickBrown,
                borderW - (thickBlack + thickBrown)*2, borderH - (thickBlack + thickBrown)*2);

        if (this.overlayMessage != null) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, (int) sw, (int) sh);
            g.setFont(context.getGameFont().deriveFont(32f));
            var metrics = g.getFontMetrics();
            int msgWidth = metrics.stringWidth(this.overlayMessage);
            int msgAscent = metrics.getAscent();

            g.setColor(Objects.requireNonNullElse(this.overlayColor, Color.WHITE));
            g.drawString(this.overlayMessage, (int) ((sw - msgWidth) / 2), (int) ((sh + msgAscent) / 2));

            g.setFont(context.getGameFont().deriveFont(16f));
            String clickMsg = "(Cliquez pour fermer)";
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(clickMsg, (int) ((sw - metrics.stringWidth(clickMsg)) / 2), (int) ((sh + msgAscent) / 2) + 40);
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
    public void handlePointerMove(int mx, int my, float sw, float sh) {
        if (this.overlayMessage != null) return;
        this.rootContainer.handlePointerMove(mx, my, 0, 0, (int) sw, (int) sh);
    }

    @Override
    public void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {}

    @Override
    public void showOverlay(String message, Color color, Runnable onClose) {
        this.overlayMessage = message;
        this.overlayColor = color;
        this.onOverlayClose = onClose;
    }
}