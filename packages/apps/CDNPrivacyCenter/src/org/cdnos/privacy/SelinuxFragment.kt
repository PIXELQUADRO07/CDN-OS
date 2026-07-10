package org.cdnos.privacy

import android.os.Bundle
import android.os.SELinux
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar

class SelinuxFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selinux, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        updateSelinuxStatus(view)
    }

    private fun updateSelinuxStatus(view: View) {
        val statusText = view.findViewById<TextView>(R.id.selinux_status_text)
        val description = view.findViewById<TextView>(R.id.selinux_description)
        val icon = view.findViewById<ImageView>(R.id.selinux_icon)
        val versionText = view.findViewById<TextView>(R.id.selinux_version)

        val isEnabled = SELinux.isSELinuxEnabled()
        val isEnforced = SELinux.isSELinuxEnforced()

        if (!isEnabled) {
            statusText.text = "DISABLED"
            statusText.setTextColor(resources.getColor(R.color.accent_red, null))
            description.text = "Attenzione: SELinux è disabilitato. Il sistema è vulnerabile."
            icon.setColorFilter(resources.getColor(R.color.accent_red, null))
        } else if (isEnforced) {
            statusText.text = "ENFORCING"
            statusText.setTextColor(resources.getColor(android.R.color.holo_green_light, null))
            description.text = "Il sistema è protetto attivamente dalle policy di sicurezza."
            icon.setColorFilter(resources.getColor(android.R.color.holo_green_light, null))
        } else {
            statusText.text = "PERMISSIVE"
            statusText.setTextColor(resources.getColor(android.R.color.holo_orange_light, null))
            description.text = "Modalità debug: le violazioni vengono loggate ma non bloccate."
            icon.setColorFilter(resources.getColor(android.R.color.holo_orange_light, null))
        }

        // Tentativo di leggere la versione (richiede accesso a /sys o props)
        versionText.text = "Versione Policy: AOSP Baklava Standard"
    }
}
