package ru.itmo.lab.common.myExceptions;

public class FileManagerException extends RuntimeException
{
    public FileManagerException( String errMessage )
    {
        super("Ошибка при работе с файлом: " + errMessage);
    }
}
