package ru.itmo.server.serverNetManager;

import ru.itmo.lab.common.commonNet.Response;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ResponseSender
{
    public static void sendResponse( ObjectOutputStream outputStream, Response response ) throws IOException
    {
        if (outputStream == null || response == null )
            throw new IOException("Был сгенерирован пустой ответ");
        outputStream.writeObject(response);
        outputStream.flush();
        outputStream.reset();
    }
}
