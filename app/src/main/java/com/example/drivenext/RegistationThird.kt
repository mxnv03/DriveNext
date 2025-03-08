package com.example.drivenext

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.drivenext.data.User
import com.example.drivenext.viewmodel.UserViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class RegistationThird : BaseActivity() {
    private lateinit var nextButton: LinearLayout
    private lateinit var backButton: ImageView
    private lateinit var pravaInput: EditText
    private lateinit var dateInput: EditText

    private lateinit var errorTextPrava: TextView
    private lateinit var errorTextDate: TextView

    private lateinit var calendarIcon: ImageView

    private lateinit var userViewModel: UserViewModel
    private var userId: Long = -1 // id пользователя

    // фото
    private lateinit var userPhoto: ImageView
    private lateinit var pravaPhoto: FrameLayout
    private lateinit var passportPhoto: FrameLayout

    private lateinit var pravaImageView: ImageView
    private lateinit var passportImageView: ImageView

    private var currentPhotoType: Int = 0
    private var photoUrl: String = "" // Сохраняем ссылку на фото профиля

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 1

        private const val TYPE_AVATAR = 100
        private const val TYPE_PRAVA = 101
        private const val TYPE_PASSPORT = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_third_step)

        // Получаем userId из предыдущего экрана
        userId = intent.getLongExtra("USER_ID", -1)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Инициализация элементов
        nextButton = findViewById(R.id.button_layout)
        backButton = findViewById(R.id.backButton)
        errorTextPrava = findViewById(R.id.errorTextPrava)
        errorTextDate = findViewById(R.id.errorTextDate)

        pravaInput = findViewById(R.id.pravaInput)
        dateInput = findViewById(R.id.dateInput)

        calendarIcon = findViewById(R.id.calendarIcon)
        userPhoto = findViewById(R.id.addAvatar)
        pravaPhoto = findViewById(R.id.add_prava_photo)
        passportPhoto = findViewById(R.id.add_passport_photo)

        pravaImageView = ImageView(this)
        passportImageView = ImageView(this)

        // Выбор фото профиля
        userPhoto.setOnClickListener {
            currentPhotoType = TYPE_AVATAR
            showPhotoOptions()
        }

        // Выбор фото ВУ
        pravaPhoto.setOnClickListener {
            currentPhotoType = TYPE_PRAVA
            showPhotoOptions()
        }

        // Выбор фото паспорта
        passportPhoto.setOnClickListener {
            currentPhotoType = TYPE_PASSPORT
            showPhotoOptions()
        }

        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this) { user: User? ->
                user?.let {
                    nextButton.setOnClickListener {
                        val prava = pravaInput.text.toString()
                        val datePrava = dateInput.text.toString()

                        if (validateInput(prava, datePrava)) {
                            val updatedUser = User(
                                id = user.id,
                                firstName = user.firstName,
                                lastName = user.lastName,
                                patronymic = user.patronymic,
                                dateOfBirth = user.dateOfBirth,
                                email = user.email,
                                password = user.password,
                                driverLicense = prava,
                                sex = user.sex,
                                registration_date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                photo_url = photoUrl // Сохраняем ссылку на аватар
                            )
                            userViewModel.updateUser(updatedUser)
                            startActivity(Intent(this, Congratulations::class.java))
                            finish()
                        }
                    }
                }
            }
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, RegistationSecond::class.java))
        }

        calendarIcon.setOnClickListener {
            showDatePicker()
        }

        // расстановка пробелов в поле с правами
        pravaInput.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s.isNullOrEmpty()) return
                isEditing = true

                val digits = s.toString().replace(" ", "") // Убираем пробелы
                val formatted = StringBuilder()

                for (i in digits.indices) {
                    formatted.append(digits[i])
                    if (i == 1 || i == 3) formatted.append(" ") // Вставляем пробелы после двух и четырёх символов
                }

                pravaInput.setText(formatted.toString())
                pravaInput.setSelection(formatted.length)

                isEditing = false
            }
        })
    }

    // Отображение выбора источника фото (галерея)
    private fun showPhotoOptions() {
        val options = arrayOf("Выбрать из галереи")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Выберите действие")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> selectImageInAlbum()
            }
        }
        builder.show()
    }

    // Выбрать фото из галереи
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    // Обработка выбора фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                    val imageUri: Uri? = data?.data // Извлекаем Uri из Intent
                    handleImageSelection(imageUri)
                }
            }
        }
    }

    private fun handleImageSelection(uri: Uri?) {
        if (uri != null) {
            when (currentPhotoType) {
                TYPE_AVATAR -> uploadImageToServer(uri)
                TYPE_PRAVA -> pravaImageView.setImageURI(uri)
                TYPE_PASSPORT -> passportImageView.setImageURI(uri)
            }
            Toast.makeText(this, "Снимок загружен", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ошибка загрузки фото", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToServer(imageUri: Uri) {
        val client = OkHttpClient()

        // Создаем временный файл для загрузки
        val tempFile = createTempFileFromUri(imageUri)
        if (tempFile == null) {
            Toast.makeText(this, "Ошибка: не удалось создать временный файл", Toast.LENGTH_SHORT).show()
            return
        }

        // Указываем имя файла в URL
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://10.0.2.2:9090/upload/$fileName")
            .put(requestBody)
            .header("Content-Type", "image/jpeg")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistationThird, "Ошибка загрузки фото: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val errorBody = response.body?.string()
                if (response.isSuccessful) {
                    // Успешная загрузка
                    val imageUrl = "http://10.0.2.2:9090/upload/$fileName" // URL загруженного фото
                    runOnUiThread {
                        Toast.makeText(this@RegistationThird, "Фото успешно загружено", Toast.LENGTH_SHORT).show()

                        // Сохраняем ссылку на фото в базе данных
                        savePhotoUrlToDatabase(imageUrl)

                        // Отображаем фото в аватарке
                        when (currentPhotoType) {
                            TYPE_AVATAR -> loadImageWithGlide(imageUrl, userPhoto)
                            TYPE_PRAVA -> loadImageWithGlide(imageUrl, pravaImageView)
                            TYPE_PASSPORT -> loadImageWithGlide(imageUrl, passportImageView)
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@RegistationThird,
                            "Ошибка загрузки фото: ${response.code} - ${response.message}\n$errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }

    private fun savePhotoUrlToDatabase(imageUrl: String) {
        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this) { user: User? ->
                user?.let {
                    val updatedUser = User(
                        id = user.id,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        patronymic = user.patronymic,
                        dateOfBirth = user.dateOfBirth,
                        email = user.email,
                        password = user.password,
                        driverLicense = user.driverLicense,
                        sex = user.sex,
                        registration_date = user.registration_date,
                        photo_url = imageUrl // Сохраняем ссылку на фото
                    )
                    userViewModel.updateUser(updatedUser)
                }
            }
        }
    }

    private fun loadImageWithGlide(imageUrl: String, imageView: ImageView) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.avatar) // Заглушка, пока загружается фото
            .error(R.drawable.avatar) // Заглушка, если произошла ошибка
            .into(imageView)
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        val context: Context = this
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        return try {
            inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                return null
            }

            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            outputStream = FileOutputStream(tempFile)

            val buffer = ByteArray(4 * 1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            dateInput.setText(formattedDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun validateInput(prava: String, datePrava: String): Boolean {
        if (prava.isEmpty()) {
            pravaInput.error = "Ошибка"
            errorTextPrava.visibility = View.VISIBLE
            pravaInput.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            pravaInput.error = null
            errorTextPrava.visibility = View.GONE
            pravaInput.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        if (datePrava.isEmpty()) {
            dateInput.error = "Ошибка"
            errorTextDate.visibility = View.VISIBLE
            dateInput.setBackgroundResource(R.drawable.rounded_button_red)
            return false
        } else {
            dateInput.error = null
            errorTextDate.visibility = View.GONE
            dateInput.setBackgroundResource(R.drawable.rounded_button_white_login)
        }

        return true
    }
}