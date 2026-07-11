package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.DnsMode
import cdnos.setupwizard.model.FirewallMode
import cdnos.setupwizard.model.SetupViewModel

class PrivacyFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_privacy, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rbBalanced = view.findViewById<RadioButton>(R.id.rb_balanced)
        val rbStrict = view.findViewById<RadioButton>(R.id.rb_maximum)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            viewModel.configuration.firewallMode = if (rbStrict.isChecked) FirewallMode.STRICT else FirewallMode.BALANCED
            viewModel.configuration.dnsMode = DnsMode.QUAD9 // Esempio statico per ora
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
