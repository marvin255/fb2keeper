package com.github.marvin255.fb2keeper.dirhandler;

import com.github.marvin255.fb2keeper.Fb2KeeperException;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public interface DirHandler
{
    Path getPath();

    void processFiles(Function<Path, Path> step) throws Fb2KeeperException;

    void processFiles(List<Function<Path, Path>> steps) throws Fb2KeeperException;
}
