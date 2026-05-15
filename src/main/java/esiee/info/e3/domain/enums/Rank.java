package esiee.info.e3.domain.enums;

public enum Rank {
  RANK_2("2", 2),
  RANK_3("3", 3),
  RANK_4("4", 4),
  RANK_5("5", 5),
  RANK_6("6", 6),
  RANK_7("7", 7),
  RANK_8("8", 8),
  RANK_9("9", 9),
  RANK_10("10", 10),
  RANK_VALET("Valet", 11),
  RANK_DAME("Dame", 12),
  RANK_ROI("Roi", 13),
  RANK_AS("As", 14);

  private final String label;
  private final int value;

  private Rank(String label, int value) {
    this.label = label;
    this.value = value;
  }

  public String getLabel() {
    return this.label;
  }

  public int getValue() {
    return this.value;
  }
}
