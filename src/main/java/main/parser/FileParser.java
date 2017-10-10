package main.parser;

import main.model.Game;
import main.model.Goals;
import main.model.Piece;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lukasz on 06.10.17.
 */
public class FileParser {

    public Game parseGame(File file) throws IOException {
        final Game game = new Game();

        String content = new String(Files.readAllBytes(Paths.get(file.getPath())));

        game.setContent(content);

        String noComments = cleanFile(content);

        Scanner scanner = new Scanner(noComments);

        Map<String, StringBuilder> sectionValues = new HashMap<>();
        sectionValues.put("<BOARD>", new StringBuilder());
        sectionValues.put("<PIECES>", new StringBuilder());
        sectionValues.put("<GOALS>", new StringBuilder());

        String currentSection = null;
        while (scanner.hasNextLine()) {
            final String s = scanner.nextLine();
            Optional<String> first = sectionValues.keySet().stream()
                    .filter(s::contains).findFirst();
            if (first.isPresent()) {
                currentSection = first.get();
                continue;
            }
            if (currentSection != null) {
                sectionValues.get(currentSection).append(s).append("\n");
            }
        }

        String board = sectionValues.get("<BOARD>").toString();
        String pieces = sectionValues.get("<PIECES>").toString();
        String goals = sectionValues.get("<GOALS>").toString();

        List<String> split = new ArrayList<>(Arrays.asList(board.split("\n")));
        String size = split.get(0);
        split.remove(0);

        game.setBoard(split);
        game.setWidth(Integer.parseInt(size.trim().split(" ")[0]));
        game.setHeight(Integer.parseInt(size.trim().split(" ")[1]));

        game.setPieces(getPieces(pieces, game.getWidth(), game.getHeight()));

        game.setGoals(Goals.GoalsParser.parseGoals(goals));

        Map<String, Integer> piecesCount = new HashMap<>();
        for (char c : board.toCharArray()) {
            if (Character.isLetter(c)) {
                piecesCount.merge(Character.toString(c).toUpperCase(), 1, Integer::sum);
            }
        }
        game.setPiecesCount(piecesCount);

        return game;
    }

    private Collection<Piece> getPieces(String piecesSection, int x, int y) {
        return Stream.of(piecesSection.split("&"))
                .filter(StringUtils::isNotBlank)
                .map(s -> s.replaceAll("\n", "") + " &")
                .map(s -> Piece.parse(s, x, y))
                .collect(Collectors.toList());
    }

    private String cleanFile(String content) {
        String result = content;
        while (result.contains("/*")) {
            int i = result.indexOf("/*");
            int i1 = result.indexOf("*/", i);
            if (i1 < 0) {
                throw new RuntimeException("Invalid comments");
            }
            result = result.substring(0, i) + result.substring(i1 + 2);
        }
        return result.replaceAll("//.*(?=\\n)", "")
                .replaceAll("\\n\\n*", "\n");

    }



}
