package org.cdnos.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.card_firewall).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_firewall)
        }

        view.findViewById<MaterialCardView>(R.id.card_permissions).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_permissions)
        }

        view.findViewById<MaterialCardView>(R.id.card_network).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_network)
        }

        view.findViewById<MaterialCardView>(R.id.card_selinux).setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_selinux)
        }
    }

    private fun showFeatureNotImplemented(feature: String) {
        Toast.makeText(context, "$feature coming soon", Toast.LENGTH_SHORT).show()
    }
}
