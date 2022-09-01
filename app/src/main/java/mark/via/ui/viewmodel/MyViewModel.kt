package mark.via.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.flow.first
import mark.via.ui.activities.dataStore
import mark.via.util.Const
import mark.via.util.TagSender
import mark.via.util.UriBuilder

class MyViewModel(app: Application) : AndroidViewModel(app) {
    private val sender = TagSender()
    private val builder = UriBuilder()

    private val TAG = "My Tag"

    val urlLiveData: MutableLiveData<String> = MutableLiveData()

    fun fetchDeeplink(activity: Activity) {
        Log.d(TAG, "started deep")
        AppLinkData.fetchDeferredAppLinkData(activity) {
            val deepLink = it?.targetUri.toString()
            Log.d(TAG, deepLink)
            if (deepLink == "null") {
                fetchAppsData(activity)
                Log.d(TAG, "switched to apps")
            } else {
                urlLiveData.postValue(builder.createUrl(deepLink, null, activity))
                sender.sendTag(deepLink, null)
                Log.d(TAG, "created link from deep")
            }
        }
    }

    private fun fetchAppsData(activity: Activity) {
        Log.d(TAG, "started apps")
        AppsFlyerLib.getInstance().init(Const.APPS_DEV_KEY, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                urlLiveData.postValue(builder.createUrl("null", data, activity))
                sender.sendTag("null", data)
                Log.d(TAG, "created rul from apps")
            }

            override fun onConversionDataFail(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                TODO("Not yet implemented")
            }

            override fun onAttributionFailure(p0: String?) {
                TODO("Not yet implemented")
            }

        }, activity)
        AppsFlyerLib.getInstance().start(activity)
    }

    suspend fun checkDatastoreValue(key: String, context: Context): String? {
        val datastoreKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[datastoreKey]
    }

    suspend fun saveLastUrl(url: String, context: Context) {
        val datastoreKey = stringPreferencesKey("finalUrl")
        context.dataStore.edit {
            it[datastoreKey] = url
        }
    }
}