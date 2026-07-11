package cdnos.setupwizard.manager

import android.app.AlarmManager
import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * Gestisce la configurazione di data, ora e fuso orario.
 */
class TimeManager(private val context: Context) {

    fun applyTimeSettings(timezoneId: String, use24Hour: Boolean) {
        Log.i("CDNSetupWizard", "Applicazione impostazioni tempo: $timezoneId, 24h=$use24Hour")
        
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setTimeZone(timezoneId)
            
            Settings.System.putString(
                context.contentResolver,
                Settings.System.TIME_12_24,
                if (use24Hour) "24" else "12"
            )
        } catch (e: Exception) {
            Log.e("CDNSetupWizard", "Errore nell'applicazione delle impostazioni temporali", e)
        }
    }
}
