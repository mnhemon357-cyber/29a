package bd.emonindustry.cyberguardian29a

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.security.KeyChain
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class MainActivity : AppCompatActivity() {

    companion object {
        private const val EXPECTED_SHA256 =
            "AA:DF:60:FC:CE:71:CA:A9:72:ED:2A:F8:6C:92:91:AE:66:03:9D:C5:53:B8:05:AF:DC:67:CD:0B:CA:20:6F:7B"
    }

    private lateinit var scanResultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#070B14"))
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(44, 56, 44, 64)
        }

        // 1. Header Card with 29A Logo
        val headerCard = makeCard()
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.logo_29a)
            layoutParams = LinearLayout.LayoutParams(190, 190).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = 24
            }
        }
        val title = TextView(this).apply {
            text = "29A • CYBER GUARDIAN® v2.1"
            setTextColor(Color.parseColor("#00E5A8"))
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        val sub = TextView(this).apply {
            text = "Anti-Spyware & Cryptographic Trust Shield\nEMON INDUSTRY PVT LTD _29A"
            setTextColor(Color.parseColor("#8EA0BD"))
            textSize = 13f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 0)
        }
        headerCard.addView(logo)
        headerCard.addView(title)
        headerCard.addView(sub)
        root.addView(headerCard)

        // 2. Anti-Spyware & Trust Scanner Card
        val scanCard = makeCard()
        scanCard.addView(sectionTitle("ANTI-SPYWARE & ROOT CA AUDIT"))

        scanResultView = TextView(this).apply {
            setTextColor(Color.parseColor("#EEF2F8"))
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setPadding(0, 12, 0, 24)
        }
        scanCard.addView(scanResultView)

        val btnInstallCa = makeButton("INSTALL CYBER GUARDIAN ROOT CA", "#00E5A8", "#04150F").apply {
            setOnClickListener { installCyberGuardianCa() }
        }
        val btnRescan = makeButton("RUN ANTI-SPYWARE SCAN", "#1E293B", "#00E5A8").apply {
            setOnClickListener { runSecurityAudit() }
        }
        scanCard.addView(btnInstallCa)
        scanCard.addView(btnRescan)
        root.addView(scanCard)

        // 3. Developer & Technical Support Profile Card (from Manual v2.1)
        val devCard = makeCard()
        devCard.addView(sectionTitle("DEVELOPER & TECHNICAL SUPPORT"))
        val devInfo = TextView(this).apply {
            text = """
                NAZMUL HOSSAIN
                Computer Science Student
                Python Developer
                Linux & Cybersecurity Enthusiast

                Role: Lead Engineer • System Architecture & Security Operations
                Organization: EMON INDUSTRY PVT LTD _29A
                Support Desk v2.1: secure@emonindustry.bd
                Version: 2.1 (UTF-8 • English + বাংলা)
                © 2026 EMON INDUSTRY PVT LTD _29A. All Rights Reserved.
            """.trimIndent()
            setTextColor(Color.parseColor("#D6DFED"))
            textSize = 13f
            setLineSpacing(6f, 1.0f)
            setPadding(0, 10, 0, 20)
        }
        val btnSupport = makeButton("CONTACT SUPPORT DESK", "#141D2F", "#38BDF8").apply {
            setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:secure@emonindustry.bd")
                    putExtra(Intent.EXTRA_SUBJECT, "29A Cyber Guardian v2.1 Support")
                }
                startActivity(Intent.createChooser(emailIntent, "Email Support Desk"))
            }
        }
        devCard.addView(devInfo)
        devCard.addView(btnSupport)
        root.addView(devCard)

        scroll.addView(root)
        setContentView(scroll)

        runSecurityAudit()
    }

    private fun runSecurityAudit() {
        val sb = StringBuilder()

        // Step 1: Verify bundled Cyber Guardian Root CA
        val certBytes = assets.open("cyber_guardian_root_ca.der").use { it.readBytes() }
        val cf = CertificateFactory.getInstance("X.509")
        val cert = cf.generateCertificate(certBytes.inputStream()) as X509Certificate
        val actualFp = sha256Fingerprint(cert.encoded)
        val caValid = actualFp.equals(EXPECTED_SHA256, ignoreCase = true)

        sb.appendLine("[1] Bundled Root CA Verification:")
        sb.appendLine(if (caValid) "  [OK] Authentic Cyber Guardian CA" else "  [ALERT] Fingerprint Mismatch!")
        sb.appendLine("  SHA-256: ${actualFp.take(23)}...")
        sb.appendLine()

        // Step 2: Audit AndroidCAStore for user-installed rogue spyware CAs
        var cgInstalled = false
        val rogueUserCas = mutableListOf<String>()
        try {
            val ks = KeyStore.getInstance("AndroidCAStore")
            ks.load(null)
            val aliases = ks.aliases()
            while (aliases.hasMoreElements()) {
                val alias = aliases.nextElement()
                if (alias.startsWith("user:")) {
                    val c = ks.getCertificate(alias) as? X509Certificate ?: continue
                    val fp = sha256Fingerprint(c.encoded)
                    if (fp.equals(EXPECTED_SHA256, ignoreCase = true)) {
                        cgInstalled = true
                    } else {
                        rogueUserCas.add(c.subjectX500Principal.name)
                    }
                }
            }
        } catch (e: Exception) {
            sb.appendLine("  [INFO] Store read: ${e.message}")
        }

        sb.appendLine("[2] Device Trust Store Status:")
        sb.appendLine(if (cgInstalled) "  [OK] Cyber Guardian CA is ACTIVE" else "  [ACTION] Cyber Guardian CA not installed yet")
        if (rogueUserCas.isEmpty()) {
            sb.appendLine("  [OK] 0 unauthorized user Root CAs found")
        } else {
            sb.appendLine("  [WARNING] ${rogueUserCas.size} unverified user CA(s):")
            rogueUserCas.forEach { sb.appendLine("   - $it") }
        }
        sb.appendLine()

        // Step 3: Check active HTTP/HTTPS MITM proxy variables
        val proxyHost = System.getProperty("http.proxyHost") ?: System.getProperty("https.proxyHost")
        sb.appendLine("[3] Network Proxy Inspection:")
        if (proxyHost.isNullOrBlank()) {
            sb.appendLine("  [OK] No active system HTTP/HTTPS proxy")
        } else {
            sb.appendLine("  [ALERT] Active proxy detected: $proxyHost")
        }

        scanResultView.text = sb.toString().trim()
    }

    private fun installCyberGuardianCa() {
        try {
            val certBytes = assets.open("cyber_guardian_root_ca.der").use { it.readBytes() }
            val intent = KeyChain.createInstallIntent().apply {
                putExtra(KeyChain.EXTRA_CERTIFICATE, certBytes)
                putExtra(KeyChain.EXTRA_NAME, "Cyber Guardian Private Root CA")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Install error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sha256Fingerprint(der: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(der)
        return digest.joinToString(":") { "%02X".format(it) }
    }

    private fun makeCard(): LinearLayout {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#0F1624"))
            cornerRadius = 28f
            setStroke(2, Color.parseColor("#22304A"))
        }
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = bg
            setPadding(36, 36, 36, 36)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }
    }

    private fun sectionTitle(label: String): TextView = TextView(this).apply {
        text = label
        setTextColor(Color.parseColor("#00E5A8"))
        textSize = 14f
        setTypeface(null, Typeface.BOLD)
    }

    private fun makeButton(label: String, bgHex: String, textHex: String): Button {
        val bg = GradientDrawable().apply {
            setColor(Color.parseColor(bgHex))
            cornerRadius = 18f
        }
        return Button(this).apply {
            text = label
            setTextColor(Color.parseColor(textHex))
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            background = bg
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        }
    }
}
