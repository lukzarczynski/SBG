import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import main.model.OneMove;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Created by lukasz on 26.11.16.
 */
@RunWith(Parameterized.class)
public class OneMoveTest {

    private String move;

    public OneMoveTest(String move) {
        this.move = move;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {"(1,0,e)^{1}"},
                {"(1,0,e)^2(1,0,p)^{0,1}"},
                {"(1,0,e)^*(1,0,p)^*"}
        });
    }

    @Test
    public void testParameters() {
        final Set<OneMove> parse = OneMove.parse(move);

        System.out.println(String.format("Parsed: %s as %s moves", move, parse.size()));
        parse.forEach(System.out::println);
    }
}