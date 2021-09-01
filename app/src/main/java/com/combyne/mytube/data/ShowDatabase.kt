package com.combyne.mytube.data

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Inject

@Database(entities = [Show::class], version = 1)
abstract class ShowDatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
    class Callback @Inject constructor() : RoomDatabase.Callback()
}