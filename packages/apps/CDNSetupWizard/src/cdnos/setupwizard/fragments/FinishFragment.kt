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

class FinishFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_finish, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivLogo = view.findViewById<ImageView>(R.id.iv_finish_logo)
        val pulseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.finish_pulse)
        ivLogo.startAnimation(pulseAnim)

        val btnFinish = view.findViewById<Button>(R.id.btn_finish)
        btnFinish.alpha = 1f
        btnFinish.setOnClickListener {
            (activity as? SetupWizardActivity)?.completeSetup()
        }
    }
}
