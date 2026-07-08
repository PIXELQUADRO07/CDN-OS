package cdnos.setupwizard

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment as SupportFragment

/**
 * Pagina 3 — Rete
 *
 * Gestisce tre tab:
 * - Wi-Fi: scansione reti, connessione con password
 * - Dati mobili: toggle abilitazione
 * - Ethernet: rilevamento disponibilità
 */
class NetworkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_network, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_network)
        val viewPager = view.findViewById<ViewPager2>(R.id.vp_network)

        // Adapter per le 3 tab
        val pagerAdapter = NetworkPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false // swipe disabilitato

        // Sincronizza tab e ViewPager
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.network_wifi)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.network_mobile)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.network_ethernet)))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            (activity as? SetupWizardActivity)?.nextPage()
        }
        view.findViewById<Button>(R.id.btn_skip).setOnClickListener {
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    // ── Adapter ViewPager ────────────────────────────────────────────────────

    inner class NetworkPagerAdapter(fragment: Fragment) :
        androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {

        override fun getItemCount() = 3

        override fun createFragment(position: Int): SupportFragment = when (position) {
            0 -> WifiTabFragment()
            1 -> MobileDataTabFragment()
            else -> EthernetTabFragment()
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Tab Wi-Fi
// ════════════════════════════════════════════════════════════════════════════

class WifiTabFragment : Fragment() {

    private lateinit var wifiManager: WifiManager
    private lateinit var adapter: WifiNetworkAdapter
    private val scanResults = mutableListOf<ScanResult>()

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                updateScanResults()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.tab_wifi, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifiManager = requireContext().applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager

        val rv = view.findViewById<RecyclerView>(R.id.rv_wifi_networks)
        adapter = WifiNetworkAdapter(scanResults) { result ->
            showPasswordDialog(result)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // Abilita Wi-Fi se non attivo
        if (!wifiManager.isWifiEnabled) {
            @Suppress("DEPRECATION")
            wifiManager.isWifiEnabled = true
        }

        startScan(view)
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(
            scanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
    }

    override fun onPause() {
        super.onPause()
        try {
            requireContext().unregisterReceiver(scanReceiver)
        } catch (e: Exception) {
            // Già rimosso
        }
    }

    private fun startScan(view: View) {
        val tvStatus = view.findViewById<TextView>(R.id.tv_wifi_status)
        tvStatus.text = getString(R.string.network_scanning)

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            @Suppress("DEPRECATION")
            wifiManager.startScan()
        } else {
            // Richiedi permesso location (necessario per Wi-Fi scan su API 29+)
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun updateScanResults() {
        val view = view ?: return
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val results = wifiManager.scanResults
            .filter { it.SSID.isNotEmpty() }
            .distinctBy { it.SSID }
            .sortedByDescending { it.level }

        scanResults.clear()
        scanResults.addAll(results)
        adapter.notifyDataSetChanged()

        val tvStatus = view.findViewById<TextView>(R.id.tv_wifi_status)
        tvStatus.text = if (results.isEmpty()) {
            getString(R.string.network_no_networks)
        } else {
            "${results.size} reti trovate"
        }
    }

    private fun showPasswordDialog(result: ScanResult) {
        val ctx = requireContext()
        val dialogView = LayoutInflater.from(ctx).inflate(
            android.R.layout.simple_list_item_1, null
        )

        // Dialog semplice con campo password
        val passwordInput = EditText(ctx).apply {
            hint = getString(R.string.network_password_hint)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(48, 32, 48, 32)
            setTextColor(resources.getColor(R.color.text_primary, null))
            setHintTextColor(resources.getColor(R.color.text_hint, null))
            background = resources.getDrawable(R.drawable.bg_card, null)
        }

        AlertDialog.Builder(ctx, R.style.Theme_CDNSetupWizard_Dialog)
            .setTitle(getString(R.string.network_password_title, result.SSID))
            .setView(passwordInput)
            .setPositiveButton(getString(R.string.btn_connect)) { _, _ ->
                connectToWifi(result, passwordInput.text.toString())
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun connectToWifi(scanResult: ScanResult, password: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        // API 29+: usa WifiNetworkSpecifier per la connessione
        try {
            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid(scanResult.SSID)
                .setWpa2Passphrase(password)
                .build()

            val request = android.net.NetworkRequest.Builder()
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build()

            val connectivityManager = requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

            connectivityManager.requestNetwork(
                request,
                object : android.net.ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: android.net.Network) {
                        super.onAvailable(network)
                        // Connesso con successo
                        activity?.runOnUiThread {
                            android.widget.Toast.makeText(
                                requireContext(),
                                "${getString(R.string.network_connected)}: ${scanResult.SSID}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("CDNSetupWizard", "Errore connessione Wi-Fi", e)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Adapter RecyclerView Wi-Fi
// ════════════════════════════════════════════════════════════════════════════

class WifiNetworkAdapter(
    private val networks: List<ScanResult>,
    private val onNetworkClick: (ScanResult) -> Unit
) : RecyclerView.Adapter<WifiNetworkAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSsid: TextView = view.findViewById(R.id.tv_ssid)
        val tvSignal: TextView = view.findViewById(R.id.tv_signal_strength)
        val tvSecurity: TextView = view.findViewById(R.id.tv_security)
        val ivSignal: android.widget.ImageView = view.findViewById(R.id.iv_signal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wifi_network, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = networks[position]
        holder.tvSsid.text = result.SSID
        holder.tvSignal.text = "Segnale: ${WifiManager.calculateSignalLevel(result.level, 5)}/5"

        // Sicurezza
        val isSecured = result.capabilities.contains("WPA") ||
                result.capabilities.contains("WEP") ||
                result.capabilities.contains("PSK")
        holder.tvSecurity.text = if (isSecured) "🔒" else "🔓"

        holder.itemView.setOnClickListener {
            onNetworkClick(result)
        }
    }

    override fun getItemCount() = networks.size
}

// ════════════════════════════════════════════════════════════════════════════
// Tab Dati Mobili
// ════════════════════════════════════════════════════════════════════════════

class MobileDataTabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.tab_mobile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tm = requireContext().getSystemService(Context.TELEPHONY_SERVICE)
            as android.telephony.TelephonyManager

        val hasSim = tm.simState == android.telephony.TelephonyManager.SIM_STATE_READY

        val tvStatus = view.findViewById<TextView>(R.id.tv_mobile_status)
        val tvDesc = view.findViewById<TextView>(R.id.tv_mobile_desc)
        val layoutToggle = view.findViewById<android.widget.LinearLayout>(R.id.layout_mobile_toggle)
        val swMobile = view.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.sw_mobile_data)

        if (hasSim) {
            tvStatus.text = getString(R.string.network_mobile_enabled)
            tvDesc.text = "SIM rilevata. Puoi abilitare o disabilitare i dati mobili."
            layoutToggle.visibility = View.VISIBLE

            // Toggle dati mobili tramite ConnectivityManager
            swMobile.setOnCheckedChangeListener { _, isChecked ->
                try {
                    val method = tm.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.java)
                    method.isAccessible = true
                    method.invoke(tm, isChecked)
                } catch (e: Exception) {
                    android.util.Log.w("CDNSetupWizard", "Impossibile cambiare stato dati mobili", e)
                }
            }
        } else {
            tvStatus.text = getString(R.string.network_mobile_unavailable)
            tvDesc.text = "Nessuna SIM rilevata. Inserisci una SIM per usare i dati mobili."
            layoutToggle.visibility = View.GONE
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Tab Ethernet
// ════════════════════════════════════════════════════════════════════════════

class EthernetTabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.tab_ethernet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)
            as android.net.ConnectivityManager

        val hasEthernet = cm.allNetworks.any { network ->
            val caps = cm.getNetworkCapabilities(network)
            caps?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) == true
        }

        val tvStatus = view.findViewById<TextView>(R.id.tv_ethernet_status)
        tvStatus.text = if (hasEthernet) {
            getString(R.string.network_ethernet_available)
        } else {
            getString(R.string.network_ethernet_unavailable)
        }

        tvStatus.setTextColor(
            resources.getColor(
                if (hasEthernet) R.color.accent_secondary else R.color.text_secondary,
                null
            )
        )
    }
}
