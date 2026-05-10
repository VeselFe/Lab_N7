package ru.itmo.client.clientTerminal;

import ru.itmo.client.FileHandler.ScriptHandler;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CreationException;
import ru.itmo.lab.common.myRecords.FieldDescriptor;

import static ru.itmo.lab.common.myRecords.Lab5FieldDescriptor.NEW_GROUP_FIELDS;

/**
 * Класс для чтения значений для полей {@link StudyGroup} из консоли
 * Используется для создания новой группы
 */
public class StudyGroupReader
{
    private IO_Handler reader;
    public StudyGroupReader( IO_Handler newConsole )
    {
        reader = newConsole;
    }

    public StudyGroup readStudygroup()
    {
        String input;

        StudyGroup.Builder groupGenerator = new StudyGroup.Builder();

        for (FieldDescriptor field : NEW_GROUP_FIELDS)
        {
            do
            {
                try
                {
                    reader.printRequest(field.request());
                    input = reader.readline();
                    input = input.trim();
                    switch (field.name().toLowerCase())
                    {
                        case "name" -> groupGenerator.setName(input);
                        case "coordinates" -> groupGenerator.setCoordinates(input);
                        case "studentcount" -> groupGenerator.setStudCount(input);
                        case "shoudbeexpelled" -> groupGenerator.setShBeExp(input);
                        case "formofeducation" -> groupGenerator.setFormOfEdu(input);
                        case "semester" -> groupGenerator.setSem(input);
                        default -> throw new IllegalArgumentException("Неизвестное поле: " + field.name());
                    }
                    break;
                }
                catch (Exception e)
                {
                    if (reader instanceof ScriptHandler)
                    {
                        throw new CreationException("Ошибка валидации в скрипте для поля " + field.name() + ": " + e.getMessage());
                    }
                    reader.printError(e.getMessage() + "\nПопробуйте снова ввести");
                }
            } while (true);
        }
        try 
        {
            reader.printInfo("Заполните анкету о новом админе");
            groupGenerator.setAdmin(reader.readPerson());
            return groupGenerator.build();
        }
        catch( Exception e )
        {
            throw new CreationException("Ошибка при попытке инициализации группы: " + e.getMessage());
        }
    }
}
