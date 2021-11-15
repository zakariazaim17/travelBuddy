package com.example.myapplication.screens

import android.app.Activity
import android.content.ContentResolver

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.icu.lang.UCharacter.getType
import android.net.Uri
import android.opengl.GLUtils.getType
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Attendees.query
import android.provider.CalendarContract.Instances.query
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentResolverCompat.query

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.MainViewModelFactory
import com.example.myapplication.R
import com.example.myapplication.model.CreatePost

import com.example.myapplication.repository.Repository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_add_post_screen.*
import kotlinx.android.synthetic.main.fragment_add_post_screen.add_Floating_Btn

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var isFabOpen :Boolean = false
private var ImageUri:Uri ?= null
private lateinit var viewModel: MainViewModel
private var ImgPath:File ?= null
private var uploadedImageUrl:String? = null
private var fili:File? = null


class AddPostScreen : Fragment() {
    // TODO: Rename and change types of parameters




    val CAMERA_REQUEST_CODE = 200
    val GALLERY_REQUEST_CODE = 300



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_post_screen, container, false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null){
            imageView.setImageURI(data?.data) // handle chosen image
            ImageUri = data?.data
        }else if(resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE && data != null){
            imageView.setImageBitmap(data.extras?.get("data") as Bitmap)
            ImageUri = data?.data
        }
    }
    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }



    private fun uploadPost(uri:Uri){

        val description =  editText_PostDescription.text.toString()
        val descriptionPart: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("post_data", "{\"description\": ${description}}").build()
        val imagePart:RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image", "https://1.bp.blogspot.com/-lIfFZILZhBg/WyfY50xXXFI/AAAAAAAABEE/fGD29MdIHhIGwcf0tdvl6lz8uRYQSNcOgCLcBGAs/w1200-h630-p-k-no-nu/android-retrofit-2-crud-example.jpg").build()
           // "{\"description\": ${description}}".toRequestBody(MultipartBody.FORM)

        var filePath:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c:Cursor? = requireActivity().contentResolver.query(
            uri!!, filePath,
            null, null, null);
        c?.moveToFirst();
        var columnIndex:Int = c!!.getColumnIndex(filePath[0]);
        val FilePathStr = c.getString(columnIndex);
        Log.d("mm", FilePathStr)
        c.close();

          val originalFile:File = File(FilePathStr)
        fili = originalFile

        //  val originalFile:File by lazy { File(uri.path!!) }
     // val inputStream = requireActivity().contentResolver.openInputStream(Uri.fromFile(originalFile))
        //val filePart:RequestBody = RequestBody.create(MediaType.parse("image/*"),originalFile)
       //- val filePart: RequestBody = originalFile.asRequestBody("image/*".toMediaTypeOrNull())
        //-val file:MultipartBody.Part = MultipartBody.Part.createFormData("image",originalFile.name, filePart)
//Log.d("fileTypii", filePart.toString())
  //      Log.d("filetypiDe", descriptionPart.toString())

        //uploadImagetoFirebase()

        /*try {

            viewModel.createPost(userToken!!, descriptionPart, "multipart/form-data")
        }catch (err:Error){
            Log.d("what", err.toString())
        }
        */
      /*  viewModel.createPostResponse.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                if(!response.body()!!.success){
                    Toast.makeText(context, response.body()?.success.toString(), Toast.LENGTH_LONG).show()
                }else{

                    Toast.makeText(context, "some error occurred" , Toast.LENGTH_SHORT).show()

                }
                Log.d("Main3", response.body()?.success.toString())
                Log.d("Main3", response.code().toString())
                Log.d("Main3", response.message())




            }else{
                Log.d("Main3", response.errorBody().toString()+ description.toString())

            }

        })*/

        val storage = Firebase.storage
        val storageRef = storage.reference
        val ImageRef = storageRef.child("travel/${originalFile.name}")
        val upload = ImageRef.putFile(uri)

        upload
            .addOnFailureListener {
            Log.d("fire", "failled")
        }.addOnSuccessListener { taskSnapshot ->

            ImageRef.downloadUrl.addOnSuccessListener { url->
                Log.d("firee", url.toString())
                uploadedImageUrl = url.toString()
                uploadImagetoFirebase(url.toString())

            }

        }

    }

    private fun uploadImagetoFirebase(url:String) {
        //val filePart: RequestBody = fili!!.asRequestBody("image/*".toMediaTypeOrNull())

       //val descriptionPart1: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("post_data", "{\"description\": \"khraaaa\", \"image\":\"https://1.bp.blogspot.com/-lIfFZILZhBg/WyfY50xXXFI/AAAAAAAABEE/fGD29MdIHhIGwcf0tdvl6lz8uRYQSNcOgCLcBGAs/w1200-h630-p-k-no-nu/android-retrofit-2-crud-example.jpg\"").build()
     //   val imagePart:RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image", "https://1.bp.blogspot.com/-lIfFZILZhBg/WyfY50xXXFI/AAAAAAAABEE/fGD29MdIHhIGwcf0tdvl6lz8uRYQSNcOgCLcBGAs/w1200-h630-p-k-no-nu/android-retrofit-2-crud-example.jpg").build()
      //val json:JsonObject = {"description": test,"image": "ggghhhjj"}
       // val desc:RequestBody = RequestBody.create(MediaType.parse("application/json", { "description": khraaaa, "image":https://1.bp}))

        //val imagePart:MultipartBody.Part = MultipartBody.Part.createFormData("image","\"https://1.bp.blogspot.com/-lIfFZILZhBg/WyfY50xXXFI/AAAAAAAABEE/fGD29MdIHhIGwcf0tdvl6lz8uRYQSNcOgCLcBGAs/w1200-h630-p-k-no-nu/android-retrofit-2-crud-example.jpg\"")

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
         val userToken: String? = sharedPreferences.getString("User_token",null);
        //val descriptionPart: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("post_data", "{\"description\": \"hellothreree\", \"image\":\"https://1.bp.blogspot.com/-lIfFZILZhBg/WyfY50xXXFI/AAAAAAAABEE/fGD29MdIHhIGwcf0tdvl6lz8uRYQSNcOgCLcBGAs/w1200-h630-p-k-no-nu/android-retrofit-2-crud-example.jpg\"}").build()

        val postPart:CreatePost =CreatePost(editText_PostDescription.text.toString(),url)
       // val imagePart:File = MultipartBody.Part.createFormData("post_image", "test7878.png",filePart )
            viewModel.createPost(token = "Bearer ${userToken.toString()}",postPart)

        viewModel.createPostResponse.observe(viewLifecycleOwner, {response ->
            if(response.isSuccessful){
                if(response.body()!!.success){
                    Toast.makeText(context, response.body()?.success.toString(), Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "some error occurred" , Toast.LENGTH_SHORT).show()
                }
               // Log.d("Main3", response.body()?.success.toString())
            }else{
                Log.d("Main3", response?.errorBody().toString())

            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_ImageCamera.visibility = View.GONE
        add_ImageGallery.visibility = View.GONE

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)




        add_Floating_Btn.setOnClickListener {
            controlSubFab()
            //capturePhoto()
        }
        add_ImageCamera.setOnClickListener {
            controlSubFab()
            capturePhoto()
        }
        add_ImageGallery.setOnClickListener {
            controlSubFab()

            openGalleryForImage()
        }
        button_uploadPost.setOnClickListener {

           uploadPost(ImageUri!!)
            Log.d("Main4", ImageUri.toString())

//val user = CreatePost(PostData("sthh"))
          //  Log.d("Maa",user.toString())






                /*var filePath:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                var c:Cursor? = requireActivity().contentResolver.query(
                    ImageUri!!, filePath,
                    null, null, null);
                c?.moveToFirst();
                var columnIndex:Int = c!!.getColumnIndex(filePath[0]);
                val FilePathStr = c.getString(columnIndex);
            Log.d("mm", FilePathStr)
                c.close();
*/




            }


    }

    private fun controlSubFab (){
        Log.d("floatingBtn", isFabOpen.toString())
        if(!isFabOpen){

            add_ImageCamera.show()
            add_ImageGallery.show()
            add_ImageCamera.animate().translationY(resources.getDimension(R.dimen.standard_55))

            add_ImageGallery.animate().translationX(-resources.getDimension(R.dimen.standard_55))

            isFabOpen = true
        }else{
            add_ImageCamera.animate().translationY(0F)
            add_ImageGallery.animate().translationX(0F)
            add_ImageCamera.hide()
            add_ImageGallery.hide()
            isFabOpen = false
        }
    }
}