package ru.itmo.server.dao;

import org.postgresql.util.PSQLException;
import ru.itmo.server.serverInterfaces.UserDAI;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;

public class UserDAO implements UserDAI
{
    private final Connection DB;

    public UserDAO( Connection connection ) { DB = connection; }
    public long registerUser( String login, String password ) throws SQLException
    {
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id";
        try( PreparedStatement request = DB.prepareStatement(sql) )
        {
            request.setString(1, login);
            request.setString(2, hashPassword(password));
            try( ResultSet result = request.executeQuery() )
            {
                if( result.next() )
                {
                    return result.getLong(1);
                }
                else
                {
                    throw new SQLException("Ошибка регистрации нового пользователя");
                }
            }
        }
        catch( PSQLException e )
        {
            throw new SQLException("Логин занят!");
        }
    }
    public long authenticateUser( String login, String password ) throws SQLException
    {
        String sql = "SELECT id FROM users AS u WHERE u.login = ? AND u.password_hash = ?";
        try( PreparedStatement request = DB.prepareStatement(sql) )
        {
            request.setString(1, login);
            request.setString(2, hashPassword(password));
            try( ResultSet result = request.executeQuery() )
            {
                if( result.next() )
                {
                    return result.getLong(1);
                }
                else
                {
                    throw new SQLException("Ошибка авторизации пользователя: Неверный логин или пароль");
                }
            }
        }
    }

    private String hashPassword( String password )
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch( NoSuchAlgorithmException e )
        {
            throw new RuntimeException("Ошибка при хэшировании пароля!");
        }
    }
}
