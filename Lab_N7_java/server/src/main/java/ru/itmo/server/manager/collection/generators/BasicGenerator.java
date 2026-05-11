package ru.itmo.server.manager.collection.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.lab.common.interfaces.IdGeneratorInterface;
import ru.itmo.server.manager.collection.CollectionManager;
import ru.itmo.lab.common.model.StudyGroup;
import ru.itmo.lab.common.myExceptions.CreationException;

public class BasicGenerator implements IdGeneratorInterface {
    private CollectionManager manager;
    private Logger logger = LoggerFactory.getLogger(BasicGenerator.class);

    public BasicGenerator(CollectionManager newManager) {
        manager = newManager;
    }

    /**
     * Генерирует уникальный ID, не совпадающий с ID существующих групп.
     *
     * <p>Алгоритм: последовательный перебор с начала до первого свободного ID.</p>
     *
     * @return первый свободный положительный ID
     * @throws CreationException если все ID заняты (переполнение)
     */

    @Override
    public long generateUniqueId()
    {
        if (manager == null)
        {
            return 1L;
        }

        long id = 1;
        boolean flag = true, searching = true;
        while (searching)
        {
            flag = true;
            for (StudyGroup element : manager.getStudyGroups().values())
            {
                if( id == element.getId() )
                {
                    flag = false;
                    break;
                }
            }
            if (flag)
            {
                searching = false;
            }
            else
            {
                id++;
                if (id < 0)
                {
                    throw new CreationException("Нет свободных ID");
                }
            }
        }
        logger.info("Сгенерированный ID='" + id + "'");
        return id;
    }
}
