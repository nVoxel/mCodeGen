package com.voxeldev.mcodegen.v1.utils

import com.voxeldev.mcodegen.v1.GlobalConstants
import java.io.File

object GlobalTelegramApiUtils {

    fun getAndroidTelegramApiFile(): File {
        return File(GlobalConstants.ANDROID_SOURCE_PATH)
    }

    fun getDesktopTelegramApiFile(): File {
        return File(GlobalConstants.DESKTOP_SOURCE_PATH)
    }
}
