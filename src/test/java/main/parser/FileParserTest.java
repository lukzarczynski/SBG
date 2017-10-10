package main.parser;

import main.model.Game;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by lukasz on 06.10.17.
 */
public class FileParserTest {

    FileParser parser = new FileParser();

    @Test
    public void test() throws IOException {
        Game game = parser.parseGame(new File("/home/lukasz/IdeaProjects/SBG/src/main/resources/RAPP_LegacyOfIbis.sbg"));

        System.out.println(game);
    }


}