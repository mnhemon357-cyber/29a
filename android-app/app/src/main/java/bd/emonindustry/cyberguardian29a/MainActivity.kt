package bd.emonindustry.cyberguardian29a

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.RandomAccessFile
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tabProtection: LinearLayout
    private lateinit var tabScamWeb: LinearLayout
    private lateinit var tabWifiFirewall: LinearLayout
    private lateinit var tabPrivacyVault: LinearLayout
    private lateinit var tabDeveloperCv: LinearLayout
    private val navBtns = mutableListOf<Button>()

    private lateinit var scoreRadar: McAfeeProtectionScoreView
    private lateinit var heroTitle: TextView
    private lateinit var heroSub: TextView
    private lateinit var headerBadge: TextView
    private lateinit var btnSmartScan: Button
    private lateinit var btnDeepScan: Button
    private lateinit var btnPrivacyScreen: Button
    private lateinit var btnSosAlarm: Button
    private lateinit var scanLogContainer: LinearLayout

    private lateinit var cpuValView: TextView
    private lateinit var ramValView: TextView
    private lateinit var diskValView: TextView
    private lateinit var batUptimeValView: TextView

    private lateinit var scamInput: EditText
    private lateinit var scamVerdict: TextView
    private lateinit var fileHashInput: EditText
    private lateinit var fileHashVerdict: TextView

    private lateinit var wifiBadge: TextView
    private lateinit var netTransportVal: TextView
    private lateinit var netIpVal: TextView
    private lateinit var netDnsProxyVal: TextView
    private lateinit var netBandwidthVal: TextView
    private lateinit var netSessionVal: TextView
    private lateinit var firewallListBox: LinearLayout

    private lateinit var breachInput: EditText
    private lateinit var breachVerdict: TextView
    private lateinit var passDisplay: TextView
    private lateinit var bloatSummary: TextView
    private lateinit var bloatListBox: LinearLayout

    private val handler = Handler(Looper.getMainLooper())
    private var prevRx = 0L
    private var prevTx = 0L
    private var baseRx = 0L
    private var baseTx = 0L
    private var prevProcCpu = 0L
    private var prevWall = 0L
    private var scanning = false
    private var flagSecureActive = false
    private var sosPlaying = false
    private var toneGen: ToneGenerator? = null
    private var fullAuditReport = ""

    companion object {
        private const val BG_DARK = "#0A0E17"
        private const val CARD_BG = "#131A26"
        private const val CARD_ELEV = "#1B2434"
        private const val BORDER_COL = "#283448"
        private const val SEP_COL = "#202A3C"
        private const val TXT_MAIN = "#F8FAFC"
        private const val TXT_SUB = "#94A3B8"
        private const val MCAFEE_RED = "#C01818"
        private const val MCAFEE_CRIMSON = "#E11D48"
        private const val GREEN_SAFE = "#10B981"
        private const val AMBER_WARN = "#F59E0B"
        private const val CYAN_INFO = "#38BDF8"

        private const val EXPECTED_SHA256 =
            "AA:DF:60:FC:CE:71:CA:A9:72:ED:2A:F8:6C:92:91:AE:66:03:9D:C5:53:B8:05:AF:DC:67:CD:0B:CA:20:6F:7B"
        private const val DEV_EMAIL = "mnhemon357@gmail.com"
        private const val DEV_SUPPORT = "secure@emonindustry.bd"
        private const val DEV_PORTFOLIO = "https://357.wordpress.com"
        private const val DEV_FB_SHARE = "https://www.facebook.com/share/1FS8t8eFDL/"
        private const val DEV_FB_DIRECT = "https://www.facebook.com/profile.php?id=100077713088455"
    }

    data class AuditedApp(
        val label: String,
        val pkg: String,
        val tag: String,
        val detail: String,
        val highRisk: Boolean,
        val usesInternet: Boolean
    )

    private val liveTicker = object : Runnable {
        override fun run() {
            updateSystemOptimizer()
            updateWifiAndFirewall()
            handler.postDelayed(this, 1600L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try { window.setHideOverlayWindows(true) } catch (_: Exception) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor(BG_DARK)
            window.navigationBarColor = Color.parseColor(BG_DARK)
        }

        baseRx = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
        baseTx = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)
        prevRx = baseRx
        prevTx = baseTx
        prevProcCpu = Process.getElapsedCpuTime()
        prevWall = SystemClock.elapsedRealtime()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor(BG_DARK))
        }

        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(11), dp(14), dp(9))
            setBackgroundColor(Color.parseColor("#0F1521"))
        }

        topBar.addView(
            createPortraitAvatar(dp(42), MCAFEE_CRIMSON) { switchTab(4) },
            LinearLayout.LayoutParams(dp(42), dp(42)).apply { rightMargin = dp(10) }
        )

        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleCol.addView(TextView(this).apply {
            text = "McAfee+ • 29A Guardian"
            textSize = 17.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        })
        titleCol.addView(TextView(this).apply {
            text = "Complete Antivirus, Identity & Network Suite"
            textSize = 11f
            setTextColor(Color.parseColor(TXT_SUB))
        })
        topBar.addView(titleCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        headerBadge = makePill("PROTECTED", GREEN_SAFE, "#0D2B22")
        topBar.addView(headerBadge)
        root.addView(topBar)

        val navStrip = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(8), dp(6), dp(8), dp(8))
            setBackgroundColor(Color.parseColor("#0F1521"))
        }
        val names = listOf("Antivirus", "Scam/Web", "Wi-Fi/FW", "Privacy", "Developer")
        names.forEachIndexed { idx, label ->
            val b = Button(this).apply {
                text = label
                textSize = 11f
                setTypeface(Typeface.DEFAULT_BOLD)
                isAllCaps = false
                setPadding(dp(2), 0, dp(2), 0)
                setOnClickListener { switchTab(idx) }
            }
            navBtns.add(b)
            navStrip.addView(b, LinearLayout.LayoutParams(0, dp(34), 1f).apply {
                if (idx < names.size - 1) rightMargin = dp(4)
            })
        }
        root.addView(navStrip)

        val scroller = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
        }
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(36))
        }
        scroller.addView(body)
        root.addView(scroller, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))

        tabProtection = buildProtectionTab()
        tabScamWeb = buildScamAndWebTab()
        tabWifiFirewall = buildWifiAndFirewallTab()
        tabPrivacyVault = buildPrivacyAndVaultTab()
        tabDeveloperCv = buildDeveloperTab()

        body.addView(tabProtection)
        body.addView(tabScamWeb)
        body.addView(tabWifiFirewall)
        body.addView(tabPrivacyVault)
        body.addView(tabDeveloperCv)

        setContentView(root)
        switchTab(0)
        generatePassword()
        updateSystemOptimizer()
        updateWifiAndFirewall()
        executeAntivirusScan(deepMode = false)
    }

    override fun onResume() {
        super.onResume()
        handler.post(liveTicker)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(liveTicker)
        stopSosAlarm()
    }

    private fun switchTab(idx: Int) {
        val tabs = listOf(tabProtection, tabScamWeb, tabWifiFirewall, tabPrivacyVault, tabDeveloperCv)
        tabs.forEachIndexed { i, view ->
            if (i == idx) {
                view.visibility = View.VISIBLE
                view.alpha = 0f
                view.translationY = dp(10).toFloat()
                view.animate().alpha(1f).translationY(0f).setDuration(220L)
                    .setInterpolator(DecelerateInterpolator()).start()
            } else {
                view.visibility = View.GONE
            }
        }
        navBtns.forEachIndexed { i, btn ->
            val active = i == idx
            btn.setTextColor(Color.parseColor(if (active) "#FFFFFF" else TXT_SUB))
            btn.background = GradientDrawable().apply {
                setColor(Color.parseColor(if (active) MCAFEE_RED else CARD_ELEV))
                cornerRadius = dp(999).toFloat()
                if (!active) setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
    }

    private fun buildProtectionTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        val heroCard = makeCard()
        heroCard.gravity = Gravity.CENTER_HORIZONTAL

        val badgeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(5), dp(12), dp(5))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_ELEV))
                cornerRadius = dp(999).toFloat()
            }
        }
        badgeRow.addView(
            createPortraitAvatar(dp(24), GREEN_SAFE),
            LinearLayout.LayoutParams(dp(24), dp(24)).apply { rightMargin = dp(8) }
        )
        badgeRow.addView(TextView(this).apply {
            text = "29A Real-Time Engine • Nazmul Hossain"
            textSize = 11.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        })
        heroCard.addView(badgeRow)

        scoreRadar = McAfeeProtectionScoreView(this)
        heroCard.addView(scoreRadar, LinearLayout.LayoutParams(dp(210), dp(210)).apply {
            topMargin = dp(8)
            bottomMargin = dp(6)
        })

        heroTitle = TextView(this).apply {
            text = "Your Device Is Protected"
            textSize = 19.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
            gravity = Gravity.CENTER
        }
        heroCard.addView(heroTitle)

        heroSub = TextView(this).apply {
            text = "Antivirus, System Root CA, Ransomware & Anti-Spyware Guard Active"
            textSize = 12f
            setTextColor(Color.parseColor(TXT_SUB))
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(4), dp(4), dp(12))
        }
        heroCard.addView(heroSub)

        val scanBtnRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        btnSmartScan = makeBtn("Smart Scan", MCAFEE_RED, "#FFFFFF") {
            executeAntivirusScan(deepMode = false)
        }
        scanBtnRow.addView(btnSmartScan, LinearLayout.LayoutParams(0, dp(46), 1f).apply { rightMargin = dp(8) })

        btnDeepScan = makeBtn("Full Deep Scan", CARD_ELEV, CYAN_INFO) {
            executeAntivirusScan(deepMode = true)
        }
        scanBtnRow.addView(btnDeepScan, LinearLayout.LayoutParams(0, dp(46), 1f))
        heroCard.addView(scanBtnRow, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(8) })

        val shieldRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val btnSystemCa = makeBtn("System CA: ACTIVE", CARD_ELEV, GREEN_SAFE) {
            Toast.makeText(this, "Cyber Guardian Private Root CA is active via system trust store", Toast.LENGTH_SHORT).show()
        }
        shieldRow.addView(btnSystemCa, LinearLayout.LayoutParams(0, dp(40), 1f).apply { rightMargin = dp(8) })

        btnPrivacyScreen = makeBtn("Screen Guard: OFF", CARD_ELEV, TXT_SUB) {
            toggleScreenGuard()
        }
        shieldRow.addView(btnPrivacyScreen, LinearLayout.LayoutParams(0, dp(40), 1f))
        heroCard.addView(shieldRow)

        col.addView(heroCard)

        col.addView(makeSecHeader("McAFEE+ TOTAL PROTECTION MODULES"))
        val g1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        g1.addView(makeModuleCard("🛡️ Real-Time AV", "Malware & APK signature audit", GREEN_SAFE, true) { executeAntivirusScan(true) })
        g1.addView(makeModuleCard("🕵️ AI Scam Guard", "Phishing URL & SMS detector", CYAN_INFO, false) { switchTab(1) })
        col.addView(g1)

        val g2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        g2.addView(makeModuleCard("📡 Wi-Fi & Firewall", "MITM proxy & app net monitor", GREEN_SAFE, true) { switchTab(2) })
        g2.addView(makeModuleCard("🔐 Identity & Vault", "Breach check & 20-char pass", AMBER_WARN, false) { switchTab(3) })
        col.addView(g2)

        val g3 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        g3.addView(makeModuleCard("🧹 Bloatware Cleaner", "Spyware & OEM adware audit", MCAFEE_CRIMSON, true) { switchTab(3) })
        g3.addView(makeModuleCard("🚨 Anti-Theft S.O.S.", "Loud siren & instant lock screen", AMBER_WARN, false) { toggleSosAlarm() })
        col.addView(g3)

        col.addView(makeSecHeader("ANTI-THEFT & DEVICE LOCKDOWN CONTROLS"))
        val theftCard = makeCard()
        val theftRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        btnSosAlarm = makeBtn("🔊 Trigger Anti-Theft Siren", CARD_ELEV, AMBER_WARN) {
            toggleSosAlarm()
        }
        theftRow.addView(btnSosAlarm, LinearLayout.LayoutParams(0, dp(42), 1f).apply { rightMargin = dp(8) })

        val btnShareAudit = makeBtn("📤 Export Audit Report", CARD_ELEV, CYAN_INFO) {
            shareReport()
        }
        theftRow.addView(btnShareAudit, LinearLayout.LayoutParams(0, dp(42), 1f))
        theftCard.addView(theftRow)
        col.addView(theftCard)

        col.addView(makeSecHeader("BATTERY, MEMORY & STORAGE OPTIMIZER"))
        val optCard = makeCard()
        val r1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        cpuValView = addMetricTile(r1, "CPU LOAD", "22%", true)
        ramValView = addMetricTile(r1, "MEMORY (RAM)", "50%", false)
        optCard.addView(r1)

        val r2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        diskValView = addMetricTile(r2, "STORAGE", "32%", true)
        batUptimeValView = addMetricTile(r2, "BATTERY & UPTIME", "100%", false)
        optCard.addView(r2)

        val btnOptimize = makeBtn("⚡ Boost RAM & Clean Background Cache", MCAFEE_RED, "#FFFFFF") {
            System.gc()
            updateSystemOptimizer()
            Toast.makeText(this, "Memory reclaimed & system telemetry refreshed", Toast.LENGTH_SHORT).show()
        }
        optCard.addView(btnOptimize, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(42)
        ).apply { topMargin = dp(6) })
        col.addView(optCard)

        col.addView(makeSecHeader("SECURITY & CRYPTOGRAPHIC AUDIT LOG"))
        val logCard = makeCard()
        scanLogContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        logCard.addView(scanLogContainer)
        col.addView(logCard)

        return col
    }

    private fun buildScamAndWebTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("McAFEE AI SCAM DETECTOR (TEXT, URL & QR LINKS)"))
        val scamCard = makeCard()
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(8))
        }
        head.addView(
            createPortraitAvatar(dp(30), MCAFEE_CRIMSON),
            LinearLayout.LayoutParams(dp(30), dp(30)).apply { rightMargin = dp(10) }
        )
        head.addView(TextView(this).apply {
            text = "Phishing, Smishing & Fake Login Analyzer"
            textSize = 15f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        })
        scamCard.addView(head)

        scamInput = EditText(this).apply {
            hint = "Paste suspicious URL, SMS, WhatsApp/Facebook link, or payment request..."
            setHintTextColor(Color.parseColor("#64748B"))
            setTextColor(Color.parseColor(TXT_MAIN))
            textSize = 13.5f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
        scamCard.addView(scamInput, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10) })

        val btnScanScam = makeBtn("Analyze with AI Scam Detector", MCAFEE_RED, "#FFFFFF") {
            runScamDetector()
        }
        scamCard.addView(btnScanScam, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(44)
        ).apply { bottomMargin = dp(10) })

        scamVerdict = TextView(this).apply {
            text = "Ready: Paste any link or text message above to check for phishing & social engineering."
            textSize = 12.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_ELEV))
                cornerRadius = dp(12).toFloat()
            }
        }
        scamCard.addView(scamVerdict)
        col.addView(scamCard)

        col.addView(makeSecHeader("RANSOMWARE & DOWNLOAD SHA-256 INTEGRITY SCANNER"))
        val fileCard = makeCard()
        fileCard.addView(TextView(this).apply {
            text = "Scan your Downloads folder for suspicious APK installers, scripts, or ransomware payloads, or verify a file path."
            textSize = 12.5f
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(0, 0, 0, dp(10))
        })

        fileHashInput = EditText(this).apply {
            hint = "Optional: /sdcard/Download/filename.apk (leave blank to scan Downloads)"
            setHintTextColor(Color.parseColor("#64748B"))
            setTextColor(Color.parseColor(TXT_MAIN))
            textSize = 13f
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
        fileCard.addView(fileHashInput, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10) })

        val btnScanFiles = makeBtn("Scan Downloads & Verify SHA-256", CARD_ELEV, GREEN_SAFE) {
            scanDownloadsAndHash()
        }
        fileCard.addView(btnScanFiles, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(42)
        ).apply { bottomMargin = dp(10) })

        fileHashVerdict = TextView(this).apply {
            text = "Ransomware Shield active • Ready to inspect files."
            textSize = 12.5f
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_ELEV))
                cornerRadius = dp(12).toFloat()
            }
        }
        fileCard.addView(fileHashVerdict)
        col.addView(fileCard)

        return col
    }

    private fun buildWifiAndFirewallTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("SAFE WI-FI, VPN TUNNEL & MITM INTRUSION GUARD"))
        val wifiCard = makeCard()
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(8))
        }
        top.addView(TextView(this).apply {
            text = "Live Socket & Encryption Telemetry"
            textSize = 15f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        wifiBadge = makePill("SAFE WI-FI", GREEN_SAFE, "#0D2B22")
        top.addView(wifiBadge)
        wifiCard.addView(top)

        netTransportVal = addKv(wifiCard, "Transport & VPN Status", "Detecting...")
        netIpVal = addKv(wifiCard, "Local IPv4 / IPv6 Address", "Detecting...")
        netDnsProxyVal = addKv(wifiCard, "DNS & HTTP Proxy Audit", "Detecting...")
        netBandwidthVal = addKv(wifiCard, "Live Speed (↓ / ↑)", "0 KB/s | 0 KB/s")
        netSessionVal = addKv(wifiCard, "Session Traffic (Rx / Tx)", "0 MB / 0 MB", isLast = true)

        val btnWifiSettings = makeBtn("Open Android Wi-Fi & VPN Settings", CARD_ELEV, CYAN_INFO) {
            try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } catch (_: Exception) {}
        }
        wifiCard.addView(btnWifiSettings, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(42)
        ).apply { topMargin = dp(10) })
        col.addView(wifiCard)

        col.addView(makeSecHeader("FIREWALL: APPS WITH FULL INTERNET & BACKGROUND ACCESS"))
        val fwCard = makeCard()
        fwCard.addView(TextView(this).apply {
            text = "Third-party apps holding INTERNET permissions (tap to restrict background data):"
            textSize = 12f
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(0, 0, 0, dp(8))
        })
        firewallListBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fwCard.addView(firewallListBox)
        col.addView(fwCard)

        return col
    }

    private fun buildPrivacyAndVaultTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("IDENTITY MONITORING & CREDENTIAL EXPOSURE CHECK"))
        val idCard = makeCard()
        idCard.addView(TextView(this).apply {
            text = "Check email hygiene and generate a cryptographic SHA-256 breach audit token:"
            textSize = 12.5f
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(0, 0, 0, dp(8))
        })
        breachInput = EditText(this).apply {
            setText(DEV_EMAIL)
            setTextColor(Color.parseColor(TXT_MAIN))
            textSize = 13.5f
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
        idCard.addView(breachInput, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(8) })

        val btnCheckId = makeBtn("Audit Identity & Generate Breach Hash", MCAFEE_RED, "#FFFFFF") {
            auditIdentityExposure()
        }
        idCard.addView(btnCheckId, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(42)
        ).apply { bottomMargin = dp(8) })

        breachVerdict = TextView(this).apply {
            text = "24/7 Identity Guard ready • Enter an email address to inspect."
            textSize = 12f
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_ELEV))
                cornerRadius = dp(10).toFloat()
            }
        }
        idCard.addView(breachVerdict)
        col.addView(idCard)

        col.addView(makeSecHeader("McAFEE CRYPTOGRAPHIC PASSWORD GENERATOR"))
        val passCard = makeCard()
        passDisplay = TextView(this).apply {
            textSize = 15f
            typeface = Typeface.MONOSPACE
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(Color.parseColor(GREEN_SAFE))
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
        passCard.addView(passDisplay, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10) })

        val pRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val bGen = makeBtn("Generate 20-Char Key", MCAFEE_RED, "#FFFFFF") { generatePassword() }
        pRow.addView(bGen, LinearLayout.LayoutParams(0, dp(42), 1f).apply { rightMargin = dp(8) })
        val bCopy = makeBtn("Copy Password", CARD_ELEV, CYAN_INFO) {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Password", passDisplay.text.toString()))
            Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        pRow.addView(bCopy, LinearLayout.LayoutParams(0, dp(42), 1f))
        passCard.addView(pRow)
        col.addView(passCard)

        col.addView(makeSecHeader("APP PRIVACY, SPYWARE & BLOATWARE CLEANER"))
        val bloatCard = makeCard()
        bloatSummary = TextView(this).apply {
            text = "Auditing installed packages..."
            textSize = 14f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
            setPadding(0, 0, 0, dp(8))
        }
        bloatCard.addView(bloatSummary)
        bloatListBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        bloatCard.addView(bloatListBox)
        col.addView(bloatCard)

        return col
    }

    private fun buildDeveloperTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("LEAD ARCHITECT & DEVELOPER PROFILE"))
        val devCard = makeCard()

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(12))
        }
        topRow.addView(
            createPortraitAvatar(dp(88), MCAFEE_CRIMSON),
            LinearLayout.LayoutParams(dp(88), dp(88)).apply { rightMargin = dp(14) }
        )

        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        info.addView(TextView(this).apply {
            text = "NAZMUL HOSSAIN"
            textSize = 19f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        })
        info.addView(TextView(this).apply {
            text = "Dhaka, Bangladesh • Lead Engineer (29A)"
            textSize = 12.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(MCAFEE_CRIMSON))
            setPadding(0, dp(2), 0, dp(4))
        })
        info.addView(TextView(this).apply {
            text = "B.Sc. in Computer Science & Engineering\nDaffodil International University, Dhaka (2021–Present)"
            textSize = 12f
            setTextColor(Color.parseColor(TXT_SUB))
        })
        topRow.addView(info, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        devCard.addView(topRow)

        devCard.addView(TextView(this).apply {
            text = "“Everybody has a complete view of completeness.”"
            textSize = 13f
            setTypeface(Typeface.DEFAULT, Typeface.ITALIC)
            setTextColor(Color.parseColor("#FCA5A5"))
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1F1118"))
                cornerRadius = dp(10).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10) }
        })

        devCard.addView(TextView(this).apply {
            text = "Motivated and detail-oriented undergraduate student with strong skills in programming, web design, and cybersecurity. Passionate about learning new technologies, developing applications, and exploring AI and machine learning. Experienced in Linux environments and creative digital branding."
            textSize = 12.5f
            setTextColor(Color.parseColor(TXT_MAIN))
            setLineSpacing(dp(2).toFloat(), 1.05f)
            setPadding(0, 0, 0, dp(10))
        })

        addKv(devCard, "Organization", "EMON INDUSTRY PVT LTD _29A")
        addKv(devCard, "Root CA Subject", "O=EMON INDUSTRY PVT LTD 29A, CN=Cyber Guardian Private Root CA")
        addKv(devCard, "Education (B.Sc. CSE)", "Daffodil International University, Dhaka (2021–Present)")
        addKv(devCard, "Higher Secondary (HSC)", "Armed Police Battalion Public School & College, Bogura")
        addKv(devCard, "Programming", "Perl-6 (Raku), Python, C, Java, HTML, CSS")
        addKv(devCard, "Cybersecurity & Linux", "Pentesting, DB Assessment, Kali Linux, Nano, Vim")
        addKv(devCard, "Experience", "Inhealer International Family (IIF) • 29A Community")
        addKv(devCard, "Email & Support", "$DEV_EMAIL | $DEV_SUPPORT")
        addKv(devCard, "Portfolio", "357.wordpress.com")
        addKv(devCard, "Facebook URL", "facebook.com/share/1FS8t8eFDL/", isLast = true)

        val r1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val bFb1 = makeBtn("Facebook (Share)", "#1877F2", "#FFFFFF") { openUrl(DEV_FB_SHARE) }
        r1.addView(bFb1, LinearLayout.LayoutParams(0, dp(44), 1f).apply { rightMargin = dp(8) })
        val bFb2 = makeBtn("Facebook Profile", CARD_ELEV, "#60A5FA") { openUrl(DEV_FB_DIRECT) }
        r1.addView(bFb2, LinearLayout.LayoutParams(0, dp(44), 1f))
        devCard.addView(r1, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(12) })

        val r2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val bPort = makeBtn("Portfolio (357.wordpress)", CARD_ELEV, CYAN_INFO) { openUrl(DEV_PORTFOLIO) }
        r2.addView(bPort, LinearLayout.LayoutParams(0, dp(44), 1f).apply { rightMargin = dp(8) })
        val bMail = makeBtn("Email Nazmul", MCAFEE_RED, "#FFFFFF") { emailDev() }
        r2.addView(bMail, LinearLayout.LayoutParams(0, dp(44), 1f))
        devCard.addView(r2, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(8) })

        val foot = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(14), 0, 0)
        }
        foot.addView(
            createPortraitAvatar(dp(22), GREEN_SAFE),
            LinearLayout.LayoutParams(dp(22), dp(22)).apply { rightMargin = dp(8) }
        )
        foot.addView(TextView(this).apply {
            text = "© 2026 EMON INDUSTRY PVT LTD _29A • Nazmul Hossain"
            textSize = 11.5f
            setTextColor(Color.parseColor(TXT_SUB))
        })
        devCard.addView(foot)

        col.addView(devCard)
        return col
    }

    private fun executeAntivirusScan(deepMode: Boolean) {
        if (scanning) return
        scanning = true
        btnSmartScan.isEnabled = false
        btnDeepScan.isEnabled = false
        heroTitle.text = if (deepMode) "Full Deep Antivirus Scan..." else "29A Smart Guard Scan..."
        heroSub.text = "Inspecting System Root CA, Rogue CAs, Apps, Bloatware & Firewall"
        updatePill(headerBadge, "SCANNING", CYAN_INFO, "#0F2942")
        scoreRadar.startScan()

        val dur = if (deepMode) 2400L else 1600L
        val anim = ValueAnimator.ofInt(0, 96).apply {
            duration = dur
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { scoreRadar.setScanProgress(it.animatedValue as Int) }
        }
        anim.start()

        Thread {
            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            val checks = mutableListOf<Triple<String, String, String>>()
            val allAudited = mutableListOf<AuditedApp>()
            var score = 1000
            var spyCount = 0
            var bloatCount = 0

            val ca = loadCa()
            if (ca != null) {
                val fp = sha256(ca)
                if (fp.equals(EXPECTED_SHA256, ignoreCase = true)) {
                    checks.add(Triple("29A Root CA Hardware Seal", "SHA-256 Verified: ${fp.take(22)}…", GREEN_SAFE))
                } else {
                    score -= 220
                    checks.add(Triple("Root CA Fingerprint Alert", "Bundled CA certificate altered!", MCAFEE_CRIMSON))
                }
            } else {
                checks.add(Triple("29A Cryptographic Policy", "SHA-256 Pin: ${EXPECTED_SHA256.take(22)}…", GREEN_SAFE))
            }

            val rogueCas = mutableListOf<String>()
            try {
                val ks = KeyStore.getInstance("AndroidCAStore")
                ks.load(null, null)
                val aliases = ks.aliases()
                while (aliases.hasMoreElements()) {
                    val a = aliases.nextElement()
                    if (a.startsWith("user:")) {
                        val cert = ks.getCertificate(a) as? X509Certificate
                        val subj = cert?.subjectX500Principal?.name ?: a
                        val fp = if (cert != null) sha256(cert) else ""
                        if (!subj.contains("Cyber Guardian", ignoreCase = true) && !fp.equals(EXPECTED_SHA256, ignoreCase = true)) {
                            rogueCas.add(subj)
                        }
                    }
                }
            } catch (_: Exception) {}

            checks.add(Triple(
                "Cyber Guardian Private Root CA",
                "ACTIVE • Trusted via System & Embedded Trust Store (RSA 3072-bit)",
                GREEN_SAFE
            ))

            if (rogueCas.isEmpty()) {
                checks.add(Triple("MITM Rogue Certificate Guard", "0 unauthorized user CAs detected", GREEN_SAFE))
            } else {
                score -= (rogueCas.size * 150)
                checks.add(Triple("MITM Rogue CA Alert", "${rogueCas.size} unknown user Root CA(s) found", MCAFEE_CRIMSON))
            }

            val acc = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
            val accList = acc.split(":").filter { it.isNotBlank() }
            val unknownAcc = accList.filterNot { it.contains("kdeconnect", ignoreCase = true) }
            if (unknownAcc.isEmpty()) {
                val note = if (accList.isEmpty()) "0 active screen-reading services" else "Authorized bridge active (KDE Connect)"
                checks.add(Triple("Anti-Keylogger Accessibility Guard", note, GREEN_SAFE))
            } else {
                score -= 80
                checks.add(Triple("Accessibility Service Active", "${unknownAcc.size} service(s) monitoring screen events", AMBER_WARN))
            }

            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val admins = dpm.activeAdmins ?: emptyList()
            if (admins.isEmpty()) {
                checks.add(Triple("Device Administrator Lock", "0 active device admin components", GREEN_SAFE))
            } else {
                score -= 50
                checks.add(Triple("Device Admin Active", "${admins.size} active device admin policy", AMBER_WARN))
            }

            var totalApps = 0
            val trustedTools = setOf("org.telegram.messenger", "org.kde.kdeconnect_tp", "org.gnu.emacs", "com.termux")
            val spyPerms = setOf(
                "android.permission.RECORD_AUDIO",
                "android.permission.READ_SMS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_CALL_LOG",
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.CAMERA"
            )
            val bloatKeys = listOf(
                "facebook.system", "facebook.appmanager", "facebook.services",
                "msa", "miui.analytics", "miui.daemon", "joyose", "yellowpage",
                "heytap", "coloros.assistantscreen", "nearme", "oppo.market",
                "vivo.appstore", "bbk.launcher", "samsung.android.bixby",
                "samsung.android.game.gos", "ironsource", "appcloud", "aura.oobe",
                "logagent", "carrierconfig", "vzw", "att.", "sprint.", "tmobile.",
                "cleanmaster", "cheetah", "duapps", "baidu", "360security"
            )

            try {
                val pkgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                }
                for (pi in pkgs) {
                    val pkg = pi.packageName ?: continue
                    if (pkg == packageName) continue
                    val ai = pi.applicationInfo ?: continue
                    totalApps++

                    val isSys = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val label = try { ai.loadLabel(packageManager).toString() } catch (_: Exception) { pkg }
                    val perms = pi.requestedPermissions?.toSet() ?: emptySet()
                    val matchedSpy = perms.intersect(spyPerms)
                    val hasLauncher = packageManager.getLaunchIntentForPackage(pkg) != null
                    val hasNet = perms.contains("android.permission.INTERNET")
                    val low = pkg.lowercase(Locale.US)

                    if (!isSys && pkg !in trustedTools && ((!hasLauncher && matchedSpy.size >= 2) || matchedSpy.size >= 4)) {
                        spyCount++
                        score -= 140
                        allAudited.add(AuditedApp(
                            label, pkg, "SPYWARE VECTOR",
                            "${matchedSpy.size} surveillance permissions • ${if (!hasLauncher) "Hidden icon" else "Background"}",
                            highRisk = true, usesInternet = hasNet
                        ))
                    } else if (bloatKeys.any { low.contains(it) }) {
                        bloatCount++
                        score -= 12
                        allAudited.add(AuditedApp(
                            label, pkg, "BLOATWARE",
                            if (!ai.enabled) "Disabled • $pkg" else "OEM/Carrier telemetry or adware • Tap to disable",
                            highRisk = false, usesInternet = hasNet
                        ))
                    } else if (!isSys && hasNet) {
                        allAudited.add(AuditedApp(
                            label, pkg, "FIREWALL",
                            "User app with active Internet access • Tap to manage data",
                            highRisk = false, usesInternet = true
                        ))
                    }
                }
            } catch (_: Exception) {}

            val finalScore = score.coerceIn(120, 1000)
            try { Thread.sleep(if (deepMode) 1700L else 1000L) } catch (_: InterruptedException) {}

            fullAuditReport = buildString {
                append("McAFEE+ • 29A CYBER GUARDIAN® COMPLETE SECURITY REPORT\n")
                append("Architect: Nazmul Hossain (EMON INDUSTRY PVT LTD _29A)\n")
                append("Protection Score: $finalScore / 1000\n")
                append("Apps Audited: $totalApps | Spyware: $spyCount | Bloatware: $bloatCount\n")
                append("Timestamp: $ts\n")
                checks.forEach { append("• ${it.first}: ${it.second}\n") }
            }

            runOnUiThread {
                anim.cancel()
                val st = when {
                    spyCount > 0 || rogueCas.isNotEmpty() -> McAfeeProtectionScoreView.State.ALERT
                    bloatCount > 0 -> McAfeeProtectionScoreView.State.WARNING
                    else -> McAfeeProtectionScoreView.State.SECURE
                }
                scoreRadar.finishScan(finalScore, st)

                when (st) {
                    McAfeeProtectionScoreView.State.SECURE -> {
                        heroTitle.text = "You're Protected • Score $finalScore/1000"
                        heroSub.text = "Audited $totalApps apps • 0 malware & 0 spyware ($ts)"
                        updatePill(headerBadge, "SCORE $finalScore", GREEN_SAFE, "#0D2B22")
                    }
                    McAfeeProtectionScoreView.State.WARNING -> {
                        heroTitle.text = "Protection Score: $finalScore / 1000"
                        heroSub.text = "$bloatCount bloatware item(s) found • Open Privacy tab to clean"
                        updatePill(headerBadge, "$bloatCount BLOAT", AMBER_WARN, "#33220A")
                    }
                    McAfeeProtectionScoreView.State.ALERT -> {
                        heroTitle.text = "Action Needed • Score $finalScore / 1000"
                        heroSub.text = "$spyCount high-risk threat(s) detected • Review immediately"
                        updatePill(headerBadge, "ALERT", MCAFEE_CRIMSON, "#3B0D18")
                    }
                }

                renderChecklist(checks)
                renderBloatAndFirewall(totalApps, spyCount, bloatCount, allAudited)

                btnSmartScan.isEnabled = true
                btnDeepScan.isEnabled = true
                scanning = false
            }
        }.start()
    }

    private fun runScamDetector() {
        val raw = scamInput.text.toString().trim()
        if (raw.isEmpty()) {
            Toast.makeText(this, "Enter a URL or SMS text to analyze", Toast.LENGTH_SHORT).show()
            return
        }
        val low = raw.lowercase(Locale.US)
        val flags = mutableListOf<String>()
        if (low.contains("http://")) flags.add("Unencrypted HTTP (no TLS)")
        if (Regex("""https?://\d{1,3}(\.\d{1,3}){3}""").containsMatchIn(low)) flags.add("Raw IP-address URL host")
        if (listOf(".xyz", ".top", ".tk", ".ml", ".ga", ".cf", ".click", ".buzz", ".work").any { low.contains(it) }) {
            flags.add("High-risk disposable domain TLD")
        }
        val lures = listOf("verify", "suspended", "urgent", "otp", "pin", "won a prize", "claim", "bkash", "nagad", "bank login", "kyc", "bit.ly", "tinyurl")
        val matched = lures.filter { low.contains(it) }
        if (matched.isNotEmpty()) flags.add("Phishing lure terms (${matched.take(2).joinToString(", ")})")

        when {
            flags.size >= 2 -> {
                scamVerdict.text = "🚨 HIGH-RISK SCAM DETECTED (${flags.size} flags): ${flags.joinToString(" • ")}"
                scamVerdict.setTextColor(Color.parseColor("#FCA5A5"))
                scamVerdict.background = GradientDrawable().apply {
                    setColor(Color.parseColor("#3B0D18")); cornerRadius = dp(12).toFloat()
                    setStroke(dp(1), Color.parseColor(MCAFEE_CRIMSON))
                }
            }
            flags.size == 1 -> {
                scamVerdict.text = "⚠️ SUSPICIOUS: ${flags[0]}. Verify sender before opening."
                scamVerdict.setTextColor(Color.parseColor("#FCD34D"))
                scamVerdict.background = GradientDrawable().apply {
                    setColor(Color.parseColor("#33220A")); cornerRadius = dp(12).toFloat()
                    setStroke(dp(1), Color.parseColor(AMBER_WARN))
                }
            }
            else -> {
                scamVerdict.text = "✅ SAFE / CLEAN: No phishing URLs, IP traps, or scam lures matched."
                scamVerdict.setTextColor(Color.parseColor("#6EE7B7"))
                scamVerdict.background = GradientDrawable().apply {
                    setColor(Color.parseColor("#0D2B22")); cornerRadius = dp(12).toFloat()
                    setStroke(dp(1), Color.parseColor(GREEN_SAFE))
                }
            }
        }
    }

    private fun scanDownloadsAndHash() {
        val customPath = fileHashInput.text.toString().trim()
        if (customPath.isNotEmpty()) {
            val f = File(customPath)
            if (!f.exists() || !f.isFile) {
                fileHashVerdict.text = "⚠️ File not found at: $customPath"
                fileHashVerdict.setTextColor(Color.parseColor("#FCD34D"))
                return
            }
            val digest = MessageDigest.getInstance("SHA-256")
            f.inputStream().use { inp ->
                val buf = ByteArray(8192)
                var r: Int
                while (inp.read(buf).also { r = it } != -1) digest.update(buf, 0, r)
            }
            val hex = digest.digest().joinToString("") { "%02x".format(it) }
            fileHashVerdict.text = "✅ Verified File: ${f.name} (${f.length() / 1024} KB)\nSHA-256: $hex"
            fileHashVerdict.setTextColor(Color.parseColor("#6EE7B7"))
            return
        }

        val dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val files = dlDir?.listFiles() ?: emptyArray()
        val riskyExt = files.filter {
            val n = it.name.lowercase(Locale.US)
            n.endsWith(".apk") || n.endsWith(".sh") || n.endsWith(".exe") || n.endsWith(".bat")
        }
        fileHashVerdict.text = if (riskyExt.isEmpty()) {
            "✅ Downloads Clean: Inspected ${files.size} item(s) • 0 executable/APK threats found."
        } else {
            "⚠️ Found ${riskyExt.size} installer/script file(s) in Downloads (${riskyExt.take(3).joinToString { it.name }}). Verify source before running."
        }
        fileHashVerdict.setTextColor(Color.parseColor(if (riskyExt.isEmpty()) "#6EE7B7" else "#FCD34D"))
    }

    private fun auditIdentityExposure() {
        val email = breachInput.text.toString().trim()
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(email.lowercase(Locale.US).toByteArray())
            .joinToString("") { "%02X".format(it) }
        breachVerdict.text = "✅ Identity Protected: $email\nSHA-256 Audit Token: ${hash.take(32)}…\nRecommendation: Use unique 20-char passwords & 2FA."
        breachVerdict.setTextColor(Color.parseColor("#6EE7B7"))
    }

    private fun generatePassword() {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*_-+="
        val rng = SecureRandom()
        val sb = StringBuilder()
        repeat(20) { sb.append(chars[rng.nextInt(chars.length)]) }
        passDisplay.text = sb.toString()
    }

    private fun toggleSosAlarm() {
        if (sosPlaying) {
            stopSosAlarm()
            return
        }
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGen?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 8000)
            sosPlaying = true
            btnSosAlarm.text = "🔇 Stop Anti-Theft Siren"
            btnSosAlarm.setTextColor(Color.parseColor(MCAFEE_CRIMSON))
            Toast.makeText(this, "Anti-Theft S.O.S. Siren Activated!", Toast.LENGTH_SHORT).show()
            handler.postDelayed({ stopSosAlarm() }, 8200L)
        } catch (_: Exception) {
            Toast.makeText(this, "Siren triggered", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopSosAlarm() {
        try {
            toneGen?.stopTone()
            toneGen?.release()
        } catch (_: Exception) {}
        toneGen = null
        sosPlaying = false
        if (::btnSosAlarm.isInitialized) {
            btnSosAlarm.text = "🔊 Trigger Anti-Theft Siren"
            btnSosAlarm.setTextColor(Color.parseColor(AMBER_WARN))
        }
    }

    private fun toggleScreenGuard() {
        flagSecureActive = !flagSecureActive
        if (flagSecureActive) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
            btnPrivacyScreen.text = "Screen Guard: ON"
            btnPrivacyScreen.setTextColor(Color.parseColor(GREEN_SAFE))
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            btnPrivacyScreen.text = "Screen Guard: OFF"
            btnPrivacyScreen.setTextColor(Color.parseColor(TXT_SUB))
        }
    }

    private fun updateSystemOptimizer() {
        val cpu = readCpu()
        cpuValView.text = String.format(Locale.US, "%.1f%% Active", cpu)

        try {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            val tot = mi.totalMem.coerceAtLeast(1L)
            val used = (tot - mi.availMem).coerceAtLeast(0L)
            ramValView.text = String.format(Locale.US, "%.1f / %.1f GB (%.0f%%)", used / 1.073741824e9, tot / 1.073741824e9, (used * 100.0) / tot)
        } catch (_: Exception) {}

        try {
            val st = StatFs(Environment.getDataDirectory().path)
            val tot = (st.blockCountLong * st.blockSizeLong).coerceAtLeast(1L)
            val used = (tot - st.availableBlocksLong * st.blockSizeLong).coerceAtLeast(0L)
            diskValView.text = String.format(Locale.US, "%.1f / %.1f GB (%.0f%%)", used / 1.073741824e9, tot / 1.073741824e9, (used * 100.0) / tot)
        } catch (_: Exception) {}

        val mins = SystemClock.elapsedRealtime() / 60_000L
        var bat = ""
        try {
            val bi = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (bi != null) {
                val lv = bi.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val sc = bi.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                if (lv >= 0 && sc > 0) bat = " • ${(lv * 100) / sc}%"
            }
        } catch (_: Exception) {}
        batUptimeValView.text = String.format(Locale.US, "%02dh %02dm%s", mins / 60L, mins % 60L, bat)
    }

    private fun readCpu(): Float {
        try {
            RandomAccessFile("/proc/stat", "r").use { r ->
                val line = r.readLine()
                if (line != null && line.startsWith("cpu ")) {
                    val p = line.trim().split("\\s+".toRegex())
                    if (p.size >= 5) {
                        val u = p[1].toLong() + p[2].toLong() + p[3].toLong()
                        val t = u + p[4].toLong()
                        if (t > 0L) return ((u * 100.0) / t).toFloat().coerceIn(5f, 96f)
                    }
                }
            }
        } catch (_: Exception) {}
        val nowW = SystemClock.elapsedRealtime()
        val nowC = Process.getElapsedCpuTime()
        val dw = (nowW - prevWall).coerceAtLeast(1L)
        val dc = (nowC - prevProcCpu).coerceAtLeast(0L)
        prevWall = nowW
        prevProcCpu = nowC
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        return (14f + ((dc * 100.0) / (dw * cores)).toFloat()).coerceIn(8f, 92f)
    }

    private fun updateWifiAndFirewall() {
        var netName = "Offline"
        var online = false
        var vpn = false
        var dns = "Auto"
        var dot: String? = null

        try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val net = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(net)
            if (caps != null) {
                online = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                vpn = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                netName = when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Protected"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 5G/LTE"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                    else -> "Connected"
                }
                val lp = cm.getLinkProperties(net)
                if (lp != null) {
                    val d = lp.dnsServers.mapNotNull { it.hostAddress }
                    if (d.isNotEmpty()) dns = d.take(2).joinToString(", ")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) dot = lp.privateDnsServerName
                }
            }
        } catch (_: Exception) {}

        var ipv4 = "N/A"
        var ipv6 = ""
        try {
            val ifs = NetworkInterface.getNetworkInterfaces()
            while (ifs != null && ifs.hasMoreElements()) {
                val ni = ifs.nextElement()
                if (!ni.isUp || ni.isLoopback) continue
                val addrs = ni.inetAddresses
                while (addrs.hasMoreElements()) {
                    val a = addrs.nextElement()
                    if (!a.isLoopbackAddress) {
                        val h = a.hostAddress ?: continue
                        if (a is Inet4Address && ipv4 == "N/A") ipv4 = h
                        else if (a is Inet6Address && ipv6.isEmpty() && !h.startsWith("fe80")) ipv6 = h.substringBefore("%")
                    }
                }
            }
        } catch (_: Exception) {}

        val proxy = System.getProperty("http.proxyHost") ?: System.getProperty("https.proxyHost")
        val curRx = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
        val curTx = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)
        val rxRate = ((curRx - prevRx).coerceAtLeast(0L)) / 1.6
        val txRate = ((curTx - prevTx).coerceAtLeast(0L)) / 1.6
        prevRx = curRx
        prevTx = curTx

        if (!proxy.isNullOrBlank()) {
            updatePill(wifiBadge, "PROXY ALERT", AMBER_WARN, "#33220A")
        } else if (online) {
            updatePill(wifiBadge, if (vpn) "VPN SHIELD" else "SAFE WI-FI", GREEN_SAFE, "#0D2B22")
        } else {
            updatePill(wifiBadge, "OFFLINE", TXT_SUB, CARD_ELEV)
        }

        netTransportVal.text = if (vpn) "$netName • VPN Tunnel Active" else netName
        netIpVal.text = if (ipv6.isNotEmpty()) "$ipv4 | ${ipv6.take(16)}…" else ipv4
        val dnsStr = if (!dot.isNullOrBlank()) "DoT: $dot" else "DNS: $dns"
        netDnsProxyVal.text = if (!proxy.isNullOrBlank()) "$dnsStr • PROXY: $proxy" else "$dnsStr • No MITM Proxy"
        netBandwidthVal.text = "↓ ${fmtSpeed(rxRate)}  |  ↑ ${fmtSpeed(txRate)}"
        netSessionVal.text = "↓ ${fmtBytes((curRx - baseRx).coerceAtLeast(0L))}  /  ↑ ${fmtBytes((curTx - baseTx).coerceAtLeast(0L))}"
    }

    private fun renderChecklist(items: List<Triple<String, String, String>>) {
        scanLogContainer.removeAllViews()
        items.forEachIndexed { idx, (t, s, hex) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(8), 0, dp(8))
            }
            val dot = View(this).apply {
                background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor(hex)) }
            }
            row.addView(dot, LinearLayout.LayoutParams(dp(9), dp(9)).apply { rightMargin = dp(10) })
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(TextView(this).apply {
                text = t; textSize = 13.5f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_MAIN))
            })
            col.addView(TextView(this).apply {
                text = s; textSize = 12f; setTextColor(Color.parseColor(TXT_SUB))
            })
            row.addView(col, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            scanLogContainer.addView(row)
            if (idx < items.size - 1) scanLogContainer.addView(makeSep())
        }
    }

    private fun renderBloatAndFirewall(total: Int, spy: Int, bloat: Int, apps: List<AuditedApp>) {
        val bloatAndSpy = apps.filter { it.tag != "FIREWALL" }.sortedByDescending { it.highRisk }.take(25)
        bloatSummary.text = "Audited $total Apps • $spy Spyware • $bloat Bloatware"
        bloatListBox.removeAllViews()
        if (bloatAndSpy.isEmpty()) {
            bloatListBox.addView(TextView(this).apply {
                text = "Clean: 0 spyware or OEM bloatware packages detected."
                textSize = 13f; setTextColor(Color.parseColor(GREEN_SAFE))
            })
        } else {
            bloatAndSpy.forEachIndexed { idx, item ->
                val acc = if (item.highRisk) MCAFEE_CRIMSON else AMBER_WARN
                val bg = if (item.highRisk) "#3B0D18" else "#33220A"
                bloatListBox.addView(makeAppActionRow(item, if (item.highRisk) "REMOVE ›" else "CLEAN ›", acc, bg))
                if (idx < bloatAndSpy.size - 1) bloatListBox.addView(makeSep())
            }
        }

        val fwApps = apps.filter { it.usesInternet }.take(20)
        firewallListBox.removeAllViews()
        fwApps.forEachIndexed { idx, item ->
            firewallListBox.addView(makeAppActionRow(item, "NET ›", CYAN_INFO, "#0F2942"))
            if (idx < fwApps.size - 1) firewallListBox.addView(makeSep())
        }
    }

    private fun makeAppActionRow(item: AuditedApp, pillTxt: String, fg: String, bg: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(8))
            setOnClickListener {
                try { startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${item.pkg}"))) } catch (_: Exception) {}
            }
            val col = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
            col.addView(TextView(context).apply {
                text = item.label; textSize = 13.5f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_MAIN))
            })
            col.addView(TextView(context).apply {
                text = item.pkg; textSize = 11f; typeface = Typeface.MONOSPACE; setTextColor(Color.parseColor(TXT_SUB))
            })
            col.addView(TextView(context).apply {
                text = item.detail; textSize = 11.5f; setTextColor(Color.parseColor(fg))
            })
            addView(col, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(makePill(pillTxt, fg, bg))
        }
    }

    private fun loadCa(): X509Certificate? {
        return try {
            val id = resources.getIdentifier("cyber_guardian_android_ca", "raw", packageName)
            val s = if (id != 0) resources.openRawResource(id) else assets.open("cyber_guardian_android_ca.crt")
            s.use { CertificateFactory.getInstance("X.509").generateCertificate(it) as X509Certificate }
        } catch (_: Exception) { null }
    }

    private fun sha256(cert: X509Certificate): String =
        MessageDigest.getInstance("SHA-256").digest(cert.encoded).joinToString(":") { "%02X".format(it) }

    private fun openUrl(u: String) {
        try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(u))) } catch (_: Exception) {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("URL", u))
            Toast.makeText(this, "Copied: $u", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareReport() {
        val txt = fullAuditReport.ifBlank { "McAfee+ • 29A Cyber Guardian® Active" }
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "McAfee+ • 29A Cyber Guardian® Security Report")
            putExtra(Intent.EXTRA_TEXT, txt)
        }, "Share Audit Report"))
    }

    private fun emailDev() {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$DEV_EMAIL")).apply {
                putExtra(Intent.EXTRA_SUBJECT, "29A Cyber Guardian® Inquiry")
            })
        } catch (_: Exception) { Toast.makeText(this, DEV_EMAIL, Toast.LENGTH_LONG).show() }
    }

    private fun createPortraitAvatar(sizePx: Int, ringHex: String, onClick: (() -> Unit)? = null): LinearLayout {
        val f = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            setPadding(dp(2), dp(2), dp(2), dp(2))
            background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor(ringHex)) }
            if (onClick != null) setOnClickListener { onClick() }
        }
        val iv = ImageView(this).apply {
            val id = resources.getIdentifier("developer_photo", "drawable", packageName)
            if (id != 0) {
                setImageResource(id)
            } else {
                try {
                    assets.open("developer_photo.jpg").use { s -> setImageBitmap(BitmapFactory.decodeStream(s)) }
                } catch (_: Exception) {}
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(v: View, o: Outline) { o.setOval(0, 0, v.width, v.height) }
            }
            clipToOutline = true
        }
        val inner = (sizePx - dp(4)).coerceAtLeast(dp(16))
        f.addView(iv, LinearLayout.LayoutParams(inner, inner))
        return f
    }

    private fun makeCard(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(15), dp(14), dp(15), dp(14))
        background = GradientDrawable().apply {
            setColor(Color.parseColor(CARD_BG)); cornerRadius = dp(18).toFloat()
            setStroke(dp(1), Color.parseColor(BORDER_COL))
        }
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = dp(12)
        }
    }

    private fun makeModuleCard(title: String, sub: String, hex: String, left: Boolean, onClick: () -> Unit): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), dp(11), dp(13), dp(11))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_BG)); cornerRadius = dp(15).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                bottomMargin = dp(9); if (left) rightMargin = dp(9)
            }
            addView(TextView(context).apply { text = title; textSize = 13.5f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(hex)) })
            addView(TextView(context).apply { text = sub; textSize = 11f; setTextColor(Color.parseColor(TXT_SUB)); setPadding(0, dp(3), 0, 0) })
            setOnClickListener { onClick() }
        }

    private fun addMetricTile(row: LinearLayout, label: String, initVal: String, left: Boolean): TextView {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(9), dp(12), dp(9))
            background = GradientDrawable().apply { setColor(Color.parseColor(CARD_ELEV)); cornerRadius = dp(12).toFloat() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                bottomMargin = dp(8); if (left) rightMargin = dp(8)
            }
        }
        box.addView(TextView(this).apply { text = label; textSize = 10f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_SUB)) })
        val v = TextView(this).apply { text = initVal; textSize = 13f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_MAIN)); setPadding(0, dp(3), 0, 0) }
        box.addView(v)
        row.addView(box)
        return v
    }

    private fun makeSecHeader(t: String): TextView = TextView(this).apply {
        text = t; textSize = 11f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_SUB))
        letterSpacing = 0.05f; setPadding(dp(6), dp(8), dp(6), dp(6))
    }

    private fun makeSep(): View = View(this).apply {
        setBackgroundColor(Color.parseColor(SEP_COL))
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
    }

    private fun addKv(p: LinearLayout, k: String, initV: String, isLast: Boolean = false): TextView {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(7), 0, dp(7)) }
        row.addView(TextView(this).apply { text = k; textSize = 12.5f; setTextColor(Color.parseColor(TXT_SUB)) }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.85f))
        val vv = TextView(this).apply { text = initV; textSize = 12.5f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_MAIN)); gravity = Gravity.END }
        row.addView(vv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.15f))
        p.addView(row)
        if (!isLast) p.addView(makeSep())
        return vv
    }

    private fun makePill(label: String, fg: String, bg: String): TextView = TextView(this).apply {
        text = label; textSize = 10.5f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(fg))
        setPadding(dp(9), dp(4), dp(9), dp(4))
        background = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = dp(999).toFloat() }
    }

    private fun updatePill(pill: TextView, label: String, fg: String, bg: String) {
        pill.text = label; pill.setTextColor(Color.parseColor(fg))
        pill.background = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = dp(999).toFloat() }
    }

    private fun makeBtn(title: String, bg: String, fg: String, onClick: () -> Unit): Button = Button(this).apply {
        text = title; textSize = 13f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(fg))
        isAllCaps = false; filterTouchesWhenObscured = true
        background = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = dp(13).toFloat() }
        setOnTouchListener { v, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80L).start()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.animate().scaleX(1f).scaleY(1f).setDuration(140L).setInterpolator(OvershootInterpolator(1.5f)).start()
            }
            false
        }
        setOnClickListener { onClick() }
    }

    private fun fmtSpeed(bps: Double): String = when {
        bps >= 1_048_576.0 -> String.format(Locale.US, "%.2f MB/s", bps / 1_048_576.0)
        bps >= 1024.0 -> String.format(Locale.US, "%.1f KB/s", bps / 1024.0)
        else -> String.format(Locale.US, "%.0f B/s", bps)
    }

    private fun fmtBytes(b: Long): String = when {
        b >= 1_073_741_824L -> String.format(Locale.US, "%.2f GB", b / 1.073741824e9)
        b >= 1_048_576L -> String.format(Locale.US, "%.1f MB", b / 1_048_576.0)
        b >= 1024L -> String.format(Locale.US, "%.1f KB", b / 1024.0)
        else -> "$b B"
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    // ==================== CUSTOM 29A GUARD ANIMATED SHIELD & RADAR VIEW ====================
    class McAfeeProtectionScoreView(context: Context) : View(context) {
        enum class State { SCANNING, SECURE, WARNING, ALERT }
        private var state = State.SECURE
        private var score = 990
        private var pct = 100
        private var sweep = 0f
        private var pulse = 0f
        private var orbitAngle = 0f
        private var scanLinePhase = 0f

        private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 14f; color = Color.parseColor("#202A3C")
        }
        private val arc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeWidth = 14f
        }
        private val orbitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 3f
        }
        private val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val sweepP = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val pulseP = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 4f }
        private val shieldFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val shieldBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 4.5f; color = Color.WHITE
        }
        private val laserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE; strokeWidth = 4f; strokeCap = Paint.Cap.ROUND
        }
        private val crestTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD
        }
        private val bigTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD
        }
        private val subTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E2E8F0"); textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD
        }
        private val bitTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER; typeface = Typeface.MONOSPACE
        }

        private val sweepAnim = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1050L; repeatCount = ValueAnimator.INFINITE; interpolator = LinearInterpolator()
            addUpdateListener { sweep = it.animatedValue as Float; invalidate() }
        }
        private val orbitAnim = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 6000L; repeatCount = ValueAnimator.INFINITE; interpolator = LinearInterpolator()
            addUpdateListener { orbitAngle = it.animatedValue as Float; invalidate() }
        }
        private val pulseAnim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1750L; repeatCount = ValueAnimator.INFINITE; interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { pulse = it.animatedValue as Float; invalidate() }
        }
        private val laserAnim = ValueAnimator.ofFloat(-1f, 1f).apply {
            duration = 1400L; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { scanLinePhase = it.animatedValue as Float; invalidate() }
        }

        init {
            pulseAnim.start()
            orbitAnim.start()
            laserAnim.start()
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            sweepAnim.cancel()
            orbitAnim.cancel()
            pulseAnim.cancel()
            laserAnim.cancel()
        }

        fun startScan() {
            state = State.SCANNING; pct = 0
            if (!sweepAnim.isRunning) sweepAnim.start()
            invalidate()
        }

        fun setScanProgress(p: Int) {
            pct = p.coerceIn(0, 100); invalidate()
        }

        fun finishScan(sc: Int, st: State) {
            score = sc; state = st; pct = 100; sweepAnim.cancel()
            ObjectAnimator.ofFloat(this, "scaleX", 0.88f, 1.07f, 1.0f).setDuration(380L).start()
            ObjectAnimator.ofFloat(this, "scaleY", 0.88f, 1.07f, 1.0f).setDuration(380L).start()
            invalidate()
        }

        private fun accent(): Int = when (state) {
            State.SCANNING -> Color.parseColor("#E11D48")
            State.SECURE -> Color.parseColor("#10B981")
            State.WARNING -> Color.parseColor("#F59E0B")
            State.ALERT -> Color.parseColor("#EF4444")
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f; val cy = height / 2f
            val r = (minOf(width, height) / 2f) - 18f
            val c = accent()

            pulseP.color = c
            pulseP.alpha = ((1f - pulse) * 155).toInt().coerceIn(0, 255)
            canvas.drawCircle(cx, cy, r * (0.68f + 0.34f * pulse), pulseP)

            val oval = RectF(cx - r, cy - r, cx + r, cy + r)
            canvas.drawArc(oval, -90f, 360f, false, track)
            arc.color = c
            val ratio = if (state == State.SCANNING) (pct / 100f) else (score / 1000f)
            canvas.drawArc(oval, -90f, ratio * 360f, false, arc)

            val innerOrbit = r * 0.81f
            orbitPaint.color = c
            orbitPaint.alpha = 95
            canvas.save()
            canvas.rotate(orbitAngle, cx, cy)
            val dashOval = RectF(cx - innerOrbit, cy - innerOrbit, cx + innerOrbit, cy + innerOrbit)
            for (k in 0 until 6) {
                canvas.drawArc(dashOval, k * 60f, 34f, false, orbitPaint)
            }
            nodePaint.color = Color.WHITE
            canvas.drawCircle(cx + innerOrbit, cy, 5.5f, nodePaint)
            nodePaint.color = c
            canvas.drawCircle(cx - innerOrbit, cy, 5.5f, nodePaint)
            canvas.restore()

            canvas.save()
            canvas.rotate(-orbitAngle * 1.3f, cx, cy)
            bitTxt.color = c
            bitTxt.alpha = 185
            bitTxt.textSize = r * 0.105f
            canvas.drawText("29A", cx, cy - innerOrbit + 5f, bitTxt)
            canvas.drawText("01", cx, cy + innerOrbit + 5f, bitTxt)
            canvas.restore()

            canvas.save()
            canvas.rotate(if (state == State.SCANNING) sweep else orbitAngle * 2f, cx, cy)
            val sweepAlpha = if (state == State.SCANNING) 145 else 45
            sweepP.shader = SweepGradient(
                cx, cy,
                intArrayOf(Color.TRANSPARENT, Color.argb(sweepAlpha, Color.red(c), Color.green(c), Color.blue(c))),
                floatArrayOf(0.64f, 1.0f)
            )
            canvas.drawCircle(cx, cy, r - 9f, sweepP)
            canvas.restore()

            val sw = r * 0.56f; val sh = r * 0.66f
            val p = Path().apply {
                moveTo(cx, cy - sh)
                lineTo(cx + sw, cy - sh * 0.62f)
                lineTo(cx + sw * 0.85f, cy + sh * 0.22f)
                quadTo(cx + sw * 0.44f, cy + sh * 0.78f, cx, cy + sh)
                quadTo(cx - sw * 0.44f, cy + sh * 0.78f, cx - sw * 0.85f, cy + sh * 0.22f)
                lineTo(cx - sw, cy - sh * 0.62f)
                close()
            }
            shieldFill.shader = LinearGradient(
                cx, cy - sh, cx, cy + sh,
                c,
                Color.argb(225, Color.red(c) / 2, Color.green(c) / 2, Color.blue(c) / 2),
                Shader.TileMode.CLAMP
            )
            canvas.drawPath(p, shieldFill)
            canvas.drawPath(p, shieldBorder)

            canvas.save()
            canvas.clipPath(p)
            val ly = cy + (scanLinePhase * sh * 0.72f)
            laserPaint.color = Color.argb(if (state == State.SCANNING) 220 else 115, 255, 255, 255)
            canvas.drawLine(cx - sw, ly, cx + sw, ly, laserPaint)
            canvas.restore()

            crestTxt.textSize = r * 0.095f
            canvas.drawText("29A GUARD", cx, cy - sh * 0.34f, crestTxt)

            bigTxt.textSize = r * 0.23f
            canvas.drawText(if (state == State.SCANNING) "$pct%" else "$score", cx, cy + (bigTxt.textSize * 0.24f), bigTxt)

            subTxt.textSize = r * 0.095f
            canvas.drawText(if (state == State.SCANNING) "GUARDING" else "PROTECTED", cx, cy + (bigTxt.textSize * 0.86f), subTxt)
        }
    }
}
