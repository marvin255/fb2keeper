package com.github.marvin255.fb2keeper.dirhandler;

import com.github.marvin255.fb2keeper.Fb2KeeperException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

final class DirHandlerNioTest
{
    @TempDir
    private Path tempDir;

    @Test
    void testConstructorExceptionPathIsNull()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DirHandlerNio(null)
        );
        assertTrue(
                exception.getMessage().startsWith("Path can't be null"),
                "Expected path can't be null message"
        );
    }

    @Test
    void testConstructorExceptionIfPathIsAFile() throws IOException
    {
        Path file = Files.createTempFile(tempDir, "FolderHandlerNioTest", ".txt");

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DirHandlerNio(file)
        );
        assertTrue(
                exception.getMessage().startsWith("Path is not a folder"),
                "Expected path is not a folder message"
        );
    }

    @Test
    void testConstructorExceptionIfDirDoesNotExist()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DirHandlerNio(Paths.get("not_exist"))
        );
        assertTrue(
                exception.getMessage().startsWith("Folder doesn't exist"),
                "Expected folder doesn't exist message"
        );
    }

    @Test
    void testConstructor()
    {
        DirHandlerNio handler = new DirHandlerNio(tempDir);
        Path path = handler.getPath();

        assertEquals(tempDir, path);
    }

    @Test
    void testProcessFilesExceptionWithNull()
    {
        DirHandlerNio handler = new DirHandlerNio(tempDir);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.processFiles((List<Function<Path, Path>>) null)
        );
        assertTrue(
                exception.getMessage().startsWith("Steps can't be null"),
                "Expected steps can't be null message"
        );
    }

    @Test
    void testProcessFilesExceptionWithNullInList()
    {
        DirHandlerNio handler = new DirHandlerNio(tempDir);
        List<Function<Path, Path>> steps = new ArrayList<>();
        steps.add(null);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> handler.processFiles(steps)
        );
        assertTrue(
                exception.getMessage().startsWith("Steps can't have nulls"),
                "Expected steps can't have nulls message"
        );
    }

    @Test
    void testProcessFiles() throws IOException, Fb2KeeperException
    {
        Path file1 = Files.createTempFile(tempDir, "FolderHandlerNioTest", ".txt");
        Path file2 = Files.createTempFile(tempDir, "FolderHandlerNioTest", ".txt");

        DirHandlerNio handler = new DirHandlerNio(tempDir);

        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        handler.processFiles(f -> {
            results.add(f.toString());
            return f;
        });

        assertTrue(results.size() >= 2, "All files must be included");
        assertTrue(results.contains(file1.toString()), "First file path must be included");
        assertTrue(results.contains(file2.toString()), "Second file path must be included");
    }

    @Test
    void testProcessFilesWithFilter() throws IOException, Fb2KeeperException
    {
        Predicate<Path> filter = f -> f.toString().toLowerCase().endsWith(".fb2");
        Path file1 = Files.createTempFile(tempDir, "FolderHandlerNioTest", ".fb2");
        Path file2 = Files.createTempFile(tempDir, "FolderHandlerNioTest", ".txt");

        DirHandlerNio handler = new DirHandlerNio(tempDir, filter);

        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        handler.processFiles(f -> {
            results.add(f.toString());
            return f;
        });

        assertEquals(1, results.size());
        assertTrue(results.contains(file1.toString()), "First file path must be included");
        assertFalse(results.contains(file2.toString()), "Second file path must not be included");
    }

    @Test
    void testProcessFilesProcessException() throws IOException
    {
        Files.createTempFile(tempDir, "FolderHandlerNioTest", ".txt");

        DirHandlerNio handler = new DirHandlerNio(tempDir);

        assertThrows(
                Fb2KeeperException.class,
                () -> handler.processFiles(f -> {
                    throw new RuntimeException();
                })
        );
    }
}