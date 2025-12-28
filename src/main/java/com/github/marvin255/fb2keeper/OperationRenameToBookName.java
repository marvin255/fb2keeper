package com.github.marvin255.fb2keeper;

import com.github.marvin255.fb2keeper.fb2file.Fb2File;
import com.github.marvin255.fb2keeper.fb2file.Fb2FileFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public final class OperationRenameToBookName implements Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>
{
    @Override
    public Fb2KeeperOperationContext apply(Fb2KeeperOperationContext fb2KeeperOperationContext)
    {
        Path filePath = FileSystemHelper.checkAndReturnFile(fb2KeeperOperationContext.path());
        Path targetDirPath = FileSystemHelper.checkAndReturnDir(fb2KeeperOperationContext.target());

        Fb2File file = Fb2FileFactory.createFromPath(filePath);
        Path authorFolderPath = getAuthorFolder(targetDirPath, file);
        Path newFilePath = renameFileUsingBookData(filePath, authorFolderPath, file);

        return fb2KeeperOperationContext.withPath(newFilePath);
    }

    private Path getAuthorFolder(Path baseDir, Fb2File file)
    {
        if (file.authors().isEmpty())
        {
            throw new RuntimeException("No authors found");
        }

        Fb2File.Author author = file.authors().getFirst();
        if (author.lastName().isEmpty())
        {
            throw new RuntimeException("No last name for author found");
        }
        if (author.name().isEmpty())
        {
            throw new RuntimeException("No name for author found");
        }

        return StripedPathLocker.runLockedFunction(
                baseDir.resolve(author.lastName() + " " + author.name()),
                this::createAndReturnAuthorFolder
        );
    }

    private Path createAndReturnAuthorFolder(Path authorFolderPath)
    {
        if (Files.exists(authorFolderPath) && !Files.isDirectory(authorFolderPath))
        {
            throw new RuntimeException(
                    "Can't create author directory, because it's a file: %s".formatted(authorFolderPath.toString())
            );
        }

        if (!Files.exists(authorFolderPath))
        {
            try
            {
                Files.createDirectory(authorFolderPath);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        return authorFolderPath.toAbsolutePath();
    }

    private Path renameFileUsingBookData(Path filePath, Path targetDirPath, Fb2File file)
    {
        if (file.title().isEmpty())
        {
            throw new RuntimeException("Book title can't be empty");
        }

        return StripedPathLocker.runLockedFunction(
                targetDirPath.resolve(
                        file.title() + "." + FileSystemHelper.getExtension(filePath)
                ),
                p -> renameFile(filePath, p)
        );
    }

    private Path renameFile(Path from, Path to)
    {
        if (Files.exists(to))
        {
            throw new RuntimeException(
                    "Book file %s already exists".formatted(to.toString())
            );
        }

        try
        {
            Files.move(from, to);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return to.toAbsolutePath();
    }
}
