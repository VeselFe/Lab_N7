package ru.itmo.lab.common.interfaces;


import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;

/**
 * Интерфейс обработки консольного ввода-вывода для команд имеющий вводные значение и вывод резуьтата
 */
public interface IO_Handler extends OutputHandler, InputHandler
{
    StudyGroup readNewStudyGroup();
    Person readPerson();
}
