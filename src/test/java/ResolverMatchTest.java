import main.Piece;
import main.PieceResolverException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by lukza on 26.12.2016.
 */
@RunWith(Parameterized.class)
public class ResolverMatchTest {

    private String regex;

    public ResolverMatchTest(String regex) {
        this.regex = regex;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {"(-1,0,p) + (0,1,p) + (1,0,p) + (-1,0,e) + (0,1,e) + (1,0,e)"},

        });
    }

    @Test
    public void someTest() throws FileNotFoundException, PieceResolverException {
        Piece p = Piece.parse("NAME " + regex + " &", 8, 8);


        HashSet<Piece> pieces = new HashSet<>();
        pieces.add(p);
        StringBuilder b1 = new StringBuilder();
        StringBuilder b2 = new StringBuilder();
//        Main.resolve(pieces, b1, b2);

        System.out.println(String.format("P: %s, F: %s", b1.toString(), b2.toString()));
    }

    private String toString(Set<? extends Object> oneMoves) {
        return oneMoves.stream().map(Object::toString).collect(Collectors.joining(" + "));
    }
}
