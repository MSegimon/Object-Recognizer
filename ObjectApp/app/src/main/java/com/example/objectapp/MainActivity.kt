package com.example.objectapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*
import okhttp3.RequestBody

import okhttp3.MultipartBody
import android.net.Uri
import android.util.Log





class MainActivity : AppCompatActivity() {
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 10000 // Delay in seconds / 1000
    var test = 1
    val url = "http://141.94.70.32:5000/Object_Recognizer"

    val REQUEST_CODE = 5555

    val client = OkHttpClient()

    /** PERMISSION FUNCTION HERE **/

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                println("Denied permanently")
            } else {
                ActivityCompat.requestPermissions(this as Activity,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here

                } else {
//                     permission is denied, you can ask for permission again, if you want
                      askForPermissions()
                }
                return
            }
        }
    }

    /** END PERMS **/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** Function to send image */
    private fun sendMessage() {
        val text = "Running method time $test"
//        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()

        if (!isPermissionsAllowed()) {
            askForPermissions()
        }

        val base64Encoded = "I_AM_THE_CODE"
        val request = Request.Builder().url(url).addHeader("image64", base64Encoded).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("FAILED")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                println("WORKED?")
                println(response.body()?.string())
            }
        })
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