package ru.itmo.client.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.commonNet.Response;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.ConnectionException;
import ru.itmo.lab.common.myExceptions.ResponseException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NetworkManager {
    private SocketChannel channel;
    private final ByteBuffer buffer = ByteBuffer.allocate(65536);

    public NetworkManager() {
    }

    public void setChannel(SocketChannel channel) throws IOException {
        this.channel = channel;
        channel.configureBlocking(false);
    }

    public void network(Request request) throws IOException {
        if (channel == null)
            throw new IOException("Сетевой канал не определен!");
        try {
            sendRequest(request);
        } catch (IOException e) {
            throw new ConnectionException("Сервер разрвал соединение.");
        }
    }

    private void sendRequest(Request request) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream)) {
            outputStream.writeObject(request);
            outputStream.flush();
        }

        byte[] data = byteOutputStream.toByteArray();
//        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
//        buffer.putInt(data.length);
//        buffer.put(data);
//        buffer.flip();
        ByteBuffer outputBuffer = ByteBuffer.wrap(data);

        while (outputBuffer.hasRemaining())
        {
            channel.write(outputBuffer);
        }
    }

    public Response getAuthenResponse() throws IOException {
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

    public String getServerResponse() throws IOException, ResponseException {
        try {
            Response serverResponse = recieveResponse();
            boolean success = serverResponse.isSuccess();
            String responseMessage = serverResponse.getMessage();
            List<StudyGroup> responeCollection = serverResponse.getCollection();
            StringBuilder printedCollection = new StringBuilder();
            if (serverResponse.getCollection() != null) {
                printedCollection.append("\nКоллекция после выполнения команды:\n");
                for (StudyGroup element : responeCollection) {
                    printedCollection.append(element.getInformation() + "\n");
                }
            }

            if (success) {
                return "Команда выполнена успешно:\n___________________\n" + responseMessage + "\n___________________\n" + printedCollection;
            } else {
                return "Возникла ошибка при выполнении команды:\n___________________\n" + responseMessage + "\n___________________\n" + printedCollection;
            }
        } catch (ClassNotFoundException e) {
            throw new ResponseException("Не обработался ответ: " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("Не обработался ответ: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseException("Неизвестная ошибка при обработке запроса: " + e.getMessage());
        }
    }

    private Response recieveResponse() throws IOException, ClassNotFoundException {
//        ByteBuffer buffer = ByteBuffer.allocate(4);
//        while (buffer.hasRemaining())
//        {
//            int bytes = channel.read(buffer);
//            checkBytes(bytes);
//        }
//        buffer.flip();
//        int objectLength = buffer.getInt();
//
//        ByteBuffer objectBuffer = ByteBuffer.allocate(objectLength);
//        while (objectBuffer.hasRemaining())
//        {
//            int bytesRead = channel.read(objectBuffer);
//            checkBytes(bytesRead);
//        }
//
//        objectBuffer.flip();
        buffer.clear();
        int ReadData;
        try {
            while ((ReadData = channel.read(buffer)) == 0) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectionException("Ожидание ответа прервано.");
        }
        checkBytes(ReadData);

        int attemptsWithoutData = 0;
        while (attemptsWithoutData < 3)
        {
            int dopBytes = channel.read(buffer);
            if (dopBytes > 0)
            {
                attemptsWithoutData = 0;
            }
            else if (dopBytes == 0)
            {
                try
                {
                    Thread.sleep(15); // Даем 15 мс сети на ожидание следующего TCP-пакета
                }
                catch( InterruptedException ignored ) {}
                attemptsWithoutData++;
            }
            else
            {
                break;
            }
        }
        checkBytes(ReadData);

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data); // data -> objectBuffer.array()
             ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream)) {
            return (Response) objectInputStream.readObject();
        }
    }

    private void checkBytes(int bytes) throws IOException {
        if (bytes == -1) {
            throw new IOException("Соединение разорвано сервером.");
        }
//        if( bytes == 0 )
//        {
//            try
//            {
//                Thread.sleep(50);
//            }
//            catch (InterruptedException e) {}
//        }
    }
}
