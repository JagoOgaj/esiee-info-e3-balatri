package esiee.info.e3.config;

import esiee.info.e3.domain.Blind;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
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
        List<Blind> blinds = new ArrayList<>();
        long baseScore = 100;
        String[] bossNames = {"Le Crochet", "Les Menottes", "La Maison", "Le Mur", "La Roue", "Le Bras", "L'Oeil", "La Bouche", "La Dent", "La Tête"};
        for (int i = 0; i < 10; i++) {
            blinds.add(new Blind(i * 3 + 1, "Petite Blinde (Niv " + (i+1) + ")",  baseScore));
            blinds.add(new Blind(i * 3 + 2, "Grosse Blinde (Niv " + (i+1) + ")", (long) (baseScore * 1.5)));
            String bossName = bossNames[i % bossNames.length];
            blinds.add(new Blind(i * 3 + 3, bossName + " (Niv " + (i+1) + ")", (long) (baseScore * 2)));
            baseScore = (long)(baseScore * 2.5);
        }
        blinds.add(new Blind(31, "Le Cœur du Jeu (Boss Final)", (baseScore * 3)));
        return blinds;
    }
}
