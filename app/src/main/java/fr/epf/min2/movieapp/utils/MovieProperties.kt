package fr.epf.min2.movieapp.utils

import fr.epf.min2.movieapp.models.MovieModel

object MovieProperties {

    // A movie to add on 'My list' automatically when launching the app
    val moviesToAdd = listOf(
        MovieModel(
            "Avatar: The Way of Water",
            "Set more than a decade after the events of the first film, learn the story of the Sully family (Jake, Neytiri, and their kids), the trouble that follows them, the lengths they go to keep each other safe, the battles they fight to stay alive, and the tragedies they endure.",
            "https://image.tmdb.org/t/p/original/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg",
            false,
            "2022-12-14",
            "en",
            7.705
        ),
        MovieModel(
            "Fast & Furious X",
            "Over many missions and against impossible odds, Dom Toretto and his family have outsmarted, out-nerved and outdriven every foe in their path. Now, they confront the most lethal opponent they've ever faced: A terrifying threat emerging from the shadows of the past who's fueled by blood revenge, and who is determined to shatter this family and destroy everything—and everyone—that Dom loves, forever.",
            "https://image.tmdb.org/t/p/original/1E5baAaEse26fej7uHcjOgEE2t2.jpg",
            false,
            "2023-05-17",
            "en",
            7.03
        ),
        MovieModel(
            "Interstellar",
            "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
            "https://image.tmdb.org/t/p/original/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            false,
            "2014-11-05",
            "en",
            8.402
        ),
        MovieModel(
            "Monsters, Inc.",
            "James Sullivan and Mike Wazowski are monsters, they earn their living scaring children and are the best in the business... even though they're more afraid of the children than they are of them. When a child accidentally enters their world, James and Mike suddenly find that kids are not to be afraid of and they uncover a conspiracy that could threaten all children across the world.",
            "https://image.tmdb.org/t/p/original/sgheSKxZkttIe8ONsf2sWXPgip3.jpg",
            false,
            "2001-11-01",
            "en",
            7.831
        )
    )

    val genreMap: Map<String, Int> by lazy {
        mapOf(
            "Science Fiction" to 878,
            "TV Movie" to 10770,
            "Thriller" to 53,
            "Romance" to 10749,
            "War" to 10752,
            "Western" to 37,
            "Action" to 28,
            "Adventure" to 12,
            "Animation" to 16,
            "Comedy" to 35,
            "Crime" to 80,
            "Documentary" to 99,
            "Drama" to 18,
            "Family" to 10751,
            "Fantasy" to 14,
            "History" to 36,
            "Horror" to 27,
            "Music" to 10402,
            "Mystery" to 9648
        )
    }

    fun getGenreId(genreName: String): Int? {
        return genreMap[genreName]
    }
}
