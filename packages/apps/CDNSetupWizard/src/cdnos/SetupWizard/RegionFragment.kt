package cdnos.setupwizard

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.IActivityManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import android.os.SystemProperties
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.android.internal.app.LocalePicker
import java.util.Locale
import java.util.TimeZone

/**
 * Pagina 2 — Regione e Lingua
 *
 * Configura le preferenze locali del sistema usando le API Android native:
 * - Lingua → aggiorna la Configuration di sistema tramite ActivityManager
 * - Regione → codice paese
 * - Tastiera → InputMethodManager (selezione IME)
 * - Fuso orario → AlarmManager.setTimeZone()
 * - Formato data → spinner con formati comuni
 * - Formato ora → Settings.System.TIME_12_24
 * - Unità di misura → system property persist.sys.units
 */
class RegionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_region, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLanguageSpinner(view)
        setupRegionSpinner(view)
        setupKeyboardSpinner(view)
        setupTimezoneSpinner(view)
        setupDateFormatSpinner(view)

        view.findViewById<Button>(R.id.btn_next).setOnClickListener {
            applySettings(view)
            (activity as? SetupWizardActivity)?.nextPage()
        }
    }

    // ── Lingua ──────────────────────────────────────────────────────────────

    private fun setupLanguageSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinner_language)

        // Ottieni tutte le lingue supportate dal sistema in modo dinamico
        val systemLocales = LocalePicker.getAllAssetLocales(requireContext(), true)
        val languages = systemLocales.map { info ->
            LanguageEntry(info.label, info.locale)
        }.sortedBy { it.display }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages.map { it.display }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Pre-seleziona la lingua corrente del sistema
        val currentLocale = Locale.getDefault()
        val idx = languages.indexOfFirst { it.locale.language == currentLocale.language }
        if (idx >= 0) spinner.setSelection(idx)

        // Salviamo gli oggetti Locale per usarli in applySettings
        spinner.tag = languages
    }

    // ── Regione ─────────────────────────────────────────────────────────────

    private fun setupRegionSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinner_region)
        val regions = Locale.getISOCountries()
            .map { code ->
                val locale = Locale("", code)
                RegionEntry(locale.displayCountry, code)
            }
            .sortedBy { it.display }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            regions.map { it.display }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Pre-seleziona il paese corrente
        val currentCountry = Locale.getDefault().country
        val idx = regions.indexOfFirst { it.code == currentCountry }
        if (idx >= 0) spinner.setSelection(idx)

        spinner.tag = regions
    }

    // ── Tastiera ─────────────────────────────────────────────────────────────

    private fun setupKeyboardSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinner_keyboard)

        // Ottieni la lista degli IME installati
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
            as android.view.inputmethod.InputMethodManager
        val imeList = imm.inputMethodList

        val imeNames = if (imeList.isNotEmpty()) {
            imeList.map { it.loadLabel(requireContext().packageManager).toString() }
        } else {
            listOf("Tastiera di sistema (default)")
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            imeNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.tag = imeList
    }

    // ── Fuso orario ──────────────────────────────────────────────────────────

    private fun setupTimezoneSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinner_timezone)

        val timezones = TimeZone.getAvailableIDs()
            .map { id ->
                val tz = TimeZone.getTimeZone(id)
                val offsetMs = tz.rawOffset
                val hours = offsetMs / 3600000
                val mins = Math.abs(offsetMs % 3600000 / 60000)
                val sign = if (hours >= 0) "+" else "-"
                TimezoneEntry(
                    display = "UTC$sign${Math.abs(hours)}:${mins.toString().padStart(2, '0')} – $id",
                    id = id
                )
            }
            .sortedBy { it.id }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            timezones.map { it.display }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Pre-seleziona il timezone corrente
        val currentTz = TimeZone.getDefault().id
        val idx = timezones.indexOfFirst { it.id == currentTz }
        if (idx >= 0) spinner.setSelection(idx)

        spinner.tag = timezones
    }

    // ── Formato data ─────────────────────────────────────────────────────────

    private fun setupDateFormatSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinner_date_format)
        val formats = listOf("dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd.MM.yyyy", "dd-MM-yyyy")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            formats
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // ── Applicazione impostazioni ────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    private fun applySettings(view: View) {
        val ctx = requireContext()

        // Applica lingua sistema
        val langSpinner = view.findViewById<Spinner>(R.id.spinner_language)
        val languages = langSpinner.tag as? List<LanguageEntry>
        languages?.getOrNull(langSpinner.selectedItemPosition)?.let { entry ->
            updateSystemLocale(entry.locale)
        }

        // Applica fuso orario
        val tzSpinner = view.findViewById<Spinner>(R.id.spinner_timezone)
        val timezones = tzSpinner.tag as? List<TimezoneEntry>
        timezones?.getOrNull(tzSpinner.selectedItemPosition)?.let { entry ->
            applyTimezone(ctx, entry.id)
        }

        // Applica formato ora
        val is24h = view.findViewById<RadioButton>(R.id.rb_24h).isChecked
        Settings.System.putString(
            ctx.contentResolver,
            Settings.System.TIME_12_24,
            if (is24h) "24" else "12"
        )

        // Applica unità di misura tramite SystemProperties
        val isMetric = view.findViewById<RadioButton>(R.id.rb_metric).isChecked
        try {
            SystemProperties.set("persist.sys.units", if (isMetric) "metric" else "imperial")
        } catch (e: Exception) {
            Log.w("CDNSetupWizard", "Impossibile scrivere persist.sys.units", e)
        }
    }

    private fun updateSystemLocale(locale: Locale) {
        try {
            val am: IActivityManager = ActivityManager.getService()
            val config: Configuration = am.configuration
            config.setLocales(LocaleList(locale))
            am.updatePersistentConfiguration(config)
            Log.i("CDNSetupWizard", "Lingua di sistema impostata a: ${locale.toLanguageTag()}")
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore nell'aggiornamento della locale di sistema", e)
        }
    }

    private fun applyTimezone(ctx: Context, timezoneId: String) {
        try {
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setTimeZone(timezoneId)
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore applicando il timezone $timezoneId", e)
        }
    }

    // ── Data classes helper ──────────────────────────────────────────────────

    data class LanguageEntry(val display: String, val locale: Locale)
    data class RegionEntry(val display: String, val code: String)
    data class TimezoneEntry(val display: String, val id: String)
}

