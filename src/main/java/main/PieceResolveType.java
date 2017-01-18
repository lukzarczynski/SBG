package main;

import main.model.Move;
import main.model.OneMove;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by lukza on 17.01.2017.
 */
enum PieceResolveType {

    SIMPLE,
    COMPOSITE,
    OTHER;

    public static PieceResolveType forPiece(OneMove om) {
        int types = 0;
        Pair<Integer, Integer> currPair = Pair.of(0, 0);

        for (Move m : om.getMoves()) {
            Pair<Integer, Integer> of = Pair.of(m.getDx(), m.getDy());
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
            default:
                return OTHER;
        }


    }

}
