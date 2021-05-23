package com.saket.taskapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.ktx.getValue
import com.saket.taskapp.R
import com.saket.taskapp.databinding.ActivityMainBinding
import com.saket.taskapp.model.User
import com.saket.taskapp.repository.FireBaseRepository
import com.saket.taskapp.viewmodel.fbViewModel
import com.saket.taskapp.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var mainbinding : ActivityMainBinding
    lateinit var firebaseViewModel: fbViewModel
    lateinit var currentUserProfile : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        mainbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainbinding.root)

    }

    private fun setUpViewModel() {
        val fireBaseRepository = FireBaseRepository()
        val viewModelProviderFactory =
            ViewModelFactory(
                 fireBaseRepository
            )

        firebaseViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        ).get(fbViewModel::class.java)
        //firebaseViewModel.getCurrentUser()
    }
    /**/
}