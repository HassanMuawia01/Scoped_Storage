package com.example.scopedstorage

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.scopedstorage.databinding.ActivityTextFileBinding
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.StringBuilder


class TextFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextFileBinding

    private  var uri: Uri? = null
    
    private val createFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data?.data.let {
            createFile(it)
        }
    }

    private val readFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data?.data.let {
            uri = it
            val read = readFile(uri)
            binding.updateText.setText(read)
        }
    }

    private val deleteFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data!!.data.let {
            deleteFile(it)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val filename = binding.fileName.text.toString()
            val fileContent = binding.fileContent.text.toString()
            if (filename.isNotEmpty() && fileContent.isNotEmpty())
                createFile()
            else Toast.makeText(this, "Please Enter File Name or File Content", Toast.LENGTH_SHORT).show()
        }

        binding.btnReadData.setOnClickListener {
              readFile()
        }

        binding.btnUpdateData.setOnClickListener {
            val filecontent = binding.updateText.text.toString()
            if (filecontent.isNotEmpty()){
                updateFile(uri)
            }
        }

        binding.btnDelete.setOnClickListener {
            deleteFile()
        }
    }

    private fun deleteFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/*"
        deleteFile.launch(intent)
    }
    private fun deleteFile(uri: Uri?){
        val cursor = this.contentResolver.query(uri!!,null,null,null,null)
        try {
            if (cursor != null && cursor.moveToFirst()){
                val alertDialog = AlertDialog.Builder(this)

                alertDialog.apply {
                    setTitle("Delete")
                    setIcon(R.drawable.baseline_delete_24)
                    setMessage("Do you want to delete this file ")
                    setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                        DocumentsContract.deleteDocument(context.contentResolver,uri)
                        Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show()
                    }
                    setNegativeButton("No") { _, _ ->
                        Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                    }



                }.create().show()
                  }
        }catch (e:Exception){
                e.printStackTrace()
        }
        finally {
            cursor!!.close()
        }
    }

    private fun updateFile(uri: Uri?){
        try {
            val parcelFileDispatcher = this.contentResolver.openFileDescriptor(uri!!,"wrt")
            val outputStream = FileOutputStream(parcelFileDispatcher!!.fileDescriptor)
            outputStream.write(binding.updateText.text.toString().toByteArray())
            outputStream.close()
            parcelFileDispatcher.close()
            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
        finally {
            binding.updateText.setText("")
        }
    }
    
    private fun readFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/*"
        readFile.launch(intent)
    }
    private fun readFile(uri: Uri?):String{
        try {
            val inputStream = this.contentResolver.openInputStream(uri!!)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val lines = StringBuilder()
            do {
                val l = bufferedReader.readLine()
                l?.let {
                    lines.append(l)
                }
            }while (l!=null)
            return lines.toString()
        }catch (e:Exception){
            e.printStackTrace()
            return ""
        }
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, binding.fileName.text.toString())
        createFile.launch(intent)
    }
    private fun createFile(uri:Uri?){
       try {
           //3 tara k methods hoty h hamary pass w,r,t
           val parcelFileDispatcher = this.contentResolver.openFileDescriptor(uri!!,"w")
           val fileOutputStream = FileOutputStream(parcelFileDispatcher!!.fileDescriptor)
           fileOutputStream.write(binding.fileContent.text.toString().toByteArray())
           fileOutputStream.close()
           parcelFileDispatcher.close()
           Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT).show()
       }catch (e:Exception){
           e.printStackTrace()
       }
        finally {
            binding.fileName.setText("")
            binding.fileContent.setText("")
        }
    }
}