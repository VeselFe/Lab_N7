package ru.itmo.client;

import ru.itmo.client.clientTerminal.ClientConsoleHandler;
import ru.itmo.client.network.NetworkManager;
import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.commonNet.Response;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.myExceptions.ConnectionException;
import ru.itmo.lab.common.myExceptions.ResponseException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Stack;

public class Client
{
    private static final String host = "localhost";
    private static final int port = 6060;
    private static final int connectionDelay = 5000;

    public static void main(String[] args)
    {
        ClientConsoleHandler console = new ClientConsoleHandler();
        console.initRequestCreator( console );

        boolean exit = false;
        while( !exit )
        {
            try( SocketChannel channel = connectToServer() )
            {
                if (channel != null)
                {
                    NetworkManager networkManager = new NetworkManager(channel);
                    console.welcomMessage();

                    Request request = console.createRequest();
                    try
                    {
                        if( request != null )
                        {
                            networkManager.network(request);
                            String serverResponse = networkManager.getServerResponse();
                            if( !request.getCommandType().equals("exit") )
                            {
                                console.printInfo(serverResponse);
                            }
                            else
                            {
                                console.printInfo("Соединение успешно завершено!");
                                exit = true;
                            }
                        }
                        else
                        {
                            console.printError("Ошибка генерации запроса. Запрос не отправлен!");
                        }
                    }
                    catch( ConnectionException e )
                    {
                        throw new ConnectionException(e.getMessage());
                    }
                    catch( ResponseException e )
                    {
                        console.printError(e.getMessage());
                    }
                    catch( Exception e )
                    {
                        console.printError("ошибка при попытке отправки запроса на сервер. " + e.getMessage());
                    }

                    if (exit) break;
                }
            }
            catch( Exception e )
            {
                System.out.println("Ошибка при работе приложения: " + e.getMessage());
            }
        }
        console.close();
    }

    private static SocketChannel connectToServer()
    {
        SocketChannel socketChannel = null;
        boolean conection = false;
        ClientConsoleHandler console = new ClientConsoleHandler();
        console.printInfo("Клиент подключен к серверу " + host + ":" + port);

        while( !conection )
        {
            try
            {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect( new InetSocketAddress(host, port) );

                while( !socketChannel.finishConnect() )
                {
                    console.printRequest(".");
                    Thread.sleep(500);
                }
                conection = true;
                console.printInfo("Успешно подключено к серверу!");
            }
            catch( IOException e )
            {
                console.printError("Сервер недоступен. Повторная попытка через " + (connectionDelay / 1000) + " секунд...");
                try
                {
                    if( socketChannel != null ) { socketChannel.close(); }
                    Thread.sleep(connectionDelay);
                }
                catch(InterruptedException | IOException exception )
                {
                    console.printError("Ошибка при переподключении");
                    break;
                }
            }
            catch( InterruptedException e )
            {
                console.printError("Подключение прервано");
                break;
            }
        }
        return socketChannel;
    }
}