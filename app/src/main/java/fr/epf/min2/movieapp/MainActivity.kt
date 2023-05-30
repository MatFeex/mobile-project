package fr.epf.min2.movieapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.util.Log
import fr.epf.min2.movieapp.components.HomePage
import fr.epf.min2.movieapp.components.LikedPage
import fr.epf.min2.movieapp.components.QRCodePage
import fr.epf.min2.movieapp.components.SearchPage
import fr.epf.min2.movieapp.models.MovieModel

class MainActivity : AppCompatActivity() {

    // Define movieList as a static variable using the companion object
    companion object {
        val movieList = arrayListOf<MovieModel>()
    }

    private lateinit var homeFragment: HomePage
    private lateinit var likedFragment: LikedPage

    private fun loadFragment(fragment: Fragment) {
        Log.d("MainActivity", "Loading fragment: ${fragment.javaClass.simpleName}")
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_template)

        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_view)
        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_page -> {
                    if (::homeFragment.isInitialized) {
                        loadFragment(homeFragment)
                    } else {
                        homeFragment = HomePage(this, supportFragmentManager)
                        loadFragment(homeFragment)
                    }
                    true
                }
                R.id.search_bar -> {
                    loadFragment(SearchPage())
                    true
                }
                R.id.liked_page -> {
                    if (::likedFragment.isInitialized) {
                        likedFragment.updateMovieList(movieList)
                        loadFragment(likedFragment)
                    } else {
                        likedFragment = LikedPage(this)
                        loadFragment(likedFragment)
                    }
                    true
                }
                R.id.qr_code -> {
                    loadFragment(QRCodePage())
                    true
                }
                else -> {
                    false
                }
            }
        }
        loadFragment(HomePage(this, supportFragmentManager))
    }
}
