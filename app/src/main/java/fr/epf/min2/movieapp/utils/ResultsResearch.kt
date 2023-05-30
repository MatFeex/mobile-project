package fr.epf.min2.movieapp.utils

data class ResultsResearch(
    val results: List<Movie>
)
data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val adult: Boolean,
    val overview: String,
    val release_date: String,
    val genre_ids: List<Int>,
    val original_title: String,
    val original_language: String,
    val backdrop_path: String?,
    val popularity: Double,
    val vote_count: Int,
    val video: Boolean,
    val vote_average: Double
)


