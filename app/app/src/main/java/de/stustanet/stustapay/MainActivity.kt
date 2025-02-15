package de.stustanet.stustapay

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.stustanet.stustapay.nfc.NFCHandler
import de.stustanet.stustapay.ui.Main

interface SysUiController {
    fun hideSystemUI()
    fun showSystemUI()
}

class MainActivity : ComponentActivity(), SysUiController {
    /** nfc interface connection */
    private var nfcHandler = NFCHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            remember {
                nfcHandler.context!!.scanRequest = mutableStateOf(false)
                nfcHandler.context.scanRequest!!
            }

            remember {
                nfcHandler.context!!.uid = mutableStateOf(0uL)
                nfcHandler.context.uid!!
            }
            
            Main(this, nfcHandler.context)
        }

        nfcHandler.onCreate()
    }

    public override fun onPause() {
        super.onPause()

        nfcHandler.onPause()
    }

    public override fun onResume() {
        super.onResume()

        nfcHandler.onResume()
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED
        ) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                nfcHandler.handleTag(intent.action!!, tag)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.hideSystemUI()
    }

    private var sysUiHidden = false

    @SuppressLint("ObsoleteSdkInt")
    override fun hideSystemUI() {
        if (sysUiHidden) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                window.navigationBarColor = getColor(R.color.black_semitransparent)
                it.hide(WindowInsets.Type.navigationBars())
            }
        } else {
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    // Do not let system steal touches for showing the navigation bar
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            // or View.SYSTEM_UI_FLAG_FULLSCREEN
                            // Keep the app content behind the bars even if user swipes them up
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            // make navbar translucent - do this already in hideSystemUI() so that the bar
            // is translucent if user swipes it up
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        sysUiHidden = true
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun showSystemUI() {
        if (!sysUiHidden) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.navigationBars())
        } else {
            // Shows the system bars by removing all the flags
            // except for the ones that make the content appear under the system bars.
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
        sysUiHidden = false
    }
}