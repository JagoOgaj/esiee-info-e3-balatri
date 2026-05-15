package esiee.info.e3.domain.enums;

import static esiee.info.e3.domain.enums.Combination.*;

public enum Planet {
    PLANET_PLUTO("Pluton", COMBINATION_HIGH_MAP, 10, 1),
    PLANET_MERCURY("Mercure", COMBINATION_PAIR, 15, 1),
    PLANET_URANUS("Uranus", COMBINATION_DOUBLE_PAIR, 20, 1),
    PLANET_VENUS("Venus", COMBINATION_THREE_OF_KIND, 20, 2),
    PLANET_SATURN("Saturne", COMBINATION_STRAIGHT, 30, 3),
    PLANET_JUPITER("Jupiter", COMBINATION_COLOR, 15, 2),
    PLANET_EARTH("Terre", COMBINATION_FULL, 25, 2),
    PLANET_MARS("Mars", COMBINATION_SQUARE, 30, 3),
    PLANET_NEPTUNE("Neptune", COMBINATION_STRAIGHT_COLOR, 40, 4);

    private final String label;
    private final Combination combination;
    private final int chipsBonus;
    private final int multiplierBonus;

    private Planet(String label, Combination combination, int chipsBonus, int multiplierBonus) {
        this.label = label;
        this.combination = combination;
        this.chipsBonus = chipsBonus;
        this.multiplierBonus = multiplierBonus;
    }

    public static Planet fromCombination(Combination combo) {
        for (var p : values()) {
            if (p.getCombination() == combo) return p;
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public int getMultiplierBonus() {
        return this.multiplierBonus;
    }

    public Combination getCombination() {
        return this.combination;
    }

    public int getChipsBonus() {
        return this.chipsBonus;
    }

    public String getFileName() {
        return this.name().replace("PLANET_", "").toLowerCase() + ".png";
    }
}
