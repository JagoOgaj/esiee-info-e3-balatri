package esiee.info.e3.test;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import esiee.info.e3.model.StandardHandEvaluator;

import java.util.List;

public class HandEvaluatorTest {

    static void main() {
        StandardHandEvaluator evaluator = new StandardHandEvaluator();

        List<Card> straightFlush = List.of(
                new Card(Rank.RANK_ROI, Suit.SUIT_HEART),
                new Card(Rank.RANK_DAME, Suit.SUIT_HEART),
                new Card(Rank.RANK_VALET, Suit.SUIT_HEART),
                new Card(Rank.RANK_10, Suit.SUIT_HEART),
                new Card(Rank.RANK_9, Suit.SUIT_HEART)
        );
        testHand("Quinte Flush", straightFlush, Combination.COMBINATION_STRAIGHT_COLOR, evaluator);

        List<Card> lowStraight = List.of(
                new Card(Rank.RANK_AS, Suit.SUIT_SPIKE),
                new Card(Rank.RANK_5, Suit.SUIT_HEART),
                new Card(Rank.RANK_4, Suit.SUIT_TILE),
                new Card(Rank.RANK_3, Suit.SUIT_CLOVER),
                new Card(Rank.RANK_2, Suit.SUIT_HEART)
        );
        testHand("Suite (A-2-3-4-5)", lowStraight, Combination.COMBINATION_STRAIGHT, evaluator);

        List<Card> fullHouse = List.of(
                new Card(Rank.RANK_ROI, Suit.SUIT_SPIKE),
                new Card(Rank.RANK_ROI, Suit.SUIT_HEART),
                new Card(Rank.RANK_ROI, Suit.SUIT_TILE),
                new Card(Rank.RANK_AS, Suit.SUIT_CLOVER),
                new Card(Rank.RANK_AS, Suit.SUIT_HEART)
        );
        testHand("Full", fullHouse, Combination.COMBINATION_FULL, evaluator);

        List<Card> twoPair = List.of(
                new Card(Rank.RANK_10, Suit.SUIT_SPIKE),
                new Card(Rank.RANK_10, Suit.SUIT_HEART),
                new Card(Rank.RANK_5, Suit.SUIT_TILE),
                new Card(Rank.RANK_5, Suit.SUIT_CLOVER),
                new Card(Rank.RANK_2, Suit.SUIT_HEART)
        );
        testHand("Double Paire", twoPair, Combination.COMBINATION_DOUBLE_PAIR, evaluator);

        List<Card> highCard = List.of(
                new Card(Rank.RANK_AS, Suit.SUIT_SPIKE),
                new Card(Rank.RANK_10, Suit.SUIT_HEART),
                new Card(Rank.RANK_7, Suit.SUIT_TILE),
                new Card(Rank.RANK_4, Suit.SUIT_CLOVER),
                new Card(Rank.RANK_2, Suit.SUIT_HEART)
        );
        testHand("Carte Haute", highCard, Combination.COMBINATION_HIGH_MAP, evaluator);
    }

    private static void testHand(String name, List<Card> hand, Combination expected, StandardHandEvaluator evaluator) {
        Combination result = evaluator.evaluate(hand);
        if (result == expected) {
            System.out.println("SUCCÈS : " + name + " a bien été détectée.");
        } else {
            System.out.println("ÉCHEC : " + name + " - Attendu : " + expected.getLabel() + ", Obtenu : " + result.getLabel());
        }
    }
}