package com.example.theaterparking.utils

import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class ImageToTextUtils(private val textFoundListener: (String) -> Unit) : ImageAnalysis.Analyzer {
    private companion object {
        const val TAG = "ImageToTextUtils"
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { process(it, imageProxy) }
    }

    private fun process(image: Image, imageProxy: ImageProxy) {
        try {
            readTextFromImage(InputImage.fromMediaImage(image, 90), imageProxy)
        } catch (e: IOException) {
            Log.d(TAG, "Failed to load the image")
            e.printStackTrace()
        }
    }


    private fun readTextFromImage(image: InputImage, imageProxy: ImageProxy) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText ->
                processTextFromImage(visionText, imageProxy)
                imageProxy.close()
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Failed to process the image")
                error.printStackTrace()
                imageProxy.close()
            }
    }

    private fun processTextFromImage(visionText: Text, imageProxy: ImageProxy) {
        for (block in visionText.textBlocks) {
            // You can access whole block of text using block.text
            for (line in block.lines) {
                // You can access whole line of text using line.text
                for (element in line.elements) {
                    textFoundListener(element.text)
                }
            }
        }
    }
}