package ru.itmo.client;

import ru.itmo.client.clientTerminal.ClientConsoleHandler;
import ru.itmo.client.clientTerminal.AuthManager;
import ru.itmo.client.network.NetworkManager;
import ru.itmo.lab.common.commonNet.Request;

import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.myExceptions.ConnectionException;
import ru.itmo.lab.common.myExceptions.ResponseException;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client
{
    private static final String host = "localhost";
    private static final int port = 6020;
    private static final int connectionDelay = 5000;

    public static void main(String[] args)
    {
        boolean startClient = true;
        boolean authenticated = false;
        ClientConsoleHandler console = new ClientConsoleHandler();
        NetworkManager networkManager = new NetworkManager();

        while( startClient )
        {
            try( SocketChannel channel = connectToServer(console) )
            {
                networkManager.setChannel( channel );

                while( !authenticated && startClient )
                {
                    AuthManager authManager = new AuthManager(console, networkManager);
                    boolean shouldRetry = authManager.authenticate();
                    if( !shouldRetry )
                    {
                        if( authManager.getLogin() == null )
                        {
                            console.printInfo("Приложение завершило работу.");
                            startClient = false;
                        }
                        else
                        {
                            console.initRequestCreator( console, authManager.getLogin(), authManager.getPassword() );
                            console.setUser(authManager.getLogin());
                            authenticated = true;
                        }
                    }
                }
                if( startClient )
                {
                    processClient(console, networkManager);
                    startClient = false;
                }
            }
            catch( ConnectionException e )
            {
                console.printError("Ошибка соединения: " + e.getMessage());
            }
            catch( IOException e )
            {
                console.printError("Ошибка подключения: " + e.getMessage());
            }
            catch( Exception e )
            {
                console.printError("Неизвестная ошибка: " + e.getMessage());
                startClient = false;
            }
        }
        console.close();
    }

    private static void processClient( ClientConsoleHandler console, NetworkManager networkManager ) throws IOException, ConnectionException
    {
        boolean exit = false;
        console.welcomMessage();

        while( !exit )
        {
            Request request = console.createRequest();
            try
            {
                if (request != null)
                {
                    networkManager.network(request);
                    String serverResponse = networkManager.getServerResponse();
                    if (!request.getCommandType().equals("exit"))
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
            catch (ResponseException e)
            {
                console.printError(e.getMessage());
            }
            catch (Exception e)
            {
                throw new IOException("ошибка при попытке отправки запроса на сервер. " + e.getMessage());
            }

            if (exit) break;
        }
    }

    private static SocketChannel connectToServer( IO_Handler console )
    {
        SocketChannel socketChannel = null;
        boolean conection = false;

        console.printInfo("Клиент подключен к серверу " + host + ":" + port);

        while( !conection )
        {
            try
            {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(true);
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