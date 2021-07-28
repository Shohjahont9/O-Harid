package lars_lion.dev.o_harid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transform.CircleCropTransformation
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.databinding.ItemCommentsBinding
import lars_lion.dev.o_harid.databinding.ItemNowadaysBinding
import lars_lion.dev.o_harid.databinding.ItemRvBinding
import lars_lion.dev.o_harid.network.response.nowadays.Object
import lars_lion.dev.o_harid.utils.visible
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class CommentsAdapter(
    private val listener: BestSellerAdapterListener
) : RecyclerView.Adapter<CommentsAdapter.BestSellerViewHolder>() {

    private var fullDataList = ArrayList<lars_lion.dev.o_harid.network.response.comments.Object>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BestSellerViewHolder(
            ItemCommentsBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        holder.bind(fullDataList[position])
    }

    override fun getItemCount() =
        fullDataList.size

    fun updateList(list: List<lars_lion.dev.o_harid.network.response.comments.Object>) {
        fullDataList.clear()
        fullDataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BestSellerViewHolder
    constructor(
        private val binding: ItemCommentsBinding,
        private val listener: BestSellerAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: lars_lion.dev.o_harid.network.response.comments.Object) {
            with(binding) {
//                imgUser.load(data.) {
//                    crossfade(true)
//                    listener(object : ImageRequest.Listener {
//                        override fun onSuccess(
//                            request: ImageRequest,
//                            metadata: ImageResult.Metadata
//                        ) {
//                            super.onSuccess(request, metadata)
//                            watchAnimation.visible(false)
//                        }
//                    })
//                }

                tvUserName.text =
                tvTitle.text = data.name
                tvPrice.text = "${data.price.toInt()} UZS"

                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        listener.onItemClick(adapterPosition, data)
                }
            }
        }
    }


    interface BestSellerAdapterListener {
        fun onItemClick(
            position: Int,
            data: lars_lion.dev.o_harid.network.response.comments.Object
        )
    }
}

