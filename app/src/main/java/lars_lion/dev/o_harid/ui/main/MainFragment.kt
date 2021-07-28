package lars_lion.dev.o_harid.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.adapter.BestSellerAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentMainBinding
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(),
    BestSellerAdapter.BestSellerAdapterListener {
    lateinit var bestSellerAdapter: BestSellerAdapter

    @Inject
    lateinit var prefs: PreferencesManager

    val viewModel: MainViewModel by viewModels()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding =
        FragmentMainBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRv()

        loadBestSeller()

    }

    private fun loadBestSeller() {
        println("token -> ${prefs.token}")
        with(binding!!) {
            lifecycleScope.launch {
                viewModel.fetchBestSeller("Bearer ${prefs.token}").catch { it ->
                    toast(it.message ?: "message == null")
                }.collect { data ->
                    val bestSeller = ArrayList<lars_lion.dev.o_harid.network.response.bestSeller.Object>()

                    bestSeller.addAll(data.`object`)
                    binding!!.rvBestseller.adapter = bestSellerAdapter
                    bestSellerAdapter.updateList(bestSeller)

//                    viewModel.getBestSeller(data)
//                    viewModel.bestSeller.observe(viewLifecycleOwner, Observer {
//                        println("werewrwerewrw -> $it")
//
//                        when (it) {
//                            UiState.Loading -> progressBarBestseller.visible(true)
//                            is UiState.Success -> {
//                                println("status success")
//                                progressBarBestseller.visible(false)
//
//                                it.value.`object`.forEach { data ->
//                                    bestSeller.add(
//                                        lars_lion.dev.o_harid.network.response.bestSeller.Object(
//                                            data.author,
//                                            data.description,
//                                            data.file,
//                                            data.foto,
//                                            data.id,
//                                            data.interested,
//                                            data.language,
//                                            data.like,
//                                            data.name,
//                                            data.page_size,
//                                            data.price
//                                        )
//                                    )
//                                }
//
//                                println("Data-> $bestSeller")
//
//
//                            }
//                            is UiState.Error -> {
//                                progressBarBestseller.visible(false)
//                                toast(it.message)
//                            }
//                        }.exhaustive
//                    })
                }
            }
        }

    }

    private fun initRv() {
        bestSellerAdapter = BestSellerAdapter(this)
        with(binding!!.rvBestseller) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.bestSeller.Object
    ) {
            toast(position.toString())
    }

}