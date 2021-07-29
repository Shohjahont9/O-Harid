package lars_lion.dev.o_harid.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.BestSellerAdapter
import lars_lion.dev.o_harid.adapter.JanrAdapter
import lars_lion.dev.o_harid.adapter.NowadaysBooksAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentMainBinding
import lars_lion.dev.o_harid.network.response.nowadays.Object
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(),
    BestSellerAdapter.BestSellerAdapterListener, NowadaysBooksAdapter.BestSellerAdapterListener,
    JanrAdapter.BestSellerAdapterListener {

    lateinit var bestSellerAdapter: BestSellerAdapter
    lateinit var nowadaysAdapter: NowadaysBooksAdapter
    lateinit var bookTypeAdapter: JanrAdapter

    @Inject
    lateinit var prefs: PreferencesManager

    val viewModel: MainViewModel by viewModels()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding =
        FragmentMainBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsData()

        loadBestSeller()

        loadNowadaysBooks()

        loadBookType()

        onClicks()
    }

    private fun prefsData() {
        when (prefs.language) {
            "en" -> uzbek()
            "ru" -> kiril()
        }
    }

    private fun uzbek() {
        prefs.language = "en"
    }

    private fun kiril() {
        prefs.language = "ru"
    }

    private fun onClicks() {
        with(binding!!) {
            tvName.text = prefs.name

            cvProfile.setOnClickListener {
                findNavController().navigateSafe(R.id.action_mainFragment_to_profileFragment)
            }

        }
    }

    private fun loadBookType() {
        with(binding!!) {
            viewModel.getBookType("Bearer ${prefs.token}")
            viewModel.bookType.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> watchAnimationJanr.visible(true)
                    is UiState.Success -> {
                        watchAnimationJanr.visible(false)
                        val nowadaysBooks =
                            ArrayList<lars_lion.dev.o_harid.network.response.bookType.Object>()
                        nowadaysBooks.addAll(it.value.`object`)
                        initRvBookType()
                        binding!!.rvJanr.adapter = bookTypeAdapter
                        bookTypeAdapter.updateList(nowadaysBooks)
                        with(rvJanr) {
                            viewTreeObserver.addOnPreDrawListener(
                                object : ViewTreeObserver.OnPreDrawListener {
                                    override fun onPreDraw(): Boolean {
                                        viewTreeObserver.removeOnPreDrawListener(this)
                                        for (i in 0 until childCount) {
                                            val v: View = getChildAt(i)
                                            v.alpha = 0.0f
                                            v.animate().alpha(1.0f)
                                                .setDuration(1000)
                                                .setStartDelay((i * 100).toLong())
                                                .start()
                                        }
                                        return true
                                    }
                                })
                        }
                    }
                    is UiState.Error -> {
                        watchAnimationJanr.visible(false)
                        toast(it.message)
                    }
                }.exhaustive
            })

        }

    }

    private fun loadNowadaysBooks() {
        with(binding!!) {
            viewModel.getNowadaysBook("Bearer ${prefs.token}")
            viewModel.nowadaysBook.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> watchAnimation.visible(true)
                    is UiState.Success -> {
                        watchAnimation.visible(false)
                        val nowadaysBooks =
                            ArrayList<Object>()
                        nowadaysBooks.addAll(it.value.`object`)
                        initRvNowadays()
                        binding!!.rvNow.adapter = nowadaysAdapter
                        nowadaysAdapter.updateList(nowadaysBooks)
                        with(rvNow) {
                            viewTreeObserver.addOnPreDrawListener(
                                object : ViewTreeObserver.OnPreDrawListener {
                                    override fun onPreDraw(): Boolean {
                                        viewTreeObserver.removeOnPreDrawListener(this)
                                        for (i in 0 until childCount) {
                                            val v: View = getChildAt(i)
                                            v.alpha = 0.0f
                                            v.animate().alpha(1.0f)
                                                .setDuration(1000)
                                                .setStartDelay((i * 100).toLong())
                                                .start()
                                        }
                                        return true
                                    }
                                })
                        }
                    }
                    is UiState.Error -> {
                        watchAnimation.visible(false)
                        toast(it.message)
                    }
                }.exhaustive
            })

        }

    }

    private fun loadBestSeller() {
        with(binding!!) {
            viewModel.getBestSeller("Bearer ${prefs.token}")
            viewModel.bestSeller.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> progressBarBestseller.visible(true)
                    is UiState.Success -> {
                        progressBarBestseller.visible(false)
                        val bestSeller =
                            ArrayList<lars_lion.dev.o_harid.network.response.bestSeller.Object>()

                        bestSeller.addAll(it.value.`object`)
                        initRv()
                        binding!!.rvBestseller.adapter = bestSellerAdapter
                        bestSellerAdapter.updateList(bestSeller)
                        with(rvBestseller) {
                            viewTreeObserver.addOnPreDrawListener(
                                object : ViewTreeObserver.OnPreDrawListener {
                                    override fun onPreDraw(): Boolean {
                                        viewTreeObserver.removeOnPreDrawListener(this)
                                        for (i in 0 until childCount) {
                                            val v: View = getChildAt(i)
                                            v.alpha = 0.0f
                                            v.animate().alpha(1.0f)
                                                .setDuration(1000)
                                                .setStartDelay((i * 50).toLong())
                                                .start()
                                        }
                                        return true
                                    }
                                })
                        }
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

    private fun initRvBookType() {
        bookTypeAdapter = JanrAdapter(this)
        with(binding!!.rvJanr) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    private fun initRvNowadays() {
        nowadaysAdapter = NowadaysBooksAdapter(this)
        with(binding!!.rvNow) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.bestSeller.Object
    ) {
        prefs.bookId = data.id
        findNavController().navigateSafe(R.id.action_mainFragment_to_bookDetailFragment)
    }

    override fun onItemClick(position: Int, data: Object) {
        prefs.bookId = data.id
        findNavController().navigateSafe(R.id.action_mainFragment_to_bookDetailFragment)
    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.bookType.Object
    ) {

    }

}