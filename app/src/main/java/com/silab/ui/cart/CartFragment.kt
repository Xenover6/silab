package com.silab.ui.cart

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.silab.MainActivity
import com.silab.R
import com.silab.data.model.GoodsDataItem
import com.silab.databinding.FragmentCartBinding
import com.silab.di.ViewModelFactory
import com.silab.ui.adapter.CartItemAdapter
import com.silab.ui.home.HomeViewModel
import com.silab.utils.LoadingDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val cartViewModel : CartViewModel by activityViewModels{
        ViewModelFactory.getInstance(requireContext(), requireActivity().application)
    }
    private val homeViewModel : HomeViewModel by activityViewModels{
        ViewModelFactory.getInstance(requireContext(), requireActivity().application)
    }
    private val binding get() = _binding!!

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private var startDateBorrow: String = ""
    private var endDateBorrow: String = ""
    private var selectedFileUri: Uri? = null
    private var selectedFileTextView: TextView? = null

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileUri = uri
        if (uri != null) {
            val path = uri.path
            val file = File(path!!)
            selectedFileTextView?.text = file.name
        } else {
            selectedFileTextView?.text = getString(R.string.pilih)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val loadingDialog = LoadingDialog(requireActivity())
        val toolbar: Toolbar = binding.toolbarCart.toolbar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        binding.toolbarCart.toolbarTitle.text = getString(R.string.keranjang_anda)

        cartViewModel.listCartItem.observe(viewLifecycleOwner) {goods ->
            goods?.let {
                if (it.isEmpty()) {
                    binding.rvCart.visibility = View.GONE
                    binding.emptyContainer.emptyContainer.visibility = View.VISIBLE
                    binding.btnBorrow.visibility = View.GONE
                } else {
                    binding.emptyContainer.emptyContainer.visibility = View.GONE
                    binding.btnBorrow.visibility = View.VISIBLE
                    showCartItemRecycleList(it.map { item -> item!! }.toMutableList())
                }
            }
        }

        cartViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) loadingDialog.startLoadingDialog() else loadingDialog.dismissDialog()
        }

        cartViewModel.navigateToBorrow.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                lifecycleScope.launch {
                    showSuccessDialog()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("navigate_to", "borrow")
                    startActivity(intent)
                }

            }
        }

        binding.btnBorrow.setOnClickListener {
            selectedFileTextView?.text = null
            showModalDialog()
        }

        return root
    }

    private fun showCartItemRecycleList(listAdapter: MutableList<GoodsDataItem>) {
        if (listAdapter.isNotEmpty()) {
            binding.emptyContainer.emptyContainer.visibility = View.GONE
            binding.rvCart.visibility = View.VISIBLE
            binding.rvCart.layoutManager = LinearLayoutManager(context)
            val cartAdapter = CartItemAdapter(listAdapter)
            cartAdapter.setOnItemClickCallback(object : CartItemAdapter.OnItemClickCallback {
                override fun onItemClicked(data: GoodsDataItem?) {
                    cartViewModel.removeFromCart(goods = data!!)
                    homeViewModel.addGoods(data)
                }
            })
            binding.rvCart.adapter = cartAdapter

        } else {
            binding.rvCart.visibility = View.GONE
            binding.emptyContainer.emptyContainer.visibility = View.VISIBLE
        }
    }

    private fun showModalDialog() {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.modal_input, null)

        val name: EditText = dialogView.findViewById(R.id.etName)
        val address: EditText = dialogView.findViewById(R.id.etAddress)
        val phone: EditText = dialogView.findViewById(R.id.etPhone)
        val datePicker1: Button = dialogView.findViewById(R.id.datePicker1)
        val datePicker2: Button = dialogView.findViewById(R.id.datePicker2)
        val filePicker: Button = dialogView.findViewById(R.id.filePicker)
        selectedFileTextView = dialogView.findViewById(R.id.tvSelectedFile)

        // Variables for start and end date
        var startDateBorrow = ""
        var endDateBorrow = ""

        datePicker1.setOnClickListener {
            showDatePicker { date ->
                startDateBorrow = date
                datePicker1.text = formatDateToDisplay(date)
                // Update minDate for the second date picker
                datePicker2.isEnabled = true
            }
        }

        datePicker2.setOnClickListener {
            if (startDateBorrow.isNotEmpty()) {
                showDatePicker(minDate = startDateBorrow) { date ->
                    endDateBorrow = date
                    datePicker2.text = formatDateToDisplay(date)
                }
            } else {
                Snackbar.make(binding.root, "Pilih tanggal mulai terlebih dahulu", Snackbar.LENGTH_LONG).show()
            }
        }

        // Handle File Picker
        filePicker.setOnClickListener {
            checkPermissionAndPickFile()
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("")
            .setPositiveButton("Kirim") { _, _ ->
                val inputName = name.text.toString()
                val inputAddress = address.text.toString()
                val inputPhone = phone.text.toString()

                if (inputName.isNotEmpty() && inputAddress.isNotEmpty() && inputPhone.isNotEmpty() &&
                    startDateBorrow.isNotEmpty() && endDateBorrow.isNotEmpty() && selectedFileUri != null
                ) {
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(selectedFileUri!!)
                        val tempFile = File(requireContext().cacheDir, "uploaded_file")
                        inputStream?.use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }

                        val mimeType = requireContext().contentResolver.getType(selectedFileUri!!) ?: "application/octet-stream"

                        val requestImageFileRB = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

                        val imagePart = MultipartBody.Part.createFormData(
                            "surat_tugas",
                            tempFile.name,
                            requestImageFileRB
                        )

                        cartViewModel.postBorrowRequest(
                            inputName,
                            inputAddress,
                            inputPhone,
                            startDateBorrow,
                            endDateBorrow,
                            imagePart
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Snackbar.make(binding.root, "Gagal memproses file: ${e.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Snackbar.make(binding.root, "Pastikan semuanya sudah diisi", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
            .setNegativeButton("Batal", null)
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showDatePicker(minDate: String? = null, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        minDate?.let {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDate = dateFormat.parse(minDate)
                startDate?.let {
                    calendar.time = it
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                // Format the date as YYYY-MM-DD
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun formatDateToDisplay(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: Date())
        } catch (e: ParseException) {
            e.printStackTrace()
            date // Return the original date if formatting fails
        }
    }

    private fun checkPermissionAndPickFile() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33): Gunakan READ_MEDIA_* izin
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    filePickerLauncher.launch("*/*")
                } else {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Android 10 - 12 (API 29 - 32): Gunakan READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    filePickerLauncher.launch("*/*")
                } else {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
            else -> {
                // Android 9 dan sebelumnya (API < 29): Gunakan READ_EXTERNAL_STORAGE & WRITE_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    filePickerLauncher.launch("*/*")
                } else {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }



    private suspend fun showSuccessDialog() {
        suspendCancellableCoroutine{ continuation ->
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Sukses")
            builder.setMessage("Anda berhasil mengajukan peminjaman")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                continuation.resume(Unit)
            }
            val dialog = builder.create()
            dialog.setOnCancelListener {
                continuation.resume(Unit)
            }
            dialog.show()
        }
    }
}