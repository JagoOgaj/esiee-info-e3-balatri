package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.JokerType;
import java.util.Objects;

public record ShopItem(JokerType item, int price, JokerType replacedJoker) {

  public ShopItem {
    Objects.requireNonNull(item);
    if (price < 0) {
      throw new IllegalArgumentException();
    }
  }

  public ShopItem(JokerType item, int price) {
    this(item, price, null);
  }
}
