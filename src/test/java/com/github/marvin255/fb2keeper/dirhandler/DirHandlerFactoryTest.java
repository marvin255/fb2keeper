package com.github.marvin255.fb2keeper.dirhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

final class DirHandlerFactoryTest
{
    @TempDir
    private Path tempDir;

    @Test
    void testCreateWithString()
    {
        DirHandler dirHandler = DirHandlerFactory.create(tempDir.toString());

        assertNotNull(dirHandler);
    }

    @Test
    void testCreateWithStringAndFilter()
    {
        DirHandler dirHandler = DirHandlerFactory.create(tempDir.toString(), f -> true);

        assertNotNull(dirHandler);
    }

    @Test
    void testCreateWithPath()
    {
        DirHandler dirHandler = DirHandlerFactory.create(tempDir);

        assertNotNull(dirHandler);
    }

    @Test
    void testCreateWithPathAndFilter()
    {
        DirHandler dirHandler = DirHandlerFactory.create(tempDir, f -> true);

        assertNotNull(dirHandler);
    }
}