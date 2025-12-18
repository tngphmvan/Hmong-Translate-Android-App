# Ứng dụng Hmong Translate (Dịch tiếng Mông - Việt)

Ứng dụng Android hỗ trợ dịch thuật hai chiều giữa tiếng Mông và tiếng Việt thông qua giọng nói, lấy cảm hứng từ giao diện Papago.

## 1. Yêu cầu hệ thống

*   **Android Studio**: Phiên bản mới nhất (hỗ trợ Kotlin và Jetpack Compose).
*   **JDK**: Phiên bản 11 hoặc 17 (được bundle sẵn trong Android Studio).
*   **Python**: Môi trường để chạy Backend Server.
*   **Thiết bị chạy**: Máy ảo Android (Emulator) hoặc thiết bị thật (Android 7.0 trở lên).

## 2. Cấu trúc dự án

Dự án gồm 2 phần chính:
1.  **Android Client**: Viết bằng Kotlin, sử dụng Jetpack Compose.
2.  **Backend Server** (Python): Xử lý AI (ASR, Google Translate, TTS).

## 3. Hướng dẫn cài đặt & Chạy

### Bước 1: Thiết lập Backend (Server Python)

Bạn cần đảm bảo server Python đang chạy trước khi bật ứng dụng Android.

1.  Chuẩn bị các file Python (`app.py`, `hmongtts.py`) như mô tả của bạn.
2.  Đảm bảo `app.py` lắng nghe ở cổng **5000** và định nghĩa các API endpoint như sau:
    *   `POST /translate/hmong-to-viet`: Nhận file âm thanh (key: `audio`), trả về JSON `{ "text": "Nội dung tiếng Việt" }`.
    *   `POST /translate/viet-to-hmong`: Nhận file âm thanh (key: `audio`), trả về file âm thanh (binary/blob) tiếng Mông.
3.  Chạy server:
    ```bash
    python app.py
    ```

### Bước 2: Cấu hình kết nối mạng cho Android

Mặc định ứng dụng đang trỏ tới `http://10.0.2.2:5000/`.

*   **Nếu dùng Android Emulator**: Địa chỉ `10.0.2.2` là địa chỉ localhost của máy tính host. Bạn **không cần sửa** gì cả nếu server chạy ở port 5000 trên máy tính.
*   **Nếu dùng thiết bị thật**:
    1.  Đảm bảo điện thoại và máy tính cùng kết nối một mạng Wi-Fi.
    2.  Tìm địa chỉ IP LAN của máy tính (ví dụ: `192.168.1.15`).
    3.  Mở file `app/src/main/java/com/example/hmong_translate/ui/TranslateViewModel.kt`.
    4.  Sửa dòng `baseUrl`:
        ```kotlin
        // Thay đổi dòng này
        .baseUrl("http://192.168.1.15:5000/") 
        ```

### Bước 3: Chạy ứng dụng Android

1.  Mở **Android Studio**.
2.  Chọn **Open** và trỏ đến thư mục `Hmong_Translate`.
3.  Đợi Gradle sync hoàn tất.
4.  Nhấn nút **Run** (biểu tượng tam giác xanh) trên thanh công cụ.
5.  Chọn thiết bị (Emulator hoặc máy thật) để cài đặt.

## 4. Hướng dẫn sử dụng

1.  **Cấp quyền**: Lần đầu mở app, hãy chọn "Allow" (Cho phép) khi được hỏi quyền ghi âm.
2.  **Chọn chiều dịch**: Nhấn vào biểu tượng mũi tên 2 chiều ở thanh trên cùng để đổi giữa "Mông -> Việt" và "Việt -> Mông".
3.  **Dịch thuật**:
    *   Chạm vào nút **Micro** màu đỏ để bắt đầu nói.
    *   Nói câu cần dịch.
    *   Chạm lại nút đó (hoặc đợi, tùy thao tác bạn muốn tùy chỉnh) để dừng và gửi đi dịch.
4.  **Kết quả**:
    *   Nếu dịch sang tiếng Việt: Văn bản sẽ hiện trên màn hình.
    *   Nếu dịch sang tiếng Mông: Ứng dụng sẽ tự động phát âm thanh kết quả.

## 5. Khắc phục lỗi thường gặp

*   **Lỗi "Translation failed: ... Failed to connect to /10.0.2.2:5000"**:
    *   Kiểm tra xem file `app.py` đã chạy chưa.
    *   Nếu dùng máy thật, hãy kiểm tra lại IP như hướng dẫn ở Bước 2.
    *   Tắt tường lửa (Firewall) trên máy tính nếu cần thiết.
*   **Lỗi không ghi âm được**: Vào Cài đặt điện thoại -> Ứng dụng -> Hmong Translate -> Quyền -> Bật quyền Micro.
