package cdnos.setupwizard.manager

import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemProperties
import android.util.Log

/**
 * Gestisce l'attivazione o disattivazione delle applicazioni preinstallate nella ROM.
 */
class AppInstaller(private val context: Context) {

    /**
     * Mappa dei nomi visualizzati nel wizard verso i package name reali.
     */
    private val appPackages = mapOf(
        "AuroraStore" to "com.aurora.store",
        "FDroid" to "org.fdroid.fdroid",
        "Magisk" to "com.topjohnwu.magisk",
        "microG" to "com.google.android.gms", // Esempio per microG
        "Termux" to "com.termux"
    )

    fun installApps(aurora: Boolean, fdroid: Boolean, microg: Boolean, termux: Boolean, magisk: Boolean) {
        Log.i("CDNSetupWizard", "Configurazione applicazioni preinstallate...")

        // Gestione tramite abilitazione/disabilitazione dei package preinstallati
        setAppEnabled("AuroraStore", aurora)
        setAppEnabled("FDroid", fdroid)
        setAppEnabled("Magisk", magisk)
        setAppEnabled("microG", microg)
        setAppEnabled("Termux", termux)

        // Manteniamo comunque la system property per compatibilità con altri servizi
        val selectedApps = mutableListOf<String>()
        if (aurora) selectedApps.add("AuroraStore")
        if (fdroid) selectedApps.add("FDroid")
        if (microg) selectedApps.add("microG")
        if (termux) selectedApps.add("Termux")
        if (magisk) selectedApps.add("Magisk")
        
        try {
            SystemProperties.set("persist.cdnos.install_apps", selectedApps.joinToString(","))
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore nel salvataggio persist.cdnos.install_apps", e)
        }
    }

    private fun setAppEnabled(appName: String, enabled: Boolean) {
        val packageName = appPackages[appName] ?: return
        val pm = context.packageManager
        
        try {
            val state = if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
            }
            
            // Nota: Richiede permessi privilegiati (già presenti in CDNSetupWizard)
            pm.setApplicationEnabledSetting(packageName, state, 0)
            Log.d("AppInstaller", "Stato $appName ($packageName) impostato a: $state")
        } catch (e: Exception) {
            Log.w("AppInstaller", "Impossibile cambiare stato per $packageName. Forse non è installata nella ROM?", e)
        }
    }
}
