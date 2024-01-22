package com.example.theaterparking.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale
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
    private lateinit var logoutBtn: TextView
    private lateinit var cameraPreview: PreviewView
    private lateinit var binding: ParkingEntryBinding
    private lateinit var loader: ProgressBar
    private lateinit var vToggleBtn: SwitchCompat
    private lateinit var vToggleText: TextView
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    private var parkings: List<Parking> = listOf()

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
        vToggleBtn = findViewById(R.id.vToggleBtn)
        vToggleText = findViewById(R.id.vToggleText)
        binding.cameraPreview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        val adapter = CustomParkingAdapter(this, parkings)
        parkingList.adapter = adapter

        handleScan()
        handleSubmit()
        handleToggle()
        handleLogout()
        handleVNumberUppercase()
        getMyParkings()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown() // shutdown the camera executor
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        // stop user from going back because we don't want to show the login screen again
        ToastUtils.showToast(this, "Please logout to go back")
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
                        it.amount + "/-"
                    )
                }
                if (!responseParkingList.isNullOrEmpty()) {
                    runOnUiThread {
                        parkings = responseParkingList
                        val adapter =
                            CustomParkingAdapter(this@ParkingEntryScreen, responseParkingList)
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
        if (text.length == 10 && ParkingUtils.isCorrectVehicleNumber(text)) {
            val vNumber = text.uppercase()
            vNumberField.setText(vNumber)
            val date = getCurrentDate()
            val price = if (vToggleBtn.isChecked) {
                // if toggle button is checked, then price is 30
                "30/-"
            } else {
                "20/-"
            }
            val parking = Parking(parkings.size + 1, date, vNumber, price)
            parkings = listOf(parking) + parkings
            val adapter = CustomParkingAdapter(this, parkings)
            parkingList.adapter = adapter
            ToastUtils.showToast(this, "Parking entry added successfully")
            vNumberField.setText("")
            stopCamera()
            playSound()
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
    }

    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
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

    private fun handleToggle() {
        vToggleBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vToggleText.text = "Car"
                vToggleBtn.trackTintList = ContextCompat.getColorStateList(this, R.color.coral)
            } else {
                vToggleText.text = "Bike"
                vToggleBtn.trackTintList = ContextCompat.getColorStateList(this, R.color.white)
            }
        }
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

    private fun handleVNumberUppercase() {
        vNumberField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This method is called to notify you that characters within `charSequence` are about to be replaced with new text.
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // This method is called to notify you that characters within `charSequence` have been replaced with new text.

                // Convert the text to uppercase and set it back to the EditText
                vNumberField.removeTextChangedListener(this)
                vNumberField.setText(charSequence?.toString()?.uppercase())
                vNumberField.setSelection(vNumberField.text.length) // Move the cursor to the end
                vNumberField.addTextChangedListener(this)
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called to notify you that somewhere within `editable`, new text has been added or removed.
            }
        })
    }

    private fun getCurrentDate(): String {
        val dateFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentDate = dateFormatter.format(Date())
        // last 2 words uppercase
        val words = currentDate.split(" ")
        val lastTwoWords = words.takeLast(2).joinToString(" ") { it.uppercase() }
        return "${words.dropLast(2).joinToString(" ")} $lastTwoWords"
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
            } else if (!ParkingUtils.isCorrectVehicleNumber(vNumber)) {
                vNumberField.error = "Vehicle Number must be in APXXABXXXX format"
                return@setOnClickListener
            } else {
                // call api to check if vehicle number is valid
                // if valid, show success message
                // else show error message
                val price = if (vToggleBtn.isChecked) {
                    // if toggle button is checked, then price is 30
                    "30/-"
                } else {
                    "20/-"
                }
                // getting current date
                val date = getCurrentDate()
                val parking = Parking(parkings.size + 1, date, vNumber, price)
                // add to the first position of the list
                parkings = listOf(parking) + parkings
                val adapter = CustomParkingAdapter(this, parkings)
                parkingList.adapter = adapter
                ToastUtils.showToast(this, "Parking entry added successfully")
                vNumberField.setText("")
                playSound()

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