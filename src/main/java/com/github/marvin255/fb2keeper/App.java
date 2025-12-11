package com.github.marvin255.fb2keeper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public final class App
{
    private static final Fb2Keeper KEEPER = new Fb2Keeper(
            List.of(
                    new OperationCopyFileToTarget(),
                    new OperationUnzipFb2File(),
                    new OperationRenameToBookName(),
                    new OperationZipFb2File()
            ),
            Logger.getLogger(App.class.getName())
    );

    public static void main(String[] args) throws IOException
    {
        KEEPER.keep(
                Path.of(args[0]),
                Path.of(args[1])
        );
    }
}
