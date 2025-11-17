package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

final class FileSystemHelperTest
{
    @TempDir
    private Path tempDir;

    private Path file;

    @BeforeEach
    void before() throws IOException
    {
        file = Files.createTempFile(tempDir, "file", ".fb2");
    }

    @Test
    void testCheckAndReturnFileWithNull()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.checkAndReturnFile(null)
        );

        assertEquals("File can't be null", exception.getMessage());
    }

    @Test
    void testCheckAndReturnFileWithDir()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.checkAndReturnFile(tempDir)
        );

        assertEquals(
                "Path is not a file: %s".formatted(tempDir.toString()),
                exception.getMessage()
        );
    }

    @Test
    void testCheckAndReturnFileWithNotExistedPath()
    {
        Path notExistedPath = Paths.get("/not_existed");

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.checkAndReturnFile(notExistedPath)
        );

        assertEquals(
                "File doesn't exist: %s".formatted(notExistedPath.toString()),
                exception.getMessage()
        );
    }

    @Test
    void testCheckAndReturnFile()
    {
        Path checkedFile = FileSystemHelper.checkAndReturnFile(file);

        assertSame(file, checkedFile);
    }

    @Test
    void testCheckAndReturnDirWithNull()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.checkAndReturnDir(null)
        );

        assertEquals("Dir can't be null", exception.getMessage());
    }

    @Test
    void testCheckAndReturnDirWithFile()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.checkAndReturnDir(file)
        );

        assertEquals(
                "Path is not a dir: %s".formatted(file.toString()),
                exception.getMessage()
        );
    }

    @Test
    void testCheckAndReturnDirWithNotExistedPath()
    {
        Path notExistedPath = Paths.get("/not_existed");

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.checkAndReturnDir(notExistedPath)
        );

        assertEquals(
                "Dir doesn't exist: %s".formatted(notExistedPath.toString()),
                exception.getMessage()
        );
    }

    @Test
    void testCheckAndReturnDir()
    {
        Path checkedDir = FileSystemHelper.checkAndReturnDir(tempDir);

        assertSame(tempDir, checkedDir);
    }
}