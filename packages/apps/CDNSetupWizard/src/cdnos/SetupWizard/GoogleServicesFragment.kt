package cdnos.setupwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Pagina 5 — Servizi Google
 *
 * L'utente sceglie tra:
 * - Nessun servizio Google (default, privacy massima)
 * - microG (sostituto open source dei Google Play Services)
 * - Google Play Services completo
 *
 * La scelta viene salvata in persist.cdnos.google_services.
 * L'effettiva installazione/abilitazione dei componenti viene
 * gestita da un service privilegiato separato che legge questa property.
 */
class GoogleServicesFragment : Fragment() {

    private var selectedOption = GoogleOption.NONE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_google_services, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rbNone = view.findViewById<RadioButton>(R.id.rb_no_google)
        val rbMicrog = view.findViewById<RadioButton>(R.id.rb_microg)
        val rbGapps = view.findViewById<RadioButton>(R.id.rb_gapps)

        val cardNone = view.findViewById<LinearLayout>(R.id.card_no_google)
        val cardMicrog = view.findViewById<LinearLayout>(R.id.card_microg)
        val cardGapps = view.findViewById<LinearLayout>(R.id.card_gapps)

        val tvWarning = view.findViewById<TextView>(R.id.tv_gapps_warning)

        // Listener card
        fun selectOption(opt: GoogleOption) {
            selectedOption = opt
            rbNone.isChecked = opt == GoogleOption.NONE
            rbMicrog.isChecked = opt == GoogleOption.MICROG
            rbGapps.isChecked = opt == GoogleOption.GAPPS

            val selected = resources.getDrawable(R.drawable.bg_card_selected, null)
            val normal = resources.getDrawable(R.drawable.bg_card, null)
            cardNone.background = if (opt == GoogleOption.NONE) selected else normal
            cardMicrog.background = if (opt == GoogleOption.MICROG) selected else normal
            cardGapps.background = if (opt == GoogleOption.GAPPS) selected else normal

            // Warning riavvio solo per GApps
            tvWarning.visibility = if (opt == GoogleOption.GAPPS) View.VISIBLE else View.GONE
        }

        cardNone.setOnClickListener { selectOption(GoogleOption.NONE) }
        cardMicrog.setOnClickListener { selectOption(GoogleOption.MICROG) }
        cardGapps.setOnClickListener { selectOption(GoogleOption.GAPPS) }
        rbNone.setOnClickListener { selectOption(GoogleOption.NONE) }
        rbMicrog.setOnClickListener { selectOption(GoogleOption.MICROG) }
        rbGapps.setOnClickListener { selectOption(GoogleOption.GAPPS) }

        // Init: NONE selezionato di default
        selectOption(GoogleOption.NONE)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            applyGoogleServices()
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    private fun applyGoogleServices() {
        val value = selectedOption.name.lowercase()
        try {
            ProcessBuilder("setprop", "persist.cdnos.google_services", value).start()
        } catch (e: Exception) {
            android.util.Log.w("CDNSetupWizard", "Impossibile scrivere persist.cdnos.google_services", e)
        }
    }

    enum class GoogleOption { NONE, MICROG, GAPPS }
}
