package com.example.fishingapp.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fishingapp.database.EventDatabaseHandler
import com.example.fishingapp.R
import com.example.fishingapp.models.EventModel
import kotlinx.android.synthetic.main.activity_add_event.*
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity(), View.OnClickListener {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var mEventDetails : EventModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        setSupportActionBar(toolbar_add_event)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_add_event.setNavigationOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(EventsListActivity.EXTRA_EVENT_DETAILS)){
            mEventDetails = intent.getSerializableExtra(
                EventsListActivity.EXTRA_EVENT_DETAILS
            ) as EventModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()

        if(mEventDetails != null){
            supportActionBar?.title = "Edycja wyprawy"
            et_date_event.setText(mEventDetails!!.date)
            et_location_event.setText(mEventDetails!!.location)
            et_description_event.setText(mEventDetails!!.description)
            btn_save_event.text = "AKTUALIZUJ"
        }
        et_date_event.setOnClickListener(this)
        btn_save_event.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date_event ->{
                DatePickerDialog(this@AddEventActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.btn_save_event ->{
                when{
                    et_date_event.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this,
                            "Podaj datę wyprawy",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    et_location_event.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this,
                            "Podaj miejsce wyprawy",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else ->{
                        val eventModel = EventModel(
                            if(mEventDetails == null) 0 else mEventDetails!!.id,
                            et_date_event.text.toString(),
                            et_location_event.text.toString(),
                            et_description_event.text.toString()
                        )
                        val dbHandler = EventDatabaseHandler(this)

                        if(mEventDetails == null){
                            val addEvent = dbHandler.addEvent(eventModel)
                            if(addEvent > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            val updateEvent = dbHandler.updateEvent(eventModel)
                            if(updateEvent > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        et_date_event.setText(sdf.format(cal.time).toString())
    }
}