package main.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by lukza on 17.01.2017.
 */
public class Game {

    private Collection<Piece> pieces;
    private Map<String, Integer> piecesCount;
    private int width;
    private int height;

    private List<String> board;
    private Goals goals;
    private String content;

    public Collection<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(Collection<Piece> pieces) {
        this.pieces = pieces;
    }

    public Map<String, Integer> getPiecesCount() {
        return piecesCount;
    }

    public void setPiecesCount(Map<String, Integer> piecesCount) {
        this.piecesCount = piecesCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Pair<Integer, Integer> getXY() {
        return Pair.of(width, height);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public List<String> getBoard() {
        return board;
    }

    public void setBoard(List<String> board) {
        this.board = board;
    }

    public Goals getGoals() {
        return goals;
    }

    public void setGoals(Goals goals) {
        this.goals = goals;
    }
}
