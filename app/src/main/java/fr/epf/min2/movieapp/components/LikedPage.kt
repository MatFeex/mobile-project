package fr.epf.min2.movieapp.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.epf.min2.movieapp.R
import fr.epf.min2.movieapp.MainActivity
import fr.epf.min2.movieapp.adapter.MovieAdapter
import fr.epf.min2.movieapp.models.MovieModel
import fr.epf.min2.movieapp.utils.MovieProperties
import java.io.*

class LikedPage(private val context: MainActivity) : Fragment() {
    private val movieList = mutableListOf<MovieModel>()
    private lateinit var horizontalRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.page_liked, container, false)

        // Load saved movies from file and add them to movieList if they don't already exist
        val moviesToAdd = loadSavedMovies().filter { savedMovie ->
            !movieList.any { movie -> movie.title == savedMovie.title }
        }
        movieList.addAll(moviesToAdd)

        // Add new movies to movieList if they don't already exist
        MovieProperties.moviesToAdd.forEach { movie ->
            val isMovieAlreadyAdded = movieList.any { it.title == movie.title }
            if (!isMovieAlreadyAdded) {
                movieList.add(movie)
            }
        }

        horizontalRecyclerView = view.findViewById(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter = MovieAdapter(context, movieList, R.layout.movie_horizontal)

        return view
    }

    private fun loadSavedMovies(): List<MovieModel> {
        val file = File(context.filesDir, "movieData.txt")
        if (!file.exists()) {
            return emptyList()
        }

        val reader = BufferedReader(FileReader(file))
        val gson = Gson()
        val savedMovies = mutableListOf<MovieModel>()

        reader.useLines { lines ->
            lines.forEach { line ->
                val movie: MovieModel = gson.fromJson(line, object : TypeToken<MovieModel>() {}.type)
                savedMovies.add(movie)
            }
        }

        return savedMovies
    }

    override fun onPause() {
        super.onPause()
        saveMoviesToFile()
    }

    private fun saveMoviesToFile() {
        val file = File(requireContext().filesDir, "movieData.txt")
        val writer = BufferedWriter(FileWriter(file))
        val gson = Gson()
        val savedMovies = HashSet<MovieModel>()

        val allMovies = movieList + MainActivity.movieList

        for (movie in allMovies) {
            if (savedMovies.contains(movie)) {
                continue  // Skip if movie is already saved
            }

            // Check if the movie is already present in the file
            val isMovieAlreadySaved = savedMovies.any { savedMovie ->
                savedMovie.title == movie.title
            }

            if (!isMovieAlreadySaved) {
                savedMovies.add(movie)
                val movieString = gson.toJson(movie)
                writer.write(movieString)
                writer.newLine()
            }
        }
        writer.close()
    }

    fun updateMovieList(newMovieList: List<MovieModel>) {
        for (newMovie in newMovieList) {
            val existingMovie = movieList.find { it.title == newMovie.title }
            if (existingMovie == null) {
                movieList.add(newMovie)
            } else {
                // Handle the case when a movie with the same title already exists
                // decide how to handle this scenario
            }
        }

        // Notify the adapter of the RecyclerView to update the displayed data
        horizontalRecyclerView.adapter?.notifyDataSetChanged()
    }
}
