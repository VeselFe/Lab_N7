package ru.itmo.server.ioHandlers;

import ru.itmo.server.serverInterfaces.ExecuteResult;
import ru.itmo.lab.common.model.StudyGroup;

import java.util.List;

public class CommandResult implements ExecuteResult
{
    private final boolean success;
    private final String message;
    private final List<StudyGroup> collection;

    public static CommandResult.Builder builder()
    {
        return new CommandResult.Builder();
    }
    private CommandResult( CommandResult.Builder builder )
    {
        this.success = builder.success;
        this.message = builder.message;
        this.collection = builder.collection;
    }

    public static class Builder
    {
        private boolean success = false;
        private String message = "";
        private List<StudyGroup> collection = null;

        public CommandResult buildCommandResult()
        {
            return new CommandResult( this );
        }

        public CommandResult.Builder setSuccess( Boolean success )
        {
            this.success = success;
            return this;
        }
        public CommandResult.Builder setMessage( String message )
        {
            this.message = message;
            return this;
        }
        public CommandResult.Builder setSortedCollection( List<StudyGroup> collection )
        {
            this.collection = collection;
            return this;
        }
    }

    @Override
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<StudyGroup> getCollection() { return collection; }
}
