package org.cdnos.privacy.firewall

import android.content.Context
import android.util.Log
import com.cdnos.firewall.FirewallManager
import com.cdnos.firewall.FirewallRule

object FirewallController {
    private const val TAG = "FirewallController"
    private var firewallManager: FirewallManager? = null

    private fun getManager(context: Context): FirewallManager? {
        if (firewallManager == null) {
            firewallManager = context.getSystemService("cdnos_firewall") as? FirewallManager
        }
        return firewallManager
    }

    fun getRule(context: Context, packageName: String): FirewallRule? {
        return try {
            getManager(context)?.getRuleForPackage(packageName)
        } catch (e: Exception) {
            Log.e(TAG, "Errore getRule", e)
            null
        }
    }

    fun updateRule(context: Context, rule: FirewallRule) {
        try {
            getManager(context)?.setRuleForPackage(rule.packageName, rule)
        } catch (e: Exception) {
            Log.e(TAG, "Errore updateRule", e)
        }
    }
}
