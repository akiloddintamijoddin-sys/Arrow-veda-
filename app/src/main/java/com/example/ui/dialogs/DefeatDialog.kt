package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ArrowThemePalette

@Composable
fun DefeatDialog(
    coins: Int,
    palette: ArrowThemePalette,
    onRevive: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    val canAffordRevive = coins >= 50

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, palette.boardBorder, RoundedCornerShape(28.dp))
                .testTag("defeat_dialog"),
            color = palette.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heart break badge
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(palette.errorColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "No hearts",
                        tint = palette.errorColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Out of Hearts!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Untangling requires patience. Ready for another try or keep going?",
                    fontSize = 13.sp,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Revive Option Button
                Button(
                    onClick = onRevive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("defeat_revive_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.clearHighlight,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (canAffordRevive) "Revive (50 🪙)" else "Free Daily Revive",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Restart Button
                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("defeat_restart_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.surfaceVariant,
                        contentColor = palette.textPrimary
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Restart Level (3 Hearts)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Home Button
                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("defeat_home_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = palette.textSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Main Menu", fontSize = 13.sp)
                }
            }
        }
    }
}
