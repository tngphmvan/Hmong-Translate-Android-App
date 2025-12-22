package com.example.hmong_translate.ui
// import framework
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

enum class TranslationDirection {
    HMONG_TO_VIET,
    VIET_TO_HMONG
}

enum class UiState {
    IDLE,
    RECORDING,
    PROCESSING,
    SUCCESS,
    ERROR
}

data class TranslationResult(
    val sourceText: String = "",
    val targetText: String = "",
    val audioFile: File? = null
)

class TranslateViewModel(application: Application) : AndroidViewModel(application) {
    private val audioRecorder = AudioRecorder(application)
    private var mediaPlayer: MediaPlayer? = null
    
    // UI State
    var uiState by mutableStateOf(UiState.IDLE)
        private set
    
    var direction by mutableStateOf(TranslationDirection.HMONG_TO_VIET)
        private set
        
    var result by mutableStateOf(TranslationResult())
        private set
        
    var errorMessage by mutableStateOf("")
        private set

    // Setup Retrofit
    private val apiService: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Emulator localhost, change for physical device
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    fun toggleDirection() {
        direction = if (direction == TranslationDirection.HMONG_TO_VIET) {
            TranslationDirection.VIET_TO_HMONG
        } else {
            TranslationDirection.HMONG_TO_VIET
        }
        resetState()
    }
    
    private fun resetState() {
        uiState = UiState.IDLE
        result = TranslationResult()
        errorMessage = ""
    }

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

    private fun uploadAudio(file: File) {
        viewModelScope.launch {
            try {
                // Ensure MIME type is correct based on file extension or requirement
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

    private fun saveBase64Audio(base64String: String): File? {
        return try {
            val audioBytes = Base64.decode(base64String, Base64.DEFAULT)
            val audioFile = File(getApplication<Application>().cacheDir, "response_audio_${System.currentTimeMillis()}.mp3") // Assuming mp3 or use wav based on format
            FileOutputStream(audioFile).use { it.write(audioBytes) }
            audioFile
        } catch (e: Exception) {
            Log.e("TranslateViewModel", "Error saving audio", e)
            null
        }
    }

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
            // Don't change main UI state to ERROR just for playback failure, maybe show toast
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        audioRecorder.stopRecording()
    }
}
