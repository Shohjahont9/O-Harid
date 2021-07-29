package lars_lion.dev.o_harid

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class O_HaridApp: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        val store = PreferenceLocaleStore(this, Locale(LANGUAGE_LOTIN))
        val lingver = Lingver.init(this, store)
    }

    companion object {
        const val LANGUAGE_LOTIN = "en"
        const val LANGUAGE_KIRIL = "ru"
    }
}