package ru.itmo.server.сommand;

import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.StudyGroup;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


/**
 * Команда вывода всех элементов коллекции в коносль
 */
public class ShowCommand implements Command
{
    private Hashtable<Long, StudyGroup> collection;
    public ShowCommand( CollectionManager newCollection )
    {
        collection = newCollection.getStudyGroups();
    }

    @Override
    public ExecuteResult execute( CommandArgs args )
    {
        StringBuilder res = new StringBuilder();
        res.append("Элементы коллекции: ");
        res.append("***************************************");
        Set<Map.Entry<Long, StudyGroup>> collectionLikeSet = collection.entrySet();
        if( collectionLikeSet.isEmpty() )
        {
            res.append("В колекции отсутствуют элементы!");
        }
        else
        {
            collectionLikeSet.stream().forEach(element -> {
                res.append("\n\n" + element.getKey() + ": " + element.getValue().getName() + "\n");
                res.append("Подробная информация о " + element.getKey() + "-ом элеменете коллеции:\n" + element.getValue().getInformation());
            });
        }
        res.append("\n***************************************\n");
        return new CommandResult.Builder()
                .setSuccess( true )
                .setMessage(res.toString())
                .buildCommandResult();
    }
    @Override
    public String getName()
    {
        return "show";
    }
    @Override
    public String getDescription()
    {
        return "вывести все элементы коллекции в строковом представлении";
    }

}
