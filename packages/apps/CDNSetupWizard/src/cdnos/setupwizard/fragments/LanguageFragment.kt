package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.SetupViewModel
import com.android.internal.app.LocalePicker
import java.util.Locale

class LanguageFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_region, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<TextView>(R.id.tv_page_label).text = "02 / 12"
        
        // Nascondi tutto tranne lingua
        view.findViewById<View>(R.id.spinner_region).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_keyboard).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_timezone).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_date_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_time_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_units).visibility = View.GONE
        // Nascondi anche le label (usando i parent se necessario o semplicemente ignorando per brevità in questo esempio)
        
        val spinner = view.findViewById<Spinner>(R.id.spinner_language)
        val systemLocales = LocalePicker.getAllAssetLocales(requireContext(), true)
        val languages = systemLocales.map { it.locale }.sortedBy { it.displayName }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages.map { it.getDisplayName(it).replaceFirstChar { c -> c.uppercase() } }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val currentIdx = languages.indexOfFirst { it.language == viewModel.configuration.locale.language }
        if (currentIdx >= 0) spinner.setSelection(currentIdx)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            viewModel.configuration.locale = languages[spinner.selectedItemPosition]
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
