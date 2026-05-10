package ru.itmo.server.сommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.server.manager.collection.StudyGroupByStudentsComparator;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;

import java.util.List;
import java.util.Map;

/**
 * Команда для удаления элементов коллекции с меньшим количеством студентов
 */
public class RemoveLower implements Command
{
    private final Logger logger = LoggerFactory.getLogger(RemoveLower.class);
    private final CollectionManager collection;
    private String errorMessage;
    private Long Key;
    private StudyGroup choosenGroup;

    public RemoveLower( CollectionManager newCollection )
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        try
        {
            Key = Long.valueOf( args.getKey() );
            choosenGroup = collection.getStudyGroups().get(Key);
        }
        catch (IllegalArgumentException e)
        {
            errorMessage = "Неверно введен параметр: по данному ключу ничего не найдено";
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        if( choosenGroup == null )
        {
            errorMessage = "По данному ключу не найдено элементов!";
            logger.error(errorMessage);
            throw  new CommandException(errorMessage);
        }

        StudyGroupByStudentsComparator comparator = new StudyGroupByStudentsComparator();

        List<Long> removingList = collection.getStudyGroups().entrySet().stream()
                .filter(element -> comparator.compare(element.getValue(), choosenGroup) < 0)
                .map(Map.Entry::getKey)
                .toList();
        long count = removingList.size();
        removingList.forEach(key -> collection.getStudyGroups().remove(key));

        CommandResult.Builder resBuilder = new CommandResult.Builder().setSuccess( true );
        if( count == 0 )
        {
            resBuilder.setMessage("Совпадений не найдено!");
        }
        else
        {
            resBuilder.setMessage("Было найдено и удалено " + count + " элементов с меньшим количеством.");
        }
        return  resBuilder.setSortedCollection(collection.getSortedByNameCollection())
                          .buildCommandResult();
    }
    @Override
    public String getName()
    {
        return "remove_lower { key }";
    }
    @Override
    public String getDescription()
    {
        return "удалить из коллекции все элементы, у которых количество студентов меньше, чем в заданном";
    }
}
