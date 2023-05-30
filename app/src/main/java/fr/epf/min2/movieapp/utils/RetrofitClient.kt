package fr.epf.min2.movieapp.utils

import retrofit2.Call

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


object RetrofitClient {

    interface MovieApiService {
        @GET("movie/{id}")
        fun getMovieDetails(
            @Path("id") movieId: Int,
            @Query("api_key") apiKey: String
        ): Call<Movie>
        @GET("discover/movie")
        fun getMoviesByGenre(
            @Query("api_key") apiKey: String,
            @Query("with_genres") genreId: Int
        ): Call<ResultsResearch>
        @GET("trending/movie/day")
        fun getPopularMovies(
            @Query("api_key") apiKey: String,
            @Query("language") language: String = "en-US"
        ): Call<ResultsResearch>
        @GET("search/movie")
        fun findMovies(
            @Query("api_key") apiKey: String,
            @Query("query") query: String
        ): Call<ResultsResearch>
        @GET("movie/{id}/recommendations")
        fun getRecommendations(
            @Path("id") movieId: Int,
            @Query("api_key") apiKey: String
        ): Call<ResultsResearch>
    }

    val movieApiService: MovieApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(Credentials.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MovieApiService::class.java)
    }
}
