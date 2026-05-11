package ru.itmo.server.manager.collection;

import ru.itmo.lab.common.model.StudyGroup;

import java.util.Comparator;

/**
 * Реализация comparator для нахождения групп по относительному количеству студентов
 */
public class StudyGroupByStudentsComparator implements Comparator<StudyGroup>
{
    @Override
    public int compare( StudyGroup group1, StudyGroup group2 )
    {
        return Integer.compare(group1.getStudentsCount(), group2.getStudentsCount());
    }
}
