package main.operator;

import main.model.MoveType;
import main.model.OneMove;

import java.util.function.Predicate;

/**
 * moves that end only without capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class WithoutCapture extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> !move.getMoves().get(move.getMoves().size() - 1).getMoveType().equals(MoveType.PIECE);
    }

    @Override
    public String getDescription() {
        return "Without Capture";
    }
}
