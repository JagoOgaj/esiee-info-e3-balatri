package esiee.info.e3.domain;

public enum Combination {

    COMBINATION_HIGH_MAP("Carte Haute", "Aucune combinaison", 5, 1),
    COMBINATION_PAIR("Paire", "Deux cartes de même rang", 10, 2),
    COMBINATION_DOUBLE_PAIR("Double paire", "Deux paires distinctes", 20, 2),
    COMBINATION_THREE_OF_KIND("Brelan", "Trois cartes de même rang", 30, 3),
    COMBINATION_STRAIGHT("Suite", "Cinq cartes de rangs consécutifs", 30, 4),
    COMBINATION_COLOR("Couleur", "Cinq cartes de même couleur", 35, 4),
    COMBINATION_FULL("Full", "Un brelan et une paire", 40, 4),
    COMBINATION_SQUARE("Carré", "Quatre cartes de même rang", 60, 7),
    COMBINATION_STRAIGHT_COLOR("Quinte flush", "Suite et couleur réunies", 100, 8);

    private final String label;
    private final String description;
    private final int basicChips;
    private final int multiplier;

    private Combination(String label, String description, int basicChips, int multiplier) {
        this.label = label;
        this.description = description;
        this.basicChips = basicChips;
        this.multiplier = multiplier;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public int getBasicChips() {
        return this.basicChips;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

}
