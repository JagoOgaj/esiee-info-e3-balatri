package esiee.info.e3.model.interfaces;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Combination;

import java.util.List;

public interface IHandEvaluator {
    Combination evaluate(List<Card> selectedCards);
}
