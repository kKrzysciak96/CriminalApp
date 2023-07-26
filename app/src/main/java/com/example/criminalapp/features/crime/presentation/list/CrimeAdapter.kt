package com.example.criminalapp.features.crime.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.criminalapp.databinding.CrimeItemBinding
import com.example.criminalapp.features.crime.presentation.model.CrimeDisplayable
import java.util.*

class CrimeAdapter(private val crimeList: List<CrimeDisplayable>, private val onCrimeClick: (UUID)->Unit) :
    RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder>() {

    inner class CrimeViewHolder(private val binding: CrimeItemBinding) : ViewHolder(binding.root) {

        fun bind(crime: CrimeDisplayable) {
            binding.run {
                crimeTitle.text = crime.title
                crimeDate.text = crime.date.toString()
                isSolved.isVisible = crime.isSolved
                root.setOnClickListener {
                    onCrimeClick(crime.id)

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CrimeItemBinding.inflate(layoutInflater, parent, false)
        return CrimeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return crimeList.size
    }

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        val singleCrime = crimeList[position]
        holder.bind(singleCrime)
    }
}