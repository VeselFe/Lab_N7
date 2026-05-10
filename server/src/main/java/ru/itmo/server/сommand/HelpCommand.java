package ru.itmo.server.сommand;

import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.serverInterfaces.InvokerActions;

import java.util.Comparator;

/**
 * Команда для вывода в консоль списка всех пользовательских команд
 */
public class HelpCommand implements Command
{
    private final InvokerActions invoker;

    public HelpCommand( InvokerActions newInvokerInterface )
    {
        invoker = newInvokerInterface;
    }

    @Override
    public ExecuteResult execute(CommandArgs args )
    {
        try
        {
            StringBuilder result = new StringBuilder();
            result.append("Список всех возможных команд:\n" +
                    "--------------------------------------------------------------\n");
            invoker.getCommands().values().stream()
                    .sorted(Comparator.comparing(Command::getName))
                    .forEach(cmd -> result.append(cmd.getName() + ": " + cmd.getDescription() + "\n"));
            result.append("\n--------------------------------------------------------------\n");
            return new CommandResult.Builder()
                    .setSuccess( true )
                    .setMessage( result.toString() )
                    .buildCommandResult();
        }
        catch( Exception e )
        {
            String errorMessage = "Неизвестная ошибка: " + e.getMessage();
            LoggerFactory.getLogger(HelpCommand.class).error(errorMessage);
            throw new CommandException(errorMessage);
        }
    }

    @Override
    public String getName()
    {
        return "help";
    }
    @Override
    public String getDescription()
    {
        return "вывести справку по доступным командам";
    }
}
