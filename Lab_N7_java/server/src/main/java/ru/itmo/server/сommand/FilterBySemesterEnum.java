package ru.itmo.server.сommand;

import org.slf4j.LoggerFactory;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myEnums.Semester;

import java.util.List;


/**
 * Команда для вывода элементов коллекции, отсортировав по семестру обучения
 */
public class FilterBySemesterEnum implements Command
{
    private final CollectionManager collection;
    private String errorMessage;
    private Semester sem;

    public FilterBySemesterEnum( CollectionManager newCollection )
    {
        collection = newCollection;
    }

    @Override
    public ExecuteResult execute( CommandArgs args ) throws CommandException
    {
        errorMessage = null;
        try
        {
            sem = Semester.valueOf(args.getStringArg().toUpperCase().trim());
        }
        catch( NullPointerException e )
        {
            errorMessage = "Некорректное количество аргументов для данной функции";
        }
        catch( IllegalArgumentException e )
        {
            errorMessage = "Неверно введен семестр. Варианты: FIRST, SECOND, THIRD, FIFTH, EIGHTH";
        }
        if( errorMessage != null )
        {
            LoggerFactory.getLogger(FilterStartsWithName.class).error(errorMessage);
            throw new CommandException(errorMessage);
        }
        List<StudyGroup> filteredGroups = collection.getStudyGroups().values().stream()
                .filter(group -> group.getSemester().equals(sem))
                .toList();
        StringBuilder result = new StringBuilder();
        filteredGroups.forEach(group -> result.append(group.getInformation() + "\n"));
        if( filteredGroups.isEmpty() )
        {
            return new CommandResult.Builder()
                    .setSuccess( true )
                    .setMessage("Совпадений не найдено!")
                    .buildCommandResult();
        }
        else
        {
            return new CommandResult.Builder()
                    .setSuccess( true )
                    .setMessage("Было найдено " + filteredGroups.size() + " совпадений: \n" + result.toString())
                    .buildCommandResult();
        }
    }
    @Override
    public String getName()
    {
        return "filter_by_semester_enum { semesterEnum }";
    }
    @Override
    public String getDescription()
    {
        return "вывести элементы, значение поля semesterEnum которых равно заданному";
    }
}
