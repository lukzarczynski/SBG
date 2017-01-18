package main.piececlass;

import main.MoveUtil;
import main.model.OneMove;
import main.operator.Operator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Set;

/**
 * Created by lukasz on 06.12.16.
 */
public class XYRider extends PieceClass {

    private final String description;

    public XYRider(int x, int y) {
        super(Pair.of(x, y));
        addAnyNumberOf(x, y);
        addAnyNumberOf(-x, y);
        addAnyNumberOf(x, -y);
        addAnyNumberOf(-x, -y);
        description = String.format("(%s,%s) rider", x, y);
    }

    @Override
    public boolean matches(Set<OneMove> pieceMoves, Collection<Operator> op, Pair<Integer, Integer> xy) {
        final Set<OneMove> b = filterMoves(op, xy);
        return !b.isEmpty() && MoveUtil.containsAll(pieceMoves, b);
    }

    @Override
    public boolean matchesPrefix(Set<OneMove> moves, Collection<Operator> op, Pair<Integer, Integer> xy) {
        final Set<OneMove> b = filterMoves(op, xy);
        return !b.isEmpty() && MoveUtil.containsAllPrefixes(moves, b);
    }

    @Override
    public Set<OneMove> apply(Set<OneMove> pieceMoves, Collection<Operator> op, Pair<Integer, Integer> xy) {
        final Set<OneMove> b = filterMoves(op, xy);
        return b.isEmpty() ? pieceMoves : MoveUtil.subtract(pieceMoves, b);
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void addAnyNumberOf(int x, int y) {
        for (int i = 1; i < 8; i++) {
            for (int j = 1; j <= i; j++) {
                if (j * x > 7 || j * y > 7) {
                    continue;
                }
                moves.addAll(OneMove.parse(String.format("(%s,%s,e)^%s", x, y, j)));
            }
        }
        addAnyNumberTaking(x, y);
    }

    private void addAnyNumberTaking(int x, int y) {
        for (int i = 1; i < 8; i++) {
            for (int j = 0; j < i; j++) {
                if (j * x > 7 || j * y > 7) {
                    continue;
                }
                moves.addAll(OneMove.parse(String.format("(%s,%s,e)^%s(%s,%s,p)", x, y, j, x, y)));
            }
        }
    }
}
