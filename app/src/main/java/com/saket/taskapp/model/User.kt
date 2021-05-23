package com.saket.taskapp.model

import java.io.Serializable

data class User(var uid:String?= null,
                var name : String?= null,
                var age : String? = null,
                var dob : String?= null,
                var email: String?=null): Serializable

