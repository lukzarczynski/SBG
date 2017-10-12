package main;

import main.model.Move;
import main.model.OneMove;

/**
 * Created by lukza on 17.01.2017.
 */
enum PieceResolveType {

    SIMPLE,
    COMPOSITE,
    DOUBLE_BENT,
    OTHER;

    public static PieceResolveType forPiece(OneMove om) {
        int types = 0;
        Point currPair = Point.of(0, 0);

        for (Move m : om.getMoves()) {
            Point of = Point.of(m.getDx(), m.getDy());
            if (!currPair.equals(of)) {
                types++;
            }
            currPair = of;
        }
        switch (types) {
            case 1:
                return SIMPLE;
            case 2:
                return COMPOSITE;
            case 3:
                return DOUBLE_BENT;
            default:
                return OTHER;
        }


    }

}
