package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.manager.ConfigurationApplier
import cdnos.setupwizard.model.SetupViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplyingFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_applying, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<LinearProgressIndicator>(R.id.applying_progress)
        val statusText = view.findViewById<TextView>(R.id.tv_status)
        val titleText = view.findViewById<TextView>(R.id.tv_title)
        
        titleText.text = getString(R.string.applying_title)
        statusText.text = getString(R.string.applying_status_init)

        lifecycleScope.launch {
            val applier = ConfigurationApplier(requireContext())
            
            withContext(Dispatchers.IO) {
                applier.apply(viewModel.configuration, object : ConfigurationApplier.ProgressListener {
                    override fun onProgress(step: Int, totalSteps: Int, message: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            progressBar.max = totalSteps
                            progressBar.setProgressCompat(step, true)
                            statusText.text = message
                        }
                    }
                })
            }
            
            delay(1500)
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
