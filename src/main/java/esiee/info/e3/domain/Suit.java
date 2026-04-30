package esiee.info.e3.domain;

public enum Suit {
    SUIT_CLOVER("Trèfle", 1),
    SUIT_TILE("Carreau", 2),
    SUIT_HEART("Coeur", 3),
    SUIT_SPIKE("Pique", 4);

    private final String label;
    private final int value;

    private Suit(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return this.label;
    }

    public int getValue() {
        return this.value;
    }
}
