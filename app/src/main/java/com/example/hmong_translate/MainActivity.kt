/**
 * Hmong Translate Android Application
 *
 * This package contains the main entry point and core components
 * for the Hmong translation application.
 *
 * @author Hmong Translate Team
 * @since 1.0.0
 */
package com.example.hmong_translate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hmong_translate.ui.TranslateScreen
import com.example.hmong_translate.ui.theme.Hmong_TranslateTheme

/**
 * MainActivity là Activity chính của ứng dụng Hmong Translate.
 *
 * Activity này đóng vai trò là điểm khởi đầu của ứng dụng, thiết lập giao diện người dùng
 * sử dụng Jetpack Compose và áp dụng theme của ứng dụng.
 *
 * ## Chức năng chính:
 * - Khởi tạo giao diện người dùng với Jetpack Compose
 * - Bật chế độ Edge-to-Edge để tối ưu hiển thị trên các thiết bị có notch hoặc navigation bar
 * - Áp dụng [Hmong_TranslateTheme] cho toàn bộ ứng dụng
 * - Hiển thị màn hình dịch thuật [TranslateScreen]
 *
 * @see ComponentActivity
 * @see TranslateScreen
 * @see Hmong_TranslateTheme
 */
class MainActivity : ComponentActivity() {
    /**
     * Được gọi khi Activity được tạo lần đầu tiên.
     *
     * Phương thức này thực hiện các bước khởi tạo sau:
     * 1. Gọi phương thức `onCreate` của lớp cha để thực hiện khởi tạo cơ bản
     * 2. Bật chế độ Edge-to-Edge để nội dung có thể hiển thị phía sau system bars
     * 3. Thiết lập nội dung Compose với theme và màn hình dịch thuật
     *
     * @param savedInstanceState Bundle chứa trạng thái đã lưu trước đó của Activity.
     *                           Có thể là null nếu Activity được tạo mới hoàn toàn.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hmong_TranslateTheme {
                TranslateScreen()
            }
        }
    }
}
