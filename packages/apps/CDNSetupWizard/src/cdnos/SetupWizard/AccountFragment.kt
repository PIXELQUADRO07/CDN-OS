package cdnos.setupwizard

import android.os.Bundle
import android.os.SystemProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.fragment.app.Fragment

/**
 * Pagina 8 — Account CDNOS
 *
 * L'utente può scegliere se creare un account CDNOS o continuare senza.
 * La scelta viene salvata tramite SystemProperties in persist.cdnos.account_setup.
 */
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rbYes = view.findViewById<RadioButton>(R.id.rb_account_yes)
        val rbNo = view.findViewById<RadioButton>(R.id.rb_account_no)
        val cardYes = view.findViewById<LinearLayout>(R.id.card_account_yes)
        val cardNo = view.findViewById<LinearLayout>(R.id.card_account_no)

        fun selectAccount(wantsAccount: Boolean) {
            rbYes.isChecked = wantsAccount
            rbNo.isChecked = !wantsAccount

            val selected = resources.getDrawable(R.drawable.bg_card_selected, null)
            val normal = resources.getDrawable(R.drawable.bg_card, null)
            cardYes.background = if (wantsAccount) selected else normal
            cardNo.background = if (!wantsAccount) selected else normal
        }

        cardYes.setOnClickListener { selectAccount(true) }
        cardNo.setOnClickListener { selectAccount(false) }
        rbYes.setOnClickListener { selectAccount(true) }
        rbNo.setOnClickListener { selectAccount(false) }

        // Default: No account
        selectAccount(false)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            val wantsAccount = rbYes.isChecked
            saveAccountPreference(wantsAccount)
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    private fun saveAccountPreference(wantsAccount: Boolean) {
        try {
            SystemProperties.set(
                "persist.cdnos.account_setup",
                if (wantsAccount) "1" else "0"
            )
            Log.i("CDNSetupWizard", "Account preference salvata: $wantsAccount")
        } catch (e: Exception) {
            Log.w("CDNSetupWizard", "Impossibile salvare account preference", e)
        }
    }
}

