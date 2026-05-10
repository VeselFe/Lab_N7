package ru.itmo.lab.common.myRecords;

import ru.itmo.lab.common.model.Person;

import java.util.List;

public record Lab5FieldDescriptor()
{
    public static final List<FieldDescriptor> NEW_PERSON_FIELDS = List.of(
            new FieldDescriptor("Name",
                    "Введите Имя студента: "),
            new FieldDescriptor("Birthday",
                    "Введите Дату рождения (формата YYYY-MM-DDTHH:MM:SS) : "),
            new FieldDescriptor("Weight",
                    "Введите вес студента: "),
            new FieldDescriptor("passport ID",
                    "Введите паспортные данные студента* (формат 10 цифр) : "),
            new FieldDescriptor("nationality",
                    "  ВАРИАНТЫ НАЦИОНАЛЬНОСТИ:" +
                            "============================\n" +
                            "RUSSIA       | (Россия)\n" +
                            "USA          | (США)\n" +
                            "THAILAND     | (Тайланд)\n" +
                            "SOUTH_KOREA  | (Южная Корея)\n" +
                            "============================\n" +
                            "Введите национальность студента: ")
            );
    public static final List<FieldDescriptor> NEW_GROUP_FIELDS = List.of(
            new FieldDescriptor("Name",
                    "Введите название новой группы: "),
            new FieldDescriptor("Coordinates",
                    "Введите координаты группы X Y(два числа, разделенные пробелом): "),
            new FieldDescriptor("StudentCount",
                    "Введите количество студетов в учебной группе: "),
            new FieldDescriptor("ShoudBeExpelled",
                    "Введите какое количество студетов следует отчислить: "),
            new FieldDescriptor("FormOfEducation",
                    " ВОЗМОЖНЫЕ ФОРМЫ ОБУЧЕНИЯ:\n" +
                            "============================\n" +
                            "DISTANCE_EDUCATION    | (Дистанционное обучение)\n" +
                            "FULL_TIME_EDUCATION   | (Очное дневное)\n" +
                            "EVENING_CLASSES       | (Очное вечернее)\n" +
                            "============================\n" +
                            "Введите форму обучения группы: "),
            new FieldDescriptor("Semester",
                    "    ВОЗМОЖНЫЕ СЕМЕСТРЫ:\n" +
                            "============================\n" +
                            "FIRST   | (Первый семестр)\n" +
                            "SECOND  | (Второй семестр)\n" +
                            "THIRD   | (Третий семестр)\n" +
                            "FIFTH   | (Пятый семестр)\n" +
                            "EIGHTH  | (Восьмой семестр)\n" +
                            "============================\n" +
                            "Введите семестр: ")
    );
    public static final List<UpdatedFieldDescriptor> UPDATED_FIELDS = List.of(
            new UpdatedFieldDescriptor("Name",
                             "Введите новое имя: ",
                         "updateName",
                                     String.class),
            new UpdatedFieldDescriptor("Coordinates",
                            "Введите новые координаты(X Y): ",
                        "updateCoordinates",
                                     String.class),
            new UpdatedFieldDescriptor("StudentCount",
                            "Введите новое новое количество студентов: ",
                        "updateStudCount",
                    String.class),
            new UpdatedFieldDescriptor("ShoudBeExpelled",
                            "Введите новое количество студентов, которых надо отчислить: ",
                        "updateShBeExp" ,
                    String.class),
            new UpdatedFieldDescriptor("FormOfEducation",
                            "Введите новую форму форму обучения(DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES): ",
                        "updateFormOfEdu",
                    String.class),
            new UpdatedFieldDescriptor("Semester",
                            "Введите семестр (FIRST, SECOND, THIRD, FIFTH, EIGHTH): ",
                        "updateSem",
                    String.class),
            new UpdatedFieldDescriptor("Group Admin",
                            "Нужно заполнить анкету для нового админа: \n",
                        "updateAdmin",
                    Person.class)
    );
}
