package esiee.info.e3.domain.enums;

import esiee.info.e3.domain.Card;
import java.util.function.Predicate;

public enum BlindConstraint {
    NONE("Aucune", "Pas d'effet spécial", c -> false, false),

    // --- CONTRAINTES FACILES (Désactivation d'une famille) ---
    THE_CLUB("The Club", "Toutes les cartes de Trèfle sont désactivées",
            c -> c.suit().name().equals("SUIT_CLOVER"), false),
    THE_GOAD("The Goad", "Toutes les cartes de Pique sont désactivées",
            c -> c.suit().name().equals("SUIT_SPIKE"), false),
    THE_WINDOW("The Window", "Toutes les cartes de Carreau sont désactivées",
            c -> c.suit().name().equals("SUIT_TILE"), false),
    THE_HEAD("The Head", "Toutes les cartes de Cœur sont désactivées",
            c -> c.suit().name().equals("SUIT_HEART"), false),

    // --- CONTRAINTES DIFFICILES ---
    THE_HOOK("The Hook", "Défausse 2 cartes au hasard après chaque main", c -> false, false),
    THE_MANACLE("The Manacle", "-1 Taille de Main", c -> false, false),
    THE_HOUSE("The House", "Toutes les cartes en main sont face cachée", c -> false, true);

    private final String label;
    private final String description;
    private final Predicate<Card> disableCondition;
    private final boolean hidden;

    BlindConstraint(String label, String description, Predicate<Card> disableCondition, boolean hidden) {
        this.label = label;
        this.description = description;
        this.disableCondition = disableCondition;
        this.hidden = hidden;
    }

    public String getLabel() { return label; }
    public String getDescription() { return description; }

    // Règle métier encapsulée : La carte est-elle annulée par le Boss ?
    public boolean isCardDisabled(Card card) { return disableCondition.test(card); }

    // Règle métier encapsulée : Le Boss force-t-il la face cachée ?
    public boolean isHidden() { return hidden; }
}