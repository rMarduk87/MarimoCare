package rpt.tool.marimocare.ui.marimo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rpt.tool.marimocare.BaseFragment
import rpt.tool.marimocare.R
import rpt.tool.marimocare.databinding.FragmentAddOrEditBinding
import rpt.tool.marimocare.utils.AppUtils
import rpt.tool.marimocare.utils.data.appmodels.Marimo
import rpt.tool.marimocare.utils.managers.RepositoryManager
import rpt.tool.marimocare.utils.navigation.safeNavController
import rpt.tool.marimocare.utils.navigation.safeNavigate
import rpt.tool.marimocare.utils.view.HeaderButtonConfig
import rpt.tool.marimocare.utils.view.HeaderHelper
import rpt.tool.marimocare.utils.view.adapters.CustomSpinnerAdapter
import rpt.tool.marimocare.utils.view.adapters.ImagePrintAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import androidx.core.net.toUri
import android.content.ContentValues
import android.graphics.PorterDuff
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.balloon
import rpt.tool.marimocare.utils.balloon.photo.MarimoPhotoInfoBalloonFactory
import rpt.tool.marimocare.utils.managers.SharedPreferencesManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import rpt.tool.marimocare.utils.balloon.newMarimo.MarimoAddNewInfoBalloonFactory

class AddOrEditMarimoFragment :
    BaseFragment<FragmentAddOrEditBinding>(FragmentAddOrEditBinding::inflate) {

    private var photoFile: File? = null
    private lateinit var frequencies: List<String>
    private var freq: Int = 0
    private var marimoCode: Int = 0
    private val args: AddOrEditMarimoFragmentArgs by navArgs()
    private var marimo: Marimo? = null
    private var qrCodeBtnEnabled: Boolean = false
    private val marimoBalloon by balloon<MarimoPhotoInfoBalloonFactory>()
    private val marimoAddNewBalloon by balloon<MarimoAddNewInfoBalloonFactory>()
    private var photoUri: Uri? = null
    private var marimoPhotoPath: String? = null
    private val REQUEST_CAMERA = 1001
    private val REQUEST_GALLERY = 1002

    private fun takePhotoWithCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = File(requireContext().cacheDir,
            "marimo_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(requireContext(),
            "${requireContext().packageName}.provider", photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CAMERA)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frequencies = resources.getStringArray(R.array.marimo_frequencies).toList()

        setupHeaderButtons()
        setupSpinner(frequencies)
        setupDatePicker()

        marimoCode = args.MarimoCode

        qrCodeBtnEnabled = marimoCode != 0

        binding.btnQrCode.isEnabled = qrCodeBtnEnabled

        addDataToMarimo(marimoCode)

        binding.btnQrCode.setOnClickListener {
            manageQRCode()
        }

        if(SharedPreferencesManager.showBallonFirstTime){
            SharedPreferencesManager.showBallonFirstTime = false
            marimoBalloon.showAlign(
                align = BalloonAlign.BOTTOM,
                mainAnchor = binding.marimoPicture as View,
                subAnchorList = listOf(binding.marimoPicture as View),
            )
        }

        binding.marimoPicture.setOnClickListener {
            showPhotoChooserDialog()
        }

        binding.marimoPicture.setOnLongClickListener {
            photoFile?.let { file ->
                if (file.exists()) showPhotoPreview(file)
            }
            true
        }
    }

    private fun setupHeaderButtons() {
        HeaderHelper.setupHeaderButtons(
            requireContext(),
            listOf(
                HeaderButtonConfig(
                    button = binding.include1.btnDashboardHeader,
                    iconRes = R.drawable.ic_dashboard,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.dashboard),
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections
                                .actionAddOrEditFragmentToDashboardFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnAddMarimoHeader,
                    iconRes = R.drawable.ic_add,
                    colorRes = R.color.marimo_item_green,
                    backgroundRes = R.drawable.bg_button_light_green,
                    enabled = true,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.add_marimo),
                    onClick = {
                        clearAll()
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenSettings,
                    iconRes = R.drawable.ic_settings,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.settings),
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections
                                .actionAddOrEditFragmentToSettingsFragment()
                        )
                    }
                ),
                HeaderButtonConfig(
                    button = binding.include1.btnOpenStats,
                    iconRes = R.drawable.ic_stats,
                    colorRes = R.color.marimo_add_icon,
                    backgroundRes = R.drawable.bg_button_white,
                    isTablet = resources.configuration.smallestScreenWidthDp >= 600,
                    text = requireContext().getString(R.string.stats),
                    onClick = {
                        safeNavController?.safeNavigate(
                            AddOrEditMarimoFragmentDirections
                                .actionAddOrEditFragmentToStatsFragment()
                        )
                    }
                )
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSpinner(frequencies: List<String>) {
        val spinner = binding.marimoSpinnerLayout.customSpinner
        val arrow = binding.marimoSpinnerLayout.arrow
        val adapter = CustomSpinnerAdapter(requireContext(), frequencies)
        spinner.adapter = adapter
        spinner.setPopupBackgroundDrawable(ContextCompat.getDrawable(requireContext(),
            R.drawable.spinner_dropdown_background))

        val rotateUp = AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_up)
        val rotateDown = AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_down)

        spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) arrow.startAnimation(rotateUp)
            false
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int,
                                        id: Long) {
                adapter.setSelectedIndex(position)
                arrow.startAnimation(rotateDown)
                freq = AppUtils.extractDay(frequencies[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                arrow.startAnimation(rotateDown)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDatePicker() {
        binding.inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.inputDate.setText(SimpleDateFormat("yyyy-MM-dd").
                    format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = AppUtils.getMaxDate()
                setTitle("")
                show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupActionButtons(marimo: Marimo?) {
        binding.btnAdd.setOnClickListener { saveMarimoIfValid(marimo) }
        binding.btnCancel.setOnClickListener { clearAll() }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMarimoIfValid(marimo: Marimo?) {
        val name = binding.inputName.text.toString()
        val lastWater = binding.inputDate.text.toString()
        val notes = binding.inputNotes.text.toString()

        if (name.isBlank() || lastWater.isBlank()) {
            Toast.makeText(requireContext(), getString(
                R.string.please_fill_all_the_fields),
                Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if(marimo != null) {
                RepositoryManager.marimoRepository.updateMarimo(
                    marimo.code,
                    name,
                    lastWater, notes, freq,marimoPhotoPath
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), marimo.name + " " + getString(
                        R.string.updated_ok),
                        Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({

                        if(SharedPreferencesManager.showBallonFirstUpdate){
                            SharedPreferencesManager.showBallonFirstUpdate = false
                            marimoAddNewBalloon.showAlign(
                                align = BalloonAlign.BOTTOM,
                                mainAnchor = binding.include1.btnAddMarimoHeader as View,
                                subAnchorList = listOf(binding.include1.btnAddMarimoHeader as View),
                            )
                        }
                    }, 1100)
                }
            }
            else{
                val id = RepositoryManager.marimoRepository.addMarimo(name,
                    lastWater, notes, freq,marimoPhotoPath)

                RepositoryManager.marimoRepository.addWaterChanges(id, lastWater)

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), getString(
                        R.string.new_marimo_added),
                        Toast.LENGTH_SHORT).show()
                    qrCodeBtnEnabled = true
                    binding.btnQrCode.isEnabled = qrCodeBtnEnabled
                    marimoCode = id
                    clearAll()
                }
            }
        }
    }

    private fun clearAll() {
        binding.inputName.text.clear()
        binding.inputDate.text.clear()
        binding.inputNotes.text.clear()
        binding.marimoSpinnerLayout.customSpinner.setSelection(0)
        val imageView = binding.marimoPicture
        imageView.setImageResource(R.drawable.ic_water_drop_white)
        imageView.setColorFilter("#00BFA6".toColorInt(), PorterDuff.Mode.SRC_IN)
        marimoPhotoPath = ""
        binding.title.text = getString(R.string.add_new_marimo)
        binding.subtitle.text = getString(R.string.add_a_new_marimo_friend_to_track)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDataToMarimo(marimoCode: Int) {
        if (marimoCode != 0) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                marimo = RepositoryManager.marimoRepository.getMarimo(marimoCode)
                withContext(Dispatchers.Main) {
                    if (marimo != null) {
                        binding.inputName.setText(marimo!!.name)
                        binding.inputDate.setText(marimo!!.lastChanged)
                        binding.inputNotes.setText(marimo!!.notes)

                        val index = AppUtils.indexOfContaining(
                            marimo!!.changeFrequencyDays.toString(), frequencies
                        )
                        binding.marimoSpinnerLayout.customSpinner
                            .setSelection(if (index != -1) index else 0)

                        binding.btnAdd.text = getString(R.string.update)
                        marimoPhotoPath = marimo!!.photo
                    }

                    setupActionButtons(marimo)
                    if(!marimo!!.photo.isNullOrEmpty()){
                        showMarimoImage(File(marimo!!.photo!!))
                    }
                    else{
                        showMarimoImage(null)
                    }

                    binding.title.text = buildString {
                        append(getString(R.string.edit_marimo))
                        append(" ${marimo!!.name}")
                    }

                    binding.subtitle.text = getString(R.string.modify_marimo)

                }
            }
        } else {
            // Caso nuovo marimo
            setupActionButtons(null)
            showMarimoImage(null)
            binding.title.text = getString(R.string.add_new_marimo)
            binding.subtitle.text = getString(R.string.add_a_new_marimo_friend_to_track)
        }
    }

    private fun manageQRCode() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val marimo = RepositoryManager.marimoRepository.getMarimo(marimoCode)
            val qrCode = AppUtils.generateQRCode(marimo)
            val qrCodeToStore = AppUtils.bitMapToString(qrCode)
            RepositoryManager.marimoRepository.addMarimoQR(marimoCode, qrCodeToStore)
            withContext(Dispatchers.Main) {
                showMarimoQR(marimo,qrCode)
            }
        }
    }

    private fun showMarimoQR(marimo: Marimo?, qrCode: Bitmap) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_qr_code_marimo)
        dialog.window?.setBackgroundDrawable(Color.WHITE.toDrawable())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        val icon = dialog.findViewById<ImageView>(R.id.qrCode)
        val qrTitle = dialog.findViewById<TextView>(R.id.qrText)
        qrTitle.text = buildString {
            append(requireContext().getString(R.string.qr_code_for_marimo))
            append(" ")
            append(marimo?.name)
            append(" ")
            append(requireContext().getString(R.string.qr_code_you_can_scan_it_later))
        }

        icon.setImageBitmap(qrCode)

        dialog.findViewById<ImageView>(R.id.btnCloseDialog).setOnClickListener {
            dialog.dismiss() }

        dialog.findViewById<Button>(R.id.btnQrActions).setOnClickListener {
            manageQRActions(qrCode) }

        dialog.show()
    }

    private fun manageQRActions(qrCode: Bitmap) {
        val options =  resources.getStringArray(R.array.marimo_qr_actions)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.what))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> printQr(qrCode)      // Stampa QR
                    1 -> shareQr(qrCode)      // Condivisione
                    2 -> saveQr(qrCode)       // Salva QR)
                }
            }
            .show()
    }

    private fun printQr(qrCode: Bitmap) {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${getString(R.string.app_name)} QR"

        val printAdapter = ImagePrintAdapter(requireContext(), qrCode)
        printManager.print(jobName, printAdapter, null)
    }

    private fun shareQr(qrCode: Bitmap) {
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver
            , qrCode, "QR Code", null)
        val uri = path.toUri()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_qr)))
    }

    private fun saveQr(qrCode: Bitmap): Uri?{
        val mimeType = "image/png"
        var uri: Uri? = null
        var outputStream: OutputStream? = null
        val fileName = "qr_${System.currentTimeMillis()}.png"

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES +
                            "/MarimoCare")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val resolver = requireContext().contentResolver
                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                outputStream = uri?.let { resolver.openOutputStream(it) }

                if (outputStream != null) {
                    qrCode.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                uri?.let { resolver.update(it, values, null, null) }

            } else {

                val imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES + "/MarimoCare"
                )

                if (!imagesDir.exists()) imagesDir.mkdirs()

                val image = File(imagesDir, fileName)
                outputStream = FileOutputStream(image)
                qrCode.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                uri = Uri.fromFile(image)
            }

            Toast.makeText(context, getString(R.string.qr_saved),
                Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.save_error),
                Toast.LENGTH_SHORT).show()
        } finally {
            outputStream?.close()
        }

        return uri
    }

    private fun showMarimoImage(file: File?) {
        val imageView = binding.marimoPicture

        imageView.clearColorFilter()
        imageView.imageTintList = null
        imageView.background = null

        if (file != null && file.exists()) {
            Glide.with(this)
                .load(file)
                .centerCrop()
                .placeholder(R.drawable.ic_water_drop_white)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_water_drop_white)
            imageView.setColorFilter("#00BFA6".toColorInt(), PorterDuff.Mode.SRC_IN)
        }
    }


    private fun showPhotoPreview(file: File) {
        val dialog = Dialog(requireContext(), android.R.style.
                Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_marimo_photo_preview)

        val photoView = dialog.findViewById<com.github.chrisbanes.photoview.PhotoView>(
            R.id.photoPreview)
        val closeBtn = dialog.findViewById<ImageView>(R.id.btnClosePreview)

        Glide.with(this)
            .load(file)
            .into(photoView)

        closeBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showPhotoChooserDialog() {
        val options = arrayOf(
            getString(R.string.take_photo),
            getString(R.string.choose_from_gallery)
        )

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.photo))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhotoWithCamera()
                    1 -> pickPhotoFromGallery()
                }
            }
            .show()
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {

            REQUEST_CAMERA -> {
                photoFile?.let {
                    if (it.exists() && it.length() > 0) {
                        showMarimoImage(it)
                        marimoPhotoPath = it.absolutePath
                        marimo?.photo = it.absolutePath
                    }
                }
            }

            REQUEST_GALLERY -> {
                data?.data?.let { uri ->
                    val file = copyUriToInternalFile(uri)
                    photoFile = file
                    photoUri = file.toUri()

                    showMarimoImage(file)
                    marimoPhotoPath = file.absolutePath
                    marimo?.photo = file.absolutePath
                }
            }
        }
    }

    private fun copyUriToInternalFile(uri: Uri): File {
        val file = File(requireContext().filesDir,
            "marimo_${System.currentTimeMillis()}.jpg")

        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

}