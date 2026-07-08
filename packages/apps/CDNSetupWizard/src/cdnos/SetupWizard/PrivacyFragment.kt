package cdnos.setupwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.fragment.app.Fragment

/**
 * Pagina 4 — Privacy
 *
 * Presenta 3 modalità di privacy:
 * - Standard: telemetria abilitata, esperienza completa
 * - Bilanciata: solo crash report, location opzionale
 * - Massima: nessuna raccolta dati
 *
 * La selezione viene salvata in system property persist.cdnos.privacy_mode
 * e applicata da un servizio di sistema al boot successivo.
 */
class PrivacyFragment : Fragment() {

    private var selectedMode = PrivacyMode.STANDARD

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_privacy, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rbStandard = view.findViewById<RadioButton>(R.id.rb_standard)
        val rbBalanced = view.findViewById<RadioButton>(R.id.rb_balanced)
        val rbMaximum = view.findViewById<RadioButton>(R.id.rb_maximum)

        val cardStandard = view.findViewById<LinearLayout>(R.id.card_standard)
        val cardBalanced = view.findViewById<LinearLayout>(R.id.card_balanced)
        val cardMaximum = view.findViewById<LinearLayout>(R.id.card_maximum)

        // Click su card → seleziona il radio button corrispondente
        cardStandard.setOnClickListener { selectMode(PrivacyMode.STANDARD, rbStandard, rbBalanced, rbMaximum, cardStandard, cardBalanced, cardMaximum) }
        cardBalanced.setOnClickListener { selectMode(PrivacyMode.BALANCED, rbStandard, rbBalanced, rbMaximum, cardStandard, cardBalanced, cardMaximum) }
        cardMaximum.setOnClickListener { selectMode(PrivacyMode.MAXIMUM, rbStandard, rbBalanced, rbMaximum, cardStandard, cardBalanced, cardMaximum) }

        rbStandard.setOnClickListener { selectMode(PrivacyMode.STANDARD, rbStandard, rbBalanced, rbMaximum, cardStandard, cardBalanced, cardMaximum) }
        rbBalanced.setOnClickListener { selectMode(PrivacyMode.BALANCED, rbStandard, rbBalanced, rbMaximum, cardStandard, cardBalanced, cardMaximum) }
        rbMaximum.setOnClickListener { selectMode(PrivacyMode.MAXIMUM, rbStandard, rbBalanced, rbMaximum, cardStandard, cardBalanced, cardMaximum) }

        // Inizializza con Standard selezionato
        updateCardAppearance(selectedMode, cardStandard, cardBalanced, cardMaximum)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            applyPrivacyMode()
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    private fun selectMode(
        mode: PrivacyMode,
        rbStd: RadioButton, rbBal: RadioButton, rbMax: RadioButton,
        cardStd: LinearLayout, cardBal: LinearLayout, cardMax: LinearLayout
    ) {
        selectedMode = mode
        rbStd.isChecked = mode == PrivacyMode.STANDARD
        rbBal.isChecked = mode == PrivacyMode.BALANCED
        rbMax.isChecked = mode == PrivacyMode.MAXIMUM
        updateCardAppearance(mode, cardStd, cardBal, cardMax)
    }

    private fun updateCardAppearance(
        selected: PrivacyMode,
        cardStd: LinearLayout, cardBal: LinearLayout, cardMax: LinearLayout
    ) {
        val selectedBg = resources.getDrawable(R.drawable.bg_card_selected, null)
        val normalBg = resources.getDrawable(R.drawable.bg_card, null)

        cardStd.background = if (selected == PrivacyMode.STANDARD) selectedBg else normalBg
        cardBal.background = if (selected == PrivacyMode.BALANCED) selectedBg else normalBg
        cardMax.background = if (selected == PrivacyMode.MAXIMUM) selectedBg else normalBg
    }

    private fun applyPrivacyMode() {
        val modeValue = selectedMode.name.lowercase()
        try {
            ProcessBuilder("setprop", "persist.cdnos.privacy_mode", modeValue).start()
        } catch (e: Exception) {
            android.util.Log.w("CDNSetupWizard", "Impossibile scrivere persist.cdnos.privacy_mode", e)
        }
    }

    enum class PrivacyMode { STANDARD, BALANCED, MAXIMUM }
}
