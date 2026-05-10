package ru.itmo.client.clientTerminal;

import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myEnums.Commands;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.lab.common.myRecords.Lab5FieldDescriptor;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;

public class RequestCreator 
{
    private final IO_Handler console;
    private final UpdateReader updateReader;
    public RequestCreator( IO_Handler newIOHandler )
    {
        console = newIOHandler;
        updateReader = new UpdateReader();
    }
    public Request buildRequest( String input, IO_Handler intputHandler )
    {
        if( input.trim().isEmpty() )
        {
            console.printError("Пустая команда");
            return null;
        }
        if( input.trim().toLowerCase().equals("exit") )
        {
            return new Request.Builder()
                    .setCommandType("exit")
                    .buildRequest();
        }

        String[] args = input.trim().split("\\s+");
        String name = args[0];

        try
        {
            switch (name)
            {
                case "insert_element" -> {
                    if( args.length != 2 ) throw new CommandException("Ошибка получения аргументов: <команда> <аргумент>");
                    Long id = Long.parseLong(args[1]);
                    StudyGroup newGroup = intputHandler.readNewStudyGroup();
                    return new Request.Builder()
                            .setCommandType(name)
                            .setID(id)
                            .setGroup(newGroup)
                            .buildRequest();
                }
                case "update_id" -> {
                    if( args.length != 2 ) throw new CommandException("Ошибка получения аргументов: <команда> <аргумент>");
                    Long id = Long.parseLong(args[1]);

                    Request.Builder requestBuilder = new Request.Builder()
                            .setCommandType(name)
                            .setID(id);

                    return updateReader.readUpdateField( requestBuilder, intputHandler ).buildRequest();
                }
                default -> {
                    Commands cmd = Commands.find(name);
                    if( cmd == null ) throw new IllegalArgumentException("Неизвестная команда!");
                    Request.Builder clientRequestBuilder = new Request.Builder();
                    clientRequestBuilder.setCommandType(name);
                    if( args.length == 2 )
                    {
                        if( cmd.haveArguments() )
                        {
                            if(cmd.getArgType().equals("long"))
                            {
                                try
                                {
                                    Long id = Long.parseLong( args[1] );
                                    clientRequestBuilder.setID(id);
                                }
                                catch( NumberFormatException e )
                                {
                                    clientRequestBuilder.setArgument(args[1]);
                                }
                            }
                            else
                            {
                                clientRequestBuilder.setArgument(args[1]);
                            }
                        }
                    }
                    clientRequestBuilder.setCommandType(name);

                    return clientRequestBuilder.buildRequest();
                }
            }
        }
        catch( IllegalArgumentException e )
        {
            console.printError(e.getMessage());
            return null;
        }
        catch ( Exception e )
        {
            console.printError("Ошибка при выполнении команды: " + e.getMessage());
            return null;
        }
    }
}
