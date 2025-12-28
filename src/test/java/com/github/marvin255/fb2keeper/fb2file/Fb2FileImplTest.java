package com.github.marvin255.fb2keeper.fb2file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                Files.createTempFile(tempDir, "Fb2FileImplTest", ".fb2"),
                FILE_CONTENT
        );
    }

    @Test
    void testTitle()
    {
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        String title = fb2File.title();

        assertEquals(BOOK_TITLE, title);
    }

    @Test
    void testTitleNoTitle() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testTitleNoTitle", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);

        assertTrue(fb2File.title().isEmpty());
    }

    @Test
    void testTitleBlankTitle() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <book-title>  </book-title>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testTitleBlankTitle", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);

        assertTrue(fb2File.title().isEmpty());
    }

    @Test
    void testAuthors()
    {
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(2, authors.size());
        assertEquals(AUTHOR_FIRST_NAME, authors.getFirst().name());
        assertEquals(AUTHOR_LAST_NAME, authors.getFirst().lastName());
        assertEquals(AUTHOR_1_FIRST_NAME, authors.get(1).name());
        assertEquals(AUTHOR_1_LAST_NAME, authors.get(1).lastName());
    }

    @Test
    void testAuthorsNoAuthors() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testAuthorsNoAuthors", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(0, authors.size());
    }

    @Test
    void testAuthorsNoAuthorName() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <last-name>Last name</last-name>
                            </author>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testAuthorsNoAuthorName", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(1, authors.size());
        assertTrue(authors.getFirst().name().isEmpty());
    }

    @Test
    void testAuthorsBlankAuthorName() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>  </first-name>
                                <last-name>Last name</last-name>
                            </author>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testAuthorsBlankAuthorName", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(1, authors.size());
        assertTrue(authors.getFirst().name().isEmpty());
    }

    @Test
    void testAuthorsNoAuthorLastName() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>Name</first-name>
                            </author>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testAuthorsNoAuthorLastName", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(1, authors.size());
        assertTrue(authors.getFirst().lastName().isEmpty());
    }

    @Test
    void testAuthorsBlankAuthorLastName() throws IOException
    {
        String contentNoBookName = """
                <?xml version="1.0" encoding="utf-8"?>
                <FictionBook>
                    <description>
                        <title-info>
                            <author>
                                <first-name>Name</first-name>
                                <last-name>   </last-name>
                            </author>
                        </title-info>
                    </description>
                </FictionBook>""";

        Path file = Files.writeString(
                Files.createTempFile(tempDir, "testAuthorsBlankAuthorLastName", ".fb2"),
                contentNoBookName
        );
        Fb2FileImpl fb2File = new Fb2FileImpl(file);
        List<Fb2File.Author> authors = fb2File.authors();

        assertEquals(1, authors.size());
        assertTrue(authors.getFirst().lastName().isEmpty());
    }
}