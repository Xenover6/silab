package com.silab.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silab.R
import com.silab.data.model.GoodsDataItem
import com.silab.databinding.CartItemContainerBinding
import com.silab.utils.Utils

class CartItemAdapter (
    private var listGoods: MutableList<GoodsDataItem>
) : RecyclerView.Adapter<CartItemAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(private val binding: CartItemContainerBinding,
                         private val onItemClickCallback: OnItemClickCallback
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goods: GoodsDataItem) {
            with(binding) {
                tvCartName.text = goods.namaBarang
                tvCartPlace.text = goods.letakBarang
                Glide.with(itemView)
                    .load("${Utils.IMAGE_URL}${goods.gambar}")
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(ivCart)
                    .clearOnDetach()

                btnDelete.setOnClickListener{
                    onItemClickCallback.onItemClicked(goods)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = CartItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listGoods[position])
    }

    override fun getItemCount(): Int {
        return listGoods.size
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: GoodsDataItem?)
    }

}