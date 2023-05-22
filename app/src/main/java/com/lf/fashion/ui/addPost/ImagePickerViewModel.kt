package com.lf.fashion.ui.addPost

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

private const val INDEX_MEDIA_ID = MediaStore.MediaColumns._ID
private const val INDEX_MEDIA_URI = MediaStore.MediaColumns.DATA
private const val INDEX_ALBUM_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
private const val INDEX_DATE_ADDED = MediaStore.MediaColumns.DATE_ADDED

/**
 * 최종 선택된 이미지를 참조할 때는 checkedItemList 를 참조하는 것이 아니라 getCheckedImageUriList 로 참조할것 !
 *
 */
class ImagePickerViewModel(context: Context): ViewModel() {
    val imageItemList = MutableLiveData<MutableList<ImageItem>>(mutableListOf())
    val checkedItemList = MutableLiveData<MutableList<ImageItem>>()

    init {
    fetchImageItemList(context)
    }

    @SuppressLint("Range")
     private fun fetchImageItemList(context: Context) {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            INDEX_MEDIA_ID,
            INDEX_MEDIA_URI,
            INDEX_ALBUM_NAME,
            INDEX_DATE_ADDED
        )
        val selection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.SIZE + " > 0"
            else null
        val sortOrder = "$INDEX_DATE_ADDED DESC"
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        cursor?.let {
            while(cursor.moveToNext()) {
                val mediaPath = cursor.getString(cursor.getColumnIndex(INDEX_MEDIA_URI))
                imageItemList.value!!.add(
                    ImageItem(Uri.fromFile(File(mediaPath)), false)
                )
            }
        }

        cursor?.close()
    }

    fun getCheckedImageUriList(): MutableList<String> {
        val checkedImageUriList = mutableListOf<String>()
        imageItemList.value?.let {
            for(imageItem in imageItemList.value!!) {
                if(imageItem.isChecked) checkedImageUriList.add(imageItem.uri.toString())
            }
        }
        return checkedImageUriList
    }

    fun cancelCheck(uncheckedItem: ImageItem) {
        //이미지 리스트에서 checked false 로 변경
        val tempList = imageItemList.value
        tempList?.let {
            for (imageItem in it) {
                if (imageItem.uri == uncheckedItem.uri) {
                    imageItem.isChecked = false
                }
            }
            imageItemList.value = it
        }

        //체크리스트에서 삭제
        val checkTemp = checkedItemList.value?.toMutableList()
        checkTemp?.removeIf { checked -> checked.uri == uncheckedItem.uri }
        checkTemp?.let {
            checkedItemList.value = it
        }
    }

    // 이미지를 기존 리스트에 추가하는 메서드
    fun addImageToImageList(newItem: ImageItem) {
        val currentList = imageItemList.value
        currentList?.let {
            it.add(newItem)
            imageItemList.value = it
        }
    }

    fun addCheckedItem(item: ImageItem){
        if(checkedItemList.value==null){
            val temporal = mutableListOf(item)
            checkedItemList.value = temporal
        }else {
            val temporal = checkedItemList.value
            temporal!!.add(item)
            checkedItemList.value = temporal!!
        }
    }

}
class ImagePickerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImagePickerViewModel::class.java)) {
            return ImagePickerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
