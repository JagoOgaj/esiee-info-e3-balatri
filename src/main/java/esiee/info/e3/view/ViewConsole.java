package esiee.info.e3.view;

import esiee.info.e3.config.enums.TextConstant;
import esiee.info.e3.controller.GameController;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.EvaluatedHand;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.interfaces.IView;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class ViewConsole implements IView {
    private GameController controller;
    private GameState currentState;
    private EvaluatedHand eval;
    private List<Card> currentHand = List.of();
    private List<Card> selectedCards = List.of();
    private String message = "Bienvenue dans Balatri !";

    //utils class
    private final Scanner sc = new Scanner(System.in);
    private final List<Character> actionPool = List.of('J', 'D', 'Q');

    @Override
    public void setController(GameController controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    @Override
    public void update(GameState state, List<Card> hand, List<Card> selectedCards, EvaluatedHand eval) {
        this.currentState = state;
        this.currentHand = hand;
        this.selectedCards = selectedCards;
        this.eval = eval;
    }

    public void showMessage(String message) {
        this.message = message;
        IO.println(message);
    }

    @Override
    public void showError(String error) {
        this.message = "ERREUR : " + error;
        IO.println(this.message);
    }

    @Override
    public void showGameOver(boolean victory, long finalScore) {
        if (!victory) {
            IO.println(TextConstant.TEXT_CONSTANT_DEFEAT.getText() + finalScore);
            IO.println(TextConstant.TEXT_CONSTANT_GGWP_DEFEAT.getText());
        } else {
            IO.println(TextConstant.TEXT_CONSTANT_VICTORY.getText() + finalScore);
            IO.println(TextConstant.TEXT_CONSTANT_GGWP_VICTORY.getText());
        }
            var choice = sc.next().trim().toUpperCase();
            while (!choice.equals("Q") && !choice.equals("R")) {
                IO.println("Veuillez entrer Q pour quitter ou R pour recommencer !");
                choice = sc.next().trim().toUpperCase();
            }
            if (choice.equals("Q")) {
                System.exit(0);
            }
            controller.resetGame();
        }



    @Override
    public void start() {
        while (true) {
            displayCurrentState();
            String input = sc.next().trim().toUpperCase();
            if (isArgumentValid(input)) {
                char action = input.charAt(0);
                if (actionPool.contains(action)) {
                    handleAction(action);
                } else {
                    handleSelectedCard(Character.getNumericValue(action));
                }
            } else {
                showError(TextConstant.TEXT_CONSTANT_ERROR_INVALID_INPUT.getText());
            }
        }
    }

    public void displayCurrentState() {
        for (int i = 0; i < 5; ++i) {
            IO.println("");
        }
        IO.println("============================================================");
        IO.println("[BLIND] " + currentState.getCurrentBlind().name() + " | [CIBLE] " + currentState.getCurrentBlind().score());
        IO.println("[SCORE] " + currentState.getCurrentScore() + "      | [MAINS] " + currentState.getHandsLeft() + "      | [DEFAUSSES] " + currentState.getDiscardsLeft());
        IO.println("[BONUS] " + (this.eval == null ? "X" : Planet.fromCombination(eval.combo()) == null ? "X" : Planet.fromCombination(eval.combo()).getLabel()));
        IO.println("============================================================");
        IO.println("");

        IO.println("Vos 8 cartes :");
        int i = 1;
        for (Card card : currentHand) {
            IO.println(i + ": " + card.rank().getLabel() + "-" + card.suit().getLabel());
            i++;
        }
        IO.println("");

        IO.println("Sélection actuelle : ");
        for (var card : selectedCards) {
            IO.print(card.rank().getLabel() + "-" + card.suit().getLabel() + " ");
        }
        IO.println("");

        IO.println("Actions :");
        IO.println("Entrez le numéro d'une carte pour l'ajouter ou la retirer.");
        IO.println("Tapez 'J' pour Jouer la main.");
        IO.println("Tapez 'D' pour Défausser la main.");
        IO.println("Tapez 'Q' pour Quitter.");
        IO.println("============================================================");

        IO.print("Votre choix : ");
    }


    public void handleSelectedCard(int selectedCard) {
        while (selectedCard < 1 || selectedCard > 8) {
            IO.println("Veuillez entrer un numéro de carte valide entre 1 et 8 !");
            selectedCard = sc.nextInt();
        }
        Card card = currentHand.get(selectedCard - 1);
        controller.toggleCardSelection(card);
    }

    public void handleAction(char actionSelected) {
        switch (actionSelected) {
            case 'Q' -> controller.handlePlay(Optional.of('Q'));
            case 'J' -> controller.handlePlay(Optional.of('J'));
            case 'D' -> controller.handleDiscard();
            default -> throw new IllegalArgumentException("Action non reconnue. Veuillez entrer J, D ou Q.");
        }
    }

    public Boolean isArgumentValid(String arg) {
        return arg.matches("^[1-8JDQ]$");
    }

}

