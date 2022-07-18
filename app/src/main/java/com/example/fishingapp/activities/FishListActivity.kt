package com.example.fishingapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fishingapp.database.FishDatabaseHandler
import com.example.fishingapp.R
import com.example.fishingapp.models.FishModel
import com.example.fishingapp.adapters.FishAdapter
import com.example.fishingapp.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_fish_list.*
import com.example.fishingapp.utils.SwipeToEditCallback

class FishListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_list)

        fabAddFish.setOnClickListener {
            val intent = Intent(this, AddFishActivity::class.java)
                startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getFishListFromLocalDB()
    }

    private fun setupFishRecyclerView(fishList: ArrayList<FishModel>){
        rv_fish_list.layoutManager = LinearLayoutManager(this)

        rv_fish_list.setHasFixedSize(true)
        val fishAdapter = FishAdapter(this, fishList)
        rv_fish_list.adapter = fishAdapter

        fishAdapter.setOnClickListener(object : FishAdapter.OnClickListener{
            override fun onClick(position: Int, model: FishModel) {
                val intent = Intent(this@FishListActivity,
                    FishDetailActivity::class.java)
                intent.putExtra(EXTRA_FISH_DETAILS, model)
                startActivity(intent)
            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_fish_list.adapter as FishAdapter
                adapter.notifyEditItem(
                    this@FishListActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_fish_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_fish_list.adapter as FishAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getFishListFromLocalDB()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_fish_list)
    }

    private fun getFishListFromLocalDB(){
        val dbHandler = FishDatabaseHandler(this)
        val getFishList : ArrayList<FishModel> = dbHandler.getFishList()

        if(getFishList.size > 0){
            rv_fish_list.visibility = View.VISIBLE
            tv_no_records_fish_available.visibility = View.GONE
            setupFishRecyclerView(getFishList)
        }else{
            rv_fish_list.visibility = View.GONE
            tv_no_records_fish_available.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getFishListFromLocalDB()
            }else{
                Log.e("Activity", "anulowanie akcji")
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
            R.id.action_events ->{
                startActivity(Intent(this, EventsListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_FISH_DETAILS = "extra_fish_details"
    }
}