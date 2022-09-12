///usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PlaylistGenerator {

    public static void main(String... args) throws IOException {

        BiFunction<String, String, String> decorate = (template, param) -> template.replace("PARAM", param);

        final String headerTemplateBlock = new String(Files.readAllBytes(Paths.get("./templates/header.txt")), "UTF-8");
        final String footerTemplateBlock = new String(Files.readAllBytes(Paths.get("./templates/footer.txt")), "UTF-8");
        final String testTemplateBlock = new String(Files.readAllBytes(Paths.get("./templates/test.txt")), "UTF-8");

        final String path = "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests";
        final String body = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().contains("tests/atomicity/primitives/"))//TODO Remove in some point
                .map(f -> f.getFileName())
                .map(String::valueOf)
                .map(s -> s.replace(".java", ""))
                .map(s -> decorate.apply(testTemplateBlock, s))
                .reduce("", String::concat);

        final String fileName = "playlist.xml";
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(headerTemplateBlock);
        myWriter.write(body);
        myWriter.write(footerTemplateBlock);
        myWriter.close();
    }
}

