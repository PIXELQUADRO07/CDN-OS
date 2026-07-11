package cdnos.setupwizard.model

import androidx.lifecycle.ViewModel

/**
 * ViewModel per condividere lo stato della configurazione tra l'Activity e i Fragment.
 */
class SetupViewModel : ViewModel() {
    val configuration = SetupConfiguration()
}
