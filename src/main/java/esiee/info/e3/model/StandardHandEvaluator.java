package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.Combination;
import esiee.info.e3.domain.Rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StandardHandEvaluator implements IHandEvaluator{

    @Override
    public Combination evaluate(List<Card> selectedCards) {
        if (selectedCards == null || selectedCards.isEmpty()) {
            throw new IllegalArgumentException("La main est vide");
        }

        List<Card> sortedCards = new ArrayList<>(selectedCards);
        sortedCards.sort((c1, c2) -> Integer.compare(c2.rank().getValue(), c1.rank().getValue()));

        boolean isFlush = checkFlush(sortedCards);
        boolean isStraight = checkStraight(sortedCards);

        Map<Rank, Long> rankCounts = sortedCards.stream()
                .collect(Collectors.groupingBy(Card::rank, Collectors.counting()));

        Collection<Long> counts = rankCounts.values();

        if (isFlush && isStraight) return Combination.COMBINATION_STRAIGHT_COLOR;
        if (counts.contains(4L)) return Combination.COMBINATION_SQUARE;
        if (counts.contains(3L) && counts.contains(2L)) return Combination.COMBINATION_FULL;
        if (isFlush) return Combination.COMBINATION_COLOR;
        if (isStraight) return Combination.COMBINATION_STRAIGHT;
        if (counts.contains(3L)) return Combination.COMBINATION_THREE_OF_KIND;

        long pairCount = counts.stream().filter(count -> count == 2L).count();
        if (pairCount == 2) return Combination.COMBINATION_DOUBLE_PAIR;
        if (pairCount == 1) return Combination.COMBINATION_PAIR;

        return Combination.COMBINATION_HIGH_MAP;
    }

    private boolean checkFlush(List<Card> cards) {
        if (cards.size() < 5) return false;
        return cards.stream().map(Card::suit).distinct().count() == 1;
    }

    private boolean checkStraight(List<Card> cards) {
        if (cards.size() < 5) return false;

        boolean isStandardStraight = true;
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).rank().getValue() != cards.get(i + 1).rank().getValue() + 1) {
                isStandardStraight = false;
                break;
            }
        }

        if (isStandardStraight) return true;

        return cards.get(0).rank().getValue() == 14 &&
                cards.get(1).rank().getValue() == 5 &&
                cards.get(2).rank().getValue() == 4 &&
                cards.get(3).rank().getValue() == 3 &&
                cards.get(4).rank().getValue() == 2;
    }
}
