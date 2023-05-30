package fr.epf.min2.movieapp.adapter

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import fr.epf.min2.movieapp.R
import fr.epf.min2.movieapp.models.MovieModel


// CLASS MovieDialog : Open a Dialog Box to see details for a movie
class DialogAdapter(
    private val adapter: MovieAdapter,
    private val currentMovie: MovieModel
) : Dialog(adapter.context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_movie_details)
        setupComponents()
        setupCloseButton()
    }

    private fun setupCloseButton() {
        findViewById<ImageView>(R.id.dialog_close_btn).setOnClickListener {
            dismiss()
        }
    }

    private fun setupComponents() {
        val movieImage = findViewById<ImageView>(R.id.popup_image)
        Glide.with(adapter.context).load(Uri.parse(currentMovie.posterPath)).into(movieImage)
        findViewById<TextView>(R.id.dialog_movie_title).text = currentMovie.title
        findViewById<TextView>(R.id.dialog_overview).text = currentMovie.overview
        findViewById<TextView>(R.id.dialog_release_date_value).text = currentMovie.releaseDate
        findViewById<TextView>(R.id.dialog_vo_value).text = currentMovie.originalLanguage
        findViewById<TextView>(R.id.popup_movie_vote_average).text = currentMovie.voteAverage.toString()
    }
}
