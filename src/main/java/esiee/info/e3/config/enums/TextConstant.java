package esiee.info.e3.config.enums;

import java.util.Objects;

public enum TextConstant {
  TEXT_CONSTANT_VICTORY("VICTOIRE ! Score : "),
  TEXT_CONSTANT_GGWP_VICTORY(
      "Félicitations ! Tu as réussi à battre tous les blinds ! (Appuie sur Q pour quitter ou R pour recommencer)"),
  TEXT_CONSTANT_DEFEAT("DÉFAITE ! Score : "),
  TEXT_CONSTANT_GGWP_DEFEAT(
      "Tu feras (surement) mieux la prochaine fois ! (Appuie sur Q pour quitter ou R pour recommencer)"),
  TEXT_CONSTANT_GAME_TITLE("BALATRI"),
  TEXT_CONSTANT_PLAY("Jouer"),
  TEXT_CONSTANT_BY_52(" / 52"),
  TEXT_CONSTANT_BY_8(" / 8"),
  TEXT_CONSTANT_BY_5(" / 5"),
  TEXT_CONSTANT_SCORE_TO_ACHIEVE("Score à atteindre"),
  TEXT_CONSTANT_ACTUAL_SCORE("Score Actuel"),
  TEXT_CONSTANT_DEFAULT_0X0("0 x 0"),
  TEXT_CONSTANT_SELECT("Sélectionnez"),
  TEXT_CONSTANT_HAND("Mains"),
  TEXT_CONSTANT_DEFAUSSE("Défausses"),
  TEXT_CONSTANT_DEFAUSSED("DÉFAUSSER"),
  TEXT_CONSTANT_PLAY_HAND("JOUER LA MAIN"),
  TEXT_CONSTANT_ERROR_MAX_CARDS("Vous ne pouvez sélectionner que 5 cartes maximum."),
  TEXT_CONSTANT_BLIND_BEATEN("BLIND BATTUE"),
  TEXT_CONSTANT_LEVEL_INCREASED("Les combinaisons joués sont augmentées !"),
  TEXT_PLANET_WIN("Tu as obtenu la planète "),
  TEXT_CONSTANT_HAND_PLAYED("Main jouée ! +"),
  TEXT_CONSTANT_POINTS(" points."),
  TEXT_CONSTANT_ERROR_INVALID_INPUT("Commande non reconnue. Veuillez entrer 1 à 8, J, D ou Q."),
  TEXT_CONSTANT_ERROR_PLAY_EMPTY("Vous ne pouvez jouer, aucune carte n'est selectionnée."),
  TEXT_CONSTANT_ERROR_DISCARD_EMPTY("Vous ne pouvez défausser, aucune carte n'est selectionnée."),
  TEXT_CONSTANT_SCORE("[SCORE]"),
  TEXT_CONSTANT_NO_HAND_AVAILABLE("Plus de mains disponibles !"),
  TEXT_CONSTANT_PREFIX_CONSTRAINT("\nContrainte : "),
  TEMPLATE_ROUND_REWARD_MESSAGE(
      "[SHOP_REWARD:%d|%d|/planets/%s]%s\n"
          + "Prochain Niveau : %s%s\n\n"
          + "Gains du Round :\n"
          + "Base : %d $\n"
          + "Mains restantes : +%d $\n"
          + "Défausses restantes : +%d $\n"
          + "Total gagné : %d $"),

  TEXT_CONSTANT_LOOP(" [Boucle %d ]"),
  TEXT_CONSTANT_GAME_PAUSED("JEU EN PAUSE"),
  TEXT_CONSTANT_TO_RESUME("REPRENDRE"),
  TEXT_CONSTANT_BACK_TO_MENU("RETOUR AU MENU"),
  TEXT_CONSTANT_BREAK("PAUSE"),
  TEXT_CONSTANT_$("$"),
  TEXT_CONSTANT_PLUS_$("+$"),
  TEXT_CONSTANT_FORECAST_HIDE("Prévision : ??? pts"),
  TEXT_CONSTANT_FORECAST("Prévision : "),
  TEXT_CONSTANT_POINT_SUFFIXE(" pts"),
  TEXT_CONSTANT_HIGHT_SCORE("Le Score le plus élevé : "),
  TEXT_CONSTANT_LVL_1("Lvl 1"),
  TEMPLATE_LVL("Lvl %d"),
  TEXT_CONSTANT_PLANET("PLANÈTE"),
  TEXT_CONSTANT_JOKER("JOKER"),
  TEXT_CONSTANT_EARN("Total gagné :"),
  TEXT_CONSTANT_MONEY("MONNAIE"),
  TEXT_CONSTANT_TO_CONTINUE("(Cliquez n'importe où pour continuer)"),
  TEXT_CONSTANT_TO_CLOSE("(Cliquez n'importe où pour fermer)"),
  TEXT_CONSTANT_CLASSIC_MOD("MODE CLASSIQUE"),
  TEXT_CONSTANT_INFINITY_MOD("MODE INFINI"),
  TEXT_CONSTANT_PARTIES_LISTING("LISTE DES PARTIES"),
  TEXT_CONSTANT_QUIT_GAME("QUITTER LE JEU"),
  TEXT_CONSTANT_VERSION_GAME("v0.0.1-STABLE"),
  TEXT_CONSTANT_LOADING("Chargement"),
  TEXT_SAVES_TITLE("SAUVEGARDES"),
  TEXT_SAVES_EMPTY("Aucune sauvegarde disponible..."),
  TEXT_SAVES_MODE_INFINITE("Infini (B.%d)"),
  TEXT_SAVES_MODE_CLASSIC("Classique"),
  TEXT_SAVES_SCORE_SUFFIX("%d pts"),
  TEXT_SAVES_RESUME("REPRENDRE"),
  TEXT_SAVES_PREVIOUS("< PRECEDENT"),
  TEXT_SAVES_NEXT("SUIVANT >"),
  TEXT_SAVES_RETURN("RETOUR"),
  TEXT_SAVES_CLICK_CLOSE("(Cliquez pour fermer)"),
  TEXT_SAVES_STATUS_VICTORY("VICTOIRE"),
  TEXT_SAVES_STATUS_DEFEAT("DÉFAITE"),
  TEXT_SHOP_TITLE("LA BOUTIQUE"),
  TEXT_SHOP_MONEY("Monnaie : %d $"),
  TEXT_SHOP_NEXT_LEVEL("Prochain Niveau"),
  TEXT_SHOP_REROLL("Reroll (5 $)"),
  TEXT_SHOP_ERROR_NO_MONEY_REROLL("Pas assez d'argent pour rafraîchir !"),
  TEXT_SHOP_ITEMS_FOR_SALE("Articles en vente :"),
  TEXT_SHOP_PRICE_TAG("%d $"),
  TEXT_SHOP_ERROR_NO_MONEY_BUY("Vous n'avez pas assez d'argent !"),
  TEXT_SHOP_EFFECT("Effet : %s"),
  TEXT_SHOP_PRICE_LABEL("Prix : %d $"),
  TEXT_SHOP_BUY("Acheter"),
  TEXT_SHOP_CANCEL_PREVIEW("Reposer"),
  TEXT_SHOP_REFUND_QUESTION("Reposer %s ?"),
  TEXT_SHOP_REFUND_LABEL("Remboursement : +%d $"),
  TEXT_SHOP_REFUND_CONFIRM("Reposer (Rembourser)"),
  TEXT_SHOP_REFUND_KEEP("Garder"),
  TEXT_SHOP_ERROR_INVENTORY_FULL("INVENTAIRE PLEIN ! Cliquez sur un Joker à remplacer."),
  TEXT_SHOP_CANCEL_SWAP("ANNULER"),
  TEXT_SHOP_YOUR_JOKERS("Vos Jokers :"),
  ;

  private final String text;

  TextConstant(String text) {
    this.text = Objects.requireNonNull(text);
  }

  public String getText() {
    return this.text;
  }
}
