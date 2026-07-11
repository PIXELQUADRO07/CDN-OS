package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.SetupViewModel

class SummaryFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_summary, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val config = viewModel.configuration
        val summaryText = StringBuilder().apply {
            append("• ${getString(R.string.summary_language)}: ${config.locale.getDisplayName(config.locale)}\n")
            append("• ${getString(R.string.summary_region)}: ${config.regionCode}\n")
            append("• ${getString(R.string.summary_wifi)}: ${config.wifiSsid ?: getString(R.string.summary_none)}\n")
            append("• ${getString(R.string.summary_theme)}: ${config.themeColor.replaceFirstChar { it.uppercase() }}\n")
            append("• ${getString(R.string.summary_firewall)}: ${config.firewallMode.name}\n")
            append("• ${getString(R.string.summary_dns)}: ${config.dnsMode.name}\n")
            append("• ${getString(R.string.summary_apps)}: ")
            val apps = mutableListOf<String>()
            if (config.installAurora) apps.add("Aurora")
            if (config.installFDroid) apps.add("F-Droid")
            if (config.installMagisk) apps.add("Magisk")
            if (config.installMicroG) apps.add("microG")
            if (config.installTermux) apps.add("Termux")
            append(if (apps.isEmpty()) getString(R.string.summary_none) else apps.joinToString(", "))
        }.toString()
        
        view.findViewById<TextView>(R.id.tv_title).text = getString(R.string.summary_title)
        view.findViewById<TextView>(R.id.tv_summary).text = summaryText
        
        view.findViewById<Button>(R.id.btn_confirm).apply {
            text = getString(R.string.summary_confirm)
            setOnClickListener {
                (activity as? SetupWizardActivity)?.nextPage()
            }
        }
    }
}
