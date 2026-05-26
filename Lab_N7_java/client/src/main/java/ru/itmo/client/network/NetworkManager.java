package ru.itmo.client.network;

import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.commonNet.Response;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.ConnectionException;
import ru.itmo.lab.common.myExceptions.ResponseException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class NetworkManager
{
    private SocketChannel channel;

    public NetworkManager() {}

    public void setChannel( SocketChannel channel ) throws IOException
    {
        this.channel = channel;
        channel.configureBlocking(false);
    }

    public void network( Request request ) throws IOException
    {
        if( channel == null )
            throw new IOException("Сетевой канал не определен!");
        try
        {
            sendRequest(request);
        }
        catch (IOException e)
        {
            throw new ConnectionException("Сервер разрвал соединение.");
        }
    }

    private void sendRequest( Request request ) throws IOException
    {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try( ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream) )
        {
            outputStream.writeObject(request);
            outputStream.flush();
        }

        byte[] data = byteOutputStream.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();

        while (buffer.hasRemaining())
        {
            channel.write(buffer);
        }
    }

    public Response getAuthenResponse() throws IOException
    {
        try
        {
            Response serverResponse = recieveResponse();
            return serverResponse;
        }
        catch( IOException e )
        {
            throw new IOException("Не обработался ответ авторизации: " + e.getMessage());
        }
        catch( Exception e )
        {
            throw new ResponseException("Неизвестная ошибка при обработке запроса: " + e.getMessage());
        }
    }

    public String getServerResponse() throws IOException, ResponseException
    {
        try
        {
            Response serverResponse = recieveResponse();
            boolean success = serverResponse.isSuccess();
            String responseMessage = serverResponse.getMessage();
            List<StudyGroup> responeCollection = serverResponse.getCollection();
            StringBuilder printedCollection = new StringBuilder();
            if( serverResponse.getCollection() != null )
            {
                printedCollection.append("\nКоллекция после выполнения команды:\n");
                for (StudyGroup element : responeCollection)
                {
                    printedCollection.append(element.getInformation() + "\n");
                }
            }

            if (success)
            {
                return "Команда выполнена успешно:\n___________________\n" + responseMessage + "\n___________________\n" + printedCollection;
            }
            else
            {
                return "Возникла ошибка при выполнении команды:\n___________________\n" + responseMessage + "\n___________________\n" + printedCollection;
            }
        }
        catch( ClassNotFoundException e )
        {
            throw new ResponseException("Не обработался ответ: " + e.getMessage());
        }
        catch( IOException e )
        {
            throw new IOException("Не обработался ответ: " + e.getMessage());
        }
        catch( Exception e )
        {
            throw new ResponseException("Неизвестная ошибка при обработке запроса: " + e.getMessage());
        }
    }

    private Response recieveResponse() throws IOException, ClassNotFoundException
    {
        ByteBuffer bufferLength = ByteBuffer.allocate(4);
        while( bufferLength.hasRemaining() )
        {
            int bytes = channel.read(bufferLength);
            checkBytes(bytes);
        }
        bufferLength.flip();
        int objectLength = bufferLength.getInt();

        ByteBuffer objectBuffer = ByteBuffer.allocate(objectLength);
        while( objectBuffer.hasRemaining() )
        {
            int bytesRead = channel.read(objectBuffer);
            checkBytes(bytesRead);
        }

        objectBuffer.flip();
        byte[] data = new byte[objectBuffer.remaining()];
        objectBuffer.get(data);

        try ( ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
              ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream) )
        {
            return (Response) objectInputStream.readObject();
        }
    }

    private void checkBytes(int bytes) throws IOException
    {
        if( bytes == -1 )
        {
            throw new IOException("Соединение разорвано сервером.");
        }
        if( bytes == 0 )
        {
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {}
        }
    }
}
