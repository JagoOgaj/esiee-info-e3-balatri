package esiee.info.e3.model.jokerModel;

import esiee.info.e3.domain.enums.JokerRarity;
import esiee.info.e3.domain.enums.JokerType;
import java.util.List;

public sealed interface IJokerRewardService permits JokerRewardService {
  JokerType rollJokerReward(int blindIndex, List<JokerType> ownedJokers);

  JokerRarity getJokerRarity(JokerType joker);
}
