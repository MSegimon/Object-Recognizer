package com.example.objectapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000 // Delay in seconds / 1000
    var test = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** Function to send image */
    private fun sendMessage() {
        val text = "Running method time $test"
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        test++
    }

    // https://www.tutorialspoint.com/how-to-run-a-method-every-10-seconds-in-android-using-kotlin
    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())

            // CALL OUR FUNCTION HERE
            sendMessage()

        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }
}