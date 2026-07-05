package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Design parameters: 12dp rounded corners for Cards, 8dp for Buttons/TextFields.
val ButtonShape = RoundedCornerShape(8.dp)
val CardShape = RoundedCornerShape(12.dp)

val Shapes = Shapes(
    small = ButtonShape,
    medium = CardShape,
    large = RoundedCornerShape(16.dp)
)
