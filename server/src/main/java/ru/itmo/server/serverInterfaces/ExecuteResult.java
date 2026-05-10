package ru.itmo.server.serverInterfaces;

import ru.itmo.lab.common.model.StudyGroup;
import java.util.List;

public interface ExecuteResult
{
    String getMessage();
    boolean isSuccess();
    List<StudyGroup> getCollection();
}
