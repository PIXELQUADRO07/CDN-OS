package org.cdnos.privacy

import android.Manifest
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
import org.cdnos.privacy.permissions.PermissionsAdapter
import org.cdnos.privacy.permissions.PermissionAppModel

class PermissionsFragment : Fragment() {

    private lateinit var adapter: PermissionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.permissions_recycler_view)
        adapter = PermissionsAdapter(emptyList())
        recyclerView.adapter = adapter

        loadAppsWithPermissions()
    }

    private fun loadAppsWithPermissions() {
        val pm = requireContext().packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        
        val filteredApps = installedApps
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .mapNotNull { appInfo ->
                val hasCamera = pm.checkPermission(Manifest.permission.CAMERA, appInfo.packageName) == PackageManager.PERMISSION_GRANTED
                val hasMic = pm.checkPermission(Manifest.permission.RECORD_AUDIO, appInfo.packageName) == PackageManager.PERMISSION_GRANTED
                
                if (hasCamera || hasMic) {
                    PermissionAppModel(
                        packageName = appInfo.packageName,
                        appName = pm.getApplicationLabel(appInfo).toString(),
                        icon = pm.getApplicationIcon(appInfo),
                        hasCamera = hasCamera,
                        hasMic = hasMic
                    )
                } else null
            }.sortedBy { it.appName }

        adapter.updateData(filteredApps)
    }
}
