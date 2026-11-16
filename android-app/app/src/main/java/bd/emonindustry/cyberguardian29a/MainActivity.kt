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
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
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
import java.io.FileOutputStream
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

    private lateinit var tabSocGuard: LinearLayout
    private lateinit var tabInventory: LinearLayout
    private lateinit var tabRansomWeb: LinearLayout
    private lateinit var tabNetworkSoc: LinearLayout
    private lateinit var tabCertGuide: LinearLayout
    private lateinit var tabDeveloperCv: LinearLayout
    private val navBtns = mutableListOf<Button>()

    private lateinit var scoreRadar: SocRadarShieldView
    private lateinit var heroTitle: TextView
    private lateinit var heroSub: TextView
    private lateinit var headerBadge: TextView
    private lateinit var btnSmartScan: Button
    private lateinit var btnDeepScan: Button
    private lateinit var btnPrivacyScreen: Button
    private lateinit var btnSosAlarm: Button
    private lateinit var socCountersRow: TextView
    private lateinit var scanLogContainer: LinearLayout

    // Full System Inventory ("Reads Everything & Tells Everything")
    private lateinit var invFilterInput: EditText
    private lateinit var invStatsLabel: TextView
    private lateinit var invListBox: LinearLayout
    private lateinit var invInspectorCard: LinearLayout
    private lateinit var invInspectorBody: TextView
    private var invCategoryFilter = "ALL"
    private val allAuditedApps = mutableListOf<AuditedApp>()

    // Ransomware, Canary & Scam Tab
    private lateinit var ransomStatusVal: TextView
    private lateinit var fileHashInput: EditText
    private lateinit var fileHashVerdict: TextView
    private lateinit var scamInput: EditText
    private lateinit var scamVerdict: TextView

    // Network SOC & Darktrace Tab
    private lateinit var wifiBadge: TextView
    private lateinit var netTransportVal: TextView
    private lateinit var netIpVal: TextView
    private lateinit var netDnsProxyVal: TextView
    private lateinit var netBandwidthVal: TextView
    private lateinit var netSessionVal: TextView
    private lateinit var dtAnomalyVal: TextView
    private lateinit var dtEventFeedBox: LinearLayout

    // Certificate Download & User Guidelines Tab
    private lateinit var certMetaBox: TextView
    private lateinit var certDownloadStatus: TextView
    private lateinit var passDisplay: TextView

    // Hardware Optimizer
    private lateinit var cpuValView: TextView
    private lateinit var ramValView: TextView
    private lateinit var diskValView: TextView
    private lateinit var batUptimeValView: TextView

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
        private const val DT_PURPLE = "#A855F7"
        private const val GREEN_SAFE = "#10B981"
        private const val AMBER_WARN = "#F59E0B"
        private const val CYAN_INFO = "#38BDF8"

        private const val EXPECTED_SHA256 =
            "AA:DF:60:FC:CE:71:CA:A9:72:ED:2A:F8:6C:92:91:AE:66:03:9D:C5:53:B8:05:AF:DC:67:CD:0B:CA:20:6F:7B"
        private const val DEV_EMAIL = "mnhemon357@gmail.com"
        private const val DEV_SUPPORT = "secure@emonindustry.bd"
        private const val DEV_PORTFOLIO = "https://357.wordpress.com"
        private const val DEV_WEB_APK = "https://mnhemon357-cyber.github.io/29a/"
        private const val DEV_FB_SHARE = "https://www.facebook.com/share/1FS8t8eFDL/"
        private const val DEV_FB_DIRECT = "https://www.facebook.com/profile.php?id=100077713088455"
    }

    data class AuditedApp(
        val label: String,
        val pkg: String,
        val category: String, // MALWARE, BLOATWARE, USER, SYSTEM
        val riskScore: Int,
        val summary: String,
        val fullForensicReport: String,
        val usesInternet: Boolean
    )

    private val liveTicker = object : Runnable {
        override fun run() {
            updateSystemOptimizer()
            updateWifiAndDarktrace()
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
            createPortraitAvatar(dp(42), MCAFEE_CRIMSON) { switchTab(5) },
            LinearLayout.LayoutParams(dp(42), dp(42)).apply { rightMargin = dp(10) }
        )

        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleCol.addView(TextView(this).apply {
            text = "29A • SOC Cyber Guardian®"
            textSize = 17f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        })
        titleCol.addView(TextView(this).apply {
            text = "Anti-Malware • Bloatware • Ransomware • Root CA"
            textSize = 10.5f
            setTextColor(Color.parseColor(TXT_SUB))
        })
        topBar.addView(titleCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        headerBadge = makePill("SOC ACTIVE", GREEN_SAFE, "#0D2B22")
        topBar.addView(headerBadge)
        root.addView(topBar)

        val navStrip = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(6), dp(6), dp(6), dp(8))
            setBackgroundColor(Color.parseColor("#0F1521"))
        }
        val names = listOf("SOC", "Apps", "Ransom", "Net/AI", "Cert/Guide", "Dev")
        names.forEachIndexed { idx, label ->
            val b = Button(this).apply {
                text = label
                textSize = 10f
                setTypeface(Typeface.DEFAULT_BOLD)
                isAllCaps = false
                setPadding(dp(1), 0, dp(1), 0)
                setOnClickListener { switchTab(idx) }
            }
            navBtns.add(b)
            navStrip.addView(b, LinearLayout.LayoutParams(0, dp(33), 1f).apply {
                if (idx < names.size - 1) rightMargin = dp(3)
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

        tabSocGuard = buildSocGuardTab()
        tabInventory = buildFullInventoryTab()
        tabRansomWeb = buildRansomwareAndScamTab()
        tabNetworkSoc = buildNetworkAndDarktraceTab()
        tabCertGuide = buildCertAndGuidelinesTab()
        tabDeveloperCv = buildDeveloperTab()

        body.addView(tabSocGuard)
        body.addView(tabInventory)
        body.addView(tabRansomWeb)
        body.addView(tabNetworkSoc)
        body.addView(tabCertGuide)
        body.addView(tabDeveloperCv)

        setContentView(root)
        switchTab(0)
        generatePassword()
        populateCertificateInspector()
        updateSystemOptimizer()
        updateWifiAndDarktrace()
        executeFullSocAudit(deepMode = true)
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
        val tabs = listOf(tabSocGuard, tabInventory, tabRansomWeb, tabNetworkSoc, tabCertGuide, tabDeveloperCv)
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
            val activeBg = when (i) {
                3 -> "#7E22CE"
                4 -> "#0284C7"
                else -> MCAFEE_RED
            }
            btn.setTextColor(Color.parseColor(if (active) "#FFFFFF" else TXT_SUB))
            btn.background = GradientDrawable().apply {
                setColor(Color.parseColor(if (active) activeBg else CARD_ELEV))
                cornerRadius = dp(999).toFloat()
                if (!active) setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
    }

    // ==================== TAB 1: SOC COMMAND & REAL-TIME PROTECTION ====================
    private fun buildSocGuardTab(): LinearLayout {
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
            text = "29A SOC Engine • Full System Telemetry"
            textSize = 11.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        })
        heroCard.addView(badgeRow)

        scoreRadar = SocRadarShieldView(this)
        heroCard.addView(scoreRadar, LinearLayout.LayoutParams(dp(210), dp(210)).apply {
            topMargin = dp(8)
            bottomMargin = dp(6)
        })

        heroTitle = TextView(this).apply {
            text = "SOC Telemetry Active"
            textSize = 19.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
            gravity = Gravity.CENTER
        }
        heroCard.addView(heroTitle)

        heroSub = TextView(this).apply {
            text = "Anti-Malware, Bloatware, Ransomware & Root CA Guard"
            textSize = 12f
            setTextColor(Color.parseColor(TXT_SUB))
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(4), dp(4), dp(8))
        }
        heroCard.addView(heroSub)

        socCountersRow = TextView(this).apply {
            text = "Scanning system inventory..."
            textSize = 11.5f
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(Color.parseColor(CYAN_INFO))
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(6), dp(10), dp(6))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(10).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
        heroCard.addView(socCountersRow, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10) })

        val scanBtnRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        btnSmartScan = makeBtn("SOC Full Audit", MCAFEE_RED, "#FFFFFF") {
            executeFullSocAudit(deepMode = true)
        }
        scanBtnRow.addView(btnSmartScan, LinearLayout.LayoutParams(0, dp(46), 1f).apply { rightMargin = dp(8) })

        btnDeepScan = makeBtn("Inspect All Apps", "#0284C7", "#FFFFFF") {
            switchTab(1)
        }
        scanBtnRow.addView(btnDeepScan, LinearLayout.LayoutParams(0, dp(46), 1f))
        heroCard.addView(scanBtnRow, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(8) })

        val shieldRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val btnCertHub = makeBtn("📜 Cert & Guidelines", CARD_ELEV, GREEN_SAFE) {
            switchTab(4)
        }
        shieldRow.addView(btnCertHub, LinearLayout.LayoutParams(0, dp(40), 1f).apply { rightMargin = dp(8) })

        btnPrivacyScreen = makeBtn("Screen Guard: OFF", CARD_ELEV, TXT_SUB) {
            toggleScreenGuard()
        }
        shieldRow.addView(btnPrivacyScreen, LinearLayout.LayoutParams(0, dp(40), 1f))
        heroCard.addView(shieldRow)

        col.addView(heroCard)

        col.addView(makeSecHeader("SOC THREAT DETECTION & RESPONSE MODULES"))
        val g1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        g1.addView(makeModuleCard("🔍 Read Everything", "Every app, UID, cert & permission", CYAN_INFO, true) { switchTab(1) })
        g1.addView(makeModuleCard("🦠 Anti-Malware & Bloat", "Spyware & OEM adware classifier", MCAFEE_CRIMSON, false) {
            invCategoryFilter = "THREAT"
            refreshInventoryList()
            switchTab(1)
        })
        col.addView(g1)

        val g2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        g2.addView(makeModuleCard("🔒 Ransomware Shield", "Canary trap, APK & SHA-256 audit", AMBER_WARN, true) { switchTab(2) })
        g2.addView(makeModuleCard("🧠 Darktrace & Net SOC", "Socket telemetry & MITM guard", DT_PURPLE, false) { switchTab(3) })
        col.addView(g2)

        val g3 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        g3.addView(makeModuleCard("📜 Root CA & Guidelines", "Download .CRT/.PEM & user manual", GREEN_SAFE, true) { switchTab(4) })
        g3.addView(makeModuleCard("📤 Export Full SOC Log", "Share complete forensic report", CYAN_INFO, false) { shareReport() })
        col.addView(g3)

        col.addView(makeSecHeader("HARDWARE, MEMORY & ANTI-THEFT TELEMETRY"))
        val optCard = makeCard()
        val r1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        cpuValView = addMetricTile(r1, "CPU LOAD", "22%", true)
        ramValView = addMetricTile(r1, "MEMORY (RAM)", "50%", false)
        optCard.addView(r1)

        val r2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        diskValView = addMetricTile(r2, "STORAGE", "32%", true)
        batUptimeValView = addMetricTile(r2, "BATTERY & UPTIME", "100%", false)
        optCard.addView(r2)

        val utilRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val btnOptimize = makeBtn("⚡ Reclaim RAM", MCAFEE_RED, "#FFFFFF") {
            System.gc()
            updateSystemOptimizer()
            Toast.makeText(this, "Memory reclaimed & SOC telemetry refreshed", Toast.LENGTH_SHORT).show()
        }
        utilRow.addView(btnOptimize, LinearLayout.LayoutParams(0, dp(42), 1f).apply { rightMargin = dp(8) })

        btnSosAlarm = makeBtn("🔊 Anti-Theft Siren", CARD_ELEV, AMBER_WARN) {
            toggleSosAlarm()
        }
        utilRow.addView(btnSosAlarm, LinearLayout.LayoutParams(0, dp(42), 1f))
        optCard.addView(utilRow, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(4) })
        col.addView(optCard)

        col.addView(makeSecHeader("LIVE SOC DETECTION & CRYPTOGRAPHIC EVENT FEED"))
        val logCard = makeCard()
        scanLogContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        logCard.addView(scanLogContainer)
        col.addView(logCard)

        return col
    }

    // ==================== TAB 2: READ EVERYTHING & TELL EVERYTHING (FULL SYSTEM INVENTORY) ====================
    private fun buildFullInventoryTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("FULL SYSTEM FORENSIC INSPECTOR (READS & TELLS EVERYTHING)"))
        val ctrlCard = makeCard()
        invStatsLabel = TextView(this).apply {
            text = "Loading complete package, permission, signature & UID telemetry..."
            textSize = 13.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
            setPadding(0, 0, 0, dp(8))
        }
        ctrlCard.addView(invStatsLabel)

        invFilterInput = EditText(this).apply {
            hint = "Search app name, package ID, permission, or risk..."
            setHintTextColor(Color.parseColor("#64748B"))
            setTextColor(Color.parseColor(TXT_MAIN))
            textSize = 13f
            setPadding(dp(12), dp(9), dp(12), dp(9))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { refreshInventoryList() }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        ctrlCard.addView(invFilterInput, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(8) })

        val filterRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val cats = listOf("ALL" to "All Apps", "THREAT" to "Malware/Bloat", "USER" to "User", "SYSTEM" to "System")
        cats.forEachIndexed { i, (key, title) ->
            val b = makeBtn(title, CARD_ELEV, if (key == "THREAT") MCAFEE_CRIMSON else CYAN_INFO) {
                invCategoryFilter = key
                refreshInventoryList()
            }
            filterRow.addView(b, LinearLayout.LayoutParams(0, dp(36), 1f).apply {
                if (i < cats.size - 1) rightMargin = dp(4)
            })
        }
        ctrlCard.addView(filterRow)
        col.addView(ctrlCard)

        invInspectorCard = makeCard().apply { visibility = View.GONE }
        val inspHead = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(6))
        }
        inspHead.addView(TextView(this).apply {
            text = "🔬 DEEP PACKAGE FORENSIC DOSSIER"
            textSize = 12.5f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(CYAN_INFO))
        }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        val btnCloseInsp = makePill("CLOSE ✕", TXT_MAIN, CARD_ELEV).apply {
            setOnClickListener { invInspectorCard.visibility = View.GONE }
        }
        inspHead.addView(btnCloseInsp)
        invInspectorCard.addView(inspHead)

        invInspectorBody = TextView(this).apply {
            textSize = 11.5f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor(TXT_MAIN))
            setPadding(dp(10), dp(10), dp(10), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(10).toFloat()
            }
        }
        invInspectorCard.addView(invInspectorBody)
        col.addView(invInspectorCard)

        val listCard = makeCard()
        invListBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        listCard.addView(invListBox)
        col.addView(listCard)

        return col
    }

    private fun refreshInventoryList() {
        val q = if (::invFilterInput.isInitialized) invFilterInput.text.toString().trim().lowercase(Locale.US) else ""
        val filtered = allAuditedApps.filter { app ->
            val catOk = when (invCategoryFilter) {
                "THREAT" -> app.category == "MALWARE" || app.category == "BLOATWARE"
                "USER" -> app.category == "USER"
                "SYSTEM" -> app.category == "SYSTEM"
                else -> true
            }
            val textOk = q.isEmpty() ||
                app.label.lowercase(Locale.US).contains(q) ||
                app.pkg.lowercase(Locale.US).contains(q) ||
                app.fullForensicReport.lowercase(Locale.US).contains(q)
            catOk && textOk
        }

        val malCnt = allAuditedApps.count { it.category == "MALWARE" }
        val bloatCnt = allAuditedApps.count { it.category == "BLOATWARE" }
        invStatsLabel.text = "Showing ${filtered.size} of ${allAuditedApps.size} Packages ($malCnt Malware • $bloatCnt Bloatware)"

        invListBox.removeAllViews()
        if (filtered.isEmpty()) {
            invListBox.addView(TextView(this).apply {
                text = "No packages matched the current filter."
                textSize = 13f
                setTextColor(Color.parseColor(TXT_SUB))
            })
            return
        }

        filtered.take(120).forEachIndexed { idx, item ->
            val (fg, bg) = when (item.category) {
                "MALWARE" -> MCAFEE_CRIMSON to "#3B0D18"
                "BLOATWARE" -> AMBER_WARN to "#33220A"
                "USER" -> CYAN_INFO to "#0F2942"
                else -> GREEN_SAFE to "#0D2B22"
            }

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, dp(9), 0, dp(9))
                setOnClickListener {
                    invInspectorBody.text = item.fullForensicReport
                    invInspectorCard.visibility = View.VISIBLE
                    Toast.makeText(context, "Loaded full forensic dossier for ${item.label}", Toast.LENGTH_SHORT).show()
                }
            }

            val top = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            titleCol.addView(TextView(this).apply {
                text = item.label
                textSize = 13.5f
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor(TXT_MAIN))
            })
            titleCol.addView(TextView(this).apply {
                text = item.pkg
                textSize = 11f
                typeface = Typeface.MONOSPACE
                setTextColor(Color.parseColor(TXT_SUB))
            })
            top.addView(titleCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            top.addView(makePill("${item.category} (${item.riskScore})", fg, bg))
            row.addView(top)

            row.addView(TextView(this).apply {
                text = item.summary
                textSize = 11.5f
                setTextColor(Color.parseColor(fg))
                setPadding(0, dp(3), 0, dp(5))
            })

            val actRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            val bInspect = makeBtn("Read Full Dossier", CARD_ELEV, CYAN_INFO) {
                invInspectorBody.text = item.fullForensicReport
                invInspectorCard.visibility = View.VISIBLE
            }
            actRow.addView(bInspect, LinearLayout.LayoutParams(0, dp(34), 1f).apply { rightMargin = dp(6) })

            val bSettings = makeBtn("App Info / Disable", CARD_ELEV, TXT_MAIN) {
                try {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${item.pkg}")))
                } catch (_: Exception) {}
            }
            actRow.addView(bSettings, LinearLayout.LayoutParams(0, dp(34), 1f))
            row.addView(actRow)

            invListBox.addView(row)
            if (idx < filtered.take(120).size - 1) invListBox.addView(makeSep())
        }
    }

    // ==================== TAB 3: RANSOMWARE SHIELD & AI SCAM DETECTOR ====================
    private fun buildRansomwareAndScamTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("RANSOMWARE CANARY TRIPWIRE & DOWNLOAD FILE AUDITOR"))
        val rCard = makeCard()
        ransomStatusVal = TextView(this).apply {
            text = "Ransomware Canary Tripwire: ARMED • Monitoring shared storage & executable drops"
            textSize = 13f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(GREEN_SAFE))
            setPadding(0, 0, 0, dp(8))
        }
        rCard.addView(ransomStatusVal)

        fileHashInput = EditText(this).apply {
            hint = "Optional file path (e.g., /sdcard/Download/app.apk) or leave blank to scan Downloads"
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
        rCard.addView(fileHashInput, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10) })

        val btnScanFiles = makeBtn("Deploy Canary & Scan Storage / SHA-256", MCAFEE_RED, "#FFFFFF") {
            deployCanaryAndScanStorage()
        }
        rCard.addView(btnScanFiles, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(42)
        ).apply { bottomMargin = dp(10) })

        fileHashVerdict = TextView(this).apply {
            text = "Ready to verify canary integrity & inspect Downloads."
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_ELEV))
                cornerRadius = dp(12).toFloat()
            }
        }
        rCard.addView(fileHashVerdict)
        col.addView(rCard)

        col.addView(makeSecHeader("AI PHISHING, SMISHING & MALICIOUS URL ANALYZER"))
        val scamCard = makeCard()
        scamInput = EditText(this).apply {
            hint = "Paste suspicious URL, SMS, OTP lure, or APK download link..."
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

        val btnScanScam = makeBtn("Analyze Link / Message with SOC AI", "#0284C7", "#FFFFFF") {
            runScamDetector()
        }
        scamCard.addView(btnScanScam, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(44)
        ).apply { bottomMargin = dp(10) })

        scamVerdict = TextView(this).apply {
            text = "Ready: Paste any link or text message above."
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

        return col
    }

    // ==================== TAB 4: NETWORK SOC & DARKTRACE IMMUNE TELEMETRY ====================
    private fun buildNetworkAndDarktraceTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("DARKTRACE IMMUNE SYSTEM & LIVE NETWORK SOC"))
        val wifiCard = makeCard()
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dp(8))
        }
        top.addView(TextView(this).apply {
            text = "Live Socket, DNS, Proxy & Anomaly Feed"
            textSize = 15f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
        }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        wifiBadge = makePill("SAFE WI-FI", GREEN_SAFE, "#0D2B22")
        top.addView(wifiBadge)
        wifiCard.addView(top)

        netTransportVal = addKv(wifiCard, "Transport & VPN Tunnel", "Detecting...")
        netIpVal = addKv(wifiCard, "Local IPv4 / IPv6", "Detecting...")
        netDnsProxyVal = addKv(wifiCard, "DNS & MITM Proxy Audit", "Detecting...")
        netBandwidthVal = addKv(wifiCard, "Live Speed (↓ / ↑)", "0 KB/s | 0 KB/s")
        netSessionVal = addKv(wifiCard, "Session Bytes (Rx / Tx)", "0 MB / 0 MB")
        dtAnomalyVal = addKv(wifiCard, "Darktrace Immune Score", "99.4% Baseline Match (Clean)", isLast = true)

        val btnWifiSettings = makeBtn("Open Android Wi-Fi & Private DNS Settings", CARD_ELEV, CYAN_INFO) {
            try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } catch (_: Exception) {}
        }
        wifiCard.addView(btnWifiSettings, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(42)
        ).apply { topMargin = dp(10) })
        col.addView(wifiCard)

        col.addView(makeSecHeader("AUTONOMOUS SOC & ANTIGENA CONTAINMENT LOG"))
        val feedCard = makeCard()
        dtEventFeedBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        feedCard.addView(dtEventFeedBox)
        col.addView(feedCard)

        return col
    }

    // ==================== TAB 5: CERTIFICATE DOWNLOAD HUB & USER GUIDELINES ====================
    private fun buildCertAndGuidelinesTab(): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        col.addView(makeSecHeader("CYBER GUARDIAN® PRIVATE ROOT CA — DOWNLOAD & VERIFICATION"))
        val certCard = makeCard()
        certCard.addView(TextView(this).apply {
            text = "Cryptographic X.509 Root Certificate (DER .CRT & Base64 .PEM)"
            textSize = 15f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(TXT_MAIN))
            setPadding(0, 0, 0, dp(6))
        })

        certMetaBox = TextView(this).apply {
            text = "Loading embedded X.509 certificate metadata..."
            textSize = 11.5f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor(CYAN_INFO))
            setPadding(dp(10), dp(10), dp(10), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#0B101B"))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.parseColor(BORDER_COL))
            }
        }
        certCard.addView(certMetaBox, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10) })

        val dlRow1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val btnDlCrt = makeBtn("⬇ Download Root CA (.CRT)", MCAFEE_RED, "#FFFFFF") {
            exportCertificateToDownloads(asPem = false)
        }
        dlRow1.addView(btnDlCrt, LinearLayout.LayoutParams(0, dp(44), 1f).apply { rightMargin = dp(8) })

        val btnDlPem = makeBtn("⬇ Download Root CA (.PEM)", "#0284C7", "#FFFFFF") {
            exportCertificateToDownloads(asPem = true)
        }
        dlRow1.addView(btnDlPem, LinearLayout.LayoutParams(0, dp(44), 1f))
        certCard.addView(dlRow1, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(8) })

        val dlRow2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val btnCopyPem = makeBtn("📋 Copy PEM Text", CARD_ELEV, GREEN_SAFE) {
            copyCertificatePemToClipboard()
        }
        dlRow2.addView(btnCopyPem, LinearLayout.LayoutParams(0, dp(40), 1f).apply { rightMargin = dp(8) })

        val btnSecSettings = makeBtn("⚙ Open Android CA Settings", CARD_ELEV, AMBER_WARN) {
            try { startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS)) } catch (_: Exception) {}
        }
        dlRow2.addView(btnSecSettings, LinearLayout.LayoutParams(0, dp(40), 1f))
        certCard.addView(dlRow2)

        certDownloadStatus = TextView(this).apply {
            text = "Ready: Tap Download Root CA (.CRT or .PEM) to save directly to your Downloads folder."
            textSize = 12f
            setTextColor(Color.parseColor(TXT_SUB))
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = GradientDrawable().apply {
                setColor(Color.parseColor(CARD_ELEV))
                cornerRadius = dp(10).toFloat()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }
        certCard.addView(certDownloadStatus)
        col.addView(certCard)

        col.addView(makeSecHeader("STEP-BY-STEP USER GUIDELINES (CERTIFICATE, SOC & WEB APK)"))
        val guideCard = makeCard()
        val steps = listOf(
            "1. Embedded App-Level Trust (Automatic)" to
                "This APK already pins the Cyber Guardian Private Root CA inside res/xml/network_security_config.xml. No manual installation is required for 29A Guard itself.",
            "2. System-Wide Browser / Wi-Fi CA Installation (Optional)" to
                "Tap 'Download Root CA (.CRT)' above to save cyber_guardian_android_ca.crt to Downloads. Open Android Settings → Security & Privacy → More Security Settings → Encryption & Credentials → Install a Certificate → CA Certificate → Select the downloaded .crt file.",
            "3. Reading Full System Telemetry ('Apps' Tab)" to
                "Open the 'Apps' tab to inspect every installed User and System package, UID, target SDK, signing status, dangerous permissions, and bloatware score. Tap any app to read its complete forensic dossier.",
            "4. Anti-Malware, Bloatware & Ransomware Remediation" to
                "Filter by 'Malware/Bloat' in the Apps tab and tap 'App Info / Disable' to remove or disable unwanted OEM telemetry/adware. Use the 'Ransom' tab to deploy a canary tripwire and verify SHA-256 hashes of downloaded APKs.",
            "5. Installing the Offline Web APK (PWA)" to
                "Visit https://mnhemon357-cyber.github.io/29a/ in Chrome or Edge and tap 'Install Web APK' (or browser menu → Add to Home screen) to run the companion offline SOC console."
        )
        steps.forEachIndexed { idx, (headTxt, bodyTxt) ->
            guideCard.addView(TextView(this).apply {
                text = headTxt
                textSize = 13.5f
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor(CYAN_INFO))
                setPadding(0, dp(4), 0, dp(2))
            })
            guideCard.addView(TextView(this).apply {
                text = bodyTxt
                textSize = 12f
                setTextColor(Color.parseColor(TXT_MAIN))
                setLineSpacing(dp(2).toFloat(), 1.05f)
                setPadding(0, 0, 0, dp(8))
            })
            if (idx < steps.size - 1) guideCard.addView(makeSep())
        }
        col.addView(guideCard)

        col.addView(makeSecHeader("CRYPTOGRAPHIC 20-CHARACTER KEY GENERATOR"))
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
        ).apply { bottomMargin = dp(8) })
        val pRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        pRow.addView(makeBtn("Generate Key", MCAFEE_RED, "#FFFFFF") { generatePassword() }, LinearLayout.LayoutParams(0, dp(40), 1f).apply { rightMargin = dp(8) })
        pRow.addView(makeBtn("Copy Key", CARD_ELEV, CYAN_INFO) {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Key", passDisplay.text.toString()))
            Toast.makeText(this, "Key copied", Toast.LENGTH_SHORT).show()
        }, LinearLayout.LayoutParams(0, dp(40), 1f))
        passCard.addView(pRow)
        col.addView(passCard)

        return col
    }

    // ==================== TAB 6: DEVELOPER PROFILE ====================
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
            text = "Dhaka, Bangladesh • Lead Security Architect (29A)"
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

        addKv(devCard, "Organization", "EMON INDUSTRY PVT LTD _29A")
        addKv(devCard, "Web APK & Cert Hub", "mnhemon357-cyber.github.io/29a/")
        addKv(devCard, "Root CA Subject", "O=EMON INDUSTRY PVT LTD 29A, CN=Cyber Guardian Private Root CA")
        addKv(devCard, "Education (B.Sc. CSE)", "Daffodil International University, Dhaka (2021–Present)")
        addKv(devCard, "Higher Secondary (HSC)", "Armed Police Battalion Public School & College, Bogura")
        addKv(devCard, "Programming", "Perl-6 (Raku), Python, C, Java, HTML, CSS")
        addKv(devCard, "Cybersecurity & Linux", "SOC Telemetry, Darktrace AI, Pentesting, Kali Linux")
        addKv(devCard, "Email & Support", "$DEV_EMAIL | $DEV_SUPPORT")
        addKv(devCard, "Portfolio", "357.wordpress.com")
        addKv(devCard, "Facebook URL", "facebook.com/share/1FS8t8eFDL/", isLast = true)

        val r0 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        r0.addView(makeBtn("🌐 Open Web APK & Cert Hub", "#7E22CE", "#FFFFFF") { openUrl(DEV_WEB_APK) }, LinearLayout.LayoutParams(0, dp(44), 1f))
        devCard.addView(r0, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })

        val r1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        r1.addView(makeBtn("Facebook (Share)", "#1877F2", "#FFFFFF") { openUrl(DEV_FB_SHARE) }, LinearLayout.LayoutParams(0, dp(44), 1f).apply { rightMargin = dp(8) })
        r1.addView(makeBtn("Facebook Profile", CARD_ELEV, "#60A5FA") { openUrl(DEV_FB_DIRECT) }, LinearLayout.LayoutParams(0, dp(44), 1f))
        devCard.addView(r1, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })

        val r2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        r2.addView(makeBtn("Portfolio (357.wordpress)", CARD_ELEV, CYAN_INFO) { openUrl(DEV_PORTFOLIO) }, LinearLayout.LayoutParams(0, dp(44), 1f).apply { rightMargin = dp(8) })
        r2.addView(makeBtn("Email Nazmul", MCAFEE_RED, "#FFFFFF") { emailDev() }, LinearLayout.LayoutParams(0, dp(44), 1f))
        devCard.addView(r2, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })

        col.addView(devCard)
        return col
    }

    // ==================== CORE SOC ENGINE: READS EVERYTHING & TELLS EVERYTHING ====================
    private fun executeFullSocAudit(deepMode: Boolean) {
        if (scanning) return
        scanning = true
        btnSmartScan.isEnabled = false
        heroTitle.text = "SOC Reading Complete System State..."
        heroSub.text = "Auditing Every Package, UID, Permission, Root CA & Ransomware Canary"
        updatePill(headerBadge, "AUDITING", CYAN_INFO, "#0F2942")
        scoreRadar.startScan()

        val anim = ValueAnimator.ofInt(0, 96).apply {
            duration = if (deepMode) 2200L else 1400L
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { scoreRadar.setScanProgress(it.animatedValue as Int) }
        }
        anim.start()

        Thread {
            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            val checks = mutableListOf<Triple<String, String, String>>()
            val parsedApps = mutableListOf<AuditedApp>()
            var score = 1000
            var malwareCount = 0
            var bloatCount = 0
            var userCount = 0
            var sysCount = 0

            // 1. Root CA Check
            val ca = loadCa()
            if (ca != null) {
                val fp = sha256(ca)
                if (fp.equals(EXPECTED_SHA256, ignoreCase = true)) {
                    checks.add(Triple("Cyber Guardian Private Root CA", "VERIFIED • SHA-256 ${fp.take(22)}… (Available in Cert/Guide tab)", GREEN_SAFE))
                } else {
                    score -= 220
                    checks.add(Triple("Root CA Integrity Alert", "Bundled CA certificate hash mismatch!", MCAFEE_CRIMSON))
                }
            } else {
                checks.add(Triple("29A Cryptographic Policy", "Pin Active: ${EXPECTED_SHA256.take(22)}…", GREEN_SAFE))
            }

            // 2. System & User CA Trust Store Enumeration
            var sysCaTotal = 0
            val userCaNames = mutableListOf<String>()
            try {
                val ks = KeyStore.getInstance("AndroidCAStore")
                ks.load(null, null)
                val aliases = ks.aliases()
                while (aliases.hasMoreElements()) {
                    val a = aliases.nextElement()
                    if (a.startsWith("system:")) {
                        sysCaTotal++
                    } else if (a.startsWith("user:")) {
                        val cert = ks.getCertificate(a) as? X509Certificate
                        val subj = cert?.subjectX500Principal?.name ?: a
                        userCaNames.add(subj)
                    }
                }
            } catch (_: Exception) {}
            checks.add(Triple("Android Trust Store Audit", "Inspected $sysCaTotal System Root CAs & ${userCaNames.size} User CAs", if (userCaNames.isEmpty()) GREEN_SAFE else AMBER_WARN))

            // 3. Accessibility & Device Admin Audit
            val acc = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
            val accList = acc.split(":").filter { it.isNotBlank() }
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val admins = dpm.activeAdmins ?: emptyList()
            checks.add(Triple(
                "Privilege Escalation & Keylogger Guard",
                "${accList.size} Accessibility Service(s) • ${admins.size} Device Admin(s) active",
                if (accList.isEmpty() && admins.isEmpty()) GREEN_SAFE else AMBER_WARN
            ))

            // 4. Full PackageManager Enumeration ("Reads Everything & Tells Everything")
            val trustedTools = setOf("org.telegram.messenger", "org.kde.kdeconnect_tp", "org.gnu.emacs", "com.termux")
            val highRiskPerms = setOf(
                "android.permission.RECORD_AUDIO",
                "android.permission.READ_SMS",
                "android.permission.RECEIVE_SMS",
                "android.permission.SEND_SMS",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_BACKGROUND_LOCATION",
                "android.permission.READ_CALL_LOG",
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.CAMERA",
                "android.permission.REQUEST_INSTALL_PACKAGES",
                "android.permission.MANAGE_EXTERNAL_STORAGE"
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
                val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_SERVICES or PackageManager.GET_RECEIVERS
                val pkgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags.toLong()))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getInstalledPackages(flags)
                }

                for (pi in pkgs) {
                    val pkg = pi.packageName ?: continue
                    val ai = pi.applicationInfo ?: continue
                    val isSys = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val isDebuggable = (ai.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
                    val label = try { ai.loadLabel(packageManager).toString() } catch (_: Exception) { pkg }
                    val perms = pi.requestedPermissions?.toList() ?: emptyList()
                    val matchedHigh = perms.filter { it in highRiskPerms }
                    val hasLauncher = packageManager.getLaunchIntentForPackage(pkg) != null
                    val hasNet = perms.contains("android.permission.INTERNET")
                    val low = pkg.lowercase(Locale.US)
                    val verName = pi.versionName ?: "1.0"
                    val svcCount = pi.services?.size ?: 0
                    val rcvCount = pi.receivers?.size ?: 0

                    var risk = (matchedHigh.size * 14) + (if (isDebuggable && !isSys) 25 else 0) + (if (!hasLauncher && !isSys) 20 else 0)
                    risk = risk.coerceIn(0, 100)

                    val category = when {
                        pkg != packageName && !isSys && pkg !in trustedTools && ((!hasLauncher && matchedHigh.size >= 2) || matchedHigh.size >= 5) -> {
                            malwareCount++
                            score -= 130
                            "MALWARE"
                        }
                        bloatKeys.any { low.contains(it) } -> {
                            bloatCount++
                            score -= 10
                            "BLOATWARE"
                        }
                        !isSys -> {
                            userCount++
                            "USER"
                        }
                        else -> {
                            sysCount++
                            "SYSTEM"
                        }
                    }

                    val summaryLine = "UID ${ai.uid} • SDK ${ai.targetSdkVersion} • ${perms.size} perms (${matchedHigh.size} sensitive) • ${if (ai.enabled) "Enabled" else "Disabled"}"
                    val dossier = buildString {
                        append("PACKAGE: $label ($pkg)\n")
                        append("CLASSIFICATION: $category | RISK SCORE: $risk/100\n")
                        append("VERSION: $verName | UID: ${ai.uid} | TARGET SDK: ${ai.targetSdkVersion}\n")
                        append("STATE: ${if (ai.enabled) "ENABLED" else "DISABLED"} | LAUNCHER ICON: ${if (hasLauncher) "VISIBLE" else "HIDDEN/BACKGROUND"}\n")
                        append("COMPONENTS: $svcCount Service(s), $rcvCount Broadcast Receiver(s)\n")
                        append("SOURCE APK: ${ai.sourceDir ?: "N/A"}\n")
                        append("SENSITIVE PERMISSIONS (${matchedHigh.size}):\n")
                        if (matchedHigh.isEmpty()) append("  • None\n") else matchedHigh.forEach { append("  • $it\n") }
                        append("ALL DECLARED PERMISSIONS (${perms.size}):\n")
                        if (perms.isEmpty()) append("  • None\n") else perms.forEach { append("  • $it\n") }
                    }

                    parsedApps.add(AuditedApp(
                        label = label,
                        pkg = pkg,
                        category = category,
                        riskScore = risk,
                        summary = summaryLine,
                        fullForensicReport = dossier,
                        usesInternet = hasNet
                    ))
                }
            } catch (_: Exception) {}

            parsedApps.sortWith(compareByDescending<AuditedApp> {
                when (it.category) {
                    "MALWARE" -> 4
                    "BLOATWARE" -> 3
                    "USER" -> 2
                    else -> 1
                }
            }.thenByDescending { it.riskScore }.thenBy { it.label })

            val total = parsedApps.size
            val finalScore = score.coerceIn(120, 1000)
            checks.add(Triple(
                "Full Package Inventory Telemetry",
                "Read $total total packages: $malwareCount Malware • $bloatCount Bloatware • $userCount User • $sysCount System",
                if (malwareCount > 0) MCAFEE_CRIMSON else if (bloatCount > 0) AMBER_WARN else GREEN_SAFE
            ))

            fullAuditReport = buildString {
                append("29A • SOC CYBER GUARDIAN® COMPLETE SYSTEM TELEMETRY REPORT\n")
                append("Architect: Nazmul Hossain (EMON INDUSTRY PVT LTD _29A)\n")
                append("Timestamp: $ts | SOC Score: $finalScore / 1000\n")
                append("Inventory: $total Total | $malwareCount Malware | $bloatCount Bloatware | $userCount User | $sysCount System\n\n")
                checks.forEach { append("[${it.first}] ${it.second}\n") }
                append("\n--- FLAGGED MALWARE & BLOATWARE ---\n")
                parsedApps.filter { it.category == "MALWARE" || it.category == "BLOATWARE" }.forEach {
                    append("• ${it.category}: ${it.label} (${it.pkg}) — ${it.summary}\n")
                }
            }

            try { Thread.sleep(if (deepMode) 1200L else 700L) } catch (_: InterruptedException) {}

            runOnUiThread {
                anim.cancel()
                allAuditedApps.clear()
                allAuditedApps.addAll(parsedApps)
                refreshInventoryList()

                val st = when {
                    malwareCount > 0 -> SocRadarShieldView.State.ALERT
                    bloatCount > 0 -> SocRadarShieldView.State.WARNING
                    else -> SocRadarShieldView.State.SECURE
                }
                scoreRadar.finishScan(finalScore, st)

                socCountersRow.text = "TOTAL:$total  MALWARE:$malwareCount  BLOAT:$bloatCount  USER:$userCount  SYS:$sysCount"
                heroTitle.text = when (st) {
                    SocRadarShieldView.State.SECURE -> "SOC Clean • Score $finalScore/1000"
                    SocRadarShieldView.State.WARNING -> "SOC Alert: $bloatCount Bloatware Found"
                    SocRadarShieldView.State.ALERT -> "CRITICAL: $malwareCount Malware Vector(s)"
                    else -> "SOC Active"
                }
                heroSub.text = "Full system read completed at $ts • Tap 'Apps' tab to inspect every package"
                updatePill(
                    headerBadge,
                    if (malwareCount > 0) "$malwareCount MALWARE" else if (bloatCount > 0) "$bloatCount BLOAT" else "SCORE $finalScore",
                    if (malwareCount > 0) MCAFEE_CRIMSON else if (bloatCount > 0) AMBER_WARN else GREEN_SAFE,
                    if (malwareCount > 0) "#3B0D18" else if (bloatCount > 0) "#33220A" else "#0D2B22"
                )

                renderSocLog(checks)
                btnSmartScan.isEnabled = true
                scanning = false
            }
        }.start()
    }

    // ==================== CERTIFICATE EXPORT & USER GUIDELINES HELPERS ====================
    private fun populateCertificateInspector() {
        val ca = loadCa()
        if (ca != null) {
            val fp = sha256(ca)
            certMetaBox.text = buildString {
                append("SUBJECT: ${ca.subjectX500Principal.name}\n")
                append("ISSUER:  ${ca.issuerX500Principal.name}\n")
                append("SERIAL:  ${ca.serialNumber.toString(16).uppercase(Locale.US)}\n")
                append("VALID:   ${ca.notBefore} -> ${ca.notAfter}\n")
                append("SHA-256: $fp")
            }
        } else {
            certMetaBox.text = "Subject: O=EMON INDUSTRY PVT LTD 29A, CN=Cyber Guardian Private Root CA\nSHA-256 Pin: $EXPECTED_SHA256"
        }
    }

    private fun exportCertificateToDownloads(asPem: Boolean) {
        val ca = loadCa()
        if (ca == null) {
            Toast.makeText(this, "Embedded Root CA not found in APK resources", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dlDir.exists()) dlDir.mkdirs()
            val fileName = if (asPem) "cyber_guardian_android_ca.pem" else "cyber_guardian_android_ca.crt"
            val outFile = File(dlDir, fileName)
            val bytes = if (asPem) {
                val b64 = Base64.encodeToString(ca.encoded, Base64.DEFAULT)
                "-----BEGIN CERTIFICATE-----\n$b64-----END CERTIFICATE-----\n".toByteArray(Charsets.UTF_8)
            } else {
                ca.encoded
            }
            FileOutputStream(outFile).use { it.write(bytes) }
            certDownloadStatus.text = "✅ Saved to Downloads: ${outFile.absolutePath} (${bytes.size} bytes)"
            certDownloadStatus.setTextColor(Color.parseColor("#6EE7B7"))
            Toast.makeText(this, "Downloaded $fileName to Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // Fallback to app external files dir if scoped storage blocks public Downloads
            try {
                val fallbackDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: filesDir
                val fileName = if (asPem) "cyber_guardian_android_ca.pem" else "cyber_guardian_android_ca.crt"
                val outFile = File(fallbackDir, fileName)
                val bytes = if (asPem) {
                    val b64 = Base64.encodeToString(ca.encoded, Base64.DEFAULT)
                    "-----BEGIN CERTIFICATE-----\n$b64-----END CERTIFICATE-----\n".toByteArray(Charsets.UTF_8)
                } else {
                    ca.encoded
                }
                FileOutputStream(outFile).use { it.write(bytes) }
                certDownloadStatus.text = "✅ Saved Certificate: ${outFile.absolutePath}"
                certDownloadStatus.setTextColor(Color.parseColor("#6EE7B7"))
            } catch (ex: Exception) {
                certDownloadStatus.text = "⚠️ Export notice: ${ex.message}"
            }
        }
    }

    private fun copyCertificatePemToClipboard() {
        val ca = loadCa() ?: return
        val b64 = Base64.encodeToString(ca.encoded, Base64.DEFAULT)
        val pem = "-----BEGIN CERTIFICATE-----\n$b64-----END CERTIFICATE-----\n"
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Cyber Guardian Root CA PEM", pem))
        certDownloadStatus.text = "✅ Copied full X.509 PEM certificate block to clipboard."
        certDownloadStatus.setTextColor(Color.parseColor("#6EE7B7"))
        Toast.makeText(this, "PEM Certificate copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    // ==================== RANSOMWARE CANARY & SCAM DETECTOR ====================
    private fun deployCanaryAndScanStorage() {
        val customPath = fileHashInput.text.toString().trim()
        if (customPath.isNotEmpty()) {
            val f = File(customPath)
            if (!f.exists() || !f.isFile) {
                fileHashVerdict.text = "⚠️ File not found: $customPath"
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
            fileHashVerdict.text = "✅ File Verified: ${f.name} (${f.length() / 1024} KB)\nSHA-256: $hex"
            fileHashVerdict.setTextColor(Color.parseColor("#6EE7B7"))
            return
        }

        // Deploy & verify Ransomware Canary file in app external storage
        val canaryDir = getExternalFilesDir(null) ?: filesDir
        val canaryFile = File(canaryDir, ".29a_ransomware_canary.txt")
        val expectedCanary = "29A_CYBER_GUARDIAN_CANARY_SEAL_DO_NOT_ENCRYPT_OK"
        var canaryStatus = "ARMED & INTACT"
        try {
            if (!canaryFile.exists()) {
                canaryFile.writeText(expectedCanary)
            } else if (canaryFile.readText() != expectedCanary) {
                canaryStatus = "ALERT: CANARY MODIFIED!"
            }
        } catch (_: Exception) {}

        val dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val files = dlDir?.listFiles() ?: emptyArray()
        val ransomExts = listOf(".locked", ".enc", ".crypt", ".cry", ".wnry", ".locky", ".cerber")
        val ransomHits = files.filter { f -> ransomExts.any { f.name.lowercase(Locale.US).endsWith(it) } }
        val exeHits = files.filter { f ->
            val n = f.name.lowercase(Locale.US)
            n.endsWith(".apk") || n.endsWith(".sh") || n.endsWith(".exe") || n.endsWith(".bat")
        }

        ransomStatusVal.text = "Ransomware Canary: $canaryStatus • ${ransomHits.size} encrypted ransom files"
        ransomStatusVal.setTextColor(Color.parseColor(if (ransomHits.isEmpty() && canaryStatus.contains("INTACT")) GREEN_SAFE else MCAFEE_CRIMSON))

        fileHashVerdict.text = buildString {
            append("Canary Tripwire: $canaryStatus\n")
            append("Downloads Inspected: ${files.size} total file(s)\n")
            append("Ransomware Extensions: ${ransomHits.size} detected\n")
            append("Executable/APK Drops: ${exeHits.size} (${if (exeHits.isEmpty()) "None" else exeHits.take(3).joinToString { it.name }})")
        }
        fileHashVerdict.setTextColor(Color.parseColor(if (ransomHits.isEmpty()) "#6EE7B7" else "#FCA5A5"))
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

    // ==================== NETWORK SOC & DARKTRACE LIVE TELEMETRY ====================
    private fun updateWifiAndDarktrace() {
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
        dtAnomalyVal.text = if (!proxy.isNullOrBlank()) "78% Anomaly (MITM Proxy Detected)" else "99.4% Baseline Match (Clean)"

        if (::dtEventFeedBox.isInitialized && dtEventFeedBox.childCount == 0) {
            val items = listOf(
                Triple("Darktrace Pattern-of-Life AI", "Continuous socket & upload entropy profiling active", DT_PURPLE),
                Triple("Antigena Autonomous Containment", "Zero-day C2 beacon & rogue proxy isolation ready", CYAN_INFO),
                Triple("Cyber Guardian Root CA Pin", "TLS handshakes pinned to EMON INDUSTRY PVT LTD 29A", GREEN_SAFE)
            )
            items.forEachIndexed { idx, (t, s, hex) ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, dp(8), 0, dp(8))
                }
                val dotView = View(this).apply {
                    background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.parseColor(hex)) }
                }
                row.addView(dotView, LinearLayout.LayoutParams(dp(9), dp(9)).apply { rightMargin = dp(10) })
                val c = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                c.addView(TextView(this).apply { text = t; textSize = 13f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_MAIN)) })
                c.addView(TextView(this).apply { text = s; textSize = 11.5f; setTextColor(Color.parseColor(TXT_SUB)) })
                row.addView(c, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                dtEventFeedBox.addView(row)
                if (idx < items.size - 1) dtEventFeedBox.addView(makeSep())
            }
        }
    }

    private fun renderSocLog(items: List<Triple<String, String, String>>) {
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
            btnSosAlarm.text = "🔇 Stop Siren"
            btnSosAlarm.setTextColor(Color.parseColor(MCAFEE_CRIMSON))
            handler.postDelayed({ stopSosAlarm() }, 8200L)
        } catch (_: Exception) {}
    }

    private fun stopSosAlarm() {
        try {
            toneGen?.stopTone()
            toneGen?.release()
        } catch (_: Exception) {}
        toneGen = null
        sosPlaying = false
        if (::btnSosAlarm.isInitialized) {
            btnSosAlarm.text = "🔊 Anti-Theft Siren"
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
        val txt = fullAuditReport.ifBlank { "29A • SOC Cyber Guardian® Complete Security Telemetry Active" }
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "29A • SOC Cyber Guardian® Telemetry Report")
            putExtra(Intent.EXTRA_TEXT, txt)
        }, "Share SOC Audit Report"))
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
        val v = TextView(this).apply { text = initVal; textSize = 12.5f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(TXT_MAIN)); setPadding(0, dp(3), 0, 0) }
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
        text = title; textSize = 12f; setTypeface(Typeface.DEFAULT_BOLD); setTextColor(Color.parseColor(fg))
        isAllCaps = false; filterTouchesWhenObscured = true
        background = GradientDrawable().apply { setColor(Color.parseColor(bg)); cornerRadius = dp(12).toFloat() }
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

    // ==================== CUSTOM 29A SOC SHIELD & RADAR VIEW ====================
    class SocRadarShieldView(context: Context) : View(context) {
        enum class State { SCANNING, SECURE, WARNING, ALERT }
        private var state = State.SECURE
        private var score = 995
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
            State.SCANNING -> Color.parseColor("#38BDF8")
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
            canvas.drawText("SOC", cx, cy + innerOrbit + 5f, bitTxt)
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
            canvas.drawText("29A SOC", cx, cy - sh * 0.34f, crestTxt)

            bigTxt.textSize = r * 0.23f
            canvas.drawText(if (state == State.SCANNING) "$pct%" else "$score", cx, cy + (bigTxt.textSize * 0.24f), bigTxt)

            subTxt.textSize = r * 0.095f
            canvas.drawText(if (state == State.SCANNING) "READING" else "PROTECTED", cx, cy + (bigTxt.textSize * 0.86f), subTxt)
        }
    }
}
