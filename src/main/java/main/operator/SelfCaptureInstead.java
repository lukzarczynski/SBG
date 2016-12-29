package main.operator;

import main.Move;
import main.MoveType;
import main.OneMove;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * moves that end only without capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class SelfCaptureInstead extends Operator {


    @Override
    public Predicate<OneMove> matches() {
        return move -> true;
    }

    @Override
    public Function<OneMove, OneMove> map() {
        return move -> {
            OneMove om = new OneMove();
            List<Move> moves = new ArrayList<>(om.getMoves());
            if (!moves.isEmpty()) {
                Move lastMove = moves.get(moves.size() - 1).copy();
                lastMove.setMoveType(MoveType.OWN);
                moves.set(moves.size() - 1, lastMove.copy());
                om.setMoves(moves);
            }
            return om;
        };
    }

    @Override
    public String getDescription() {
        return "Captures self piece instead of enemy";
    }
}
