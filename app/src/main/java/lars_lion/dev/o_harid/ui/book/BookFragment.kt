package lars_lion.dev.o_harid.ui.book

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.folioreader.FolioReader
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
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
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BookFragment : BaseFragment<FragmentBookBinding>(),
    PaidBooksAdapter.BestSellerAdapterListener, SearchBooksAdapter.BestSellerAdapterListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

    lateinit var paidBooksAdapter: PaidBooksAdapter
    lateinit var folioReader: FolioReader

    @Inject
    lateinit var prefs: PreferencesManager
    val viewModelMain: MainViewModel by viewModels()
    lateinit var searchAdapter: SearchBooksAdapter
    val viewModel: BookViewModel by viewModels()
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBookBinding =
        FragmentBookBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folioReader = FolioReader.get()

        loadNowadaysBooks()

        binding!!.etSearchPlaces.setOnQueryTextListener(this)
        binding!!.etSearchPlaces.setIconifiedByDefault(false)
        val v: View = binding!!.etSearchPlaces.findViewById(R.id.search_plate)
        v.setBackgroundColor(Color.WHITE)

        binding!!.rooot.setOnClickListener { it ->
            binding!!.etSearchPlaces.clearFocus()
            hideKeyBoard(it)
        }

        binding!!.rvBook.setOnClickListener { it ->
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
                        initRvNowadays(nowadaysBooks)
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

    private fun initRvNowadays(dataList: ArrayList<lars_lion.dev.o_harid.network.response.paidBooks.Object>) {
        paidBooksAdapter = PaidBooksAdapter(this)
        binding!!.rvBook.apply {
            OverScrollDecoratorHelper.setUpOverScroll(
                this,
                OverScrollDecoratorHelper.ORIENTATION_VERTICAL
            )
            setHasFixedSize(true)
            adapter = paidBooksAdapter
            paidBooksAdapter.updateList(dataList)
            scheduleLayoutAnimation()
        }
    }

    override fun onItemClick(
        position: Int,
        data: lars_lion.dev.o_harid.network.response.paidBooks.Object,
        horizontalProgressBar: RoundedHorizontalProgressBar,
        textView: TextView
    ) {
        val file = File(requireContext().getExternalFilesDir(null), "${data.file.substring(35)}.epub")


        println("Path -> ${file.absoluteFile}")
   //     folioReader.openBook(file.absolutePath)

        if (file.exists()) {
            println("Path -> ${file.absoluteFile}")
            folioReader.openBook("${file.absolutePath}")
        } else {
            downloadFile(data.file, horizontalProgressBar, textView)
        }

    }

    var file: File? = null
    private fun downloadFile(
        url: String,
        progressBar: RoundedHorizontalProgressBar,
        textView: TextView
    ) {
        println("url -> $url")
        val config = FetchConfiguration.Builder(context = requireContext()).build()
        val downloader = Fetch.Impl.getInstance(config)
        file = File(requireContext().getExternalFilesDir(null), "${url.substring(35)}.epub")

        val request = Request(url, file!!.absolutePath)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        downloader.enqueue(request, { updatedRequest ->
            println("Download success -> ${updatedRequest.fileUri}")

            toast("Yuklash boshlandi.")
        }) { error ->
            println("Download error -> ${error.throwable}")
        }
        downloader.addListener(downloaderListener(progressBar, textView))
    }

    private fun downloaderListener(
        progressBar: RoundedHorizontalProgressBar,
        textView: TextView
    ): FetchListener {
        return object : FetchListener {
            override fun onAdded(download: Download) {
                println("Download method -> onAdded")
            }

            override fun onCancelled(download: Download) {
                println("Download method -> onCancelled")
            }

            override fun onCompleted(download: Download) {
                if (file != null && file!!.exists()) {
//                    encryptedFile(file!!.absolutePath)
//                    viewModel.updateDownloadedUrl(mUuid, file!!.absolutePath)
                    progressBar.visible(false)
                    textView.visible(false)
                    toast("Download Successfully")
//                    toast("Bu kitob sotib olingan kitoblar ro`yhatiga kiritildi!")
                    file = null
                }
            }

            override fun onDeleted(download: Download) {
                println("Download method -> onDeleted")
            }

            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
                println("Download method -> onDownloadBlockUpdated")

            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                println("Download method -> onError")
            }

            override fun onPaused(download: Download) {
                println("Download method -> onPaused")
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                println("Download method -> onProgress")
                println("downloadedBytesPerSecond -> $downloadedBytesPerSecond Mb/s")
                progressBar.progress = download.progress
                textView.text = "${download.progress}%"
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                println("Download method -> onQueued")
            }

            override fun onRemoved(download: Download) {
                println("Download method -> onRemoved")
            }

            override fun onResumed(download: Download) {
                println("Download method -> onResumed")
            }

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                progressBar.visible(true)
                textView.visible(true)
                println("Download method -> onStarted")
            }

            override fun onWaitingNetwork(download: Download) {
                println("Download method -> onWaitingNetwork")
            }
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
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
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