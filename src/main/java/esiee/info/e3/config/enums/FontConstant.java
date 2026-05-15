package esiee.info.e3.config.enums;

public enum FontConstant {
  FONT_BOLD_PIXEL("bold-pixel.ttf");

  private final String fileName;

  private FontConstant(String fileName) {
    this.fileName = fileName;
  }

  public String getPath() {
    return "/fonts/" + this.fileName;
  }
}
