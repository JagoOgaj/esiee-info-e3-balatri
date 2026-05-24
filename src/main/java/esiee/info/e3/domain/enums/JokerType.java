package esiee.info.e3.domain.enums;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.JokerContext;
import esiee.info.e3.domain.ScoreResult;
import esiee.info.e3.model.interfaces.JokerEffect;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public enum JokerType {

  JOKER_001(
      "001",
      "Gros Jojo",
      "+20 Jetons si la main contient au moins un As.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().anyMatch(c -> c.rank().getValue() == 14)
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_002(
      "002",
      "Petit Jojo",
      "+10 Jetons par carte de rang inférieur ou égal à 5.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() <= 5).count()
                      * 10))),
  JOKER_003(
      "003",
      "Têtes Couronnées",
      "+10 Mult si une figure (V, D, R) est jouée.",
      (ctx, score) ->
          ctx.allSelectedCards().stream()
                  .anyMatch(c -> c.rank().getValue() >= 11 && c.rank().getValue() <= 13)
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_004(
      "004",
      "Suite de Fibonacci",
      "+8 Mult si la main contient 2, 3, 5 ou 8.",
      (ctx, score) ->
          ctx.allSelectedCards().stream()
                  .anyMatch(
                      c -> {
                        int v = c.rank().getValue();
                        return v == 2 || v == 3 || v == 5 || v == 8;
                      })
              ? score.withMultiplier(score.multiplier() + 8)
              : score),
  JOKER_005(
      "005",
      "Le Chiffre 7",
      "+77 Jetons si la main contient au moins un 7.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().anyMatch(c -> c.rank().getValue() == 7)
              ? score.withChips(score.chips() + 77)
              : score),
  JOKER_006(
      "006",
      "Diabolique",
      "+6 Mult pour chaque carte de rang 6 jouée.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 6).count()
                      * 6))),
  JOKER_007(
      "007",
      "Le Baron",
      "+15 Mult pour chaque Roi joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 13).count()
                      * 15))),
  JOKER_008(
      "008",
      "Matador",
      "+40 Jetons si la main contient un Valet.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().anyMatch(c -> c.rank().getValue() == 11)
              ? score.withChips(score.chips() + 40)
              : score),
  JOKER_009(
      "009",
      "Trio d'As",
      "+30 Jetons et +3 Mult si exactement trois As sont joués.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 14).count() == 3
              ? new ScoreResult(score.chips() + 30, score.multiplier() + 3)
              : score),

  JOKER_010(
      "010",
      "Spécialiste Deux",
      "+4 Mult pour chaque 2 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 2).count()
                      * 4))),
  JOKER_011(
      "011",
      "Briseur de Chaînes",
      "+12 Mult si un Boss Blind (contrainte) est actif.",
      (ctx, score) ->
          ctx.constraint() != BlindConstraint.NONE
              ? score.withMultiplier(score.multiplier() + 12)
              : score),
  JOKER_012(
      "012",
      "Spécialiste Trois",
      "+4 Mult pour chaque 3 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 3).count()
                      * 4))),
  JOKER_013(
      "013",
      "Spécialiste Quatre",
      "+4 Mult pour chaque 4 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 4).count()
                      * 4))),
  JOKER_014(
      "014",
      "Spécialiste Cinq",
      "+4 Mult pour chaque 5 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 5).count()
                      * 4))),
  JOKER_015(
      "015",
      "Spécialiste Six",
      "+4 Mult pour chaque 6 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 6).count()
                      * 4))),
  JOKER_016(
      "016",
      "Spécialiste Sept",
      "+4 Mult pour chaque 7 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 7).count()
                      * 4))),
  JOKER_017(
      "017",
      "Spécialiste Huit",
      "+4 Mult pour chaque 8 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 8).count()
                      * 4))),
  JOKER_018(
      "018",
      "Spécialiste Neuf",
      "+4 Mult pour chaque 9 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 9).count()
                      * 4))),
  JOKER_019(
      "019",
      "Spécialiste Dix",
      "+4 Mult pour chaque 10 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 10).count()
                      * 4))),
  JOKER_020(
      "020",
      "Spécialiste Dame",
      "+4 Mult pour chaque Dame jouée.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 12).count()
                      * 4))),

  JOKER_021(
      "021",
      "L'Impair",
      "+3 Mult par carte de rang impair.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() % 2 != 0)
                          .count()
                      * 3))),
  JOKER_022(
      "022",
      "Le Pair",
      "+30 Jetons par carte de rang pair.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() % 2 == 0)
                          .count()
                      * 30))),
  JOKER_023(
      "023",
      "Minimaliste",
      "+80 Jetons si la sélection ne contient qu'une seule carte.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 1 ? score.withChips(score.chips() + 80) : score),
  JOKER_024(
      "024",
      "Surchargé",
      "+25 Jetons par carte supérieure à 10.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() > 10).count()
                      * 25))),
  JOKER_025(
      "025",
      "Division par Zéro",
      "x1.5 Mult si la somme des rangs est inférieure à 10.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().mapToInt(c -> c.rank().getValue()).sum() < 10
              ? score.withMultiplier((long) (score.multiplier() * 1.5))
              : score),
  JOKER_026(
      "026",
      "Le Grimoire",
      "+1 Mult par tranche de 5 cartes restantes dans la pioche.",
      (ctx, score) -> score.withMultiplier(score.multiplier() + (ctx.state().getDeckSize() / 5))),
  JOKER_027(
      "027",
      "Mirage",
      "+11 Jetons bonus par carte Face (V, D, R).",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() >= 11 && c.rank().getValue() <= 13)
                          .count()
                      * 11))),
  JOKER_028(
      "028",
      "L'Alchimiste",
      "+2 Mult pour chaque 2, 3 ou 4 joué.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() >= 2 && c.rank().getValue() <= 4)
                          .count()
                      * 2))),
  JOKER_029(
      "029",
      "Heavy Metal",
      "+50 Jetons si la main est uniquement composée de cartes >= 10.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream().allMatch(c -> c.rank().getValue() >= 10))
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_030(
      "030",
      "Petite Suite",
      "+8 Mult si la main contient un 2, un 3 et un 4.",
      (ctx, score) -> {
        var r = ctx.allSelectedCards().stream().map(c -> c.rank().getValue()).toList();
        return (r.contains(2) && r.contains(3) && r.contains(4))
            ? score.withMultiplier(score.multiplier() + 8)
            : score;
      }),

  JOKER_031(
      "031",
      "Hacker de Piques",
      "+4 Mult pour chaque carte de Trèfle ou Pique active.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.suit() == Suit.SUIT_SPIKE || c.suit() == Suit.SUIT_CLOVER)
                          .count()
                      * 4))),
  JOKER_032(
      "032",
      "Cœur Brisé",
      "+30 Jetons pour chaque carte de Cœur jouée.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.suit() == Suit.SUIT_HEART)
                          .count()
                      * 30))),
  JOKER_033(
      "033",
      "Sang Pur",
      "+5 Mult si toutes les cartes jouées sont rouges.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream()
                      .allMatch(c -> c.suit() == Suit.SUIT_HEART || c.suit() == Suit.SUIT_TILE))
              ? score.withMultiplier(score.multiplier() + 5)
              : score),
  JOKER_034(
      "034",
      "Ombre Chinoise",
      "+5 Mult si toutes les cartes jouées sont noires.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream()
                      .allMatch(c -> c.suit() == Suit.SUIT_SPIKE || c.suit() == Suit.SUIT_CLOVER))
              ? score.withMultiplier(score.multiplier() + 5)
              : score),
  JOKER_035(
      "035",
      "Diamant Brut",
      "+40 Jetons pour chaque carte de Carreau active.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream().filter(c -> c.suit() == Suit.SUIT_TILE).count()
                      * 40))),
  JOKER_036(
      "036",
      "Monochrome",
      "+10 Mult si la combinaison est une Couleur (Flush).",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_COLOR
                  || ctx.combo() == Combination.COMBINATION_STRAIGHT_COLOR
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_037(
      "037",
      "Bicolore",
      "+4 Mult si la main contient exactement 2 couleurs différentes.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().map(Card::suit).distinct().count() == 2
              ? score.withMultiplier(score.multiplier() + 4)
              : score),
  JOKER_038(
      "038",
      "Tricolore",
      "+6 Mult si la main contient exactement 3 couleurs différentes.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().map(Card::suit).distinct().count() == 3
              ? score.withMultiplier(score.multiplier() + 6)
              : score),
  JOKER_039(
      "039",
      "Caméléon",
      "+15 Jetons si des cartes rouges et noires sont jouées ensemble.",
      (ctx, score) ->
          (ctx.allSelectedCards().stream()
                      .anyMatch(c -> c.suit() == Suit.SUIT_HEART || c.suit() == Suit.SUIT_TILE)
                  && ctx.allSelectedCards().stream()
                      .anyMatch(c -> c.suit() == Suit.SUIT_CLOVER || c.suit() == Suit.SUIT_SPIKE))
              ? score.withChips(score.chips() + 15)
              : score),

  JOKER_040(
      "040",
      "Obsession Trèfle",
      "x2 Mult si toutes les cartes sont Trèfle.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream().allMatch(c -> c.suit() == Suit.SUIT_CLOVER))
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_041(
      "041",
      "Obsession Cœur",
      "x2 Mult si toutes les cartes sont Cœur.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream().allMatch(c -> c.suit() == Suit.SUIT_HEART))
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_042(
      "042",
      "Obsession Pique",
      "x2 Mult si toutes les cartes sont Pique.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream().allMatch(c -> c.suit() == Suit.SUIT_SPIKE))
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_043(
      "043",
      "Obsession Carreau",
      "x2 Mult si toutes les cartes sont Carreau.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream().allMatch(c -> c.suit() == Suit.SUIT_TILE))
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_044(
      "044",
      "Fanatique Trèfle",
      "+50 Jetons si la main contient au moins 3 Trèfles.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().filter(c -> c.suit() == Suit.SUIT_CLOVER).count() >= 3
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_045(
      "045",
      "Fanatique Cœur",
      "+50 Jetons si la main contient au moins 3 Cœurs.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().filter(c -> c.suit() == Suit.SUIT_HEART).count() >= 3
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_046(
      "046",
      "Fanatique Pique",
      "+50 Jetons si la main contient au moins 3 Piques.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().filter(c -> c.suit() == Suit.SUIT_SPIKE).count() >= 3
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_047(
      "047",
      "Fanatique Carreau",
      "+50 Jetons si la main contient au moins 3 Carreaux.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().filter(c -> c.suit() == Suit.SUIT_TILE).count() >= 3
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_048(
      "048",
      "Dévotion Rouge",
      "+20 Jetons pour chaque combinaison 100% rouge.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream()
                      .allMatch(c -> c.suit() == Suit.SUIT_HEART || c.suit() == Suit.SUIT_TILE))
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_049(
      "049",
      "Dévotion Noire",
      "+20 Jetons pour chaque combinaison 100% noire.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream()
                      .allMatch(c -> c.suit() == Suit.SUIT_CLOVER || c.suit() == Suit.SUIT_SPIKE))
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_050(
      "050",
      "Puriste",
      "+15 Mult si une seule couleur est jouée.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().map(Card::suit).distinct().count() == 1
              ? score.withMultiplier(score.multiplier() + 15)
              : score),

  JOKER_051(
      "051",
      "Collecteur de Rangs",
      "+1 Mult par carte dans la pioche.",
      (ctx, score) -> score.withMultiplier(score.multiplier() + ctx.state().getDeckSize())),
  JOKER_052(
      "052",
      "Fossoyeur",
      "+3 Jetons par carte dans la défausse.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + ((52 - ctx.state().getDeckSize() - ctx.allSelectedCards().size()) * 3L))),
  JOKER_053(
      "053",
      "Amasseur",
      "+10 Jetons par niveau du Blind actuel.",
      (ctx, score) -> score.withChips(score.chips() + (ctx.state().getCurrentBlindIndex() * 10L))),
  JOKER_054(
      "054",
      "Archiviste",
      "+5 Mult si c'est votre dernière main.",
      (ctx, score) ->
          ctx.state().getHandsLeft() == 1 ? score.withMultiplier(score.multiplier() + 5) : score),
  JOKER_055(
      "055",
      "Prévoyant",
      "+20 Jetons si vous avez toutes vos défausses.",
      (ctx, score) ->
          ctx.state().getDiscardsLeft() == 3 ? score.withChips(score.chips() + 20) : score),
  JOKER_056(
      "056",
      "Bibliothécaire",
      "x1.2 Mult si le Deck contient plus de 40 cartes.",
      (ctx, score) ->
          ctx.state().getDeckSize() > 40
              ? score.withMultiplier((long) (score.multiplier() * 1.2))
              : score),
  JOKER_057(
      "057",
      "Le Gardien",
      "+30 Jetons si l'objectif du Blind est atteint.",
      (ctx, score) ->
          ctx.state().getCurrentScore() + score.chips() * score.multiplier()
                  >= ctx.state().getCurrentBlind().score()
              ? score.withChips(score.chips() + 30)
              : score),
  JOKER_058(
      "058",
      "Symphonie",
      "+2 Mult par planète découverte.",
      (ctx, score) ->
          score.withMultiplier(score.multiplier() + (ctx.state().getLevel(ctx.combo()) * 2L))),
  JOKER_059(
      "059",
      "Antiquaire",
      "+50 Jetons si moins de 10 cartes dans la pioche.",
      (ctx, score) -> ctx.state().getDeckSize() < 10 ? score.withChips(score.chips() + 50) : score),
  JOKER_060(
      "060",
      "Accumulateur",
      "Ajoute +1 Mult pour chaque 1000 points de score.",
      (ctx, score) ->
          score.withMultiplier(score.multiplier() + (ctx.state().getCurrentScore() / 1000))),

  JOKER_061(
      "061",
      "Pro de la Paire",
      "+4 Mult si la combinaison est une Paire.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_PAIR
              ? score.withMultiplier(score.multiplier() + 4)
              : score),
  JOKER_062(
      "062",
      "Double Paire Élite",
      "+6 Mult et +20 Jetons si Double Paire.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_DOUBLE_PAIR
              ? new ScoreResult(score.chips() + 20, score.multiplier() + 6)
              : score),
  JOKER_063(
      "063",
      "Brelan d'Or",
      "+8 Mult si Brelan.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_THREE_OF_KIND
              ? score.withMultiplier(score.multiplier() + 8)
              : score),
  JOKER_064(
      "064",
      "Full Master",
      "+12 Mult si Full House.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_FULL
              ? score.withMultiplier(score.multiplier() + 12)
              : score),
  JOKER_065(
      "065",
      "Escabeau",
      "+10 Mult si Suite.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_STRAIGHT
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_066(
      "066",
      "Super Nova",
      "+2 Mult par niveau de la combinaison actuelle.",
      (ctx, score) ->
          score.withMultiplier(score.multiplier() + (ctx.state().getLevel(ctx.combo()) * 2L))),
  JOKER_067(
      "067",
      "Répétiteur",
      "+5 Mult si aucune défausse n'a été utilisée.",
      (ctx, score) ->
          ctx.state().getDiscardsLeft() == 3
              ? score.withMultiplier(score.multiplier() + 5)
              : score),
  JOKER_068(
      "068",
      "Polyvalent",
      "+30 Jetons si la main fait exactement 5 cartes.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 5 ? score.withChips(score.chips() + 30) : score),
  JOKER_069(
      "069",
      "Chaos Ordinateur",
      "+4 Mult si la combinaison est Carte Haute.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_HIGH_MAP
              ? score.withMultiplier(score.multiplier() + 4)
              : score),

  JOKER_070(
      "070",
      "Carré Magique",
      "+20 Mult si Carré.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_SQUARE
              ? score.withMultiplier(score.multiplier() + 20)
              : score),
  JOKER_071(
      "071",
      "Quinte Ultime",
      "+30 Mult si Quinte Flush.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_STRAIGHT_COLOR
              ? score.withMultiplier(score.multiplier() + 30)
              : score),
  JOKER_072(
      "072",
      "Acrobate",
      "x2 Mult si c'est votre dernière main du Blind.",
      (ctx, score) ->
          ctx.state().getHandsLeft() == 1 ? score.withMultiplier(score.multiplier() * 2) : score),
  JOKER_073(
      "073",
      "Oiseau de Nuit",
      "+15 Jetons si la main contient des Piques et des Trèfles.",
      (ctx, score) ->
          (ctx.allSelectedCards().stream().anyMatch(c -> c.suit() == Suit.SUIT_SPIKE)
                  && ctx.allSelectedCards().stream().anyMatch(c -> c.suit() == Suit.SUIT_CLOVER))
              ? score.withChips(score.chips() + 15)
              : score),
  JOKER_074(
      "074",
      "Soleil Levant",
      "+15 Jetons si la main contient des Cœurs et Carreaux.",
      (ctx, score) ->
          (ctx.allSelectedCards().stream().anyMatch(c -> c.suit() == Suit.SUIT_HEART)
                  && ctx.allSelectedCards().stream().anyMatch(c -> c.suit() == Suit.SUIT_TILE))
              ? score.withChips(score.chips() + 15)
              : score),
  JOKER_075(
      "075",
      "L'Ermite",
      "+40 Jetons si Carte Haute de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_HIGH_MAP && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 40)
              : score),
  JOKER_076(
      "076",
      "Jumeaux",
      "+30 Jetons si Paire de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_PAIR && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 30)
              : score),
  JOKER_077(
      "077",
      "Quartette",
      "+50 Jetons si Double Paire de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_DOUBLE_PAIR
                  && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_078(
      "078",
      "Troupe",
      "+60 Jetons si Brelan de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_THREE_OF_KIND
                  && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 60)
              : score),
  JOKER_079(
      "079",
      "Défilé",
      "+70 Jetons si Suite de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_STRAIGHT && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 70)
              : score),
  JOKER_080(
      "080",
      "Peintre",
      "+80 Jetons si Couleur de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_COLOR && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 80)
              : score),
  JOKER_081(
      "081",
      "Bâtisseur",
      "+90 Jetons si Full de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_FULL && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 90)
              : score),
  JOKER_082(
      "082",
      "Architecte",
      "+100 Jetons si Carré de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_SQUARE && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 100)
              : score),
  JOKER_083(
      "083",
      "Visionnaire",
      "+150 Jetons si Quinte Flush de niveau > 1.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_STRAIGHT_COLOR
                  && ctx.state().getLevel(ctx.combo()) > 1)
              ? score.withChips(score.chips() + 150)
              : score),
  JOKER_084(
      "084",
      "Le Guide",
      "+5 Mult si la combinaison actuelle est niveau 3+.",
      (ctx, score) ->
          ctx.state().getLevel(ctx.combo()) >= 3
              ? score.withMultiplier(score.multiplier() + 5)
              : score),
  JOKER_085(
      "085",
      "L'Expert",
      "+10 Mult si la combinaison actuelle est niveau 5+.",
      (ctx, score) ->
          ctx.state().getLevel(ctx.combo()) >= 5
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_086(
      "086",
      "Le Maître",
      "+20 Mult si la combinaison actuelle est niveau 10+.",
      (ctx, score) ->
          ctx.state().getLevel(ctx.combo()) >= 10
              ? score.withMultiplier(score.multiplier() + 20)
              : score),
  JOKER_087(
      "087",
      "Étoile Filante",
      "+15 Jetons pour chaque carte sélectionnée.",
      (ctx, score) -> score.withChips(score.chips() + (ctx.allSelectedCards().size() * 15L))),
  JOKER_088(
      "088",
      "Comète",
      "+2 Mult pour chaque carte sélectionnée.",
      (ctx, score) ->
          score.withMultiplier(score.multiplier() + (ctx.allSelectedCards().size() * 2L))),
  JOKER_089(
      "089",
      "Constellation",
      "x1.5 Mult si vous jouez 5 cartes.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 5
              ? score.withMultiplier((long) (score.multiplier() * 1.5))
              : score),
  JOKER_090(
      "090",
      "Galaxie",
      "x2 Mult si la combinaison est un Full, Carré ou Quinte Flush.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_FULL
                  || ctx.combo() == Combination.COMBINATION_SQUARE
                  || ctx.combo() == Combination.COMBINATION_STRAIGHT_COLOR)
              ? score.withMultiplier(score.multiplier() * 2)
              : score),

  JOKER_091(
      "091",
      "Planétologue",
      "+15 Jetons multipliés par le niveau de la combinaison.",
      (ctx, score) -> score.withChips(score.chips() + (ctx.state().getLevel(ctx.combo()) * 15L))),
  JOKER_092(
      "092",
      "Économe",
      "+20 Jetons par tranche de 10 cartes restantes dans la pioche.",
      (ctx, score) -> score.withChips(score.chips() + ((ctx.state().getDeckSize() / 10) * 20L))),
  JOKER_093(
      "093",
      "Vide-Poche",
      "+35 Jetons si vous n'avez plus de défausse.",
      (ctx, score) ->
          ctx.state().getDiscardsLeft() == 0 ? score.withChips(score.chips() + 35) : score),
  JOKER_094(
      "094",
      "Boussole",
      "+3 Mult si l'index du Blind actuel est impair.",
      (ctx, score) ->
          ctx.state().getCurrentBlindIndex() % 2 != 0
              ? score.withMultiplier(score.multiplier() + 3)
              : score),
  JOKER_095(
      "095",
      "Cadran Solaire",
      "+40 Jetons si l'index du Blind actuel est pair.",
      (ctx, score) ->
          ctx.state().getCurrentBlindIndex() % 2 == 0
              ? score.withChips(score.chips() + 40)
              : score),
  JOKER_096(
      "096",
      "Anti-Hook",
      "Donne +50 Jetons si la contrainte The Hook est active.",
      (ctx, score) ->
          ctx.constraint() == BlindConstraint.THE_HOOK
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_097(
      "097",
      "Anti-Manacle",
      "Donne +10 Mult si la contrainte The Manacle est active.",
      (ctx, score) ->
          ctx.constraint() == BlindConstraint.THE_MANACLE
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_098(
      "098",
      "Paradoxe",
      "+15 Mult si le niveau de la combinaison jouée est 1.",
      (ctx, score) ->
          ctx.state().getLevel(ctx.combo()) == 1
              ? score.withMultiplier(score.multiplier() + 15)
              : score),
  JOKER_099(
      "099",
      "Banqueroute",
      "+100 Jetons si votre score actuel est à 0.",
      (ctx, score) ->
          ctx.state().getCurrentScore() == 0 ? score.withChips(score.chips() + 100) : score),

  JOKER_100(
      "100",
      "L'Apprenti",
      "+1 Mult pour chaque Blind validé.",
      (ctx, score) ->
          score.withMultiplier(score.multiplier() + ctx.state().getCurrentBlindIndex())),
  JOKER_101(
      "101",
      "Le Voyageur",
      "+5 Jetons pour chaque Blind validé.",
      (ctx, score) -> score.withChips(score.chips() + (ctx.state().getCurrentBlindIndex() * 5L))),
  JOKER_102(
      "102",
      "Le Vétéran",
      "+10 Jetons pour chaque Blind validé au-delà du 10ème.",
      (ctx, score) ->
          ctx.state().getCurrentBlindIndex() > 10
              ? score.withChips(score.chips() + ((ctx.state().getCurrentBlindIndex() - 10) * 10L))
              : score),
  JOKER_103(
      "103",
      "Bottes de 7 Lieues",
      "+7 Mult si Index Blind > 7.",
      (ctx, score) ->
          ctx.state().getCurrentBlindIndex() > 7
              ? score.withMultiplier(score.multiplier() + 7)
              : score),
  JOKER_104(
      "104",
      "Marathonien",
      "+15 Jetons si Index Blind > 15.",
      (ctx, score) ->
          ctx.state().getCurrentBlindIndex() > 15 ? score.withChips(score.chips() + 15) : score),
  JOKER_105(
      "105",
      "L'Horloge",
      "+2 Jetons par carte restante dans le Deck.",
      (ctx, score) -> score.withChips(score.chips() + (ctx.state().getDeckSize() * 2L))),
  JOKER_106(
      "106",
      "Sablier",
      "+5 Mult si le Deck fait exactement 52 cartes.",
      (ctx, score) ->
          ctx.state().getDeckSize() == 52 ? score.withMultiplier(score.multiplier() + 5) : score),
  JOKER_107(
      "107",
      "Machine à Sous",
      "+7 Mult si votre score se termine par 7.",
      (ctx, score) ->
          ctx.state().getCurrentScore() % 10 == 7
              ? score.withMultiplier(score.multiplier() + 7)
              : score),
  JOKER_108(
      "108",
      "Jackpot",
      "+50 Jetons si votre score se termine par 0.",
      (ctx, score) ->
          ctx.state().getCurrentScore() % 10 == 0 ? score.withChips(score.chips() + 50) : score),
  JOKER_109(
      "109",
      "Ascension",
      "+1 Mult pour chaque 5 niveaux cumulés de votre main.",
      (ctx, score) ->
          score.withMultiplier(score.multiplier() + (ctx.state().getLevel(ctx.combo()) / 5))),
  JOKER_110(
      "110",
      "Escalade",
      "+10 Jetons pour chaque 2 niveaux cumulés.",
      (ctx, score) ->
          score.withChips(score.chips() + (ctx.state().getLevel(ctx.combo()) / 2 * 10L))),
  JOKER_111(
      "111",
      "Miroir Magique",
      "Le Multiplicateur copie les Jetons s'ils sont < 20.",
      (ctx, score) -> score.chips() < 20 ? score.withMultiplier(score.chips()) : score),
  JOKER_112(
      "112",
      "Lentille",
      "Ajoute 10% des Jetons au Multiplicateur.",
      (ctx, score) -> score.withMultiplier(score.multiplier() + (score.chips() / 10))),
  JOKER_113(
      "113",
      "Prisme",
      "Ajoute 10% du Multiplicateur aux Jetons.",
      (ctx, score) -> score.withChips(score.chips() + (score.multiplier() / 10))),
  JOKER_114(
      "114",
      "Échangeur",
      "Inverse les Jetons et le Multiplicateur.",
      (ctx, score) -> new ScoreResult(score.multiplier(), score.chips())),
  JOKER_115(
      "115",
      "Balance",
      "Moyenne les Jetons et le Multiplicateur.",
      (ctx, score) -> {
        long avg = (score.chips() + score.multiplier()) / 2;
        return new ScoreResult(avg, avg);
      }),
  JOKER_116(
      "116",
      "Pesanteur",
      "+5 Mult si les Jetons > 100.",
      (ctx, score) -> score.chips() > 100 ? score.withMultiplier(score.multiplier() + 5) : score),
  JOKER_117(
      "117",
      "Apesanteur",
      "+50 Jetons si le Mult < 5.",
      (ctx, score) -> score.multiplier() < 5 ? score.withChips(score.chips() + 50) : score),
  JOKER_118(
      "118",
      "Croissance",
      "+1 Mult par carte rouge jouée.",
      (ctx, score) ->
          score.withMultiplier(
              score.multiplier()
                  + ctx.allSelectedCards().stream()
                      .filter(c -> c.suit() == Suit.SUIT_HEART || c.suit() == Suit.SUIT_TILE)
                      .count())),
  JOKER_119(
      "119",
      "Racines",
      "+5 Jetons par carte noire jouée.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.suit() == Suit.SUIT_CLOVER || c.suit() == Suit.SUIT_SPIKE)
                          .count()
                      * 5))),
  JOKER_120(
      "120",
      "Floraison",
      "+2 Mult si vous jouez exactement 4 cartes.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 4
              ? score.withMultiplier(score.multiplier() + 2)
              : score),
  JOKER_121(
      "121",
      "Récolte",
      "+15 Jetons si vous jouez exactement 3 cartes.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 3 ? score.withChips(score.chips() + 15) : score),
  JOKER_122(
      "122",
      "Abondance",
      "+25 Jetons si vous jouez exactement 2 cartes.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 2 ? score.withChips(score.chips() + 25) : score),
  JOKER_123(
      "123",
      "Famine",
      "+10 Mult si vous jouez 5 cartes dont aucun As.",
      (ctx, score) ->
          (ctx.allSelectedCards().size() == 5
                  && ctx.allSelectedCards().stream().noneMatch(c -> c.rank().getValue() == 14))
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_124(
      "124",
      "Sécheresse",
      "+20 Jetons s'il n'y a pas de Cœur dans la main.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().noneMatch(c -> c.suit() == Suit.SUIT_HEART)
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_125(
      "125",
      "Inondation",
      "+20 Jetons s'il n'y a pas de Pique dans la main.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().noneMatch(c -> c.suit() == Suit.SUIT_SPIKE)
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_126(
      "126",
      "Incendie",
      "+20 Jetons s'il n'y a pas de Trèfle dans la main.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().noneMatch(c -> c.suit() == Suit.SUIT_CLOVER)
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_127(
      "127",
      "Tornade",
      "+20 Jetons s'il n'y a pas de Carreau dans la main.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().noneMatch(c -> c.suit() == Suit.SUIT_TILE)
              ? score.withChips(score.chips() + 20)
              : score),
  JOKER_128(
      "128",
      "Survivant",
      "+10 Mult si vous n'avez plus de Mains ni Défausses.",
      (ctx, score) ->
          (ctx.state().getHandsLeft() == 1 && ctx.state().getDiscardsLeft() == 0)
              ? score.withMultiplier(score.multiplier() + 10)
              : score),
  JOKER_129(
      "129",
      "Héros",
      "+50 Jetons face à The House.",
      (ctx, score) ->
          ctx.constraint() == BlindConstraint.THE_HOUSE
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_130(
      "130",
      "Légende Naissante",
      "+5 Mult si le score du Blind > 5000.",
      (ctx, score) ->
          ctx.state().getCurrentBlind().score() > 5000
              ? score.withMultiplier(score.multiplier() + 5)
              : score),

  JOKER_131(
      "131",
      "Le Clone",
      "x1.5 Mult si vous possédez exactement 5 Jokers.",
      (ctx, score) ->
          ctx.state().getActiveJokers().size() == 5
              ? score.withMultiplier((long) (score.multiplier() * 1.5))
              : score),
  JOKER_132(
      "132",
      "Effet Miroir",
      "+50 Jetons si vous possédez exactement 5 Jokers.",
      (ctx, score) ->
          ctx.state().getActiveJokers().size() == 5 ? score.withChips(score.chips() + 50) : score),
  JOKER_133(
      "133",
      "Joker Sombre",
      "x1.5 Mult si le score requis par le Blind est > 10 000.",
      (ctx, score) ->
          ctx.state().getCurrentBlind().score() > 10000
              ? score.withMultiplier((long) (score.multiplier() * 1.5))
              : score),
  JOKER_134(
      "134",
      "Roulette Russe",
      "1 chance sur 4 d'ajouter +50 Mult.",
      (ctx, score) ->
          ThreadLocalRandom.current().nextInt(4) == 0
              ? score.withMultiplier(score.multiplier() + 50)
              : score),
  JOKER_135(
      "135",
      "Feu de Joie",
      "+50 Jetons si la main jouée ne contient que des Trèfles.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream().allMatch(c -> c.suit() == Suit.SUIT_CLOVER))
              ? score.withChips(score.chips() + 50)
              : score),
  JOKER_136(
      "136",
      "Excalibur",
      "x2 Mult si la combinaison est une Quinte Flush.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_STRAIGHT_COLOR
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_137(
      "137",
      "Le Graal",
      "x3 Mult si la combinaison est un Carré d'As.",
      (ctx, score) ->
          (ctx.combo() == Combination.COMBINATION_SQUARE
                  && ctx.allSelectedCards().stream().anyMatch(c -> c.rank().getValue() == 14))
              ? score.withMultiplier(score.multiplier() * 3)
              : score),
  JOKER_138(
      "138",
      "Midas",
      "Transforme les Jetons en Multiplicateur (si Jetons < 100).",
      (ctx, score) ->
          score.chips() < 100 ? new ScoreResult(0, score.multiplier() + score.chips()) : score),
  JOKER_139(
      "139",
      "Trou Noir",
      "Met les Jetons à 0 mais donne x5 Mult.",
      (ctx, score) -> new ScoreResult(0, score.multiplier() * 5)),
  JOKER_140(
      "140",
      "Supernova Élite",
      "x2 Mult si le niveau du Blind est > 20.",
      (ctx, score) ->
          ctx.state().getCurrentBlindIndex() > 20
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_141(
      "141",
      "Dieu du Poker",
      "+100 Jetons et +10 Mult pour chaque As.",
      (ctx, score) -> {
        long asCount =
            ctx.allSelectedCards().stream().filter(c -> c.rank().getValue() == 14).count();
        return new ScoreResult(
            score.chips() + (asCount * 100), score.multiplier() + (asCount * 10));
      }),
  JOKER_142(
      "142",
      "L'Olympe",
      "+50 Jetons par figure (V, D, R) jouée.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() >= 11 && c.rank().getValue() <= 13)
                          .count()
                      * 50))),
  JOKER_143(
      "143",
      "Le Titan",
      "x2 Mult si les Jetons de base > 50.",
      (ctx, score) -> score.chips() > 50 ? score.withMultiplier(score.multiplier() * 2) : score),
  JOKER_144(
      "144",
      "L'Oracle",
      "+20 Mult si aucune figure n'est jouée.",
      (ctx, score) ->
          ctx.allSelectedCards().stream()
                  .noneMatch(c -> c.rank().getValue() >= 11 && c.rank().getValue() <= 13)
              ? score.withMultiplier(score.multiplier() + 20)
              : score),
  JOKER_145(
      "145",
      "Le Fou",
      "+10 Mult, ou +20 si vous jouez un 2.",
      (ctx, score) ->
          ctx.allSelectedCards().stream().anyMatch(c -> c.rank().getValue() == 2)
              ? score.withMultiplier(score.multiplier() + 20)
              : score.withMultiplier(score.multiplier() + 10)),
  JOKER_146(
      "146",
      "L'Empereur",
      "+30 Jetons par Roi ou Dame joués.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() == 12 || c.rank().getValue() == 13)
                          .count()
                      * 30))),
  JOKER_147(
      "147",
      "Le Chariot",
      "x1.5 Mult si la combinaison est une Suite.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_STRAIGHT
              ? score.withMultiplier((long) (score.multiplier() * 1.5))
              : score),
  JOKER_148(
      "148",
      "La Justice",
      "Rend les Jetons égaux au Multiplicateur s'ils sont inférieurs.",
      (ctx, score) ->
          score.chips() < score.multiplier() ? score.withChips(score.multiplier()) : score),
  JOKER_149(
      "149",
      "La Tempête",
      "+100 Jetons si la combinaison est une Couleur.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_COLOR
              ? score.withChips(score.chips() + 100)
              : score),
  JOKER_150(
      "150",
      "Le Diable",
      "x2 Mult mais divise les Jetons par 2.",
      (ctx, score) -> new ScoreResult(score.chips() / 2, score.multiplier() * 2)),
  JOKER_151(
      "151",
      "Le Pendu",
      "+50 Mult si vous jouez exactement 1 carte.",
      (ctx, score) ->
          ctx.allSelectedCards().size() == 1
              ? score.withMultiplier(score.multiplier() + 50)
              : score),
  JOKER_152(
      "152",
      "La Mort",
      "+100 Mult si c'est votre dernière Main et dernière Défausse.",
      (ctx, score) ->
          (ctx.state().getHandsLeft() == 1 && ctx.state().getDiscardsLeft() == 0)
              ? score.withMultiplier(score.multiplier() + 100)
              : score),
  JOKER_153(
      "153",
      "L'Étoile",
      "+40 Jetons pour chaque carte impaire.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() % 2 != 0)
                          .count()
                      * 40))),
  JOKER_154(
      "154",
      "La Lune",
      "+40 Jetons pour chaque carte paire.",
      (ctx, score) ->
          score.withChips(
              score.chips()
                  + (ctx.allSelectedCards().stream()
                          .filter(c -> c.rank().getValue() % 2 == 0)
                          .count()
                      * 40))),
  JOKER_155(
      "155",
      "Le Soleil",
      "x2 Mult si aucune carte noire n'est jouée.",
      (ctx, score) ->
          (!ctx.allSelectedCards().isEmpty()
                  && ctx.allSelectedCards().stream()
                      .noneMatch(c -> c.suit() == Suit.SUIT_CLOVER || c.suit() == Suit.SUIT_SPIKE))
              ? score.withMultiplier(score.multiplier() * 2)
              : score),
  JOKER_156(
      "156",
      "Le Jugement",
      "x3 Mult si vous battez un Boss Blind avec une Carte Haute.",
      (ctx, score) ->
          (ctx.constraint() != BlindConstraint.NONE
                  && ctx.combo() == Combination.COMBINATION_HIGH_MAP)
              ? score.withMultiplier(score.multiplier() * 3)
              : score),
  JOKER_157(
      "157",
      "Le Monde",
      "+200 Jetons si la combinaison est un Full House.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_FULL
              ? score.withChips(score.chips() + 200)
              : score),
  JOKER_158(
      "158",
      "L'Univers",
      "x5 Mult si vous jouez une Quinte Flush.",
      (ctx, score) ->
          ctx.combo() == Combination.COMBINATION_STRAIGHT_COLOR
              ? score.withMultiplier(score.multiplier() * 5)
              : score);

  private final String id;
  private final String jokerName;
  private final String description;
  private final JokerEffect effect;

  JokerType(String id, String jokerName, String description, JokerEffect effect) {
    this.id = Objects.requireNonNull(id);
    this.jokerName = Objects.requireNonNull(jokerName);
    this.description = Objects.requireNonNull(description);
    this.effect = Objects.requireNonNull(effect);
  }

  public ScoreResult apply(JokerContext ctx, ScoreResult currentScore) {
    return this.effect.apply(ctx, currentScore);
  }

  public String getJokerName() {
    return jokerName;
  }

  public String getDescription() {
    return description;
  }

  public String getImagePath() {
    return "/jokers/joker_" + this.id + ".png";
  }
}
