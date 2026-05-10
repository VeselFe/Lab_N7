package ru.itmo.server.сommand;

import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.StudyGroup;

import java.util.List;

/**
 * Команда для отсортированного по названию вывода всех элементов коллекции
 */
public class PrintAscending implements Command
{
    private CollectionManager collectionManager;
    public PrintAscending( CollectionManager newCollectionManager )
    {
        collectionManager = newCollectionManager;
    }

    @Override
    public ExecuteResult execute( CommandArgs args )
    {
        StringBuilder result = new StringBuilder();
        result.append("Элементы коллекции в отсортированном порядке: ");
        result.append("***************************************\n");
        List<StudyGroup> sortedGroups = collectionManager.getSortedCollection().stream()
                .toList();
        sortedGroups.forEach(group -> result.append(group.getInformation()));

        if( sortedGroups.isEmpty() )
        {
            result.append("В колекции отсутствуют элементы!");
        }
        result.append("\n***************************************\n");
        return new CommandResult.Builder()
                .setSuccess( true )
                .setMessage( result.toString() )
                .buildCommandResult();
    }
    @Override
    public String getName()
    {
        return "print_ascending";
    }
    @Override
    public String getDescription()
    {
        return "вывести элементы коллекции в порядке возрастания (по id)";
    }

}
