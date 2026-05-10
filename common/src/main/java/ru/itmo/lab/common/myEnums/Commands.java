package ru.itmo.lab.common.myEnums;

public enum Commands
{
    HELP("help", false),
    INFO("info", false),
    SHOW("show", false),
    INSERT("insert_element", true, "long"),
    UPDATE("update_id", true, "long"),
    REMOVE("remove_key", true, "long"),
    CLEAR("clear", false),
    EXIT("exit", false),
    REMOVE_GREATER("remove_greater", true, "long"),
    REMOVE_LOWER("remove_lower", true, "long"),
    REMOVE_LOWER_KEY("remove_lower_key", true, "long"),
    FILTER_SEM("filter_by_semester_enum", true, "string"),
    FILTER_NAME("filter_starts_with_name", true, "string"),
    PRINT("print_ascending", false);

    private final String name;
    private final boolean arguments;
    private final String type;
    Commands(String name, boolean arguments)
    {
        this.name = name;
        this.arguments = arguments;
        this.type = null;
    }
    Commands(String name, boolean arguments, String type)
    {
        this.name = name;
        this.arguments = arguments;
        this.type = type;
    }

    public String getName() { return name; }
    public  boolean haveArguments() { return arguments; }
    public String getArgType() { return type; }
    public static Commands find( String name )
    {
        for( Commands command : values() )
        {
            if( command.name.equalsIgnoreCase(name) ) return command;
        }
        return null;
    }
}
