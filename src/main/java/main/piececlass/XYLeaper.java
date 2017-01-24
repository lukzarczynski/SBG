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
public class XYLeaper extends PieceClass {

    private final String description;

    public XYLeaper(int x, int y) {
        super(Pair.of(x, y));
        final String[] targets = {"e", "p"};
        for (String t : targets) {
            moves.add(OneMove.parse(x, y, t));
            moves.add(OneMove.parse(-x, y, t));
            moves.add(OneMove.parse(x, -y, t));
            moves.add(OneMove.parse(-x, -y, t));
        }

        if (x == y) {
            description = String.format("leaps %s diagonally", x);
        } else if (x == 0) {
            description = String.format("leaps %s forward or backwards", y);
        } else if (y == 0) {
            description = String.format("leaps %s sideways", x);
        } else {
            description = String.format("(%s,%s) leaper", x, y);
        }
    }



    @Override
    public String getDescription() {
        return description;
    }


}
