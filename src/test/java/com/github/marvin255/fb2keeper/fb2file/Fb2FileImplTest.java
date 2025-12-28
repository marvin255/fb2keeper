package com.github.marvin255.fb2keeper.fb2file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class Fb2FileImplTest
{
    private final static String AUTHOR_FIRST_NAME = "John";
    private final static String AUTHOR_LAST_NAME = "Doe";
    private final static String AUTHOR_1_FIRST_NAME = "Jane";
    private final static String AUTHOR_1_LAST_NAME = "Smith";
    private final static String BOOK_TITLE = "The book";
    private final static String FILE_CONTENT = """
            <?xml version="1.0" encoding="utf-8"?>
            <FictionBook>
                <description>
                    <title-info>
                        <author>
                            <first-name>%s</first-name>
                            <last-name>%s</last-name>
                        </author>
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
                    AUTHOR_1_FIRST_NAME,
                    AUTHOR_1_LAST_NAME,
                    BOOK_TITLE
            );

    @TempDir
    private Path tempDir;

    private Path file;

    @BeforeEach
    void before() throws IOException
    {
        file = Files.writeString(
                Files.createTempFile(tempDir, "file", ".fb2"),
                FILE_CONTENT
        );
    }

    @Test
    void title()
    {
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        String title = fb2File.title();

        assertEquals(BOOK_TITLE, title);
    }

    @Test
    void authors()
    {
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(2, authors.size());
        assertEquals(AUTHOR_FIRST_NAME, authors.getFirst().name());
        assertEquals(AUTHOR_LAST_NAME, authors.getFirst().lastName());
        assertEquals(AUTHOR_1_FIRST_NAME, authors.get(1).name());
        assertEquals(AUTHOR_1_LAST_NAME, authors.get(1).lastName());
    }
}