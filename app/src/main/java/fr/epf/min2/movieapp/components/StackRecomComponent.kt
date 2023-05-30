package fr.epf.min2.movieapp.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.epf.min2.movieapp.R

class StackRecomComponent : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout XML file "stack_recommendation"
        return inflater.inflate(R.layout.stack_recommendation, container, false)
    }
}
