package com.example.hmong_translate.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private var outputFile: File? = null

    // Cấu hình cho WAV (16-bit PCM, Mono, 44.1kHz)
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    @SuppressLint("MissingPermission")
    fun startRecording(): File? {
        outputFile = File(context.cacheDir, "recording.wav")
        val rawFile = File(context.cacheDir, "temp_raw.pcm")

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (recorder?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioRecorder", "AudioRecord initialization failed")
            return null
        }

        recorder?.startRecording()
        isRecording = true

        recordingThread = Thread {
            writeAudioDataToFile(rawFile)
        }
        recordingThread?.start()

        return outputFile
    }

    fun stopRecording(): File? {
        if (!isRecording) return null

        isRecording = false
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            recordingThread?.join() // Chờ luồng ghi file thô kết thúc

            // Chuyển đổi file PCM thô sang WAV (thêm header)
            val rawFile = File(context.cacheDir, "temp_raw.pcm")
            outputFile?.let { wavFile ->
                rawToWave(rawFile, wavFile)
            }
            rawFile.delete() // Xóa file tạm

            return outputFile

        } catch (e: Exception) {
            Log.e("AudioRecorder", "stop() failed", e)
            return null
        }
    }

    private fun writeAudioDataToFile(file: File) {
        val data = ByteArray(bufferSize)
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(file)
            while (isRecording) {
                val read = recorder?.read(data, 0, bufferSize) ?: 0
                if (read > 0) {
                    os.write(data, 0, read)
                }
            }
        } catch (e: IOException) {
            Log.e("AudioRecorder", "Error writing audio data", e)
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = ByteArray(rawFile.length().toInt())
        var input: FileInputStream? = null
        var output: FileOutputStream? = null

        try {
            input = FileInputStream(rawFile)
            input.read(rawData)

            output = FileOutputStream(waveFile)
            // Ghi header WAV
            writeWavHeader(output, rawData.size.toLong(), sampleRate.toLong(), 1, (16 * 1 / 8).toLong())
            // Ghi dữ liệu âm thanh
            output.write(rawData)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            input?.close()
            output?.close()
        }
    }

    private fun writeWavHeader(
        output: FileOutputStream,
        totalAudioLen: Long,
        longSampleRate: Long,
        channels: Int,
        byteRate: Long
    ) {
        val totalDataLen = totalAudioLen + 36
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1 (PCM)
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (longSampleRate * byteRate * channels.toLong() and 0xff).toByte() // Byte rate
        header[29] = (longSampleRate * byteRate * channels.toLong() shr 8 and 0xff).toByte()
        header[30] = (longSampleRate * byteRate * channels.toLong() shr 16 and 0xff).toByte()
        header[31] = (longSampleRate * byteRate * channels.toLong() shr 24 and 0xff).toByte()
        header[32] = (channels * 16 / 8).toByte() // Block align
        header[33] = 0
        header[34] = 16 // Bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()

        output.write(header, 0, 44)
    }
}
