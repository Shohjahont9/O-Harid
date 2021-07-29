package lars_lion.dev.o_harid.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentProfileBinding
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.MainActivity
import lars_lion.dev.o_harid.utils.navigateSafe
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    @Inject
    lateinit var prefs: PreferencesManager


    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateSafe(R.id.action_profileFragment_to_mainFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        prefsData()

        onClicks()


    }

    private fun onClicks() {
        with(binding!!) {

            tvName.text = prefs.name

            if (prefs.language=="ru") applyModeSwitch.toggle()

            cvBack.setOnClickListener {
                findNavController().navigateSafe(R.id.action_profileFragment_to_mainFragment)
            }

            applyModeSwitch.setOnClickListener {
                if (applyModeSwitch.isChecked) {
                    setNewLocale("en", "LOTIN")
                    uzbek()
                } else {
                    setNewLocale("ru", "KIRIL")
                    kiril()
                }
            }

            cvHisob.setOnClickListener {
                findNavController().navigateSafe(R.id.action_profileFragment_to_cardFragment)
            }
        }
    }

    private fun prefsData() {
        when (prefs.language) {
            "en" -> uzbek()
            "ru" -> kiril()
        }
    }

    private fun setNewLocale(language: String, country: String) {
        Lingver.getInstance().setLocale(requireContext(), language, country)
        restart()
    }

    private fun restart() {
        val i = Intent(requireContext(), MainActivity::class.java)
        requireActivity().overridePendingTransition(0, 0)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    private fun uzbek() {
        prefs.language = "en"
    }

    private fun kiril() {
        prefs.language = "ru"
    }
}
