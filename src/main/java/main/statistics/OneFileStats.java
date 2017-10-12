package main.statistics;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import main.model.Piece;

/**
 * Created by lukza on 24.01.2017.
 */
public class OneFileStats {

    private final Set<PieceStatistic> pieces = new HashSet<>();
    private FileStatistic fileStatistic;

    public void addPiece(Piece p, File f, String descr, Integer value) {
        PieceStatistic stat = new PieceStatistic();
        stat.setDescription(descr);
        stat.setPiece(p);
        stat.setValue(value);
        stat.setFile(f.getName());
        pieces.add(stat);
    }

    public Set<PieceStatistic> getPieces() {
        return pieces;
    }

    public FileStatistic getFileStatistic() {
        return fileStatistic;
    }

    public void setFileStatistic(FileStatistic fileStatistic) {
        this.fileStatistic = fileStatistic;
    }

    public void setFileStatistic(File f, Double rappValue, Integer result) {
        fileStatistic = new FileStatistic();
        fileStatistic.setFile(f.getName());
        fileStatistic.setRappValue(rappValue);
        fileStatistic.setHumaLikenessValue(result);
    }
}
