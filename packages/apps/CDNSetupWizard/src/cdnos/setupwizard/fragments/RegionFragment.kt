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
import java.util.Locale

class RegionFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_region, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_page_label).text = "03 / 12"
        val spinner = view.findViewById<Spinner>(R.id.spinner_region)
        
        // Nascondi tutto tranne regione
        view.findViewById<View>(R.id.spinner_language).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_keyboard).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_timezone).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_date_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_time_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_units).visibility = View.GONE
        val regions = Locale.getISOCountries()
            .map { code ->
                val locale = Locale("", code)
                RegionEntry(locale.getDisplayCountry(viewModel.configuration.locale), code)
            }
            .sortedBy { it.display }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            regions.map { it.display }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val currentCountry = viewModel.configuration.regionCode
        val idx = regions.indexOfFirst { it.code == currentCountry }
        if (idx >= 0) spinner.setSelection(idx)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            viewModel.configuration.regionCode = regions[spinner.selectedItemPosition].code
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    data class RegionEntry(val display: String, val code: String)
}
