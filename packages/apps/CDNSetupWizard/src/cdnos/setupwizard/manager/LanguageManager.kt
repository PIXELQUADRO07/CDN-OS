package cdnos.setupwizard.manager

import android.app.ActivityManager
import android.app.IActivityManager
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.util.Log
import java.util.Locale

/**
 * Gestisce l'applicazione della lingua di sistema.
 */
class LanguageManager(private val context: Context) {

    fun applyLanguage(locale: Locale) {
        try {
            val am: IActivityManager = ActivityManager.getService()
            val config: Configuration = am.configuration
            config.setLocales(LocaleList(locale))
            am.updatePersistentConfiguration(config)
            Log.i("CDNSetupWizard", "Lingua di sistema applicata: ${locale.toLanguageTag()}")
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore nell'applicazione della lingua", e)
        }
    }
}
