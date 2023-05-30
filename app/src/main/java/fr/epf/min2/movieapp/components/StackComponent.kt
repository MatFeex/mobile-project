package fr.epf.min2.movieapp.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.min2.movieapp.R

class StackComponent : Fragment() {
    var fragmentCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout XML file "stack"
        return inflater.inflate(R.layout.stack, container, false)
    }

    // Increment the fragment count
    fun incrementFragmentCount() {
        fragmentCount++
    }
}
