package cdnos.setupwizard.manager

import android.content.Context
import android.os.SystemProperties
import android.util.Log
import cdnos.setupwizard.model.FirewallMode

/**
 * Gestisce la configurazione del firewall di sistema.
 */
class FirewallManager(private val context: Context) {

    fun applyFirewallMode(mode: FirewallMode) {
        val modeValue = mode.name.lowercase()
        Log.i("CDNSetupWizard", "Applicazione modalità firewall: $modeValue")
        try {
            SystemProperties.set("persist.cdnos.firewall.mode", modeValue)
            // Se la modalità è STRICT, potremmo abilitare regole più restrittive immediatamente
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore nell'applicazione del firewall", e)
        }
    }
}
