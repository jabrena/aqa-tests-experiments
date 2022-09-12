///usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

public class PlaylistGenerator {

    public static void main(String... args) throws IOException {

        Function<String, String> decorate = param -> {
            String testItem = """
                    <test>
                        <testCaseName>jcstress_PARAM</testCaseName>
                        <command>$(JAVA_COMMAND) $(JVM_OPTIONS) -jar $(Q)$(LIB_DIR)$(D)jcstress-latest.jar$(Q) -jvmArgs $(Q)$(JVM_OPTIONS) -t PARAM$(Q); \\
                        $(TEST_STATUS)</command>
                        <levels>
                            <level>extended</level>
                        </levels>
                        <groups>
                            <group>system</group>
                        </groups>
                    </test>
                    """.replace("PARAM", param);
            return testItem;
        };

        String header = new String(Files.readAllBytes(Paths.get("./template/header.txt")), "UTF-8");
        String footer = new String(Files.readAllBytes(Paths.get("./template/footer.txt")), "UTF-8");
        final String path = "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests";
        final String body = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(f -> f.toString().contains("tests/atomicity/primitives/"))
                .map(f -> f.getFileName())
                .map(String::valueOf)
                .map(s -> s.replace(".java", ""))
                .map(decorate)
                .reduce("", String::concat);

        FileWriter myWriter = new FileWriter("playlist.xml");
        myWriter.write(header);
        myWriter.write(body);
        myWriter.write(footer);
        myWriter.close();
    }
}

