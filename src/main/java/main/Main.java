package main;

import main.model.Piece;
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

    public static void parseDirectory(String directory, final Statistics statistics) throws IOException {

        final File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new RuntimeException(directory + " is not a directory, aborting");
        }

        final File[] sbgs = dir.listFiles((d, name) -> FilenameUtils.getExtension(name).equals("sbg"));
        assert sbgs != null;

        final List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, sbgs);

        fileList.parallelStream().forEach(f -> handleOneFile(f, sbgs.length, statistics));

    }

    private static void handleOneFile(File f, int length, final Statistics statistics) {
        long startFile = System.currentTimeMillis();
        try {

            ParseResult parseResult = parseOneFile(f);
            Integer i = 0;
            List<String> metaTags = new ArrayList<>();

            for (Piece p : parseResult.getPieces()) {
                Pair<String, Integer> resolve;
                resolve = PieceResolver.resolve(p, parseResult.getXY());

                statistics.addPiece(p, f,  resolve.getKey(), resolve.getValue());

                metaTags.add(String.format(
                        "META SCORE HUML_%s: %s", p.getName(), resolve.getValue()
                ));
                metaTags.add(String.format(
                        "META DESC HUML_%s: %s", p.getName(), resolve.getKey()
                ));

                i += resolve.getValue() * Optional.ofNullable(parseResult.getPiecesCount().get(p.getName())).orElse(0);
            }

            Integer sumOfCounts = parseResult.getPiecesCount().values().stream().reduce(0, Integer::sum);
            Integer numberOfDifferentPieces = parseResult.getPiecesCount().size();
            Integer result = (i / sumOfCounts) * (10 + numberOfDifferentPieces);
            metaTags.add(String.format("META SCORE HUML: %s", result));

            final Scanner scanner = new Scanner(f);

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

            statistics.addFile(f, rappValue, result);

            File resultFile = new File(f.getAbsolutePath());
            resultFile.createNewFile();
            FileOutputStream resultFOS = new FileOutputStream(resultFile);
            resultFOS.write(fileContent.toString().getBytes());
            resultFOS.close();


            System.out.println(String.format("Finished file %s (P: %s, F: %s, SUM: %s, FM: %s) of %s -- %s ms",
                    f.getName(),
                    statistics.getSuccess().incrementAndGet(),
                    statistics.getFail().get(),
                    statistics.getSuccess().get() + statistics.getFail().get(),
                    PieceResolver.failedMoves.size(),
                    length,
                    System.currentTimeMillis() - startFile));
        } catch (PieceResolverException e) {
            System.out.println("\t" + e.getMessage());
            System.out.println(String.format("Failed file %s (P: %s, F: %s, SUM: %s, FM: %s) of %s -- %s ms",
                    f.getName(),
                    statistics.getSuccess().get(),
                    statistics.getFail().incrementAndGet(),
                    statistics.getSuccess().get() + statistics.getFail().get(),
                    PieceResolver.failedMoves.size(), length,
                    System.currentTimeMillis() - startFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ParseResult parseOneFile(File file) throws FileNotFoundException {
        final ParseResult result = new ParseResult();

        final Set<Piece> pieces = new HashSet<>();
        final Scanner scanner = new Scanner(file);

        final Map<String, Integer> piecesCount = new HashMap<>();

        boolean boardSection = false;
        boolean piecesSection = false;

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
                pieces.add(Piece.parse(s, result.getWidth(), result.getHeight()));
            }
        }

        result.setPieces(pieces);
        result.setPiecesCount(piecesCount);

        return result;

    }

    public static void main(String[] args) throws IOException {
        final Statistics statistics = new Statistics();

        try {
            parseDirectory("C:\\Users\\lukza\\Documents\\sbg_games\\RAPP-EVOLVED_GAMES-LIST", statistics);
        } catch (RuntimeException e) {
            System.out.println(statistics);
        }

        File statFile = new File(Main.class.getClassLoader().getResource("statistics.html").getFile());
        StringBuilder fContent = new StringBuilder();
        new Scanner(statFile).forEachRemaining(s -> fContent.append(s).append("\n"));

        String content = fContent.toString();

        content = content.replace("{BEST_FILES_HUM_TABLE}", getFTable(statistics.getBestHumanFiles()));
        content = content.replace("{WORST_FILES_HUM_TABLE}", getFTable(statistics.getWorstHumanFiles()));
        content = content.replace("{BEST_FILES_RAPP_TABLE}", getFTable(statistics.getBestRappFiles()));
        content = content.replace("{WORST_FILES_RAPP_TABLE}", getFTable(statistics.getWorstRappFiles()));
        content = content.replace("{BEST_PIECES_TABLE}", getPTable(statistics.getBestPieces()));
        content = content.replace("{WORST_PIECES_TABLE}", getPTable(statistics.getWorstPieces()));


        File resultFile = new File("C:\\Users\\lukza\\Documents\\sbg_games\\RAPP-EVOLVED_GAMES-LIST\\statistics.html");
        resultFile.createNewFile();
        FileOutputStream resultFOS = new FileOutputStream(resultFile);
        resultFOS.write(content.getBytes());
        resultFOS.close();

//        parseDirAsPieces("C:\\Users\\lukza\\Documents\\sbg_games\\RAPP-EVOLVED_GAMES-LIST");

    }

    private static String getFTable(List<Statistics.FileStatistic> filesStats){
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
    private static String getPTable(List<Statistics.PieceStatistic> filesStats){
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
            try {
                String filename = f.getName();
                ParseResult parseResult = parseOneFile(f);

                parseResult.getPieces().parallelStream().forEach(pa -> {
                    try {
                        pa.setName(filename + pa.getName());
                        Pair<String, Integer> text = PieceResolver.resolve(pa, parseResult.getXY());
                        synchronized (parsedFOS) {
                            try {
                                parsedFOS.write(String.format("%s : %s \n", pa.getName(), text.getKey().replaceAll("\n", "")).getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (PieceResolverException e) {
                        String text = String.format("[%s,%s] %s\n", parseResult.getXY().getKey(), parseResult.getXY().getValue(), pa.toString());
                        synchronized (failedFOS) {
                            try {
                                failedFOS.write(text.getBytes());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    System.out.println("Piece no: " + index.getAndIncrement());
                });

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

    }


}
