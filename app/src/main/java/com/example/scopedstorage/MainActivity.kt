package com.example.scopedstorage

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.scopedstorage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textFile.setOnClickListener {
            val intent = Intent(this,TextFileActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Text File", Toast.LENGTH_SHORT).show()
        }

        binding.mediaStoreFile.setOnClickListener {
            val intent = Intent(this,MediaStoreActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Media Store Activity", Toast.LENGTH_SHORT).show()
        }

    }
}