package ru.itmo.server.serverInterfaces;

import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;

public interface CommandArgs
{
    String getCmdName();
    Long getKey();
    String getStringArg();
    StudyGroup getGroup();
    UpdatedFieldDescriptor getUpdatedField();
    Person getAdmin();
}
