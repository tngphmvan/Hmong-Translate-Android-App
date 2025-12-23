package com.example.hmong_translate.utils

import com.example.hmong_translate.ui.TranslateViewModel
import com.example.hmong_translate.ui.TranslationDirection
import java.io.File

/**
 * Non-intrusive helper functions to orchestrate translation using existing ViewModel API
 * without modifying old code. These functions set the direction if needed and submit
 * a recorded audio file for processing.
 */
object TranslationHelper {
    /**
     * Submit a recorded audio file to translate from H'Mông to Vietnamese.
     * Does not modify ViewModel internals; uses public APIs only.
     */
    fun translateHmongToViet(viewModel: TranslateViewModel, audioFile: File) {
        if (viewModel.direction != TranslationDirection.HMONG_TO_VIET) {
            viewModel.toggleDirection()
        }
        viewModel.submitRecordedFile(audioFile)
    }

    /**
     * Submit a recorded audio file to translate from Vietnamese to H'Mông.
     * Does not modify ViewModel internals; uses public APIs only.
     */
    fun translateVietToHmong(viewModel: TranslateViewModel, audioFile: File) {
        if (viewModel.direction != TranslationDirection.VIET_TO_HMONG) {
            viewModel.toggleDirection()
        }
        viewModel.submitRecordedFile(audioFile)
    }
}
