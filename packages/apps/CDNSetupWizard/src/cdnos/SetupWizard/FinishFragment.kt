package cdnos.setupwizard

import android.os.Bundle
import android.os.SystemProperties
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Pagina 9 — Fine
 *
 * Schermata finale del wizard:
 * - Logo animato con pulsazione continua
 * - Testo "Il tuo dispositivo è pronto. Benvenuto in CDNOS."
 * - Bottone "Inizia" che completa il setup
 *
 * Al click su "Inizia":
 * 1. Mostra stato installazione app (se selezionate)
 * 2. Chiama SetupWizardActivity.completeSetup()
 *    che scrive USER_SETUP_COMPLETE e lancia il launcher
 */
class FinishFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_finish, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivLogo = view.findViewById<ImageView>(R.id.iv_finish_logo)
        val tvTitle = view.findViewById<TextView>(R.id.tv_finish_title)
        val tvSubtitle = view.findViewById<TextView>(R.id.tv_finish_subtitle)
        val btnFinish = view.findViewById<Button>(R.id.btn_finish)
        val tvInstalling = view.findViewById<TextView>(R.id.tv_installing)

        // Animazione logo pulsante
        val pulseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.finish_pulse)
        ivLogo.startAnimation(pulseAnim)

        // Anima testo in sequenza
        tvTitle.alpha = 0f
        tvSubtitle.alpha = 0f
        btnFinish.alpha = 0f

        tvTitle.animate().alpha(1f).setStartDelay(300).setDuration(700).start()
        tvSubtitle.animate().alpha(1f).setStartDelay(700).setDuration(700).start()
        btnFinish.animate().alpha(1f).setStartDelay(1200).setDuration(600).start()

        btnFinish.setOnClickListener {
            btnFinish.isEnabled = false
            btnFinish.text = "…"

            // Mostra stato installazione se ci sono app da installare
            val appsToInstall = getAppsToInstall()
            if (appsToInstall.isNotEmpty()) {
                tvInstalling.visibility = View.VISIBLE
                tvInstalling.text = getString(R.string.finish_installing)
            }

            // Completa il setup con un breve delay per mostrare il feedback
            view.postDelayed({
                (activity as? SetupWizardActivity)?.completeSetup()
            }, if (appsToInstall.isNotEmpty()) 1500L else 300L)
        }
    }

    /**
     * Legge la lista delle app selezionate dalla system property tramite SystemProperties.
     */
    private fun getAppsToInstall(): List<String> {
        val result = SystemProperties.get("persist.cdnos.install_apps", "")
        return if (result.isBlank()) emptyList()
        else result.split(",").filter { it.isNotBlank() }
    }
}
