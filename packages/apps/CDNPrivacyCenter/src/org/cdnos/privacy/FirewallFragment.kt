package org.cdnos.privacy

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import org.cdnos.privacy.firewall.FirewallAdapter
import org.cdnos.privacy.firewall.FirewallAppModel
import org.cdnos.privacy.firewall.FirewallController

class FirewallFragment : Fragment() {

    private lateinit var adapter: FirewallAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_firewall, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.apps_recycler_view)
        adapter = FirewallAdapter(emptyList()) { app ->
            // Invia l'aggiornamento al servizio di sistema tramite il controller
            FirewallController.updateRule(requireContext(), app.rule)
        }
        recyclerView.adapter = adapter

        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        val pm = requireContext().packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        
        val firewallApps = installedApps
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map { appInfo ->
                val rule = FirewallController.getRule(requireContext(), appInfo.packageName)
                    ?: com.cdnos.firewall.FirewallRule().apply { 
                        packageName = appInfo.packageName
                        uid = appInfo.uid
                    }
                
                FirewallAppModel(
                    packageName = appInfo.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    icon = pm.getApplicationIcon(appInfo),
                    rule = rule
                )
            }.sortedBy { it.appName }

        adapter.updateData(firewallApps)
    }
}
