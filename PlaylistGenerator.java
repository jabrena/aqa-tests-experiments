///usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

public class PlaylistGenerator {

    public static void main(String... args) throws IOException {

        Function<String, String> decorate = param -> {
            try {
                String test = new String(Files.readAllBytes(Paths.get("./templates/test.txt")), "UTF-8");
                return test.replace("PARAM", param);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        String header = new String(Files.readAllBytes(Paths.get("./templates/header.txt")), "UTF-8");
        String footer = new String(Files.readAllBytes(Paths.get("./templates/footer.txt")), "UTF-8");
        final String path = "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests";
        final String body = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().contains("tests/atomicity/primitives/"))//TODO Remove in some point
                .map(f -> f.getFileName())
                .map(String::valueOf)
                .map(s -> s.replace(".java", ""))
                .map(decorate)
                .reduce("", String::concat);

        final String fileName = "playlist.xml";
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(header);
        myWriter.write(body);
        myWriter.write(footer);
        myWriter.close();
    }
}

