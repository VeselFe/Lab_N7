package ru.itmo.client.network;

import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.commonNet.Response;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.ConnectionException;
import ru.itmo.lab.common.myExceptions.ResponseException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.List;

public class NetworkManager
{
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private SocketChannel channel;

    public void setChannel(SocketChannel channel) throws IOException
    {
        this.channel = channel;
        this.outputStream = new ObjectOutputStream(Channels.newOutputStream(channel));
        this.outputStream.flush();

        this.inputStream = new ObjectInputStream(Channels.newInputStream(channel));
    }

    public NetworkManager() {}

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
        outputStream.writeObject(request);
        outputStream.flush();
        outputStream.reset();
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
        return ResponseReader.read(this.inputStream);
    }
}
