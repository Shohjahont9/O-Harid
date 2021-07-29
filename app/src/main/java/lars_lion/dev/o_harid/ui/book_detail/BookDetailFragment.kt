package lars_lion.dev.o_harid.ui.book_detail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
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
import org.w3c.dom.Text
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
                        toast("Bu kitob sotib olingan kitoblar ro`yhatiga kiritildi!")
                        findNavController().navigateSafe(R.id.action_bookDetailFragment_to_bookFragment)

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
}