package esiee.info.e3.domain;

import esiee.info.e3.domain.enums.JokerType;
import java.util.Objects;

public record ShopItem(Object item, int price, JokerType replacedJoker) {

  public ShopItem {
    Objects.requireNonNull(item);
    if (price < 0) {
      throw new IllegalArgumentException();
    }
  }

  public ShopItem(Object item, int price) {
    this(item, price, null);
  }
}
