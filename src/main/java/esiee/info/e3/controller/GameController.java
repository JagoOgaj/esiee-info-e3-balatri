package esiee.info.e3.controller;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.manager.SaveManager;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.interfaces.IView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameController {
    private final GameModel model;
    private final IView view;
    private final List<Card> selectedCards;

    public GameController(GameModel model, IView view) {
        this.model = Objects.requireNonNull(model);
        this.view = Objects.requireNonNull(view);
        this.selectedCards = new ArrayList<>();
    }

    public void startGame(boolean infiniteMode) {
        this.selectedCards.clear();
        this.model.resetGame();
        this.model.getState().setInfiniteMode(infiniteMode);
        this.refreshView();
    }

    public void init() {
        this.refreshView();
        this.view.start();
    }

    public void resetGame() {
        this.selectedCards.clear();
        this.model.resetGame();
        this.refreshView();
    }

    public void loadGameFromJson(String saveId) {
        this.selectedCards.clear();
        SaveManager.loadGame(saveId, this.model);
        this.refreshView();
    }

    public void toggleCardSelection(Card card) {
        var selectedCard = Objects.requireNonNull(card);
        if (this.selectedCards.contains(selectedCard)) {
            this.selectedCards.remove(selectedCard);
        } else {
            if (this.selectedCards.size() < 5) {
                this.selectedCards.add(selectedCard);
            } else {
                this.view.showError(TextConstant.TEXT_CONSTANT_ERROR_MAX_CARDS.getText());
            }
        }
        this.refreshView();
    }

    public void handleRemoveJoker(JokerType joker) {
        boolean isRemoved = this.model.removeJoker(joker);
        if (!isRemoved) {
            this.view.showError("Erreur : Impossible de retirer ce Joker.");
        }
        this.refreshView();
    }

    public void handlePlay(Optional<Character> actionFromConsole) {
        if (this.selectedCards.isEmpty()) {
            view.showError(TextConstant.TEXT_CONSTANT_ERROR_PLAY_EMPTY.getText());
            return;
        }
        if (this.model.getState().getHandsLeft() <= 0) return;

        try {
            var scoreGained = this.model.playHand(new ArrayList<>(this.selectedCards));
            this.selectedCards.clear();

            var state = this.model.getState();

            if (state.getCurrentScore() >= state.getCurrentBlind().score()) {
                var wonPlanet = this.model.grantRandomPlanetReward();
                var wonJoker = this.model.rollJokerReward(state.getCurrentBlindIndex());

                String jokerText = "";
                String jokerTagPart = "NONE";

                if (wonJoker != null) {
                    jokerTagPart = wonJoker.name();
                    if (state.isJokersFull()) {
                        jokerText = "\n\nINVENTAIRE JOKERS PLEIN !";
                        this.view.triggerJokerReplacement(
                                wonJoker,
                                this::refreshView,
                                (oldJoker) -> {
                                    if (this.model.removeJoker(oldJoker)) {
                                        if (!this.model.addJoker(wonJoker)) {
                                            this.view.showError("Erreur à l'ajout du nouveau Joker.");
                                        }
                                    } else {
                                        this.view.showError("Impossible de supprimer l'ancien Joker.");
                                    }
                                    this.refreshView();
                                }
                        );
                    } else {
                        boolean added = this.model.addJoker(wonJoker);
                        if (!added) {
                            System.err.println("Avertissement : Le Joker n'a pas pu être ajouté (Inventaire plein ?).");
                        }
                    }
                }

                if (this.model.nextBlind()) {
                    var nextBlind = state.getCurrentBlind();
                    var constraint = state.getCurrentConstraint();
                    String constraintText = (constraint != null && !constraint.name().equals("NONE")) ? "\nContrainte : " + constraint.getDescription() : "";

                    var msg = "[REWARD:/planets/" + wonPlanet.getFileName() + "|" + nextBlind.id() + "|" + jokerTagPart + "]"
                            + TextConstant.TEXT_CONSTANT_BLIND_BEATEN.getText() + "\n"
                            + "Prochain Niveau : " + nextBlind.name()
                            + constraintText
                            + jokerText;

                    this.view.showMessage(msg);
                } else {
                    SaveManager.saveGame(model, "VICTOIRE");
                    this.view.showGameOver(true, state.getCurrentScore());
                }

            } else if (state.getHandsLeft() <= 0) {
                saveHighScoreIfBetter(state);
                SaveManager.saveGame(model, "DÉFAITE");
                this.view.showGameOver(false, state.getCurrentScore());
            } else {
                SaveManager.saveGame(model, "EN_COURS");
                this.view.showMessage("[SCORE]" + TextConstant.TEXT_CONSTANT_HAND_PLAYED.getText() + scoreGained + TextConstant.TEXT_CONSTANT_POINTS.getText());
            }
            this.refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getJokerRarity(JokerType joker) {
        return this.model.getJokerRarityLabel(joker);
    }

    private void saveHighScoreIfBetter(GameState state) {
        try {
            long currentHigh = 0;
            File f = new File("saves/highscore.txt");
            if (f.exists()) currentHigh = Long.parseLong(Files.readString(f.toPath()).trim());

            if (state.getCurrentScore() > currentHigh) {
                Files.writeString(f.toPath(), String.valueOf(state.getCurrentScore()));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void handleDiscard() {
        if (this.selectedCards.isEmpty()) {
            this.view.showError(TextConstant.TEXT_CONSTANT_ERROR_PLAY_EMPTY.getText());
            return;
        }
        if (this.model.getState().getDiscardsLeft() <= 0) return;
        try {
            this.model.discardHand(new ArrayList<>(this.selectedCards));
            this.selectedCards.clear();
            this.refreshView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getExpectedScore() { return this.model.calculateExpectedScore(this.selectedCards); }
    public void saveAndQuit() { SaveManager.saveGame(this.model, "EN_COURS"); this.view.showMenu(); }
    public void refreshView() { this.view.update(this.model.getState(), this.model.getHand(), this.selectedCards, this.model.evaluateHand(this.selectedCards)); }
    public void exitGame() { System.exit(0); }
}