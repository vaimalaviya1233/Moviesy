package com.kpstv.yts.data.db.repository

import androidx.lifecycle.LiveData
import com.kpstv.common_moviesy.extensions.Coroutines
import com.kpstv.yts.data.db.localized.FavouriteDao
import com.kpstv.yts.data.models.Movie
import com.kpstv.yts.data.models.response.Model
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteRepository @Inject constructor(
    private val favDao: FavouriteDao
) {

    suspend fun saveMovie(data: Model.response_favourite) {
        favDao.upsert(data)
    }

    fun isMovieFavouriteLive(movieId: Int) = favDao.isDataExistLive(movieId)
    suspend fun isMovieFavourite(movieId: Int) = favDao.isDataExist(movieId)

    /**
     * @return true if current [movie] is marked as favourite
     */
    suspend fun toggleFavourite(movie: Movie): Boolean {
        return if (favDao.isDataExist(movie.id)) {
            favDao.delete(movie.id)
            false
        } else {
            favDao.upsert(
                Model.response_favourite(
                    movieId = movie.id,
                    imdbCode = movie.imdb_code,
                    title = movie.title,
                    imageUrl = movie.medium_cover_image,
                    runtime = movie.runtime,
                    rating = movie.rating,
                    year = movie.year
                )
            )
            true
        }
    }

    suspend fun deleteMovie(movieId: Int) {
        favDao.delete(movieId)
    }

    fun getAllMovieId(): LiveData<List<Model.response_favourite>> {
        return favDao.getAllDataLive()
    }

    /** @Note
     *
     * Following are the methods that are used to export this table.
     *
     * This repository falls under user data which can be backed up or
     * restored.
     */

    suspend fun exportAllDataToJSON(): JSONArray {
        val array = JSONArray()
        favDao.getAllData().forEach { model ->
            val item = JSONObject().apply {
                put(model::imageUrl.name, model.imageUrl)
                put(model::movieId.name, model.movieId)
                put(model::rating.name, model.rating)
                put(model::runtime.name, model.runtime)
                put(model::title.name, model.title)
                put(model::year.name, model.year)
                put(model::imdbCode.name, model.imdbCode)
            }
            array.put(item)
        }
        return array
    }

    suspend fun importAllDataFromJSON(jsonData: String) {
        val jsonObject = JSONObject(jsonData)
        val jsonArray = jsonObject.getJSONArray(FAVOURITES)
        for (i in 0 until jsonArray.length()) {
            val favObject = jsonArray.getJSONObject(i)
            saveDataToRepository(favObject)
        }
    }

    private suspend fun saveDataToRepository(jsonObject: JSONObject) {
        val favModel = Model.response_favourite(
            movieId = jsonObject.getInt(Model.response_favourite::movieId.name),
            title = jsonObject.getString(Model.response_favourite::title.name),
            imdbCode = jsonObject.getString(Model.response_favourite::imdbCode.name),
            rating = jsonObject.getDouble(Model.response_favourite::rating.name),
            runtime = jsonObject.getInt(Model.response_favourite::runtime.name),
            year = jsonObject.getInt(Model.response_favourite::year.name),
            imageUrl = jsonObject.getString(Model.response_favourite::imageUrl.name)
        )
        if (!favDao.isDataExist(favModel.movieId))
            favDao.upsert(favModel)
    }

    companion object {
        const val FAVOURITES = "favourites"
    }
}