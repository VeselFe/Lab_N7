package ru.itmo.server.manager.serverLogic;

import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;

/**
 * Команда для выхода из программы
 */
public class ExitCommand implements Command
{
    private String message;
    public ExitCommand()
    {
    }
    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        CommandProccessor.stopServerProgramm();
        message = "Подключение прекращено. Сервер Отключен.";
        LoggerFactory.getLogger(ExitCommand.class).info(message);
        return new CommandResult.Builder()
                .setSuccess( true )
                .setMessage( message )
                .buildCommandResult();
    }

    @Override
    public String getName()
    {
        return "exit";
    }
    @Override
    public String getDescription()
    {
        return "завершить программу";
    }
}
