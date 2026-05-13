package ru.itmo.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.server.serverInterfaces.StudyGroupDAI;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

public class CollectionLoader
{
    private final CollectionManager collectionManager;
    private final StudyGroupDAI loader;
    private final Logger logger = LoggerFactory.getLogger(CollectionLoader.class);
    public CollectionLoader(CollectionManager collectionManager, StudyGroupDAI loader )
    {
        this.collectionManager = collectionManager;
        this.loader = loader;
    }

    public void loadCollection()
    {
        long count = 0;
        try
        {
            Hashtable<Long, StudyGroup> collection = loader.loadCollectionFromDB();
            for(Map.Entry<Long, StudyGroup> element : collection.entrySet())
            {
                collectionManager.addInMemory(element.getKey(), element.getValue());
                count++;
            }
            logger.info("Успешно загружено " + count + " элементов.");
        }
        catch( SQLException e )
        {
            logger.error("Возникла неизвестная ошибка: коллекция из БД не загружена! " + e.getMessage());
        }
    }
}
