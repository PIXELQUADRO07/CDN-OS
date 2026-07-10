package org.cdnos.privacy.firewall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

class FirewallBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("FirewallBoot", "Ripristino regole firewall al boot...")
            val prefs = context.getSharedPreferences("firewall_prefs", Context.MODE_PRIVATE)
            val pm = context.packageManager
            
            // Recuperiamo tutte le app installate
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            
            apps.forEach { app ->
                // Se l'app era bloccata nelle preferenze, riapplichiamo la regola tramite il servizio
                if (prefs.getBoolean(app.packageName, false)) {
                    FirewallController.setAppBlocked(context, app.packageName, app.uid, true)
                }
            }
        }
    }
}
