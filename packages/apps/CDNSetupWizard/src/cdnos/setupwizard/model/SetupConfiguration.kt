package cdnos.setupwizard.model

import java.util.Locale

/**
 * Modello che mantiene la configurazione temporanea del wizard.
 * I fragment modificano questo oggetto, ma i cambiamenti non vengono applicati
 * al sistema finché non si arriva alla fine del wizard.
 */
data class SetupConfiguration(
    // Livello 1: Lingua e Regione
    var locale: Locale = Locale.US,
    var regionCode: String = Locale.US.country,

    // Livello 2: Tastiera
    var keyboardImeId: String? = null,

    // Livello 3: Rete
    var wifiSsid: String? = null,
    var wifiPassword: String? = null,

    // Livello 4: Data e Ora
    var timezoneId: String = java.util.TimeZone.getDefault().id,
    var use24HourFormat: Boolean = true,
    var dateFormat: String = "dd/MM/yyyy",

    // Livello 5: Tema
    var themeColor: String = "blue", // default: blue, red, green, dark

    // Livello 6: Applicazioni
    var installAurora: Boolean = true,
    var installFDroid: Boolean = true,
    var installMicroG: Boolean = false,
    var installTermux: Boolean = false,
    var installMagisk: Boolean = false,
    val extraPackages: MutableList<String> = mutableListOf(),

    // Livello 7: Privacy & Sicurezza
    var firewallMode: FirewallMode = FirewallMode.BALANCED,
    var dnsMode: DnsMode = DnsMode.AUTOMATIC,
    var privateDnsHostname: String? = "dns.quad9.net"
)

enum class FirewallMode { BALANCED, STRICT, CUSTOM }
enum class DnsMode { AUTOMATIC, QUAD9, CLOUDFLARE, CDN_DNS }
