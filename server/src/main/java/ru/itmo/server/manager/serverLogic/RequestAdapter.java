package ru.itmo.server.manager.serverLogic;

import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;

public class RequestAdapter implements CommandArgs
{
    private final Request request;

    RequestAdapter( Request request )
    {
        this.request = request;
    }

    @Override
    public String getCmdName() { return request.getCommandType(); }

    @Override
    public Long getKey() { return request.getIdArg(); }

    @Override
    public String getStringArg() { return request.getArgument(); }

    @Override
    public StudyGroup getGroup() { return request.getGroup(); }

    @Override
    public Person getAdmin() { return request.getAdmin(); }

    @Override
    public UpdatedFieldDescriptor getUpdatedField() { return request.getUpdatedField(); }
}
