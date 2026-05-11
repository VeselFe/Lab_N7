package ru.itmo.client.clientTerminal;

import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.lab.common.myRecords.Lab5FieldDescriptor;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;

public class UpdateReader
{
    private IO_Handler console;
    public UpdateReader()
    {    }

    public Request.Builder readUpdateField( Request.Builder requstBuilder, IO_Handler console ) throws CommandException
    {
        console.printInfo("Какой параметр Вы хотите обновить?\n" +
                "===========================================");
        Lab5FieldDescriptor.UPDATED_FIELDS.stream()
                .map(UpdatedFieldDescriptor::name)
                .forEach(fieldName -> console.printInfo(fieldName));
        console.printInfo("===========================================");
        console.printRequest("Введите название параметра: ");
        String updated_name = console.readline().trim();

        if( updated_name.trim().isEmpty() )
        {
            throw new CommandException("Поле ввода пусто!");
        }
        updated_name = updated_name.toLowerCase();
        for(UpdatedFieldDescriptor element : Lab5FieldDescriptor.UPDATED_FIELDS)
        {
            if( updated_name.toLowerCase().equals(element.name().toLowerCase()) )
            {
                if( updated_name.equals("group admin") )
                {
                    console.printInfo(element.request());
                    // Создание нового админа
                    Person newAdmin = console.readPerson();
                    return requstBuilder
                            .setPerson(newAdmin)
                            .setUpdatedField(element);
                }
                console.printRequest(element.request());
                String value = console.readline().trim();

                return requstBuilder
                        .setArgument(value)
                        .setUpdatedField(element);
            }
        }
        throw new CommandException("Поля с таким именем '" + updated_name + "' отсутствуют.");
    }
}
