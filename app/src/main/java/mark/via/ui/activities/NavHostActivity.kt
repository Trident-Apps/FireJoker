package mark.via.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mark.via.R
import mark.via.util.Const

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Const.PREFERENCE_DATASTORE_NAME)

class NavHostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_host)

        lifecycleScope.launch(Dispatchers.IO) {
            val adId =
                AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id.toString()
            OneSignal.initWithContext(applicationContext)
            OneSignal.setAppId(Const.ONESIGNAL_ID)
            OneSignal.setExternalUserId(adId)
        }
    }

    companion object {
        lateinit var gadId: String
    }
}