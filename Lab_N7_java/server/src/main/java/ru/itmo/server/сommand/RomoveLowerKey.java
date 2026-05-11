package ru.itmo.server.сommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;

import java.util.List;
import java.util.Map;


/**
 * Команда для удаления элементов коллекции с меньшим ключом
 */
public class RomoveLowerKey implements Command
{
    private final Logger logger = LoggerFactory.getLogger(RomoveLowerKey.class);
    private final CollectionManager collection;
    private String errorMessage;
    private Long Key;

    public RomoveLowerKey( CollectionManager newCollection )
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
            errorMessage = "Некорректные данные для ключа";
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }

        long count = 0;
        try
        {
            List< Map.Entry<Long, StudyGroup> > removingList = collection.getStudyGroups().entrySet().stream()
                    .filter(element -> element.getKey() < Key)
                    .toList();
            CommandResult.Builder resBuilder = new CommandResult.Builder().setSuccess( true );
            if( removingList.isEmpty() )
                resBuilder.setMessage("Совпадений не найдено!");
            else
            {
                StringBuilder deletedNames = new StringBuilder();
                for( var element : removingList )
                {
                    count++;
                    deletedNames.append("\n" + count + ") " + element.getValue().getName());
                    deletedNames.append("   'id' = " + element.getKey());
                }
                removingList.forEach(element -> collection.getStudyGroups().remove(element.getKey()));
                resBuilder.setMessage("Было обнаружено и удалено " + removingList.size() + " элемента(-ов): " + deletedNames);
            }
            return resBuilder.setSortedCollection(collection.getSortedByNameCollection())
                    .buildCommandResult();
        }
        catch( Exception e )
        {
            errorMessage = "ошибка при удалении элементов с меньшим id " + e.getMessage();
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
    }

    @Override
    public String getName()
    {
        return "remove_lower_key { key }";
    }
    @Override
    public String getDescription()
    {
        return "удалить из коллекции все элементы, ключ которых меньше, чем заданный";
    }
}
