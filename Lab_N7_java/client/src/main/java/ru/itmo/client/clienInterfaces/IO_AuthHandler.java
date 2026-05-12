package ru.itmo.client.clienInterfaces;

import ru.itmo.lab.common.interfaces.IO_Handler;

public interface IO_AuthHandler extends IO_Handler
{
    String readPassword();
}
