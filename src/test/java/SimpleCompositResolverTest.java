import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import main.OneMove;
import main.Piece;
import main.operator.Forward;
import main.operator.OnlyCapture;
import main.operator.Operator;
import main.operator.RestrictedToX;
import main.operator.WithoutCapture;
import main.piececlass.XYLeaper;
import main.piececlass.XYRider;
import main.resolvers.SimpleComopositResolver;
import main.resolvers.SimplePieceResolver;
import main.tree.ResolveResult;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SimpleCompositResolverTest {

  @Test
  public void testPiece() {
    String pieceRegex = " M(0,1,e)(1,1,p) + (0,1,e)(-1,1,p) + (2,0,e) &";

    Set<OneMove> parse = Piece.parse(pieceRegex).getMoves();

    Set<Operator> operators1 = new HashSet<>();

    operators1.add(new WithoutCapture());
    operators1.add(new Forward());
    operators1.add(new RestrictedToX(1));
    SimplePieceResolver resolver1 = new SimplePieceResolver(new XYRider(0, 1), operators1);


    Set<Operator> operators2 = new HashSet<>();

    operators2.add(new OnlyCapture());
    operators2.add(new Forward());

    SimplePieceResolver resolver2 = new SimplePieceResolver(new XYLeaper(1, 1), operators2);


    SimpleComopositResolver resolver = new SimpleComopositResolver(resolver1, resolver2);

    boolean applicable = resolver.isApplicable(parse);

    ResolveResult apply = resolver.apply(parse);
  }

}
