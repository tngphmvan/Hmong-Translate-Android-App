package com.example.hmong_translate.ui

import android.app.Application
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmong_translate.data.ApiService
import com.example.hmong_translate.utils.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Enum biểu diễn hướng dịch ngôn ngữ.
 *
 * - HMONG_TO_VIET: Dịch từ tiếng H'Mông sang tiếng Việt
 * - VIET_TO_HMONG: Dịch từ tiếng Việt sang tiếng H'Mông
 */
enum class TranslationDirection {
    HMONG_TO_VIET,
    VIET_TO_HMONG
}

/**
 * Enum biểu diễn trạng thái hiện tại của UI.
 */
enum class UiState {
    /** Trạng thái ban đầu, chưa thực hiện hành động */
    IDLE,

    /** Đang ghi âm */
    RECORDING,

    /** Đang xử lý (upload & dịch) */
    PROCESSING,

    /** Dịch thành công */
    SUCCESS,

    /** Có lỗi xảy ra */
    ERROR
}

/**
 * Data class chứa kết quả dịch.
 *
 * @property sourceText Văn bản nguồn (ngôn ngữ ban đầu)
 * @property targetText Văn bản đích sau khi dịch
 * @property audioFile File audio kết quả (chỉ có khi dịch sang H'Mông)
 */
data class TranslationResult(
    val sourceText: String = "",
    val targetText: String = "",
    val audioFile: File? = null
)

/**
 * ViewModel chịu trách nhiệm:
 * - Ghi âm giọng nói
 * - Upload audio lên server
 * - Nhận kết quả dịch
 * - Quản lý trạng thái UI cho Jetpack Compose
 * - Phát lại audio kết quả
 */
class TranslateViewModel(application: Application) : AndroidViewModel(application) {

    /** Helper class dùng để ghi âm */
    private val audioRecorder = AudioRecorder(application)

    /** MediaPlayer dùng để phát audio kết quả */
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Trạng thái hiện tại của UI.
     * Được expose dưới dạng read-only cho UI layer.
     */
    var uiState by mutableStateOf(UiState.IDLE)
        private set

    /**
     * Hướng dịch hiện tại.
     */
    var direction by mutableStateOf(TranslationDirection.HMONG_TO_VIET)
        private set

    /**
     * Kết quả dịch hiện tại.
     */
    var result by mutableStateOf(TranslationResult())
        private set

    /**
     * Thông báo lỗi nếu có.
     */
    var errorMessage by mutableStateOf("")
        private set

    /**
     * ApiService dùng Retrofit để giao tiếp backend.
     */
    private val apiService: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Emulator localhost
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Đổi hướng dịch (H'Mông ↔ Việt) và reset trạng thái UI.
     */
    fun toggleDirection() {
        direction = if (direction == TranslationDirection.HMONG_TO_VIET) {
            TranslationDirection.VIET_TO_HMONG
        } else {
            TranslationDirection.HMONG_TO_VIET
        }
        resetState()
    }

    /**
     * Reset trạng thái UI và kết quả dịch.
     */
    private fun resetState() {
        uiState = UiState.IDLE
        result = TranslationResult()
        errorMessage = ""
    }

    /**
     * Bắt đầu ghi âm giọng nói.
     */
    fun startRecording() {
        try {
            resetState()
            audioRecorder.startRecording()
            uiState = UiState.RECORDING
        } catch (e: Exception) {
            errorMessage = "Could not start recording: ${e.message}"
            uiState = UiState.ERROR
        }
    }

    /**
     * Dừng ghi âm và bắt đầu upload audio lên server.
     */
    fun stopRecording() {
        if (uiState != UiState.RECORDING) return

        val file = audioRecorder.stopRecording()
        if (file != null) {
            uiState = UiState.PROCESSING
            uploadAudio(file)
        } else {
            errorMessage = "Recording failed"
            uiState = UiState.ERROR
        }
    }

    /**
     * Upload file audio lên backend để thực hiện dịch.
     *
     * @param file File audio được ghi âm
     */
    private fun uploadAudio(file: File) {
        viewModelScope.launch {
            try {
                val mimeType = if (file.name.endsWith(".wav")) "audio/wav" else "audio/mpeg"
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

                if (direction == TranslationDirection.HMONG_TO_VIET) {
                    val response = apiService.translateHmongToViet(body)
                    if (response.success) {
                        result = TranslationResult(
                            sourceText = response.hmongText ?: "",
                            targetText = response.vietnameseText ?: ""
                        )
                        uiState = UiState.SUCCESS
                    } else {
                        errorMessage = response.message ?: "Translation failed"
                        uiState = UiState.ERROR
                    }
                } else {
                    val response = apiService.translateVietToHmong(body)
                    if (response.success) {
                        var audioFile: File? = null
                        if (!response.audioBase64.isNullOrEmpty()) {
                            audioFile = saveBase64Audio(response.audioBase64)
                        }

                        result = TranslationResult(
                            sourceText = response.vietnameseText ?: "",
                            targetText = response.hmongText ?: "",
                            audioFile = audioFile
                        )
                        uiState = UiState.SUCCESS
                    } else {
                        errorMessage = response.message ?: "Translation failed"
                        uiState = UiState.ERROR
                    }
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Error uploading", e)
                errorMessage = "Translation failed: ${e.message}"
                uiState = UiState.ERROR
            }
        }
    }

    /**
     * Lưu audio Base64 nhận từ server thành file trong cache.
     *
     * @param base64String Chuỗi audio mã hóa Base64
     * @return File audio hoặc null nếu lỗi
     */
    private fun saveBase64Audio(base64String: String): File? {
        return try {
            val audioBytes = Base64.decode(base64String, Base64.DEFAULT)
            val audioFile = File(
                getApplication<Application>().cacheDir,
                "response_audio_${System.currentTimeMillis()}.mp3"
            )
            FileOutputStream(audioFile).use { it.write(audioBytes) }
            audioFile
        } catch (e: Exception) {
            Log.e("TranslateViewModel", "Error saving audio", e)
            null
        }
    }

    /**
     * Phát audio kết quả dịch (nếu có).
     */
    fun playAudioResult() {
        val file = result.audioFile ?: return
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("TranslateViewModel", "Error playing audio", e)
            errorMessage = "Could not play audio"
        }
    }

    /**
     * Được gọi khi ViewModel bị huỷ.
     * Giải phóng MediaPlayer và dừng ghi âm để tránh leak.
     */
    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        audioRecorder.stopRecording()
    }
}
