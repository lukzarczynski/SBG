package main.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import main.model.Move;
import main.model.MoveType;
import main.model.OneMove;

/**
 * moves that captures self piece instead of enemy <p> Created by lukasz on 06.12.16.
 */
public class SelfCaptureInstead extends Operator {


    @Override
    public Predicate<OneMove> matches() {
        return move -> true;
    }

    @Override
    public Function<OneMove, Set<OneMove>> map() {
        return move -> {
            OneMove om = new OneMove();
            List<Move> moves = new ArrayList<>(om.getMoves());
            if (moves.isEmpty()) {
                return setOf(move);
            }
            Move lastMove = moves.get(moves.size() - 1).copy();
            if (lastMove.getMoveType().equals(MoveType.PIECE)) {
                lastMove.setMoveType(MoveType.OWN);
            }
            moves.set(moves.size() - 1, lastMove);
            om.setMoves(moves);

            return setOf(om);
        };
    }

    @Override
    public String getDescription() {
        return "Captures self piece instead of enemy";
    }
}
