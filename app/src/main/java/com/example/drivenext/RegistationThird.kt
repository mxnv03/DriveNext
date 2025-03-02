package com.example.drivenext

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.*

import com.example.drivenext.viewmodel.UserViewModel
import com.example.drivenext.data.User

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

    companion object {
        private const val REQUEST_TAKE_PHOTO = 0
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
                                registration_date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
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


    // Отображение выбора источника фото (галерея или камера)
    private fun showPhotoOptions() {
        val options = arrayOf("Выбрать из галереи", "Сделать фото")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Выберите действие")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> selectImageInAlbum()
                1 -> takePhoto()
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

    // Сделать фото камерой
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    // Обработка выбора фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        Toast.makeText(this, "Снимок загружен", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка загрузки фото", Toast.LENGTH_SHORT).show()
                    }
                }

                REQUEST_TAKE_PHOTO -> {
                    val photo: Bitmap? = data?.extras?.get("data") as? Bitmap
                    if (photo != null) {
                        Toast.makeText(this, "Снимок загружен", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Ошибка снимка", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
