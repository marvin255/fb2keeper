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

    @Test
    void testGetExtensionWithNull()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.getExtension(null)
        );

        assertEquals("Path can't be null", exception.getMessage());
    }

    @Test
    void testGetExtension()
    {
        Path path = Paths.get("test.txt");

        String extension = FileSystemHelper.getExtension(path);

        assertEquals("txt", extension);
    }

    @Test
    void testGetExtensionFB2Zip()
    {
        Path path = Paths.get("test.fb2.zip");

        String extension = FileSystemHelper.getExtension(path);

        assertEquals("fb2.zip", extension);
    }

    @Test
    void testGetExtensionNoExtension()
    {
        Path path = Paths.get("test");

        String extension = FileSystemHelper.getExtension(path);

        assertEquals("", extension);
    }

    @Test
    void testGetFileNameWithoutExtensionWithNull()
    {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> FileSystemHelper.getFileNameWithoutExtension(null)
        );

        assertEquals("Path can't be null", exception.getMessage());
    }

    @Test
    void testGetFileNameWithoutExtension()
    {
        Path path = Paths.get("test.txt");

        String name = FileSystemHelper.getFileNameWithoutExtension(path);

        assertEquals("test", name);
    }

    @Test
    void testGetFileNameWithoutExtensionNoExtension()
    {
        Path path = Paths.get("test");

        String name = FileSystemHelper.getFileNameWithoutExtension(path);

        assertEquals("test", name);
    }

    @Test
    void testGetFileNameWithoutExtensionMultipleDots()
    {
        Path path = Paths.get("test.test.zip");

        String name = FileSystemHelper.getFileNameWithoutExtension(path);

        assertEquals("test.test", name);
    }

    @Test
    void testGetFileNameWithoutExtensionFB2Zip()
    {
        Path path = Paths.get("test.fb2.zip");

        String name = FileSystemHelper.getFileNameWithoutExtension(path);

        assertEquals("test", name);
    }

    @Test
    void testIsFB2()
    {
        Path path = Paths.get("test.fb2");

        assertTrue(FileSystemHelper.isFB2(path));
    }

    @Test
    void testIsFB2Null()
    {
        assertFalse(FileSystemHelper.isFB2(null));
    }

    @Test
    void testIsNotFB2()
    {
        Path path = Paths.get("test.fb2.zip");

        assertFalse(FileSystemHelper.isFB2(path));
    }

    @Test
    void testIsFB2ZipFBZ()
    {
        Path path = Paths.get("test.fbz");

        assertTrue(FileSystemHelper.isFB2Zip(path));
    }

    @Test
    void testIsFB2ZipFB2Zip()
    {
        Path path = Paths.get("test.fb2.zip");

        assertTrue(FileSystemHelper.isFB2Zip(path));
    }

    @Test
    void testIsFB2ZipFB2ZipNull()
    {
        assertFalse(FileSystemHelper.isFB2Zip(null));
    }

    @Test
    void testIsNotFB2Zip()
    {
        Path path = Paths.get("test.zip");

        assertFalse(FileSystemHelper.isFB2Zip(path));
    }

    @Test
    void testGetFB2ZipExtension()
    {
        assertEquals("fbz", FileSystemHelper.getFB2ZipExtension());
    }
}