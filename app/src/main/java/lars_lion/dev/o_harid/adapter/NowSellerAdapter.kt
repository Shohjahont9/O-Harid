package lars_lion.dev.o_harid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import lars_lion.dev.o_harid.databinding.ItemRvBinding
import lars_lion.dev.o_harid.model.NowSeller
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class NowSellerAdapter(
    private val dataList: ArrayList<NowSeller>,
    private val listener: NowSellerAdapterListener
) : RecyclerView.Adapter<NowSellerAdapter.DistrictComplexPageViewHolder>() {

    private var updateDataList = ArrayList<NowSeller>()

    private var fullDataList = ArrayList<NowSeller>()

    init {
        fullDataList.addAll(dataList)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DistrictComplexPageViewHolder(
            ItemRvBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )

    override fun onBindViewHolder(holder: DistrictComplexPageViewHolder, position: Int) {
        holder.bind(dataList[position])
        println("sts -> ${dataList[position].image}")
    }

    override fun getItemCount() =
        dataList.size

    fun updateList(list: List<NowSeller>) {
        updateDataList.clear()
        updateDataList.addAll(list)
        notifyDataSetChanged()
    }

    inner class DistrictComplexPageViewHolder
    constructor(
        private val binding: ItemRvBinding,
        private val listener: NowSellerAdapterListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NowSeller) {
            with(binding) {

            }
        }
    }


    interface NowSellerAdapterListener {
        fun onItemClick(position: Int, data: NowSeller)
        fun onItemLongClicked(position: Int, data: NowSeller, view: View): Boolean
    }
}

