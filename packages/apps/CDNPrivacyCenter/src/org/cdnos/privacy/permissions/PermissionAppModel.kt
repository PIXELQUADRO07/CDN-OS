package org.cdnos.privacy.permissions

import android.graphics.drawable.Drawable

data class PermissionAppModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val hasCamera: Boolean,
    val hasMic: Boolean
)
