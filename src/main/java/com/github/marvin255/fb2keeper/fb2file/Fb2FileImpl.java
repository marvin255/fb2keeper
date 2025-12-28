package com.github.marvin255.fb2keeper.fb2file;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class Fb2FileImpl implements Fb2File
{
    public static final String CSS_QUERY_TITLE = "FictionBook > description > title-info > book-title";
    public static final String CSS_QUERY_AUTHORS = "FictionBook > description > title-info > author";
    public static final String CSS_QUERY_AUTHOR_FIRST_NAME = "first-name";
    public static final String CSS_QUERY_AUTHOR_LAST_NAME = "last-name";

    private final Path filePath;

    private Document document;

    private String title;

    private List<Author> authors;

    public Fb2FileImpl(Path filePath)
    {
        this.filePath = filePath;
    }

    @Override
    public String title()
    {
        if (title == null)
        {
            title = extractStringValue(getDocument().selectFirst(CSS_QUERY_TITLE));
        }
        return title;
    }

    @Override
    public List<Author> authors()
    {
        if (authors == null)
        {
            authors = getDocument().select(CSS_QUERY_AUTHORS).stream()
                    .map(
                            e -> (Author) new AuthorRecord(
                                    extractStringValue(e.selectFirst(CSS_QUERY_AUTHOR_FIRST_NAME)),
                                    extractStringValue(e.selectFirst(CSS_QUERY_AUTHOR_LAST_NAME))
                            )
                    )
                    .toList();
        }
        return authors;
    }

    private Document getDocument()
    {
        if (document == null)
        {
            try
            {
                document = Jsoup.parse(filePath);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return document;
    }

    private String extractStringValue(Element element)
    {
        if (element == null)
        {
            return "";
        }
        return Objects.requireNonNull(element).text().trim();
    }

    private record AuthorRecord(
            String name,
            String lastName
    ) implements Author
    {
    }
}
