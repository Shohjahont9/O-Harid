package lars_lion.dev.o_harid.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

val <T> T.exhaustive: T
    get() = this

fun Activity.hideKeyBoard(v: View) {
    val imm =
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v.windowToken, 0)
}

fun Fragment.hideKeyBoard(v: View) {
    val imm =
        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v.windowToken, 0)
}


fun View?.hideSoftKeyboardOnOutsideTouch(onEnd: () -> Unit = {}) =
    effectViewOnOutsideTouch<MaterialButton> {
        onEnd.invoke()
    }

@SuppressLint("ClickableViewAccessibility")
inline fun <reified T : Any> View?.effectViewOnOutsideTouch(crossinline doOnTouch: (() -> Unit) = {}) {
    this?.let {
        if (it !is T) {
            it.setOnTouchListener { _, _ ->
                doOnTouch.invoke()
                return@setOnTouchListener false
            }
        }

        if (it is ViewGroup) {
            for (i in 0 until it.childCount) {
                doOnTouch.invoke()
            }
        }
    }
}

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}