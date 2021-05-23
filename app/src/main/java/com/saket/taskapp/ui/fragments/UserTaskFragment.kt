package com.saket.taskapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.saket.taskapp.R
import com.saket.taskapp.adapter.HomeAdapter
import com.saket.taskapp.adapter.TaskAdapter
import com.saket.taskapp.databinding.FragmentHomeBinding
import com.saket.taskapp.databinding.FragmentUserTaskBinding
import com.saket.taskapp.model.User
import com.saket.taskapp.ui.activity.MainActivity
import com.saket.taskapp.viewmodel.fbViewModel



class UserTaskFragment : Fragment() {

    private lateinit var currentUser: User
    private lateinit var binding : FragmentUserTaskBinding
    lateinit var fbViewModel: fbViewModel
    lateinit var taskAdapter:TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentUserTaskBinding.bind(view)

        fbViewModel = (activity as MainActivity).firebaseViewModel
        val args : UserTaskFragmentArgs by navArgs()
        currentUser = args.clickedUser
        setCarView()
        setUpRecyclerView()
        binding.taskToolbar.title= currentUser.name
        
        binding.taskToolbar.setOnClickListener{
            Navigation.findNavController(it)
                .navigateUp()
        }
    }

    private fun setUpRecyclerView() {
        taskAdapter = TaskAdapter()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = taskAdapter
        }
        fbViewModel.getTask(currentUser.uid)
        fbViewModel.userTaskList?.observe(viewLifecycleOwner,{

            var localtaskList : MutableList<String> = mutableListOf(String())
            for(task in it)
                localtaskList.add(task.value.toString())
            localtaskList.removeAt(0)
            taskAdapter.differ.submitList(localtaskList)
            Log.e("FireBase", "TaskFragment $it")
        })

    }

    fun setCarView(){
        binding.userTaskName.text="Name :"+currentUser.name
        binding.userAge.text=currentUser.age.toString()
        binding.userDob.text=currentUser.dob
        binding.userEmail.text=currentUser.email
    }


}