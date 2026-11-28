package com.softwarica.splitstay.view.household

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Confirmation dialog for deleting a household.
 * The "Delete" button is enabled only when [confirmText] exactly matches [householdName].
 */
@Composable
fun DeleteHouseholdDialog(
    householdName: String,
    confirmText: String,
    isDeleting: Boolean,
    deleteError: String?,
    onConfirmTextChange: (String) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val dangerRed = ErrorRed // #C0392B from HouseholdColors
    val confirmMatches = confirmText.trim() == householdName

    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        containerColor = CardBackground,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Delete household?",
                color = TitleColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "This permanently deletes $householdName and removes all its members. This cannot be undone.",
                    color = SubtitleGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Type the household name to confirm",
                    color = SubtitleGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmText,
                    onValueChange = onConfirmTextChange,
                    enabled = !isDeleting,
                    placeholder = {
                        Text(householdName, fontSize = 14.sp, color = SubtitleGray.copy(alpha = 0.5f))
                    },
                    textStyle = TextStyle(fontSize = 14.sp, color = TitleColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = dangerRed,
                        unfocusedBorderColor = InputBorder,
                        focusedTextColor = TitleColor,
                        unfocusedTextColor = TitleColor
                    )
                )

                if (deleteError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = deleteError,
                        color = dangerRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                enabled = confirmMatches && !isDeleting,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = dangerRed,
                    contentColor = Color.White,
                    disabledContainerColor = dangerRed.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isDeleting,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDeleting) SubtitleGray.copy(alpha = 0.4f) else SubtitleGray
                )
            ) {
                Text(
                    text = "Cancel",
                    color = if (isDeleting) SubtitleGray.copy(alpha = 0.4f) else SubtitleGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    )
}
