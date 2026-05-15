package esiee.info.e3.config;

import esiee.info.e3.domain.Blind;

import java.awt.*;
import java.io.InputStream;
import java.util.List;

public class GameConfig {

  public static Font loadPixelFont(String path, float size) {
    try (InputStream is = GameConfig.class.getResourceAsStream(path)) {
      if (is == null) {
        return new Font("SansSerif", Font.BOLD, (int) size);
      }
      Font font = Font.createFont(Font.TRUETYPE_FONT, is);
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
      return font.deriveFont(size);
    } catch (Exception e) {
      System.err.println("ERREUR lors du chargement de la police : " + e.getMessage());
      return new Font("SansSerif", Font.BOLD, (int) size);
    }
  }

  public static List<Blind> allBlinds() {
    return List.of(
        new Blind(1, "Petite Blinde", 300),
        new Blind(2, "Grosse Blinde", 450),
        new Blind(3, "Le Crochet", 600));
  }
}
