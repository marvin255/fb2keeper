package com.github.marvin255.fb2keeper.dirhandler;

import com.github.marvin255.fb2keeper.Fb2KeeperException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

final class DirHandlerNio implements DirHandler
{
    private final Path path;

    private final Predicate<Path> filesFilter;

    DirHandlerNio(Path path)
    {
        this(path, null);
    }

    DirHandlerNio(Path path, Predicate<Path> filesFilter)
    {
        if (path == null)
        {
            throw new IllegalArgumentException("Path can't be null");
        }

        if (!Files.exists(path))
        {
            throw new IllegalArgumentException("Folder doesn't exist: %s".formatted(path.toString()));
        }

        if (!Files.isDirectory(path))
        {
            throw new IllegalArgumentException("Path is not a folder: %s".formatted(path.toString()));
        }

        this.path = path;
        this.filesFilter = filesFilter;
    }

    @Override
    public Path getPath()
    {
        return path;
    }

    @Override
    public void processFiles(Function<Path, Path> step) throws Fb2KeeperException
    {
        processFiles(List.of(step));
    }

    @Override
    public void processFiles(List<Function<Path, Path>> steps) throws Fb2KeeperException
    {
        if (steps == null)
        {
            throw new IllegalArgumentException("Steps can't be null");
        }

        if (steps.stream().anyMatch(Objects::isNull))
        {
            throw new IllegalArgumentException("Steps can't have nulls");
        }

        final List<Function<Path, Path>> immutableSteps = List.copyOf(steps);
        try (var files = Files.walk(path))
        {
            files.parallel()
                    .filter(Files::isRegularFile)
                    .filter(f -> filesFilter == null || filesFilter.test(f))
                    .forEach(path -> {
                        Path current = path;
                        for (var step : immutableSteps)
                        {
                            current = step.apply(current);
                        }
                    });
        }
        catch (Exception e)
        {
            throw new Fb2KeeperException("File processing failed", e);
        }
    }
}
