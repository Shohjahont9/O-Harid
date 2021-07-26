package lars_lion.dev.o_harid.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.android.material.snackbar.Snackbar
import lars_lion.dev.o_harid.R
import java.io.File


fun Fragment.toast(message: String, isLong: Boolean = false) {
    if (!isLong)
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    else
        Toast.makeText(context, message, Toast.LENGTH_LONG)
            .show()
}

fun Context.toast(message: String, isLong: Boolean = false) {
    if (!isLong)
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
            .show()
    else
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
}


fun File.copyTo(file: File) {
    inputStream().use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

@Nullable
fun <T> getListener(fragment: Fragment, listenerClass: Class<T>): T? {
    var listener: T? = null
    if (listenerClass.isInstance(fragment.parentFragment)) {
        listener = listenerClass.cast(fragment.parentFragment)
    } else if (listenerClass.isInstance(fragment.activity)) {
        listener = listenerClass.cast(fragment.activity)
    }
    return listener
}

fun mlogging(msg: String) = Log.i("MY_LOG", msg)

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.menable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.9f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snacbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snacbar.setAction("Retry") {
            it()
        }
    }
    snacbar.show()
}

fun View.topSnackbar(message: String) {
    val snack: Snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    val view = snack.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    snack.show()
}

fun View.slideDown(animTime: Long, startOffset: Long) {
    val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideDown)
}

fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null,
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}

fun Activity.snackbar(view: View, message: String, action: (() -> Unit)? = null) {
    val snacbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    action?.let {
        snacbar.setAction("Retry") {
            it()
        }
    }
    snacbar.show()
}


