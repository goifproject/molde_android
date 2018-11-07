package com.limefriends.molde.model.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.limefriends.molde.model.database.schema.SearchHistorySchema;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface SearchHistoryDao {

    @Insert
    long insert(SearchHistorySchema searchEntity);

    @Query("SELECT * FROM search")
    Maybe<List<SearchHistorySchema>> getAll();

    @Delete
    int deleteAll(List<SearchHistorySchema> schemas);

    @Delete
    int deleteSingle(SearchHistorySchema schema);
}
