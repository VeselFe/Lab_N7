package ru.itmo.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler
{
    private final String URL;
    private final String username;
    private final String password;
    private Connection connection;
    private final Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);

    public DatabaseHandler( String URL, String username, String password )
    {
        this.URL = URL;
        this.username = username;
        this.password = password;
    }

    public Connection connectToDatabase()
    {
        try
        {
            connection = DriverManager.getConnection(URL, username, password);
            logger.info("Подключение к базе данных установлено");
            return connection;
        }
        catch( SQLException e )
        {
            logger.error("Не удалось подключиться к базе данных");
        }
        return null;
    }
}
