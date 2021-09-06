package com.combyne.mytube.repository

import com.apollographql.apollo.coroutines.await
import com.combyne.mytube.CreateMovieMutation
import com.combyne.mytube.MoviesQuery
import com.combyne.mytube.data.Show
import com.combyne.mytube.data.ShowDao
import com.combyne.mytube.network.TvShowManagerAPI
import com.combyne.mytube.util.DateUtils
import com.combyne.mytube.util.StateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShowsRepository @Inject constructor(
    private val showsDao: ShowDao,
    private val tvShowManagerAPI: TvShowManagerAPI
) {

    // Make GraphQL query 'movies' to fetch most recent 10 movies.
    // Pagination not implemented yet.
    fun queryShows(listener: StateListener): Flow<List<Show>> {
        CoroutineScope(Dispatchers.Main).launch {
            //show loader
            listener.onComplete(StateListener.State.LOADING)
            try {
                val data =
                    tvShowManagerAPI.getApolloClient().query(MoviesQuery())
                        .await().data?.movies?.edges
                data?.forEach {
                    it?.node?.apply {
                        // cache data fetched from server in local Sqlite DB and
                        // then only display in UI to maintain Single Source of Truth principle.
                        val releaseDate = DateUtils.getTimeStamp(releaseDate as String)
                        val show = showsDao.isExist(id)
                        val showRefreshed = Show(title, releaseDate, seasons ?: 0.0, id)
                        // detect existing data by ID from server and update it or insert fresh
                        if (show != null) {
                            showsDao.update(showRefreshed)
                        } else showsDao.insert(showRefreshed)
                    }
                }
                listener.onComplete(StateListener.State.SUCCESS)
            } catch (e: Exception) {
                listener.onComplete(StateListener.State.ERROR)
            }
        }
        // return cached list of shows/movies from local DB
        return showsDao.getShows()
    }

    // Make GraphQL mutation 'createMovie' to send new TV Show data to server.
    // Insert the new Show object in local DB after success from Server.
    // View automatically updates as observing LiveData on the show table.
    suspend fun createMovie(
        title: String,
        releaseDate: String,
        seasons: Double,
        listener: StateListener
    ) {
        try {
            listener.onComplete(StateListener.State.LOADING)
            val data = tvShowManagerAPI.getApolloClient()
                .mutate(CreateMovieMutation(title, seasons, releaseDate))
                .await().data?.createMovie?.movie
            var show: Show? = null
            data?.apply {
                show = Show(
                    title,
                    DateUtils.getTimeStamp(releaseDate),
                    seasons,
                    id,
                    DateUtils.getTimeStamp(createdAt as String)
                )
            }
            show?.let { showsDao.insert(it) }
            listener.onComplete(StateListener.State.SUCCESS)
        } catch (e: Exception) {
            listener.onComplete(StateListener.State.ERROR)
        }
    }
}