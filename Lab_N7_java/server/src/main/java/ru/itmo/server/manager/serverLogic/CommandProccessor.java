package ru.itmo.server.manager.serverLogic;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.commonNet.Response;
import ru.itmo.server.dao.UserDAO;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.serverInterfaces.InvokerActions;
import ru.itmo.server.serverInterfaces.UserDAI;

import java.sql.SQLException;

public class CommandProccessor
{
    public static final Logger logger = LoggerFactory.getLogger(CommandProccessor.class);
    private static boolean exit = false;
    private InvokerActions invoker;

    public CommandProccessor( InvokerActions invoker )
    {
        this.invoker = invoker;
    }

    public Response ProcessRequest( Request clientRequest, UserDAI userDAO )
    {
        try
        {
            logger.debug("Получен запрос");
            logger.debug(clientRequest.toString());
            if(clientRequest.getCommandType().equals("register"))
            {
                long res = userDAO.registerUser(clientRequest.getLogin(), clientRequest.getPassword());
                return new Response.Builder()
                        .setSuccess(true)
                        .setMessage("Пользователь успешно зарегистрирован.")
                        .buildResponse();
            }
            else if( clientRequest.getCommandType().equals("login") )
            {
                long res = userDAO.authenticateUser(clientRequest.getLogin(), clientRequest.getPassword());
                return new Response.Builder()
                        .setSuccess(true)
                        .setMessage("Пользователь успешно авторизован.")
                        .buildResponse();
            }
            else
            {
                userDAO.authenticateUser(clientRequest.getLogin(), clientRequest.getPassword());
                ExecuteResult result = invoker.execute(new RequestAdapter( clientRequest ));
                return new Response.Builder()
                        .setSuccess(result.isSuccess())
                        .setMessage(result.getMessage())
                        .setSortedCollection(result.getCollection())
                        .buildResponse();
            }
        }
        catch( SQLException e )
        {
            logger.warn(e.getMessage());
            return Response.builder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .buildResponse();
        }
        catch( Exception e )
        {
            logger.error(e.getMessage());
            return Response.builder()
                    .setSuccess(false)
                    .setMessage("Ошибка при конвертации данных: " + e.getMessage() + "\n")
                    .buildResponse();
        }
    }
    public static void restartServerProgramm() { exit = false; }
    public static void stopServerProgramm() { exit = true; }
    public boolean isProgrammFinished()
    {
        return exit;
    }
}
