package com.lf.fashion.data.response

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


/*
* main post 에서 필요한 정보
* 프로필 이미지 url , 유저 아이디, 포스트 이미지 url 리스트, 좋아요 , 사진모아보기 옆에 표시될 전체 게시물 갯수,
* post scrap 여부 , 팔로잉 여부
*
* 정보 보기 탭 클릭시 받아야할 정보
* 유저 프로필 이미지 url, 유저아이디 , 팔로우여부
* 유저가 추가해둔 스타일 칩 text array , 유저의 키 몸무게 위치 , 소개글
* 등록한 의상리스트(의상 이미지, 의상 카테고리,의상이름,의상 브랜드,의상 상세 정보 )
*
* 정보탭 내부 이의상은 어때? 로 클릭시 받아야할 정보
* 유저 프로필 이미지 url , 유저 아이디 , 팔로우 여부
* 등록된 룩의 좋아요 갯수 , 등록된 룩(의상 이미지, 의상 카테고리,의상이름,의상 브랜드,의상 상세 정보 ) ,의상 코멘트
*
* 마이페이지
* 유저 프로필 이미지 url ,유저 키,몸무게,성별, 소개글, 게시물 갯수, 팔로워 명수, 팔로잉 명수 ,
* post 리스트(공개여부)

* 마이페이지 내부 프로필 수정 클릭시 받아야할 정보
* 유저 아이디, 유저 닉네임 , 이메일
*
* filter 에 chip 도 있어서 url get 파라미터에 chip 도 추가해야할 듯.
*
* 스크랩 바텀 메뉴 - > main post 정보와 동일..
*
* 검색 바텀 메뉴 -> 인기 검색어 string list , 리스트 변동 순위 여부 ??
* 검색 결과 -> Look 결과 : post 리스트 (main post 정보와 동일) Item 결과 : post 리스트(이미지 , 스크랩여부)+ 쇼핑몰명, 제품명, 가격
* 검색결과에 성별,체형,계절 필터를 먹일 수 있기때문에 해당 정보도 필요합니다
*
* */
@Parcelize
data class Post(
    val id : Int,
    val photo :  List<Photo>,  // navigation safeargs 사용을 위해 List<String> 이 아닌 사용자정의 객체 사용!
    val profile : String,
    val userId : String,
    val likes:String
) : Parcelable

@Parcelize
data class Photo(
    val id : String,
    val imageUrl : String
):Parcelable


data class ChipInfo(
    val id: String, //  카테고리 , style tpo season 등등
    val chips : List<ChipContents>
)
data class ChipContents(
    val text:String,
    val emoji : String?
)

data class UserInfo(
    @SerializedName("user_info")
    val modelInfo: ModelInfo,
    @SerializedName("clothes")
    val clothesInfo : List<ClothesInfo>
)

data class ModelInfo(
    val profile: Profile,
    val height : String,
    val weight : String,
    val place : String,
    @SerializedName("style_chips")
    val styleChips : List<ChipContents>,
    val introduce : String
)

// 나중에 RegClothes 로 다 대체할것 ..
data class ClothesInfo(
    val category : String,
    val brand: String,
    val detail : String,
    val image : String,
    val name:String
)

data class RegClothes(
    val image: String? = null,
    val category: String,
    val name: String,
    val price: String,
    val color: String,
    val size: String,
    val brand: String,
    val detail: String? = null
)
data class LookBook(
    val profile: Profile,
    val likes: String,
    val clothes: ClothesInfo
)
data class Profile(
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("user_id")
    val userId : String
)
data class ImageItem(
    var uri: Uri?,
    var isChecked: Boolean,
    var checkCount : String
)