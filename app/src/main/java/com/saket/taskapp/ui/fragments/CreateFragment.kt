package com.saket.taskapp.ui.fragments

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.saket.taskapp.R
import com.saket.taskapp.databinding.FragmentCreateBinding
import com.saket.taskapp.model.User
import com.saket.taskapp.ui.activity.MainActivity
import com.saket.taskapp.viewmodel.fbViewModel


class CreateFragment : Fragment() {

    private lateinit var binding : FragmentCreateBinding
    lateinit var fbViewModel:fbViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentCreateBinding.bind(view)
        fbViewModel = (activity as MainActivity).firebaseViewModel
        binding.loginButton.setOnClickListener{
            it.findNavController().navigate(R.id.action_createFragment_to_loginFragment)
        }
        binding.createAccountButton.setOnClickListener{
            if(validateFields()){
                var email= true
                fbViewModel.register(binding.emailEditText.text.toString(),binding.passwordEditText.text.toString())
                fbViewModel.userExists?.observe(viewLifecycleOwner,
                    {
                        if(it== false){
                            binding.createAccountScreenProgressBar.visibility=View.INVISIBLE
                            binding.emailTextInput.error = "Email already Exists"
                            email =false}

                    })
                fbViewModel.userCreated?.observe(viewLifecycleOwner,{
                    if(it!=null) {

                        fbViewModel.insertUser(createUser(it))
                        fbViewModel.createTaskDir()
                        fbViewModel.getCurrentUser()
                        Log.e("Create Fragment","User ${fbViewModel.currentuserProfile}")
                    }
                })


                binding.createAccountScreenProgressBar.visibility=View.VISIBLE


            }
        }


       fbViewModel.userLiveData?.observe(viewLifecycleOwner,
            { firebaseUser ->
                if (firebaseUser != null) {
                    getView()?.let {
                        Navigation.findNavController(it)
                            .navigate(R.id.action_createFragment_to_homeFragment)
                        fbViewModel.getCurrentUser()
                        Log.e("Create Fragment","User ${fbViewModel.currentuserProfile}")
                    }

                }

            })
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        /*binding.DobTextInput.setOnClickListener (View.OnClickListener{

        })*/
        binding.DateEditText.isFocusableInTouchMode=false
        binding.DateEditText.setOnClickListener(View.OnClickListener {
            datePicker.show(parentFragmentManager,datePicker.toString())
        })
        datePicker.addOnPositiveButtonClickListener {
            var date =datePicker.headerText
            var year = date.substring(7).replace(" ","")
            var month = date.substring(0,4).replace(" ","")
            var months = listOf<String>("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
            var setmonth = months.indexOf(month)+1
            var day = date.substring(4,7).replace(" ","")
            day=day.replace(",","")
            var setDate= day+"/"+setmonth.toString()+"/"+year
            Log.e("Date Picekr","${datePicker.headerText} ${setDate} ${it}")
            binding.DateEditText.setText(setDate)
        }


    }

    private fun createUser(fbUser: String): User {
        return User(fbUser,binding.fullNameEditText.text.toString(), binding.AgeEditText.text.toString(), binding.DateEditText.text.toString(),binding.emailEditText.getText().toString())
    }





    private fun validateFields(): Boolean {
        var fullName = binding.fullNameEditText.getText().toString()
        val nameValidation = validateName(fullName)
        val dateValidation =validateDOB(binding.DateEditText.text.toString())
        val ageValidation = validateAge(binding.AgeEditText.text.toString())
        var email = binding.emailEditText.getText().toString()
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(
            binding.passwordEditText.getText().toString(),
            binding.confirmPasswordEditText.getText().toString()
        )
        val confirmPasswordValidation = validateConfirmPassword(
            binding.confirmPasswordEditText.getText().toString(),
            binding.passwordEditText.getText().toString()
        )
        if (nameValidation == null && dateValidation == null && ageValidation == null && emailValidation == null && passwordValidation == null && confirmPasswordValidation == null ) {
            Log.d(TAG, "validateFields: all fields valid")
            return true
        }
        if(dateValidation != null)
        {
            binding.DobTextInput.error= dateValidation
        }
        if(ageValidation != null)
        {
            binding.ageTextInput.error= ageValidation
        }
        if (nameValidation != null) {
            binding.fullNameTextInput.error = nameValidation
        }
        if (emailValidation != null) {
            binding.emailTextInput.error = emailValidation
        }
        if (passwordValidation != null) {
            binding.passwordTextInput.error = passwordValidation
        }
        if (confirmPasswordValidation != null) {
            binding.confirmPasswordTextInput.error = confirmPasswordValidation
        }
        return false
    }

    private fun validateDOB(dob: String): String? {
        if (dob.trim { it <= ' ' }.length == 0) {
            return "Date of Birth cannot be empty"
        }
        return null


    }

    private fun validateName(name: String): String? {
        if (name.trim { it <= ' ' }.isEmpty()) {
            return "Name cannot be empty"
        } else if (name.trim { it <= ' ' }.matches(Regex("^[0-9]+$"))) {
            return "Name cannot have numbers in it"
        } else if (!name.trim { it <= ' ' }.matches(Regex("^[a-zA-Z][a-zA-Z ]++$"))) {
            return "Invalid Name"
        }
        return null
    }

    private fun validateAge(age: String):String?{
        if (age.trim { it <= ' ' }.length == 0) {
            return "Age cannot be empty"
        }
        return null
    }
    private fun validateEmail(email: String): String? {
        if (email.trim { it <= ' ' }.length == 0) {
            return "Email cannot be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim { it <= ' ' }).matches()) {
            return "Invalid Email"
        }
        return null
    }

    private fun validatePassword(password: String, confirmPassword: String): String? {
        if (password.trim { it <= ' ' }.isEmpty()) {
            return "Passwords cannot be empty"
        } else if (confirmPassword.trim { it <= ' ' }.isEmpty()) {
            return "Passwords cannot be empty"
        } else if (confirmPassword.trim { it <= ' ' } != password.trim { it <= ' ' }) {
            return "Passwords do not match"
        } else if (password.trim { it <= ' ' }.length < 8) {
            return "Password too small. Minimum length is 8"
        } else if (password.trim { it <= ' ' }.length > 15) {
            return "Password too long. Maximum length is 15"
        } else if (password.trim { it <= ' ' }.contains(" ")) {
            return "Password cannot contain spaces"
        }
        return null
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        if (confirmPassword.trim { it <= ' ' }.isEmpty()) {
            return "Passwords cannot be empty"
        } else if (password.trim { it <= ' ' }.isEmpty()) {
            return "Passwords cannot be empty"
        } else if (confirmPassword.trim { it <= ' ' } != password.trim { it <= ' ' }) {
            return "Passwords do not match"
        } else if (password.trim { it <= ' ' }.length < 8) {
            return "Password too small. Minimum length is 8"
        } else if (password.trim { it <= ' ' }.length > 15) {
            return "Password too long. Maximum length is 15"
        } else if (password.trim { it <= ' ' }.contains(" ")) {
            return "Password cannot contain spaces"
        }
        return null
    }




}


