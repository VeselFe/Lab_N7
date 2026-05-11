package ru.itmo.lab.common.fileManagement;

import java.util.Hashtable;

/**
 * Абстрактный класс менеджера для работы с файлами.
 * Содержит шаблоны методов выполнения функций сохранения в файл, выгрузки из файла.
 *
 * @param <K>
 * @param <V>
 */
abstract public class FileManager<K, V>
{
    protected String fileName;

    public FileManager( String newFileName )
    {
        fileName = newFileName;
    }
    abstract public void save( Hashtable<K, V> data );
    abstract public Hashtable<K, V> load();
}
