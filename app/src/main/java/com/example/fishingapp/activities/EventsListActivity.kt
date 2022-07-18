package com.example.fishingapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingapp.database.EventDatabaseHandler
import com.example.fishingapp.R
import com.example.fishingapp.adapters.EventAdapter
import com.example.fishingapp.models.EventModel
import com.example.fishingapp.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_events_list.*
import kotlinx.android.synthetic.main.activity_fish_list.*
import com.example.fishingapp.utils.SwipeToEditCallback

class EventsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_list)

        fabAddEvent.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivityForResult(intent, ADD_EVENT_ACTIVITY_REQUEST_CODE)
        }
        getEventListFromLocalDB()
    }

    private fun setupEventsRecyclerView(eventsList: ArrayList<EventModel>){
        rv_event_list.layoutManager = LinearLayoutManager(this)
        rv_event_list.setHasFixedSize(true)
        val eventsAdapter = EventAdapter(this, eventsList)
        rv_event_list.adapter = eventsAdapter

        eventsAdapter.setOnClickListener(object : EventAdapter.OnClickListener{
            override fun onClick(position: Int, model: EventModel) {
                intent.putExtra(EXTRA_EVENT_DETAILS, model)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_event_list.adapter as EventAdapter
                adapter.notifyEditItem(
                    this@EventsListActivity,
                    viewHolder.adapterPosition,
                    ADD_EVENT_ACTIVITY_REQUEST_CODE
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_event_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_event_list.adapter as EventAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getEventListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_event_list)
    }

    private fun getEventListFromLocalDB(){
        val dbHandler = EventDatabaseHandler(this)
        val getEventList : ArrayList<EventModel> = dbHandler.getEventList()

        if(getEventList.size > 0){
            rv_event_list.visibility = View.VISIBLE
            tv_no_records_available_2.visibility = View.GONE
            setupEventsRecyclerView(getEventList)
        }else{
            rv_event_list.visibility = View.GONE
            tv_no_records_available_2.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_EVENT_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getEventListFromLocalDB()
            }else{
                Log.e("Activity", "Anulowano")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_day -> {
                startActivity(Intent(this, DayActivity::class.java))
                true
            }
            R.id.action_archiver ->{
                startActivity(Intent(this, FishListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        var ADD_EVENT_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_EVENT_DETAILS = "extra_event_details"
    }
}