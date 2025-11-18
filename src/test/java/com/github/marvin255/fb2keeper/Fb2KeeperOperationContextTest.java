package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class Fb2KeeperOperationContextTest
{
    private static final Path PATH = Paths.get("/path");
    private static final Path PATH_2 = Paths.get("/path_2");
    private static final Path SOURCE = Paths.get("/source");
    private static final Path TARGET = Paths.get("/target");

    @Test
    void testConstructorWithNullPath()
    {
        assertThrows(
                NullPointerException.class,
                () -> new Fb2KeeperOperationContext(null, SOURCE, TARGET)
        );
    }

    @Test
    void testConstructorWithNullSource()
    {
        assertThrows(
                NullPointerException.class,
                () -> new Fb2KeeperOperationContext(PATH, null, TARGET)
        );
    }

    @Test
    void testConstructorWithNullTarget()
    {
        assertThrows(
                NullPointerException.class,
                () -> new Fb2KeeperOperationContext(PATH, SOURCE, null)
        );
    }

    @Test
    void testWithPath()
    {
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(PATH, SOURCE, TARGET);

        Fb2KeeperOperationContext newContext = context.withPath(PATH_2);

        assertSame(SOURCE, newContext.source());
        assertSame(TARGET, newContext.target());
        assertSame(PATH_2, newContext.path());
    }
}