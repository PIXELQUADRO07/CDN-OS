package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.SetupViewModel
import java.util.TimeZone

class DateTimeFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_region, container, false) // Placeholder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_page_label).text = "06 / 12"
        
        // Nascondi tutto tranne fuso orario e formato ora
        view.findViewById<View>(R.id.spinner_language).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_region).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_keyboard).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_date_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_units).visibility = View.GONE
        val timezones = TimeZone.getAvailableIDs().sorted()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            timezones
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        
        val currentTz = viewModel.configuration.timezoneId
        val idx = timezones.indexOf(currentTz)
        if (idx >= 0) spinner.setSelection(idx)

        val rb24h = view.findViewById<RadioButton>(R.id.rb_24h)
        rb24h?.isChecked = viewModel.configuration.use24HourFormat

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            viewModel.configuration.timezoneId = timezones[spinner.selectedItemPosition]
            viewModel.configuration.use24HourFormat = rb24h?.isChecked ?: true
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
