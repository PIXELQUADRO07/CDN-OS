package cdnos.setupwizard

import android.os.Bundle
import android.os.SystemProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Pagina 6 — Applicazioni consigliate
 *
 * Mostra una lista di app selezionabili con checkbox.
 * Le app selezionate vengono salvate in una system property e installate
 * al termine del wizard da FinishFragment.
 *
 * App proposte:
 * - F-Droid (✓ default)
 * - Aurora Store (✓ default)
 * - Magisk (✗, disabilitato — richiede root/modifiche boot)
 * - Neo Store (✗ default)
 * - Termux (✗ default)
 */
class AppsFragment : Fragment() {

    private val appList = listOf(
        AppEntry(
            name = "F-Droid",
            description = "Il market open source per Android. Migliaia di app libere e gratuite.",
            iconRes = android.R.drawable.ic_menu_search,
            isSelected = true,
            isEnabled = true,
            warning = null
        ),
        AppEntry(
            name = "Aurora Store",
            description = "Client alternativo per il Google Play Store. Scarica app senza un account Google.",
            iconRes = android.R.drawable.ic_dialog_map,
            isSelected = true,
            isEnabled = true,
            warning = null
        ),
        AppEntry(
            name = "Magisk",
            description = "Framework di root avanzato. Richiede modifiche all'immagine di boot.",
            iconRes = android.R.drawable.ic_lock_lock,
            isSelected = false,
            isEnabled = false, // Disabilitato — non si può installare come normale app
            warning = "Richiede root — configurabile dopo il setup"
        ),
        AppEntry(
            name = "Neo Store",
            description = "Client moderno per F-Droid con interfaccia Material You.",
            iconRes = android.R.drawable.ic_menu_view,
            isSelected = false,
            isEnabled = true,
            warning = null
        ),
        AppEntry(
            name = "Termux",
            description = "Terminale Linux completo per Android. Strumento potente per sviluppatori.",
            iconRes = android.R.drawable.ic_menu_agenda,
            isSelected = false,
            isEnabled = true,
            warning = null
        )
    )

    private lateinit var adapter: AppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_apps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rv_apps)
        adapter = AppListAdapter(appList)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            saveSelectedApps()
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    private fun saveSelectedApps() {
        val selected = appList
            .filter { it.isEnabled && it.isSelected }
            .joinToString(",") { it.name }

        try {
            SystemProperties.set("persist.cdnos.install_apps", selected)
            Log.i("CDNSetupWizard", "Lista app da installare salvata: $selected")
        } catch (e: Exception) {
            Log.w("CDNSetupWizard", "Impossibile salvare la lista app", e)
        }
    }

    // ── Data class ──────────────────────────────────────────────────────────

    data class AppEntry(
        val name: String,
        val description: String,
        val iconRes: Int,
        var isSelected: Boolean,
        val isEnabled: Boolean,
        val warning: String?
    )

    // ── Adapter ─────────────────────────────────────────────────────────────

    inner class AppListAdapter(private val apps: List<AppEntry>) :
        RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivIcon: ImageView = view.findViewById(R.id.iv_app_icon)
            val tvName: TextView = view.findViewById(R.id.tv_app_name)
            val tvDesc: TextView = view.findViewById(R.id.tv_app_desc)
            val tvWarning: TextView = view.findViewById(R.id.tv_app_warning)
            val cbSelected: CheckBox = view.findViewById(R.id.cb_app_selected)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_app, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val app = apps[position]

            holder.ivIcon.setImageResource(app.iconRes)
            holder.tvName.text = app.name
            holder.tvDesc.text = app.description

            if (app.warning != null) {
                holder.tvWarning.visibility = View.VISIBLE
                holder.tvWarning.text = app.warning
            } else {
                holder.tvWarning.visibility = View.GONE
            }

            holder.cbSelected.isChecked = app.isSelected
            holder.cbSelected.isEnabled = app.isEnabled
            holder.itemView.alpha = if (app.isEnabled) 1f else 0.5f

            // Toggle selezione al click sulla riga
            holder.itemView.setOnClickListener {
                if (app.isEnabled) {
                    app.isSelected = !app.isSelected
                    holder.cbSelected.isChecked = app.isSelected
                }
            }

            holder.cbSelected.setOnCheckedChangeListener { _, isChecked ->
                if (app.isEnabled) app.isSelected = isChecked
            }
        }

        override fun getItemCount() = apps.size
    }
}
