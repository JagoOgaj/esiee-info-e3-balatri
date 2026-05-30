package esiee.info.e3.domain;

import esiee.info.e3.model.GameState;

import java.util.List;

public record GameSnapshot(
        GameState state,
        List<Card> hand,
        List<Card> selectedCards,
        EvaluatedHand evaluation
) {}
