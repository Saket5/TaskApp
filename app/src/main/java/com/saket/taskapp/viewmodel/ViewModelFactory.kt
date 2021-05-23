package com.saket.taskapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saket.taskapp.repository.FireBaseRepository
import java.lang.IllegalArgumentException

class ViewModelFactory (
    private val fireBaseRepository:FireBaseRepository)
    : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(fbViewModel::class.java)){
                return fbViewModel(fireBaseRepository) as T
            }
            throw IllegalArgumentException("Unknown View Model class")
        }
}