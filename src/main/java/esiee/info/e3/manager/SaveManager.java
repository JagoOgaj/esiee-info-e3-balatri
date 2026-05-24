package esiee.info.e3.manager;

import esiee.info.e3.config.GameConfig;
import esiee.info.e3.domain.Blind;
import esiee.info.e3.domain.Card;
import esiee.info.e3.domain.enums.Combination;
import esiee.info.e3.domain.enums.Rank;
import esiee.info.e3.domain.enums.Suit;
import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.domain.enums.Planet;
import esiee.info.e3.model.GameModel;
import esiee.info.e3.model.GameState;
import esiee.info.e3.view.pages.SavesPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveManager {

    private static final String SAVES_DIR = GameConfig.getSavesDir();

    public static void saveGame(GameModel model, String status) {
        try {
            Files.createDirectories(Paths.get(SAVES_DIR));
            GameState state = model.getState();
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String id = state.getCurrentSaveId();
            if (id == null || id.isEmpty()) {
                id = "save_" + System.currentTimeMillis();
                state.setCurrentSaveId(id);
            }

            StringBuilder json = new StringBuilder("{\n");
            json.append("  \"id\": \"").append(id).append("\",\n");
            json.append("  \"date\": \"").append(date).append("\",\n");
            json.append("  \"status\": \"").append(status).append("\",\n");
            json.append("  \"score\": ").append(state.getCurrentScore()).append(",\n");
            json.append("  \"handsLeft\": ").append(state.getHandsLeft()).append(",\n");
            json.append("  \"discardsLeft\": ").append(state.getDiscardsLeft()).append(",\n");
            json.append("  \"deckSize\": ").append(state.getDeckSize()).append(",\n");
            json.append("  \"currentBlindIndex\": ").append(state.getCurrentBlindIndex()).append(",\n");

            json.append("  \"infiniteMode\": ").append(state.isInfiniteMode()).append(",\n");
            json.append("  \"loopCount\": ").append(state.getLoopCount()).append(",\n");

            json.append("  \"currentHand\": [\n");
            List<Card> hand = model.getHand();
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                json.append("    {\"rank\": \"").append(c.rank().name()).append("\", \"suit\": \"").append(c.suit().name()).append("\"}");
                if (i < hand.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ],\n");

            json.append("  \"handLevels\": {\n");
            Combination[] combos = Combination.values();
            for (int i = 0; i < combos.length; i++) {
                json.append("    \"").append(combos[i].name()).append("\": ").append(state.getLevel(combos[i]));
                if (i < combos.length - 1) json.append(",");
                json.append("\n");
            }
            json.append("  },\n");

            json.append("  \"activeJokers\": [\n");
            List<JokerType> jokers = state.getActiveJokers();
            for (int i = 0; i < jokers.size(); i++) {
                json.append("    \"").append(jokers.get(i).name()).append("\"");
                if (i < jokers.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ],\n");

            json.append("  \"wonPlanets\": [\n");
            List<Planet> planets = new ArrayList<>(state.getWonPlanets());
            for (int i = 0; i < planets.size(); i++) {
                json.append("    \"").append(planets.get(i).name()).append("\"");
                if (i < planets.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ]\n");
            json.append("}\n");
            Files.writeString(Paths.get(SAVES_DIR + id + ".json"), json.toString());

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static List<SavesPage.SaveSlot> loadAllSaves() {
        List<SavesPage.SaveSlot> list = new ArrayList<>();
        File dir = new File(SAVES_DIR);
        if (!dir.exists() || !dir.isDirectory()) return list;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return list;

        List<Blind> allBlinds = GameConfig.allBlinds();

        for (File f : files) {
            try {
                String content = Files.readString(f.toPath());
                String id = extractString(content, "\"id\": \"(.*?)\"");
                String date = extractString(content, "\"date\": \"(.*?)\"");
                String status = extractString(content, "\"status\": \"(.*?)\"");
                long score = Long.parseLong(extractString(content, "\"score\": (\\d+)"));
                int blindIndex = Integer.parseInt(extractString(content, "\"currentBlindIndex\": (\\d+)"));

                boolean infiniteMode = false;
                int loopCount = 0;
                try {
                    infiniteMode = Boolean.parseBoolean(extractString(content, "\"infiniteMode\": (true|false)"));
                    loopCount = Integer.parseInt(extractString(content, "\"loopCount\": (\\d+)"));
                } catch (Exception ignored) {}

                String blindName = "Inconnu";
                int blindId = 1;
                if (blindIndex >= 0 && blindIndex < allBlinds.size()) {
                    blindName = allBlinds.get(blindIndex).name();
                    blindId = allBlinds.get(blindIndex).id();
                }

                boolean canResume = "EN_COURS".equals(status);

                list.add(new SavesPage.SaveSlot(id, date, blindName, blindId, score, status, canResume, infiniteMode, loopCount));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        list.sort((a, b) -> b.id().compareTo(a.id()));
        return list;
    }

    public static void loadGame(String saveId, GameModel model) {
        try {
            String content = Files.readString(Paths.get(SAVES_DIR + saveId + ".json"));

            long score = Long.parseLong(extractString(content, "\"score\": (\\d+)"));
            int handsLeft = Integer.parseInt(extractString(content, "\"handsLeft\": (\\d+)"));
            int discardsLeft = Integer.parseInt(extractString(content, "\"discardsLeft\": (\\d+)"));
            int deckSize = Integer.parseInt(extractString(content, "\"deckSize\": (\\d+)"));
            int blindIndex = Integer.parseInt(extractString(content, "\"currentBlindIndex\": (\\d+)"));

            boolean infiniteMode = false;
            int loopCount = 0;
            try {
                infiniteMode = Boolean.parseBoolean(extractString(content, "\"infiniteMode\": (true|false)"));
                loopCount = Integer.parseInt(extractString(content, "\"loopCount\": (\\d+)"));
            } catch (Exception ignored) {}

            GameState state = model.getState();
            state.loadState(score, handsLeft, discardsLeft, deckSize, blindIndex, infiniteMode, loopCount);
            state.setCurrentSaveId(saveId);

            List<Card> loadedHand = new ArrayList<>();
            Matcher handMatcher = Pattern.compile("\"rank\": \"(.*?)\", \"suit\": \"(.*?)\"").matcher(content);
            while (handMatcher.find()) {
                Rank r = Rank.valueOf(handMatcher.group(1));
                Suit s = Suit.valueOf(handMatcher.group(2));
                loadedHand.add(new Card(r, s));
            }
            model.loadHand(loadedHand);

            String levelsBlock = extractString(content, "\"handLevels\": \\{([^}]*)\\}");
            if (!levelsBlock.isEmpty()) {
                Matcher levelMatcher = Pattern.compile("\"([A-Z_]+)\": (\\d+)").matcher(levelsBlock);
                while (levelMatcher.find()) {
                    Combination combo = Combination.valueOf(levelMatcher.group(1));
                    int level = Integer.parseInt(levelMatcher.group(2));
                    state.setLevel(combo, level);
                }
            }

            String jokersBlock = extractString(content, "\"activeJokers\"\\s*:\\s*\\[([\\s\\S]*?)\\]");
            if (!jokersBlock.equals("0") && !jokersBlock.isEmpty()) {
                Matcher jItem = Pattern.compile("\"([A-Z0-9_]+)\"").matcher(jokersBlock);
                while (jItem.find()) {
                    try {
                        state.addJoker(JokerType.valueOf(jItem.group(1)));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Joker introuvable (peut-être retiré du code) : " + jItem.group(1));
                    }
                }
            }

            String planetsBlock = extractString(content, "\"wonPlanets\"\\s*:\\s*\\[([\\s\\S]*?)\\]");
            if (!planetsBlock.equals("0") && !planetsBlock.isEmpty()) {
                Matcher pItem = Pattern.compile("\"([A-Z0-9_]+)\"").matcher(planetsBlock);
                while (pItem.find()) {
                    try {
                        state.addWonPlanet(Planet.valueOf(pItem.group(1)));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Planète introuvable : " + pItem.group(1));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Can't save  " + saveId + " : " + e.getMessage());
        }
    }

    private static String extractString(String source, String regex) {
        Matcher m = Pattern.compile(regex).matcher(source);
        if (m.find()) return m.group(1);
        return "0";
    }
}