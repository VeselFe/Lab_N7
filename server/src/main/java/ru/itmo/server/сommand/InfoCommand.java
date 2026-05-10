package ru.itmo.server.сommand;

import org.slf4j.LoggerFactory;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.myExceptions.CommandException;

/**
 * Команда для вывода информации о коллекции
 */
public class InfoCommand implements Command
{
    private CollectionManager CollInfo;
    public InfoCommand( CollectionManager collection )
    {
        CollInfo = collection;
    }

    @Override
    public ExecuteResult execute( CommandArgs args )
    {
        if( CollInfo == null )
        {
            String errorMessage = "Некорректные данные для ключа";
            LoggerFactory.getLogger(InfoCommand.class).error(errorMessage);
            throw new CommandException(errorMessage);
        }
        return new CommandResult.Builder()
                .setSuccess( true )
                .setMessage( CollInfo.getInfo() )
                .buildCommandResult();
    }
    @Override
    public String getName()
    {
        return "info";
    }
    @Override
    public String getDescription()
    {
        return "вывести информацию о коллекции";
    }
}
