package ru.itmo.server.manager.collection;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.lab.common.myExceptions.CreationException;
import ru.itmo.server.ioHandlers.CommandResult;
import ru.itmo.server.serverInterfaces.StudyGroupDAI;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Менеджер коллекции учебных групп.
 *
 * <p>Реализует паттерн <b>Singleton</b> для обеспечения единственного экземпляра коллекции
 *
 * <p><b>Основное назначение:</b></p>
 * <ul>
 *   <li>Хранение коллекции учебных групп с уникальными ключами</li>
 *   <li>Генерация уникальных ID для новых элементов</li>
 *   <li>Предоставление информации о состоянии коллекции</li>
 * </ul>
 *
 */
public class CollectionManager
{
    /** Единственный экземпляр менеджера коллекции */
    private static CollectionManager singleCollection;
    /** Основная коллекция: ключ - ID, значение - учебная группа */
    private final Hashtable<Long, StudyGroup> studyGroups;
    /** Дата и время инициализации коллекции */
    private final LocalDateTime initializationDate;
    private final Logger logger = LoggerFactory.getLogger(CollectionManager.class);
    private final StudyGroupDAI dbManager;

    /**
     * Приватный конструктор для реализации Singleton.
     * Инициализирует пустую коллекцию и устанавливает время создания.
     */
    private CollectionManager( StudyGroupDAI dbManager )
    {
        studyGroups = new Hashtable<>();
        initializationDate = LocalDateTime.now();
        this.dbManager = dbManager;
    }

    /**
     * Создает единственный экземпляр менеджера коллекции (паттерн Singleton).
     *
     * @return единственный экземпляр {@link CollectionManager}
     */
    public static CollectionManager createCollection( StudyGroupDAI dbManager )
    {
        if( singleCollection == null )
        {
            singleCollection = new CollectionManager( dbManager );
        }
        return singleCollection;
    }
    /**
     * Возвращает основную коллекцию учебных групп.
     *
     * @return коллекция {@link Hashtable}&lt;{@link Long}, {@link StudyGroup}&gt;
     */
    public Hashtable<Long, StudyGroup> getStudyGroups()
    {
        return studyGroups;
    }
    /**
     * Возвращает отсортированный список всех учебных групп.
     * Сортировка выполняется по естественному порядку {@link StudyGroup}.
     *
     * @return новый {@link List} с отсортированными группами
     * @see StudyGroup#compareTo(StudyGroup)
     */
    public List<StudyGroup> getSortedCollection()
    {
        List<StudyGroup> groups = new ArrayList<>(studyGroups.values());
        groups.sort(null);
        return groups;
    }
    public List<StudyGroup> getSortedByNameCollection()
    {
        List<StudyGroup> groups = new ArrayList<>(studyGroups.values());
        StudyGroupByNameComparator comparator = new StudyGroupByNameComparator();
        groups.sort(comparator);
        return groups;
    }
    /**
     * Возвращает текстовую информацию о коллекции в формате:
     * <pre>
     * Тип: Hashtable
     * Дата инициализации: DD.MM.YYYY в HH:MM:SS
     * Количество элементов: N
     * </pre>
     *
     * @return строка с информацией о коллекции
     */
    public String getInfo()
    {
        String creationTime = initializationDate.toString();

        return  "Тип: Hashtable\n" +
                "Дата инициализации: " +
                creationTime.substring(8,10) + '.' + creationTime.substring(5,7) +
                '.' + creationTime.substring(0,4) +
                " в " + (creationTime.substring(11,19)) +
                "\nКоличество элементов: " + studyGroups.size();
    }

    /**
     * Добавляет новую учебную группу в коллекцию.
     *
     * @param key уникальный ключ группы
     * @param newGroup учебная группа для добавления (не null)
     *
     * @throws CreationException если ключ уже существует в коллекции
     */
    public void addElement( Long key, StudyGroup newGroup, long ownerID )
    {
        try
        {
            long id = dbManager.addGroup(key, newGroup, ownerID);
            newGroup.setId(id);
        }
        catch( SQLException e )
        {
            String errorMessage = "Не удалось загрузить элемент в БД: " + e.getMessage();
            logger.error(errorMessage);
            throw new CreationException(errorMessage);
        }
        catch(Exception e)
        {
            logger.error("Неизвестная ошибка: " + e.getMessage());
        }
        addInMemory(key, newGroup);
    }
    public void addInMemory( Long key, StudyGroup newGroup )
    {
        //lock.lock();
        try
        {
            if( studyGroups.get( key ) != null )
            {
                throw new CreationException("Элемент с данным ключем уже был создан");
            }
            studyGroups.put(key, newGroup);
        }
        finally
        {
            //lock.unlock();
        }
    }
    public void updateElement( Long key, long ownerId, UpdatedFieldConsumer updateLogic )
    {
        //lock.lock();
        try
        {
            StudyGroup group = studyGroups.get(key);
            if (group == null) throw new CommandException("Элемент не найден");

            StudyGroup tempGroup = group.copy();
            updateLogic.accept(tempGroup);
            boolean success = dbManager.updateGroup(key, tempGroup, ownerId);
            if (success)
            {
                updateLogic.accept(group);
                logger.info("Элемент с ключом key='" + key + "' успешно обновлен");
            }
            else
            {
                throw new CommandException("У вас нет прав на редактирование этого объекта или он не существует");
            }
        }
        catch( SQLException e )
        {
            throw new CommandException("Ошибка БД: " + e.getMessage());
        }
        catch( Exception e )
        {
            throw new CommandException("Менеджер коллекции: Не удалось обновить элемент " + e.getMessage());
        }
        finally
        {
            //lock.unlock();
        }
    }
    @FunctionalInterface
    public interface UpdatedFieldConsumer
    {
        void accept(StudyGroup group) throws Exception;
    }
    public void removeElement( Long key, long ownerID ) throws Exception
    {
        boolean success = false;
        if(studyGroups.isEmpty())
        {
            throw new CommandException("Коллекция пуста!");
        }
        if(studyGroups.get( key ) == null)
        {
            throw new CommandException("Отсутствует элемент по данному ключу");
        }
        else
        {
            try
            {
                success = dbManager.removeGroup(key, ownerID);
            }
            catch( SQLException e )
            {
                logger.error("Не удалось удалить элемент из БД: " + e.getMessage());
                throw new CommandException("Не удалось удалить элемент из БД.");
            }
            try
            {
                if(success)
                    studyGroups.remove( key );
                else
                    throw new CommandException("Не удалось удалить элемент из БД - нет прав доступа!");
            }
            catch ( Exception e )
            {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
    public void clearCollection(long ownerID) {
        //lock.lock();
        try
        {
            boolean dbSuccess = dbManager.clearGroups(ownerID);


            if( dbSuccess )
            {
                studyGroups.entrySet().removeIf(entry -> entry.getValue().getOwnerID() == ownerID);
                logger.info("Коллекция очищена для пользователя {}", ownerID);
            }
        }
        catch( SQLException e )
        {
            throw new CommandException("Ошибка БД при очистке: " + e.getMessage());
        }
        finally
        {
            //lock.unlock();
        }
    }
    public boolean updateElement( Long key, String parametr, String value, Person newAdmin )
    {
        if( !studyGroups.containsKey(key) )
        {
            throw new CommandException("По данному ключу ничего не найдено");
        }

        try
        {
            switch (parametr.toLowerCase())
            {
                case "name" -> studyGroups.get(key).updateName(value);
                case "coordinates" -> studyGroups.get(key).updateCoordinates(value);
                case "studentcount" -> studyGroups.get(key).updateStudCount(value);
                case "shouldbeexpelled" -> studyGroups.get(key).updateShBeExp(value);
                case "formofeducation" -> studyGroups.get(key).updateFormOfEdu(value);
                case "semester" -> studyGroups.get(key).updateSem(value);
                case "admin" -> studyGroups.get(key).updateAdmin(newAdmin);
                default -> throw new IllegalArgumentException("Неизвестное поле!");
            }
            return true;
        }
        catch( CreationException e )
        {
            throw new CommandException("Ошибка при попытке обновления поля '" + parametr + "': \n" + e.getMessage());
        }
        catch ( Exception e )
        {
            throw new CommandException("Неизвестная ошибка при попытке обновления поля '" + parametr + "'");
        }
    }
}
