package com.example.theaterparking.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.theaterparking.R
import com.example.theaterparking.api.api
import com.example.theaterparking.databinding.ParkingEntryBinding
import com.example.theaterparking.dto.Parking
import com.example.theaterparking.ui.spinners.CustomParkingAdapter
import com.example.theaterparking.utils.ImageToTextUtils
import com.example.theaterparking.utils.ParkingUtils
import com.example.theaterparking.utils.StorageUtils
import com.example.theaterparking.utils.ToastUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@ExperimentalGetImage
class ParkingEntryScreen : AppCompatActivity() {
    private companion object {
        const val TAG = "ParkingEntryScreen"
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var parkingList: ListView
    private lateinit var vNumberField: EditText
    private lateinit var submitBtn: Button
    private lateinit var scanBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var cameraPreview: PreviewView
    private lateinit var binding: ParkingEntryBinding
    private lateinit var loader: ProgressBar
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    private var parkings: List<Parking> = listOf(
        Parking(1, "10:00 AM", "AP16BV9911", "50/-"),
        Parking(2, "8:00 AM", "AP16BV9911", "50/-"),
        Parking(3, "12:00 AM", "AP16BV9911", "50/-"),
        Parking(4, "9:30 AM", "AP16BV9911", "50/-"),
        Parking(5, "10:00 AM", "AP16BV9911", "50/-"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ParkingEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // getting permissions
        if (!allPermissionsGranted()) {
            requestPermissions()
        }
        // getting elements by id
        parkingList = findViewById(R.id.parkingList)
        vNumberField = findViewById(R.id.vNumber)
        submitBtn = findViewById(R.id.submitBtn)
        scanBtn = findViewById(R.id.scanBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        cameraPreview = findViewById(R.id.cameraPreview)
        loader = findViewById(R.id.progressBar)
        binding.cameraPreview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        val adapter = CustomParkingAdapter(this, parkings)
        parkingList.adapter = adapter

        handleScan()
        handleSubmit()
        handleLogout()
        getMyParkings()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown() // shutdown the camera executor
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                ToastUtils.showToast(this, "Permissions not granted by the user.")
                finish()
            }
        }
    }

    private fun getMyParkings() {
        // call api to get the parking's
        val userId = StorageUtils.getData("userId", this)
        if (userId.isNullOrEmpty()) {
            ToastUtils.showToast(this, "User not found")
            return
        }
        showLoader()
        GlobalScope.launch {
            val response = api.getParkingsByUserId(userId)
            if (response.isSuccessful) {
                val responseParkingList = response.body()?.map {
                    Parking(
                        it.id,
                        it.entryTime,
                        it.vehicleNumber,
                        it.amount
                    )
                }
                if (!responseParkingList.isNullOrEmpty()) {
                    Log.d(TAG, responseParkingList.toString())
                    runOnUiThread {
                        val adapter = CustomParkingAdapter(this@ParkingEntryScreen, responseParkingList)
                        parkingList.adapter = adapter
                    }
                }
            } else {
                runOnUiThread {
                    ToastUtils.showToast(this@ParkingEntryScreen, "Error occurred")
                }
            }
            runOnUiThread {
                hideLoader()
            }
        }
    }

    private fun showLoader() {
        loader.visibility = ProgressBar.VISIBLE
        parkingList.visibility = ListView.GONE
    }

    private fun hideLoader() {
        loader.visibility = ProgressBar.GONE
        parkingList.visibility = ListView.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onTextFound(text: String) {
        if (text.length == 10) {
            if (ParkingUtils.isCorrectVehicleNumber(text)) {
                vNumberField.setText(text)
                var time = LocalTime.now().toString()
                val amPm = if (time.substring(0, 2).toInt() > 12) {
                    "PM"
                } else {
                    "AM"
                }
                // get only hour
                time = time.substring(0, 5)
                val parking = Parking(parkings.size + 1, "$time $amPm", text.uppercase(), "20/-")
                parkings = listOf(parking) + parkings
                val adapter = CustomParkingAdapter(this, parkings)
                parkingList.adapter = adapter
                ToastUtils.showToast(this, "Parking entry added successfully")
                vNumberField.setText("")
                stopCamera()
                playSound()
            }
        }
    }

    private fun playBeepSound() {
//        val mediaPlayer = MediaPlayer.create(this, R.raw.beep_sound)
        // play the sound only once
//        mediaPlayer.start()
//        mediaPlayer.setOnCompletionListener {
//            mediaPlayer.release()
//        }
    }

    private fun playSound() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)

        toneGen.also {
            it.release()
        }

//        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TONE_PROP_BEEP)
//        val mMediaPlayer = MediaPlayer()
//        mMediaPlayer.setDataSource(this, soundUri)
//        val audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
//        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
//            // Uncomment the following line if you aim to play it repeatedly
//            // mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepare()
//            mMediaPlayer.start()
//        }
    }

    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(binding.cameraPreview.display.rotation)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    ImageToTextUtils(::onTextFound)
                )
            }
    }

    private fun ProcessCameraProvider.bind(
        preview: Preview,
        imageAnalyzer: ImageAnalysis
    ) = try {
        unbindAll()
        bindToLifecycle(
            this@ParkingEntryScreen,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalyzer
        )
    } catch (ise: IllegalStateException) {
        // Thrown if binding is not done from the main thread
        Log.e(TAG, "Binding failed", ise)
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraProviderFeature.addListener({
            val cameraProvider = cameraProviderFeature.get()
            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

            cameraProvider.bindToLifecycle(
                this as LifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private val handleScan = {
        scanBtn.setOnClickListener {
            showAndHideCameraPreview()
        }
    }

    private fun stopCamera() {
        // stop the camera
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(this))
        cameraPreview.visibility = PreviewView.GONE
        parkingList.visibility = ListView.VISIBLE
        parkingList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        scanBtn.text = "Scan Vehicle Number"
    }

    private fun showAndHideCameraPreview() {
        if (binding.cameraPreview.visibility == PreviewView.VISIBLE) {
            // stop the camera
            stopCamera()
        } else {
            binding.cameraPreview.visibility = PreviewView.VISIBLE
            parkingList.visibility = ListView.GONE
            scanBtn.text = "Hide"
            startCamera()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val handleSubmit = {
        submitBtn.setOnClickListener {
            // check vehicle number
            val vNumber = vNumberField.text.toString()
            if (vNumber.isEmpty()) {
                vNumberField.error = "Vehicle number is required"
                return@setOnClickListener
            } else if (vNumber.length < 10) {
                vNumberField.error = "Vehicle number must be 10 characters long"
                return@setOnClickListener
            } else if (vNumber.length > 10) {
                vNumberField.error = "Vehicle number cannot be more than 10 characters long"
                return@setOnClickListener
            } else {
                // call api to check if vehicle number is valid
                // if valid, show success message
                // else show error message
                var time = LocalTime.now().toString()
                val amPm = if (time.substring(0, 2).toInt() > 12) {
                    "PM"
                } else {
                    "AM"
                }
                // get only hour
                time = time.substring(0, 5)
                val parking = Parking(parkings.size + 1, "$time $amPm", vNumber.uppercase(), "20/-")
                // add to the first position of the list
                parkings = listOf(parking) + parkings
                val adapter = CustomParkingAdapter(this, parkings)
                parkingList.adapter = adapter
                ToastUtils.showToast(this, "Parking entry added successfully")
                vNumberField.setText("")

                AnimationUtils.loadAnimation(this, R.anim.slide_up).also {
                    parkingList.startAnimation(it)
                }
            }
        }
    }

    private val handleLogout = {
        logoutBtn.setOnClickListener {
            // call api to logout
            // navigate to login screen
            startActivity(Intent(this, HomeScreen::class.java))
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }
}