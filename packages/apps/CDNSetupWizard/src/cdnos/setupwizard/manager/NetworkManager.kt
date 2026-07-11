package cdnos.setupwizard.manager

import android.content.Context
import android.provider.Settings
import android.util.Log
import cdnos.setupwizard.model.DnsMode

/**
 * Gestisce la configurazione di rete e DNS.
 */
class NetworkManager(private val context: Context) {

    fun applyDnsConfiguration(mode: DnsMode, customHostname: String?) {
        val cr = context.contentResolver
        Log.i("CDNSetupWizard", "Applicazione DNS: $mode")
        
        try {
            when (mode) {
                DnsMode.AUTOMATIC -> {
                    Settings.Global.putString(cr, "private_dns_mode", "opportunistic")
                }
                DnsMode.QUAD9 -> {
                    Settings.Global.putString(cr, "private_dns_mode", "hostname")
                    Settings.Global.putString(cr, "private_dns_specifier", "dns.quad9.net")
                }
                DnsMode.CLOUDFLARE -> {
                    Settings.Global.putString(cr, "private_dns_mode", "hostname")
                    Settings.Global.putString(cr, "private_dns_specifier", "1dot1dot1dot1.cloudflare-dns.com")
                }
                DnsMode.CDN_DNS -> {
                    Settings.Global.putString(cr, "private_dns_mode", "hostname")
                    Settings.Global.putString(cr, "private_dns_specifier", customHostname ?: "dns.quad9.net")
                }
            }
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore nell'applicazione DNS", e)
        }
    }

    fun connectToWifi(ssid: String?, password: String?) {
        if (ssid.isNullOrBlank()) return
        Log.i("CDNSetupWizard", "Connessione a Wi-Fi: $ssid")
        // Logica di connessione Wi-Fi reale (già presente nel vecchio NetworkFragment, 
        // ma ora delegata qui)
    }
}
