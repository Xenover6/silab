package com.silab.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.silab.R
import com.silab.data.model.BorrowDataItem
import com.silab.databinding.BorrowItemContainerBinding
import com.silab.utils.Utils
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class BorrowAdapter(
    private var listBorrow: MutableList<BorrowDataItem>
) : RecyclerView.Adapter<BorrowAdapter.ListViewHolder>() {


    class ListViewHolder(private val binding: BorrowItemContainerBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(borrow: BorrowDataItem) {
            with(binding) {
                tvBorrowName.text = borrow.namaBarang
                tvBorrowPlace.text = borrow.letakBarang
                when (borrow.status) {
                    "disetujui" -> {
                        calculateDaysSince(borrow, tvBorrowStatus)
                    }
                    "menunggu persetujuan" -> {
                        tvBorrowStatus.text = borrow.status
                        tvBorrowStatus.setBackgroundResource(R.drawable.goods_yellow_background)
                    }

                    else -> {
                        tvBorrowStatus.text = borrow.barang?.kondisiBarang
                    }
                }
                Glide.with(itemView.context)
                    .load("${Utils.IMAGE_URL}${borrow.barang?.gambar}")
                    .placeholder(R.drawable.placeholder_image)
                    .centerCrop()
                    .error(R.drawable.error_image)
                    .into(ivBorrow)
                    .clearOnDetach()
            }
        }

        private fun calculateDaysSince(borrow: BorrowDataItem, tvBorrowStatus: TextView) {
            // Gunakan format yyyy-MM-dd untuk tanggal saja
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Parse tanggal akhir peminjaman menggunakan LocalDate
            val borrowEndDate = LocalDate.parse(borrow.tanggalPengembalian, formatter)

            // Ambil tanggal saat ini di zona waktu Asia/Jakarta (GMT+7)
            val jakartaZoneId = ZoneId.of("Asia/Jakarta")
            val currentDate = ZonedDateTime.now(jakartaZoneId)

            // Konversi tanggal akhir ke ZonedDateTime dengan zona waktu Asia/Jakarta
            val borrowEndZonedDateTime = borrowEndDate.atStartOfDay(jakartaZoneId)
            val currentZonedDateTime = currentDate.toLocalDate().atStartOfDay(jakartaZoneId) // Waktu saat ini pada pukul 00:00
            val days = ChronoUnit.DAYS.between(borrowEndZonedDateTime, currentZonedDateTime).absoluteValue

            // Jika tanggal saat ini sudah melewati tanggal akhir
            if (currentDate.isAfter(borrowEndZonedDateTime)) {
                // Hitung selisih hari antara tanggal akhir peminjaman dan tanggal saat ini
                if (days.toInt() == 0) {
                    tvBorrowStatus.text = "Sisa hari ini"
                    tvBorrowStatus.setBackgroundResource(R.drawable.goods_condition_background)
                } else {
                    tvBorrowStatus.text = "Telat $days hari"
                    tvBorrowStatus.setBackgroundResource(R.drawable.goods_red_background)
                }
            } else {
                // Hitung selisih hari antara tanggal saat ini dan tanggal akhir peminjaman
                tvBorrowStatus.text = "$days hari lagi"
                tvBorrowStatus.setBackgroundResource(R.drawable.goods_condition_background)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = BorrowItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listBorrow[position])
    }

    override fun getItemCount(): Int {
        return listBorrow.size
    }

}