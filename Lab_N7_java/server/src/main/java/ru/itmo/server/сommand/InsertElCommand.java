package ru.itmo.server.сommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CreationException;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.serverInterfaces.StudyGroupDAI;

import java.sql.SQLException;

/**
 * Команда для добавления нового элемента в коллекции
 */
public class InsertElCommand implements Command
{
    private Logger logger = LoggerFactory.getLogger(InsertElCommand.class);
    private final CollectionManager collection;

    public InsertElCommand(CollectionManager newCollection)
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute( CommandArgs args )
    {
        Long Key;
        try
        {
            Key = Long.valueOf( args.getKey() );
        }
        catch( NumberFormatException e )
        {
            String errorMessage = "Некорректные данные для ключа";
            logger.error( errorMessage );
            throw new CreationException(errorMessage);
        }
        if( Key == null )
        {
            String errorMessage = "Ключ не определен";
            logger.error( errorMessage );
            throw new CreationException(errorMessage);
        }
        if( collection.getStudyGroups().get(Key) != null )
        {
            String errorMessage = "Элемент с таким ключем уже существует";
            logger.error( errorMessage );
            throw new CreationException(errorMessage);
        }
        try
        {
            StudyGroup newGroup = args.getGroup();

            newGroup.setOwnerID(args.getOwnerID());
            collection.addElement(Key, newGroup, args.getOwnerID());
            logger.info( "Колекция: Добавлен новый элемент!" );
            return new CommandResult.Builder()
                    .setSuccess( true )
                    .setMessage( "Колекция: Добавлен новый элемент!" )
                    .buildCommandResult();
        }
        catch( Exception e )
        {
            String errorMessage = "Ошибка при создании группы:\n" + e.getMessage();
            logger.error( errorMessage );
            throw new CreationException(errorMessage);
        }
    }

    @Override
    public String getName()
    {
        return "insert_element";
    }
    @Override
    public String getDescription()
    {
        return "добавить новый элемент с заданным ключом";
    }
}
