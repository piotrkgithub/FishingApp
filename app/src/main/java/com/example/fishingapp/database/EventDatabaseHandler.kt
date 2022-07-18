package com.example.fishingapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.fishingapp.models.EventModel

class EventDatabaseHandler (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EventsAppDatabase"
        private const val TABLE_EVENT = "EventTable"
        private const val KEY_ID = "_id"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_DESCRIPTION = "description"
    }

    override fun onCreate(dbEvent: SQLiteDatabase?) {
        val CREATE_EVENT_TABLE = ("CREATE TABLE " + TABLE_EVENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_DESCRIPTION + " TEXT)")
        dbEvent?.execSQL(CREATE_EVENT_TABLE)
    }

    override fun onUpgrade(dbEvent: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        dbEvent!!.execSQL("DROP TABLE IF EXISTS $TABLE_EVENT")
        onCreate(dbEvent)
    }

    fun addEvent(event: EventModel): Long {
        val dbEvent = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_DATE, event.date)
        contentValues.put(KEY_LOCATION, event.location)
        contentValues.put(KEY_DESCRIPTION, event.description)
        val result = dbEvent.insert(TABLE_EVENT, null, contentValues)
        dbEvent.close()
        return result
    }

    fun updateEvent(event: EventModel): Int {
        val dbEvent = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_DATE, event.date)
        contentValues.put(KEY_LOCATION, event.location)
        contentValues.put(KEY_DESCRIPTION, event.description)
        val success = dbEvent.update(
            TABLE_EVENT,
            contentValues,
            KEY_ID + "=" + event.id,
            null
        )
        dbEvent.close()
        return success
    }

    fun deleteEvent(event: EventModel) : Int{
        val dbEvent = this.writableDatabase
        val success = dbEvent.delete(
            TABLE_EVENT,
            KEY_ID + "=" + event.id,
            null)
        dbEvent.close()
        return success
    }

    @SuppressLint("Range")
    fun getEventList():ArrayList<EventModel>{
        val eventList = ArrayList<EventModel>()
        val selectQuery = "SELECT * FROM $TABLE_EVENT"
        val dbEvent = this.readableDatabase
        try {
            val cursor: Cursor = dbEvent.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val placeEvent = EventModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
                    )
                    eventList.add(placeEvent)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            dbEvent.execSQL(selectQuery)
            return ArrayList()
        }
        return eventList
    }
}