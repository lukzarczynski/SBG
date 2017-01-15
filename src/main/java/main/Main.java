package main;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lukasz on 26.11.16.
 */
public class Main {

    public static void parseDirectory(String directory) throws IOException {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new RuntimeException(directory + " is not a directory, aborting");
        }

        File[] sbgs = dir.listFiles((d, name) -> FilenameUtils.getExtension(name).equals("sbg"));
        assert sbgs != null;


        final List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, sbgs);

        AtomicInteger succ = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);


        fileList.parallelStream().forEach(f -> handleOneFile(f, succ, fail, sbgs.length));

    }

    private static void handleOneFile(File f, AtomicInteger succ, AtomicInteger fail, int length) {
        try {
            ParseResult parseResult = parseOneFile(f);
            Integer i = 0;
            List<String> metaTags = new ArrayList<>();

            for (Piece p : parseResult.getPieces()) {
                Pair<String, Integer> resolve;
                resolve = PieceResolver.resolve(p, parseResult.getXY());
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


            boolean isMeta = true;

            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();

                if (isMeta) {
                    if (s.contains("<BOARD>")) {
                        isMeta = false;
                        metaTags.forEach(tag -> fileContent.append(String.format("/* %s */\n", tag)));
                        fileContent.append("\n").append(s).append("\n");
                    }
//                        if (s.contains("META SCORE HUML")) {
//                        }
                } else {
                    fileContent.append(s).append("\n");
                }
            }

            File resultFile = new File(f.getAbsolutePath() + "2");
            boolean newFile = resultFile.createNewFile();
            FileOutputStream resultFOS = new FileOutputStream(resultFile);
            resultFOS.write(fileContent.toString().getBytes());
            resultFOS.close();


            System.out.println(String.format("Finished file %s (P: %s, F: %s, SUM: %s) of %s",
                    f.getName(),
                    succ.incrementAndGet(),
                    fail.get(),
                    succ.get() + fail.get(), length));
        } catch (PieceResolverException e) {
            System.out.println("\t" + e.getMessage());
            System.out.println(String.format("Failed file %s (P: %s, F: %s, SUM: %s) of %s",
                    f.getName(),
                    succ.get(),
                    fail.incrementAndGet(),
                    succ.get() + fail.get(),
                    length));
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
//        parseDirectory("C:\\Users\\lukza\\Documents\\sbg_games\\RAPP-EVOLVED_GAMES-LIST");
        parseDirAsPieces("C:\\Users\\lukza\\Documents\\sbg_games\\RAPP-EVOLVED_GAMES-LIST");

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

//    public static void main2(String[] args) throws IOException {
//        System.out.println("Scanning");
//
////        scanDirectory();
//
//        System.out.println("Scanned");
//
//        final File allMovesFile = new File("src/main/resources/allMoves.sbg1");
//
//        final Set<Piece> pieces = loadPieces(allMovesFile);
//
//        System.out.println("Loaded");
//
//
//        new PrintWriter("src/main/resources/parsedMoves.sbg1").close();
//        new PrintWriter("src/main/resources/failedMoves.sbg1").close();
//        File parsedFile = new File("src/main/resources/parsedMoves.sbg1");
//        File failedFile = new File("src/main/resources/failedMoves.sbg1");
//
//        FileOutputStream parsedFOS = new FileOutputStream(parsedFile, true);
//        FileOutputStream failedFOS = new FileOutputStream(failedFile, true);
//
//        resolve(pieces, parsedFOS, failedFOS);
//
//        System.out.println("Finished");
//    }

//    public static Set<Piece> loadPieces(File allMovesFile) throws FileNotFoundException {
//        final Set<Piece> pieces = new HashSet<>();
//
//        final Scanner scanner = new Scanner(allMovesFile);
//        while (scanner.hasNextLine()) {
//            final String pieceString = scanner.nextLine();
//            if (!pieceString.startsWith("//")) {
//                pieces.add(Piece.parse(pieceString, result.getWidth(), result.getHeight()));
//            }
//        }
//        return pieces;
//    }

//    public static void resolve(Set<Piece> pieces, FileOutputStream parsedFOS, FileOutputStream failedFOS) throws IOException {
//        final AtomicInteger index = new AtomicInteger(0);
//
//
//        pieces.parallelStream().forEach(pa -> {
//            try {
//                String text = resolve(pa);
//                synchronized (parsedFOS) {
//                    try {
//                        parsedFOS.write(text.getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (PieceResolverException e) {
//                String text = pa.toString() + "\n";
//                synchronized (failedFOS) {
//                    try {
//                        failedFOS.write(text.getBytes());
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//            System.out.println("Piece no: " + index.getAndIncrement());
//        });
//
//        System.out.println("Resolved");
//    }
//
//    public static String resolve(Piece piece) throws PieceResolverException {
//        Pair<String, Integer> result = PieceResolver.resolve(piece);
//        return "// V: " + result.getValue() + ", D: " + result.getKey() + "\n" + piece.toString() + "\n";
//    }

//    private static void scanDirectory() throws IOException {
//        final File dir = new File("/home/lukasz/Documents/mgr/xxxya/ggp_sbgRAPP/Fitness^1");
//
//        final Iterator<File> fileIterator = FileUtils.iterateFiles(dir, new String[]{"sbg"}, true);
//
//        final File allMovesFile = new File("allMoves.sbg1");
//
//        FileOutputStream allMovesOS = new FileOutputStream(allMovesFile, false);
//
//        final List<String> pieces = new ArrayList<>();
//        fileIterator
//                .forEachRemaining(f -> {
//                    Scanner scanner = null;
//                    try {
//                        scanner = new Scanner(f);
//                    } catch (FileNotFoundException ignored) {
//                        throw new RuntimeException();
//                    }
//
//                    Boolean p = false;
//                    StringBuilder sb = new StringBuilder();
//                    while (scanner.hasNext()) {
//                        final String line = scanner.nextLine();
//
//                        if (StringUtils.isBlank(line)) {
//                            continue;
//                        }
//                        if (line.contains("<--PIECES-->")) {
//                            p = true;
//                            continue;
//                        } else if (line.contains("<--GOALS-->")) {
//                            p = false;
//                            continue;
//                        }
//
//                        if (p && sb.length() == 0) {
//                            sb.append(f.getParent()).append(f.getName());
//                        }
//
//                        if (p && !line.startsWith("/")) {
//                            sb.append(line);
//                            if (line.contains("&")) {
//                                pieces.add(sb.toString());
//                                sb = new StringBuilder();
//                            }
//                        }
//                    }
//                });
//
//
//        final Set<Piece> collect = pieces.stream().map((regex) -> Piece.parse(regex, result.getWidth(), result.getHeight())).collect(Collectors.toSet());
//
//        final StringBuilder builder = new StringBuilder();
//        collect.stream().map(Piece::toString).forEach(ps -> {
//            builder.append(ps);
//            builder.append("\n");
//        });
//        allMovesOS.write(builder.toString().getBytes());
//
//    }

    public static class ParseResult {

        private Set<Piece> pieces;
        private Map<String, Integer> piecesCount;
        private int width;
        private int height;


        public Set<Piece> getPieces() {
            return pieces;
        }

        public void setPieces(Set<Piece> pieces) {
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
    }


}
