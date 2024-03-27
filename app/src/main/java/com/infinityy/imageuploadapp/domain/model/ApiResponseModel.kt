package com.infinityy.imageuploadapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ApiResponseModel(
    val image: String
) : Parcelable