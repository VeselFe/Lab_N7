package ru.itmo.server.serverInterfaces;

import ru.itmo.lab.common.myExceptions.CommandException;

import java.util.Map;

/**
 * Интерфейс, описывающий действия manager.Invoker
 */
public interface InvokerActions
{
    void addCommand( String name, Command newCommand );
    ExecuteResult execute(CommandArgs args ) throws CommandException;
    Map<String, Command> getCommands();
}
