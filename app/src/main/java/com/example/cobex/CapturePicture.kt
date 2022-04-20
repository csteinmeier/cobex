package com.example.cobex

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobex.Extensions.toImage
import com.example.cobex.databinding.FragmentCapturePictureBinding
import com.example.cobex.timelineview.TimelineObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class CapturePicture : Fragment(), PermissionHelper.IRequirePermission, CompositionArtifact.IArtifact {

    private val binding get() = _binding!!
    private var _binding: FragmentCapturePictureBinding? = null
    private lateinit var pictureListManager: PictureListManager

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

        binding.buttonCapture.setOnClickListener {
            hasPermission { takePicture() }
        }

        pictureListManager = PictureListManager(
            context = requireContext(),
            recycler = binding.recyclerPictures,
            numberOfColumns = 3,
            lifecycleOwner = viewLifecycleOwner,
            listOfSavedImages = getSavedImagesPath(),
            buttonCapturePicture = binding.buttonCapture
        )
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, PermissionHelper.CAMARA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        helperOnRequestPermissionResult(
            requestCode = requestCode,
            permission = permissions,
            grantResults = grantResults,
            hasPermission = { takePicture() },
            null
        )
    }

    private fun getSavedImagesPath(): List<String> {
        val mutableSet = mutableSetOf<String>()
        mutableSet.apply {
            getStringSet(requireContext(), CapturePicture::class.java)?.let { addAll(it) }
            getStringSet(requireContext(), TimelineObject.ImageItem::class.java)?.let { addAll(it) }
            getStringSet(requireContext(), TimelineObject.BigImageItem::class.java)?.let { addAll(it) }
        }
        Log.e("dasdasd", mutableSet.toString())
        return mutableSet.toList()
    }

    /**
     *
     *  Function which received the taken picture
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PermissionHelper.CAMARA_REQUEST_CODE) {
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
        listOfSavedImages: List<String>? = null,
        buttonCapturePicture: Button,
    ) : CompositionArtifact.IArtifact {
        private var pictureListAdapter: PictureListAdapter? = null

        init {
            //Layout of the recyclerView
            val gridLayoutManager = GridLayoutManager(context, numberOfColumns)

            //Instance of the Adapter
            pictureListAdapter =
                PictureListAdapter(lifecycleOwner, context, listOfSavedImages, buttonCapturePicture)

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

        fun getAmountOfTakenPictures(): Int? = pictureListAdapter?.getAmountTakenPictures()


        fun isPictureTaken(): Boolean = pictureListAdapter?.getCapturedPictures()?.size != 0


        /** Adapter Class of a Recyclerview
         *  uses the @recyclerview_picture_list_items layout
         *
         *  @param lifecycleOwner to observe LiveData
         *  @param list old List to recreate
         *  */
        private class PictureListAdapter(
            val lifecycleOwner: LifecycleOwner,
            val context: Context,
            val list: List<String>? = null,
            val captureButton: Button
        ) :
            RecyclerView.Adapter<PictureListAdapter.ImageHolder>() {
            private val listHolder = mutableListOf<ImageHolder>()

            /**Adds a new Bitmap Picture to the List*/
            fun addNewImage(bitmap: Bitmap) {
                listHolder.find { holder ->
                    !holder.isBitmapPicture()
                }
                    ?.addImage(bitmap)
            }

            /**Adds a new Bitmap Picture to the List*/
            fun addNewImage(path: String) {
                listHolder.find { holder ->
                    !holder.isBitmapPicture()
                }
                    ?.addImage(path)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
                val inflater = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_picture_list_items, parent, false)
                val imageHolder = ImageHolder(inflater, context, captureButton)
                listHolder.add(imageHolder)
                return imageHolder
            }

            override fun onBindViewHolder(holder: ImageHolder, position: Int) {
                holder.image.setImageResource(R.drawable.ic_lense_24)
                holder.picture.observe(lifecycleOwner, holder)
                //Add the saved Pictures
                if (list != null && list.size - 1 >= position) {
                    addNewImage(list[position])
                }
            }

            override fun getItemCount(): Int = 9

            fun getAmountTakenPictures(): Int = listHolder.filter { it.isBitmapPicture() }.size

            /**
             *
             * @return all Bitmaps that carried in the Items (ImageHolder)
             * */
            fun getCapturedPictures() =
                listHolder
                    .filter { it.isBitmapPicture() }
                    .map { it.getBitmapPicture() }

            fun getFileToCapturedPictures() =
                listHolder
                    .filter { it.isBitmapPicture() }
                    .map { it.getBitmapFile() }


            /**Holder Class with LiveData, will change the Image if an taken picture is set*/
            class ImageHolder(item: View, val context: Context, val captureButton: Button)
                : RecyclerView.ViewHolder(item), Observer<ImageHolder.CapturedPicture>,
                CompositionArtifact.IArtifact {

                private val _pictures = MutableLiveData<CapturedPicture>()
                val picture: LiveData<CapturedPicture> = _pictures

                val image: ImageView = itemView.findViewById(R.id.picture)

                init {
                    image.setOnClickListener { }
                }


                /**Sets the value of the LiveData to the picture*/
                fun addImage(newPicture: Bitmap) {
                    val file = saveImageInternal(newPicture)
                    _pictures.value = CapturedPicture(
                        bitmap = newPicture,
                        file = file
                    )
                    synchroniseArtifact(
                        context,
                        file.absolutePath,
                        CapturePicture::class.java,
                        true
                    )
                    setButtonEnable()
                }

                fun addImage(filePath: String) {
                    val file = File(filePath)
                    _pictures.value = CapturedPicture(
                        bitmap = filePath.toImage(context),
                        file = file
                    )
                    setButtonEnable()
                }


                private fun saveImageInternal(bitmap: Bitmap): File {
                    val artifact = CompositionArtifact.getInstance(context)
                    val timeStamp = artifact.getTimeStamp()
                    val file = File(artifact.getImageFileDir(), "$timeStamp.jpg")
                    try {
                        val stream: OutputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        stream.flush()
                        stream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    Log.i("Uri Path", Uri.parse(file.absolutePath).toString())
                    return file
                }

                /**Removes the taken picture*/
                fun removeImage() {
                    val filePath = picture.value?.file?.absolutePath
                    _pictures.value?.file?.delete()
                    _pictures.value = CapturedPicture(placeholder = R.drawable.ic_lense_24)
                    synchroniseArtifact(context, filePath!!, CapturePicture::class.java, false)
                    setButtonEnable()
                }

                fun setButtonEnable() {
                    captureButton.isEnabled = getCounter(context, CapturePicture::class.java) < 9
                }

                fun isBitmapPicture() = picture.value?.bitmap != null

                fun getBitmapPicture() = picture.value?.bitmap

                fun getBitmapFile() = picture.value?.file


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
                    val file: File? = null,
                    val placeholder: Int? = null
                )


                fun alertDeletePicture(holder: ImageHolder): AlertDialog {
                    return AlertDialog.Builder(context)
                        .setTitle("Delete Picture")
                        .setMessage("Do you want to remove this picture? ")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok) { _, _ -> holder.removeImage() }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun mainPermission() = Manifest.permission.CAMERA

    override fun fragment() = this

    override fun fragmentCode() = PermissionHelper.FRAGMENT_CAPTURE_PICTURE_CODE

    override fun requiredPermissions() = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

}