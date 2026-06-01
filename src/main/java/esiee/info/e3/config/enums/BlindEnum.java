package esiee.info.e3.config.enums;

import java.util.Objects;

public enum BlindEnum {
  THE_HOOK("Le Crochet"),
  THE_HANDCUFFS("Les Menottes"),
  THE_HOUSE("La Maison"),
  THE_WHEEL("La Roue"),
  THE_ARM("Le bras"),
  THE_EYE("L'Oeil"),
  THE_MOUTH("La Bouche"),
  THE_TOOTH("La Dent"),
  THE_HEAD("La Tête"),
  THE_HEARTH_OF_THE_GAME("Le Coeur du Jeu"),
  LITTLE_BLIND("Petite Blinde"),
  BIG_BLIND("Grosse Blinde");

  private final String text;

  BlindEnum(String text) {
    this.text = Objects.requireNonNull(text);
  }

  public String getText() {
    return this.text;
  }
}
