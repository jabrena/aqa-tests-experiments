package net.adoptopenjdk.generators;

import java.util.List;

public class PlaylistGeneratorExample {

    public static void main(String... args) {
        final List<String> paths = List.of("./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests");

        PlaylistGenerator playlistGenerator = new PlaylistGenerator();
        playlistGenerator.generate(paths);
    }
}
