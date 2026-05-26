package ru.itmo.server.serverNetManager;

import ru.itmo.lab.common.commonNet.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RequestReader
{
    public static Request read( byte[] requestBytes ) throws IOException, ClassNotFoundException
    {
        try( ByteArrayInputStream byteInputStream = new ByteArrayInputStream(requestBytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream) )
        {
            return (Request) objectInputStream.readObject();
        }
    }
}
