package com.gpfei.graduationproject.kotlin.upload

import androidx.lifecycle.ViewModel
import com.gpfei.graduationproject.kotlin.model.FileBean

class StorageFragmentViewModel : ViewModel() {

    var path: String? = null

    val storageList = ArrayList<FileBean>()
}