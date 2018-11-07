package com.limefriends.molde.model.database.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.limefriends.molde.model.database.dao.SearchHistoryDao;
import com.limefriends.molde.model.database.schema.SearchHistorySchema;

@Database(version = 1, entities = {SearchHistorySchema.class})
public abstract class MoldeDatabase extends RoomDatabase {

    abstract public SearchHistoryDao getSearchDao();

    public static final String MOLDE_DB = "molde.db";

    public static MoldeDatabase instance;

    public static MoldeDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MoldeDatabase.class, MOLDE_DB).allowMainThreadQueries().build();
        }
        return instance;
    }
}
