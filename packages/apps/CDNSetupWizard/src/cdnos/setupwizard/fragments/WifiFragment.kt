package cdnos.setupwizard.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.SetupViewModel

class WifiFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()
    private lateinit var wifiManager: WifiManager
    private val scanResults = mutableListOf<ScanResult>()
    private lateinit var adapter: WifiAdapter

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateScanResults()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.tab_wifi, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        
        val rv = view.findViewById<RecyclerView>(R.id.rv_wifi_networks)
        adapter = WifiAdapter(scanResults) { result ->
            showPasswordDialog(result)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        view.findViewById<Button>(R.id.btn_next)?.setOnClickListener {
            (activity as? SetupWizardActivity)?.nextPage()
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager.startScan()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    private fun updateScanResults() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        val results = wifiManager.scanResults.filter { it.SSID.isNotEmpty() }.distinctBy { it.SSID }
        scanResults.clear()
        scanResults.addAll(results)
        adapter.notifyDataSetChanged()
    }

    private fun showPasswordDialog(result: ScanResult) {
        val passwordInput = EditText(requireContext())
        AlertDialog.Builder(requireContext(), R.style.Theme_CDNSetupWizard_Dialog)
            .setTitle("Connetti a ${result.SSID}")
            .setView(passwordInput)
            .setPositiveButton("Salva") { _, _ ->
                viewModel.configuration.wifiSsid = result.SSID
                viewModel.configuration.wifiPassword = passwordInput.text.toString()
                (activity as? SetupWizardActivity)?.nextPage()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    inner class WifiAdapter(val list: List<ScanResult>, val onClick: (ScanResult) -> Unit) : RecyclerView.Adapter<WifiAdapter.VH>() {
        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val txt = v.findViewById<TextView>(R.id.tv_ssid)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_wifi_network, parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.txt.text = list[position].SSID
            holder.itemView.setOnClickListener { onClick(list[position]) }
        }
        override fun getItemCount() = list.size
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(scanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(scanReceiver)
    }
}
