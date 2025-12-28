package com.github.marvin255.fb2keeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class OperationRenameToBookNameTest
{
    private final static String AUTHOR_FIRST_NAME = "John";
    private final static String AUTHOR_LAST_NAME = "Doe";
    private final static String BOOK_TITLE = "The book";
    private final static String EXPECTED_FILE_PATH = AUTHOR_LAST_NAME + " " + AUTHOR_FIRST_NAME
            + "/" + BOOK_TITLE + ".fb2";

    private final static String DEFAULT_FILE_CONTENT = """
            <?xml version="1.0" encoding="utf-8"?>
            <FictionBook>
                <description>
                    <title-info>
                        <author>
                            <first-name>%s</first-name>
                            <last-name>%s</last-name>
                        </author>
                        <book-title>%s</book-title>
                    </title-info>
                </description>
            </FictionBook>"""
            .formatted(
                    AUTHOR_FIRST_NAME,
                    AUTHOR_LAST_NAME,
                    BOOK_TITLE
            );

    @TempDir
    private Path tempDir;

    private Path sourceDir;

    private Path targetDir;

    @BeforeEach
    void before() throws IOException
    {
        sourceDir = Files.createTempDirectory(tempDir, "sourceDir");
        targetDir = Files.createTempDirectory(tempDir, "targetDir");
    }

    @Test
    void apply() throws IOException
    {
        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                DEFAULT_FILE_CONTENT
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();
        Fb2KeeperOperationContext result = operation.apply(context);

        assertFalse(Files.exists(file), "Old file must be removed");
        assertEquals(targetDir.resolve(EXPECTED_FILE_PATH), result.path(), "File must be renamed");
        assertEquals(
                DEFAULT_FILE_CONTENT,
                Files.readString(result.path()),
                "New file must have the same content"
        );
    }

    @Test
    void applyMultipleAuthors() throws IOException
    {
        String contentWithMultipleAuthors = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>%s</first-name>
                                <last-name>%s</last-name>
                            </author>
                            <author>
                                <first-name>The second name</first-name>
                                <last-name>The last name</last-name>
                            </author>
                            <book-title>%s</book-title>
                        </title-info>
                    </description>
                </FictionBook>"""
                .formatted(
                        AUTHOR_FIRST_NAME,
                        AUTHOR_LAST_NAME,
                        BOOK_TITLE
                );

        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                contentWithMultipleAuthors
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();
        Fb2KeeperOperationContext result = operation.apply(context);

        assertEquals(targetDir.resolve(EXPECTED_FILE_PATH), result.path());
    }

    @Test
    void applyNoBookTitle() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>%s</first-name>
                                <last-name>%s</last-name>
                            </author>
                        </title-info>
                    </description>
                </FictionBook>"""
                .formatted(
                        AUTHOR_FIRST_NAME,
                        AUTHOR_LAST_NAME
                );

        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                contentNoBookName
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }

    @Test
    void applyEmptyBookTitle() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>%s</first-name>
                                <last-name>%s</last-name>
                            </author>
                            <book-title></book-title>
                        </title-info>
                    </description>
                </FictionBook>"""
                .formatted(
                        AUTHOR_FIRST_NAME,
                        AUTHOR_LAST_NAME
                );

        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                contentNoBookName
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }

    @Test
    void applyBookAlreadyExists() throws IOException
    {
        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                DEFAULT_FILE_CONTENT
        );
        Files.createDirectory(targetDir.resolve(AUTHOR_LAST_NAME + " " + AUTHOR_FIRST_NAME));
        Files.createFile(targetDir.resolve(EXPECTED_FILE_PATH));
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }

    @Test
    void applyNoAuthors() throws IOException
    {
        String contentNoAuthors = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <book-title>%s</book-title>
                        </title-info>
                    </description>
                </FictionBook>"""
                .formatted(BOOK_TITLE);

        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                contentNoAuthors
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }

    @Test
    void applyEmptyAuthorName() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name></first-name>
                                <last-name>%s</last-name>
                            </author>
                            <book-title>%s</book-title>
                        </title-info>
                    </description>
                </FictionBook>"""
                .formatted(
                        AUTHOR_LAST_NAME,
                        BOOK_TITLE
                );

        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                contentNoBookName
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }

    @Test
    void applyEmptyAuthorLastName() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>%s</first-name>
                                <last-name></last-name>
                            </author>
                            <book-title>%s</book-title>
                        </title-info>
                    </description>
                </FictionBook>"""
                .formatted(
                        AUTHOR_FIRST_NAME,
                        BOOK_TITLE
                );

        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                contentNoBookName
        );
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }

    @Test
    void applyAuthorDirIsAFile() throws IOException
    {
        Path file = Files.writeString(
                Files.createTempFile(sourceDir, "file", ".fb2"),
                DEFAULT_FILE_CONTENT
        );
        Files.createFile(targetDir.resolve(AUTHOR_LAST_NAME + " " + AUTHOR_FIRST_NAME));
        Fb2KeeperOperationContext context = new Fb2KeeperOperationContext(file, sourceDir, targetDir);

        OperationRenameToBookName operation = new OperationRenameToBookName();

        assertThrows(
                RuntimeException.class,
                () -> operation.apply(context)
        );
    }
}