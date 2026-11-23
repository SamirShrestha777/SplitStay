package com.softwarica.splitstay.view.verifyemail

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
        name = "VerifyBackArrow",
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

// ── Mail icon (envelope) ───────────────────────────────────────────────────────

internal val MailIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "MailIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Envelope body
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            // Rounded rectangle: M4,4 → 20,4 → 20,20 → 4,20 close
            moveTo(4f, 4f)
            lineTo(20f, 4f)
            curveTo(21.1f, 4f, 22f, 4.9f, 22f, 6f)
            lineTo(22f, 18f)
            curveTo(22f, 19.1f, 21.1f, 20f, 20f, 20f)
            lineTo(4f, 20f)
            curveTo(2.9f, 20f, 2f, 19.1f, 2f, 18f)
            lineTo(2f, 6f)
            curveTo(2f, 4.9f, 2.9f, 4f, 4f, 4f)
            close()
        }
        // Envelope flap (V shape)
        path(
            stroke = SolidColor(Color(0xFF0F5F57)),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(22f, 6f)
            lineTo(12f, 13f)
            lineTo(2f, 6f)
        }
    }.build()
}
