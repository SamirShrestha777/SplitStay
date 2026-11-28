package com.softwarica.splitstay.view.household

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// ── Back arrow icon (←) ────────────────────────────────────────────────────────

internal val BackArrowIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "HouseholdBackArrow",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF17372F)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(19f, 12f)
            lineTo(5f, 12f)
        }
        path(
            stroke = SolidColor(Color(0xFF17372F)),
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

// ── Edit / pencil icon ─────────────────────────────────────────────────────────

internal val EditIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "EditPencil",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Pencil body
            moveTo(11f, 4f)
            lineTo(18f, 4f)
            curveTo(18.53f, 4f, 19.04f, 4.21f, 19.41f, 4.59f)
            curveTo(19.79f, 4.96f, 20f, 5.47f, 20f, 6f)
            lineTo(20f, 18f)
            curveTo(20f, 18.53f, 19.79f, 19.04f, 19.41f, 19.41f)
            curveTo(19.04f, 19.79f, 18.53f, 20f, 18f, 20f)
            lineTo(6f, 20f)
            curveTo(5.47f, 20f, 4.96f, 19.79f, 4.59f, 19.41f)
            curveTo(4.21f, 19.04f, 4f, 18.53f, 4f, 18f)
            lineTo(4f, 13f)
        }
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Edit line
            moveTo(15.5f, 2.5f)
            curveTo(15.89f, 2.11f, 16.42f, 1.89f, 16.97f, 1.89f)
            curveTo(17.52f, 1.89f, 18.05f, 2.11f, 18.44f, 2.5f)
            curveTo(18.83f, 2.89f, 19.05f, 3.42f, 19.05f, 3.97f)
            curveTo(19.05f, 4.52f, 18.83f, 5.05f, 18.44f, 5.44f)
            lineTo(10f, 13.88f)
            lineTo(7f, 14.88f)
            lineTo(8f, 11.88f)
            lineTo(15.5f, 2.5f)
            close()
        }
    }.build()
}

// ── Person icon (for member list) ──────────────────────────────────────────────

internal val PersonIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Person",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Head
            val cx = 12f; val cy = 8f; val r = 4f
            moveTo(cx + r, cy)
            arcTo(r, r, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = cx - r, y1 = cy)
            arcTo(r, r, 0f, isMoreThanHalf = true, isPositiveArc = false, x1 = cx + r, y1 = cy)
            close()
        }
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Body
            moveTo(20f, 21f)
            lineTo(20f, 19f)
            curveTo(20f, 17.94f, 19.58f, 16.92f, 18.83f, 16.17f)
            curveTo(18.08f, 15.42f, 17.06f, 15f, 16f, 15f)
            lineTo(8f, 15f)
            curveTo(6.94f, 15f, 5.92f, 15.42f, 5.17f, 16.17f)
            curveTo(4.42f, 16.92f, 4f, 17.94f, 4f, 19f)
            lineTo(4f, 21f)
        }
    }.build()
}

// ── Home / household icon ──────────────────────────────────────────────────────

internal val HomeIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Home",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3f, 12f)
            lineTo(12f, 3f)
            lineTo(21f, 12f)
        }
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5f, 10f)
            lineTo(5f, 20f)
            curveTo(5f, 20.55f, 5.45f, 21f, 6f, 21f)
            lineTo(18f, 21f)
            curveTo(18.55f, 21f, 19f, 20.55f, 19f, 20f)
            lineTo(19f, 10f)
        }
    }.build()
}
