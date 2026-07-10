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

    /**
     * Abilita o disabilita l'accesso alla rete per un determinato UID.
     * Utilizza il servizio di sistema CDNOS Firewall.
     */
    fun setAppBlocked(context: Context, packageName: String, uid: Int, blocked: Boolean) {
        val rule = getRule(context, packageName) ?: com.cdnos.firewall.FirewallRule().apply {
            this.packageName = packageName
            this.uid = uid
        }
        rule.wifiAllowed = !blocked
        rule.mobileAllowed = !blocked
        updateRule(context, rule)
    }
}
