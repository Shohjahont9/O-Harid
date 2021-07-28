package lars_lion.dev.o_harid.ui.book_detail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.adapter.BestSellerAdapter
import lars_lion.dev.o_harid.adapter.CommentsAdapter
import lars_lion.dev.o_harid.adapter.JanrAdapter
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentBookDetailBinding
import lars_lion.dev.o_harid.model.Comment
import lars_lion.dev.o_harid.network.response.bookType.Object
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.main.MainViewModel
import lars_lion.dev.o_harid.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class BookDetailFragment : BaseFragment<FragmentBookDetailBinding>(),
    CommentsAdapter.BestSellerAdapterListener {

    lateinit var commentsAdapter: CommentsAdapter

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

        binding!!.cvBack.setOnClickListener {
            findNavController().navigateSafe(R.id.action_bookDetailFragment_to_mainFragment)
        }

        binding!!.cvLib.setOnClickListener {
            viewModel.addFavBook(prefs.bookId)
            viewModel.addFavouriteBook.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> {
                    }
                    is UiState.Success -> {
                        root.snackbar("Bu kitob javonga qo`shildi")
                    }
                    is UiState.Error -> toast(it.message)
                }.exhaustive
            })
        }
    }

    private fun loadBookItems() {
        viewModel.getBookDetail(prefs.bookId)
        viewModel.bookDetail.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                UiState.Loading -> binding!!.progressBar.visible(true)
                is UiState.Success -> {
                    val data = it.value.`object`
                    with(binding!!) {
                        tvAuthor.text = data.author
                        tvTitle.text = data.name
                        tvPrice.text = data.price.toString()
                        tvDescription.text = data.description
                        tvInterested.text = "${data.interested}\nQiziqish"
                        tvLanguage.text = data.language
                        tvPage.text = data.page_size.toString()
                        imgBook.load(data.foto) {
                            crossfade(true)
                            listener(object : ImageRequest.Listener {
                                override fun onSuccess(
                                    request: ImageRequest,
                                    metadata: ImageResult.Metadata
                                ) {
                                    super.onSuccess(request, metadata)
                                    progressBar1.visible(false)
                                }
                            })
                        }
                    }
                }
                is UiState.Error -> {
                    toast(it.message)
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
}