package com.lf.fashion.data.model

import com.google.gson.annotations.SerializedName

data class SearchItemResult (
    val size : Int,
    @SerializedName("search")
    val term : String,
    val clothes : List<Cloth>?
):BindableItem {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as SearchItemResult

        if (size != other.size) return false
        if (term != other.term) return false
        if (clothes != other.clothes) return false

        return true
    }
    override fun hashCode(): Int {
        var result = size
        result = 31 * result + term.hashCode()
        result = 31 * result + (clothes?.hashCode() ?: 0)
        return result
    }
}

data class SearchLookResult(
    val size : Int,
    @SerializedName("search")
    val term : String,
    val posts : List<Posts>?
):BindableItem{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as SearchLookResult

        if (size != other.size) return false
        if (term != other.term) return false
        if (posts != other.posts) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + term.hashCode()
        result = 31 * result + (posts?.hashCode() ?: 0)
        return result
    }
}


interface BindableItem {
    override fun equals(other: Any?): Boolean

}