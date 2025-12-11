package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

final class OperationZipFb2FileTest
{
    public static final String FILE_CONTENT = "Hello, temp file content!";

    @TempDir
    private Path tempDir;

    private Path file;

    private Fb2KeeperOperationContext context;

    @BeforeEach
    void before() throws IOException
    {
        Path source = Files.createTempDirectory(tempDir, "source");
        file = Files.writeString(
                Files.createTempFile(source, "file", ".txt"),
                FILE_CONTENT
        );
        Path target = Files.createTempDirectory(tempDir, "target");
        context = new Fb2KeeperOperationContext(file, source, target);
    }

    @Test
    void apply()
    {
        Path archivePath = Paths.get(
                file.getParent().toString(),
                FileSystemHelper.getFileNameWithoutExtension(file)
                        + "." + FileSystemHelper.getFB2ZipExtension()
        );

        OperationZipFb2File operation = new OperationZipFb2File();
        Fb2KeeperOperationContext result = operation.apply(context);

        assertEquals(result.path(), archivePath, "Expect new context has path to the archive");
        assertTrue(Files.exists(result.path()), "Expect archive is created");
        assertFalse(Files.exists(file), "Expect target is deleted");
    }
}