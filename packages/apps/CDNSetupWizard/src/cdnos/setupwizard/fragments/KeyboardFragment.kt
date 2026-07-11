package cdnos.setupwizard.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.SetupViewModel

class KeyboardFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_region, container, false) // Placeholder layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_page_label).text = "04 / 12"
        
        // Nascondi tutto tranne tastiera
        view.findViewById<View>(R.id.spinner_language).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_region).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_timezone).visibility = View.GONE
        view.findViewById<View>(R.id.spinner_date_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_time_format).visibility = View.GONE
        view.findViewById<View>(R.id.rg_units).visibility = View.GONE
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val imeList = imm.inputMethodList

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            imeList.map { it.loadLabel(requireContext().packageManager).toString() }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            if (imeList.isNotEmpty()) {
                viewModel.configuration.keyboardImeId = imeList[spinner.selectedItemPosition].id
            }
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }
}
