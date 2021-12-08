package com.example.livecamerafootagekotlin

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.example.imageclassificationlivefeed.CameraConnectionFragment
import com.example.imageclassificationlivefeed.ImageUtils
import java.util.*
import android.os.Handler
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), ImageReader.OnImageAvailableListener {

    var handler: Handler = Handler()
    lateinit var mainHandler: Handler
    var runnable: Runnable? = null
    var delay = 1000 // Delay in seconds / 1000
    var test = 1
    var url = "http://141.94.70.32:5000/Object_Recognizer"
    val REQUEST_CODE = 5555
    val client = OkHttpClient()

    /** Permission functions **/

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
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

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO show live camera footage
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //TODO show live camera footage
            setFragment()
        } else {
            finish()
        }
    }

    /** Text-to-speech functionality **/

    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    private val mTTS: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    mTTS.language = Locale.US
                }
            })
    }


    /** On create operations **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainHandler = Handler(Looper.getMainLooper())


        //TODO ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                        Manifest.permission.CAMERA
                )
                requestPermissions(permission, 1122)
            } else {
                //TODO show live camera footage
                setFragment()
            }
        }else{
            //TODO show live camera footage
            setFragment()
        }
    }

    /** CAMERA FUNCTIONS **/
    var previewHeight = 0
    var previewWidth = 0
    var sensorOrientation = 0;

    //TODO fragment which show live footage from camera
    protected fun setFragment() {
        val manager =
                getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var cameraId: String? = null
        try {
            cameraId = manager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        val fragment: Fragment
        val camera2Fragment = CameraConnectionFragment.newInstance(
                object :
                        CameraConnectionFragment.ConnectionCallback {
                    override fun onPreviewSizeChosen(size: Size?, rotation: Int) {
                        previewHeight = size!!.height
                        previewWidth = size.width
                        sensorOrientation = rotation - getScreenOrientation()
                    }
                },
                this,
                R.layout.camera_fragment,
                Size(640, 480)
        )
        camera2Fragment.setCamera(cameraId)
        fragment = camera2Fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
    protected fun getScreenOrientation(): Int {
        return when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }
    }

    fun fillBytes(
        planes: Array<Image.Plane>,
        yuvBytes: Array<ByteArray?>
    ) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]]
        }
    }

    //Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    fun rotateBitmap(input: Bitmap): Bitmap? {
        Log.d("trySensor", sensorOrientation.toString() + "     " + getScreenOrientation())
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(sensorOrientation.toFloat())
        return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
    }

    //TODO getting frames of live camera footage and passing them to model
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    private var yRowStride = 0
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null
    private var rgbFrameBitmap: Bitmap? = null
    private var base64encoded: String? = null

    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = Runnable {
                ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    previewWidth,
                    previewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    rgbBytes!!
                )
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }

            base64encoded = processImage()

        } catch (e: Exception) {
            return
        }
    }

    fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = mTTS!!.setLanguage(Locale.US)
            println("TTS Initialization successful")

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private val getImageString = object : Runnable {
        override fun run() {
            ConnectToServer(base64encoded)
            mainHandler.postDelayed(this, 3000)
        }
    }


    /** Function to send image */
    private fun ConnectToServer(enc: String?) {
        println(enc)

        if (enc != null) {

            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image64", enc)
                .build()

            val request = Request.Builder().url(url).post(requestBody).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("FAILED")
                    println(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    println("WORKED?")
                    mTTS!!.speak(response.body()?.string(), TextToSpeech.QUEUE_ADD, null, "tts1")
                }
            })
        }

        test++
    }


    private fun processImage(): String? {
        imageConverter!!.run()
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
        rgbFrameBitmap?.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight)
        postInferenceCallback!!.run()
        val baos = ByteArrayOutputStream()
        (rgbFrameBitmap!!).compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }


    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(getImageString)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(getImageString)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}