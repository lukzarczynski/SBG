package main;

import main.model.Piece;
import main.statistics.FileStatistic;
import main.statistics.OneFileStats;
import main.statistics.PieceStatistic;
import main.statistics.Statistics;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lukasz on 26.11.16.
 */
public class Main {

//    private static final String DIRECTORY = "C:\\Users\\lukza\\Documents\\sbg_games\\ng\\SIMB-EVOLVED_GAMES-LIST_nonverbose";
    private static final String DIRECTORY = "C:\\Users\\lukza\\Documents\\sbg_games\\ng\\test";
    private static final AtomicInteger index = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {

        final Statistics statistics = parseDirectory(DIRECTORY);

        File statFile = new File(Main.class.getClassLoader().getResource("statistics.html").getFile());
        StringBuilder fContent = new StringBuilder();
        new Scanner(statFile).forEachRemaining(s -> fContent.append(s).append("\n"));

        String content = fContent.toString();

        content = content.replace("{TIME_IN_MS}", statistics.getTimeInMs().toString());
        content = content.replace("{FILES_PARSED}", Integer.toString(statistics.getFilesParsed()));
        content = content.replace("{BEST_FILES_HUM_TABLE}", getFTable(statistics.getBestHumanFiles()));
        content = content.replace("{WORST_FILES_HUM_TABLE}", getFTable(statistics.getWorstHumanFiles()));
        content = content.replace("{BEST_FILES_RAPP_TABLE}", getFTable(statistics.getBestRappFiles()));
        content = content.replace("{WORST_FILES_RAPP_TABLE}", getFTable(statistics.getWorstRappFiles()));
        content = content.replace("{BEST_PIECES_TABLE}", getPTable(statistics.getBestPieces()));
        content = content.replace("{WORST_PIECES_TABLE}", getPTable(statistics.getWorstPieces()));


        File resultFile = new File(DIRECTORY + "\\statistics.html");
        resultFile.createNewFile();
        FileOutputStream resultFOS = new FileOutputStream(resultFile);
        resultFOS.write(content.getBytes());
        resultFOS.close();

//        parseDirAsPieces(DIRECTORY);
    }

    private static Statistics parseDirectory(String directory) throws IOException {

        final File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new RuntimeException(directory + " is not a directory, aborting");
        }

        final File[] sbgs = dir.listFiles((d, name) -> FilenameUtils.getExtension(name).equals("sbg"));
        assert sbgs != null;

        final List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, sbgs);
        long start = System.currentTimeMillis();

        final List<OneFileStats> collect = fileList.parallelStream()
                .map(Main::handleOneFile)
                .collect(Collectors.toList());

        return new Statistics(collect, System.currentTimeMillis() - start);

    }

    private static OneFileStats handleOneFile(File f) {
        OneFileStats oneFileStat = new OneFileStats();
        System.out.println(String.format("%s %s started file %s ", index.getAndIncrement(), new Date().toString(), f.getName()));
        long startFile = System.currentTimeMillis();

        ParseResult parseResult = parseOneFile(f);
        List<String> metaTags = new ArrayList<>();

        Map<String, Integer> pieceValues = new HashMap<>();

        for (Piece p : parseResult.getPieces()) {
//                System.out.println("Parsing piece " + p.getName());
            Pair<String, Integer> resolve = PieceResolver.resolve(p, parseResult.getXY());

            oneFileStat.addPiece(p, f, resolve.getKey(), resolve.getValue());

            metaTags.add(String.format(
                    "META SCORE HUML_%s: %s", p.getName(), resolve.getValue()
            ));
            metaTags.add(String.format(
                    "META DESC HUML_%s: %s", p.getName(), resolve.getKey()
            ));

            pieceValues.put(p.getName(), resolve.getValue());
        }

        Integer result = ParamsAndEvaluators.fgame(parseResult.getPiecesCount(), pieceValues);
        metaTags.add(String.format("META SCORE HUML: %s", result));

        final Scanner scanner;
        try {
            scanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found");
        }

        final StringBuilder fileContent = new StringBuilder(scanner.nextLine()).append("\n");

        Double rappValue = Double.MIN_VALUE;

        boolean isMeta = true;

        final StringBuilder meta = new StringBuilder();

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();

            if (isMeta) {
                if (s.contains("<BOARD>")) {
                    isMeta = false;

                    String metaString = meta.toString();
                    if (metaString.contains("META SCORE RAPP")) {
                        int i1 = metaString.indexOf("META SCORE RAPP") + "META SCORE RAPP: ".length();
                        rappValue = Double.parseDouble(metaString.substring(i1, metaString.indexOf("}", i1)));
                    } else if (metaString.contains("META SCORE SIMB")) {
                        int i1 = metaString.indexOf("META SCORE SIMB") + "META SCORE SIMB: ".length();
                        rappValue = Double.parseDouble(metaString.substring(i1, metaString.indexOf("}", i1)));

                    }

                    fileContent.append(
                            Stream
                                    .of(metaString.split("(?=/\\*)"))
                                    .filter(x -> !x.contains("HUML"))
                                    .collect(Collectors.joining()));

                    metaTags.forEach(tag -> fileContent.append(String.format("/* {%s} */\n", tag)));
                    fileContent.append("\n").append(s).append("\n");
                } else {
                    meta.append(s).append("\n");
                }
            } else {
                fileContent.append(s).append("\n");
            }
        }

        oneFileStat.setFileStatistic(f, rappValue, result);
        try {

            File resultFile = new File(f.getAbsolutePath());
            resultFile.createNewFile();
            FileOutputStream resultFOS = new FileOutputStream(resultFile);
            resultFOS.write(fileContent.toString().getBytes());
            resultFOS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("Finished file %s -- %s ms", f.getName(), System.currentTimeMillis() - startFile));

        return oneFileStat;

    }

    private static ParseResult parseOneFile(File file) {
        final ParseResult result = new ParseResult();

        final Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found");
        }

        final Map<String, Integer> piecesCount = new HashMap<>();

        boolean boardSection = false;
        boolean piecesSection = false;

        final StringBuilder piecesSectionString = new StringBuilder();

        while (scanner.hasNextLine()) {
            final String s = scanner.nextLine();
            if (s.startsWith("//") || StringUtils.isEmpty(s)) {
                continue;
            }

            if (s.contains("<BOARD>")) {
                result.setWidth(scanner.nextInt());
                result.setHeight(scanner.nextInt());
                piecesSection = false;
                boardSection = true;
                continue;
            } else if (s.contains("<PIECES>")) {
                boardSection = false;
                piecesSection = true;
                continue;
            } else if (s.contains("<GOALS>")) {
                break;
            }


            if (boardSection) {
                for (char c : s.toCharArray()) {
                    if (Character.isLetter(c)) {
                        piecesCount.merge(Character.toString(c).toUpperCase(), 1, Integer::sum);
                    }
                }
            } else if (piecesSection) {
                piecesSectionString.append(s);
//                pieces.add(Piece.parse(s, result.getWidth(), result.getHeight()));
            }
        }


        result.setPieces(getPieces(piecesSectionString.toString(), result.getWidth(), result.getHeight()));
        result.setPiecesCount(piecesCount);

        return result;

    }

    private static Collection<Piece> getPieces(String piecesSection, int x, int y) {
        return Stream.of(piecesSection.split("&"))
                .filter(StringUtils::isNotBlank)
                .map(s -> s.replaceAll("\n","") + " &")
                .map(s -> Piece.parse(s, x, y))
                .collect(Collectors.toSet());
    }

    private static synchronized String getFTable(List<FileStatistic> filesStats) {
        StringBuilder table = new StringBuilder();
        final String fileTableFormat = "<tr>\n" +
                "<td><a href=\"%s\">%s</a></td>\n" +
                "<td>%s</td>\n" +
                "<td>%s</td>\n" +
                "</tr>\n";
        filesStats.forEach(fs -> table.append(String.format(
                fileTableFormat, fs.getFile(), fs.getFile(), fs.getRappValue(), fs.getHumaLikenessValue())
        ));

        return table.toString();
    }

    private static synchronized String getPTable(List<PieceStatistic> filesStats) {
        StringBuilder table = new StringBuilder();
        final String pieceTableFormat = "<tr>\n" +
                "<td><a href=\"%s\">%s</a></td>\n" +
                "<td>%s</td>\n" +
                "<td>%s</td>\n" +
                "<td>%s</td>\n" +
                "</tr>\n";
        filesStats.forEach(fs -> table.append(String.format(
                pieceTableFormat, fs.getFile(), fs.getFile(), fs.getValue(), fs.getPiece().toString(), fs.getDescription())
        ));

        return table.toString();
    }

    /**
     * unused
     *
     * @param directory
     * @throws FileNotFoundException
     */
    private static void parseDirAsPieces(String directory) throws FileNotFoundException {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new RuntimeException(directory + " is not a directory, aborting");
        }

        File[] sbgs = dir.listFiles((d, name) -> FilenameUtils.getExtension(name).equals("sbg"));
        assert sbgs != null;


        final List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, sbgs);


        new PrintWriter("src/main/resources/parsedMoves.sbg1").close();
        new PrintWriter("src/main/resources/failedMoves.sbg1").close();
        File parsedFile = new File("src/main/resources/parsedMoves.sbg1");
        File failedFile = new File("src/main/resources/failedMoves.sbg1");

        FileOutputStream parsedFOS = new FileOutputStream(parsedFile, true);
        FileOutputStream failedFOS = new FileOutputStream(failedFile, true);


        AtomicInteger index = new AtomicInteger(0);


        fileList.parallelStream().forEach(f -> {
            String filename = f.getName();
            ParseResult parseResult = parseOneFile(f);

            parseResult.getPieces().parallelStream().forEach(pa -> {
                pa.setName(filename + pa.getName());
                Pair<String, Integer> text = PieceResolver.resolve(pa, parseResult.getXY());
                synchronized (parsedFOS) {
                    try {
                        parsedFOS.write(String.format("%s : %s \n", pa.getName(), text.getKey().replaceAll("\n", "")).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Piece no: " + index.getAndIncrement());
            });

        });

    }

}
