package com.github.marvin255.fb2keeper.fb2file;

import java.util.List;

public interface Fb2File
{
    String title();

    List<Author> authors();

    interface Author
    {
        String name();

        String lastName();
    }
}
