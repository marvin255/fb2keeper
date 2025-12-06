package com.github.marvin255.fb2keeper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Fb2Keeper
{
    private final List<Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>> operations;

    private final Logger logger;

    public Fb2Keeper(List<Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>> operations)
    {
        this(operations, null);
    }

    public Fb2Keeper(
            List<Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>> operations,
            Logger logger
    )
    {
        checkOperations(operations);
        this.operations = List.copyOf(operations);
        this.logger = logger;
    }

    public void keep(Path source, Path target) throws IOException
    {
        final Path checkedSource = FileSystemHelper.checkAndReturnDir(source);
        final Path checkedTarget = FileSystemHelper.checkAndReturnDir(target);
        cleanTarget(checkedTarget);
        try (var files = Files.walk(source))
        {
            files.parallel()
                    .filter(this::isFileAllowedToKeep)
                    .map(f -> new Fb2KeeperOperationContext(f, checkedSource, checkedTarget))
                    .forEach(this::runFileOperations);
        }
    }

    private boolean isFileAllowedToKeep(Path file)
    {
        return Files.isRegularFile(file)
                && (FileSystemHelper.isFB2(file) || FileSystemHelper.isFB2Zip(file));
    }

    private void runFileOperations(Fb2KeeperOperationContext context)
    {
        Fb2KeeperOperationContext operationContext = Objects.requireNonNull(context);
        for (var operation : operations)
        {
            try
            {
                operationContext = operation.apply(operationContext);
                if (operationContext == null)
                {
                    throw new IllegalArgumentException("Operation returned null context");
                }
            }
            catch (Exception e)
            {
                log(
                        Level.SEVERE,
                        "File operation '%s' for file '%s' (original file '%s') failed with an exception: %s"
                                .formatted(
                                        operation.getClass().getName(),
                                        operationContext == null ? "empty context" : operationContext.path().toString(),
                                        context.path().toString(),
                                        e.getMessage()
                                )
                );
                break;
            }
        }
    }

    private void checkOperations(final List<Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>> operations)
    {
        if (operations == null)
        {
            throw new IllegalArgumentException("Operations list can't be null");
        }
        if (operations.stream().anyMatch(Objects::isNull))
        {
            throw new IllegalArgumentException("Operations list can't have nulls");
        }
    }

    private void cleanTarget(Path target)
    {
        try (var stream = Files.walk(target))
        {
            stream.filter(path -> !path.equals(target))
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try
                        {
                            Files.delete(path);
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    });
        }
        catch (Exception e)
        {
            log(
                    Level.SEVERE,
                    "Can't clean the target folder '%s'".formatted(target.toAbsolutePath().toString())
            );
        }
    }

    private void log(Level level, String message)
    {
        if (logger != null)
        {
            logger.log(level, message);
        }
    }
}
