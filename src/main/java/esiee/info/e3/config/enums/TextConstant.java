package esiee.info.e3.config.enums;

public enum TextConstant {
    TEXT_CONSTANT_VICTORY("VICTOIRE ! Score : "),
    TEXT_CONSTANT_DEFEAT("DÉFAITE ! Score : "),
    TEXT_CONSTANT_GAME_TITLE("BALATRI"),
    TEXT_CONSTANT_PLAY("Jouer"),
    TEXT_CONSTANT_BY(" / "),
    TEXT_CONSTANT_SCORE_TO_ACHIEVE("Score à atteindre"),
    TEXT_CONSTANT_ACTUAL_SCORE("Score Actuel"),
    TEXT_CONSTANT_DEFAULT_0X0("0 x 0"),
    TEXT_CONSTANT_SELECT("Sélectionnez"),
    TEXT_CONSTANT_HAND("Mains"),
    TEXT_CONSTANT_DEFAUSSE("Défausses"),
    TEXT_CONSTANT_DEFAUSSED("DÉFAUSSER"),
    TEXT_CONSTANT_PLAY_HAND("JOUER LA MAIN"),
    TEXT_CONSTANT_ERROR_MAX_CARDS("Vous ne pouvez sélectionner que 5 cartes maximum."),
    TEXT_CONSTANT_BLIND_BEATEN("BLIND BATTUE ! Niveau de "),
    TEXT_CONSTANT_LEVEL_INCREASED(" augmenté !"),
    TEXT_CONSTANT_HAND_PLAYED("Main jouée ! +"),
    TEXT_CONSTANT_POINTS(" points."),
    TEXT_CONSTANT_ERROR_INVALID_INPUT("Commande non reconnue. Veuillez entrer 1 à 8, J, D ou Q."),
    TEXT_CONSTANT_ERROR_PLAY_EMPTY("Vous ne pouvez jouer, aucune carte n'est selectionnée."),
    TEXT_CONSTANT_ERROR_DISCARD_EMPTY("Vous ne pouvez défausser, aucune carte n'est selectionnée.");

    private final String text;

    private TextConstant(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String formatBy(String value) {
        return this.text + value;
    }
}
