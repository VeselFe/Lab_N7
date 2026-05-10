package ru.itmo.lab.common.interfaces;

/**
 * Интерфейс для генерации уникальных ID для StudyGroup.
 * Позволяет менять стратегию генерации без изменения CollectionManager.
 */
public interface IdGeneratorInterface
{
    /**
     * Генерирует следующий уникальный ID.
     * @return уникальный long ID
     */
    long generateUniqueId();
}
