package cdnos.setupwizard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cdnos.setupwizard.fragments.*
import cdnos.setupwizard.model.SetupViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * Activity principale del CDNSetupWizard rifattorizzata.
 * Gestisce la navigazione tra i 12 livelli del setup.
 */
class SetupWizardActivity : AppCompatActivity() {

    companion object {
        const val TOTAL_PAGES = 12
    }

    private val viewModel: SetupViewModel by viewModels()
    private lateinit var progressIndicator: LinearProgressIndicator

    private var currentPage = 0
    private var wizardCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.apply {
            addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
            decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }

        setContentView(R.layout.activity_setup_wizard)

        progressIndicator = findViewById(R.id.wizard_progress)
        progressIndicator.max = TOTAL_PAGES

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

    fun navigateTo(page: Int, addToBackStack: Boolean = false) {
        if (page < 0 || page >= TOTAL_PAGES) return

        val fragment: Fragment = when (page) {
            0 -> WelcomeFragment()
            1 -> LanguageFragment()
            2 -> RegionFragment()
            3 -> KeyboardFragment()
            4 -> WifiFragment()
            5 -> DateTimeFragment()
            6 -> ThemeFragment()
            7 -> AppsFragment()
            8 -> PrivacyFragment()
            9 -> SummaryFragment()
            10 -> ApplyingFragment()
            11 -> FinishFragment()
            else -> return
        }

        val transaction = supportFragmentManager.beginTransaction()

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

    fun nextPage() {
        if (currentPage < TOTAL_PAGES - 1) {
            navigateTo(currentPage + 1)
        }
    }

    fun previousPage() {
        if (currentPage > 0) {
            // Se usiamo il backstack di FragmentManager:
            // supportFragmentManager.popBackStack()
            // Altrimenti navigazione lineare inversa:
            navigateTo(currentPage - 1)
        }
    }

    private fun updateProgress(page: Int) {
        progressIndicator.setProgressCompat(page + 1, true)
    }

    override fun onBackPressed() {
        if (currentPage > 0 && currentPage < 10) { // Permetti di tornare indietro prima di applicare
            previousPage()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_HOME -> true // Blocca sempre Home durante il wizard
            KeyEvent.KEYCODE_BACK -> {
                if (wizardCompleted) {
                    super.onKeyDown(keyCode, event)
                } else {
                    onBackPressed()
                    true
                }
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    fun completeSetup() {
        try {
            Settings.Secure.putInt(contentResolver, Settings.Secure.USER_SETUP_COMPLETE, 1)
            Settings.Global.putInt(contentResolver, Settings.Global.DEVICE_PROVISIONED, 1)
        } catch (e: SecurityException) {
            android.util.Log.e("CDNSetupWizard", "Impossibile scrivere setup status", e)
        }

        wizardCompleted = true

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

        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        startActivity(homeIntent)
        finish()
    }
}
