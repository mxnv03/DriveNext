package com.example.drivenext

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import com.example.drivenext.data.User
import com.example.drivenext.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AccountActivity : BaseActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var userGenderTextView: TextView
    private lateinit var userAvatarImageView: ImageView
    private lateinit var userViewModel: UserViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dateJoin: TextView
    private lateinit var changePassword: LinearLayout
    private lateinit var changeMail: LinearLayout
    private lateinit var deleteAccount: LinearLayout

    private var currentPhotoUri: Uri? = null // Для хранения Uri выбранного фото
    private var userId: Long = -1 // ID пользователя

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 1 // Код запроса для выбора фото из галереи
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account)

        // Инициализация элементов разметки
        userNameTextView = findViewById(R.id.textView22)
        userEmailTextView = findViewById(R.id.mail_text)
        userGenderTextView = findViewById(R.id.male_text)
        userAvatarImageView = findViewById(R.id.addAvatar)
        dateJoin = findViewById(R.id.dateJoin)
        changePassword = findViewById(R.id.changePassword)
        changeMail = findViewById(R.id.changeMail)
        deleteAccount = findViewById(R.id.deleteAccount)

        // Инициализация ViewModel и SharedPreferences
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Получаем ID пользователя из SharedPreferences
        userId = sharedPreferences.getLong("user_id", -1)

        if (userId != -1L) {
            // Наблюдаем за данными пользователя
            userViewModel.getUserById(userId).observe(this, Observer { user ->
                user?.let {
                    // Заполняем поля данными пользователя
                    userNameTextView.text = "${it.firstName} ${it.lastName}"
                    userEmailTextView.text = it.email
                    userGenderTextView.text = it.sex
                    dateJoin.text = "Присоединился ${it.registration_date}"

                    // Загружаем фото профиля, если оно есть
                    if (!it.photo_url.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(it.photo_url)
                            .placeholder(R.drawable.settings_avatar)
                            .error(R.drawable.settings_avatar)
                            .circleCrop() // Обрезаем в круг
                            .into(userAvatarImageView)
                    } else {
                        userAvatarImageView.setImageResource(R.drawable.avatar)
                    }
                }
            })
        }

        // Обработчик нажатия на аватар
        userAvatarImageView.setOnClickListener {
            openGalleryForImage()
        }

        changePassword.setOnClickListener {
            startActivity(Intent(this, NewPassword::class.java))
        }

        changeMail.setOnClickListener {
            startActivity(Intent(this, NewMail::class.java))
        }

        deleteAccount.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }

        // Обработчик кнопки "Назад"
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Обработчик кнопки "Выйти"
        val logOutButton: LinearLayout = findViewById(R.id.login_out)
        logOutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("user_id")
            editor.apply()
            startActivity(Intent(this, LoginAuthActivity::class.java))
        }
    }

    // Открываем галерею для выбора фото
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    // Обработка результата выбора фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            val imageUri: Uri? = data?.data // Получаем Uri выбранного фото
            if (imageUri != null) {
                // Загружаем фото на сервер
                uploadImageToServer(imageUri)
            }
        }
    }

    // Загрузка фото на сервер
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
                    Toast.makeText(this@AccountActivity, "Ошибка загрузки фото: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val errorBody = response.body?.string()
                if (response.isSuccessful) {
                    // Успешная загрузка
                    val imageUrl = "http://10.0.2.2:9090/upload/$fileName" // URL загруженного фото
                    runOnUiThread {
                        Toast.makeText(this@AccountActivity, "Фото успешно загружено", Toast.LENGTH_SHORT).show()

                        // Сохраняем ссылку на фото в базе данных
                        savePhotoUrlToDatabase(imageUrl)

                        // Отображаем новое фото с помощью Glide
                        Glide.with(this@AccountActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.settings_avatar)
                            .error(R.drawable.settings_avatar)
                            .circleCrop() // Обрезаем в круг
                            .into(userAvatarImageView)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AccountActivity,
                            "Ошибка загрузки фото: ${response.code} - ${response.message}\n$errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }

    // Сохраняем ссылку на фото в базе данных
    private fun savePhotoUrlToDatabase(imageUrl: String) {
        if (userId != -1L) {
            userViewModel.getUserById(userId).observe(this, Observer { user ->
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
            })
        }
    }

    private fun showDeleteAccountConfirmationDialog() {
        val alertDialog = android.app.AlertDialog.Builder(this)
            .setTitle("Удаление аккаунта")
            .setMessage("Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { dialog, _ ->
                deleteUserAccount()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun deleteUserAccount() {
        if (userId != -1L) {
            // Используем корутину с Dispatchers.IO для выполнения операции в фоновом потоке
            lifecycleScope.launch(Dispatchers.IO) {
                val deletedRows = userViewModel.deleteUser(userId)
                if (deletedRows > 0) {
                    // Очищаем SharedPreferences
                    val editor = sharedPreferences.edit()
                    editor.remove("user_id")
                    editor.apply()

                    // Перенаправляем пользователя на экран авторизации
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AccountActivity, "Аккаунт успешно удален", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AccountActivity, LoginAuthActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AccountActivity, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
        }
    }

    // Создаем временный файл из Uri
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
}