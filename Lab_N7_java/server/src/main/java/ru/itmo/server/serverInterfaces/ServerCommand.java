package ru.itmo.server.serverInterfaces;

import ru.itmo.lab.common.interfaces.OutputHandler;

public interface ServerCommand
{
    void execute( OutputHandler ioHandler );
    String getName();
    String getDescription();
}
