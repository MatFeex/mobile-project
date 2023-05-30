package fr.epf.min2.movieapp.components

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import fr.epf.min2.movieapp.MainActivity
import fr.epf.min2.movieapp.utils.Movie
import fr.epf.min2.movieapp.utils.ResultsResearch
import fr.epf.min2.movieapp.*
import fr.epf.min2.movieapp.models.MovieModel
import fr.epf.min2.movieapp.utils.Credentials
import fr.epf.min2.movieapp.utils.MovieBundleCreator
import fr.epf.min2.movieapp.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResultPage : Fragment() {

    private var movieApiService = RetrofitClient.movieApiService
    private var isButtonClicked = false
    private lateinit var titleTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var overviewTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var popularityTextView: TextView
    private lateinit var voteCountTextView: TextView
    private lateinit var voteAverageTextView: TextView
    private lateinit var originalLanguageTextView: TextView
    private lateinit var idTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.page_results, container, false)
        titleTextView = view.findViewById(R.id.movie_title)
        posterImageView = view.findViewById(R.id.movie_img)
        overviewTextView = view.findViewById(R.id.movie_overview)
        releaseDateTextView = view.findViewById(R.id.movie_release_date)
        originalLanguageTextView = view.findViewById(R.id.movie_vo)
        idTextView = view.findViewById(R.id.movie_ref)
        popularityTextView = view.findViewById(R.id.movie_popularity)
        voteCountTextView = view.findViewById(R.id.movie_vote)
        voteAverageTextView = view.findViewById(R.id.movie_vote_avg)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the data passed through the Bundle
        val title = arguments?.getString("title")
        var posterPath = arguments?.getString("poster_path")
        val overview = arguments?.getString("overview")
        val releaseDate = arguments?.getString("release_date")
        val originalLanguage = arguments?.getString("original_language")
        val id = arguments?.getString("id")
        val popularity = arguments?.getDouble("popularity")
        val voteCount = arguments?.getInt("vote_count")
        val voteAverage = arguments?.getDouble("vote_average")

        // Display the data in the corresponding TextViews
        titleTextView.text = title
        overviewTextView.text = overview
        releaseDateTextView.text = releaseDate
        originalLanguageTextView.text = originalLanguage
        idTextView.text = id
        popularityTextView.text = popularity.toString()
        voteCountTextView.text = voteCount.toString()
        voteAverageTextView.text = voteAverage.toString()

        // Load the image from the posterPath URL using Glide
        if (!posterPath.isNullOrEmpty()) {
            val fullPosterPath = "https://image.tmdb.org/t/p/original$posterPath"
            Glide.with(requireContext())
                .load(fullPosterPath)
                .override(500, 500)
                .centerInside()
                .into(posterImageView)
        }

        // Set up the favorite button
        val button = view.findViewById<Button>(R.id.like_btn)
        val defaultIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_unliked)
        val clickedIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_liked)
        val iconStateList = StateListDrawable()

        // Define the button icon states
        iconStateList.addState(intArrayOf(android.R.attr.state_pressed), clickedIcon)
        iconStateList.addState(intArrayOf(), defaultIcon)
        button.setCompoundDrawablesWithIntrinsicBounds(iconStateList, null, null, null)

        // Handle button clicks
        button.setOnClickListener {
            isButtonClicked = !isButtonClicked
            if (isButtonClicked) {
                button.setCompoundDrawablesWithIntrinsicBounds(clickedIcon, null, null, null)
            } else {
                button.setCompoundDrawablesWithIntrinsicBounds(defaultIcon, null, null, null)
            }

            // Add the movie to the list if all required data is available
            if (!title.isNullOrEmpty() && !overview.isNullOrEmpty() && !posterPath.isNullOrEmpty()) {
                posterPath = "https://image.tmdb.org/t/p/original$posterPath"
                val movie = MovieModel(
                    title!!,
                    overview!!,
                    posterPath!!,
                    true,
                    releaseDate!!,
                    originalLanguage!!,
                    voteAverage!!
                )
                MainActivity.movieList.add(movie)
                button.isEnabled = false
                println("result mooviesList: $MainActivity.movieList")
            }
        }

        // Set up the suggestion button
        val suggestButton = view.findViewById<Button>(R.id.recommendation_btn)
        val suggestIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_recommendation)
        suggestButton.setCompoundDrawablesWithIntrinsicBounds(suggestIcon, null, null, null)

        // Handle suggestion button click
        suggestButton.setOnClickListener {
            val movieId = idTextView.text.toString().toInt()
            fetchMovieRecommendations(movieId)
        }
    }

    private fun fetchMovieRecommendations(movieId: Int) {
        // Create the API call to get movie recommendations
        val call = movieApiService.getRecommendations(movieId, Credentials.API_KEY)
        // Asynchronously execute the API call
        call.enqueue(object : Callback<ResultsResearch> {
            override fun onResponse(call: Call<ResultsResearch>, response: Response<ResultsResearch>) {
                if (response.isSuccessful) {
                    // Retrieve the search result from the API response
                    val searchResult = response.body()
                    val recommendedMovies = searchResult?.results ?: emptyList()
                    Log.d("SearchFragment", "Requête de recherche réussie")
                    Log.d("SearchFragment", "Résultats: $recommendedMovies")

                    // Show the recommended movie details
                    showSuggestedMovieDetails(recommendedMovies)
                } else {
                    // Handle API response errors
                }
            }
            override fun onFailure(call: Call<ResultsResearch>, t: Throwable) {
                // Handle connection or execution errors
            }
        })
    }


    private fun showSuggestedMovieDetails(suggestedMovies: List<Movie>) {
        // Create a new instance of StackSuggestionFragment
        val stackRecomComponent = StackRecomComponent()

        // Replace the current fragment with the StackSuggestionFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackRecomComponent)
            .commit()

        // Add each suggested movie as a ResultFragment to the StackSuggestionFragment
        for (movie in suggestedMovies) {
            val resultPage = ResultPage()
            val bundle = MovieBundleCreator.createMovieBundle(movie)
            resultPage.arguments = bundle

            parentFragmentManager.beginTransaction()
                .add(R.id.stackRecomContainer, resultPage)
                // Add to the back stack
                .addToBackStack(null)
                .commit()
        }
    }

}
