package com.github.marvin255.fb2keeper;

import java.nio.file.Path;
import java.util.Objects;

public record Fb2KeeperOperationContext(Path path, Path source, Path target)
{
    public Fb2KeeperOperationContext(Path path, Path source, Path target)
    {
        this.path = Objects.requireNonNull(path);
        this.source = Objects.requireNonNull(source);
        this.target = Objects.requireNonNull(target);
    }

    public Fb2KeeperOperationContext withPath(Path newPath)
    {
        return new Fb2KeeperOperationContext(
                newPath,
                source,
                target
        );
    }
}
