package ru.itmo.client.clientTerminal;

import ru.itmo.client.clienInterfaces.IO_AuthHandler;
import ru.itmo.client.network.NetworkManager;
import ru.itmo.lab.common.commonNet.Request;
import ru.itmo.lab.common.commonNet.Response;

import java.io.IOException;

public class AuthManager
{
    private final IO_AuthHandler console;
    private final NetworkManager networkManager;
    private String login = null;
    private String password = null;
    private boolean logined;
    public AuthManager(IO_AuthHandler ioHandler, NetworkManager networkManager)
    {
        console = ioHandler;
        this.networkManager = networkManager;
    }

    public boolean authenticate()
    {
        boolean continueAuth = true;
        console.printRequest(" - РЕГИСТРАЦИЯ - \n" +
                "r - регистрация нового пользователя\n" +
                "l - войти\n" +
                "q - выход\n" +
                "> ");
        switch(console.readline().trim())
        {
            case "r" -> {
                continueAuth = sendAuthRequest("register", "зарегистрироваться");
            }
            case "l" -> {
                continueAuth = sendAuthRequest("login", "авторизироваться");
            }
            case "q" -> {
                continueAuth = false;
            }
            default -> {
                console.printError("Некорректный выбор. Попробуйте снова.");
            }
        }
        return continueAuth;
    }
    private boolean sendAuthRequest( String cmdType, String errMes )
    {
        String inputLogin;
        String inputPassword;
        console.printRequest("Введите логин: ");
        inputLogin = console.readline();
        console.printRequest("Введите пароль: ");
        inputPassword = console.readPassword();

        if (inputLogin.isEmpty() || inputPassword.isEmpty()) {
            console.printError("Поля не могут быть пустыми!");
            return true;
        }

        Request loginRequest = new Request.Builder()
                .setCommandType(cmdType)
                .setLogin(inputLogin)
                .setPassword(inputPassword)
                .buildRequest();
        try
        {
            networkManager.network(loginRequest);
            Response loginResp = networkManager.getAuthenResponse();
            if( loginResp.isSuccess() )
            {
                this.login = inputLogin;
                this.password = inputPassword;
                logined = true;
                console.printInfo("Авторизация прошла успешно.");
                return false;
            }
            else
            {
                String respMes = loginResp.getMessage();
                console.printError("Не удалось " + errMes + ": " + (respMes == null ? "Неизвестная причина" : respMes) );
                return true;
            }
        }
        catch( IOException e )
        {
            console.printError("Не удалось провести авторизацию (ошибка сетевого взаимодействия): " + e.getMessage());
            return true;
        }
    }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
}
