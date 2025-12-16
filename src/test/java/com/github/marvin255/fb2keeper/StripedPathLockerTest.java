package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class StripedPathLockerTest
{
    private static final Path PATH = Path.of("/test/test");

    @Test
    void runLockedFunction()
    {
        String result = StripedPathLocker.runLocked(
                PATH,
                p -> "function: " + p.toAbsolutePath()
        );

        assertEquals("function: " + PATH.toAbsolutePath(), result);
    }

    @Test
    void runLockedConsumer()
    {
        final List<String> results = new ArrayList<>();
        Consumer<Path> consumer = p -> results.add(p.toString());

        StripedPathLocker.runLocked(PATH, consumer);

        assertEquals(List.of(PATH.toString()), results);
    }
}