import main.Main;
import main.Piece;
import main.PieceResolver;
import main.PieceResolverException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * Created by lukza on 26.12.2016.
 */
public class ResolverTest {

    @Test
    public void someTest() throws FileNotFoundException, PieceResolverException {
        File inputFile = new File("src/test/resources/testFile1.sbg1");

        Set<Piece> pieces = Main.loadPieces(inputFile);

        assertEquals(1, pieces.size());
        Piece piece = pieces.iterator().next();

        String resolve;
        try{
            resolve = PieceResolver.resolve(piece);
        } catch(PieceResolverException e){
            resolve = "FAILED:" +  e.getMessage();
        }

        System.out.println(resolve);

        assertNotNull(resolve);
    }
}
