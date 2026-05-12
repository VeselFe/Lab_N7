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
    private final SocketChannel channel;
    private final ByteBuffer buffer = ByteBuffer.allocate(65536);

    public NetworkManager( SocketChannel channel )
    {
        this.channel = channel;
    }

    public void network( Request request ) throws IOException
    {
        try
        {
            sendRequest( request );
        }
        catch( IOException e )
        {
            throw new ConnectionException("Сервер разрвал соединение.");
        }
    }

    private void sendRequest( Request request ) throws IOException
    {
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream( byteOS );
        objectOS.writeObject( request );
        objectOS.flush();

        byte[] data = byteOS.toByteArray();
        ByteBuffer outBuffer = ByteBuffer.wrap(data);

        while( outBuffer.hasRemaining() )
        {
            channel.write( outBuffer );
        }
    }

    public Response getAuthenResponse() throws IOException
    {
        try
        {
            Response serverResponse = recieveResponse();
            return serverResponse;
        }
        catch(IOException e)
        {
            throw new IOException("Не обработался ответ: " + e.getMessage());
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

            if( success )
            {
                return "Команда выполнена успешно:\n___________________\n" + responseMessage + "\n___________________\n" + printedCollection;
            }
            else
            {
                return "Возникла ошибка при выполнении команды:\n___________________\n" + responseMessage + "\n___________________\n" + printedCollection;
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new ResponseException("Не обработался ответ: " + e.getMessage());
        }
        catch(IOException e)
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
        buffer.clear();
        int bytes;

        try
        {
            while( (bytes = channel.read(buffer)) == 0 )
            {
                System.out.println("Ожидание ответа сервера..");
                Thread.sleep(500);
            }
        }
        catch( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new ConnectionException("Ожидание ответа прерывано.");
        }

        if( bytes == -1 )
            throw new ConnectionException("Сервер разорвал соединение");

        buffer.flip();

        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        try( ByteArrayInputStream byteIStream = new ByteArrayInputStream( data );
             ObjectInputStream objectIStream = new ObjectInputStream( byteIStream ) )
        {
            return ResponseReader.read( objectIStream );
        }
    }
}
