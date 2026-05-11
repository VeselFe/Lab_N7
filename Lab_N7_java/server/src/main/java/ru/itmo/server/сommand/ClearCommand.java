package ru.itmo.server.сommand;

import org.slf4j.LoggerFactory;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;

/**
 * Команда для очистки коллекции
 */
public class ClearCommand implements Command
{
    private CollectionManager collection;
    public ClearCommand( CollectionManager newCollection )
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute( CommandArgs args )
    {
        try
        {
            collection.clearCollection();
            return new CommandResult.Builder()
                    .setSuccess( true )
                    .setMessage("Коллекция успешно очищена!")
                    .buildCommandResult();
        }
        catch( Exception e )
        {
            String errorMessage = "Возникла ошибка при попытке очистить коллекцию: " + e.getMessage();
            LoggerFactory.getLogger(ClearCommand.class).error(errorMessage);
            throw new CommandException(errorMessage);
        }
    }
    @Override
    public String getName() { return "clear"; }
    @Override
    public String getDescription()
    {
        return "очистить коллекцию";
    }
}
