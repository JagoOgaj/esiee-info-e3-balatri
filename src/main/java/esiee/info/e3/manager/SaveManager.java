package esiee.info.e3.manager;

import esiee.info.e3.config.IGameConfig;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.SaveSlot;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.model.GameSateEnum;
import esiee.info.e3.model.IGameModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SaveManager(IGameConfig gameConfig) implements ISaveManager {

  private static final String SAVES_DIR = "saves/";
  private static final String SAVE_PATH = SAVES_DIR + "highscore.txt";

  public SaveManager {
    Objects.requireNonNull(gameConfig);
  }

  @Override
  public void saveGame(IGameModel model, GameSateEnum status) {
    Objects.requireNonNull(model);
    Objects.requireNonNull(status);

    try {
      Files.createDirectories(Paths.get(SAVES_DIR));
      var state = model.getState();
      String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

      var id = state.getCurrentSaveId();
      if (id == null || id.isEmpty()) {
        id = "save_" + System.currentTimeMillis();
        state.setCurrentSaveId(id);
      }

      var json = new StringBuilder("{\n");
      json.append("  \"id\": \"").append(id).append("\",\n");
      json.append("  \"date\": \"").append(date).append("\",\n");
      json.append("  \"status\": \"").append(status.getLabel()).append("\",\n");
      json.append("  \"score\": ").append(state.getCurrentScore()).append(",\n");
      json.append("  \"handsLeft\": ").append(state.getHandsLeft()).append(",\n");
      json.append("  \"discardsLeft\": ").append(state.getDiscardsLeft()).append(",\n");
      json.append("  \"deckSize\": ").append(state.getDeckSize()).append(",\n");
      json.append("  \"currentBlindIndex\": ").append(state.getCurrentBlindIndex()).append(",\n");

      json.append("  \"infiniteMode\": ").append(state.isInfiniteMode()).append(",\n");
      json.append("  \"loopCount\": ").append(state.getLoopCount()).append(",\n");
      json.append("  \"money\": ").append(state.getMoney()).append(",\n");

      json.append("  \"currentHand\": [\n");
      var hand = model.getHand();
      for (var i = 0; i < hand.size(); i++) {
        var c = hand.get(i);
        json.append("    {\"rank\": \"")
            .append(c.rank().name())
            .append("\", \"suit\": \"")
            .append(c.suit().name())
            .append("\"}");
        if (i < hand.size() - 1) json.append(",");
        json.append("\n");
      }
      json.append("  ],\n");

      json.append("  \"handLevels\": {\n");
      var combos = Combination.values();
      for (var i = 0; i < combos.length; i++) {
        json.append("    \"")
            .append(combos[i].name())
            .append("\": ")
            .append(state.getLevel(combos[i]));
        if (i < combos.length - 1) json.append(",");
        json.append("\n");
      }
      json.append("  },\n");

      json.append("  \"activeJokers\": [\n");
      var jokers = state.getActiveJokers();
      for (var i = 0; i < jokers.size(); i++) {
        json.append("    \"").append(jokers.get(i).name()).append("\"");
        if (i < jokers.size() - 1) json.append(",");
        json.append("\n");
      }
      json.append("  ],\n");

      json.append("  \"wonPlanets\": [\n");
      var planets = new ArrayList<>(state.getWonPlanets());
      for (var i = 0; i < planets.size(); i++) {
        json.append("    \"").append(planets.get(i).name()).append("\"");
        if (i < planets.size() - 1) json.append(",");
        json.append("\n");
      }
      json.append("  ]\n");
      json.append("}\n");
      Files.writeString(Paths.get(SAVES_DIR + id + ".json"), json.toString());

    } catch (IOException e) {
      System.err.println("Erreur lors de l'écriture de la sauvegarde : " + e.getMessage());
    }
  }

  @Override
  public List<SaveSlot> loadAllSaves() {
    List<SaveSlot> list = new ArrayList<>();
    var dir = new File(SAVES_DIR);
    if (!dir.exists() || !dir.isDirectory()) return list;

    var files = dir.listFiles((d, name) -> name.endsWith(".json"));
    if (files == null) return list;

    var allBlinds = this.gameConfig.getAllBlinds();

    for (var f : files) {
      try {
        var content = Files.readString(f.toPath());
        var id = extractString(content, "\"id\": \"(.*?)\"");
        var date = extractString(content, "\"date\": \"(.*?)\"");
        var status = extractString(content, "\"status\": \"(.*?)\"");
        var score = Long.parseLong(extractString(content, "\"score\": (\\d+)"));
        var blindIndex = Integer.parseInt(extractString(content, "\"currentBlindIndex\": (\\d+)"));

        var infiniteMode = false;
        var loopCount = 0;
        try {
          infiniteMode =
              Boolean.parseBoolean(extractString(content, "\"infiniteMode\": (true|false)"));
          loopCount = Integer.parseInt(extractString(content, "\"loopCount\": (\\d+)"));
        } catch (Exception ignored) {
        }

        var blindName = "Inconnu";
        int blindId = 1;
        if (blindIndex >= 0 && blindIndex < allBlinds.size()) {
          blindName = allBlinds.get(blindIndex).name();
          blindId = allBlinds.get(blindIndex).id();
        }

        var canResume = GameSateEnum.PROGRESS.getLabel().equals(status);

        list.add(
            new SaveSlot(
                id, date, blindName, blindId, score, status, canResume, infiniteMode, loopCount));
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }

    list.sort((a, b) -> b.id().compareTo(a.id()));
    return list;
  }

  @Override
  public void loadGame(String saveId, GameModel model) {
    Objects.requireNonNull(saveId, "L'ID de sauvegarde ne peut pas être nul.");
    Objects.requireNonNull(model, "Le modèle de jeu cible ne peut pas être nul.");

    try {
      var content = Files.readString(Paths.get(SAVES_DIR + saveId + ".json"));

      var score = Long.parseLong(extractString(content, "\"score\": (\\d+)"));
      var handsLeft = Integer.parseInt(extractString(content, "\"handsLeft\": (\\d+)"));
      var discardsLeft = Integer.parseInt(extractString(content, "\"discardsLeft\": (\\d+)"));
      var deckSize = Integer.parseInt(extractString(content, "\"deckSize\": (\\d+)"));
      var blindIndex = Integer.parseInt(extractString(content, "\"currentBlindIndex\": (\\d+)"));
      var infiniteMode = false;
      var loopCount = 0;
      var money = 4;
      try {
        infiniteMode =
            Boolean.parseBoolean(extractString(content, "\"infiniteMode\": (true|false)"));
        loopCount = Integer.parseInt(extractString(content, "\"loopCount\": (\\d+)"));
        money = Integer.parseInt(extractString(content, "\"money\": (\\d+)"));
      } catch (Exception ignored) {
      }

      var state = model.getState();
      state.loadState(
          score, handsLeft, discardsLeft, deckSize, blindIndex, infiniteMode, loopCount);
      state.setMoney(money);
      state.setCurrentSaveId(saveId);

      List<Card> loadedHand = new ArrayList<>();
      var handMatcher =
          Pattern.compile("\"rank\": \"(.*?)\", \"suit\": \"(.*?)\"").matcher(content);
      while (handMatcher.find()) {
        var r = Rank.valueOf(handMatcher.group(1));
        var s = Suit.valueOf(handMatcher.group(2));
        loadedHand.add(new Card(r, s));
      }
      model.loadHand(loadedHand);

      var levelsBlock = extractString(content, "\"handLevels\": \\{([^}]*)\\}");
      if (!levelsBlock.isEmpty()) {
        Matcher levelMatcher = Pattern.compile("\"([A-Z_]+)\": (\\d+)").matcher(levelsBlock);
        while (levelMatcher.find()) {
          var combo = Combination.valueOf(levelMatcher.group(1));
          var level = Integer.parseInt(levelMatcher.group(2));
          state.setLevel(combo, level);
        }
      }

      var jokersBlock = extractString(content, "\"activeJokers\"\\s*:\\s*\\[([\\s\\S]*?)\\]");
      if (!jokersBlock.equals("0") && !jokersBlock.isEmpty()) {
        var jItem = Pattern.compile("\"([A-Z0-9_]+)\"").matcher(jokersBlock);
        while (jItem.find()) {
          try {
            state.addJoker(JokerType.valueOf(jItem.group(1)));
          } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
          }
        }
      }

      var planetsBlock = extractString(content, "\"wonPlanets\"\\s*:\\s*\\[([\\s\\S]*?)\\]");
      if (!planetsBlock.equals("0") && !planetsBlock.isEmpty()) {
        Matcher pItem = Pattern.compile("\"([A-Z0-9_]+)\"").matcher(planetsBlock);
        while (pItem.find()) {
          try {
            state.addWonPlanet(Planet.valueOf(pItem.group(1)));
          } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
          }
        }
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  @Override
  public String getPathFileSave() {
    return SAVE_PATH;
  }

  private String extractString(String source, String regex) {
    var m = Pattern.compile(regex).matcher(source);
    if (m.find()) return m.group(1);
    return "0";
  }
}
