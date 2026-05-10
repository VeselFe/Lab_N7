package ru.itmo.server.сommand;

import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;

/**
 * Команда для вывода элементов коллекции, отсортировав по началу названия элементов
 */
public class FilterStartsWithName implements Command
{
    private final CollectionManager collection;
    private String tempName;

    public FilterStartsWithName( CollectionManager newCollection )
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        tempName = args.getStringArg();
        if( tempName == null || tempName.isEmpty() )
        {
            String errorMessage = "Введеная подстрока пустая!";
            LoggerFactory.getLogger(FilterStartsWithName.class).error(errorMessage);
            throw new CommandException(errorMessage);
        }

        var filteredGroups = collection.getStudyGroups().entrySet().stream()
                .filter(entry -> entry.getValue().getName().startsWith(tempName))
                .toList();
        StringBuilder result = new StringBuilder();
        filteredGroups.forEach(group -> {
            result.append(group.getKey() + ": " + group.getValue().getName());
            result.append("Подробная информация о " + group.getKey() + "-ом элеменете коллеции:\n" + group.getValue().getInformation());
        });
        if( filteredGroups.isEmpty() )
        {
            result.append("Не найдено ни одного элемента с именем, начинающимся со строки '" + tempName + "'!");
        }

        return new CommandResult.Builder()
                .setSuccess( true )
                .setMessage( result.toString() )
                .buildCommandResult();
    }
    @Override
    public String getName()
    {
        return "filter_starts_with_name { name }";
    }
    @Override
    public String getDescription()
    {
        return "вывести элементы, значение поля name которых начинается с заданной подстроки";
    }
}
