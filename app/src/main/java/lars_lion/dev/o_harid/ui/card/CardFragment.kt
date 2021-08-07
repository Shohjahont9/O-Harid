package lars_lion.dev.o_harid.ui.card

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_card.*
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentCardBinding
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class CardFragment : BaseFragment<FragmentCardBinding>() {

    @Inject
    lateinit var prefs:PreferencesManager
    val viewModel: CardViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                        findNavController().navigateSafe(R.id.action_cardFragment_to_profileFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )

    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCardBinding =
        FragmentCardBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClicks()

    }

    private fun checkNumbers() {
        with(binding!!) {
            println(cardValidation.text.toString())
            println(card.text.toString().length.toString())
            if (cardValidation.text.toString().substring(0, 2)
                    .toInt() > 12 || cardValidation.text.toString().substring(3).toInt() < 20
            ) root.snackbar("Sanani to`g`ri kiriting")
            else if (card.text.toString().length != 19) root.snackbar(getString(R.string.karta_raqam_error))
            else if (etMoney.text.toString().isNotEmpty())
                if (etMoney.text.toString()
                        .toInt() < 1000
                ) root.snackbar(getString(R.string.min_money))
                else if (etMoney.text.isEmpty() || cardValidation.text.toString()
                        .isEmpty() || card.text.toString().isEmpty()
                ) root.snackbar(
                    getString(
                        R.string.vse
                    )
                )
                else {

                    val data = JsonObject()
                    val cardNumber = card.text.toString().replace(" ","")
                    data.addProperty("number", cardNumber)
                    data.addProperty("expire", "${cardValidation.text.toString().substring(0, 2)}${cardValidation.text.toString().substring(3)}")
                    data.addProperty("amount", "${etMoney.text}00")

                    println("Data object-> $data")
                    println("object-> ${prefs.token}")
                    viewModel.getCreateCard(data.toString())
                    viewModel.createCard.observe(viewLifecycleOwner, EventObserver {
                        with(binding!!) {
                            when (it) {
                                UiState.Loading -> progressBar.visible(true)
                                is UiState.Success -> {
                                    progressBar.visible(false)
                                    findNavController().navigateSafe(R.id.action_cardFragment_to_verifyCardFragment)
                                }
                                is UiState.Error -> {
                                    progressBar.visible(false)
                                    root.snackbar(it.message)
                                }
                            }.exhaustive
                        }
                    })

                }
        }

    }

    private fun onClicks() {
        with(binding!!) {
            rooot.setOnClickListener {
                hideKeyBoard(it)
            }

            cvBack.setOnClickListener {
                findNavController().navigateSafe(R.id.action_cardFragment_to_profileFragment)
            }

            cvHisob.setOnClickListener {
                checkNumbers()


            }

        }
    }

}