package com.saket.taskapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.saket.taskapp.R
import com.saket.taskapp.adapter.HomeAdapter
import com.saket.taskapp.databinding.FragmentHomeBinding
import com.saket.taskapp.model.TaskModel
import com.saket.taskapp.model.User
import com.saket.taskapp.repository.FireBaseRepository
import com.saket.taskapp.ui.activity.MainActivity
import com.saket.taskapp.viewmodel.fbViewModel


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    lateinit var fbViewModel: fbViewModel
    lateinit var  bottomNavigation: BottomNavigationView
    lateinit var usersAdapter: HomeAdapter
    lateinit var currentUser: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentHomeBinding.bind(view)
        fbViewModel = (activity as MainActivity).firebaseViewModel
        currentUser = User()
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            finishAffinity(activity as MainActivity)
        }
        callback.isEnabled
        bottomNavigation = binding.bottomNavigationView
        binding.fab.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
        binding.homeProgressBar.visibility=View.VISIBLE
        setUpRecyclerView()
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.signOut -> {
                    fbViewModel.signOut()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.LightTheme->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.DarkTheme ->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.DefaultTheme ->{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
        getCurrentUser()
        Log.e("Home Frag"," Current User $currentUser")

        //fbViewModel.getCurrentUser()
       // Log.e("Home Fragment","${fbViewModel.currentuserProfile.value!!}")
        fbViewModel.getUser()
        fbViewModel.userLiveList?.observe(viewLifecycleOwner) {
            usersAdapter.differ.submitList(it)
            binding.homeProgressBar.visibility= View.INVISIBLE
            Log.e("FireBase", "Home frag $it")
        }
        fbViewModel.userLiveData?.observe(viewLifecycleOwner,
            { firebaseUser ->
                if (firebaseUser == null) {
                    getView()?.let {
                        Navigation.findNavController(it)
                            .navigate(R.id.action_homeFragment_to_createFragment)
                    }
                }
                else{
                    fbViewModel.getCurrentUser()
                    //Log.e("Home Fragment","${fbViewModel.currentuserProfile.value!!}")
                }
            })

    }

    private fun setUpRecyclerView() {
        usersAdapter = HomeAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = usersAdapter
        }


    }
    fun setCarView(){
        binding.userTaskName.text="Name :"+currentUser.name
        binding.userAge.text=currentUser.age.toString()
        binding.userDob.text=currentUser.dob
        binding.userEmail.text=currentUser.email
    }
    fun getCurrentUser()
    {
         FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile").get().addOnSuccessListener {
            var userProfile = it.getValue<User>()
            if (userProfile != null) {
                currentUser=userProfile
            }
             Log.e("Home Frag"," Current User $currentUser")
             setCarView()
        }

    }



}