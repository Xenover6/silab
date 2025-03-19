package com.silab.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silab.R
import com.silab.data.model.GoodsDataItem
import com.silab.databinding.HomeItemContainerBinding
import com.silab.utils.Utils

class GoodsAdapter(
    private var listGoods: MutableList<MutableList<GoodsDataItem>>,
    private val token: String?
) : RecyclerView.Adapter<GoodsAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(private val binding: HomeItemContainerBinding,
                         private val onItemClickCallback: OnItemClickCallback,
                         private val token: String?
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(listGoods: MutableList<GoodsDataItem>) {
            var listBorrowGoods: MutableList<GoodsDataItem> = mutableListOf()
            var borrowCount = 0
            var available = listGoods.count() - borrowCount
            var count = listGoods.count()

            with(binding) {
                if (listGoods.isNotEmpty()) { // Pastikan listGoods tidak kosong sebelum mengakses elemen
                    tvGoodsName.text = listGoods[0].namaBarang
                    tvGoodsPlace.text = listGoods[0].letakBarang
                    tvGoodsAvailability.text = "Tersedia $available/$count"
                    borrowedCount.text = "$borrowCount"
                    if (token.isNullOrEmpty()) {
                        binding.pinjamWidget.visibility = View.GONE
                    }
                    Glide.with(itemView)
                        .load("${Utils.IMAGE_URL}${listGoods[0].gambar}")
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(ivGoods)
                        .clearOnDetach()

                    btnIncrement.setOnClickListener {
                        if (listBorrowGoods.size < count) { // Pastikan jumlah pinjaman tidak melebihi jumlah barang
                            listBorrowGoods.add(listGoods[borrowCount]) // Tambah barang pertama dari listGoods
                            borrowCount++
                            available = count - borrowCount
                            borrowedCount.text = "$borrowCount"
                            tvGoodsAvailability.text = "Tersedia $available/$count"
                        }
                    }

                    btnDecrement.setOnClickListener {
                        if (listBorrowGoods.isNotEmpty()) { // Pastikan ada barang yang bisa dihapus
                            listBorrowGoods.removeAt(listBorrowGoods.size - 1)
                            borrowCount--
                            available = count - borrowCount
                            borrowedCount.text = "$borrowCount"
                            tvGoodsAvailability.text = "Tersedia $available/$count"
                        }
                    }

                    btnCart.setOnClickListener {
                        onItemClickCallback.onItemClicked(listBorrowGoods)
                    }
                } else {
                    tvGoodsName.text = "Tidak ada barang"
                    tvGoodsPlace.text = "-"
                    tvGoodsAvailability.text = "Tersedia 0/0"
                    borrowedCount.text = "0"
                    Glide.with(itemView).clear(ivGoods)
                    btnIncrement.isEnabled = false
                    btnDecrement.isEnabled = false
                    btnCart.isEnabled = false
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = HomeItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding, onItemClickCallback, token)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listGoods[position])
    }

    override fun getItemCount(): Int {
        return listGoods.size
    }

    fun updateData(newListGoods: MutableList<MutableList<GoodsDataItem>>) {
        listGoods.clear()
        listGoods.addAll(newListGoods)
        notifyDataSetChanged() // Memberitahu adapter bahwa datanya telah berubah
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MutableList<GoodsDataItem>)
    }

}