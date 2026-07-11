package cdnos.setupwizard

import android.os.Bundle
import android.os.SystemProperties
import android.provider.Settings
import android.util.Log
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

        // Firewall — tramite SystemProperties
        try {
            SystemProperties.set("persist.cdnos.firewall", if (firewall) "1" else "0")
        } catch (e: Exception) {
            Log.w("CDNSetupWizard", "Impossibile scrivere persist.cdnos.firewall", e)
        }

        // DNS over HTTPS — abilita Private DNS in modalità opportunistica
        if (doh) {
            try {
                Settings.Global.putString(cr, Settings.Global.PRIVATE_DNS_MODE, "opportunistic")
            } catch (e: Exception) {
                Log.w("CDNSetupWizard", "Impossibile scrivere private_dns_mode", e)
            }
        }

        // Private DNS — specifica hostname del server DNS privato
        if (privateDns) {
            try {
                Settings.Global.putString(cr, Settings.Global.PRIVATE_DNS_MODE, "hostname")
                Settings.Global.putString(cr, Settings.Global.PRIVATE_DNS_SPECIFIER, "dns.quad9.net")
            } catch (e: Exception) {
                Log.w("CDNSetupWizard", "Impossibile scrivere private_dns_specifier", e)
            }
        }

        // VPN automatica — tramite SystemProperties
        try {
            SystemProperties.set("persist.cdnos.auto_vpn", if (autoVpn) "1" else "0")
        } catch (e: Exception) {
            Log.w("CDNSetupWizard", "Impossibile scrivere persist.cdnos.auto_vpn", e)
        }
    }
}

