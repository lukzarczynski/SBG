package main.operator;

import main.model.MoveType;
import main.model.OneMove;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class WithOneOwnPiece extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            if (move.getMoves().size() <= 1) {
                return false;
            }

            int empty = 0;
            int enemyPieces = 0;
            for (int i = 0; i < move.getMoves().size() - 1; i++) {
                if (move.getMoves().get(i).getMoveType().equals(MoveType.EMPTY)) {
                    empty++;
                }
                if (move.getMoves().get(i).getMoveType().equals(MoveType.OWN)) {
                    enemyPieces++;
                }
            }
            return empty > 0 && enemyPieces == 0;
        };
    }

    @Override
    public Function<OneMove, Set<OneMove>> map() {
        return om -> {
            Set<OneMove> result = new HashSet<>();
            for (int i = 0; i < om.getMoves().size() - 1; i++) {
                if (om.getMoves().get(i).getMoveType().equals(MoveType.EMPTY)) {
                    final OneMove copy = om.copy();
                    copy.getMoves().get(i).setMoveType(MoveType.OWN);
                    result.add(copy);
                }

            }
            return result;
        };
    }

    @Override
    public String getDescription() {
        return "With one own piece on the way";
    }
}
