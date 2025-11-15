package com.github.marvin255.fb2keeper;

public class Fb2KeeperException extends Exception
{
    public Fb2KeeperException(String fileProcessingFailed, Exception e)
    {
        super(fileProcessingFailed, e);
    }
}
