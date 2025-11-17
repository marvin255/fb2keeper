package com.github.marvin255.fb2keeper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;
import java.util.regex.Pattern;

final class OperationCopyFileToTarget implements Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>
{
    private static final Pattern NON_ALPHA_NUMERIC_UNDERSCORE = Pattern.compile("[^A-Za-z0-9_]");

    @Override
    public Fb2KeeperOperationContext apply(Fb2KeeperOperationContext fb2KeeperOperationContext)
    {
        Path file = FileSystemHelper.checkAndReturnFile(fb2KeeperOperationContext.path());
        Path target = FileSystemHelper.checkAndReturnDir(fb2KeeperOperationContext.target());

        Path newFile = getNewFilePath(target, file);
        copyFile(file, newFile);

        return fb2KeeperOperationContext.withPath(newFile);
    }

    private static Path getNewFilePath(Path target, Path file)
    {
        return Paths.get(
                target.toAbsolutePath().toString(),
                NON_ALPHA_NUMERIC_UNDERSCORE.matcher(file.toAbsolutePath().toString()).replaceAll("_")
                        + "." + FileSystemHelper.getExtension(file)
        );
    }

    private static void copyFile(Path file, Path newFile)
    {
        try
        {
            Files.copy(
                    file,
                    newFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
            );
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
