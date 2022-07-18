package com.example.fishingapp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.fishingapp.database.FishDatabaseHandler
import com.example.fishingapp.R
import com.example.fishingapp.models.FishModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_fish.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddFishActivity : AppCompatActivity(), View.OnClickListener {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mFishDetails : FishModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fish)
        setSupportActionBar(toolbar_add_fish)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_add_fish.setNavigationOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(FishListActivity.EXTRA_FISH_DETAILS)){
            mFishDetails = intent.getSerializableExtra(FishListActivity.EXTRA_FISH_DETAILS) as FishModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()

        if(mFishDetails != null){
            supportActionBar?.title = "Edytuj rybę"

            et_title_fish.setText(mFishDetails!!.title)
            et_description_fish.setText(mFishDetails!!.description)
            et_date_fish.setText(mFishDetails!!.date)
            et_location_fish.setText(mFishDetails!!.location)
            et_length_fish.setText(mFishDetails!!.length)
            et_weight_fish.setText(mFishDetails!!.weight)

            saveImageToInternalStorage = Uri.parse(mFishDetails!!.image)
            iv_place_image_fish.setImageURI(saveImageToInternalStorage)
            btn_save_fish.text = "AKTUALIZUJ"
        }

        et_date_fish.setOnClickListener(this)
        tv_add_image_fish.setOnClickListener(this)
        btn_save_fish.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date_fish -> {
                DatePickerDialog(
                    this@AddFishActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image_fish -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Wybierz opcję:")
                val pictureDialogItems = arrayOf("Dodaj istniejące zdjęcie")
                pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save_fish -> {
                when{
                    et_title_fish.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this@AddFishActivity,
                            "Podaj tytuł.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    et_description_fish.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this@AddFishActivity,
                            "Podaj krótki opis.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    et_location_fish.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this@AddFishActivity,
                            "Podaj miejsce połowu.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    et_length_fish.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this@AddFishActivity,
                            "Podaj długość ryby.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    et_weight_fish.text.isNullOrEmpty() ->{
                        Toast.makeText(
                            this@AddFishActivity,
                            "Podaj wagę ryby.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(
                            this@AddFishActivity,
                            "Dodaj zdjęcie.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else ->{
                        val fishModel = FishModel(
                            if(mFishDetails == null) 0 else mFishDetails!!.id,
                            et_title_fish.text.toString(),
                            saveImageToInternalStorage.toString(),
                            et_description_fish.text.toString(),
                            et_date_fish.text.toString(),
                            et_location_fish.text.toString(),
                            et_length_fish.text.toString(),
                            et_weight_fish.text.toString()
                        )
                        val dbHandler = FishDatabaseHandler(this)

                        if(mFishDetails == null){
                            val addFish = dbHandler.addFish(fishModel)

                            if(addFish > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            val updateFish = dbHandler.updateFish(fishModel)

                            if(updateFish > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data != null){
                    val contentURI = data.data
                    try{
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            contentURI
                        )
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        iv_place_image_fish.setImageBitmap(selectedImageBitmap)
                    }catch(e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddFishActivity,
                            "Błąd dodawania obrazu z galerii",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun choosePhotoFromGallery(){
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("Aby korzystać z tej funkcji musisz " +
                "przyznać wymagane uprawnienia. Przejdź do ustawień.")
            .setPositiveButton("USTAWIENIA"){
                _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("ANULUJ"){
                    dialog, _ ->
                    dialog.dismiss()
            }.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        et_date_fish.setText(sdf.format(cal.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val IMAGE_DIRECTORY = "FishesImages"
    }
}