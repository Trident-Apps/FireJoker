package mark.via.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mark.via.R
import mark.via.databinding.WebViewFragmentBinding
import mark.via.ui.activities.dataStore
import mark.via.ui.viewmodel.MyViewModel
import mark.via.ui.viewmodel.MyViewModelFactory

class WebViewFragment : Fragment() {

    lateinit var webView: WebView
    lateinit var myViewModel: MyViewModel
    private var messageAb: ValueCallback<Array<Uri?>>? = null
    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WebViewFragmentBinding.inflate(inflater, container, false)

        val vmFactory = MyViewModelFactory(requireActivity().application)
        myViewModel = ViewModelProvider(this, vmFactory)[MyViewModel::class.java]
        webView = binding.webView
        webView.loadUrl(arguments?.getString(ARGUMENTS_NAME) ?: "null")
        webView.webViewClient = LocalClient()
        webView.settings.userAgentString = System.getProperty("http.agent")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                selectImageIfNeed()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.webChromeClient = this
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view!!.loadUrl(url!!)
                        return true
                    }
                }
                return true
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            messageAb?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (messageAb == null) return

            messageAb!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            messageAb = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                    }
                }

            })
    }

    private fun selectImageIfNeed() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = INTENT_TYPE
        startActivityForResult(
            Intent.createChooser(intent, CHOOSER_TITLE), RESULT_CODE
        )
    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            CookieManager.getInstance().flush()
            when (url) {
                BASE_URL -> {
                    findNavController().navigate(R.id.startGameFragment)
                }
                else -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (myViewModel.checkDatastoreValue(SHARED_PREF_NAME, requireContext())
                                .isNullOrEmpty()
                        ) {
                            val datastoreKEy = stringPreferencesKey(SHARED_PREF_NAME)
                            context!!.dataStore.edit {
                                it[datastoreKEy] = "final"
                            }
                            myViewModel.saveLastUrl(url!!, context!!)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARGUMENTS_NAME = "url"
        const val INTENT_TYPE = "image/*"
        const val CHOOSER_TITLE = "Image Chooser"
        const val BASE_URL = "https://firejoker.live/"
        const val SHARED_PREF_NAME = "sharedPref"
        const val RESULT_CODE = 1
    }
}