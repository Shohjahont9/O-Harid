package lars_lion.dev.o_harid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar
import lars_lion.dev.o_harid.databinding.ItemNowadaysBinding
import lars_lion.dev.o_harid.utils.visible

@SuppressLint("SetTextI18n")
class PaidBooksAdapter(
    private val listener: BestSellerAdapterListener
) : RecyclerView.Adapter<PaidBooksAdapter.BestSellerViewHolder>() {

    private var fullDataList = ArrayList<lars_lion.dev.o_harid.network.response.paidBooks.Object>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BestSellerViewHolder(
            ItemNowadaysBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        holder.bind(fullDataList[position])
    }

    override fun getItemCount() =
        fullDataList.size

    fun updateList(list: List<lars_lion.dev.o_harid.network.response.paidBooks.Object>) {
        fullDataList.clear()
        fullDataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BestSellerViewHolder
    constructor(
        private val binding: ItemNowadaysBinding,
        private val listener: BestSellerAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data:lars_lion.dev.o_harid.network.response.paidBooks.Object) {
            with(binding) {
                imgItem.load(data.foto) {
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

                imgItem.scaleType = ImageView.ScaleType.FIT_XY
                tvAuthor.text = data.author
                tvTitle.text = data.name
                icDelete.visible(false)
                tvPrice.visible(false)


                rlRoot.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        listener.onItemClick(adapterPosition, data, horizontalProgress, tvProgress)
                }
            }
        }
    }


    interface BestSellerAdapterListener {
        fun onItemClick(
            position: Int, data:lars_lion.dev.o_harid.network.response.paidBooks.Object , horizontalProgressBar: RoundedHorizontalProgressBar, textView: TextView       )

        fun onDeleteItem(position: Int, data: lars_lion.dev.o_harid.network.response.paidBooks.Object)
    }
}

