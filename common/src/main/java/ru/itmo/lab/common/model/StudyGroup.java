package ru.itmo.lab.common.model;

import ru.itmo.lab.common.interfaces.IdGeneratorInterface;
import ru.itmo.lab.common.myEnums.FormOfEducation;
import ru.itmo.lab.common.myEnums.Semester;
import ru.itmo.lab.common.myExceptions.CreationException;

import java.io.Serializable;
import java.time.ZonedDateTime;


/**
 * Основной класс учебной группы для управления коллекцией в приложении.
 * Содержит все обязательные поля.
 * Поддерживает паттерн Builder для создания и Comparable для сортировки по ID.
 *
 * @see Coordinates
 * @see Person
 * @see FormOfEducation
 * @see Semester
 */
public class StudyGroup implements Comparable<StudyGroup>, Serializable
{
    private static final long serialVersionUID = 666L;
    /** Уникальный идентификатор группы. Значение > 0, генерируется автоматически, уникально. */
    private Long id = null;
    /** Название группы. Поле не может быть null, строка не пустая. */
    private String name;
    /** Координаты группы. Поле не может быть null. */
    private Coordinates coordinates;
    /** Дата создания группы. Поле не может быть null, генерируется автоматически. */
    private java.time.ZonedDateTime creationDate;
    /** Количество студентов. Значение > 0, поле не может быть null. */
    private Integer studentsCount;
    /** Количество студентов которых необходимо отчислить. Значение > 0, поле не может быть null. */
    private Long shouldBeExpelled;
    /** Форма обучения. Поле не может быть null. */
    private FormOfEducation formOfEducation;
    /** Семестр обучения. Поле не может быть null. */
    private Semester semesterEnum;
    /** Администратор группы. Поле не может быть null. */
    private Person groupAdmin;
    /** Генератор уникального ID группы.  */
    static private IdGeneratorInterface idGenerator;
    /**
     * Устанавливает ID и текущую дату создания автоматически.
     * Копирует все поля из переданного "строителя".
     *
     * @param builder объект Builder с заполненными полями
     */
    private StudyGroup( Builder builder )
    {
        this.id = builder.id;
        this.creationDate = ZonedDateTime.now();

        this.name = builder.name;
        this.coordinates = builder.coordinates;
        this.studentsCount = builder.studentsCount;
        this.shouldBeExpelled = builder.shouldBeExpelled;
        this.formOfEducation = builder.formOfEducation;
        this.semesterEnum = builder.semesterEnum;
        this.groupAdmin = builder.groupAdmin;
    }

    public static void setIdGenerator( IdGeneratorInterface newGenerator )
    {
        idGenerator = newGenerator;
    }
    public void generateGroupID()
    {
        id = idGenerator.generateUniqueId();
    }
    /**
     * Внутренний статический класс Builder для создания объектов StudyGroup.
     * Позволяет устанавливать поля поэтапно с проверкой полеей.
     * Автоматически генерирует уникальный ID при вызове build().
     */
    public static class Builder
    {
        private Long id = null;
        private String name;
        private Coordinates coordinates;
        private Integer studentsCount;
        private Long shouldBeExpelled;
        private FormOfEducation formOfEducation;
        private Semester semesterEnum;
        private Person groupAdmin;

        /**
         * Создает и возвращает полностью корректный объект StudyGroup.
         * Генерирует уникальный ID через CollectionManager.
         *
         * @return готовый объект StudyGroup
         * @throws CreationException если поля не прошли валидацию
         */
        public StudyGroup build()
        {
            if(name.isEmpty() || coordinates == null || studentsCount == null || shouldBeExpelled == null || formOfEducation == null || semesterEnum == null || groupAdmin == null)
            {
                throw new CreationException("Не все поля заполнены!");
            }
            return new StudyGroup( this );
        }
        /**
         * Устанавливает название группы с проверкой на корректность введнных данных.
         *
         * @param newName новое название группы
         * @return this для цепочки вызовов
         * @throws CreationException если название null или пустое
         */
        public Builder setName( String newName )
        {
            StudyGroup.GroupValidations.validateName(newName);

            this.name = newName;
            return this;
        }
        /**
         * Устанавливает координаты из строки формата "X Y".
         *
         * @param XY координаты в формате "X Y"
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setCoordinates( String XY )
        {
            coordinates = GroupParsers.parseCoordinates(XY);
            return this;
        }
        /**
         * Устанавливает количество студентов. Случай для инициализации по строчному вводу
         *
         * @param newStrStudCount новое количество студентов
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setStudCount( String newStrStudCount )
        {
            studentsCount = GroupParsers.parseStudCount(newStrStudCount);
            return this;
        }
        /**
         * Устанавливает количество студентов. Случай для инициализации по числовому вводу
         *
         * @param newStudCount новое количество студентов
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setStudCount( Integer newStudCount )
        {
            StudyGroup.GroupValidations.validateIntStudCount(newStudCount);

            studentsCount = newStudCount;
            return this;
        }
        /**
         * Устанавливает количество студентов на отчисление. Случай для инициализации по строковому вводу
         *
         * @param newStrShBeExp новое количество студентов на отчисление
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setShBeExp( String newStrShBeExp )
        {
            shouldBeExpelled = GroupParsers.parseShouldBeExpelled(newStrShBeExp);
            return this;
        }
        /**
         * Устанавливает количество студентов на отчисление. Случай для инициализации по числовому вводу
         *
         * @param newShBeExp новое количество студентов на отчисление
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setShBeExp( Long newShBeExp )
        {
            StudyGroup.GroupValidations.validateLongShouldBeExpelled(newShBeExp);
            shouldBeExpelled = newShBeExp;
            return this;
        }
        /**
         * Устанавливает форму обучения группы.
         *
         * @param newStrForm новая форма обучения группы
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setFormOfEdu( String newStrForm )
        {
            formOfEducation = GroupParsers.parseFormOfEdu(newStrForm);
            return this;
        }
        /**
         * Устанавливает семестр обучения группы.
         *
         * @param newStrSem новый семестр обучения группы
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setSem( String newStrSem )
        {
            semesterEnum = GroupParsers.parseSemester(newStrSem);
            return this;
        }
        /**
         * Устанавливает администратора группы.
         *
         * @param newAdmin новый Администратор группы
         * @return this для цепочки вызовов
         * @throws CreationException если формат некорректен
         */
        public Builder setAdmin( Person newAdmin )
        {
            StudyGroup.GroupValidations.validateAdmin(newAdmin);
            groupAdmin = newAdmin;
            return this;
        }
    }

    /**
     * Обновляет название группы с валидацией.
     *
     * @param newName новое название
     * @throws CreationException если название null или пустое
     */
    public void updateName( String newName )
    {
        GroupValidations.validateName(newName);
        this.name = newName;
    }
    public void updateCoordinates( String XY )
    {
        coordinates =  GroupParsers.parseCoordinates(XY);
    }
    public void updateCoordinates( Coordinates newCoordinates )
    {
        coordinates = newCoordinates;
    }
    public void updateStudCount( String newStrStudCount )
    {
        studentsCount = GroupParsers.parseStudCount(newStrStudCount);
    }
    public void updateStudCount( Integer newStudCount )
    {
        GroupValidations.validateIntStudCount(newStudCount);
        studentsCount = newStudCount;
    }
    public void updateShBeExp( String newStrShBeExp )
    {
        shouldBeExpelled = GroupParsers.parseShouldBeExpelled(newStrShBeExp);
    }
    public void updateShBeExp( Long newShBeExp )
    {
        GroupValidations.validateLongShouldBeExpelled(newShBeExp);
        shouldBeExpelled = newShBeExp;
    }
    public void updateFormOfEdu( String newStrForm )
    {
        formOfEducation = GroupParsers.parseFormOfEdu(newStrForm);
    }
    public void updateFormOfEdu( FormOfEducation newFormOfEdu )
    {
        formOfEducation = newFormOfEdu;
    }
    public void updateSem( String newStrSem )
    {
        semesterEnum = GroupParsers.parseSemester(newStrSem);
    }
    public void updateSem( Semester newSemester )
    {
        semesterEnum = newSemester;
    }
    public void updateAdmin( Person newAdmin )
    {
        GroupValidations.validateAdmin(newAdmin);
        groupAdmin = newAdmin;
    }

    public static class GroupParsers
    {
        static Coordinates parseCoordinates( String XY )
        {
            if (XY == null || XY.trim().isEmpty())
            {
                throw new CreationException("Некорректные данные для создания: поле 'Координаты' некорректно");
            }

            String[] inputCoord = XY.trim().split("\\s+");
            if (inputCoord.length != 2)
            {
                throw new CreationException("Координаты: ожидается 2 числа(X Y)");
            }
            Double X = null;
            double Y;
            try
            {
                X = Double.parseDouble(inputCoord[0]);
                Y = Double.parseDouble(inputCoord[1]);
            } catch (NumberFormatException e) { throw new CreationException("Некорректные данные для создания: поле 'Координаты' некорректно"); }

            return new Coordinates(X, Y);
        }
        static Integer parseStudCount( String newStrStudCount )
        {
            if (newStrStudCount == null || newStrStudCount.trim().isEmpty())
            {
                throw new CreationException("Некорректные данные для создания: поле 'Количество студентов' некорректно(" + newStrStudCount + ")");
            }

            Integer newStudCount = null;
            try
            {
                newStudCount = Integer.valueOf(newStrStudCount.trim());
            }
            catch (NumberFormatException e) { throw new CreationException("Некорректные данные для создания: поле 'Количество студентов' некорректно('" + newStudCount + "') (ParseData)"); }
            if ( newStudCount <= 0 ) { throw new CreationException("Некорректные данные для создания: поле 'Количество студентов' некорректно: Значение должно быть больше нуля");}

            return newStudCount;
        }
        static Long parseShouldBeExpelled( String newStrShBeExp )
        {
            if (newStrShBeExp == null || newStrShBeExp.trim().isEmpty())
            {
                throw new CreationException("Некорректные данные для создания: поле 'Количество студентов на отчисление' некорректно");
            }
            Long newShBeExp = null;
            try
            {
                newShBeExp = Long.valueOf(newStrShBeExp.trim());
            } catch (NumberFormatException e) { throw new CreationException("Некорректные данные для создания: поле 'Количество студентов на отчисление' некорректно"); }
            if ( newShBeExp <= 0 ) { throw new CreationException("Некорректные данные для создания: поле 'Количество студентов на отчисление' некорректно: Значение должно быть больше нуля");}

            return newShBeExp;
        }
        static FormOfEducation parseFormOfEdu( String newStrForm )
        {
            if (newStrForm == null || newStrForm.trim().isEmpty())
            {
                throw new CreationException("Некорректные данные для создания: поле 'Форма обучения' некорректно");
            }

            FormOfEducation newForm = null;
            try
            {
                newForm = FormOfEducation.valueOf(newStrForm.toUpperCase().trim());
                return newForm;
            }
            catch (IllegalArgumentException e)
            {
                throw new CreationException("Некорректные данные для создания: поле 'Форма обучения' некорректно");
            }
        }
        static Semester parseSemester( String newStrSem )
        {
            if (newStrSem == null || newStrSem.trim().isEmpty())
            {
                throw new CreationException("Некорректные данные для создания: поле 'Семестр' некорректно");
            }

            Semester newSem = null;
            try
            {
                newSem = Semester.valueOf(newStrSem.toUpperCase().trim());
                return newSem;
            }
            catch (IllegalArgumentException e)
            {
                throw new CreationException("Некорректные данные для создания: поле 'Семестр' некорректно");
            }
        }
    }
    public static class GroupValidations
    {
        public static void validateName( String name )
        {
            if (name == null || name.trim() == "") { throw new CreationException("Некорректные данные для создания: поле 'имя' некорректно"); }
        }
        public static void validateIntStudCount( Integer newStudCount )
        {
            if ( newStudCount == null || newStudCount <= 0 )
            {
                throw new CreationException("Некорректные данные для создания: поле 'Количество студентов' некорректно('" + newStudCount + "')");
            }
        }
        public static void validateLongShouldBeExpelled( Long newShBeExp )
        {
            if ( newShBeExp == null || newShBeExp <= 0 )
            {
                throw new CreationException("Некорректные данные для создания: поле 'Количество студентовна отчисление' некорректно");
            }
        }
        public static void validateAdmin( Person newAdmin )
        {
            if( newAdmin == null )
            {
                throw new CreationException("Некорректные данные для создания: поле 'Админ группы' некорректно: поле должно быть заполнено!");
            }
        }
    }

    /**
     * Возвращает уникальный идентификатор группы.
     *
     * @return ID группы (> 0)
     */
    public long getId()
    {
        return id;
    }
    /**
     * Возвращает название группы.
     *
     * @return name группы
     */
    public String getName() { return name; }
    /**
     * Возвращает количество студентов в группе.
     *
     * @return studentsCount группы (> 0)
     */
    public Integer getStudentsCount() { return studentsCount; }
    /**
     * Возвращает количество студентов на отчисление в группе.
     *
     * @return shouldBeExpelled группы (> 0)
     */
    public Long getShouldBeExp() { return shouldBeExpelled; }
    /**
     * Возвращает семестр обучения группы.
     *
     * @return semesterEnum
     */
    public Semester getSemester() { return semesterEnum; }
    /**
     * Возвращает админа группы.
     *
     * @return Person
     */
    public Person getAdmin() { return groupAdmin; }
    /**
     * Возвращает подробную текстовую информацию о группе.
     * Форматирует все поля в читаемый вид.
     *
     * @return строка с информацией о группе
     */
    public String getInformation()
    {
        return  "----------------------------------------------------" +
                "\nID: " + id +
                "\nName: " + name +
                "\nCoordinates: X = " + coordinates.getX() + ", Y = " + coordinates.getY() +
                "\nDate of creation: " + creationDate +
                "\nQuantity of student: " + studentsCount +
                "\nQuantity of student, who must be expelled: " + shouldBeExpelled +
                "\nForm of education: " + formOfEducation +
                "\nSemester: " + semesterEnum +
                "\nAdmin of group: " + groupAdmin.getName() +
                "\n" +
                "----------------------------------------------------";
    }
    /**
     * Сравнивает группы по ID для сортировки.
     *
     * @param otherGroup другая группа для сравнения
     * @return отрицательное/нулевое/положительное число
     */
    @Override
    public int compareTo( StudyGroup otherGroup )
    {
        return Long.compare( this.getId(), otherGroup.getId() );
    }
}
