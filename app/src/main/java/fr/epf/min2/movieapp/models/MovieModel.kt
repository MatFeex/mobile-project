package fr.epf.min2.movieapp.models


class MovieModel(
    var title:String ="Movie",
    val overview:String="Movie overview",
    var posterPath:String="https://image.tmdb.org/t/p/original/wwemzKWzjKYJFfCeiB57q3r4Bcm.svg",
    var liked:Boolean=false,
    var releaseDate: String,
    var originalLanguage:String="en",
    var voteAverage:Double,
    val voteCount: Int = 0,
    val popularity: Double = 0.0
)