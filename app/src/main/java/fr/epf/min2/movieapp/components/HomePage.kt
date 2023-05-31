package fr.epf.min2.movieapp.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fr.epf.min2.movieapp.MainActivity
import fr.epf.min2.movieapp.utils.Movie
import fr.epf.min2.movieapp.R
import fr.epf.min2.movieapp.utils.ResultsResearch
import fr.epf.min2.movieapp.adapter.MovieAdapter
import fr.epf.min2.movieapp.adapter.SpacingAdapter
import fr.epf.min2.movieapp.models.MovieModel
import fr.epf.min2.movieapp.utils.Credentials
import fr.epf.min2.movieapp.utils.MovieBundleCreator
import fr.epf.min2.movieapp.utils.RetrofitClient

class HomePage(private val context: MainActivity, private val fragmentManager: FragmentManager) : Fragment() {
    private var movies: List<Movie> = emptyList()
    private val popularMoviesList = mutableListOf<MovieModel>()
    private val movieApiService = RetrofitClient.movieApiService
    private lateinit var verticalRecyclerView: RecyclerView
    private lateinit var searchView: SearchView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.page_home, container, false)

        // ------------------ SEARCH BAR -----------------------

        // Find the SearchView in the inflated layout
        searchView = view.findViewById(R.id.searchView)
        // Set up the SearchView and its listeners
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                // Create the API call to search movies based on the query
                val call = movieApiService.findMovies(Credentials.API_KEY, query)

                // Execute the search movies request asynchronously
                call.enqueue(object : Callback<ResultsResearch> {
                    override fun onResponse(call: Call<ResultsResearch>, response: Response<ResultsResearch>) {
                        if (response.isSuccessful) {
                            val searchResult = response.body()
                            movies = searchResult?.results ?: emptyList()
                            Log.d("Search", "request success")
                            // Check if any movies were found
                            if (movies.isNotEmpty()) {
                                // Display the ResultFragment with the data of the first movie
                                showMovieDetails(movies)
                            }
                        } else {
                            // Handle API response errors here
                            Log.e("Search", "Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ResultsResearch>, t: Throwable) {
                        // Handle connection or request execution errors here
                        Log.e("Search", "Error: ${t.message}")
                    }
                })
                // Return true to indicate the query submission has been handled
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                // Logic to execute when the search text changes
                return true
            }
        })

        // ------------------ HOME CONTENT -----------------------
        fetchByPopular("en")
        verticalRecyclerView = view.findViewById(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter = MovieAdapter(context, popularMoviesList, R.layout.movie_vertical)
        verticalRecyclerView.addItemDecoration(SpacingAdapter(50))

        return view
    }

    private fun showMovieDetails(movieList: List<Movie>) {
        // Create a new StackFragment
        val stackComponent = StackComponent()

        // Replace the existing fragment with the StackFragment in the fragment_container
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackComponent)
            .commit()

        // Add each ResultFragment to the StackFragment
        for (movie in movieList) {
            val newResultPage = ResultPage()

            // Create a bundle with movie details
            val bundle = MovieBundleCreator.createMovieBundle(movie)
            newResultPage.arguments = bundle

            fragmentManager.beginTransaction()
                .add(R.id.stackContainer, newResultPage)
                .addToBackStack(null)
                .commit()

            stackComponent.incrementFragmentCount()
        }
    }

    private fun updatePopularList(newMoviesList: List<MovieModel>) {
        // Clear the existing popularMoviesList and add all the new movies
        popularMoviesList.clear()
        popularMoviesList.addAll(newMoviesList)
        // Notify the adapter that the dataset has changed
        verticalRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun fetchByPopular(language: String) {
        val call = movieApiService.getPopularMovies(Credentials.API_KEY, language)
        call.enqueue(object : Callback<ResultsResearch> {
            override fun onResponse(call: Call<ResultsResearch>, response: Response<ResultsResearch>) {
                if (response.isSuccessful) {
                    val searchResult = response.body()
                    val popularMovies = searchResult?.results ?: emptyList()
                    val popularMovieModels = popularMovies.map { mapToMovieModel(it) }
                    updatePopularList(popularMovieModels)
                }
            }
            override fun onFailure(call: Call<ResultsResearch>, t: Throwable) {
            }
        })
    }

    private fun mapToMovieModel(movie: Movie): MovieModel {
        // Create and return a new MovieModel instance with the mapped properties
        return MovieModel(
            title = movie.title,
            overview = movie.overview,
            posterPath = "https://image.tmdb.org/t/p/original${movie.poster_path}", // Construct the full poster path by appending the partial path to the base URL
            liked = false,
            releaseDate = movie.release_date,
            originalLanguage = movie.original_language,
            voteAverage = movie.vote_average,
            voteCount = movie.vote_count,
            popularity = movie.popularity
        )
    }

}