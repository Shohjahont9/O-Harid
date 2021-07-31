package lars_lion.dev.o_harid.ui.book_detail

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.CommentsAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentBookDetailBinding
import lars_lion.dev.o_harid.model.Comment
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.utils.*
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class BookDetailFragment : BaseFragment<FragmentBookDetailBinding>(),
    CommentsAdapter.BestSellerAdapterListener {

    lateinit var commentsAdapter: CommentsAdapter
    var url = ""

    @Inject
    lateinit var prefs: PreferencesManager
    val viewModel: BookDetailViewModel by viewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateSafe(R.id.action_bookDetailFragment_to_mainFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookDetailBinding = FragmentBookDetailBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadComments()

        loadBookItems()

        onClicks()

    }

    private fun onClicks() {

        with(binding!!) {
            cvBack.setOnClickListener {
                findNavController().navigateSafe(R.id.action_bookDetailFragment_to_mainFragment)
            }
            cvLib.setOnClickListener {
                viewModel.addFavBook(prefs.bookId)
                viewModel.addFavouriteBook.observe(viewLifecycleOwner, EventObserver {
                    when (it) {
                        UiState.Loading -> {
                        }
                        is UiState.Success -> {
                            root.snackbar("Bu kitob javonga qo`shildi")
                        }
                        is UiState.Error -> root.snackbar(it.message)
                    }.exhaustive
                })
            }

            cvBuy.setOnClickListener {
                showBottomSheet()
            }

        }
    }


    fun showBottomSheet() {
        val bottomView: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.buy_book, null)
        val buyBook = bottomView.findViewById<MaterialCardView>(R.id.cv_yes)
        val noBook = bottomView.findViewById<MaterialCardView>(R.id.cv_no)
        bottomView.findViewById<TextView>(R.id.tv_money).text = binding!!.tvPrice.text.toString()
        val progress = bottomView.findViewById<LottieAnimationView>(R.id.progress_bar)
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
//            Objects.requireNonNull(dialog.window!!).setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnShowListener { d ->

            dialog.behavior.state =
                BottomSheetBehavior.STATE_EXPANDED
        }
        noBook.setOnClickListener {
            dialog.dismiss()
        }

        buyBook.setOnClickListener {
            viewModel.getBuyBook(prefs.bookId)
            viewModel.buyBook.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> progress.visible(true)
                    is UiState.Success -> {
                        progress.visible(false)
                        dialog.dismiss()

                        if (isAlreadyPermission()) downloadBook()
                        else checkPersmission()
                    }
                    is UiState.Error -> {
                        progress.visible(false)
                        dialog.dismiss()
                        toast(it.message)
                    }
                }.exhaustive
            })
        }

        dialog.setContentView(bottomView)
        dialog.show()

    }

    private fun isAlreadyPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPersmission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    READ_EXTERNAL_STORAGE
                )
            ) {

            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    listOf<String>(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                    ).toTypedArray(), PERMISSION_READ_AND_WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            downloadBook()
        }
    }

    private fun downloadBook() {
        with(binding!!) {
            tvPrice.text = ""
            tvProgress.visible(true)
            horizontalProgress.visible(true)

            downloadFile(url, horizontalProgress, tvProgress)
        }
    }

    var file: File? = null
    private fun downloadFile(url: String, progressBar: RoundedHorizontalProgressBar, textView: TextView) {
        println("url -> $url")
        val config = FetchConfiguration.Builder(context = requireContext()).build()
        val downloader = Fetch.Impl.getInstance(config)
        file = File(requireContext().getExternalFilesDir(null), "." + "O-Harid$url/$url")
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

    private fun downloaderListener(progressBar: RoundedHorizontalProgressBar, textView:TextView): FetchListener {
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
                    toast("Download Successfully")
//                    toast("Bu kitob sotib olingan kitoblar ro`yhatiga kiritildi!")
                    findNavController().navigateSafe(R.id.action_bookDetailFragment_to_bookFragment)
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
                println("Download method -> onStarted")
            }

            override fun onWaitingNetwork(download: Download) {
                println("Download method -> onWaitingNetwork")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadBookItems() {
        viewModel.getBookDetail(prefs.bookId)
        viewModel.bookDetail.observe(viewLifecycleOwner, EventObserver {
            println("UiState -> $it")
            when (it) {
                UiState.Loading -> binding!!.progressBar.visible(true)
                is UiState.Success -> {
                    val data = it.value.`object`
                    println("data-> $data")
                    url = data.file

                    binding!!.tvPrice.text = "${data.price} so`m"
                    binding!!.tvAuthor.text = data.author
                    binding!!.tvTitle.text = data.name
                    binding!!.tvDescription.text = data.description
                    binding!!.tvInterested.text = "${data.interested}\nQiziqish"
                    binding!!.tvLanguage.text = data.language
                    binding!!.tvPage.text = data.page_size.toString()
                    binding!!.imgBookBack.load(data.foto)
                    binding!!.imgBook.load(data.foto) {
                        crossfade(true)
                        listener(object : ImageRequest.Listener {
                            override fun onSuccess(
                                request: ImageRequest,
                                metadata: ImageResult.Metadata
                            ) {
                                super.onSuccess(request, metadata)
                                binding!!.progressBar1.visible(false)
                            }
                        })
                    }
                }
                is UiState.Error -> {
                    root.snackbar(it.message)
                }
            }.exhaustive
        })
    }

    private fun loadComments() {
        with(binding!!) {
            viewModel.getComment()
            viewModel.comments.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> progressBar.visible(true)
                    is UiState.Success -> {
                        progressBar.visible(false)
                        val commentsList = ArrayList<Comment>()
                        it.value.`object`.comments.forEach { data ->
                            commentsList.add(
                                Comment(
                                    id = data.id,
                                    data.user.id,
                                    data.user.name,
                                    null,
                                    data.text,
                                    data.date,
                                    data.evaluate,
                                    it.value.`object`.count
                                )
                            )
                        }

                        initComments()
                        binding!!.rvComments.adapter = commentsAdapter
                        commentsAdapter.updateList(commentsList)
                        with(rvComments) {
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
                        progressBar.visible(false)
                        toast(it.message)
                    }
                }.exhaustive
            })

        }

    }

    private fun initComments() {
        commentsAdapter = CommentsAdapter(this)
        with(binding!!.rvComments) {
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(position: Int, data: Comment) {

    }


    companion object {
        private val PERMISSION_READ_AND_WRITE_EXTERNAL_STORAGE = 1001
    }
}