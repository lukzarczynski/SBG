package main.piececlass;

import main.Point;
import main.model.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class XYRider extends PieceClass {

    private final String description;

    public XYRider(int x, int y) {
        super(Point.of(x, y));
        addAnyNumberOf(x, y);
        addAnyNumberOf(-x, y);
        addAnyNumberOf(x, -y);
        addAnyNumberOf(-x, -y);

        if (x == y) {
            description = String.format("rides %s diagonally", x);
        } else if (x == 0) {
            description = String.format("rides %s horizontaly", y);
        } else if (y == 0) {
            description = String.format("rides %s verticaly", x);
        } else {
            description = String.format("(%s,%s) rider", x, y);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    private void addAnyNumberOf(int x, int y) {
        for (int i = 1; i < 13; i++) {
            for (int j = 1; j <= i; j++) {
                if (j * x > 12 || j * y > 12) {
                    continue;
                }
                moves.addAll(OneMove.parse(String.format("(%s,%s,e)^%s", x, y, j)));
            }
        }
        addAnyNumberTaking(x, y);
    }

    private void addAnyNumberTaking(int x, int y) {
        for (int i = 1; i < 13; i++) {
            for (int j = 0; j < i; j++) {
                if (j * x > 12 || j * y > 12) {
                    continue;
                }
                moves.addAll(OneMove.parse(String.format("(%s,%s,e)^%s(%s,%s,p)", x, y, j, x, y)));
            }
        }
    }
}
