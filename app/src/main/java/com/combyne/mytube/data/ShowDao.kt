package com.combyne.mytube.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {

    @Query("SELECT *from show")
    fun getShows(): Flow<List<Show>>

    @Query("select *from show where remoteID=:remID limit 1")
    suspend fun isExist(remID: String) : Show

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(show: Show)

    @Update
    suspend fun update(show: Show)

    @Delete
    suspend fun delete(show: Show)
}