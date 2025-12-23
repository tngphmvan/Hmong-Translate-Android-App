package com.example.hmong_translate.ui

import com.example.hmong_translate.data.ApiService
import com.example.hmong_translate.data.HmongToVietResponse
import com.example.hmong_translate.data.VietToHmongResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.AdvanceTimeController
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.robolectric.RuntimeEnvironment
import java.io.File
import java.io.FileOutputStream
import android.util.Base64

private class FakeApiService(
    var hmongToViet: HmongToVietResponse? = null,
    var vietToHmong: VietToHmongResponse? = null
) : ApiService {
    override suspend fun translateHmongToViet(audio: MultipartBody.Part): HmongToVietResponse {
        return hmongToViet ?: HmongToVietResponse(
            hmongText = "",
            vietnameseText = "",
            success = false,
            message = "No response"
        )
    }

    override suspend fun translateVietToHmong(audio: MultipartBody.Part): VietToHmongResponse {
        return vietToHmong ?: VietToHmongResponse(
            vietnameseText = "",
            hmongText = "",
            audioBase64 = null,
            audioFormat = null,
            sampleRate = null,
            success = false,
            message = "No response"
        )
    }
}

private fun createFakeWavFile(dir: File): File {
    val file = File(dir, "test.wav")
    // Minimal WAV header (PCM, mono, 44100Hz, 16-bit) + small silent payload
    val header = ByteArray(44)
    // RIFF
    header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
    val dataSize = 1024
    val totalDataLen = dataSize + 36
    header[4] = (totalDataLen and 0xff).toByte()
    header[5] = (totalDataLen shr 8 and 0xff).toByte()
    header[6] = (totalDataLen shr 16 and 0xff).toByte()
    header[7] = (totalDataLen shr 24 and 0xff).toByte()
    header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
    // fmt chunk
    header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
    header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
    header[20] = 1; header[21] = 0 // PCM
    header[22] = 1; header[23] = 0 // mono
    val sampleRate = 44100
    header[24] = (sampleRate and 0xff).toByte()
    header[25] = (sampleRate shr 8 and 0xff).toByte()
    header[26] = (sampleRate shr 16 and 0xff).toByte()
    header[27] = (sampleRate shr 24 and 0xff).toByte()
    val byteRate = sampleRate * 2 // 16-bit mono -> 2 bytes per sample
    header[28] = (byteRate and 0xff).toByte()
    header[29] = (byteRate shr 8 and 0xff).toByte()
    header[30] = (byteRate shr 16 and 0xff).toByte()
    header[31] = (byteRate shr 24 and 0xff).toByte()
    header[32] = 2 // block align
    header[34] = 16 // bits per sample
    // data chunk
    header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
    header[40] = (dataSize and 0xff).toByte()
    header[41] = (dataSize shr 8 and 0xff).toByte()
    header[42] = (dataSize shr 16 and 0xff).toByte()
    header[43] = (dataSize shr 24 and 0xff).toByte()

    val payload = ByteArray(dataSize) { 0 }
    FileOutputStream(file).use { fos ->
        fos.write(header)
        fos.write(payload)
    }
    return file
}

@OptIn(ExperimentalCoroutinesApi::class)
class TranslateViewModelTest {

    @Test
    fun hmongToViet_success_updatesUiStateAndResult() = runTest(StandardTestDispatcher()) {
        val app = RuntimeEnvironment.getApplication()
        val fakeApi = FakeApiService(
            hmongToViet = HmongToVietResponse(
                hmongText = "koj nyob li cas",
                vietnameseText = "bạn khỏe không",
                success = true,
                message = null
            )
        )
        val vm = TranslateViewModel(app, apiService = fakeApi)
        vm.toggleDirection() // switch to VIET_TO_HMONG then toggle back? ensure HMONG_TO_VIET
        vm.toggleDirection() // back to HMONG_TO_VIET

        val wav = createFakeWavFile(app.cacheDir)
        vm.submitRecordedFile(wav)

        // Let coroutine finish
        kotlinx.coroutines.test.advanceUntilIdle()

        assertEquals(UiState.SUCCESS, vm.uiState)
        assertEquals("koj nyob li cas", vm.result.sourceText)
        assertEquals("bạn khỏe không", vm.result.targetText)
    }

    @Test
    fun vietToHmong_success_savesAudioFile() = runTest(StandardTestDispatcher()) {
        val app = RuntimeEnvironment.getApplication()
        val audioBytes = byteArrayOf(0x49, 0x44, 0x33) // minimal bytes (mp3 header 'ID3')
        val b64 = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
        val fakeApi = FakeApiService(
            vietToHmong = VietToHmongResponse(
                vietnameseText = "xin chào",
                hmongText = "nyob zoo",
                audioBase64 = b64,
                audioFormat = "mp3",
                sampleRate = 22050,
                success = true,
                message = null
            )
        )
        val vm = TranslateViewModel(app, apiService = fakeApi)
        vm.toggleDirection() // HMONG_TO_VIET -> VIET_TO_HMONG

        val wav = createFakeWavFile(app.cacheDir)
        vm.submitRecordedFile(wav)
        kotlinx.coroutines.test.advanceUntilIdle()

        assertEquals(UiState.SUCCESS, vm.uiState)
        assertEquals("xin chào", vm.result.sourceText)
        assertEquals("nyob zoo", vm.result.targetText)
        assertNotNull(vm.result.audioFile)
        assert(vm.result.audioFile!!.exists())
    }

    @Test
    fun network_error_setsErrorState() = runTest(StandardTestDispatcher()) {
        val app = RuntimeEnvironment.getApplication()
        val fakeApi = FakeApiService(
            hmongToViet = HmongToVietResponse(
                hmongText = null,
                vietnameseText = null,
                success = false,
                message = "Translation failed"
            )
        )
        val vm = TranslateViewModel(app, apiService = fakeApi)
        val wav = createFakeWavFile(app.cacheDir)
        vm.submitRecordedFile(wav)
        kotlinx.coroutines.test.advanceUntilIdle()
        assertEquals(UiState.ERROR, vm.uiState)
        assertEquals("Translation failed", vm.errorMessage)
    }
}
