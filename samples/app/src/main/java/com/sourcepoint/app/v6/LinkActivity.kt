package com.sourcepoint.app.v6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.databinding.ActivityDlBinding

class LinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDlBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}