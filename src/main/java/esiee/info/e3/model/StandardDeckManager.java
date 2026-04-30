package esiee.info.e3.model;

import esiee.info.e3.domain.*;
import java.util.*;

public class StandardDeckManager implements IDeckManager {
    private final List<Card> drawPile = new ArrayList<>();
    private final List<Card> discardPile = new ArrayList<>();

    public StandardDeckManager() {
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                drawPile.add(new Card(r, s));
            }
        }
        shuffle();
    }

    @Override
    public void shuffle() {
        Collections.shuffle(drawPile);
    }

    @Override
    public List<Card> draw(int count) {
        List<Card> drawn = new ArrayList<>();
        for (var i = 0; i < count; i++) {
            if (drawPile.isEmpty()) {
                drawPile.addAll(discardPile);
                discardPile.clear();
                shuffle();
            }
            if (!drawPile.isEmpty()) drawn.add(drawPile.removeFirst());
        }
        return drawn;
    }

    @Override
    public void discard(List<Card> cards) {
        discardPile.addAll(cards);
    }

    @Override
    public int getRemainingCount() { return drawPile.size(); }
}