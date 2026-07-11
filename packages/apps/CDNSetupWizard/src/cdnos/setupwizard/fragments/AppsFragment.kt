package cdnos.setupwizard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cdnos.setupwizard.R
import cdnos.setupwizard.SetupWizardActivity
import cdnos.setupwizard.model.SetupViewModel

class AppsFragment : Fragment() {

    private val viewModel: SetupViewModel by activityViewModels()

    private val appList = listOf(
        AppEntry("Aurora Store", "Alternative client for the Google Play Store.", R.drawable.ic_cdnos_logo, true),
        AppEntry("F-Droid", "Open source application catalog.", R.drawable.ic_cdnos_logo, true),
        AppEntry("Magisk", "Installs the Magisk Manager app. This does not automatically enable root. To obtain root privileges, a separate procedure will be required after the initial setup.", R.drawable.ic_cdnos_logo, false),
        AppEntry("microG", "Open source core Google services.", R.drawable.ic_cdnos_logo, false),
        AppEntry("Termux", "Terminal emulator and Linux environment.", R.drawable.ic_cdnos_logo, false)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_apps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rv_apps)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = AppsAdapter(appList)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            // Salva le scelte nel ViewModel
            appList.forEach { app ->
                when (app.name) {
                    "Aurora Store" -> viewModel.configuration.installAurora = app.isSelected
                    "F-Droid" -> viewModel.configuration.installFDroid = app.isSelected
                    "Magisk" -> viewModel.configuration.installMagisk = app.isSelected
                    "microG" -> viewModel.configuration.installMicroG = app.isSelected
                    "Termux" -> viewModel.configuration.installTermux = app.isSelected
                }
            }
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    data class AppEntry(val name: String, val desc: String, val icon: Int, var isSelected: Boolean)

    inner class AppsAdapter(val apps: List<AppEntry>) : RecyclerView.Adapter<AppsAdapter.VH>() {
        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name = v.findViewById<TextView>(R.id.tv_app_name)
            val desc = v.findViewById<TextView>(R.id.tv_app_desc)
            val icon = v.findViewById<ImageView>(R.id.iv_app_icon)
            val check = v.findViewById<CheckBox>(R.id.cb_app_selected)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val app = apps[position]
            holder.name.text = app.name
            holder.desc.text = app.desc
            holder.icon.setImageResource(app.icon)
            holder.check.isChecked = app.isSelected
            
            holder.itemView.setOnClickListener {
                app.isSelected = !app.isSelected
                holder.check.isChecked = app.isSelected
            }
            holder.check.setOnCheckedChangeListener { _, isChecked ->
                app.isSelected = isChecked
            }
        }

        override fun getItemCount() = apps.size
    }
}
