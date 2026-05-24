package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.BlindConstraint;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.model.GameState;

import java.util.List;


public record JokerContext(
        Combination combo,
        List<Card> activeCards,
        List<Card> allSelectedCards,
        int level,
        BlindConstraint constraint,
        GameState state
) {}