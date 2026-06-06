package esiee.info.e3.config;

import esiee.info.e3.config.enums.BlindEnum;
import esiee.info.e3.domain.Blind;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameConfig implements IGameConfig {

  @Override
  public Font getPixelFont(String path, float size) {
    return loadPixelFont(path, size);
  }

  @Override
  public List<Blind> getAllBlinds() {
    return allBlinds();
  }

  private Font loadPixelFont(String path, float size) {
    Objects.requireNonNull(path);
    if (size < -1) {
      throw new IllegalArgumentException();
    }

    try (InputStream is = GameConfig.class.getResourceAsStream(path)) {
      if (is == null) {
        return new Font("SansSerif", Font.BOLD, (int) size);
      }
      var font = Font.createFont(Font.TRUETYPE_FONT, is);
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
      return font.deriveFont(size);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return new Font("SansSerif", Font.BOLD, (int) size);
    }
  }

  private List<Blind> allBlinds() {
    List<Blind> blinds = new ArrayList<>();
    long baseScore = 100;
    String[] bossNames = {
      BlindEnum.THE_HOOK.getText(),
      BlindEnum.THE_HANDCUFFS.getText(),
      BlindEnum.THE_HOUSE.getText(),
      BlindEnum.THE_ARM.getText(),
      BlindEnum.THE_WHEEL.getText(),
      BlindEnum.THE_EYE.getText(),
      BlindEnum.THE_MOUTH.getText(),
      BlindEnum.THE_TOOTH.getText(),
      BlindEnum.THE_HEAD.getText(),
    };

    for (var i = 0; i < 10; i++) {
      blinds.add(
          new Blind(
              i * 3 + 1, BlindEnum.LITTLE_BLIND.getText() + " " + this.formatLevel(i), baseScore));
      blinds.add(
          new Blind(
              i * 3 + 2,
              BlindEnum.BIG_BLIND.getText() + " " + this.formatLevel(i),
              (long) (baseScore * 1.5)));

      var bossName = bossNames[i % bossNames.length];
      blinds.add(
          new Blind(i * 3 + 3, bossName + " " + this.formatLevel(i), (long) (baseScore * 2)));

      baseScore = (long) (baseScore * 2.5);
    }

    blinds.add(new Blind(31, BlindEnum.THE_HEARTH_OF_THE_GAME.getText(), (baseScore * 3)));
    return List.copyOf(blinds);
  }

  private String formatLevel(int i) {
    return "(Niv " + (i + 1) + " )";
  }
}
