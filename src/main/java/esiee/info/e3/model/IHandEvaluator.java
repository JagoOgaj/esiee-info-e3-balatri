package esiee.info.e3.model;

import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Combination;
import java.util.List;

public sealed interface IHandEvaluator permits HandEvaluator{
  Combination evaluate(List<Card> selectedCards);
}
