package com.mingyuwu.barurside.addactivity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentActivityPageBinding
import com.mingyuwu.barurside.databinding.FragmentAddActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class AddActivityFragment : Fragment() {

    private val calender = Calendar.getInstance()
    private lateinit var binding: FragmentAddActivityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_activity, container, false
        )

        // activity start time
        binding.addActivityStart.setOnClickListener {
            datePicker(binding.addActivityStart)
        }


        return binding.root
    }

    private fun datePicker(view: View) {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            timePicker(view)
            calender.set(year, month, day)
            format("yyyy / MM / dd", view)
        }

        DatePickerDialog(binding.root.context,
            dateListener,
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun timePicker(view: View) {
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hour, min->
            calender.set(Calendar.HOUR_OF_DAY, hour)
            calender.set(Calendar.MINUTE, min)
            format("HH : mm", view)
        }

        TimePickerDialog(binding.root.context,
            timeListener,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun format(format: String, view: View) {
        val time = SimpleDateFormat(format, Locale.TAIWAN)
        (view as TextView).text = time.format(calender.time)
    }
}