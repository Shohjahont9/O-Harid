package lars_lion.dev.o_harid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.load
import coil.transform.CircleCropTransformation
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.databinding.ItemRvBinding
import lars_lion.dev.o_harid.network.response.bestSeller.Object
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class BestSellerAdapter(
    private val listener: BestSellerAdapterListener
) : RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder>() {

    private var fullDataList = ArrayList<Object>()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BestSellerViewHolder(
            ItemRvBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        holder.bind(fullDataList[position])
    }

    override fun getItemCount() =
        fullDataList.size

    fun updateList(list: List<Object>) {
        fullDataList.clear()
        fullDataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BestSellerViewHolder
    constructor(
        private val binding: ItemRvBinding,
        private val listener: BestSellerAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Object) {
            with(binding) {
                imgItem.load(data.foto) {
                    crossfade(true)
                    placeholder(R.drawable.ic_books)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }


    interface BestSellerAdapterListener {
        fun onItemClick(position: Int, data: Object)
    }
}

