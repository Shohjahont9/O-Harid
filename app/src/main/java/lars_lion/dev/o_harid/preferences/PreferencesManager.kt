package lars_lion.dev.o_harid.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import uz.fizmasoft.faceapp.preferences.PreferencesDelegate

class PreferencesManager(
    private val context: Context
) {

    private val preferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
    }

    var isAuthVerified by PreferencesDelegate(preferences, IS_AUTH_VERIFIED, false)
    var name by PreferencesDelegate(preferences, NAME, "empty")
    var token by PreferencesDelegate(preferences, TOKEN, "empty")
    var bookId by PreferencesDelegate(preferences, BOOK_ID, -1)

    companion object {
        private const val IS_AUTH_VERIFIED = "IS_AUTH_VERIFIED"
        private const val NAME = "NAME"
        private const val TOKEN = "TOKEN"
        private const val BOOK_ID = "Book_id"
    }

}