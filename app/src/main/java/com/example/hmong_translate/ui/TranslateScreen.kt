package com.example.hmong_translate.ui
// import framework
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TranslateScreen(
    viewModel: TranslateViewModel = viewModel()
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )
    
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header / Language Switcher
        LanguageHeader(
            direction = viewModel.direction,
            onSwap = { viewModel.toggleDirection() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Result Area
        ResultArea(
            uiState = viewModel.uiState,
            result = viewModel.result,
            direction = viewModel.direction,
            errorMessage = viewModel.errorMessage,
            onPlayAudio = { viewModel.playAudioResult() }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Recording Button
        RecordButton(
            isRecording = viewModel.uiState == UiState.RECORDING,
            hasPermission = hasPermission,
            onStartRecord = { viewModel.startRecording() },
            onStopRecord = { viewModel.stopRecording() },
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LanguageHeader(
    direction: TranslationDirection,
    onSwap: () -> Unit
) {
    val (sourceLang, targetLang) = if (direction == TranslationDirection.HMONG_TO_VIET) {
        "H'Mông" to "Tiếng Việt"
    } else {
        "Tiếng Việt" to "H'Mông"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = sourceLang,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Swap languages",
            tint = Color.Gray,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSwap
                )
                .padding(8.dp)
        )
        
        Text(
            text = targetLang,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ResultArea(
    uiState: UiState,
    result: TranslationResult,
    direction: TranslationDirection,
    errorMessage: String,
    onPlayAudio: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Tăng chiều cao để hiển thị nhiều thông tin hơn
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = if (uiState == UiState.PROCESSING) Alignment.Center else Alignment.TopStart
    ) {
        when (uiState) {
            UiState.IDLE -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nhấn và giữ nút micro để nói",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
            UiState.RECORDING -> {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         Text(
                             text = "Đang nghe...",
                             color = MaterialTheme.colorScheme.primary,
                             fontWeight = FontWeight.Bold,
                             fontSize = 18.sp
                         )
                     }
                 }
            }
            UiState.PROCESSING -> {
                CircularProgressIndicator()
            }
            UiState.SUCCESS -> {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    // Source text section
                    Text(
                        text = if (direction == TranslationDirection.HMONG_TO_VIET) "H'Mông" else "Tiếng Việt",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.sourceText,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    

                    // Target text section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (direction == TranslationDirection.HMONG_TO_VIET) "Tiếng Việt" else "H'Mông",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.targetText,
                                fontSize = 22.sp, // Target text slightly larger
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        

                        // Show audio button if it's Viet -> Hmong and audio is available
                        
                        if (direction == TranslationDirection.VIET_TO_HMONG && result.audioFile != null) {
                            IconButton(onClick = onPlayAudio) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Play audio",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            UiState.ERROR -> {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Text(
                         text = errorMessage,
                         color = MaterialTheme.colorScheme.error
                     )
                 }
            }
        }
    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    hasPermission: Boolean,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val backgroundColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(4.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (!hasPermission) {
                        onRequestPermission()
                    }
                }
            )
            .clickable {
                if (hasPermission) {
                    if (isRecording) {
                        onStopRecord()
                    } else {
                        onStartRecord()
                    }
                } else {
                    onRequestPermission()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Microphone",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
    
    if (isRecording) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Chạm để dừng", color = Color.Gray)
    }
    // them chuc nang moi
}
