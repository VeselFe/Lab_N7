package ru.itmo.server;

import ru.itmo.lab.common.commonNet.Response;
import ru.itmo.server.dao.CollectionLoader;
import ru.itmo.server.dao.StudyGroupDAO;
import ru.itmo.server.dao.UserDAO;
import ru.itmo.server.db.DatabaseHandler;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.serverInterfaces.InvokerActions;
import ru.itmo.server.ioHandlers.ServerConsoleHandler;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.fileManagement.GroupsFileManager;
import ru.itmo.server.manager.collection.serverFileManagers.Launcher;
import ru.itmo.server.manager.serverLogic.*;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.server.serverNetManager.RequestReader;
import ru.itmo.server.serverNetManager.ResponseSender;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.itmo.server.сommand.*;


public class Server
{
    public static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int port = 6020;
    private static Invoker serverInvoker;
    private static StudyGroupDAO dbManager;
    /// Для гелиоса
    //private static String jdbcURL = "jdbc:postgresql://pg:5432/studs";
    /// Для отладки
    private static String jdbcURL = "jdbc:postgresql://localhost:2390/studs";

    public static void main(String[] args)
    {
        /// Серверный менеджер коллекцией
        Connection dbConnection = getDB_Connection();
        dbManager = new StudyGroupDAO(dbConnection);
        CollectionManager mainCollection = CollectionManager.createCollection( dbManager );

        try
        {
            dbManager.loadEnumIDs();
        }
        catch( SQLException e )
        {
            logger.error("Не удалось импортировать константы из БД.");
        }
        new CollectionLoader(mainCollection, dbManager).loadCollection();

        Invoker invoker = new Invoker();
        registerClientCommands(invoker, mainCollection);
        CommandProccessor mainProccessor = new CommandProccessor( invoker );

        serverInvoker = new Invoker();
        //serverInvoker.addCommand("save", new SaveCommand(mainCollection, "SavedCollection.txt"));
        serverInvoker.addCommand("exit", new ExitCommand());

        ServerConsoleHandler console = new ServerConsoleHandler();
//        GroupsFileManager.setErrorPrinter(console);
//        new Launcher(mainCollection, console).launchCollection();

        Thread handleServerTerminal = new Thread(() -> {
            ServerConsoleHandler terminal = new ServerConsoleHandler();
            logger.info("Серверный терминал запущен.");

            while( true )
            {
                String adminCommand = terminal.readline();
                logger.info("Получена команда от Администратора сервера: " + adminCommand);
                ExecuteResult serverCmdRes = serverInvoker.execute(new ServerCommandArgs(adminCommand.toLowerCase().trim()));
                if( serverCmdRes.isSuccess() )
                    logger.info(serverCmdRes.getMessage());
                else
                    logger.error("Ошибка обработки команды администратора: " + serverCmdRes.getMessage());
            }
        });
        handleServerTerminal.setDaemon(true);
        handleServerTerminal.start();

        /// Сеть
        logger.info("Сервер запустился. Порт: " + port);
        try( ServerSocket serverSocket = new ServerSocket(port) )
        {
            while( !mainProccessor.isProgrammFinished() )
            {
                logger.info("Ожидание подключения клиента...");
                Socket clientSocket = serverSocket.accept();
                if( mainProccessor.isProgrammFinished() )
                {
                    logger.info("Администратор завершил работу сервера.");
                    logger.info("Сохранение коллекции..");
                    serverInvoker.execute(new ServerCommandArgs("save"));
                    logger.info("Завершение работы сервера");
                    break;
                }
                logger.info("Клиент подключен: " + clientSocket.getInetAddress());
                handleClient(clientSocket, mainProccessor, dbConnection);
            }
        }
        catch( IOException e )
        {
            logger.error("Ошибка сервера: " + e.getMessage());
        }
    }

    public static void handleClient( Socket clientSocket, CommandProccessor proccessor, Connection dbConnection )
    {
        logger.info("Потоки ввода-вывода инициализированы.");
        CommandProccessor.restartServerProgramm();
        while( !clientSocket.isClosed() )
        {
            try
            {
                InputStream is = clientSocket.getInputStream();
                OutputStream os = clientSocket.getOutputStream();
                ObjectInputStream input = new ObjectInputStream(is);

                // логика обработки поступившей информации
                // читаем запрос
                Request request = RequestReader.read(input);
                logger.info("Получен запрос: " + request.getCommandType());

                // обрабатываем
                Response response = proccessor.ProcessRequest( request, new UserDAO(dbConnection) );

                logger.info("Запрос обработан!");
                logger.info("<Начало запроса>");
                logger.info("Success: " + response.isSuccess() + ";");
                logger.info("Message: " + response.getMessage() + ";");
                logger.info("<Конец запроса>");

                ObjectOutputStream output = new ObjectOutputStream(os);
                // отправляем обратно ответ
                ResponseSender.sendResponse(output, response);
                logger.info("Ответ отправлен!\n");
                break;
            }
            catch( ClassNotFoundException e )
            {
                logger.error("Некорректные полученные данные");
            }
            catch( EOFException e )
            {
                logger.info("Клиент завершил сессию.");
                break;
            }
            catch( SocketException e )
            {
                logger.error("Соединение с клиентом потеряно.");
                break;
            }
            catch( IOException e )
            {
                logger.error("Ошибка потоков ввода-вывода: " + e.getMessage());
                break;
            }
            catch ( Exception e )
            {
                logger.error("Неизвестная ошибка при обработке запроса: " + e.getMessage());
            }
        }
        try
        {
            if (!clientSocket.isClosed())
            {
                clientSocket.close();
            }
        }
        catch (IOException e)
        {
            logger.error("Ошибка при закрытии сокета.");
        }
        logger.info("Сессия завершена!\n");
    }
    public static Connection getDB_Connection()
    {
        Scanner credentials = null;
        String username = null;
        String password = null;
        try
        {
            credentials = new Scanner(new FileReader("credentials.txt"));
        }
        catch (FileNotFoundException e) {
            System.err.println("He найден credentials.txt с данными для входа в базу данных.");
            System.exit(-1);
        }
        try
        {
            username = credentials.nextLine().trim();
            password = credentials.nextLine().trim();
        }
        catch( NullPointerException e )
        {
            System.err.println("Не определен сканер файла!");
        }
        catch( NoSuchElementException e )
        {
            System.err.println("Не найдены данные для входа в файле. Завершение работы.");
            System.exit(-1);
        }

        DatabaseHandler database = new DatabaseHandler(jdbcURL, username, password);
        Connection dbConnection = database.connectToDatabase();
        return dbConnection;
    }

    public static void registerClientCommands( InvokerActions invoker, CollectionManager manager )
    {
        /// регистрируем все команды
        invoker.addCommand("help", new HelpCommand(invoker));
        invoker.addCommand("info", new InfoCommand(manager));
        invoker.addCommand("show", new ShowCommand(manager));
        invoker.addCommand("insert_element", new InsertElCommand(manager));
        invoker.addCommand("update_id", new UpdateIdCommand(manager));
        invoker.addCommand("remove_key", new RemoveCommand(manager));
        invoker.addCommand("clear", new ClearCommand(manager));
        invoker.addCommand("remove_greater", new RemoveGreater(manager));
        invoker.addCommand("remove_lower", new RemoveLower(manager));
        invoker.addCommand("remove_lower_key", new RomoveLowerKey(manager));
        invoker.addCommand("filter_by_semester_enum", new FilterBySemesterEnum(manager));
        invoker.addCommand("filter_starts_with_name", new FilterStartsWithName(manager));
        invoker.addCommand("print_ascending", new PrintAscending(manager));
    }
}
