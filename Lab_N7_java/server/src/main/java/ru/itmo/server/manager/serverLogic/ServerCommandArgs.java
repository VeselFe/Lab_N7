package ru.itmo.server.manager.serverLogic;

import ru.itmo.server.serverInterfaces.CommandArgs;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;

public class ServerCommandArgs implements CommandArgs
{
    private String name;
    private long ownerId;
    public ServerCommandArgs( String name )
    {
        this.name = name;
    }
    // мб дописать билдер?
    @Override
    public String getCmdName() { return name; }
    @Override
    public Long getKey() { return null; }
    @Override
    public String getStringArg() { return null; }
    @Override
    public StudyGroup getGroup() { return null; }
    @Override
    public UpdatedFieldDescriptor getUpdatedField() { return null; }
    @Override
    public Person getAdmin() { return null; }
    @Override
    public String getOwner() { return null; }
    @Override
    public long getOwnerID() { return ownerId; }

    @Override
    public void setOwnerID( long id )
    { ownerId = id; }
}
