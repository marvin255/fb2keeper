package com.github.marvin255.fb2keeper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
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

        BookData bookData = getBookData(filePath);
        Path authorFolderPath = checkAndReturnAuthorFolder(targetDirPath, bookData);
        Path newFilePath = renameFileAndReturnPath(filePath, authorFolderPath, bookData);

        return fb2KeeperOperationContext.withPath(newFilePath);
    }

    private Path checkAndReturnAuthorFolder(Path targetDirPath, BookData bookData)
    {
        Path authorFolderPath = targetDirPath.resolve(bookData.author());

        if (Files.exists(authorFolderPath) && !Files.isDirectory(authorFolderPath))
        {
            throw new RuntimeException(
                    "Can't create author directory, because it's a file: %s".formatted(targetDirPath.toString())
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

    private Path renameFileAndReturnPath(Path filePath, Path targetDirPath, BookData bookData)
    {
        Path newPath = targetDirPath.resolve(
                bookData.name() + "." + FileSystemHelper.getExtension(filePath)
        );

        if (Files.exists(newPath))
        {
            throw new RuntimeException(
                    "Book file %s already exists".formatted(newPath.toString())
            );
        }

        try
        {
            Files.move(filePath, newPath);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return newPath.toAbsolutePath();
    }

    private BookData getBookData(Path filePath)
    {
        Document doc;
        try
        {
            doc = Jsoup.parse(
                    new File(filePath.toAbsolutePath().toString())
            );
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        String authorName = doc.select("FictionBook > description > title-info > author > first-name")
                .text()
                .trim();
        if (authorName.isEmpty())
        {
            throw new RuntimeException("Author name can't be empty");
        }

        String authorLastName = doc.select("FictionBook > description > title-info > author > last-name")
                .text()
                .trim();
        if (authorLastName.isEmpty())
        {
            throw new RuntimeException("Author last name can't be empty");
        }

        String name = doc.select("FictionBook > description > title-info > book-title")
                .text()
                .trim();
        if (name.isEmpty())
        {
            throw new RuntimeException("Book name can't be empty");
        }

        return new BookData(authorLastName + " " + authorName, name);
    }

    private record BookData(String author, String name)
    {
    }
}
