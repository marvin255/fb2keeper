package com.github.marvin255.fb2keeper;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

public final class StripedPathLocker
{
    private static final int STRIPES = 64;
    private static final ReentrantLock[] LOCKS = new ReentrantLock[STRIPES];

    static
    {
        for (int i = 0; i < STRIPES; i++)
        {
            LOCKS[i] = new ReentrantLock();
        }
    }

    private StripedPathLocker()
    {
        throw new AssertionError("No instances");
    }

    private static ReentrantLock lockFor(Path path)
    {
        int hash = path.toAbsolutePath().normalize().hashCode();
        return LOCKS[hash & (STRIPES - 1)];
    }

    public static void runLockedAction(Path path, Consumer<Path> action)
    {
        runLockedFunction(
                path,
                p -> {
                    action.accept(p);
                    return null;
                }
        );
    }

    public static <T> T runLockedFunction(Path path, Function<Path, T> action)
    {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(action, "action");

        ReentrantLock lock = lockFor(path);
        lock.lock();
        try
        {
            return action.apply(path);
        }
        finally
        {
            lock.unlock();
        }
    }
}