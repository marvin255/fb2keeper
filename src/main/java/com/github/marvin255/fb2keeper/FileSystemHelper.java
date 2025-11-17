package com.github.marvin255.fb2keeper;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileSystemHelper
{
    private FileSystemHelper()
    {
    }

    public static Path checkAndReturnFile(Path file)
    {
        if (file == null)
        {
            throw new IllegalArgumentException("File can't be null");
        }

        if (!Files.exists(file))
        {
            throw new IllegalArgumentException("File doesn't exist: %s".formatted(file.toString()));
        }

        if (!Files.isRegularFile(file))
        {
            throw new IllegalArgumentException("Path is not a file: %s".formatted(file.toString()));
        }

        return file;
    }

    public static Path checkAndReturnDir(Path dir)
    {
        if (dir == null)
        {
            throw new IllegalArgumentException("Dir can't be null");
        }

        if (!Files.exists(dir))
        {
            throw new IllegalArgumentException("Dir doesn't exist: %s".formatted(dir.toString()));
        }

        if (!Files.isDirectory(dir))
        {
            throw new IllegalArgumentException("Path is not a dir: %s".formatted(dir.toString()));
        }

        return dir;
    }
}
