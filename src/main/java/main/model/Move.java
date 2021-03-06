package main.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 26.11.16.
 */
public class Move {

    private String regex;
    private Integer power = 1;
    private Integer dx = 0;
    private Integer dy = 0;
    private MoveType moveType;

    public Move(int power) {
        this.power = power;
    }

    public static List<List<Move>> parse(String regex) {
        String m = regex;
        final List<Move> moves = new ArrayList<>();
        if (m.contains("^")) {
            final String pow = m.split("\\^")[1];
            if ("*".equals(pow)) {
                moves.add(new Move(0));
                moves.add(new Move(1));
                moves.add(new Move(2));
                moves.add(new Move(3));
                moves.add(new Move(4));
                moves.add(new Move(5));
                moves.add(new Move(6));
                moves.add(new Move(7));
                moves.add(new Move(8));
                moves.add(new Move(9));
                moves.add(new Move(10));
                moves.add(new Move(11));
                moves.add(new Move(12));
                moves.add(new Move(13));
                moves.add(new Move(14));
            } else if (pow.contains("{")) {
                moves.addAll(
                        Arrays.stream(pow.substring(1, pow.indexOf("}"))
                                .split(","))
                                .map(Integer::parseInt)
                                .map(Move::new).collect(Collectors.toSet()));
            } else {
                moves.add(new Move(Integer.parseInt(pow)));
            }
            m = m.substring(0, m.indexOf("^"));
        } else {
            moves.add(new Move(1));
        }
        final String[] ss = m.replace(")", "").replace("(", "").split(",");
        moves.forEach(move -> {
            move.setDx(Integer.parseInt(ss[0]));
            move.setDy(Integer.parseInt(ss[1]));
            move.setMoveType(MoveType.byCode(ss[2]));
        });
        return moves.stream().map(m2 -> {
            List<Move> result = new ArrayList<>();
            for (int i = 0; i < m2.getPower(); i++) {
                Move copy = m2.copy();
                copy.setPower(1);
                result.add(copy);
            }
            return result;
        }).collect(Collectors.toList());
    }

    public Move copy() {
        final Move move = new Move(power);
        move.setDx(this.getDx());
        move.setDy(this.getDy());
        move.setMoveType(this.getMoveType());
        return move;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getDx() {
        return dx;
    }

    public void setDx(Integer dx) {
        this.dx = dx;
    }

    public Integer getDy() {
        return dy;
    }

    public void setDy(Integer dy) {
        this.dy = dy;
    }

    public Pair<Integer, Integer> getXY() {
        return Pair.of(getDx(), getDy());
    }


    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    @Override
    public int hashCode() {
        int result = power.hashCode();
        result = 31 * result + getDx().hashCode();
        result = 31 * result + getDy().hashCode();
        result = 31 * result + getMoveType().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Move move = (Move) o;

        return power.equals(move.getPower())
                && getDx().equals(move.getDx())
                && getDy().equals(move.getDy())
                && getMoveType().equals(move.getMoveType());
    }

    public boolean equalsWithoutType(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Move move = (Move) o;

        return power.equals(move.getPower())
                && getDx().equals(move.getDx())
                && getDy().equals(move.getDy());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < power; i++) {
            builder.append(String.format("(%s,%s,%s)", dx, dy, moveType.getCode()));
        }
        return builder.toString();
    }

}