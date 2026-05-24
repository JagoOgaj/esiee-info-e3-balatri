package esiee.info.e3.model;

import esiee.info.e3.domain.enums.JokerRarity;
import esiee.info.e3.domain.enums.JokerType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class JokerRewardService {

    private final Random random;

    public JokerRewardService(Random random) {
        this.random = random;
    }

    public JokerType rollJokerReward(int blindIndex, List<JokerType> ownedJokers) {
        if (!shouldSpawnJoker(blindIndex)) {
            return null;
        }
        JokerRarity targetRarity = determineRarity(blindIndex);
        return selectRandomJoker(targetRarity, ownedJokers);
    }

    private boolean shouldSpawnJoker(int blindIndex) {
        boolean isBoss = (blindIndex + 1) % 3 == 0;
        double spawnChance = isBoss ? 1.0 : 0.70 + Math.min(0.30, blindIndex * 0.05);
        return random.nextDouble() <= spawnChance;
    }

    private JokerRarity determineRarity(int blindIndex) {
        double tierRoll = random.nextDouble() * 100;

        if (blindIndex < 5) {
            return getRarityFromThresholds(tierRoll, 85, 100);
        } else if (blindIndex < 12) {
            return getRarityFromThresholds(tierRoll, 60, 95);
        } else {
            return getRarityFromThresholds(tierRoll, 40, 85);
        }
    }

    private JokerRarity getRarityFromThresholds(double roll, double classicMax, double mediumMax) {
        if (roll < classicMax) return JokerRarity.CLASSIQUE;
        if (roll < mediumMax) return JokerRarity.MOYEN;
        return JokerRarity.LEGENDAIRE;
    }

    private JokerType selectRandomJoker(JokerRarity targetRarity, List<JokerType> ownedJokers) {
        List<JokerType> matchingJokers = Arrays.stream(JokerType.values())
                .filter(j -> getJokerRarity(j) == targetRarity)
                .filter(j -> !ownedJokers.contains(j))
                .toList();

        if (matchingJokers.isEmpty()) {
            matchingJokers = Arrays.stream(JokerType.values())
                    .filter(j -> !ownedJokers.contains(j))
                    .toList();
        }

        if (matchingJokers.isEmpty()) {
            return null;
        }

        return matchingJokers.get(random.nextInt(matchingJokers.size()));
    }

    public JokerRarity getJokerRarity(JokerType joker) {
        int ord = joker.ordinal();
        if (ord % 7 == 0) return JokerRarity.LEGENDAIRE;
        if (ord % 3 == 0) return JokerRarity.MOYEN;
        return JokerRarity.CLASSIQUE;
    }
}