package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

final class OperationUnzipFb2FileTest
{
    private static final String PATH_TO_FB2_ZIP_RESOURCE = "OperationUnzipFb2FileTest/test.fb2.zip";
    private static final String PATH_TO_NO_FB2_ZIP_RESOURCE = "OperationUnzipFb2FileTest/test_no_fb2.fb2.zip";

    @TempDir
    private Path tempDir;

    private Path target;

    @BeforeEach
    void before() throws IOException
    {
        target = Files.createTempDirectory(tempDir, "target");
    }

    @Test
    void applyIgnoresNonArchiveFiles() throws IOException
    {
        Path file = Files.createTempFile(tempDir, "file", ".txt");
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, tempDir, target);

        OperationUnzipFb2File operation = new OperationUnzipFb2File();
        Fb2KeeperOperationContext result = operation.apply(context);

        assertEquals(file, result.path(), "Must ignore non-archive files");
    }

    @Test
    void applyNoFb2File() throws IOException, URISyntaxException
    {
        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource(PATH_TO_NO_FB2_ZIP_RESOURCE));
        Path resourcePath = Path.of(resource.toURI());
        Path source = Files.createTempDirectory(tempDir, "source");
        Path path = Path.of(source.toAbsolutePath().toString(), resourcePath.getFileName().toString());

        Files.copy(
                resourcePath,
                path,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        );

        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(path, tempDir, target);

        OperationUnzipFb2File operation = new OperationUnzipFb2File();

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
        assertTrue(Files.exists(path), "Original file is kept");
        assertEquals(
                "Archive '%s' doesn't have fb2 file".formatted(path.toAbsolutePath().toString()),
                exception.getMessage()
        );
    }

    @Test
    void apply() throws IOException, URISyntaxException
    {
        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource(PATH_TO_FB2_ZIP_RESOURCE));
        Path resourcePath = Path.of(resource.toURI());
        Path source = Files.createTempDirectory(tempDir, "source");
        Path path = Path.of(source.toAbsolutePath().toString(), resourcePath.getFileName().toString());

        Files.copy(
                resourcePath,
                path,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        );

        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(path, tempDir, target);

        OperationUnzipFb2File operation = new OperationUnzipFb2File();
        Fb2KeeperOperationContext result = operation.apply(context);

        assertFalse(Files.exists(path), "Original file is deleted");
        assertTrue(Files.exists(result.path()), "File is unzipped");
    }
}