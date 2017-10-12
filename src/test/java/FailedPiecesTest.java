import main.Pair;
import main.PieceResolver;
import main.Point;
import main.model.Piece;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by lukza on 15.01.2017.
 */
public class FailedPiecesTest {

    @Test
    public void testFailed() throws FileNotFoundException {
        File failedFile = new File("src/main/resources/failedMoves.sbg1");
        Scanner scanner = new Scanner(failedFile);

        int i = 0;
        int parsed = 0;

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();

            Point xy = extractXY(s);

            Piece parse = Piece.parse(s, xy.getKey(), xy.getValue());

            Pair<String, Integer> resolve = PieceResolver.resolve(parse, xy);
            System.out.println("RESOLVED line: " + parsed + " : " + s);
            System.out.println(resolve.getKey());
            System.out.println(parsed++);

        }

    }

    private Point extractXY(String s) {
        try {
            String substring = s.substring(s.indexOf("["), s.indexOf("]"));

            String[] split = substring.split(",");
            return Point.of(
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1])
            );
        } catch (Exception e) {
            return Point.of(8, 8);
        }

    }


}
