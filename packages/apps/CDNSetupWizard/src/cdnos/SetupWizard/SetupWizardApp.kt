package cdnos.setupwizard

import android.app.Application

/**
 * Application class del CDNSetupWizard.
 * Punto di ingresso per inizializzazioni globali (logging, crash reporting, ecc.)
 */
class SetupWizardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Posto ideale per inizializzare librerie di sistema se necessario
    }
}
