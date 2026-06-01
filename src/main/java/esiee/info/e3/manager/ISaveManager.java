package esiee.info.e3.manager;

import esiee.info.e3.domain.SaveSlot;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.model.GameSateEnum;
import esiee.info.e3.model.IGameModel;

import java.util.List;


public sealed interface ISaveManager permits SaveManager {
    void saveGame(IGameModel model, GameSateEnum status);
    List<SaveSlot> loadAllSaves();
    void loadGame(String saveId, GameModel model);
    String getPathFileSave();
}