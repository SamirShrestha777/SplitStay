package com.softwarica.splitstay.view.register

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.softwarica.splitstay.R

// ── ImageVector icons ──────────────────────────────────────────────────────────

/**
 * Back arrow icon (←) built purely in Kotlin.
 */
val BackArrowIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "BackArrow",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF17372F)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 12f)
            lineTo(5f, 12f)
        }
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF17372F)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12f, 5f)
            lineTo(5f, 12f)
            lineTo(12f, 19f)
        }
    }.build()
}

/**
 * Eye (visibility on) icon built purely in Kotlin.
 */
val EyeIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Eye",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF9AA5A0)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Outer eye shape
            moveTo(1f, 12f)
            curveTo(1f, 12f, 5f, 5f, 12f, 5f)
            curveTo(19f, 5f, 23f, 12f, 23f, 12f)
            curveTo(23f, 12f, 19f, 19f, 12f, 19f)
            curveTo(5f, 19f, 1f, 12f, 1f, 12f)
            close()
        }
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF9AA5A0)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Iris circle (two half-circle arcs; PathBuilder.arcTo cannot do 360°)
            val cx = 12f
            val cy = 12f
            val r = 3f
            moveTo(cx + r, cy)
            arcTo(r, r, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = cx - r, y1 = cy)
            arcTo(r, r, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = cx + r, y1 = cy)
            close()
        }
    }.build()
}

/**
 * Eye-off (visibility off) icon built purely in Kotlin.
 */
val EyeOffIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "EyeOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF9AA5A0)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Partial eye shape (top half visible)
            moveTo(17.94f, 17.94f)
            curveTo(16.23f, 19.24f, 14.15f, 20f, 12f, 20f)
            curveTo(5f, 20f, 1f, 12f, 1f, 12f)
            curveTo(3.09f, 8.18f, 5.84f, 5.86f, 9.06f, 4.56f)
        }
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF9AA5A0)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9.9f, 4.24f)
            curveTo(10.58f, 4.08f, 11.29f, 4f, 12f, 4f)
            curveTo(19f, 4f, 23f, 12f, 23f, 12f)
            curveTo(22.39f, 13.13f, 21.69f, 14.18f, 20.88f, 15.12f)
        }
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF9AA5A0)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14.12f, 14.12f)
            curveTo(13.56f, 14.64f, 12.82f, 14.95f, 12f, 14.95f)
            curveTo(10.37f, 14.95f, 9.05f, 13.63f, 9.05f, 12f)
            curveTo(9.05f, 11.18f, 9.36f, 10.44f, 9.88f, 9.88f)
        }
        // Slash line
        path(
            stroke = androidx.compose.ui.graphics.SolidColor(Color(0xFF9AA5A0)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(1f, 1f)
            lineTo(23f, 23f)
        }
    }.build()
}

// ── Canvas composable icons ────────────────────────────────────────────────────

/**
 * SplitStay app logo: house with a leaf/coin motif, drawn with Compose Canvas.
 * Matches the rounded-square green house icon in the design.
 */
@Composable
fun AppLogo(size: Dp = 40.dp) {
    Canvas(modifier = Modifier.size(size)) {
        drawAppLogo()
    }
}

private fun DrawScope.drawAppLogo() {
    val w = size.width
    val h = size.height
    val green = Color(0xFF0F5F57)
    val cream = Color(0xFFFBF6EA)
    val leafGreen = Color(0xFF4CAF50)

    // Rounded square background
    val bgPath = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, 0f, w, h),
                cornerRadius = CornerRadius(w * 0.22f, h * 0.22f)
            )
        )
    }
    drawPath(bgPath, green)

    // House body (rectangle)
    val houseLeft = w * 0.25f
    val houseRight = w * 0.75f
    val houseTop = h * 0.42f
    val houseBottom = h * 0.78f
    drawRect(
        color = cream,
        topLeft = Offset(houseLeft, houseTop),
        size = Size(houseRight - houseLeft, houseBottom - houseTop)
    )

    // Roof (triangle)
    val roofPath = Path().apply {
        moveTo(w * 0.5f, h * 0.2f)
        lineTo(w * 0.18f, h * 0.45f)
        lineTo(w * 0.82f, h * 0.45f)
        close()
    }
    drawPath(roofPath, cream)

    // Door (small rectangle)
    val doorLeft = w * 0.42f
    val doorRight = w * 0.58f
    val doorTop = h * 0.58f
    val doorBottom = h * 0.78f
    drawRect(
        color = green,
        topLeft = Offset(doorLeft, doorTop),
        size = Size(doorRight - doorLeft, doorBottom - doorTop)
    )

    // Leaf / coin accent (small circle at top-right of house)
    drawCircle(
        color = leafGreen,
        radius = w * 0.09f,
        center = Offset(w * 0.68f, h * 0.3f)
    )

    // Leaf stem
    val stemPath = Path().apply {
        moveTo(w * 0.68f, h * 0.3f)
        quadraticTo(w * 0.75f, h * 0.22f, w * 0.72f, h * 0.18f)
    }
    drawPath(
        stemPath,
        leafGreen,
        style = Stroke(width = w * 0.025f, cap = StrokeCap.Round)
    )
}

/**
 * Google icon displayed from drawable resource (search.png).
 */
@Composable
fun GoogleIcon(size: Dp = 20.dp) {
    Image(
        painter = painterResource(R.drawable.search),
        contentDescription = "Google",
        modifier = Modifier.size(size)
    )
}
