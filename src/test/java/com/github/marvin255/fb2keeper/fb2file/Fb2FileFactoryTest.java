package com.github.marvin255.fb2keeper.fb2file;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Fb2FileFactoryTest
{
    private static final Path FILE_PATH = Path.of("test.fb2");

    @Test
    void createFromPath()
    {
        Fb2File fb2File = Fb2FileFactory.createFromPath(FILE_PATH);

        assertNotNull(fb2File);
    }
}