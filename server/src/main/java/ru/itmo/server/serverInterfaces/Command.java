package ru.itmo.server.serverInterfaces;

/**
 * Интерфейс для класса пользовательской команды
 */
public interface Command
{
    ExecuteResult execute(CommandArgs args );
    String getName();
    String getDescription();
}
