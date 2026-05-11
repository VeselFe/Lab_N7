package ru.itmo.lab.common.myExceptions;

public class ConnectionException extends RuntimeException
{
    public ConnectionException( String mes )
    {
        super( mes );
    }
}
