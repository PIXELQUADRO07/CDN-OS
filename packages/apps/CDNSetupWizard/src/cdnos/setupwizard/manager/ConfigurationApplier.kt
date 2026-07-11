package cdnos.setupwizard.manager

import android.content.Context
import android.util.Log
import cdnos.setupwizard.R
import cdnos.setupwizard.model.SetupConfiguration

/**
 * Classe centrale che applica l'intera configurazione al sistema.
 * Viene chiamata solo al termine del wizard.
 */
class ConfigurationApplier(private val context: Context) {

    private val languageManager = LanguageManager(context)
    private val themeManager = ThemeManager(context)
    private val firewallManager = FirewallManager(context)
    private val appInstaller = AppInstaller(context)
    private val networkManager = NetworkManager(context)
    private val timeManager = TimeManager(context)

    interface ProgressListener {
        fun onProgress(step: Int, totalSteps: Int, message: String)
    }

    fun apply(config: SetupConfiguration, listener: ProgressListener? = null) {
        val totalSteps = 6
        var currentStep = 0

        fun notify(resId: Int) {
            currentStep++
            val message = context.getString(resId)
            listener?.onProgress(currentStep, totalSteps, message)
            Log.d("ConfigurationApplier", "Step $currentStep/$totalSteps: $message")
        }

        // 1. Lingua e Regione
        notify(R.string.applying_status_lang)
        languageManager.applyLanguage(config.locale)

        // 2. Data e Ora
        notify(R.string.applying_status_time)
        timeManager.applyTimeSettings(config.timezoneId, config.use24HourFormat)

        // 3. Rete e DNS
        notify(R.string.applying_status_network)
        networkManager.applyDnsConfiguration(config.dnsMode, config.privateDnsHostname)
        if (config.wifiSsid != null) {
            networkManager.connectToWifi(config.wifiSsid, config.wifiPassword)
        }

        // 4. Tema
        notify(R.string.applying_status_theme)
        themeManager.applyTheme(config.themeColor)

        // 5. Firewall
        notify(R.string.applying_status_firewall)
        firewallManager.applyFirewallMode(config.firewallMode)

        // 6. Applicazioni
        notify(R.string.applying_status_apps)
        appInstaller.installApps(
            config.installAurora,
            config.installFDroid,
            config.installMicroG,
            config.installTermux,
            config.installMagisk
        )

        Log.i("ConfigurationApplier", "Configurazione completata con successo!")
    }
}
