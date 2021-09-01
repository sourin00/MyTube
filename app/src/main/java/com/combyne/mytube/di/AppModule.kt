package com.combyne.mytube.di

import android.app.Application
import androidx.room.Room
import com.combyne.mytube.data.ShowDao
import com.combyne.mytube.data.ShowDatabase
import com.combyne.mytube.network.TvShowManagerAPI
import com.combyne.mytube.repository.ShowsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun getDatabase(app: Application, callback: ShowDatabase.Callback) =
        Room.databaseBuilder(app, ShowDatabase::class.java, "show_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun getShowDao(db: ShowDatabase) = db.showDao()

    @Provides
    @Singleton
    fun getApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun getShowsRepository(showDao: ShowDao, tvShowManagerAPI: TvShowManagerAPI) =
        ShowsRepository(showDao, tvShowManagerAPI)

    @Provides
    @Singleton
    fun getService() = TvShowManagerAPI()
}