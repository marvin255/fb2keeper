package com.github.marvin255.fb2keeper;

import java.nio.file.Path;
import java.util.function.Function;

public final class OperationRenameToBookName implements Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>
{
    @Override
    public Fb2KeeperOperationContext apply(Fb2KeeperOperationContext fb2KeeperOperationContext)
    {
        Path file = FileSystemHelper.checkAndReturnFile(fb2KeeperOperationContext.path());
        Path target = FileSystemHelper.checkAndReturnDir(fb2KeeperOperationContext.target());
        return fb2KeeperOperationContext;
    }
}
