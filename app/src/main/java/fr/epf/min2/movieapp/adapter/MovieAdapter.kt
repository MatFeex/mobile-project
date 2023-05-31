package fr.epf.min2.movieapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min2.movieapp.MainActivity
import fr.epf.min2.movieapp.models.MovieModel
import fr.epf.min2.movieapp.R

class MovieAdapter(
    val context: MainActivity,
    private val movieList: List<MovieModel>,
    private val layoutId: Int
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {


    // ViewHolder class to hold the views of each item
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val movieImage:ImageView=view.findViewById(R.id.movie_image)
        val movieName:TextView?=view.findViewById(R.id.title_item)
        val movieVoteAverage:TextView?=view.findViewById(R.id.vote_average_item)
        val movieVoteCount:TextView?=view.findViewById(R.id.vote_count_item)
        val moviePopularity:TextView?=view.findViewById(R.id.popularity_item)
        val likedIcon:ImageView=view.findViewById(R.id.liked_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each item
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int =movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the current movie from the list
        val currentMovie=movieList[position]

        // Load the movie image using Glide library
        Glide.with(context).load(Uri.parse(currentMovie.posterPath)).into(holder.movieImage)

        // Set the title and vote average of the movie
        holder.movieName?.text=currentMovie.title
        holder.movieVoteAverage?.text= currentMovie.voteAverage.toString()
        holder.movieVoteCount?.text= currentMovie.voteCount.toString()
        holder.moviePopularity?.text= currentMovie.popularity.toString()

        if (holder.likedIcon != null) {
            val likedImageResource = if (currentMovie.liked) {
                R.drawable.ic_liked
            } else {
                R.drawable.ic_unliked
            }
            // Set the star icon resource
            holder.likedIcon.setImageResource(likedImageResource)
        }

        // Set a click listener for the item view
        holder.itemView.setOnClickListener {
            // Create and show the movie dialog for the clicked movie
            DialogAdapter(this, currentMovie).show()
        }

    }
}