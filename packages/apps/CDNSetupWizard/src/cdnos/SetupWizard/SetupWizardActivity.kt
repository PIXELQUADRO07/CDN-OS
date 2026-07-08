package cdnos.setupwizard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * Activity principale del CDNSetupWizard.
 *
 * Gestisce:
 * - Navigazione tra i 9 fragment tramite FragmentManager con animazioni slide
 * - Blocco del tasto Back per impedire l'uscita prima del completamento
 * - Blocco del tasto Home (intercettato a livello di KeyEvent)
 * - Scrittura di USER_SETUP_COMPLETE e DEVICE_PROVISIONED al termine
 * - Lancio del launcher reale a fine wizard
 *
 * L'app è dichiarata con categoria HOME nel manifest, quindi Android la lancia
 * al boot al posto del launcher normale.
 */
class SetupWizardActivity : AppCompatActivity() {

    companion object {
        /** Numero totale di pagine nel wizard */
        const val TOTAL_PAGES = 9
    }

    private lateinit var progressIndicator: LinearProgressIndicator

    /** Indice corrente della pagina (0-based internamente, 1-based nella UI) */
    private var currentPage = 0

    /** Flag: il wizard è stato completato, ora si può uscire */
    private var wizardCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Imposta la finestra fullscreen, mostra sopra il lockscreen
        window.apply {
            addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
            // Edge-to-edge
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }

        setContentView(R.layout.activity_setup_wizard)

        progressIndicator = findViewById(R.id.wizard_progress)
        progressIndicator.max = TOTAL_PAGES

        // Carica il primo fragment se non è già presente (rotazione schermo)
        if (savedInstanceState == null) {
            navigateTo(0, addToBackStack = false)
        } else {
            currentPage = savedInstanceState.getInt("current_page", 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("current_page", currentPage)
    }

    /**
     * Naviga al fragment della pagina specificata.
     * @param page indice 0-based della pagina
     * @param addToBackStack se aggiungere al backstack (sempre false per il wizard lineare)
     */
    fun navigateTo(page: Int, addToBackStack: Boolean = false) {
        if (page < 0 || page >= TOTAL_PAGES) return

        val fragment: Fragment = when (page) {
            0 -> WelcomeFragment()
            1 -> RegionFragment()
            2 -> NetworkFragment()
            3 -> PrivacyFragment()
            4 -> GoogleServicesFragment()
            5 -> AppsFragment()
            6 -> SecurityFragment()
            7 -> AccountFragment()
            8 -> FinishFragment()
            else -> return
        }

        val transaction = supportFragmentManager.beginTransaction()

        // Animazione di transizione solo se non è la prima pagina
        if (page > 0) {
            transaction.setCustomAnimations(
                R.anim.fragment_enter,
                R.anim.fragment_exit
            )
        }

        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()

        currentPage = page
        updateProgress(page)
    }

    /**
     * Avanza alla pagina successiva.
     * Chiamato dai fragment tramite cast dell'activity.
     */
    fun nextPage() {
        if (currentPage < TOTAL_PAGES - 1) {
            navigateTo(currentPage + 1)
        }
    }

    /** Aggiorna la progress bar in cima */
    private fun updateProgress(page: Int) {
        progressIndicator.setProgressCompat(page + 1, true)
    }

    /**
     * Blocco del tasto Back.
     * Il wizard non può essere chiuso premendo Back.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Intenzionalmente vuoto — l'utente non può uscire finché il wizard non è completato
    }

    /**
     * Intercetta i tasti hardware.
     * HOME e BACK sono bloccati durante il wizard.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_BACK -> {
                if (wizardCompleted) {
                    super.onKeyDown(keyCode, event)
                } else {
                    true // Blocca
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    /**
     * Chiamato da FinishFragment quando l'utente preme "Inizia".
     * Segna il setup come completato e lancia il launcher reale.
     */
    fun completeSetup() {
        try {
            Settings.Secure.putInt(
                contentResolver,
                Settings.Secure.USER_SETUP_COMPLETE,
                1
            )
            Settings.Global.putInt(
                contentResolver,
                Settings.Global.DEVICE_PROVISIONED,
                1
            )
        } catch (e: SecurityException) {
            android.util.Log.e("CDNSetupWizard", "Impossibile scrivere USER_SETUP_COMPLETE", e)
        }

        wizardCompleted = true

        // Disabilita questo componente (SetupWizardActivity) nel PackageManager.
        // In questo modo Android smetterà di considerarlo un'attività HOME e
        // avvierà il launcher predefinito reale (Launcher3).
        try {
            val pm = packageManager
            val name = android.content.ComponentName(this, SetupWizardActivity::class.java)
            pm.setComponentEnabledSetting(
                name,
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            android.util.Log.e("CDNSetupWizard", "Impossibile disabilitare SetupWizardActivity", e)
        }

        // Lancia il launcher reale cedendo il controllo dell'intent HOME
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        startActivity(homeIntent)
        finish()
    }
}
