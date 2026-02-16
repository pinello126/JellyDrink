package com.jellydrink.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jellydrink.app.data.db.entity.DecorationEntity
import com.jellydrink.app.data.db.entity.JellyfishEntity
import com.jellydrink.app.viewmodel.ShopViewModel
import kotlinx.coroutines.delay

// Decoration icons/emojis for display
private val DecorationIcons = mapOf(
    "fish_blue" to "🐟",
    "fish_orange" to "🐠",
    "starfish" to "⭐",
    "coral_pink" to "🪸",
    "treasure" to "📦",
    "turtle" to "🐢",
    "seahorse" to "🐴",
    "crab" to "🦀"
)

private val DecorationColors = mapOf(
    "fish_blue" to Color(0xFF2196F3),
    "fish_orange" to Color(0xFFFF5722),
    "starfish" to Color(0xFFFF9800),
    "coral_pink" to Color(0xFFE91E63),
    "treasure" to Color(0xFFFFD700),
    "turtle" to Color(0xFF4CAF50),
    "seahorse" to Color(0xFFFFEB3B),
    "crab" to Color(0xFFF44336)
)

private val JellyfishColors = mapOf(
    "rosa" to Color(0xFFFFACD0),
    "lunar" to Color(0xFFACCCFF),
    "abyssal" to Color(0xFF9868D8),
    "aurora" to Color(0xFF80FFD0),
    "crystal" to Color(0xFFE8E8F8),
    "golden" to Color(0xFFFFD060)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchaseResult by viewModel.purchaseResult.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Auto-dismiss purchase result
    LaunchedEffect(purchaseResult) {
        if (purchaseResult != null) {
            delay(2000)
            viewModel.dismissPurchaseResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Negozio")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    // XP display
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF9800))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${uiState.currentXp} XP",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Decorazioni") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Meduse") }
                    )
                }

                // Tab Content
                when (selectedTab) {
                    0 -> DecorationsTab(
                        decorations = uiState.decorations,
                        currentXp = uiState.currentXp,
                        onPurchase = { viewModel.purchaseDecoration(it) },
                        onTogglePlaced = { viewModel.toggleDecorationPlaced(it) }
                    )
                    1 -> JellyfishTab(
                        jellyfish = uiState.jellyfish,
                        currentXp = uiState.currentXp,
                        onPurchase = { viewModel.purchaseJellyfish(it) },
                        onSelect = { viewModel.selectJellyfish(it) }
                    )
                }
            }

            // Purchase result feedback
            AnimatedVisibility(
                visible = purchaseResult != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (purchaseResult) {
                            is ShopViewModel.PurchaseResult.Success -> Color(0xFF4CAF50)
                            is ShopViewModel.PurchaseResult.InsufficientXp -> Color(0xFFF44336)
                            null -> Color.Transparent
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (purchaseResult) {
                                is ShopViewModel.PurchaseResult.Success -> Icons.Default.CheckCircle
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = when (purchaseResult) {
                                is ShopViewModel.PurchaseResult.Success -> "Acquisto completato!"
                                is ShopViewModel.PurchaseResult.InsufficientXp -> "XP insufficienti!"
                                null -> ""
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DecorationsTab(
    decorations: List<DecorationEntity>,
    currentXp: Int,
    onPurchase: (String) -> Unit,
    onTogglePlaced: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Decorazioni Acquario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Compra decorazioni per abbellire il tuo acquario!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(decorations) { decoration ->
            DecorationCard(
                decoration = decoration,
                currentXp = currentXp,
                onPurchase = { onPurchase(decoration.id) },
                onTogglePlaced = { onTogglePlaced(decoration.id) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun JellyfishTab(
    jellyfish: List<JellyfishEntity>,
    currentXp: Int,
    onPurchase: (String) -> Unit,
    onSelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Collezione Meduse",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sblocca nuove meduse per personalizzare il tuo acquario!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(jellyfish) { jf ->
            JellyfishCard(
                jellyfish = jf,
                currentXp = currentXp,
                onPurchase = { onPurchase(jf.id) },
                onSelect = { onSelect(jf.id) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun DecorationCard(
    decoration: DecorationEntity,
    currentXp: Int,
    onPurchase: () -> Unit,
    onTogglePlaced: () -> Unit
) {
    val canAfford = currentXp >= decoration.cost && !decoration.owned
    val color = DecorationColors[decoration.id] ?: Color.Gray
    val icon = DecorationIcons[decoration.id] ?: "🎁"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (decoration.owned) {
                color.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = color.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (decoration.id == "fish_orange") {
                    Canvas(modifier = Modifier.size(36.dp)) {
                        drawClownfish(this)
                    }
                } else {
                    Text(
                        text = icon,
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = decoration.nameIt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!decoration.owned) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${decoration.cost} XP",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Acquistato",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            // Action button
            if (decoration.owned) {
                // Toggle placed switch
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(
                        checked = decoration.placed,
                        onCheckedChange = { onTogglePlaced() }
                    )
                    Text(
                        text = if (decoration.placed) "Visibile" else "Nascosto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Compra",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun JellyfishCard(
    jellyfish: JellyfishEntity,
    currentXp: Int,
    onPurchase: () -> Unit,
    onSelect: () -> Unit
) {
    val canAfford = currentXp >= jellyfish.cost && !jellyfish.unlocked
    val color = JellyfishColors[jellyfish.id] ?: Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (jellyfish.unlocked) {
                color.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (jellyfish.unlocked) color.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (jellyfish.unlocked && jellyfish.selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selezionata",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                } else if (!jellyfish.unlocked) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Bloccata",
                        tint = Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jellyfish.nameIt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (jellyfish.unlocked) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )

                if (!jellyfish.unlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = jellyfish.unlockCondition,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (jellyfish.cost > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${jellyfish.cost} XP",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (jellyfish.selected) "Selezionata" else "Sbloccata",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            // Action button
            if (jellyfish.unlocked && !jellyfish.selected) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Seleziona",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (!jellyfish.unlocked && jellyfish.cost > 0) {
                Button(
                    onClick = onPurchase,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Compra",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun drawClownfish(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val cx = w * 0.45f
        val cy = h * 0.5f
        val bodyW = w * 0.40f
        val bodyH = h * 0.28f

        val clownOrange = Color(0xFFFF6F00)
        val stripeWhite = Color(0xFFF5F5F5)
        val outlineBlack = Color(0xFF1A1A1A)

        // --- Tail fin ---
        val tailPath = Path().apply {
            moveTo(cx + bodyW * 0.75f, cy)
            lineTo(cx + bodyW * 1.35f, cy - bodyH * 0.9f)
            quadraticBezierTo(cx + bodyW * 1.1f, cy, cx + bodyW * 1.35f, cy + bodyH * 0.9f)
            close()
        }
        drawPath(tailPath, color = clownOrange)
        drawPath(tailPath, color = outlineBlack, style = Stroke(width = 1.8f))

        // --- Body (ellipse) ---
        drawOval(
            color = clownOrange,
            topLeft = Offset(cx - bodyW, cy - bodyH),
            size = Size(bodyW * 2f, bodyH * 2f)
        )

        // --- White stripes with black edges (3 stripes) ---
        val stripeWidth = bodyW * 0.12f

        // Stripe 1 - near head
        val s1x = cx - bodyW * 0.55f
        val s1Path = Path().apply {
            moveTo(s1x, cy - bodyH * 0.85f)
            quadraticBezierTo(s1x - stripeWidth * 0.4f, cy, s1x, cy + bodyH * 0.85f)
            lineTo(s1x + stripeWidth, cy + bodyH * 0.82f)
            quadraticBezierTo(s1x + stripeWidth * 0.6f, cy, s1x + stripeWidth, cy - bodyH * 0.82f)
            close()
        }
        drawPath(s1Path, color = stripeWhite)
        drawPath(s1Path, color = outlineBlack, style = Stroke(width = 1.4f))

        // Stripe 2 - center
        val s2x = cx - stripeWidth * 0.5f
        val s2Path = Path().apply {
            moveTo(s2x, cy - bodyH * 0.97f)
            quadraticBezierTo(s2x - stripeWidth * 0.3f, cy, s2x, cy + bodyH * 0.97f)
            lineTo(s2x + stripeWidth, cy + bodyH * 0.95f)
            quadraticBezierTo(s2x + stripeWidth * 0.7f, cy, s2x + stripeWidth, cy - bodyH * 0.95f)
            close()
        }
        drawPath(s2Path, color = stripeWhite)
        drawPath(s2Path, color = outlineBlack, style = Stroke(width = 1.4f))

        // Stripe 3 - near tail
        val s3x = cx + bodyW * 0.42f
        val s3Path = Path().apply {
            moveTo(s3x, cy - bodyH * 0.72f)
            quadraticBezierTo(s3x - stripeWidth * 0.3f, cy, s3x, cy + bodyH * 0.72f)
            lineTo(s3x + stripeWidth, cy + bodyH * 0.60f)
            quadraticBezierTo(s3x + stripeWidth * 0.7f, cy, s3x + stripeWidth, cy - bodyH * 0.60f)
            close()
        }
        drawPath(s3Path, color = stripeWhite)
        drawPath(s3Path, color = outlineBlack, style = Stroke(width = 1.4f))

        // --- Dorsal fin (top) ---
        val dorsalPath = Path().apply {
            moveTo(cx - bodyW * 0.2f, cy - bodyH * 0.9f)
            quadraticBezierTo(cx, cy - bodyH * 1.7f, cx + bodyW * 0.3f, cy - bodyH * 0.85f)
        }
        drawPath(dorsalPath, color = clownOrange, style = Stroke(width = 4f, cap = StrokeCap.Round))
        drawPath(dorsalPath, color = outlineBlack, style = Stroke(width = 1.4f, cap = StrokeCap.Round))

        // --- Pectoral fin (bottom) ---
        val pectoralPath = Path().apply {
            moveTo(cx - bodyW * 0.1f, cy + bodyH * 0.85f)
            quadraticBezierTo(cx + bodyW * 0.1f, cy + bodyH * 1.55f, cx + bodyW * 0.35f, cy + bodyH * 0.80f)
        }
        drawPath(pectoralPath, color = clownOrange, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        drawPath(pectoralPath, color = outlineBlack, style = Stroke(width = 1.2f, cap = StrokeCap.Round))

        // --- Body outline ---
        drawOval(
            color = outlineBlack,
            topLeft = Offset(cx - bodyW, cy - bodyH),
            size = Size(bodyW * 2f, bodyH * 2f),
            style = Stroke(width = 2f)
        )

        // --- Eye ---
        val eyeX = cx - bodyW * 0.55f
        val eyeY = cy - bodyH * 0.15f
        val eyeR = bodyH * 0.28f
        drawCircle(color = Color.White, radius = eyeR, center = Offset(eyeX, eyeY))
        drawCircle(color = outlineBlack, radius = eyeR * 0.55f, center = Offset(eyeX + eyeR * 0.1f, eyeY))
        drawCircle(color = Color.White, radius = eyeR * 0.2f, center = Offset(eyeX + eyeR * 0.25f, eyeY - eyeR * 0.2f))
        drawCircle(color = outlineBlack, radius = eyeR, center = Offset(eyeX, eyeY), style = Stroke(width = 1.2f))

        // --- Mouth ---
        val mouthPath = Path().apply {
            moveTo(cx - bodyW * 0.85f, cy + bodyH * 0.15f)
            quadraticBezierTo(cx - bodyW * 0.75f, cy + bodyH * 0.35f, cx - bodyW * 0.65f, cy + bodyH * 0.2f)
        }
        drawPath(mouthPath, color = outlineBlack, style = Stroke(width = 1.4f, cap = StrokeCap.Round))
    }
}
