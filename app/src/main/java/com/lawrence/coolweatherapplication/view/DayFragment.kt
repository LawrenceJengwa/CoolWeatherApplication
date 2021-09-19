package com.lawrence.coolweatherapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.lawrence.coolweatherapplication.R
import com.lawrence.coolweatherapplication.databinding.FragmentDayBinding

private lateinit var binding: FragmentDayBinding

class DayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day, container, false)
        return binding.root
    }

    companion object {

    }
}