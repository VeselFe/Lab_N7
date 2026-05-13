package ru.itmo.server.serverInterfaces;

import ru.itmo.lab.common.model.StudyGroup;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

public interface StudyGroupDAI
{
    long addGroup( long key, StudyGroup newGroup, long ownerID ) throws SQLException;
    boolean updateGroup( long key, StudyGroup group, long ownerID ) throws SQLException;
    boolean removeGroup( long groupID, long ownerID ) throws SQLException;
    Hashtable<Long, StudyGroup> loadCollectionFromDB() throws SQLException;
}
