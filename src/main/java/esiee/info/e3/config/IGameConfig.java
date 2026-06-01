package esiee.info.e3.config;

import esiee.info.e3.domain.Blind;
import java.util.List;


import java.awt.*;

public sealed interface IGameConfig permits GameConfig {
    Font getPixelFont(String path, float size);
    List<Blind> getAllBlinds();
}
