package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity

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

        val logoAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_appear)
        logo.startAnimation(logoAnim)

        val textAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.text_appear)
        tvTitle.startAnimation(textAnim)
        tvSubtitle.startAnimation(textAnim)

        btnStart.alpha = 0f
        btnStart.animate()
            .alpha(1f)
            .translationYBy(-20f)
            .setStartDelay(1000)
            .setDuration(600)
            .start()

        btnStart.setOnClickListener {
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
