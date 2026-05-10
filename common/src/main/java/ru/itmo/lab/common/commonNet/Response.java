package ru.itmo.lab.common.commonNet;

import ru.itmo.lab.common.model.StudyGroup;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable
{
    private static final long serialVersionUID = 666L;

    private final boolean success;
    private final String message;
    private final List<StudyGroup> collection;

    public static Builder builder()
    {
        return new Builder();
    }
    private Response( Builder builder )
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

        public Response buildResponse()
        {
            return new Response( this );
        }

        public Builder setSuccess( Boolean success )
        {
            this.success = success;
            return this;
        }
        public Builder setMessage( String message )
        {
            this.message = message;
            return this;
        }
        public Builder setSortedCollection( List<StudyGroup> collection )
        {
            this.collection = collection;
            return this;
        }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<StudyGroup> getCollection() { return collection; }
}
