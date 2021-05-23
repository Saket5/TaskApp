package com.saket.taskapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.saket.taskapp.adapter.TaskAdapter
import com.saket.taskapp.databinding.FragmentAddTaskBinding
import com.saket.taskapp.model.TaskModel
import com.saket.taskapp.model.User
import com.saket.taskapp.ui.activity.MainActivity
import com.saket.taskapp.viewmodel.fbViewModel


class AddTaskFragment : Fragment() {

    private lateinit var binding : FragmentAddTaskBinding
    lateinit var fbViewModel: fbViewModel
    lateinit var addtaskAdapter:TaskAdapter
    var taskList : MutableList<String>?=null
    lateinit var userProfile: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentAddTaskBinding.bind(view)
        fbViewModel = (activity as MainActivity).firebaseViewModel
        setUpRecyclerView()
       // Log.e("FireBase"," ViewModel ${fbViewModel.currentUserProfile.toString()}")
        userProfile = User()
        fbViewModel.getCurrentUser()
        userProfile = fbViewModel.currentuserProfile.value!!
        binding.addtaskToolbar.title= userProfile.name
        Log.e("FireBase","TaskView $userProfile")
        getTaskList()
       // populateAdapter()
        binding.addTaskSave.setOnClickListener {
            var taskString = binding.addTaskEdit.text.toString()
            if(taskString!=null){
            addTask(taskString)
            binding.addTaskEdit.text!!.clear()
            getTaskList()
            addtaskAdapter.notifyDataSetChanged()
            }
            else
            {
                binding.addTaskInputText.error="Task Cannot be empty"
            }
        }
        binding.addtaskToolbar.setOnClickListener{
            Navigation.findNavController(it)
                .navigateUp()
        }

    }

    private fun addTask(task: String) {
        taskList?.add(task)

        var count = taskList?.size
        populateAdapter(taskList)
            if (count != null) {
                fbViewModel.addTask(TaskModel((count),task))
            }
           else
            fbViewModel.addTask(TaskModel(0,task))
    }

    private fun getTaskList(){
        var localtaskList : MutableList<String> = mutableListOf(String())
        fbViewModel.getTask(userProfile.uid)
        fbViewModel.userTaskList?.observe(viewLifecycleOwner,{
           // addtaskAdapter.differ.submitList(it)
           // this@AddTaskFragment taskList = it
            if(it == null)
                it?.let { it1 -> populateAdapter(null) }
            else {
                var localtaskList: MutableList<String> = mutableListOf(String())

                for (task in it)
                    localtaskList.add(task.value.toString())
                populateAdapter(localtaskList)
            }

        })
        Log.e("FireBase","AddHomeList $localtaskList")

    }


    private fun setUpRecyclerView() {
        addtaskAdapter = TaskAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = addtaskAdapter
        }
        addtaskAdapter.notifyDataSetChanged()

    }
    fun populateAdapter(localTaskList: MutableList<String>?)
    {
        localTaskList!!.removeAt(0)
        addtaskAdapter.differ.submitList(localTaskList)
        addtaskAdapter.notifyDataSetChanged()
        taskList=localTaskList
        Log.e("FireBase","Adapter $taskList")
    }



}