package lars_lion.dev.o_harid.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.BestSellerAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentMainBinding
import lars_lion.dev.o_harid.network.response.bestSeller.Object
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
        viewModel.getBestSeller()
        with(binding!!) {
            viewModel.bestSeller.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> progressBarBestseller.visible(true)
                    is UiState.Success -> {
                        progressBarBestseller.visible(false)
                        val bestSeller = ArrayList<Object>()
                        it.value.`object`.forEach { data ->
                            bestSeller.add(
                                Object(
                                    data.author,
                                    data.description,
                                    data.file,
                                    data.foto,
                                    data.id,
                                    data.interested,
                                    data.language,
                                    data.like,
                                    data.name,
                                    data.page_size,
                                    data.price
                                )
                            )
                        }
                        binding!!.rvBestseller.adapter = bestSellerAdapter
                        bestSellerAdapter.updateList(bestSeller)

                    }
                    is UiState.Error -> {
                        progressBarBestseller.visible(false)
                        toast(it.message)
                    }
                }.exhaustive
            })
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

    override fun onItemClick(position: Int, data: Object) {

    }

}