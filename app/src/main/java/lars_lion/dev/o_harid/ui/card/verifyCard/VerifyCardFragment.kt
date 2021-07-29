package lars_lion.dev.o_harid.ui.card.verifyCard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentVerifyCardBinding
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class VerifyCardFragment :BaseFragment<FragmentVerifyCardBinding>() {

    @Inject
    lateinit var prefs:PreferencesManager

    val viewModel: VerifyCardViewModel by viewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateSafe(R.id.action_cardFragment_to_profileFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }


    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyCardBinding = FragmentVerifyCardBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding!!){
            rooot.setOnClickListener {
                hideKeyBoard(it)
            }

            cvHisob.setOnClickListener {
                if (etCode.text.toString().isNotEmpty() && etCode.text.toString().length!=5){
                    viewModel.getVerifyCode(etCode.text.toString())
                    viewModel.verifyCode.observe(viewLifecycleOwner, EventObserver{
                        when(it){
                            UiState.Loading ->progressBar.visible(true)
                            is UiState.Success -> {
                                progressBar.visible(false)
                                findNavController().navigateSafe(R.id.action_verifyCardFragment_to_profileFragment)
                            }
                            is UiState.Error -> {
                                progressBar.visible(false)
                            }
                        }.exhaustive
                    })

                }else root.snackbar(getString(R.string.kod_error))
            }
        }

    }

}