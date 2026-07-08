package cdnos.setupwizard

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Pagina 7 — Sicurezza
 *
 * Permette di abilitare/disabilitare:
 * - Firewall: imposta system property persist.cdnos.firewall
 * - DNS over HTTPS: scrive Settings.Global.PRIVATE_DNS_MODE
 * - Private DNS: scrive Settings.Global.PRIVATE_DNS_SPECIFIER
 * - VPN automatica: imposta system property persist.cdnos.auto_vpn
 *
 * Il firewall reale viene configurato da un servizio di sistema
 * che legge le properties all'avvio.
 */
class SecurityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_security, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swFirewall = view.findViewById<SwitchMaterial>(R.id.sw_firewall)
        val swDoh = view.findViewById<SwitchMaterial>(R.id.sw_doh)
        val swPrivateDns = view.findViewById<SwitchMaterial>(R.id.sw_private_dns)
        val swVpn = view.findViewById<SwitchMaterial>(R.id.sw_vpn)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            applySecuritySettings(
                firewall = swFirewall.isChecked,
                doh = swDoh.isChecked,
                privateDns = swPrivateDns.isChecked,
                autoVpn = swVpn.isChecked
            )
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    private fun applySecuritySettings(
        firewall: Boolean,
        doh: Boolean,
        privateDns: Boolean,
        autoVpn: Boolean
    ) {
        val cr = requireContext().contentResolver

        // Firewall — system property letta dal servizio di sistema
        setProp("persist.cdnos.firewall", if (firewall) "1" else "0")

        // DNS over HTTPS — abilita Private DNS in modalità opportunistica
        if (doh) {
            try {
                Settings.Global.putString(cr, "private_dns_mode", "opportunistic")
            } catch (e: Exception) {
                android.util.Log.w("CDNSetupWizard", "Impossibile scrivere private_dns_mode", e)
            }
        }

        // Private DNS — specifica hostname del server DNS privato
        if (privateDns) {
            try {
                Settings.Global.putString(cr, "private_dns_mode", "hostname")
                Settings.Global.putString(cr, "private_dns_specifier", "dns.quad9.net")
            } catch (e: Exception) {
                android.util.Log.w("CDNSetupWizard", "Impossibile scrivere private_dns_specifier", e)
            }
        }

        // VPN automatica
        setProp("persist.cdnos.auto_vpn", if (autoVpn) "1" else "0")
    }

    private fun setProp(key: String, value: String) {
        try {
            ProcessBuilder("setprop", key, value).start()
        } catch (e: Exception) {
            android.util.Log.w("CDNSetupWizard", "Impossibile scrivere $key=$value", e)
        }
    }
}
