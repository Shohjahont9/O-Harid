package lars_lion.dev.o_harid.ui.getBookByType

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.BookTypeAdapter
import lars_lion.dev.o_harid.adapter.CommentsAdapter
import lars_lion.dev.o_harid.adapter.SearchBooksAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentGetBookByTypeBinding
import lars_lion.dev.o_harid.model.Comment
import lars_lion.dev.o_harid.network.response.getBookByBookType.Object
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.main.MainViewModel
import lars_lion.dev.o_harid.utils.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import javax.inject.Inject

@AndroidEntryPoint
class GetBookByTypeFragment : BaseFragment<FragmentGetBookByTypeBinding>(),
    BookTypeAdapter.BestSellerAdapterListener, SearchBooksAdapter.BestSellerAdapterListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

    lateinit var mAdapter: BookTypeAdapter

    @Inject
    lateinit var prefs: PreferencesManager

    val viewModel: GetBookTypeViewModel by viewModels()

    lateinit var searchAdapter: SearchBooksAdapter

    val viewModelMain: MainViewModel by viewModels()

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGetBookByTypeBinding = FragmentGetBookByTypeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.tvType.text = prefs.bookTypeName
        binding!!.etSearchPlaces.setIconifiedByDefault(false)
        val v: View =  binding!!.etSearchPlaces.findViewById(R.id.search_plate)
        v.setBackgroundColor(Color.WHITE)

        loadNowadaysBooks()

        binding!!.etSearchPlaces.setOnQueryTextListener(this)

        onClicks()

        binding!!.rooot.setOnClickListener { it ->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }

        binding!!.rvBook.setOnClickListener { it ->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }

    }

    private fun onClicks() {
        with(binding!!) {

            cvBack.setOnClickListener {
                findNavController().navigateSafe(R.id.action_getBookByTypeFragment_to_mainFragment)
            }
        }
    }

    private fun loadNowadaysBooks() {
        with(binding!!) {
            viewModel.getBookType(prefs.bookType.toString())
            viewModel.getBookType.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> {
                        progressBar.visible(true)
                    }
                    is UiState.Success -> {
                        progressBar.visible(false)

                        if (it.value.`object`.isEmpty() || it.value.`object` == null) {
                            progressBarBestseller.visible(true)
                            tvEmpty.visible(true)
                        }
                        val nowadaysBooks = ArrayList<Object>()
                        nowadaysBooks.addAll(it.value.`object`)
                        initRvBookType(nowadaysBooks)
                    }
                    is UiState.Error -> {
                        progressBar.visible(false)
                        toast(it.message)
                        if (it.message == "empty") {
                            progressBarBestseller.visible(true)
                            tvEmpty.visible(true)
                        } else
                            toast(it.message)
                    }
                }.exhaustive
            })

        }

    }

    private fun initRvBookType(dataList: ArrayList<Object>) {
        mAdapter = BookTypeAdapter(this)
        binding!!.rvBook.apply {
            OverScrollDecoratorHelper.setUpOverScroll(
                this,
                OverScrollDecoratorHelper.ORIENTATION_VERTICAL
            )
            setHasFixedSize(true)
            adapter = mAdapter
            mAdapter.updateList(dataList)
            scheduleLayoutAnimation()
        }
    }

    private fun initRvSearch() {
        searchAdapter = SearchBooksAdapter(this)
        with(binding!!.rvSearch) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(position: Int, data: Object) {
        prefs.bookId = data.id
        prefs.isGetBookTypeFragment = true
        findNavController().navigateSafe(R.id.action_getBookByTypeFragment_to_bookDetailFragment)

    }

    override fun onFavouriteClick(position: Int, data: Object) {
        viewModel.addFavBook(data.id)
        viewModel.addFavouriteBook.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                UiState.Loading -> {
                }
                is UiState.Success -> {
                    binding!!.root.snackbar("Bu kitob javonga qo`shildi")
                }
                is UiState.Error -> binding!!.root.snackbar(it.message)
            }.exhaustive
        })
    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.search.Object
    ) {
        prefs.bookId = data.id
        prefs.isGetBookTypeFragment = true
        findNavController().navigateSafe(R.id.action_getBookByTypeFragment_to_bookDetailFragment)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.toString().length >= 1) {
            with(binding!!) {
                rvSearch.visible(true)

                viewModelMain.getSearchBook(
                    "Bearer ${prefs.token}",
                    newText.toString().toLowerCase()
                )
                viewModelMain.searchBook.observe(viewLifecycleOwner, EventObserver {
                    when (it) {
                        UiState.Loading -> {
                        }
                        is UiState.Success -> {
                            val nowadaysBooks =
                                ArrayList<lars_lion.dev.o_harid.network.response.search.Object>()
                            nowadaysBooks.addAll(it.value.`object`)
                            initRvSearch()
                            binding!!.rvSearch.adapter = searchAdapter
                            searchAdapter.updateList(nowadaysBooks)
                        }
                        is UiState.Error -> {
                            root.snackbar(it.message)
                        }
                    }.exhaustive
                })
            }
        } else binding!!.rvSearch.visible(false)
        return true
    }


}