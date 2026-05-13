package ru.itmo.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.model.Coordinates;
import ru.itmo.lab.common.model.Person;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myEnums.Country;
import ru.itmo.lab.common.myEnums.FormOfEducation;
import ru.itmo.lab.common.myEnums.Semester;
import ru.itmo.lab.common.myExceptions.CreationException;
import ru.itmo.server.serverInterfaces.StudyGroupDAI;

import java.sql.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class StudyGroupDAO implements StudyGroupDAI
{
    private final Logger logger = LoggerFactory.getLogger(StudyGroupDAO.class);
    private final Connection DB;
    private Map<FormOfEducation, Integer> formIDs = new HashMap<>();
    private Map<Semester, Integer> semesterIDs = new HashMap<>();
    private Map<Country, Integer> countryIDs = new HashMap<>();

    public StudyGroupDAO( Connection connection )
    {
        DB = connection;
    }

    @Override
    public long addGroup( long key, StudyGroup newGroup, long ownerID ) throws SQLException
    {
        String CHEK_KEY_GROUP = "SELECT * FROM study_groups AS g WHERE g.key = ?";
        try( PreparedStatement chekRequest = DB.prepareStatement(CHEK_KEY_GROUP) )
        {
            chekRequest.setLong(1, key);
            try (ResultSet result = chekRequest.executeQuery())
            {
                if( result.next() )
                    throw new SQLException("Элемент с таким ключом (key='" + key + "') уже есть в базе.");
            }
        }
        DB.setAutoCommit(false);
        try( PreparedStatement request = DB.prepareStatement(ADD_NEW_GROUP) )
        {
            request.setLong(1, key);
            request.setString(2, newGroup.getName());
            long coordinates_id = addCoordinates(newGroup.getCoordinates());
            request.setLong(3, coordinates_id);
            request.setInt(4, newGroup.getStudentsCount());
            request.setLong(5, newGroup.getShouldBeExp());
            request.setLong(6, formIDs.get(newGroup.getFormOfEducation()));
            request.setLong(7, semesterIDs.get(newGroup.getSemester()));
            long person_id = addPerson(newGroup.getAdmin());
            request.setDouble(8, person_id);
            request.setLong(9, ownerID);

            try (ResultSet result = request.executeQuery())
            {
                if (result.next())
                {
                    DB.commit();
                    return result.getLong(1);
                }
                else
                {
                    DB.rollback();
                    throw new SQLException("Ошибка сохранения группы");
                }
            }
        }
        catch( SQLException e )
        {
            DB.rollback();
            if ("23503".equals(e.getSQLState()))
            {
                throw new SQLException("Ошибка: Пользователь-владелец не найден в базе данных.");
            }
            throw e;
        }
        finally
        {
            DB.setAutoCommit(true);
        }
    }
    private long addCoordinates( Coordinates newCoordinates ) throws SQLException
    {
        try (PreparedStatement request = DB.prepareStatement(ADD_COORDINATES))
        {
            request.setDouble(1, newCoordinates.getX());
            request.setDouble(2, newCoordinates.getY());
            try (ResultSet result = request.executeQuery())
            {
                if (result.next())
                {
                    return result.getLong(1);
                }
                else
                {
                    throw new SQLException("Ошибка сохранения координат: ID не был сгенерирован.");
                }
            }
        }
    }
    private long addPerson( Person newAdmin ) throws SQLException
    {
        try (PreparedStatement request = DB.prepareStatement(ADD_PERSON))
        {
            request.setString(1, newAdmin.getName());
            if (newAdmin.getBirthday() != null)
            {
                request.setTimestamp(2, java.sql.Timestamp.valueOf(newAdmin.getBirthday()));
            }
            else
            {
                request.setNull(2, java.sql.Types.TIMESTAMP);
            }
            request.setDouble(3, newAdmin.getWeight());
            String passp = newAdmin.getPassportID();
            if( passp == null || passp.trim().isEmpty() )
                request.setNull(4, Types.VARCHAR);
            else
                request.setString(4, passp);
            request.setInt(5, countryIDs.get(newAdmin.getNationality()));

            try (ResultSet result = request.executeQuery())
            {
                if (result.next())
                {
                    return result.getLong(1);
                }
                else
                {
                    throw new SQLException("Ошибка сохранения админа: ID не был сгенерирован.");
                }
            }
        }
    }
    @Override
    public boolean updateGroup( long key, StudyGroup group, long ownerID ) throws SQLException
    {
        String CHEK_KEY_GROUP = "SELECT * FROM study_groups AS g WHERE g.key = ?";
        //String CHEK_OWNER_GROUP = "SELECT * FROM study_groups AS g WHERE g.key = ? AND g.owner_id = ?";
        try( PreparedStatement chekRequest = DB.prepareStatement(CHEK_KEY_GROUP) )
        {
            chekRequest.setLong(1, key);
            chekRequest.setLong(2, ownerID);
            try (ResultSet result = chekRequest.executeQuery())
            {
                if( !result.next() )
                    throw new SQLException("Элемент с таким ключом (key='" + key + "') отсутствует в базе или у вас нет прав на его редактирование");
            }
        }
        DB.setAutoCommit(false);
        String FIND_IDs = "SELECT coordinates_id, group_admin_id FROM study_groups WHERE key = ?";
        long coordinatesID = -1;
        long adminID = -1;
        try( PreparedStatement chekRequest = DB.prepareStatement(FIND_IDs) )
        {
            chekRequest.setLong(1, key);
            try (ResultSet foundIDs = chekRequest.executeQuery())
            {
                if( foundIDs.next() )
                {
                    coordinatesID = foundIDs.getLong("coordinates_id");
                    adminID = foundIDs.getLong("group_admin_id");
                }
                else
                {
                    DB.rollback();
                    return false;
                }
            }
        }

        updateCoordinates(coordinatesID, group.getCoordinates());
        updateAdmin(adminID, group.getAdmin());

        try( PreparedStatement request = DB.prepareStatement(UPDATE_GROUP) )
        {
            request.setLong(1, key);
            request.setString(2, group.getName());
            request.setInt(3, group.getStudentsCount());
            request.setLong(4, group.getShouldBeExp());
            request.setLong(5, formIDs.get(group.getFormOfEducation()));
            request.setLong(6, semesterIDs.get(group.getSemester()));
            request.setLong(7, ownerID);
            request.setLong(8, key);

            int result = request.executeUpdate();
            if( result > 0 )
            {
                DB.commit();
                return true;
            }
            else
            {
                DB.rollback();
                return false;
            }
        }
        catch (SQLException e)
        {
            DB.rollback();
            throw e;
        }
        finally
        {
            DB.setAutoCommit(true);
        }
    }
    private boolean updateCoordinates( long id, Coordinates coordinates ) throws SQLException
    {
        String UPDATE_COORDINATES = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
        try( PreparedStatement request = DB.prepareStatement(UPDATE_COORDINATES) )
        {
            request.setDouble(1, coordinates.getX());
            request.setDouble(2, coordinates.getY());
            request.setDouble(3, id);
            int result = request.executeUpdate();
            return result > 0;
        }
    }
    private boolean updateAdmin( long id, Person updatedAdmin ) throws SQLException
    {
        String UPDATE_ADMIN = "UPDATE person SET " +
                "name = ?, " +
                "birthday = ?, " +
                "passport = ?, " +
                "country_id = ? " +
                "WHERE id = ?";
        try( PreparedStatement request = DB.prepareStatement(UPDATE_ADMIN) )
        {
            request.setString(1, updatedAdmin.getName());
            if (updatedAdmin.getBirthday() != null)
            {
                request.setTimestamp(2, java.sql.Timestamp.valueOf(updatedAdmin.getBirthday()));
            }
            else
            {
                request.setNull(2, java.sql.Types.TIMESTAMP);
            }
            request.setString(3, updatedAdmin.getPassportID());
            request.setInt(4, countryIDs.get(updatedAdmin.getNationality()));
            request.setLong(5, id);
            int result = request.executeUpdate();
            return result > 0;
        }
    }

    @Override
    public boolean removeGroup( long key, long ownerID ) throws SQLException
    {
        String REMOVE_REQUEST = "DELETE FROM study_groups WHERE key = ? AND owner_id = ?";
        try( PreparedStatement request = DB.prepareStatement(REMOVE_REQUEST) )
        {
            request.setLong(1, key);
            request.setLong(2, ownerID);
            int result = request.executeUpdate();
            return result > 0;
        }
    }
    public Hashtable<Long, StudyGroup> loadCollectionFromDB() throws SQLException
    {
        Hashtable<Long, StudyGroup> collection = new Hashtable<>();
        try( Statement statement = DB.createStatement();
             ResultSet res = statement.executeQuery(LOAD_COLLECTION) )
        {
            while( res.next() ) {
                long key = res.getLong("key");

                Coordinates coordinates = new Coordinates(
                        res.getDouble("coord_x"),
                        res.getDouble("coord_y")
                );

                Timestamp bdayTs = res.getTimestamp("admin_birthday");
                String bdayString = (bdayTs != null) ? bdayTs.toLocalDateTime().toString() : null;

                try {
                    Person admin = new Person.Builder()
                            .setName(res.getString("admin_name"))
                            .setBirthday(bdayString)
                            .setWeight(res.getFloat("admin_weight"))
                            .setPassportID(res.getString("admin_passport") == null ? "" : res.getString("admin_passport"))
                            .setNationality(res.getString("admin_nation"))
                            .build();

                    StudyGroup.Builder groupBuilder = new StudyGroup.Builder()
                            .setId(res.getLong("id"))
                            .setName(res.getString("name"))
                            .setCoordinates(coordinates)
                            .setStudCount(res.getInt("students_count"))
                            .setShBeExp(res.getLong("should_be_expelled"))
                            .setFormOfEdu(res.getString("formName"))
                            .setSem(res.getString("semName"))
                            .setAdmin(admin)
                            .setOwner(res.getString("owner"));

                    Timestamp creationTs = res.getTimestamp("creation_date");
                    if (creationTs != null) {
                        groupBuilder.setDateTime(creationTs.toLocalDateTime().atZone(java.time.ZoneId.systemDefault()));
                    } else {
                        groupBuilder.setDateTime(null);
                    }

                    collection.put(key, groupBuilder.build());
                }
                catch (CreationException e)
                {
                    logger.error(e.getMessage());
                }
            }
        }
        logger.info("Коллекция успешно загружена из БД. Всего элементов: " + collection.size());
        return collection;
    }

    private final String READ_FORM_OF_EDUCATIONS = "SELECT id, name FROM form_of_education";
    private final String READ_SEMESTER_ENUMS = "SELECT * FROM semester";
    private final String READ_COUNTRIES = "SELECT * FROM country";

    private final String UPDATE_GROUP = "UPDATE study_groups SET " +
            "key = ?," +
            "name = ?, " +
            "students_count = ?," +
            "should_be_expelled = ?," +
            "form_of_education_id = ?," +
            "semester_id = ?," +
            "owner_id = ? " +
            "WHERE key = ?";
    private final String ADD_NEW_GROUP = "INSERT INTO study_groups " +
            "(key," +
            "name, " +
            "coordinates_id, " +
            "students_count," +
            "should_be_expelled," +
            "form_of_education_id," +
            "semester_id," +
            "group_admin_id," +
            "owner_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private final String ADD_COORDINATES = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
    private final String ADD_PERSON = "INSERT INTO person " +
            "(name," +
            "birthday," +
            "weight," +
            "passport," +
            "country_id) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING id";
    private final String LOAD_COLLECTION =
            "SELECT g.*, " +
                    "c.x AS coord_x, c.y AS coord_y, " +
                    "p.name AS admin_name, p.birthday AS admin_birthday, p.weight AS admin_weight, " +
                    "p.passport AS admin_passport, nation.name AS admin_nation, semester.name AS semName, form_of_education.name AS formName, " +
                    "u.login AS owner " +
                    "FROM study_groups g " +
                    "JOIN coordinates c ON g.coordinates_id = c.id " +
                    "JOIN person p ON g.group_admin_id = p.id " +
                    "JOIN country AS nation ON p.country_id = nation.id " +
                    "JOIN semester ON g.semester_id = semester.id " +
                    "JOIN form_of_education ON g.form_of_education_id = form_of_education.id " +
                    "JOIN users AS u ON g.owner_id = u.id";

    private <T extends Enum<T>> void loadEnumIds( String sqlRequest, Map<T, Integer> enumIDs, Class<T> enumClass, String enumName  ) throws SQLException
    {
        try( ResultSet res = DB.prepareStatement(sqlRequest).executeQuery() )
        {
            while (res.next())
            {
                int id = res.getInt("id");
                String name = res.getString("name");

                try
                {
                    T enumValue = Enum.valueOf(enumClass, name);
                    enumIDs.put(enumValue, id);
                    logger.debug("Добавление новой константы {id: " + id + "; name: " + name + "} в " + enumName);
                }
                catch (IllegalArgumentException e)
                {
                    logger.warn("Обнаружено неизвестное значение в БД для импорта в " + enumName + ": " + name);
                }
            }
        }
        if( enumIDs.isEmpty() )
            logger.warn(enumName + ": Не было загружено ни одного элемента!");
    }
    public void loadFormOfEducationEnumIDs() throws SQLException
    {
        loadEnumIds(READ_FORM_OF_EDUCATIONS, formIDs, FormOfEducation.class, "FormOfEducation");
    }
    public void loadSemesterEnumIDs() throws SQLException
    {
        loadEnumIds(READ_SEMESTER_ENUMS, semesterIDs, Semester.class, "Semester");
    }
    public void loadCountyEnumIDs() throws SQLException
    {
        loadEnumIds(READ_COUNTRIES, countryIDs, Country.class, "Country");
    }
    public void loadEnumIDs() throws SQLException
    {
        loadFormOfEducationEnumIDs();
        loadSemesterEnumIDs();
        loadCountyEnumIDs();
    }
}
