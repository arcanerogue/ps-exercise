package com.glopez.platformsciencecodeexercise.ui

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.glopez.platformsciencecodeexercise.databinding.DriverItemBinding
import com.glopez.platformsciencecodeexercise.data.Driver

class DriversAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Driver, DriversAdapter.DriverViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val binding = DriverItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DriverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class DriverViewHolder(private val binding: DriverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val driver = getItem(position)
                        listener.onDriverClick(driver)
                    }
                }
            }
        }

        fun bind(driver: Driver) {
            binding.apply {
                driverName.text = driver.name
            }
        }
    }

    interface OnItemClickListener {
        fun onDriverClick(driver: Driver)
    }

    class DiffCallback : DiffUtil.ItemCallback<Driver>() {
        override fun areItemsTheSame(oldItem: Driver, newItem: Driver) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Driver, newItem: Driver) =
            oldItem == newItem

    }
}