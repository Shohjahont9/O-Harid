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
import lars_lion.dev.o_harid.databinding.ItemNowadaysBinding
import lars_lion.dev.o_harid.databinding.ItemRvBinding
import lars_lion.dev.o_harid.databinding.ItemRvBookTypeBinding
import lars_lion.dev.o_harid.network.response.nowadays.Object
import lars_lion.dev.o_harid.utils.visible
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class BookTypeAdapter(
    private val listener: BestSellerAdapterListener
) : RecyclerView.Adapter<BookTypeAdapter.BestSellerViewHolder>() {

    private var fullDataList = ArrayList<lars_lion.dev.o_harid.network.response.getBookByBookType.Object>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BestSellerViewHolder(
            ItemRvBookTypeBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        holder.bind(fullDataList[position])
    }

    override fun getItemCount() =
        fullDataList.size

    fun updateList(list: List<lars_lion.dev.o_harid.network.response.getBookByBookType.Object>) {
        fullDataList.clear()
        fullDataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BestSellerViewHolder
    constructor(
        private val binding: ItemRvBookTypeBinding,
        private val listener: BestSellerAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data:lars_lion.dev.o_harid.network.response.getBookByBookType.Object) {
            with(binding) {
                imgBook.load(data.foto) {
                    crossfade(true)
                    listener(object : ImageRequest.Listener {
                        override fun onSuccess(
                            request: ImageRequest,
                            metadata: ImageResult.Metadata
                        ) {
                            super.onSuccess(request, metadata)
                            watchAnimation.visible(false)
                        }
                    })
                }

                tvAuthor.text = data.author
                tvTitle.text = data.name
                tvPrice.text = "${data.price.toInt()} UZS"

                rating.rating = data.interested.toFloat()
                tvType.text = data.language
                imgStar.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        listener.onFavouriteClick(adapterPosition, data)
                }

                clRoot.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        listener.onItemClick(adapterPosition, data)
                }
            }
        }
    }


    interface BestSellerAdapterListener {
        fun onItemClick(
            position: Int,
            data: lars_lion.dev.o_harid.network.response.getBookByBookType.Object
        )

        fun onFavouriteClick(position: Int, data:lars_lion.dev.o_harid.network.response.getBookByBookType.Object)
    }
}

