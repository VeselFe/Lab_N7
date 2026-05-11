package ru.itmo.client.FileHandler;

import ru.itmo.lab.common.fileManagement.GroupsFileManager;
import ru.itmo.lab.common.interfaces.IO_Handler;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CreationException;
import ru.itmo.client.clientTerminal.PersonReader;
import ru.itmo.client.clientTerminal.StudyGroupReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ScriptHandler implements IO_Handler
{
    private String fileName;
    private int strIndex = 0;
    private final List<String> fileStrings;
    private static Set<Path> openedFiles = new HashSet<>();

    public ScriptHandler( String fileName ) throws IOException
    {
        this.fileName = fileName;
        Path path = Paths.get(fileName).toAbsolutePath().normalize();

        if( openedFiles.contains(path) )
        {
            throw new IOException("Обнаружена попытка рекурсивного запуска скрипта '" + fileName + "'");
        }

        this.fileStrings = new GroupsFileManager( fileName ).loadScript();
        openedFiles.add(path);
    }

    @Override
    public String readline()
    {
        if( hasNext() )
            return fileStrings.get( strIndex++ );
        return null;
    }

    public boolean hasNext() { return  strIndex < fileStrings.size(); }

    public void close()
    {
        openedFiles.remove(Paths.get(fileName).toAbsolutePath().normalize());
    }

    @Override
    public Person readPerson()
    {
        try
        {
            return new PersonReader(this).readPerson();
        }
        catch( Exception e )
        {
            throw new CreationException("Скрипт '" + fileName + "' содержит некорректные данные для инициализации админа: \n" + e.getMessage());
        }
    }
    @Override
    public StudyGroup readNewStudyGroup()
    {
        try
        {
            return new StudyGroupReader(this).readStudygroup();
        }
        catch( Exception e )
        {
            throw new CreationException("Скрипт '" + fileName + "' содержит некорректные данные для инициализации группы: \n" + e.getMessage());
        }
    }
    @Override
    public void printInfo( String messege ) { messege = null; }
    @Override
    public void printRequest( String request ) { request = null; }
    @Override
    public void printError( String messege ) { messege = null; }
}

