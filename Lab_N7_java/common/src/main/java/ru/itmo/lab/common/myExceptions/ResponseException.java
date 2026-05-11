package ru.itmo.lab.common.myExceptions;

public class ResponseException extends RuntimeException
{
    public ResponseException( String message )
    {
        super("Ошибка при обработке ответа сервера: " + message);
    }
}
