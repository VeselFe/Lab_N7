package ru.itmo.client.clientTerminal;

import ru.itmo.client.FileHandler.ScriptHandler;
import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.terminal.AbstractConsoleHandler;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CreationException;

import java.util.Scanner;
import java.util.Stack;

public class ClientConsoleHandler extends AbstractConsoleHandler
    implements IO_Handler
{
    private final Scanner scanner = new Scanner(System.in);
    private final Stack<IO_Handler> ioHandlersStack = new Stack<>();
    private String input;
    private RequestCreator requestCreator;

    public ClientConsoleHandler()
    {
        ioHandlersStack.push(this);
    }

    public void welcomMessage()
    {
        print("Введите 'help' для просмотра возможных команд.");
    }
    public void initRequestCreator( IO_Handler ioHandler )
    {
        this.requestCreator = new RequestCreator( ioHandler );
    }

    public Request createRequest()
    {
        if( requestCreator == null ) throw new RuntimeException("Сборщик запроса не инициирован.");
        print("\n         Введите команду");
        print("=====================================");
        input = readline();
        if( input.trim().toLowerCase().startsWith("execute_script") )
        {
            try {
                String fileName = input.trim().substring(14).trim();
                ioHandlersStack.push(new ScriptHandler(fileName));
                input = readline();
            }
            catch( Exception e )
            {
                printError("Ошибка при работе со скриптом: " + e.getMessage());
                ((ScriptHandler) ioHandlersStack.peek()).close();
                ioHandlersStack.pop();
                return createRequest();
            }
        }
        Request request = requestCreator.buildRequest(input, ioHandlersStack.peek());
        if( request == null )
        {
            IO_Handler curHandler = ioHandlersStack.peek();
            if( curHandler instanceof ScriptHandler )
            {
                printError("Ошибка при работе со скриптом");
                ((ScriptHandler) curHandler).close();
                ioHandlersStack.pop();
                return createRequest();
            }
        }
        return request;
    }

    @Override
    public String readline()
    {
        if (ioHandlersStack.isEmpty())
        {
            ioHandlersStack.push(this);
        }

        IO_Handler currentHanler = ioHandlersStack.peek();

        if( currentHanler instanceof ScriptHandler )
        {
            ScriptHandler script = (ScriptHandler) currentHanler;
            if( script.hasNext() )
            {
                String scriptCmd = script.readline();
                printInfo("Команда: " + scriptCmd);
                return scriptCmd;
            }
            else
            {
                script.close();
                ioHandlersStack.pop();
                return readline();
            }
        }
        return scanner.nextLine();
    }

    @Override
    public void printInfo( String message ) { print( "<i> " + message ); }
    @Override
    public void printError( String message ) { print("<Ошибка>\n" + message); }
    @Override
    public void printRequest( String request ) { printCurLine( request ); }
    @Override
    public Person readPerson() throws CreationException
    {
        return new PersonReader(this).readPerson();
    }
    @Override
    public StudyGroup readNewStudyGroup()
    {
        return new StudyGroupReader(this).readStudygroup();
    }

    public void close()
    {
        scanner.close();
        while( !ioHandlersStack.isEmpty() )
        {
            IO_Handler handler = ioHandlersStack.pop();
            if( handler instanceof ScriptHandler )
            {
                ((ScriptHandler) handler).close();
            }
        }
    }
}
