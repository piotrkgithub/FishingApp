package com.example.fishingapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.fishingapp.models.FishModel

class FishDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FishingAppDatabase"
        private const val TABLE_FISH = "FishTable"
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LENGTH = "length"
        private const val KEY_WEIGHT = "weight"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_FISH_TABLE = ("CREATE TABLE " + TABLE_FISH + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LENGTH + " TEXT,"
                + KEY_WEIGHT + " TEXT)")
        db?.execSQL(CREATE_FISH_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_FISH")
        onCreate(db)
    }

    fun addFish(fish: FishModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, fish.title)
        contentValues.put(KEY_IMAGE, fish.image)
        contentValues.put(KEY_DESCRIPTION, fish.description)
        contentValues.put(KEY_DATE, fish.date)
        contentValues.put(KEY_LOCATION, fish.location)
        contentValues.put(KEY_LENGTH, fish.length)
        contentValues.put(KEY_WEIGHT, fish.weight)
        val result = db.insert(TABLE_FISH, null, contentValues)
        db.close()
        return result
    }

    fun updateFish(fish: FishModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, fish.title)
        contentValues.put(KEY_IMAGE, fish.image)
        contentValues.put(KEY_DESCRIPTION, fish.description)
        contentValues.put(KEY_DATE, fish.date)
        contentValues.put(KEY_LOCATION, fish.location)
        contentValues.put(KEY_LENGTH, fish.length)
        contentValues.put(KEY_WEIGHT, fish.weight)
        val success = db.update(
            TABLE_FISH,
            contentValues,
            KEY_ID + "=" + fish.id,
            null)
        db.close()
        return success
    }

    fun deleteFish(fish: FishModel) : Int{
        val db = this.writableDatabase
        val success = db.delete(TABLE_FISH, KEY_ID + "=" + fish.id, null)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getFishList():ArrayList<FishModel>{
        val fishList = ArrayList<FishModel>()
        val selectQuery = "SELECT * FROM $TABLE_FISH"
        val db = this.readableDatabase
        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = FishModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_LENGTH)),
                        cursor.getString(cursor.getColumnIndex(KEY_WEIGHT))
                    )
                    fishList.add(place)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return fishList
    }
}