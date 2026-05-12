package ru.itmo.server.serverInterfaces;

import java.sql.SQLException;

public interface UserDAI
{
    long registerUser( String login, String password ) throws SQLException;
    long authenticateUser( String login, String password ) throws SQLException;
}
