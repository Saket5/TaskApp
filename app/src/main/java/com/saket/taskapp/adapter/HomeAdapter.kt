package com.saket.taskapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saket.taskapp.R
import com.saket.taskapp.databinding.UsersItemBinding
import com.saket.taskapp.model.User

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(val usersItemBinding: UsersItemBinding) :
        RecyclerView.ViewHolder(usersItemBinding.root)


    private val differCallback =
        object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            UsersItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentUser = differ.currentList[position]

        holder.usersItemBinding.userName.text = "Name :"+currentUser.name
        holder.usersItemBinding.userEmail.text = currentUser.email
        holder.usersItemBinding.userDob.text=currentUser.dob
        holder.usersItemBinding.userAge.text=currentUser.age
        //holder.itemBinding.cardView.setCardBackgroundColor(Color.parseColor(R.color.ColorLightBlack.toString()))

        holder.itemView.setOnClickListener { view ->
            val bundle = Bundle().apply {
                putSerializable("clicked_user",currentUser)
            }
            view.findNavController().navigate(R.id.action_homeFragment_to_userTaskFragment,bundle)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}