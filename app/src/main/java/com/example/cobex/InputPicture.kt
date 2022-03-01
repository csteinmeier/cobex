package com.example.cobex

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.databinding.FragmentCapturePictureBinding
import java.io.*

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class CapturePicture : Fragment(), CompositionArtifact.IArtifact {

    private val binding get() = _binding!!
    private var _binding: FragmentCapturePictureBinding? = null
    private lateinit var pictureListManager: PictureListManager

    companion object {
        private const val CAMARA_PERMISSION_CODE = 1
        private const val CAMARA_REQUEST_CODE = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCapturePictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            buttonCapture.setOnClickListener {
                if(checkIfPermissionIsGranted()) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMARA_REQUEST_CODE)
                }
            }
        }

        if (isInstanceOfSavedPreferencesAvailable(
                requireActivity(),
                CompositionArtifact.PreferenceKeywords.PICTURE_INIT
            )
        ) {
            //Has to be reset so it will not double the amount
            CompositionArtifact.capturedPicture = 0

            //Manage the Adapter of recyclerView
            pictureListManager = PictureListManager(
                requireContext(), binding.recyclerPictures, 3, viewLifecycleOwner,
                recreateListOfOldImages(), binding.buttonCapture
            )
        } else {
            pictureListManager = PictureListManager(
                requireContext(), binding.recyclerPictures, 3, viewLifecycleOwner,
                null, buttonCapturePicture = binding.buttonCapture
            )
        }
    }


    private fun recreateListOfOldImages(): MutableList<Bitmap> {
        val imgList = mutableListOf<Bitmap>()
        // load all saved uri in string style
        val setOfPictureUris = CompositionArtifact.getPreferences(requireActivity()).getStringSet(
            CompositionArtifact.PreferenceKeywords.TAKEN_PICTURES.name, setOf()
        )
        setOfPictureUris?.forEach {
            //convert string to image
            val uri = File(it).toUri()
            val source = ImageDecoder.createSource(this.requireActivity().contentResolver, uri)
            val img = ImageDecoder.decodeBitmap(source)
            imgList.add(img)
        }
        return imgList
    }


    /**
     * Checks if Permission is Granted, if then a picture is recorded
     */
    private fun checkIfPermissionIsGranted(): Boolean {
        return if (permissionIsNotGranted(Manifest.permission.CAMERA) ||
            permissionIsNotGranted(Manifest.permission.READ_EXTERNAL_STORAGE) ||
            permissionIsNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), CAMARA_PERMISSION_CODE
            )
            false
        }else{
            true
        }
    }


    private fun permissionIsNotGranted(string: String): Boolean {
        return checkSelfPermission(string) != PackageManager.PERMISSION_GRANTED
    }

    private fun checkSelfPermission(string: String) =
        ActivityCompat.checkSelfPermission(requireContext(), string)


    /**
     *
     * After permission request
     *
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Camara Picture?
        if (requestCode == CAMARA_REQUEST_CODE) {
            //Permissions granted?
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMARA_REQUEST_CODE)
            }
        } else {
            //If permission is not granted... maybe more option with "showRational.." or something like that
            Toast.makeText(
                this.requireContext(),
                "Oops you just denied a required permission " +
                        "Please change it in the settings", Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     *
     *  Function which received the taken picture
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMARA_REQUEST_CODE) {
                val uri = data?.extras?.get("data")
                pictureListManager.addNewPicture(uri as Bitmap)
            }
        }
    }


    /**
     * Manage the RecyclerView and its pictures
     * */
    private class PictureListManager(
        context: Context,
        recycler: RecyclerView,
        numberOfColumns: Int,
        lifecycleOwner: LifecycleOwner,
        list: List<Bitmap>? = null,
        buttonCapturePicture: Button,
    ) {
        private var pictureListAdapter: PictureListAdapter? = null

        init {
            //Layout of the recyclerView
            val gridLayoutManager = GridLayoutManager(context, numberOfColumns)

            //Instance of the Adapter
            pictureListAdapter = PictureListAdapter(lifecycleOwner, context, list, buttonCapturePicture)

            //setup recycler
            recycler.apply {
                layoutManager = gridLayoutManager
                adapter = pictureListAdapter
            }
        }

        /**Forwards the picture to the list*/
        fun addNewPicture(bitmap: Bitmap) {
            Log.i("Bitmap added", bitmap.toString())
            pictureListAdapter?.addNewImage(bitmap)
        }

        fun getAmountOfTakenPictures(): Int? {
            return pictureListAdapter?.getAmountTakenPictures()
        }

        fun getTakenPictures(): MutableList<Bitmap>? {
            return pictureListAdapter?.getTakenPictures()
        }

        fun isPictureTaken(): Boolean {
            return pictureListAdapter?.getTakenPictures()?.size != 0
        }

        /** Adapter Class of a Recyclerview
         *  uses the @recyclerview_picture_list_items layout
         *
         *  @param lifecycleOwner to observe LiveData
         *  @param list old List to recreate
         *  */
        private class PictureListAdapter(
            val lifecycleOwner: LifecycleOwner,
            val context: Context,
            val list: List<Bitmap>? = null,
            val capturePicture: Button
        ) :
            RecyclerView.Adapter<PictureListAdapter.ImageHolder>() {
            private val listHolder = mutableListOf<ImageHolder>()

            /**Adds a new Bitmap Picture to the List*/
            fun addNewImage(bitmap: Bitmap) {
                listHolder.find { !it.isBitmapPicture() }?.addImage(bitmap)
                CompositionArtifact.capturedPicture += 1
                capturePicture.isEnabled = CompositionArtifact.capturedPicture <= itemCount
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
                val inflater = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_picture_list_items, parent, false)
                val imageHolder = ImageHolder(inflater, context)
                listHolder.add(imageHolder)
                return imageHolder
            }

            override fun onBindViewHolder(holder: ImageHolder, position: Int) {
                holder.image.setImageResource(R.drawable.ic_lense_24)
                holder.picture.observe(lifecycleOwner, holder)
                //Add the saved Pictures
                if(list != null && list.size -1 >= position){
                    addNewImage(list[position])
                }
            }

            override fun getItemCount(): Int = 9

            fun getAmountTakenPictures(): Int {
                return listHolder.filter { it.isBitmapPicture() }.size
            }

            /**
             *
             * @return all Bitmaps that carried in the Items (ImageHolder)
             * */
            fun getTakenPictures(): MutableList<Bitmap> {
                val list = listHolder.filter { it.isBitmapPicture() }
                val pictures = mutableListOf<Bitmap>()
                list.forEach { imageHolder ->
                    imageHolder.getBitmapPicture()?.let { bitmap -> pictures.add(bitmap) }
                }
                return pictures
            }

            /**Holder Class with LiveData, will change the Image if an taken picture is set*/
            class ImageHolder(item: View, val context: Context) : RecyclerView.ViewHolder(item),
                Observer<ImageHolder.CapturedPicture> {

                private val _pictures = MutableLiveData<CapturedPicture>()
                val picture: LiveData<CapturedPicture> = _pictures

                val image: ImageView = itemView.findViewById(R.id.picture)

                init {
                    image.setOnClickListener { }
                }

                /**Sets the value of the LiveData to the picture*/
                fun addImage(newPicture: Bitmap) {
                    _pictures.value = CapturedPicture(bitmap = newPicture)
                    image.isClickable = true
                }

                /**Removes the taken picture*/
                fun removeImage() {
                    _pictures.value = CapturedPicture(placeholder = R.drawable.ic_lense_24)
                    CompositionArtifact.capturedPicture -= 1
                }

                fun isBitmapPicture(): Boolean {
                    return picture.value?.bitmap != null
                }

                fun getBitmapPicture(): Bitmap? {
                    return _pictures.value?.bitmap
                }

                override fun onChanged(changedPicture: CapturedPicture?) {
                    if (changedPicture?.bitmap != null) {
                        image.setImageBitmap(changedPicture.bitmap)
                        image.setOnClickListener { alertDeletePicture(this) }
                    }
                    if (changedPicture?.placeholder != null) {
                        image.setImageResource(changedPicture.placeholder)
                        image.setOnClickListener { }
                    }
                }

                private data class CapturedPicture(
                    val bitmap: Bitmap? = null,
                    val placeholder: Int? = null
                )


                fun alertDeletePicture(holder: ImageHolder): AlertDialog {
                    return AlertDialog.Builder(context)
                        .setTitle("Delete Picture")
                        .setMessage("Do you want to remove this picture? ")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes) { _, _ -> holder.removeImage() }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (pictureListManager.isPictureTaken()) {

            //Delete all old picture so it will not duplicate each time we destroy the view
            deleteRecursive(getFileDir()!!)

            //A Boolean to signal the button "Continue" in SecondFragment to be visible and
            createInstanceOfSavedPreferences(requireActivity())
            //a boolean to signal that there is a stored instance for this fragment
            createInstanceOfSavedPreferences(requireActivity(), CompositionArtifact.PreferenceKeywords.PICTURE_INIT)

            //Save Amount
            CompositionArtifact.getPreferenceEditor(requireActivity()).apply {
                pictureListManager.getAmountOfTakenPictures()
                    ?.let { putInt(CompositionArtifact.PreferenceKeywords.PICTURE_AMOUNT.name, it) }

                // Save Pictures
                putStringSet(
                    CompositionArtifact.PreferenceKeywords.TAKEN_PICTURES.name,
                    getPictureUriSetToSave()
                ).apply()
            }
        }
        _binding = null
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                deleteRecursive(child)
        fileOrDirectory.delete()
    }

    private fun getPictureUriSetToSave(): MutableSet<String> {
        val pictureSet = mutableSetOf<String>()
        pictureListManager.getTakenPictures()?.forEach {
            pictureSet.add(saveImageInternal(it).toString())
        }

        Log.e("Images", pictureSet.toString())
        return pictureSet
    }

    private fun getFileDir(): File? {
        return ContextWrapper(activity).getDir("images", Context.MODE_PRIVATE)
    }

    private fun saveImageInternal(bitmap: Bitmap): Uri? {
        val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val file = File(getFileDir(), "$timeStamp.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.i("Uri Path", Uri.parse(file.absolutePath).toString())
        return Uri.parse(file.absolutePath)
    }

}