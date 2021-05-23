package com.saket.taskapp.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseUser
import com.saket.taskapp.R
import com.saket.taskapp.databinding.FragmentLoginBinding
import com.saket.taskapp.ui.activity.MainActivity
import com.saket.taskapp.viewmodel.fbViewModel


class LoginFragment : Fragment() {


    private lateinit var binding : FragmentLoginBinding
    lateinit var fbViewModel: fbViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentLoginBinding.bind(view);
        fbViewModel = (activity as MainActivity).firebaseViewModel
        binding.createButton.setOnClickListener{
            it.findNavController().navigateUp()
        }

        binding.loginButton.setOnClickListener{

            if(validateFields()){
                fbViewModel.login(binding.emailEditText.text.toString(),binding.passwordEditText.text.toString())

                fbViewModel.userInvalid?.observe(viewLifecycleOwner,
                    {
                        if(it!=null)
                        {
                            binding.loginScreenProgressBar.visibility=View.INVISIBLE
                            if(it.contains("password"))
                                binding.loginScreenPasswordTextInput.error = "Wrong Password"
                            else
                                binding.loginScreenEmailTextInput.error = "Email does not exists"
                        }

                    })
                binding.loginScreenProgressBar.visibility=View.VISIBLE
            }
        }

        fbViewModel.userLiveData?.observe(viewLifecycleOwner,
            { firebaseUser ->
                if (firebaseUser != null) {
                    getView()?.let {
                        Navigation.findNavController(it)
                            .navigate(R.id.action_loginFragment_to_homeFragment)

                        fbViewModel.getCurrentUser()
                        Log.e("Create Fragment","User ${fbViewModel.currentuserProfile}")
                    }
                }
            })

    }
    private fun validateFields(): Boolean {
        Log.d(TAG, "validateFields: starts")
        val emailValidation = validateEmail(binding.emailEditText.text.toString())
        val passwordValidation = validatePassword(binding.passwordEditText.text.toString())
        if (emailValidation == null && passwordValidation == null) {
            Log.d(TAG, "validateFields() returned: " + true)
            return true
        }
        if (emailValidation != null) {
            binding.loginScreenEmailTextInput.error = emailValidation
        }
        if (passwordValidation != null) {
            binding.loginScreenPasswordTextInput.error = passwordValidation
        }
        Log.d(TAG, "validateFields() returned: " + false)
        return false
    }

    private fun validateEmail(email: String): String? {
        return if (email.trim { it <= ' ' }.isEmpty()) {
            "Email cannot be empty"
        } else null
    }

    private fun validatePassword(password: String): String? {
        return if (password.trim { it <= ' ' }.isEmpty()) {
            "Password cannot be empty"
        } else null
    }


}