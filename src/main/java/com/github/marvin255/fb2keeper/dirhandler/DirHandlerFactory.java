package com.github.marvin255.fb2keeper.dirhandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public final class DirHandlerFactory
{
    public static DirHandler create(String path)
    {
        return create(Paths.get(path), null);
    }

    public static DirHandler create(String path, Predicate<Path> filesFilter)
    {
        return create(Paths.get(path), filesFilter);
    }

    public static DirHandler create(Path path)
    {
        return new DirHandlerNio(path, null);
    }

    public static DirHandler create(Path path, Predicate<Path> filesFilter)
    {
        return new DirHandlerNio(path, filesFilter);
    }
}
