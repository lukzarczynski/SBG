import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import main.Main;
import main.Piece;
import main.PieceResolver;
import main.PieceResolverException;


/**
 * Created by lukza on 26.12.2016.
 */
@RunWith(Parameterized.class)
public class ResolverTest {

  private String fileName;

  public ResolverTest(String fileName) {
    this.fileName = fileName;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {"testFile1.sbg1"},
        {"testFile2.sbg1"}

    });
  }

  @Test
  public void someTest() throws FileNotFoundException, PieceResolverException {
    File inputFile = new File("src/test/resources/" + fileName);

    Set<Piece> pieces = Main.loadPieces(inputFile);

    assertTrue(pieces.size() > 0);

    pieces.forEach(p -> {
      String resolve;
      try {
        resolve = PieceResolver.resolve(p);
        System.out.println(resolve);
      } catch (PieceResolverException e) {
        fail();
      }
    });
  }
}
