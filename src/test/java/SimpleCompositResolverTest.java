import main.Piece;
import main.PieceResolver;
import main.PieceResolverException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SimpleCompositResolverTest {

    @Test
    public void testPiece() throws PieceResolverException {
        String pieceRegex = "K " +
                "(1,-1,p) " +
                "+ (-1,-1,p) " +
                "+ (1,0,p)(1,0,p)(1,0,p) " +
                "+ (1,0,p)(1,0,p)(1,0,p)(1,0,p)(1,0,p)(1,0,p)(1,0,p) " +
                "+ (1,0,e)(1,0,e)(1,0,e)(1,0,e)(1,0,e) " +
                "+ (-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e) " +
                "+ (1,0,p)(1,0,p)(1,0,p)(1,0,p)(1,0,p) " +
                "+ (-1,0,e)(-1,0,e)(-1,0,e) " +
                "+ (1,0,e)(1,0,e)(1,0,e)(1,0,e) " +
                "+ (-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e) " +
                "+ (-1,0,p)(-1,0,p)(-1,0,p) " +
                "+ (1,0,e) " +
                "+ (1,0,e)(1,0,e) " +
                "+ (-1,0,e) " +
                "+ (-1,0,e)(-1,0,e) " +
                "+ (1,0,e)(1,0,e)(1,0,e)(1,0,e)(1,0,e)(1,0,e)(1,0,e) " +
                "+ (1,0,p) " +
                "+ (-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p) " +
                "+ (-1,0,p) " +
                "+ (-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p) " +
                "+ (1,0,e)(1,0,e)(1,0,e)(1,0,e)(1,0,e)(1,0,e) " +
                "+ (-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p) " +
                "+ (-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p)(-1,0,p) " +
                "+ (1,0,e)(1,0,e)(1,0,e) " +
                "+ (-1,0,p)(-1,0,p) " +
                "+ (-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e) " +
                "+ (1,0,p)(1,0,p) " +
                "+ (1,0,p)(1,0,p)(1,0,p)(1,0,p) " +
                "+ (1,0,p)(1,0,p)(1,0,p)(1,0,p)(1,0,p)(1,0,p) " +
                "+ (-1,-1,e) " +
                "+ (1,-1,e) " +
                "+ (-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e)(-1,0,e) &";


        Piece piece = Piece.parse(pieceRegex);


        Pair<String, Integer> resolve = PieceResolver.resolve(piece);

        System.out.println(resolve);
    }

}
