package lars_lion.dev.o_harid.ui.book

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.koushikdutta.ion.Ion
import dagger.hilt.android.AndroidEntryPoint
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.PaidBooksAdapter
import lars_lion.dev.o_harid.adapter.SearchBooksAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentBookBinding
import lars_lion.dev.o_harid.network.response.search.Object
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.main.MainViewModel
import lars_lion.dev.o_harid.utils.*
import java.io.*
import javax.inject.Inject


@AndroidEntryPoint
class BookFragment : BaseFragment<FragmentBookBinding>(),
    PaidBooksAdapter.BestSellerAdapterListener, SearchBooksAdapter.BestSellerAdapterListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener  {

    lateinit var paidBooksAdapter: PaidBooksAdapter

    @Inject
    lateinit var prefs: PreferencesManager
    val viewModelMain: MainViewModel by viewModels()
    lateinit var searchAdapter: SearchBooksAdapter
    val viewModel: BookViewModel by viewModels()


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBookBinding =
        FragmentBookBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initRvNowadays()


        loadNowadaysBooks()

        binding!!.etSearchPlaces.setOnQueryTextListener(this)

        binding!!.rooot.setOnClickListener { it ->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }

        binding!!.rvBook.setOnClickListener { it->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }
    }


    private fun loadNowadaysBooks() {
        with(binding!!) {
            viewModel.getPaidBooks()
            viewModel.paidBooks.observe(viewLifecycleOwner, EventObserver {
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
                        val nowadaysBooks =
                            ArrayList<lars_lion.dev.o_harid.network.response.paidBooks.Object>()
                        nowadaysBooks.addAll(it.value.`object`)
                        binding!!.rvBook.adapter = paidBooksAdapter
                        paidBooksAdapter.updateList(nowadaysBooks)

                    }
                    is UiState.Error -> {
                        progressBar.visible(false)
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

    private fun initRvNowadays() {
        paidBooksAdapter = PaidBooksAdapter(this)
        with(binding!!.rvBook) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.paidBooks.Object
    ) {
        toast(position.toString())
        Ion.with(context)
            .load(data.file) // have a ProgressBar get updated automatically with the percent
//            .progressBar(progressBar) // and a ProgressDialog
//            .progressDialog(progressDialog) // can also use a custom callback
//            .progress { downloaded, total -> println("$downloaded / $total") }
            .write(File(  Environment.getExternalStorageDirectory(), "MyProfile"))
            .setCallback { e, file ->
                toast("download completed")
                // download done...
                // do stuff with the File or error
            }
    }

    override fun onDeleteItem(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.paidBooks.Object
    ) {

    }

    override fun onItemClick(position: Int, data: Object) {
        prefs.bookId = data.id
        findNavController().navigateSafe(R.id.action_bookFragment_to_bookDetailFragment)
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

                viewModelMain.getSearchBook("Bearer ${prefs.token}", newText.toString())
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