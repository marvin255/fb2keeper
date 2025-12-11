package com.github.marvin255.fb2keeper;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileSystemHelper
{
    private static final String EXTENSION_FB2 = "fb2";
    private static final String EXTENSION_FBZ = "fbz";
    private static final String EXTENSION_FB2_ZIP = "fb2.zip";

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

    public static String getExtension(Path path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException("Path can't be null");
        }

        return getExtension(path.getFileName().toString());
    }

    public static String getExtension(String fileName)
    {
        if (fileName.toLowerCase().endsWith("." + EXTENSION_FB2_ZIP))
        {
            return EXTENSION_FB2_ZIP;
        }

        int dotIndex = fileName.lastIndexOf('.');

        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static String getFileNameWithoutExtension(Path path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException("Path can't be null");
        }

        String fileName = path.getFileName().toString();
        if (fileName.toLowerCase().endsWith("." + EXTENSION_FB2_ZIP))
        {
            return fileName.substring(0, fileName.length() - 8);
        }

        int dotIndex = fileName.lastIndexOf('.');

        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    public static boolean isFB2(Path path)
    {
        return path != null && path.getFileName().toString().toLowerCase().endsWith("." + EXTENSION_FB2);
    }

    public static boolean isFB2Zip(Path path)
    {
        if (path == null)
        {
            return false;
        }

        String fileName = path.getFileName().toString().toLowerCase();

        return fileName.endsWith("." + EXTENSION_FBZ) || fileName.endsWith("." + EXTENSION_FB2_ZIP);
    }

    public static String getFB2ZipExtension()
    {
        return EXTENSION_FBZ;
    }
}
