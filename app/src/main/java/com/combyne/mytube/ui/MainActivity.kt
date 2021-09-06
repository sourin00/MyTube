package com.combyne.mytube.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.combyne.mytube.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

const val ADD_SHOW_RESULT_OK = Activity.RESULT_FIRST_USER
const val ADD_SHOW_RESULT_ERROR = Activity.RESULT_FIRST_USER + 1
const val ADD_SHOW_REQUEST_KEY = "add_show_request"
const val ADD_SHOW_RESULT_KEY = "add_show_result"