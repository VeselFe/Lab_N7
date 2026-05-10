package ru.itmo.server.ioHandlers;

import ru.itmo.lab.common.interfaces.InputHandler;
import ru.itmo.lab.common.interfaces.OutputHandler;
import ru.itmo.lab.common.terminal.AbstractConsoleHandler;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.server.manager.serverLogic.Invoker;

import java.util.Scanner;

/**
 * Консольный обработчик вывода для лабораторной работы №6.
 *
 * <p>Реализует интерфейс {@link IO_Handler} и расширяет {@link AbstractConsoleHandler}
 * для обработки команд интерактивного режима работы программы.</p>
 *
 * <p><b>Основное назначение:</b></p>
 * <ul>
 *   <li>Чтение пользовательских команд и их выполнение через {@link Invoker}</li>
 *   <li>Форматированный вывод информационных сообщений, ошибок и запросов</li>
 * </ul>
 */
public class ServerConsoleHandler extends AbstractConsoleHandler
        implements InputHandler, OutputHandler
{
    private Scanner scanner = new Scanner(System.in);
    public ServerConsoleHandler()
    {
    }

    public void printInfo( String message )
    {
        print( "СЕРВЕР: " + message );
    }
    public void printError( String messege )
    {
        print("СЕРВЕР: <Ошибка> " + messege + "\n");
    }
    public void printRequest( String request ) {}
    public String readline()
    {
        return scanner.nextLine();
    }
}
