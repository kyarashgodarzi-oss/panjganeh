package com.panjganeh.game.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.panjganeh.game.ads.TapsellManager

/**
 * پیدا کردن Activity از Context
 * (چون LocalContext.current معمولاً ContextThemeWrapper است)
 */
private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/**
 * کامپوننت بنر تبلیغاتی تپسل
 */
@Composable
fun TapsellBanner(
    modifier: Modifier = Modifier,
    tapsellManager: TapsellManager,
    zoneId: String = TapsellManager.BANNER_ZONE_ID
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    if (activity == null) {
        Log.w("TapsellBanner", "Activity not found in context - banner skipped")
        return
    }

    val container = remember(context) {
        FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    DisposableEffect(activity, zoneId) {
        Log.d("TapsellBanner", "Requesting banner ad...")
        tapsellManager.showBanner(
            activity = activity,
            container = container,
            zoneId = zoneId,
            onShown = { Log.d("TapsellBanner", "Banner shown successfully") },
            onError = { error -> Log.e("TapsellBanner", "Banner error: $error") }
        )

        onDispose {
            tapsellManager.destroyBanner(
                activity = activity,
                container = container
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { container },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
