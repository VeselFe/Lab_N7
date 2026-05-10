package ru.itmo.lab.common.fileManagement;

import ru.itmo.lab.common.interfaces.OutputHandler;
import ru.itmo.lab.common.model.Coordinates;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myEnums.Country;
import ru.itmo.lab.common.myEnums.FormOfEducation;
import ru.itmo.lab.common.myEnums.Semester;
import ru.itmo.lab.common.myExceptions.CreationException;
import ru.itmo.lab.common.myExceptions.FileManagerException;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Работает с потоками ввода-вывода в файл, xml-конвертером для StudyGroups
 * Содержит методы save, load и loadScript
 *
 * @see FileManager
 */
public class GroupsFileManager extends FileManager<Long, StudyGroup>
{
    private XStream xstream;
    static private OutputHandler errorPrinter;

    public GroupsFileManager( String newFileName )
    {
        super(newFileName);
        xstream = initXStream();
    }

    static public void setErrorPrinter( OutputHandler newPrinter )
    {
        errorPrinter = newPrinter;
    }

    /**
     * инициализация xml-конвертера
     * инициализация всех конвертируемых полей
     *
     * @return XStream
     */
    private XStream initXStream()
    {
        XStream initialXStream = new XStream(new DomDriver());
        initialXStream.autodetectAnnotations(true);

        initialXStream.allowTypesByWildcard(new String[] {
                "ru.itmo.lab.common.model.**",
                "java.util.Hashtable",
                "java.util.Map"
        });

        initialXStream.alias("studyGroups", Hashtable.class);
        initialXStream.alias("group", StudyGroup.class);
        initialXStream.alias("coordinates", Coordinates.class);
        initialXStream.alias("groupAdmin", Person.class);

        initialXStream.alias("formOfEducation", FormOfEducation.class);
        initialXStream.alias("semester", Semester.class);
        initialXStream.alias("country", Country.class);

        initialXStream.useAttributeFor(StudyGroup.class, "id");

        return initialXStream;
    }

    /**
     * Сохранение коллекции в файл
     * Используются потоки FileOutputStream,  OutputStreamWriter
     *
     * @param data
     */
    @Override
    public void save( Hashtable<Long, StudyGroup> data )
    {
        if (fileName == null || fileName.trim().isEmpty())
        {
            throw new FileManagerException("Имя файла не задано");
        }
        if (data == null)
        {
            throw new FileManagerException("Нет данных для сохранения");
        }
        if (xstream == null)
        {
            try
            {
                xstream = initXStream();
            }
            catch( Exception e )
            {
                throw new FileManagerException("Ошибка инициализации XStream");
            }
        }
        File file = new File(fileName);
        if (file.exists() && !file.canWrite())
        {
            throw new FileManagerException("Нет прав записи в файл " + fileName);
        }

        try(FileOutputStream fout = new FileOutputStream(fileName);
            OutputStreamWriter writer = new OutputStreamWriter(fout, "UTF-8"))
        {
            xstream.toXML(data, writer);
        }
        catch( FileNotFoundException e )
        {
            throw new FileManagerException("Не удается открыть(создать) файл для записи!");
        }
        catch( com.thoughtworks.xstream.XStreamException e )
        {
            throw new FileManagerException("Ошибка сериализации");
        }
        catch( IllegalArgumentException e )
        {
            throw new FileManagerException("Некорректные данные для сериализации");
        }
        catch (IOException e)
        {
            throw new FileManagerException("Ошибка сохранения в файл");
        }
    }

    /**
     * Загрузка коллекции из файла
     * Используются потоки FileInputStream, InputStreamReader
     *
     * в случае ошибок прав доступа/некорректного чтения данных выявляет исключения FileManagerException
     * @return Hashtable
     */
    @Override
    public Hashtable<Long, StudyGroup> load()
    {
        Hashtable<Long, StudyGroup> data = new Hashtable<>();
        Hashtable<Long, StudyGroup> result = new Hashtable<>();
        StudyGroup element;

        if (fileName == null || fileName.trim().isEmpty()) { throw new FileManagerException("Имя файла не задано"); }
        File file = new File( fileName );
        if( !file.exists() )
            return data;
        if( !file.canRead() )
        {
            throw new FileManagerException("Нет прав доступа к файлу");
        }
        try(FileInputStream fin = new FileInputStream( file );
            InputStreamReader reader = new InputStreamReader(fin, "UTF-8"))
        {
            data = (Hashtable<Long, StudyGroup>) xstream.fromXML(reader);
            List<Long> idList = new ArrayList<>();
            for(Long key : data.keySet())
            {
                element = data.get(key);
                if( idList.contains(element.getId()) )
                {
                    if(errorPrinter != null)
                    {
                        errorPrinter.printError("   <" + key + "> '" + element.getName() + "'(id: " + element.getId() + ") - Элемент с данным id уже существует\n");
                    }
                    else
                    {
                        throw new FileManagerException("Повторение id");
                    }
                }
                else
                {
                    StudyGroup.GroupValidations.validateName(element.getName());
                    StudyGroup.GroupValidations.validateIntStudCount(element.getStudentsCount());
                    StudyGroup.GroupValidations.validateLongShouldBeExpelled(element.getShouldBeExp());

                    Person.Validations.validateName(element.getAdmin().getName());
                    Person.Validations.validateWeight(element.getAdmin().getWeight());
                    Person.Validations.validatePassport(element.getAdmin().getPassportID());

                    idList.add(element.getId());
                    result.put(key, element);
                }
            }

            return result;
        }
        catch( FileNotFoundException e )
        {
            throw new FileManagerException("Файл не найден!");
        }
        catch( com.thoughtworks.xstream.XStreamException e )
        {
            throw new FileManagerException("Ошибка десериализации");
        }
        catch( IllegalArgumentException e )
        {
            throw new FileManagerException("Некорректный XML - неверные теги,атрибуты.. и пр.");
        }
        catch( ClassCastException e )
        {
            throw new FileManagerException("Неверный формат данных в XML (ожидалась Hashtable)");
        }
        catch( IOException e )
        {
            throw new FileManagerException("Ошибка чтения файла");
        }
        catch( CreationException e )
        {
            throw new FileManagerException("Некорректные данные для импортирования элемента в коллекцию:\n" + e.getMessage());
        }
        catch( Exception e )
        {
            throw new FileManagerException("Неожиданная ошибка при работе с файлом");
        }
    }

    /**
     * Загрузка исполняемых команд из указанного скрипта
     * Используются потоки FileInputStream, InputStreamReader
     *
     * в случае ошибок прав доступа/некорректного чтения данных выявляет исключения FileManagerException
     *
     * @return List
     */
    public List<String> loadScript()
    {
        List<String> commands = new ArrayList<>();
        File file = new File( fileName );

        try(FileInputStream fin = new FileInputStream( file );
            InputStreamReader scanner = new InputStreamReader(fin, "UTF-8"))
        {
            StringBuilder currentString = new StringBuilder();
            int newChar;

            while ((newChar = scanner.read()) != -1)
            {
                if( newChar == '\n')
                {
                    commands.add(currentString.toString());
                    currentString.setLength(0);
                }
                else
                {
                    currentString.append((char) newChar);
                }
            }
            if(!currentString.isEmpty())
            {
                commands.add(currentString.toString());
            }
        }
        catch( AccessDeniedException e )
        {
            throw new FileManagerException("нет доступа к файлу! " + e.getMessage());
        }
        catch( IOException e )
        {
            throw new FileManagerException("Файл не найден");
        }
        catch( Exception e )
        {
            throw new FileManagerException("Ошибка десериализации: " + e.getMessage());
        }

        return commands;
    }
}
