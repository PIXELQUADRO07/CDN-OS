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
import cdnos.setupwizard.model.SetupViewModel

class ThemeFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_privacy, container, false) // Placeholder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usiamo i radio button esistenti in fragment_privacy come placeholder per i temi
        val rbBlue = view.findViewById<RadioButton>(R.id.rb_standard)
        val rbRed = view.findViewById<RadioButton>(R.id.rb_balanced)
        val rbGreen = view.findViewById<RadioButton>(R.id.rb_maximum)
        
        rbBlue.text = "Blue (Default)"
        rbRed.text = "Red"
        rbGreen.text = "Green"

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            viewModel.configuration.themeColor = when {
                rbRed.isChecked -> "red"
                rbGreen.isChecked -> "green"
                else -> "blue"
            }
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
