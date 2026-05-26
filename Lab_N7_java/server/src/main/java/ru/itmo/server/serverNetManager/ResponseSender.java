package ru.itmo.server.serverNetManager;

import ru.itmo.lab.common.commonNet.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ResponseSender
{
    private static byte[] responseBytes;

    public static void sendResponse( OutputStream outputStream, Response response ) throws IOException
    {
        if( outputStream == null )
            throw new IOException("Не определен поток вывода");
        if( response == null )
            throw new IOException("Был сгенерирован пустой ответ");

        try( ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream) )
        {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            responseBytes = byteOutputStream.toByteArray();
        }

        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(responseBytes.length);

        outputStream.write(sizeBuffer.array());
        outputStream.write(responseBytes);
        outputStream.flush();
    }

    public static int getResponseLength()
    {
        return responseBytes.length;
    }
}
