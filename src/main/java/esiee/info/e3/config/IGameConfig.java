package esiee.info.e3.config;

import esiee.info.e3.domain.Blind;
import java.awt.*;
import java.util.List;

public sealed interface IGameConfig permits GameConfig {
  Font getPixelFont(String path, float size);

  List<Blind> getAllBlinds();
}
