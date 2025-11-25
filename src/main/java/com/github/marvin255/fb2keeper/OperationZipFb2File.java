package com.github.marvin255.fb2keeper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class OperationZipFb2File implements Function<Fb2KeeperOperationContext, Fb2KeeperOperationContext>
{
    @Override
    public Fb2KeeperOperationContext apply(Fb2KeeperOperationContext fb2KeeperOperationContext)
    {
        Path file = FileSystemHelper.checkAndReturnFile(fb2KeeperOperationContext.path());
        Path target = FileSystemHelper.checkAndReturnDir(fb2KeeperOperationContext.target());
        Path archive = getArchivePath(target, file);

        zipFile(file, archive);

        return fb2KeeperOperationContext.withPath(archive);
    }

    private static Path getArchivePath(Path target, Path file)
    {
        return Paths.get(
                target.toAbsolutePath().toString(),
                FileSystemHelper.getFileNameWithoutExtension(file)
                        + "." + FileSystemHelper.getFB2ZipExtension()
        );
    }

    private void zipFile(Path file, Path archive)
    {
        try (
                FileOutputStream fos = new FileOutputStream(archive.toAbsolutePath().toString());
                ZipOutputStream zos = new ZipOutputStream(fos);
                FileInputStream fis = new FileInputStream(file.toAbsolutePath().toString())
        )
        {

            ZipEntry zipEntry = new ZipEntry(FileSystemHelper.getFileNameWithoutExtension(file));
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0)
            {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();

            Files.delete(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
