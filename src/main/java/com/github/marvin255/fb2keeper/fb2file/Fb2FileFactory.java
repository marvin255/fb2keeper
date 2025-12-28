package com.github.marvin255.fb2keeper.fb2file;

import java.nio.file.Path;

final public class Fb2FileFactory
{
    private Fb2FileFactory()
    {
    }

    public static Fb2File createFromPath(Path filePath)
    {
        return new Fb2FileImpl(filePath);
    }
}
