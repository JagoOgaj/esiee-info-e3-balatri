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

    public GameController(GameModel model, IView view) {
        this.model = Objects.requireNonNull(model);
        this.view = Objects.requireNonNull(view);
    }

    public void startGame(boolean infiniteMode) {
        this.model.resetSelectedCards();
        this.model.resetGame();
        this.model.getState().setInfiniteMode(infiniteMode);
        //this.refreshView();
        this.model.notifyObservers();
    }

    public void init() {
        //this.refreshView();
        this.model.notifyObservers();
        this.view.start();
    }

    public void resetGame() {
        this.model.resetGame();
        //this.refreshView();
        this.model.notifyObservers();
    }

    public void loadGameFromJson(String saveId) {
        this.model.resetSelectedCards();
        SaveManager.loadGame(saveId, this.model);
        //this.refreshView();
        this.model.notifyObservers();
    }

    public void toggleCardSelection(Card card) {
        var isOK = this.model.toggleCardSelection(card);
        if (!isOK){
            this.view.showError(TextConstant.TEXT_CONSTANT_ERROR_MAX_CARDS.getText());
        }
        //this.refreshView();
        this.model.notifyObservers();
    }

    public void handleRemoveJoker(JokerType joker) {
        boolean isRemoved = this.model.removeJoker(joker);
        if (!isRemoved) {
            this.view.showError("Erreur : Impossible de retirer ce Joker.");
        }
        //this.refreshView();
        this.model.notifyObservers();
    }

    public void handlePlay() {
        if (this.model.isEmptySelectedCards()) {
            view.showError(TextConstant.TEXT_CONSTANT_ERROR_PLAY_EMPTY.getText());
            return;
        }
        if (this.model.getState().getHandsLeft() <= 0) return;

        try {
            var scoreGained = this.model.playHand(new ArrayList<>(this.model.getSelectedCards()));
            this.model.resetSelectedCards();

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
                                this.model::notifyObservers,
                                (oldJoker) -> {
                                    if (this.model.removeJoker(oldJoker)) {
                                        if (!this.model.addJoker(wonJoker)) {
                                            this.view.showError("Erreur à l'ajout du nouveau Joker.");
                                        }
                                    } else {
                                        this.view.showError("Impossible de supprimer l'ancien Joker.");
                                    }
                                    this.model.notifyObservers();
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
            //this.refreshView();
            this.model.notifyObservers();
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
        if (this.model.isEmptySelectedCards()) {
            this.view.showError(TextConstant.TEXT_CONSTANT_ERROR_PLAY_EMPTY.getText());
            return;
        }
        if (this.model.getState().getDiscardsLeft() <= 0) return;
        try {
            this.model.discardHand(new ArrayList<>(this.model.getSelectedCards()));
            this.model.resetSelectedCards();
            //this.refreshView();
            this.model.notifyObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getExpectedScore() { return this.model.calculateExpectedScore(); }
    public void saveAndQuit() { SaveManager.saveGame(this.model, "EN_COURS"); this.view.showMenu(); }
    public void exitGame() { System.exit(0); }

    public long getHighScore() {
        try {
            java.io.File f = new java.io.File("saves/highscore.txt");
            if (f.exists()) {
                return Long.parseLong(java.nio.file.Files.readString(f.toPath()).trim());
            }
        } catch (Exception e) {
            System.err.println("Erreur de lecture du highscore : " + e.getMessage());
        }
        return 0;
    }
}