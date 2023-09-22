package com.lf.fashion.ui.globalFrag.editPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lf.fashion.data.model.ChipInfo
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.PostInfo
import com.lf.fashion.data.model.UploadPost
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.repository.EditPostRepository
import com.lf.fashion.data.repository.FilterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val editPostRepository: EditPostRepository,
    private val filterRepository: FilterRepository
) : ViewModel() {
    private var _postInfo = MutableLiveData<Resource<PostInfo>>()
    val postInfo: LiveData<Resource<PostInfo>> = _postInfo

    var postId: Int? = null
    var imageList = MutableLiveData<MutableList<ImageUrl>>()
    var newImageList = mutableListOf<ImageUrl>()
    var newClothImageList = mutableListOf<Cloth>()
    private val _tpoChipList = MutableLiveData<List<ChipInfo>>()
    var tpoChipList: LiveData<List<ChipInfo>> = _tpoChipList

    private val _seasonChipList = MutableLiveData<List<ChipInfo>>()
    var seasonChipList: LiveData<List<ChipInfo>> = _seasonChipList

    private val _styleChipList = MutableLiveData<List<ChipInfo>>()
    var styleChipList: LiveData<List<ChipInfo>> = _styleChipList

    var selectedTpos: MutableList<ChipInfo> = mutableListOf()
    var selectedSeasons : MutableList<ChipInfo> = mutableListOf()
    var selectedStyles: MutableList<ChipInfo> = mutableListOf()

    var selectedGender :String?=null
    var selectedClothCategory : String? =null
  //  var selectedPostImages : MutableList<String > = mutableListOf()
    var uploadedClothes : MutableList<Cloth> = mutableListOf()


    var savedHeight : Int?=null
    var savedWeight : Int?=null
    var savedIntroduce : String?=null
    var savedClothList = mutableListOf<Cloth>()
    init {
        getTPOChipsInfo()
        getSeasonChipsInfo()
        getStyleChipsInfo()
    }
    suspend fun editPost(uploadPost: UploadPost): MsgResponse {
        val response = editPostRepository.editPost(uploadPost)
        return if (response is Resource.Success) response.value
        else MsgResponse(false, "Resource Fail")
    }

    suspend fun uploadNewPostImage(images: List<String>): MsgResponse {
        val response = editPostRepository.uploadNewPostImages(images)
        return if (response is Resource.Success) response.value
        else MsgResponse(false, "Resource Fail")
    }

    suspend fun uploadNewClothImage(images: List<String>): MsgResponse {
        val response = editPostRepository.uploadClothesImages(images)
        return if (response is Resource.Success) response.value
        else MsgResponse(false, "Resource Fail")
    }

    fun removeImage(image: ImageUrl) {
        val currentList = imageList.value ?: mutableListOf()
        currentList.remove(image)
        imageList.value = currentList // LiveData 값을 업데이트하여 관찰자를 트리거
    }

    fun addToImageList(image: List<ImageUrl>) {
        val currentList = imageList.value ?: mutableListOf()
        currentList.addAll(image)
        imageList.value = currentList // LiveData 값을 업데이트하여 관찰자를 트리거
    }

    fun getPostInfoByPostId(postId: Int) {
        viewModelScope.launch {
            _postInfo.value = editPostRepository.getPostInfo(postId)
        }
    }
     fun getTPOChipsInfo(){
        viewModelScope.launch {
            _tpoChipList.value = filterRepository.getTPOChips()
        }
    }
     fun getSeasonChipsInfo(){
        viewModelScope.launch {
            _seasonChipList.value = filterRepository.getSeasonChips()
        }
    }
     fun getStyleChipsInfo(){
        viewModelScope.launch {
            _styleChipList.value = filterRepository.getStyleChips()
        }
    }
}