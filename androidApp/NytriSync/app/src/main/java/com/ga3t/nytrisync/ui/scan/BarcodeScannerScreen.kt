@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.camera.core.ExperimentalGetImage::class
)

package com.ga3t.nytrisync.ui.scan

import android.Manifest
import android.content.Context
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.min

@Composable
fun BarcodeScannerScreen(
    onDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) { permLauncher.launch(Manifest.permission.CAMERA) }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
            runCatching { cameraExecutor.shutdown() }
        }
    }


    var manualSheet by remember { mutableStateOf(false) }
    var manualCode by remember { mutableStateOf("") }
    var manualError by remember { mutableStateOf<String?>(null) }


    val frameWidthFraction = 0.8f
    val frameHeightDp: Dp = 200.dp
    val density = LocalDensity.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan barcode") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Align the barcode within the frame",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = {
                            manualError = null
                            manualCode = ""
                            manualSheet = true
                        },
                        shape = MaterialTheme.shapes.large
                    ) { Text("Enter code manually") }
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (hasPermission) {
                null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                false -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Camera permission denied")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { permLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Grant permission")
                        }
                    }
                }
                true -> {
                    var scanned by remember { mutableStateOf(false) }

                    // Камера (подложка)
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx: Context ->
                            val previewView = PreviewView(ctx).apply {
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analysis = ImageAnalysis.Builder()
                                .setTargetResolution(Size(1280, 720))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            val opts = BarcodeScannerOptions.Builder()
                                .setBarcodeFormats(
                                    Barcode.FORMAT_EAN_13,
                                    Barcode.FORMAT_EAN_8,
                                    Barcode.FORMAT_UPC_A,
                                    Barcode.FORMAT_UPC_E
                                )
                                .build()
                            val scanner = BarcodeScanning.getClient(opts)

                            val analyzer = object : Analyzer {
                                @androidx.camera.core.ExperimentalGetImage
                                override fun analyze(imageProxy: ImageProxy) {
                                    if (scanned || manualSheet) {
                                        imageProxy.close()
                                        return
                                    }
                                    val mediaImage = imageProxy.image ?: run {
                                        imageProxy.close(); return
                                    }
                                    val rotation = imageProxy.imageInfo.rotationDegrees
                                    val image = InputImage.fromMediaImage(mediaImage, rotation)

                                    scanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            val code = barcodes.firstOrNull()?.rawValue
                                            if (!code.isNullOrBlank()) {
                                                scanned = true
                                                onDetected(code)
                                            }
                                        }
                                        .addOnFailureListener { /* ignore */ }
                                        .addOnCompleteListener { imageProxy.close() }
                                }
                            }

                            analysis.setAnalyzer(cameraExecutor, analyzer)

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    analysis
                                )
                            } catch (_: Exception) {}

                            previewView
                        }
                    )

                    // Маска + рамка (без BoxWithConstraints)
                    val frameHeightPx = with(density) { frameHeightDp.toPx() }
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            // Нужен offscreen, чтобы BlendMode.Clear “вырезал” дырку
                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    ) {
                        val w = size.width
                        val h = size.height
                        val rectW = w * frameWidthFraction
                        val rectH = min(h, frameHeightPx)
                        val left = (w - rectW) / 2f
                        val top = (h - rectH) / 2f

                        // Затемняем весь экран
                        drawRect(Color.Black.copy(alpha = 0.65f))

                        // Вырезаем “окно” (дырку) под рамку
                        drawRect(
                            color = Color.Transparent,
                            topLeft = androidx.compose.ui.geometry.Offset(left, top),
                            size = androidx.compose.ui.geometry.Size(rectW, rectH),
                            blendMode = BlendMode.Clear
                        )

                        // Белая рамка
                        drawRect(
                            color = Color.White,
                            topLeft = androidx.compose.ui.geometry.Offset(left, top),
                            size = androidx.compose.ui.geometry.Size(rectW, rectH),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            }
        }
    }

    // Bottom sheet: ручной ввод кода
    if (manualSheet) {
        ModalBottomSheet(
            onDismissRequest = { manualSheet = false }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Enter barcode", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = manualCode,
                    onValueChange = {
                        manualError = null
                        manualCode = it.filter { ch -> ch.isDigit() }
                    },
                    label = { Text("Code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = manualError != null,
                    supportingText = {
                        if (manualError != null) {
                            Text(manualError!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("EAN-8 / EAN-13 / UPC-A / UPC-E")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { manualSheet = false },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Cancel") }

                    Button(
                        onClick = {
                            if (manualCode.isBlank()) {
                                manualError = "Enter barcode"
                                return@Button
                            }
                            manualSheet = false
                            onDetected(manualCode)
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Use code") }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}