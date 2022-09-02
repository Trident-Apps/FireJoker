package mark.via.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mark.via.R
import mark.via.ui.viewmodel.MyViewModel
import mark.via.ui.viewmodel.MyViewModelFactory
import mark.via.util.Checker
import mark.via.util.Const

class LoadingFragment : Fragment(R.layout.fragment_loading) {

    private val TAG = "My Tag"
    private val checker = Checker()
    private lateinit var myViewModel: MyViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myVmFactory = MyViewModelFactory(this.requireActivity().application)
        Log.d(TAG, "factory created")
        myViewModel = ViewModelProvider(this, myVmFactory)[MyViewModel::class.java]
        Log.d(TAG, "VM initialized")

        when (!checker.isDeviceSecured(this@LoadingFragment.requireActivity())) {
            true -> {
                Log.d(TAG, "checked Secure")
                startGame()
            }
            false -> {
                Log.d(TAG, "checked Secure")
                lifecycleScope.launch(Dispatchers.IO) {
                    val dataStore = myViewModel.checkDatastoreValue(DATASTORE_KEY, requireContext())

                    when (dataStore) {
                        null -> {
                            myViewModel.fetchDeeplink(requireActivity())
                            lifecycleScope.launch(Dispatchers.Main) {
                                myViewModel.urlLiveData.observe(viewLifecycleOwner) {
                                    startWeb(it)
                                }
                            }
                        }
                        else -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                startWeb(dataStore.toString())
                            }
                        }
                    }
                }
            }
        }
    }


    private fun startGame() {
        findNavController().navigate(R.id.startGameFragment)
    }

    private fun startWeb(url: String) {
        val bundle = bundleOf(Const.ARGUMENTS_NAME to url)
        findNavController().navigate(R.id.webViewFragment, bundle)
    }

    companion object {
        const val DATASTORE_KEY = "finalUrl"
    }
}