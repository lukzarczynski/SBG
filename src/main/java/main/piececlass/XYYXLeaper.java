package main.piececlass;

import main.Point;
import main.model.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class XYYXLeaper extends PieceClass {

    private final String description;
    private final boolean valid;

    public XYYXLeaper(int x, int y) {
        super(Point.of(x, y));
        this.valid = x != y;
        if (valid) {
            final String[] targets = {"e", "p"};
            for (String t : targets) {
                moves.add(OneMove.parse(x, y, t));
                moves.add(OneMove.parse(-x, y, t));
                moves.add(OneMove.parse(x, -y, t));
                moves.add(OneMove.parse(-x, -y, t));

                moves.add(OneMove.parse(y, x, t));
                moves.add(OneMove.parse(-y, x, t));
                moves.add(OneMove.parse(y, -x, t));
                moves.add(OneMove.parse(-y, -x, t));
            }
        }
        if (x == y) {
            description = String.format("leaps %s diagonally", x);
        } else if (x == 0) {
            description = String.format("leaps %s horizontaly or verticaly", y);
        } else if (y == 0) {
            description = String.format("leaps %s horizontaly or verticaly", x);
        } else {
            description = String.format("(%s,%s) leaper", x, y);
        }
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String getDescription() {
        return description;
    }


}
