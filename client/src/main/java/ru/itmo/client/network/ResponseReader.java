package ru.itmo.client.network;

import ru.itmo.lab.common.commonNet.Response;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ResponseReader
{
    public static Response read( ObjectInputStream inputStream ) throws IOException, ClassNotFoundException
    {
        Object serverObject = inputStream.readObject();
        if( serverObject == null )
        {
            throw new IOException("Получен пустой пакет!");
        }
        return (Response) serverObject;
    }
}
