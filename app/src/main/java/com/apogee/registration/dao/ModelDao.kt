package com.apogee.registration.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apogee.registration.model.getmodel.Model
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {


    @Insert(onConflict =OnConflictStrategy.REPLACE)
    fun insertData(type:List<Model>)

    @Query("delete from ModelInfo")
    fun deleteAll()

    @Query("Select * from ModelInfo")
    fun selectDataBase(): Flow<List<Model>>

}