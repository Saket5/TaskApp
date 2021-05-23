package com.saket.taskapp.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.saket.taskapp.model.TaskModel
import com.saket.taskapp.model.User
import com.saket.taskapp.ui.activity.MainActivity


class FireBaseRepository {

    private val LOG_TAG = "FireBaseRepo"
    private var  auth: FirebaseAuth? = null
    private  var rootNode: FirebaseDatabase
    private  var reference: DatabaseReference
    var userExists: MutableLiveData<Boolean> = MutableLiveData()
    var userInvalid: MutableLiveData<String> = MutableLiveData()
    var userCreated:MutableLiveData<String> = MutableLiveData()
    val userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    var userLiveList: MutableLiveData<MutableList<User>> = MutableLiveData()
    var userTaskList:MutableLiveData<MutableList<TaskModel>> = MutableLiveData()
    var currentuserProfile : MutableLiveData<User> = MutableLiveData()
    init {

        auth = FirebaseAuth.getInstance()
        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode.getReference("users")
        userExists.postValue(true)
        userInvalid.postValue(null)
        if(auth!!.currentUser != null)
            userLiveData.postValue(auth!!.currentUser)
        else
            userLiveData.postValue(null)
        userCreated.postValue(null)

    }

    fun register(email: String, password: String) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(auth!!.currentUser)
                    userCreated.postValue(auth!!.currentUser!!.uid)
                    Log.e(LOG_TAG,"SignUp completed")
                } else {
                    userExists.postValue(false)
                    Log.e(LOG_TAG,"SignUp Failed")
                }
            }
    }

    fun login(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(auth!!.currentUser)
                    Log.e(LOG_TAG,"Login Succesfull")
                } else {
                    userInvalid.postValue(task.exception.toString())
                    Log.e(LOG_TAG,"Login Failed")
                }
            }
    }
    fun signOut()
    {
        auth!!.signOut()
        userLiveData.postValue(null)
        Log.e(LOG_TAG,"Signed Out")
    }

    fun insertUser(user : User){
        //Make users database
        reference.child(auth!!.currentUser!!.uid).child("profile").setValue(user)
        Log.e(LOG_TAG,"User created")
    }
    fun getCurrentUser()
    {
        reference.child(auth!!.currentUser!!.uid).child("profile").get().addOnSuccessListener {
            var userProfile = it.getValue<User>()
            if (userProfile != null) {
               currentuserProfile.postValue(userProfile)
            }
        }
        Log.e(LOG_TAG," Current User $currentuserProfile")
    }

    fun getUsers()  {
        var userList : MutableList<User> = mutableListOf<User>()
        reference.addChildEventListener (object :
            ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                var usersDB = dataSnapshot.child("profile").getValue<User>()

                if (usersDB != null && usersDB.uid!= auth!!.currentUser!!.uid) {
                    userList.add(usersDB)
                   // Log.e(LOG_TAG,"new User $userList, $prevChildKey")
                }
                this@FireBaseRepository.userLiveList.postValue(userList)
                //Log.e(LOG_TAG,"UserList $userLiveList, $prevChildKey")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {

            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        //Log.e(LOG_TAG,"UserList $userList")

    }
    fun createTaskDir()
    {
        reference.child(auth!!.currentUser!!.uid).child("tasks")
    }
    fun addTask(task:TaskModel)
    {
        reference.child(auth!!.currentUser!!.uid).child("tasks").child(task.taskId.toString()).setValue(task.value)
    }

    fun getTask(uid:String?)
    {
        var taskList : MutableList<TaskModel> = mutableListOf<TaskModel>()
        var count =0
        if (uid != null) {
           /* reference.child(uid).child("tasks").addChildEventListener (object :
                ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot,prevChildKey: String?) {
                    var tasksDB = dataSnapshot.getValue<String>()
                    Log.e(LOG_TAG,"TaskList $tasksDB" )
                    if (tasksDB != null) {
                        taskList.add(TaskModel(count,tasksDB))
                        count+=1
                        //Log.e(LOG_TAG,"new User $userList, $prevChildKey")
                    }
                    this@FireBaseRepository.userTaskList.postValue(taskList)
                    Log.e(LOG_TAG,"TaskList $taskList, $prevChildKey")
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {

                }
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })*/
            reference.child(uid).child("tasks").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Log.e(LOG_TAG,"Task $snapshot")
                    if(snapshot!=null) {
                        for (postSnapshot in snapshot.children) {
                            var id = postSnapshot.key
                            var value = postSnapshot.value
                           // id!!.toInt()
                            var cid = id?.toInt()
                            var tasksDB = TaskModel(cid, value as String? )
                            taskList.add(tasksDB)
                            //var tasksDB = postSnapshot.getValue<TaskModel>()
                            //Log.e(LOG_TAG, "Task $postSnapshot , $id , $value ,$tasksDB ")

                        }
                       // Log.e(LOG_TAG, "Task $taskList")
                    }
                    this@FireBaseRepository.userTaskList.postValue(taskList)
                   // Log.e(LOG_TAG,"TaskList $userTaskList")
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }


    }


}



