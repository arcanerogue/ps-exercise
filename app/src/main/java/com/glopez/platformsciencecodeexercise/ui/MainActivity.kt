package com.glopez.platformsciencecodeexercise.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.glopez.platformsciencecodeexercise.R
import com.glopez.platformsciencecodeexercise.databinding.ActivityMainBinding
import com.glopez.platformsciencecodeexercise.data.Driver
import com.glopez.platformsciencecodeexercise.data.Resource

class MainActivity : AppCompatActivity(), DriversAdapter.OnItemClickListener {

    private val viewModel: DriversViewModel by viewModels()
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val driversAdapter = DriversAdapter(this)

        binding.apply {
            driversRecyclerView.apply {
                adapter = driversAdapter
                layoutManager = LinearLayoutManager(view.context)
            }
            progressBar = driversProgressBar
        }

        viewModel.drivers.observe(this, { resource ->
            resource?.let {
                when (resource) {
                    is Resource.Success -> {
                        hideLoading()
                        driversAdapter.submitList(resource.data)
                    }
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                    }
                }
            }
        })
    }

    private fun showLoading() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar?.visibility = View.GONE
    }

    override fun onDriverClick(driver: Driver) {
        val destination: String = viewModel.getShipmentForDriver(driver.id)
        val message = if (destination.isEmpty()) {
            getString(R.string.dialog_default_message)
        } else {
            destination
        }
        val dialogBuilder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }
        dialogBuilder.apply {
            setTitle(getString(R.string.dialog_destination_title))
            dialogBuilder.setMessage(message)
            dialogBuilder.setPositiveButton(
                getString(R.string.dialog_destination_ok)) { _, _ -> }
        }.show()

    }
}