package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.JokerContext;
import esiee.info.e3.domain.ScoreResult;
import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.model.interfaces.IScoreCalculator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreCalculator implements IScoreCalculator {

    @Override
    public long calculateScore(Combination combo, List<Card> cards, int level,
                               BlindConstraint constraint, GameState state,
                               List<JokerType> activeJokers) {

        long chips = this.computeBaseChips(combo, level);
        long multiplier = this.computeMultiplier(combo, level);
        var activeCards = this.getActiveCards(combo, cards);

        if (!cards.isEmpty()) {
            for (var c : activeCards) {
                if (constraint == null || !constraint.isCardDisabled(c)) {
                    chips += c.rank().getValue();
                }
            }
        }

        if (state == null || activeJokers == null || activeJokers.isEmpty()) {
            return Math.max(0, chips) * Math.max(0, multiplier);
        }

        ScoreResult scoreResult = new ScoreResult(chips, multiplier);
        JokerContext context = new JokerContext(combo, activeCards, cards, level, constraint, state);

        for (JokerType joker : activeJokers) {
            scoreResult = joker.apply(context, scoreResult);
        }

        return Math.max(0, scoreResult.chips()) * Math.max(0, scoreResult.multiplier());
    }

    @Override
    public int computeBaseChips(Combination combo, int level) {
        var planet = Planet.fromCombination(combo);
        var bonus = (planet != null) ? planet.getChipsBonus() : 10;
        return combo.getBasicChips() + (level - 1) * bonus;
    }

    @Override
    public int computeMultiplier(Combination combo, int level) {
        var planet = Planet.fromCombination(combo);
        var bonus = (planet != null) ? planet.getMultiplierBonus() : 2;
        return combo.getMultiplier() + (level - 1) * bonus;
    }

    private List<Card> getActiveCards(Combination combo, List<Card> cards) {
        if (cards.isEmpty()) return List.of();

        var rankCounts = cards.stream()
                .collect(Collectors.groupingBy(Card::rank, Collectors.counting()));

        return switch (combo) {
            case COMBINATION_SQUARE -> cards.stream().filter(c -> rankCounts.getOrDefault(c.rank(), 0L) >= 4L).toList();
            case COMBINATION_THREE_OF_KIND -> cards.stream().filter(c -> rankCounts.getOrDefault(c.rank(), 0L) >= 3L).toList();
            case COMBINATION_DOUBLE_PAIR, COMBINATION_PAIR -> cards.stream().filter(c -> rankCounts.getOrDefault(c.rank(), 0L) >= 2L).toList();
            case COMBINATION_HIGH_MAP -> {
                var highestCard = cards.stream()
                        .max(Comparator.comparingInt(c -> c.rank().getValue()))
                        .orElse(null);
                yield highestCard != null ? List.of(highestCard) : List.of();
            }
            default -> cards;
        };
    }
}