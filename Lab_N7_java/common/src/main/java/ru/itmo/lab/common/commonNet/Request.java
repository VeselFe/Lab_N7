package ru.itmo.lab.common.commonNet;

import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.lab.common.myRecords.UpdatedFieldDescriptor;

import java.io.Serializable;

public class Request implements Serializable
{
    private static final long serialVersionUID = 666L;

    private final String commandType;
    private final String argument;
    private final UpdatedFieldDescriptor updatedField;
    private final Long idArg;
    private final StudyGroup group;
    private final Person admin;

    private Request( Builder builder )
    {
        this.commandType = builder.commandType;
        this.argument = builder.argument;
        this.idArg = builder.idArg;
        this.group = builder.group;
        this.admin = builder.admin;
        this.updatedField = builder.updatedField;
    }

    public String getCommandType()
    {
        return commandType;
    }
    public String getArgument() { return argument; }
    public UpdatedFieldDescriptor getUpdatedField() { return updatedField; }
    public Long getIdArg() { return idArg; }
    public StudyGroup getGroup() { return group; }
    public Person getAdmin() { return admin; }

    public static class Builder
    {
        private String commandType = null;
        private UpdatedFieldDescriptor updatedField = null;
        private String argument = null;
        private Long idArg = null;
        private StudyGroup group = null;
        private Person admin = null;

        public Request buildRequest()
        {
            return new Request( this );
        }

        public Builder setCommandType( String newCommandType )
        {
            if( newCommandType.isEmpty() )
            {
                throw new CommandException("Некорректный тип команды!");
            }

            commandType = newCommandType;
            return this;
        }
        public Builder setArgument( String newArgument )
        {
            if( newArgument.isEmpty() )
            {
                throw new CommandException("Некорректный аргумент команды!");
            }

            argument = newArgument;
            return this;
        }
        public Builder setUpdatedField( UpdatedFieldDescriptor newUField )
        {
            if( newUField == null )
            {
                throw new CommandException("Некорректный аргумент команды!");
            }

            updatedField = newUField;
            return this;
        }
        public Builder setID( Long newID )
        {
            if( newID == null )
            {
                throw new CommandException("Некорректный аргумент команды!");
            }

            idArg = newID;
            return this;
        }
        public Builder setGroup( StudyGroup newGroup )
        {
            if( newGroup == null )
            {
                throw new CommandException("Некорректный аргумент команды!");
            }

            group = newGroup;
            return this;
        }
        public Builder setPerson( Person newPerson )
        {
            if( newPerson == null )
            {
                throw new CommandException("Некорректный аргумент команды!");
            }

            admin = newPerson;
            return this;
        }
    }

    @Override
    public String toString()
    {
        return  "cmd type: " + (commandType != null ? commandType : "null") +
                "\narg: " + (argument != null ? argument : "null") +
                "\nupdated field name: " + (updatedField != null ? updatedField.name() : "null") +
                "\ngroup: " + (group != null ? "\n---------------------------------------" +
                                group.getInformation() +
                                "\n---------------------------------------" : "null") +
                "\nadmin: " + (admin != null ? admin.getName() : "null");
    }
}
