package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivityPostBinding
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2


class PostActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostBinding
    var currentPhotoPath: String? = null
    var imageFileName: String? = null
    var photoFile: File? = null
    var photoURI: Uri? = null
    var isCamera = true
    var contacts: ArrayList<String>? = null
    var links: ArrayList<String>? = null
    var tags: ArrayList<String>? = null
    var user: FirebaseUser? = null
    var postRef: DatabaseReference? = null
    var userRef: DatabaseReference? = null

    var timeStamp: String? = null
    var title: String? = null
    var body: String? = null
    var location: String? = null
    var userID: String? = null
    var userName: String? = null
    var bitmap: Bitmap? = null

    companion object {
        const val TAG = "PostsActivity"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Share a Lead or Resource"
        binding = ActivityPostBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        user = FirebaseAuth.getInstance().currentUser
        userID = user?.uid
        userName = user?.displayName
        postRef = FirebaseDatabase.getInstance().reference.child("Leads")
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(user?.uid!!)
        tags = ArrayList()
        contacts = ArrayList()
        links = ArrayList()

        binding.image.visibility = View.GONE


        binding.addImage.setOnClickListener {
            displayChoice()
        }

        setUpFilters()

        binding.post.setOnClickListener {
            timeStamp = SimpleDateFormat("dd/MM/yyyy hh:mm a ").format(Date())
            title = binding.titleEt.text.toString()
            body = binding.body.text.toString()
            location = binding.locationEt.text.toString()
            var contact1 = binding.contact1.text.toString()
            var contact2 = binding.contact2.text.toString()

            if (!contact1.isNullOrEmpty())
                contacts?.add(contact1)
            if (!contact2.isNullOrEmpty())
                contacts?.add(contact2)

            var link1 = binding.link1.text.toString()
            var link2 = binding.link2.text.toString()

            if (!link1.isNullOrEmpty())
                links?.add(link1)
            if (!link2.isNullOrEmpty())
                links?.add(link2)

            if (title.isNullOrEmpty()) {
                Toast.makeText(this, "Please Provide a Title.", Toast.LENGTH_SHORT).show()
            }
            if (body.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    "Please Provide at least 1 line in your post.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (tags.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    "Please Select at least 1 tag for better placement of the lead",
                    Toast.LENGTH_SHORT
                ).show()
            }


            if (null == photoURI) {
                post(null)
            } else {
                uploadImageAndPost()
            }

        }
    }

    private fun post(imageUrl: String?) {
        var ref = postRef?.push()

        var post = Post(
            ref?.key.toString(),
            userID!!,
            userName ?: "",
            title!!,
            body!!,
            tags!!,
            contacts,
            links,
            imageUrl,
            ArrayList(),
            ArrayList(),
            ArrayList(),
            timeStamp!!,
            location!!
        )

        ref?.setValue(post)?.addOnCompleteListener {
            Toast.makeText(
                this@PostActivity,
                "Lead Successfully Posted",
                Toast.LENGTH_SHORT
            )
                .show()
            this@PostActivity.finish()
        }
        userRef?.child("Posts")?.push()?.setValue(post)
    }

    private fun uploadImageAndPost() {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef =
            storageRef.child(
                "images/"
                        + UUID.randomUUID().toString())

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage(resources.getString(R.string.please_wait))
        progressDialog.setCancelable(false)
        progressDialog.show()

        var uploadTask = imageRef.putFile(photoURI!!)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            imageRef.downloadUrl.addOnSuccessListener {
                post(it?.toString())
                progressDialog.dismiss()
            }

        }

        uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100 * bytesTransferred) / totalByteCount
            Log.d(TAG, "Upload is $progress% done")
        }.addOnPausedListener {
            Log.d(TAG, "Upload is paused")
        }


    }

    private fun setUpFilters() {
        binding.beds.setOnClickListener {
            var color = binding.bedsTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Beds")
                binding.beds.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.bedsTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Beds")
                binding.beds.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.bedsTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }
        binding.oxi.setOnClickListener {

            var color = binding.oxiTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Oxygen")
                binding.oxi.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.oxiTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Oxygen")
                binding.oxi.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.oxiTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }
        binding.medicine.setOnClickListener {

            var color = binding.medsTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Medicines")
                binding.medicine.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.medsTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Medicines")
                binding.medicine.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.medsTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }
        binding.equipment.setOnClickListener {

            var color = binding.equipTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Equipment")
                binding.equipment.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.equipTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Equipment")
                binding.equipment.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.equipTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }
        binding.others.setOnClickListener {

            var color = binding.othersTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Others")
                binding.others.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.othersTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Others")
                binding.others.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.othersTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }
        binding.food.setOnClickListener {

            var color = binding.foodTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Food")
                binding.food.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.foodTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Food")
                binding.food.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.foodTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }

        binding.plasma.setOnClickListener {

            var color = binding.plasmaTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Plasma")
                binding.plasma.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.plasmaTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Plasma")
                binding.plasma.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.plasmaTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }

        binding.ambulance.setOnClickListener {

            var color = binding.ambulanceTv.currentTextColor
            if (color == resources.getColor(R.color.black)) {
                tags?.add("Food")
                binding.ambulance.setCardBackgroundColor(this.resources.getColor(R.color.purple_700))
                binding.ambulanceTv.setTextColor(this.resources.getColor(R.color.white))
            } else {
                tags?.remove("Food")
                binding.ambulance.setCardBackgroundColor(this.resources.getColor(R.color.white))
                binding.ambulanceTv.setTextColor(this.resources.getColor(R.color.black))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun displayChoice() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.selection_dialog)
        dialog.findViewById<View>(R.id.camera).setOnClickListener { view: View? ->
            isCamera = true
            dialog.cancel()
            uploadImage()
        }
        dialog.findViewById<View>(R.id.gallery).setOnClickListener { view: View? ->
            isCamera = false
            dialog.cancel()
            uploadImage()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun uploadImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                Constants.MY_CAMERA_PERMISSION_CODE
            )
        } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.MY_STORAGE_PERMISSION_CODE
            )
        } else {
            dispatchTakePictureIntent(isCamera)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.MY_CAMERA_PERMISSION_CODE) {
            if (null != grantResults && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(true)
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == Constants.MY_STORAGE_PERMISSION_CODE) {
            if (null != grantResults && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(false)
            } else {
                Toast.makeText(this, "storage permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_CANCELED)
        if (requestCode == Constants.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                CropImage.activity(photoURI)
                    .start(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == Constants.REQUEST_GALLERY && resultCode == RESULT_OK) {
            currentPhotoPath = data?.data.toString()
            try {
                CropImage.activity(data?.data)
                    .start(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                photoURI = result.uri
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        photoURI
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.image.visibility = View.VISIBLE
                binding.image.setImageBitmap(bitmap)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error: Exception = result.error
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageFileName = "Covid19_India_Dashboard" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent(isCamera: Boolean) {
        val takePictureIntent: Intent
        if (isCamera) {
            takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "com.apps.kunalfarmah.covid19_india_dashboard.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO)
        } else {
            takePictureIntent =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(takePictureIntent, Constants.REQUEST_GALLERY)
        }
    }

    private fun showDialog(){
        var dialog = AlertDialog.Builder(this).setTitle("Are you sure you want to discard the lead")
            .setPositiveButton("Go Back") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setNegativeButton("Discard") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                finish()
            }

        dialog.create()
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        showDialog()
    }

}