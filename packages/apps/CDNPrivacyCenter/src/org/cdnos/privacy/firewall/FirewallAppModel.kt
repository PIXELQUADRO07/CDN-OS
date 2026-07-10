package org.cdnos.privacy.firewall

import android.graphics.drawable.Drawable
import com.cdnos.firewall.FirewallRule

data class FirewallAppModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val rule: FirewallRule
)
