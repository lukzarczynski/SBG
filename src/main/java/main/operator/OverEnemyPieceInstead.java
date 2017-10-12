package main.operator;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import main.model.Move;
import main.model.MoveType;
import main.model.OneMove;

/**
 * moves that end only without capturing enemy piece <p> Created by lukasz on 06.12.16.
 */
public class OverEnemyPieceInstead extends Operator {


    @Override
    public Predicate<OneMove> matches() {
        return move -> true;
    }

    @Override
    public Function<OneMove, Set<OneMove>> map() {
        return move -> {
            OneMove om = new OneMove();
            om.setMoves(move.getMoves().stream().map(m -> {
                final Move copy = m.copy();
                if (copy.getMoveType().equals(MoveType.EMPTY)) {
                    copy.setMoveType(MoveType.PIECE);
                }
                return copy;
            }).collect(Collectors.toList()));
            return setOf(om);
        };
    }

    @Override
    public String getDescription() {
        return "Over Enemy Piece Instead of empty";
    }
}
