package ru.itmo.server.serverNetManager;

import ru.itmo.lab.common.commonNet.Request;

import java.io.IOException;
import java.io.ObjectInputStream;

public class RequestReader
{
    public static Request read(ObjectInputStream inputStream ) throws IOException, ClassNotFoundException
    {
        Object clientObject = inputStream.readObject();
        if( clientObject == null )
        {
            throw new IOException("Получен пустой пакет!");
        }
        return (Request) clientObject;
    }
}
