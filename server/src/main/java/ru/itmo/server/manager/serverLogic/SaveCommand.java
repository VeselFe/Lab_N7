package ru.itmo.server.manager.serverLogic;

import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.fileManagement.GroupsFileManager;
import ru.itmo.lab.common.myExceptions.FileManagerException;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;


/**
 * Команда для сохранении коллекции в файл
 */
public class SaveCommand implements Command
{
    private String message;
    private final CollectionManager collection;
    private final String fileName;
    private final GroupsFileManager fileManager;

    public SaveCommand( CollectionManager newCollection, String newFileName )
    {
        collection = newCollection;
        fileName = newFileName;
        fileManager = new GroupsFileManager( fileName );
    }

    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        try
        {
            fileManager.save( collection.getStudyGroups() );
            message = "Коллекция успешно сохранена в файл.";
            LoggerFactory.getLogger(SaveCommand.class).info(message);
            return new CommandResult.Builder()
                    .setSuccess(true)
                    .setMessage(message)
                    .buildCommandResult();
        }
        catch ( Exception e )
        {
            throw new FileManagerException("ошибка сохранения коллекции в файл '" + fileName + "'");
        }
    }
    @Override
    public String getName()
    {
        return "save";
    }
    @Override
    public String getDescription()
    {
        return "сохранить коллекцию в файл";
    }
}
