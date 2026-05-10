package ru.itmo.lab.common.model;

import ru.itmo.lab.common.myEnums.Country;
import ru.itmo.lab.common.myExceptions.CreationException;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Класс для представления администратора учебной группы.
 * Содержит персональные данные человека согласно требованиям.
 * Использует Builder паттерн для создания объектов с обязательной валидацией.
 *
 * @see Country
 */
public class Person implements Serializable
{
    private static final long serialVersionUID = 666L;
    /** Имя человека. Поле не может быть null, строка не может быть пустой. */
    private String name;
    /** Дата рождения. Поле в формате LocalDateTime. */
    private java.time.LocalDateTime birthday;
    /** Вес человека. Значение должно быть больше 0. */
    private float weight;
    /** Номер паспорта. Длина ровно 8 символов или null. */
    private String passportID;
    /** Национальность. Enum из myEnums.Countryl. */
    private Country nationality;

    /**
     * Приватный конструктор для "строителя".
     * Копирует все поля из переданного билдера с предварительной валидацией.
     *
     * @param builder объект Builder с заполненными полями
     */
    private Person( Builder builder )
    {
        this.name = builder.name;
        this.birthday = builder.birthday;
        this.weight = builder.weight;
        this.passportID = builder.passportID;
        this.nationality = builder.nationality;
    }

    /**
     * Внутренний статический класс Builder для создания объектов Person.
     * Обеспечивает цепочечные вызовы с автоматической проверкой на корректность полей.
     * Все обязательные проверки выполняются на этапе сборки.
     */
    public static class Builder
    {
        private String name;
        private java.time.LocalDateTime birthday;
        private float weight;
        private String passportID;
        private Country nationality;

        /**
         * Создает и возвращает валидированный объект Person.
         *
         * @return готовый объект Person
         * @throws CreationException если поля не прошли валидацию при парсинге
         */
        public Person build()
        {
            if(name.isEmpty() || birthday == null || nationality == null)
            {
                throw new CreationException("Не все параметры заданы!");
            }
            return new Person( this );
        }

        public Builder setName( String newName )
        {
            Validations.validateName(newName);
            this.name = newName;
            return this;
        }
        public Builder setBirthday( String newBirthday )
        {
            this.birthday = Parsers.parseBirthday(newBirthday);
            return this;
        }
        public Builder setWeight( String newWeight )
        {
            this.weight = Parsers.parseWeight(newWeight);
            return this;
        }
        public Builder setPassportID( String newPassport )
        {
            Validations.validatePassport(newPassport);
            this.passportID = newPassport;
            return this;
        }
        public Builder setNationality( String newNation )
        {
            this.nationality = Parsers.parseNation(newNation);
            return this;
        }
    }

    public void updateName( String newName )
    {
        Validations.validateName(newName);
        this.name = newName;
    }
    public void updateBirthday( String strBirthday )
    {
        this.birthday = Parsers.parseBirthday(strBirthday);
    }
    public void updateWeight( String newStrWeight )
    {
        this.weight = Parsers.parseWeight(newStrWeight);
    }
    public void updatePassportID( String newPassp )
    {
        Validations.validatePassport(newPassp);
        this.passportID = newPassp;
    }
    public void updateNation( String newStrNation )
    {
        this.nationality = Parsers.parseNation(newStrNation);
    }

    /**
     * Вспомогательный класс для валидации входных данных Person.
     * Содержит статические методы проверки формата полей согласно требованиям.
     * Предназначен только для внутреннего использования.
     *
     * @see Person.Builder
     */
    public static class Validations
    {
        public static void validateName( String newName )
        {
            if (newName == null || newName.trim() == "") { throw new CreationException("Некорректные данные для создания: поле 'имя' некорректно"); }
        }
        public static void validatePassport( String newPassp )
        {
            newPassp = newPassp.trim();
            if (newPassp != null && !newPassp.isEmpty() && newPassp.trim().length() < 8)
            {
                throw new CreationException("Некорректные данные для создания: поле 'паспорт' некорректно: '" + newPassp + "' - должно быть больше 8 цифр");
            }
        }
        public static void validateWeight( float newWeight )
        {
            if (newWeight <= 0)
            {
                throw new CreationException("Некорректные данные для создания: значения поля 'вес' должнобыть больше 0!");
            }
        }
    }
    /**
     * Вспомогательный класс для парсинга строковых значений в типы Person.
     * Содержит статические методы преобразования с обработкой исключений.
     * Предназначен только для внутреннего использования Builder.
     *
     * @see Person.Builder
     */
    private static class Parsers
    {
        private static java.time.LocalDateTime parseBirthday( String strBirthday )
        {
            java.time.LocalDateTime newBirthday = null;
            try
            {
                newBirthday = LocalDateTime.parse(strBirthday.trim());
                return newBirthday;
            }
            catch (java.time.format.DateTimeParseException e)
            {
                throw new CreationException("неверный формат даты");
            }
        }
        private static float parseWeight( String newStrWeight )
        {
            float newWeight;
            try
            {
                newWeight = Float.valueOf(newStrWeight.trim());
                if (newWeight <= 0)
                {
                    throw new CreationException("Некорректные данные для создания: значения поля 'вес' должнобыть больше 0!");
                }
                return newWeight;
            }
            catch (NumberFormatException e)
            {
                throw new CreationException("Некорректные данные для создания: поле 'вес' некорректно");
            }
        }
        private static Country parseNation( String newStrNation)
        {
            if (newStrNation == null || newStrNation.trim().isEmpty()) {
                throw new CreationException("Некорректные данные для создания: поле 'Национальность' некорректно: '" + newStrNation + "'");
            }
            Country newNation = null;
            try
            {
                newNation = Country.valueOf(newStrNation.toUpperCase().trim());
                return newNation;
            }
            catch (IllegalArgumentException e)
            {
                throw new CreationException("Некорректные данные для создания: поле 'Национальность' некорректно (Введенная национальность '" + newStrNation + "' не распознана)");
            }
        }
    }

    public String getName()
    {
        return name;
    }
    public float getWeight() { return weight; }
    public String getPassportID() { return passportID; }
}
