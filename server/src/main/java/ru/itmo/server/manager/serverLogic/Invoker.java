package ru.itmo.server.manager.serverLogic;

import org.slf4j.LoggerFactory;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.server.serverInterfaces.Command;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.server.serverInterfaces.InvokerActions;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для регистрации, обработки выполнения, передачи аргументов командам
 *
 */
public class Invoker implements InvokerActions
{
    /** хранит все используемые пользовательские команды    */
    private final Map<String, Command> commands;

    public Invoker()
    {
        commands = new HashMap<>();
    }

    /**
     * Добавление команды в таблицу всех пользовательских команд
     *
     * @param name
     * @param newCommand
     */
    @Override
    public void addCommand( String name, Command newCommand )
    {
        if ( commands.containsKey(name) )
        {
            throw new CommandException("Команда '" + name + "' уже существует");
        }
        commands.put(name, newCommand);
    }

    @Override
    public ExecuteResult execute(CommandArgs args ) throws CommandException
    {
        Command cmd = commands.get(args.getCmdName());
        if( cmd == null )
        {
            CommandResult.Builder builder = new CommandResult.Builder();
            return builder.setSuccess( false )
                    .setMessage( "Неизвестная команда!" )
                    .buildCommandResult();
        }

        try
        {
            //LoggerFactory.getLogger(Invoker.class).info("Исполнение команды...");
            return cmd.execute( args );
        }
        catch( Exception e )
        {
            LoggerFactory.getLogger(Invoker.class).error(e.getMessage());
            return new CommandResult.Builder()
                    .setSuccess( false )
                    .setMessage("Ошибка при исполнении команды: " + e.getMessage())
                    .buildCommandResult();
        }
    }

    /**
     * Возвращает таблицу всех команд
     *
     * @return Map
     */
    @Override
    public Map<String, Command> getCommands() { return commands; }
}
