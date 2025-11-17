package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class OperationCopyFileToTargetTest
{
    public static final String FILE_CONTENT = "Hello, temp file content!";

    @TempDir
    private Path tempDir;

    private Path target;

    private Fb2KeeperOperationContext context;

    @BeforeEach
    void before() throws IOException
    {
        Path source = Files.createTempDirectory(tempDir, "source");
        Path file = Files.writeString(
                Files.createTempFile(source, "file", ".txt"),
                FILE_CONTENT
        );
        target = Files.createTempDirectory(tempDir, "target");
        context = new Fb2KeeperOperationContext(file, source, target);
    }

    @Test
    void apply() throws IOException
    {
        OperationCopyFileToTarget operation = new OperationCopyFileToTarget();
        Fb2KeeperOperationContext result = operation.apply(context);

        assertTrue(
                result.path().toString().startsWith(target.toAbsolutePath().toString()),
                "Expect new context has path in the target folder"
        );
        assertEquals(
                FILE_CONTENT,
                Files.readString(result.path()),
                "Expect new file to have the same content"
        );
    }
}