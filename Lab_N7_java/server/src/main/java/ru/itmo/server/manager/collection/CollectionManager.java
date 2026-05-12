package ru.itmo.server.manager.collection;

import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CommandException;
import ru.itmo.lab.common.myExceptions.CreationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
    private LocalDateTime initializationDate;

    /**
     * Приватный конструктор для реализации Singleton.
     * Инициализирует пустую коллекцию и устанавливает время создания.
     */
    private CollectionManager()
    {
        studyGroups = new Hashtable<>();
        initializationDate = LocalDateTime.now();
    }

    /**
     * Создает единственный экземпляр менеджера коллекции (паттерн Singleton).
     *
     * @return единственный экземпляр {@link CollectionManager}
     */
    public static CollectionManager createCollection()
    {
        if( singleCollection == null )
        {
            singleCollection = new CollectionManager();
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
    public void addElement( Long key, StudyGroup newGroup )
    {
        if ( studyGroups.get( key ) != null )
        {
            throw new CreationException("Элемент с данным ключем уже был создан");
        }
        studyGroups.put(key, newGroup);
    }
    public void removeElement( Long key )
    {
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
                studyGroups.remove( key );
            }
            catch ( Exception e )
            {
                throw new RuntimeException(e);
            }
        }
    }
    public void clearCollection()
    {
        studyGroups.clear();
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
