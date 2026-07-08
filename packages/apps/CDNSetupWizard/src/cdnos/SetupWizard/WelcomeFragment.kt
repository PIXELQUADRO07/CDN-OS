package cdnos.setupwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Pagina 1 — Welcome
 *
 * Mostra il logo animato di CDNOS, il titolo e il sottotitolo "Built for freedom.",
 * le versioni di sistema, e il bottone "Inizia" che porta alla pagina 2.
 */
class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_welcome, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logo = view.findViewById<ImageView>(R.id.iv_logo)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvSubtitle = view.findViewById<TextView>(R.id.tv_subtitle)
        val btnStart = view.findViewById<Button>(R.id.btn_start)

        // Animazione logo: scale + fade con overshoot
        val logoAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_appear)
        logo.startAnimation(logoAnim)

        // Animazione testo: slide up + fade, con delay
        val textAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.text_appear)
        tvTitle.startAnimation(textAnim)
        tvSubtitle.startAnimation(textAnim)

        // Anima anche il bottone con un delay ulteriore
        btnStart.alpha = 0f
        btnStart.animate()
            .alpha(1f)
            .translationYBy(-20f)
            .setStartDelay(1200)
            .setDuration(600)
            .start()

        btnStart.setOnClickListener {
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
