package fr.epf.min2.movieapp.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.epf.min2.movieapp.utils.Movie
import fr.epf.min2.movieapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import fr.epf.min2.movieapp.utils.Credentials
import fr.epf.min2.movieapp.utils.MovieBundleCreator
import fr.epf.min2.movieapp.utils.RetrofitClient

class QRCodePage: Fragment() {

    private lateinit var scannerView: CompoundBarcodeView
    private var movieApiService = RetrofitClient.movieApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.page_qrcode, container, false)
        scannerView = view.findViewById(R.id.cameraPreview)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the scanButton from the layout
        val scanButton = view.findViewById<Button>(R.id.scanButton)

        // Set a click listener for the scanButton
        scanButton.setOnClickListener {
            if (hasCameraPermission()) {
                // Start the camera when camera permission is granted
                startCamera()
            } else {
                // Request camera permission when it is not granted
                requestCameraPermission()
            }
        }
    }

    // Check if the app has camera permission
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    // Start the camera and set up the barcode scanner
    private fun startCamera() {
        scannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    if (isAdded && !isRequestingMovieDetails) {
                        isRequestingMovieDetails = true
                        val movieId = result.text.toIntOrNull()
                        if (movieId != null) {
                            fetchMovieDetails(movieId)
                        } else {
                            Toast.makeText(requireContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {
                // Unused in this implementation
            }
        })

        // Set the status text to empty
        scannerView.setStatusText("")

        // Resume the barcode scanner
        scannerView.resume()
    }

    override fun onResume() {
        super.onResume()
        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause the barcode scanner
        scannerView.pause()
    }

    private fun fetchMovieDetails(movieId: Int) {
        // Make an API call to fetch movie details by ID
        val call = movieApiService.getMovieDetails(movieId, Credentials.API_KEY)

        // Execute the API call asynchronously
        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    val movie = response.body()
                    if (movie != null) {
                        // Handle the movie details
                        showQrcodeMovieDetails(movie)
                    }
                } else {
                    // Handle API response errors
                    Log.e("QR", "Response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                // Handle connection or execution errors while making the API request
                Log.e("QR", "Request failed: ${t.message}")
            }
        })
    }


    private fun showQrcodeMovieDetails(movie: Movie) {
        // Create a new StackQrcodeFragment
        val stackQRComponent = StackQRComponent()

        // Replace the current fragment with the StackQrcodeFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, stackQRComponent)
            .commit()

        // Create a new ResultFragment with the movie details
        val newResultPage = ResultPage()
        val bundle = MovieBundleCreator.createMovieBundle(movie)
        newResultPage.arguments = bundle

        // Add the ResultFragment with movie details to the StackQrcodeFragment
        parentFragmentManager.beginTransaction()
            .add(R.id.stackQRContainer, newResultPage)
            .commit()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }

    private var isRequestingMovieDetails = false


}


