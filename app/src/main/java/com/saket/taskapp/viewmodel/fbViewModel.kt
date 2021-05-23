package com.saket.taskapp.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.saket.taskapp.model.TaskModel
import com.saket.taskapp.model.User
import com.saket.taskapp.repository.FireBaseRepository
import kotlinx.coroutines.launch


class fbViewModel(private val fireBaseRepository: FireBaseRepository) : ViewModel() {
     val userLiveData: MutableLiveData<FirebaseUser>? = fireBaseRepository.userLiveData
    val userExists:MutableLiveData<Boolean>?= fireBaseRepository.userExists
    val userInvalid:MutableLiveData<String>?= fireBaseRepository.userInvalid
    val userCreated:MutableLiveData<String>?= fireBaseRepository.userCreated
    var userLiveList: MutableLiveData<MutableList<User>>? = fireBaseRepository.userLiveList
    var userTaskList: MutableLiveData<MutableList<TaskModel>>? = fireBaseRepository.userTaskList
    var currentuserProfile : MutableLiveData<User> = fireBaseRepository.currentuserProfile
    //lateinit var currentUserProfile : User
    fun login(email:String,password:String) {
        fireBaseRepository.login(email, password);
    }

    fun register(email:String,password:String) {
        fireBaseRepository.register(email, password);
    }

    fun signOut()
    {
        fireBaseRepository.signOut()
    }

    fun insertUser(user: User)
    {
        viewModelScope.launch {
            fireBaseRepository.insertUser(user)
        }
    }
    fun getUser()
    {
        fireBaseRepository.getUsers()

    }
    fun getCurrentUser()
    {
        fireBaseRepository.getCurrentUser()
    }
    fun createTaskDir()
    {
        fireBaseRepository.createTaskDir()
    }
    fun addTask(task: TaskModel)
    {
        fireBaseRepository.addTask(task)
    }
    fun getTask(uid :String?)
    {
        fireBaseRepository.getTask(uid)
    }


}