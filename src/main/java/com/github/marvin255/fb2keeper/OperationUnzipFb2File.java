package com.github.marvin255.fb2keeper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class OperationUnzipFb2File implements Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>
{
    @Override
    public Fb2KeeperOperationContext apply(Fb2KeeperOperationContext fb2KeeperOperationContext)
    {
        Path file = FileSystemHelper.checkAndReturnFile(fb2KeeperOperationContext.path());
        if (!FileSystemHelper.isFB2Zip(file))
        {
            return fb2KeeperOperationContext;
        }

        Path target = FileSystemHelper.checkAndReturnDir(fb2KeeperOperationContext.target());

        Path unzipPath;
        try
        {
            unzipPath = unzip(file, target);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return fb2KeeperOperationContext.withPath(unzipPath);
    }

    private Path unzip(Path zipFile, Path targetDir) throws IOException
    {
        Path unzippedFb2Path = null;

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile)))
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                Path newPath = targetDir.resolve(entry.getName()).normalize();
                if (!newPath.startsWith(targetDir))
                {
                    throw new IOException("Entry is outside target dir: " + entry.getName());
                }
                if (!entry.isDirectory() && FileSystemHelper.isFB2(newPath))
                {
                    unzippedFb2Path = newPath;
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    zis.closeEntry();
                    break;
                }
                zis.closeEntry();
            }
        }

        if (unzippedFb2Path == null)
        {
            throw new RuntimeException(
                    "Archive '%s' doesn't have fb2 file".formatted(zipFile.toAbsolutePath().toString())
            );
        }

        Files.delete(zipFile);

        return unzippedFb2Path;
    }
}
