package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.ChipApi
import com.lf.fashion.data.model.ChipInfo
import javax.inject.Inject

class FilterRepository  @Inject constructor(
    private val chipApi: ChipApi
) : SafeApiCall {
    //filter fragment
    suspend fun getTPOChips(): List<ChipInfo> {
        return chipApi.getTPOChips()
    }

    suspend fun getSeasonChips(): List<ChipInfo> {
        return chipApi.getSeasonChips()
    }

    suspend fun getStyleChips(): List<ChipInfo> {
        return chipApi.getStyleChips()
    }
}