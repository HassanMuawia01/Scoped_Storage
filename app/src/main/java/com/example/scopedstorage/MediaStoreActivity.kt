package com.example.scopedstorage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.scopedstorage.databinding.ActivityMediaStoreBinding
import java.text.SimpleDateFormat
import java.util.Date

class MediaStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaStoreBinding
    private  var requriedPermission = android.Manifest.permission.CAMERA
    private  var requriedPermissionForStorage = android.Manifest.permission.READ_MEDIA_IMAGES

    val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted:Boolean ->
        if (isGranted){
                openCamera()
        }else{
            Toast.makeText(this, "required for Open Camera Permission", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraOpen= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
       result ->
        val capturedImage = result.data!!.extras!!.get("data") as Bitmap
        if (capturedImage != null) {
            saveImageToInternalStorage(capturedImage)
        }
    }

    val storagePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted : Boolean ->
        if (isGranted){
            openGalleryForImagePick()
        }
        else {
            Toast.makeText(this, "Required Permission For Gallery", Toast.LENGTH_SHORT).show()
        }
    }
    val storageOpen = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                Toast.makeText(this, "image load", Toast.LENGTH_SHORT).show()
                displayImageFromUri(selectedImageUri)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureImage.setOnClickListener {
            checkCameraPermission()
        }

        binding.loadImage.setOnClickListener {
            checkStoragePermission()
        }
    }



    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,requriedPermissionForStorage) ==
            PackageManager.PERMISSION_GRANTED) {
            openGalleryForImagePick()
        }
        else {
            storagePermission.launch(requriedPermissionForStorage)
        }
    }

    private fun openGalleryForImagePick() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        storageOpen.launch(intent)
    }

    private fun displayImageFromUri(selectedImageUri: Uri) {
        try {
            val bitmap = getBitmapFromUri(selectedImageUri)
            if (bitmap != null){
                binding.imageView.setImageBitmap(bitmap)
            }else Toast.makeText(this, "Filed Load Image", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    @SuppressLint("Range")
    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
        val contentResolver = contentResolver
        val cursor : Cursor? = contentResolver.query(uri!!,null,null,null,null)
        if (cursor != null && !cursor.moveToFirst()){
            cursor.close()
            return null
        }
        val imagePath = cursor!!.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return BitmapFactory.decodeFile(imagePath)
    }

    private fun checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,requriedPermission)
            == PackageManager.PERMISSION_GRANTED){
            openCamera()
        }else{
            cameraPermission.launch(requriedPermission)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraOpen.launch(intent)
    }

    private fun saveImageToInternalStorage(image: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${Environment.DIRECTORY_PICTURES}/CameraApp_$timeStamp.jpg"
        val valueContent = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE,"image.jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val resolver = contentResolver
        val imageUri : Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,valueContent)

        if (imageUri != null){
            try {
                val outputStream = resolver.openOutputStream(imageUri)
                image.compress(Bitmap.CompressFormat.JPEG,100,outputStream!!)
                outputStream.close()
                Toast.makeText(this, "Image Save Successfully", Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}