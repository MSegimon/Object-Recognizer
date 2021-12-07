package com.example.recog

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import android.content.Intent
import android.graphics.Bitmap

import android.provider.MediaStore



class MainActivity : AppCompatActivity() {

    val REQUEST_CODE = 2000

    private lateinit var imageView : ImageView
    private lateinit var btnImageCapture : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
        imageView = findViewById(R.id.imageView)
        btnImageCapture = findViewById(R.id.btnImageCapture)



//        btnImageCapture.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CODE)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
        }
        btnImageCapture.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CODE)
        }
    }

}