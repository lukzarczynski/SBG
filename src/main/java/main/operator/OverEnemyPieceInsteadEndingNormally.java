package main.operator;

import main.model.Move;
import main.model.MoveType;
import main.model.OneMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * moves that end only without capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class OverEnemyPieceInsteadEndingNormally extends Operator {


    @Override
    public Predicate<OneMove> matches() {
        return move -> true;
    }

    @Override
    public Function<OneMove, Set<OneMove>> map() {
        return move -> {
            OneMove om = new OneMove();
            List<Move> copiedList = new ArrayList<>();
            for (int i = 0; i < move.getMoves().size(); i++) {
                if (i < move.getMoves().size() - 1) {
                    final Move copy = move.getMoves().get(i).copy();
                    if (copy.getMoveType().equals(MoveType.EMPTY)) {
                        copy.setMoveType(MoveType.PIECE);
                    }
                    copiedList.add(copy);
                } else {
                    copiedList.add(move.getMoves().get(i).copy());

                }
            }
            om.setMoves(copiedList);
            return setOf(om);
        };
    }

    @Override
    public String getDescription() {
        return "Over Enemy Piece Instead of empty (except last move)";
    }
}
