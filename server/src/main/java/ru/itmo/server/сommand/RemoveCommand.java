package ru.itmo.server.сommand;

import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;

/**
 * Команда для удаления элемента коллекции по ключу
 */
public class RemoveCommand implements Command
{
    private final CollectionManager collection;
    private Long Key;

    public RemoveCommand( CollectionManager newCollection )
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        try
        {
            Key = Long.valueOf( args.getKey() );
        }
        catch( NumberFormatException e )
        {
            String errorMessage = "Некорректные данные для ключа";
            LoggerFactory.getLogger(RemoveCommand.class).error(errorMessage);
            throw new CommandException(errorMessage);
        }
        collection.removeElement(Key);
        return new CommandResult.Builder()
                .setSuccess( true )
                .setMessage("Элемент успешно удален")
                .setSortedCollection(collection.getSortedByNameCollection())
                .buildCommandResult();
    }
    @Override
    public String getName()
    {
        return "remove_key { key }";
    }
    @Override
    public String getDescription()
    {
        return "удалить элемент коллекции по его ключу";
    }
}
