package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.Combination;

import java.util.List;

public interface IHandEvaluator {
    Combination evaluate(List<Card> selectedCards);
}
