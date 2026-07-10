package org.cdnos.privacy

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import java.net.NetworkInterface

class NetworkFragment : Fragment() {

    private lateinit var dnsStatus: TextView
    private lateinit var dnsServer: TextView
    private lateinit var vpnStatus: TextView
    private lateinit var interfacesList: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        dnsStatus = view.findViewById(R.id.dns_status)
        dnsServer = view.findViewById(R.id.dns_server)
        vpnStatus = view.findViewById(R.id.vpn_status)
        interfacesList = view.findViewById(R.id.interfaces_list)

        refreshNetworkStatus()
    }

    private fun refreshNetworkStatus() {
        updateDnsStatus()
        updateVpnStatus()
        updateInterfaces()
    }

    private fun updateDnsStatus() {
        val resolver = requireContext().contentResolver
        val mode = Settings.Global.getString(resolver, "private_dns_mode") ?: "off"
        val specifier = Settings.Global.getString(resolver, "private_dns_specifier") ?: ""

        when (mode) {
            "hostname" -> {
                dnsStatus.text = "DNS Privato Attivo (Script/Hostname)"
                dnsServer.text = specifier
                dnsStatus.setTextColor(resources.getColor(android.R.color.holo_green_light, null))
            }
            "opportunistic" -> {
                dnsStatus.text = "DNS Automatico (Opportunistico)"
                dnsServer.text = "Crittografia se disponibile"
            }
            else -> {
                dnsStatus.text = "DNS Non Protetto (Standard)"
                dnsServer.text = "Traffico DNS in chiaro"
                dnsStatus.setTextColor(resources.getColor(R.color.accent_red, null))
            }
        }
    }

    private fun updateVpnStatus() {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(activeNetwork)
        
        val isVpn = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
        
        if (isVpn) {
            vpnStatus.text = "VPN Attiva"
            vpnStatus.setTextColor(resources.getColor(android.R.color.holo_green_light, null))
        } else {
            vpnStatus.text = "VPN Disconnessa"
            vpnStatus.setTextColor(resources.getColor(R.color.secondaryTextColor, null))
        }
    }

    private fun updateInterfaces() {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces().toList()
            val activeIfaces = interfaces.filter { it.isUp && !it.isLoopback }
            
            val text = StringBuilder()
            activeIfaces.forEach { iface ->
                text.append("${iface.name}: ${iface.hardwareAddress?.joinToString(":") { "%02x".format(it) } ?: "no-mac"}\n")
            }
            interfacesList.text = if (text.isEmpty()) "Nessuna interfaccia attiva" else text.toString()
        } catch (e: Exception) {
            interfacesList.text = "Errore lettura interfacce"
        }
    }
}
