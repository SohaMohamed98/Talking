package com.soha.talking

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //Picture
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTUR_CODE = 1001
    private val REQUEST_SELECT_IMAGE_IN_ALBUM=1002
    var image_rui: Uri? = null

    //Voice
    private val Request_code_speech_input = 100

    //text to speech
    lateinit var mTTS: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        //TEXT TO SPEECH
        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                mTTS.language = Locale.UK
            }
        })

        //VOICE
        voiceBtn.setOnClickListener {
            speak()
        }
    }


    //Speak To Text
    private fun speak() {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak Something")

        try {
            startActivityForResult(mIntent, Request_code_speech_input)

        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    //Text to Speak
    private fun textToSpeak(){

        val toSpeak = textTv.text.toString()
        if (toSpeak == "") {
            Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
        } else {

            Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show()
            mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
        }
    }


    //Open Camera
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera")

        image_rui=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        //camera Intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
        startActivityForResult(cameraIntent, IMAGE_CAPTUR_CODE)
    }


    //Img From Gallery
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            Request_code_speech_input -> {

                if (resultCode == Activity.RESULT_OK && data != null) {

                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textTv.text = result[0]
                    textToSpeak()

                    if(textTv.text=="camera"){

                        Toast.makeText(this,"Camera",Toast.LENGTH_LONG).show()
                        openCamera()

                     /*   //if sys OS is Marshmallow or above, we need to request permission
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                            if(checkSelfPermission(android.Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED ){

                                val permission = arrayOf(android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                //show popup to request permission
                                // requestPermissions(permission, PERMISSION_CODE)
                            }
                            else{
                                //permission already granted
                                openCamera()
                            }
                        }
                        else{
                            //system OS is < marshmallow
                            openCamera()
                        }*/

                    }else if(textTv.text == "gallery" || textTv.text== "studio"){

                        selectImageInAlbum()

                    } else{

                        Toast.makeText(this,"NotCamera",Toast.LENGTH_LONG).show()
                        var i = Intent(Intent.ACTION_VOICE_COMMAND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)

                    }
                }
            }

            IMAGE_CAPTUR_CODE ->{
                if(resultCode== Activity.RESULT_OK){
                    //set image captured to image
                    image_view.setImageURI(image_rui)
                }
            }

            REQUEST_SELECT_IMAGE_IN_ALBUM ->{
                image_view.setImageURI(data?.data)
            }

        }
    }




   /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses Allow or DENY from permission Request Popup
        when(requestCode){
            PERMISSION_CODE ->{
                if(grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()

                }
                else{
                    //permission was denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/


}
