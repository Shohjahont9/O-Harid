package lars_lion.dev.o_harid.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.FavouriteBooksAdapter
import lars_lion.dev.o_harid.adapter.PaidBooksAdapter
import lars_lion.dev.o_harid.adapter.SearchBooksAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentFavouriteBinding
import lars_lion.dev.o_harid.network.response.favourite.Object
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.main.MainViewModel
import lars_lion.dev.o_harid.utils.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment : BaseFragment<FragmentFavouriteBinding>(),
    FavouriteBooksAdapter.BestSellerAdapterListener, SearchBooksAdapter.BestSellerAdapterListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

    lateinit var favouriteAdapter: FavouriteBooksAdapter

    @Inject
    lateinit var prefs: PreferencesManager
    val nowadaysBooks = ArrayList<Object>()

    val viewModel: FavouriteViewModel by viewModels()
    val viewModelMain: MainViewModel by viewModels()
    lateinit var searchAdapter: SearchBooksAdapter

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteBinding = FragmentFavouriteBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNowadaysBooks()

        binding!!.etSearchPlaces.setOnQueryTextListener(this)

        binding!!.rooot.setOnClickListener { it ->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }

        binding!!.rvFavourite.setOnClickListener { it->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }
    }

    private fun loadNowadaysBooks() {
        with(binding!!) {
            viewModel.getFavouriteBook()
            viewModel.favouriteBook.observe(viewLifecycleOwner, EventObserver {
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
                        nowadaysBooks.addAll(it.value.`object`)
                        println("size -> ${nowadaysBooks.size}")

                        initRvNowadays(nowadaysBooks)
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

    private fun initRvNowadays(dataList: ArrayList<Object>) {
        favouriteAdapter = FavouriteBooksAdapter(this)
        binding!!.rvFavourite.apply {
            OverScrollDecoratorHelper.setUpOverScroll(
                this,
                OverScrollDecoratorHelper.ORIENTATION_VERTICAL
            )
            setHasFixedSize(true)
            adapter = favouriteAdapter
            favouriteAdapter.updateList(dataList)
            scheduleLayoutAnimation()
        }
    }

    override fun onItemClick(
        position: Int,
        data: Object
    ) {
        toast(position.toString())
    }

    override fun onDeleteItem(position: Int, data: Object) {
        viewModel.getDeleteBook(data.id.toString())
        viewModel.deleteBook.observe(viewLifecycleOwner, EventObserver{
            when(it){
                UiState.Loading ->{}
                is UiState.Success -> {
                    nowadaysBooks.removeAt(position)
                    favouriteAdapter.updateList(nowadaysBooks)
                    binding!!.root.snackbar("${data.name} kitobi o`chirildi")
                }
                is UiState.Error -> {
                    binding!!.root.snackbar(it.message)

                }
            }.exhaustive
        })

    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.search.Object
    ) {
        prefs.bookId = data.id
        findNavController().navigateSafe(R.id.action_favouriteFragment_to_bookDetailFragment)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    private fun initRvSearch() {
        searchAdapter = SearchBooksAdapter(this)
        with(binding!!.rvSearch) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.toString().length >= 1) {
            with(binding!!) {
                rvSearch.visible(true)

                viewModelMain.getSearchBook("Bearer ${prefs.token}", newText.toString().toLowerCase())
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