package ru.itmo.server.сommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Команда для обновления указанного пользователем поля элеменат коллекции
 */
public class UpdateIdCommand implements Command
{
    private final Logger logger = LoggerFactory.getLogger(UpdateIdCommand.class);
    private final CollectionManager collection;
    private String errorMessage;
    private Long Key;
    private String argument;
    private UpdatedFieldDescriptor updatedField;
    private Person newAdmin;

    public UpdateIdCommand(CollectionManager newCollection )
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        try
        {
            Key = Long.valueOf( args.getKey() );
            updatedField = args.getUpdatedField();
            newAdmin = args.getAdmin();
            argument = args.getStringArg();
        }
        catch( NumberFormatException e )
        {
            errorMessage = "Некорректные данные для ключа";
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        catch( Exception e )
        {
            errorMessage = "Некорректный аргумент для обновления элемента";
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        if( Key == null )
        {
            errorMessage = "Не установлен ключ обновляемого элемента!";
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        try
        {
            StudyGroup group = collection.getStudyGroups().get(Key);
            if( group == null )
            {
                errorMessage = "Элемент с ключом " + Key + " не найден в коллекции!";
                logger.error(errorMessage);
                throw new CommandException(errorMessage);
            }
            if( updatedField == null )
            {
                errorMessage = "Не установлено название обновляемого поля!";
                logger.error(errorMessage);
                throw new CommandException(errorMessage);
            }
            if( updatedField.name().equals("admin") )
            {
                if( newAdmin == null )
                {
                    errorMessage = "Не определен новый админ!";
                    logger.error(errorMessage);
                    throw new CommandException(errorMessage);
                }
                group.updateAdmin(newAdmin);
            }
            else
            {
                if( argument == null )
                {
                    errorMessage = "Новое значение поля '" + updatedField.name() + "' не определено!";
                    logger.error(errorMessage);
                    throw new CommandException(errorMessage);
                }

                Method method = group.getClass().getMethod(updatedField.methodName(), updatedField.type());
                method.invoke(group, argument);
            }
            return new CommandResult.Builder()
                    .setSuccess( true )
                    .setMessage("Значение поля '" + updatedField.name() + "' успешно обновлено")
                    .buildCommandResult();
        }
        catch( NoSuchMethodException e )
        {
            errorMessage = "Ошибка при обновлении поля группы: метод " + updatedField.methodName() + " не найден в классе StudyGroup.";
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        catch( IllegalAccessException e )
        {
            errorMessage = "Ошибка доступа: серверу запрещено вызывать метод " + updatedField.methodName();
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        catch( InvocationTargetException e )
        {
            errorMessage = "Ошибка при валидации данных: " + e.getMessage();
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
        catch (Exception e)
        {
            errorMessage = "Непредвиденная ошибка рефлексии методов: " + e.getMessage();
            logger.error(errorMessage);
            throw new CommandException(errorMessage);
        }
    }

    @Override
    public String getName()
    {
        return "update_id";
    }
    @Override
    public String getDescription()
    {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }
}
