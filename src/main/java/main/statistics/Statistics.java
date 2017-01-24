package main.statistics;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lukza on 18.01.2017.
 */
public class Statistics {

    private static final Integer COUNT = 100;
    private static final Comparator<PieceStatistic> pieceComparator = Comparator.comparing(PieceStatistic::getValue);
    private static final Comparator<FileStatistic> humanLikenessComparator = Comparator.comparing(FileStatistic::getHumaLikenessValue);
    private static final Comparator<FileStatistic> rappComparator = Comparator.comparing(FileStatistic::getRappValue).reversed();
    private final Long timeInMs;
    private final int filesParsed;

    private final List<PieceStatistic> bestPieces;
    private final List<PieceStatistic> worstPieces;

    private final List<FileStatistic> bestHumanFiles;
    private final List<FileStatistic> worstHumanFiles;
    private final List<FileStatistic> bestRappFile;
    private final List<FileStatistic> worstRappFiles;




    public Statistics(List<OneFileStats> collect, long time) {
        timeInMs = time;
        filesParsed = collect.size();

        final List<FileStatistic> files = collect.stream()
                .map(OneFileStats::getFileStatistic)
                .collect(Collectors.toList());

        final Set<PieceStatistic> pieces = collect.stream()
                .map(OneFileStats::getPieces)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        bestPieces = pieces.stream().sorted(pieceComparator).limit(COUNT).collect(Collectors.toList());
        worstPieces = pieces.stream().sorted(pieceComparator.reversed()).limit(COUNT).collect(Collectors.toList());

        bestHumanFiles = files.stream().sorted(humanLikenessComparator).limit(COUNT).collect(Collectors.toList());
        worstHumanFiles = files.stream().sorted(humanLikenessComparator.reversed()).limit(COUNT).collect(Collectors.toList());

        bestRappFile = files.stream().sorted(rappComparator).limit(COUNT).collect(Collectors.toList());
        worstRappFiles = files.stream().sorted(rappComparator.reversed()).limit(COUNT).collect(Collectors.toList());

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
        return bestRappFile;
    }

    public List<FileStatistic> getWorstRappFiles() {
        return worstRappFiles;
    }

    public Long getTimeInMs() {
        return timeInMs;
    }

    public int getFilesParsed() {
        return filesParsed;
    }
}

