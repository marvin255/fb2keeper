package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
final class Fb2KeeperTest
{
    @TempDir
    private Path tempDir;

    private Path source;

    private Path file;

    private Path file1;

    private Path target;

    @Mock
    private Logger loggerMock;

    @BeforeEach
    void before() throws IOException
    {
        source = Files.createTempDirectory(tempDir, "source");
        file = Files.createTempFile(source, "file", ".fb2");
        file1 = Files.createTempFile(source, "file_1", ".fb2.zip");
        target = Files.createTempDirectory(tempDir, "target");

        Files.createTempFile(source, "file_2", ".txt");
    }

    @Test
    void testConstructWithNullOperations()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Fb2Keeper(null)
        );

        assertEquals("Operations list can't be null", exception.getMessage());
    }

    @Test
    void testConstructWithNullOperationInLIst()
    {
        List<Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>> operations = new ArrayList<>();
        operations.add(null);

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Fb2Keeper(operations)
        );

        assertEquals("Operations list can't have nulls", exception.getMessage());
    }

    @Test
    void testKeep() throws IOException
    {
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation1 = f -> {
            results.add("operation 1 " + f.path().toString());
            return f;
        };
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation2 = f -> {
            results.add("operation 2 " + f.path().toString());
            return f;
        };

        Fb2Keeper fb2Keeper = new Fb2Keeper(List.of(operation1, operation2));
        fb2Keeper.keep(source, target);

        assertEquals(4, results.size());
        assertTrue(
                results.contains("operation 1 " + file),
                "Expect that operation 1 handles file 1"
        );
        assertTrue(
                results.contains("operation 1 " + file1),
                "Expect that operation 1 handles file 2"
        );
        assertTrue(
                results.contains("operation 2 " + file),
                "Expect that operation 2 handles file 1"
        );
        assertTrue(
                results.contains("operation 2 " + file1),
                "Expect that operation 2 handles file 2"
        );
    }

    @Test
    void testKeepWithExceptionInOperation() throws IOException
    {
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation1 = f -> {
            if (f.path().toString().equals(file1.toString()))
            {
                throw new RuntimeException();
            }
            results.add("operation 1 " + f.path());
            return f;
        };
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation2 = f -> {
            results.add("operation 2 " + f.path());
            return f;
        };

        Fb2Keeper fb2Keeper = new Fb2Keeper(List.of(operation1, operation2));
        fb2Keeper.keep(source, target);

        assertEquals(2, results.size());
        assertTrue(
                results.contains("operation 1 " + file),
                "Expect that operation 1 handles file 1"
        );
        assertTrue(
                results.contains("operation 2 " + file),
                "Expect that operation 2 handles file 1"
        );
    }

    @Test
    void testKeepWithExceptionInOperationWithLogger() throws IOException
    {
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation1 = f -> {
            if (f.path().toString().equals(file1.toString()))
            {
                throw new RuntimeException();
            }
            return f;
        };

        Fb2Keeper fb2Keeper = new Fb2Keeper(List.of(operation1), loggerMock);
        fb2Keeper.keep(source, target);

        verify(loggerMock, times(1)).log(eq(Level.SEVERE), anyString());
    }

    @Test
    void testKeepWithNullContext() throws IOException
    {
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation1 = f -> {
            if (f.path().toString().equals(file1.toString()))
            {
                return null;
            }
            results.add("operation 1 " + f.path());
            return f;
        };
        Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext> operation2 = f -> {
            results.add("operation 2 " + f.path());
            return f;
        };

        Fb2Keeper fb2Keeper = new Fb2Keeper(List.of(operation1, operation2));
        fb2Keeper.keep(source, target);

        assertEquals(2, results.size());
        assertTrue(
                results.contains("operation 1 " + file),
                "Expect that operation 1 handles file 1"
        );
        assertTrue(
                results.contains("operation 2 " + file),
                "Expect that operation 2 handles file 1"
        );
    }
}