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

    fun queryShows(listener: StateListener): Flow<List<Show>> {
        CoroutineScope(Dispatchers.Main).launch {
            listener.onComplete(StateListener.State.LOADING)
            try {
                val data =
                    tvShowManagerAPI.getApolloClient().query(MoviesQuery()).await().data?.movies?.edges
                data?.forEach {
                    it?.node?.apply {
                        val releaseDate = DateUtils.getTimeStamp(releaseDate as String)
                        val show = showsDao.isExist(id)
                        val showRefreshed = Show(title, releaseDate, seasons ?: 0.0, id)
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
        return showsDao.getShows()
    }

    suspend fun createMovie(title: String, releaseDate: String, seasons: Double,listener: StateListener){
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