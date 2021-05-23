package com.saket.taskapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saket.taskapp.databinding.TaskItemBinding
import com.saket.taskapp.model.User

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    class TaskViewHolder (val taskItemBinding:TaskItemBinding) :
        RecyclerView.ViewHolder(taskItemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
         return TaskViewHolder(
             TaskItemBinding.inflate(
                 LayoutInflater.from(parent.context), parent, false
             )
         )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = differ.currentList[position]
        holder.taskItemBinding.taskTextView.text = "${position+1}. $currentTask"
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}