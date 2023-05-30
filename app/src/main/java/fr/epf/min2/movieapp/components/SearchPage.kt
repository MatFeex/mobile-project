package fr.epf.min2.movieapp.components

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import fr.epf.min2.movieapp.utils.Movie
import fr.epf.min2.movieapp.R
import fr.epf.min2.movieapp.utils.ResultsResearch
import fr.epf.min2.movieapp.utils.Credentials
import fr.epf.min2.movieapp.utils.MovieBundleCreator
import fr.epf.min2.movieapp.utils.MovieProperties
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call

import fr.epf.min2.movieapp.utils.RetrofitClient

class SearchPage : Fragment() {

    private var movieApiService = RetrofitClient.movieApiService
    private var selectedGenreId: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the search_page XML file
        return inflater.inflate(R.layout.page_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the spinner
        setupSpinner()

        // Get references to the search button and search icon
        val searchButton = view.findViewById<Button>(R.id.genreButton)
        val searchIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)

        // Set the search icon on the button and make the background transparent
        searchButton.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null)
        searchButton.setBackgroundColor(Color.TRANSPARENT)

        // Set an OnClickListener on the search button
        searchButton.setOnClickListener {
            // Check if a genre is selected
            if (selectedGenreId != 0) {
                // Fetch movies by the selected genre
                fetchByGenre(selectedGenreId)
            }
        }
    }

    private fun setupSpinner() {
        // Get a reference to the spinner view
        val spinner = requireView().findViewById<Spinner>(R.id.spinner)

        // Create an ArrayAdapter with the genre names as the data source
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, MovieProperties.genreMap.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter on the spinner
        spinner.adapter = adapter

        // Set the item selection listener for the spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected genre ID from the genreMap
                val genreName = parent.getItemAtPosition(position) as String
                selectedGenreId = MovieProperties.getGenreId(genreName) ?: -1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected
            }
        }
    }

    private fun showGenreMovie(movieList: List<Movie>) {
        // Create a new StackGenreFragment
        val stackGenreComponent = StackGenreComponent()

        // Replace the current fragment with the StackGenreFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackGenreComponent)
            .commit()

        // Add each ResultFragment to the StackGenreFragment stack
        for (movie in movieList) {
            val newResultPage = ResultPage()
            val bundle = MovieBundleCreator.createMovieBundle(movie)
            newResultPage.arguments = bundle

            parentFragmentManager.beginTransaction()
                .add(R.id.stackGenreContainer, newResultPage)
                // Add to the back stack
                .addToBackStack(null)
                .commit()
        }
    }


    private fun fetchByGenre(genreId: Int) {
        // Make an API call to get movies by genre
        val call = movieApiService.getMoviesByGenre(Credentials.API_KEY, genreId)
        call.enqueue(object : Callback<ResultsResearch> {
            override fun onResponse(call: Call<ResultsResearch>, response: Response<ResultsResearch>) {
                if (response.isSuccessful) {
                    // Retrieve the search result from the response
                    val searchResult = response.body()

                    // Extract the list of movies from the search result, or use an empty list if null
                    val genresMovies = searchResult?.results ?: emptyList()

                    // Log the successful search request and the retrieved movies
                    Log.d("SearchFragment", "Successful search request")
                    Log.d("SearchFragment", "Results: $genresMovies")

                    // Show the genre movies by passing the list of movies to the showGenreMovie function
                    showGenreMovie(genresMovies)
                } else {
                    // Handle API response errors
                }
            }
            override fun onFailure(call: Call<ResultsResearch>, t: Throwable) {
                // Handle connection or request execution errors
            }
        })
    }


}
