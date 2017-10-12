package main.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.Point;
import main.StringUtils;

/**
 * Created by lukasz on 06.10.17.
 */
public class Goals {

    private int turnCount;
    private Map<String, Set<Point>> pieceGoals = new HashMap<>();
    private Map<String, Integer> minimumPiece = new HashMap<>();

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public Map<String, Set<Point>> getPieceGoals() {
        return pieceGoals;
    }

    public void setPieceGoals(Map<String, Set<Point>> pieceGoals) {
        this.pieceGoals = pieceGoals;
    }

    public Map<String, Integer> getMinimumPiece() {
        return minimumPiece;
    }

    public void setMinimumPiece(Map<String, Integer> minimumPiece) {
        this.minimumPiece = minimumPiece;
    }


    public static class GoalsParser {

        public static Goals parseGoals(String content) {
            List<String> contentList = Arrays.asList(content.split("&"));

            Goals goals = new Goals();

            contentList
                    .stream()
                    .map(String::trim)
                    .forEach(cl -> {
                        if (cl.startsWith("@")) {
                            addGoals(cl.substring(1), goals);
                        } else if (cl.startsWith("#")) {
                            addLimit(cl.substring(1), goals);
                        } else if (StringUtils.isNotBlank(cl)) {
                            addTurnCount(cl, goals);
                        }
                    });

            return goals;
        }

        private static void addTurnCount(String cl, Goals goals) {
            goals.setTurnCount(Integer.parseInt(cl));
        }

        private static void addLimit(String cl, Goals goals) {
            String[] split = cl.split(" ", 2);
            goals.getMinimumPiece().put(split[0], Integer.parseInt(split[1]));
        }

        private static void addGoals(String cl, Goals goals) {
            String[] split = cl.split(" ", 2);

            goals.getPieceGoals()
                    .put(split[0],
                            Stream.of(split[1].split(","))
                                    .map(String::trim)
                                    .map(s -> s.split(" "))
                                    .map(s -> Point.of(
                                            Integer.parseInt(s[0]),
                                            Integer.parseInt(s[1])
                                    )).collect(Collectors.toSet()));
        }

    }
}
