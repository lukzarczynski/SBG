package main;

import main.model.Piece;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lukza on 18.01.2017.
 */
public class Statistics {

    private static final Integer COUNT = 100;
    private static final Comparator<PieceStatistic> pieceComparator = Comparator.comparing(PieceStatistic::getValue);
    private static final Comparator<FileStatistic> humanLikenessComparator = Comparator.comparing(FileStatistic::getHumaLikenessValue);
    private static final Comparator<FileStatistic> rappComparator = Comparator.comparing(FileStatistic::getRappValue);

    private final List<PieceStatistic> bestPieces = new ArrayList<>();
    private final List<PieceStatistic> worstPieces = new ArrayList<>();

    private final List<FileStatistic> bestHumanFiles = new ArrayList<>();
    private final List<FileStatistic> worstHumanFiles = new ArrayList<>();
    private final List<FileStatistic> bestRappFiles = new ArrayList<>();
    private final List<FileStatistic> worstRappFiles = new ArrayList<>();

    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger fail = new AtomicInteger(0);

    public void addPiece(Piece p, File f, String descr, Integer value) {
        PieceStatistic stat = new PieceStatistic();
        stat.setDescription(descr);
        stat.setPiece(p);
        stat.setValue(value);
        stat.setFile(f.getName());
        addPiece(stat);
    }

    public void addPiece(PieceStatistic stat) {
        synchronized (bestPieces) {
            bestPieces.add(stat);
            if (bestPieces.size() > COUNT) {
                bestPieces.sort(pieceComparator);
                bestPieces.remove(bestPieces.size() - 1);
            }

            worstPieces.add(stat);
            if (worstPieces.size() > COUNT) {
                worstPieces.sort(pieceComparator);
                worstPieces.remove(0);
            }
        }
    }

    public void addFile(File f, Double rapp, Integer value) {
        FileStatistic stat = new FileStatistic();
        stat.setFile(f.getName());
        stat.setHumaLikenessValue(value);
        stat.setRappValue(rapp);
        addFile(stat);
    }

    public void addFile(FileStatistic stat) {
        synchronized (bestHumanFiles) {

            bestHumanFiles.add(stat);
            if (bestHumanFiles.size() > COUNT) {
                bestHumanFiles.sort(humanLikenessComparator);
                bestHumanFiles.remove(bestHumanFiles.size() - 1);
            }

            worstHumanFiles.add(stat);
            if (worstHumanFiles.size() > COUNT) {
                worstHumanFiles.sort(humanLikenessComparator);
                worstHumanFiles.remove(0);
            }

            bestRappFiles.add(stat);
            if (bestRappFiles.size() > COUNT) {
                bestRappFiles.sort(rappComparator);
                bestRappFiles.remove(bestRappFiles.size() - 1);
            }

            worstRappFiles.add(stat);
            if (worstRappFiles.size() > COUNT) {
                worstRappFiles.sort(rappComparator);
                worstRappFiles.remove(0);
            }
        }
    }

    public void addSuccess() {
        success.getAndIncrement();
    }

    public void addFailure() {
        fail.getAndIncrement();
    }

    public AtomicInteger getSuccess() {
        return success;
    }

    public AtomicInteger getFail() {
        return fail;
    }

    public List<PieceStatistic> getBestPieces() {
        return bestPieces;
    }

    public List<PieceStatistic> getWorstPieces() {
        return worstPieces;
    }

    public List<FileStatistic> getBestHumanFiles() {
        return bestHumanFiles;
    }

    public List<FileStatistic> getWorstHumanFiles() {
        return worstHumanFiles;
    }

    public List<FileStatistic> getBestRappFiles() {
        return bestRappFiles;
    }

    public List<FileStatistic> getWorstRappFiles() {
        return worstRappFiles;
    }

    public static class PieceStatistic {

        private Piece piece;
        private Integer value;
        private String description;
        private String file;

        public Piece getPiece() {
            return piece;
        }

        public void setPiece(Piece piece) {
            this.piece = piece;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PieceStatistic that = (PieceStatistic) o;

            return piece.equals(that.piece);
        }

        @Override
        public int hashCode() {
            return piece.hashCode();
        }
    }

    public static class FileStatistic {

        private Integer humaLikenessValue;
        private Double rappValue;
        private String filename;

        public Integer getHumaLikenessValue() {
            return humaLikenessValue;
        }

        public void setHumaLikenessValue(Integer humaLikenessValue) {
            this.humaLikenessValue = humaLikenessValue;
        }

        public Double getRappValue() {
            return rappValue;
        }

        public void setRappValue(Double rappValue) {
            this.rappValue = rappValue;
        }

        public String getFile() {
            return filename;
        }

        public void setFile(String file) {
            this.filename = file;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileStatistic that = (FileStatistic) o;

            return filename.equals(that.filename);
        }

        @Override
        public int hashCode() {
            return filename.hashCode();
        }
    }

}

