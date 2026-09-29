package com.example.ipotracker.presentation.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipotracker.data.model.IpoCategory
import com.example.ipotracker.data.model.IpoItem
import com.example.ipotracker.data.model.IpoLifecycleState
import com.example.ipotracker.data.model.IpoStatus
import com.example.ipotracker.data.model.MarketIndex
import com.example.ipotracker.data.model.SubscriptionDayProgress
import com.example.ipotracker.data.model.SubscriptionDetails
import com.example.ipotracker.utils.CurrencyFormatter
import com.example.ipotracker.utils.DateUtils
import com.example.ipotracker.presentation.ipo.IpoSortOption
import com.example.ui.theme.*

@Composable
fun DemoDataBanner(modifier: Modifier = Modifier) {
    Surface(
        color = PrimaryOrangeLight,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, PrimaryOrangeContainer),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Demo data indicator",
                tint = PrimaryOrange,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "DEMO DATA • Indicative preview. Verify with official DRHP/RHP.",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = PrimaryOrangeDark,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MarketSummaryBar(
    indices: List<MarketIndex>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        indices.forEach { index ->
            MarketIndexCard(
                index = index,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MarketIndexCard(
    index: MarketIndex,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(
                text = index.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = index.value,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (index.isPositive) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (index.isPositive) MarketGreen else MarketRed,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${if (index.isPositive) "+" else ""}${index.change} (${CurrencyFormatter.formatPercent(index.percentChange)})",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                    color = if (index.isPositive) MarketGreen else MarketRed,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: IpoStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg, borderCol, text) = when (status) {
        IpoStatus.OPEN -> Quadruple(MarketGreenLight, MarketGreen, MarketGreenBorder, "OPEN")
        IpoStatus.UPCOMING -> Quadruple(PrimaryOrangeLight, PrimaryOrange, PrimaryOrangeContainer, "UPCOMING")
        IpoStatus.CLOSED -> Quadruple(ChipGray, NeutralGray, BorderLight, "CLOSED")
        IpoStatus.ALLOTMENT_PENDING -> Quadruple(ChipGray, NeutralGray, BorderLight, "WAITING")
        IpoStatus.ALLOTMENT_AVAILABLE -> Quadruple(MarketGreenLight, MarketGreen, MarketGreenBorder, "ALLOTMENT")
        IpoStatus.LISTED -> Quadruple(Color(0xFFEDE7F6), Color(0xFF5E35B1), Color(0xFFD1C4E9), "LISTED")
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderCol),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun CategoryBadge(
    category: IpoCategory,
    modifier: Modifier = Modifier
) {
    val isMainboard = category == IpoCategory.MAINBOARD
    Surface(
        color = if (isMainboard) SecondaryBlueLight else PrimaryOrangeLight,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, if (isMainboard) SecondaryBlueContainer else PrimaryOrangeContainer),
        modifier = modifier
    ) {
        Text(
            text = if (isMainboard) "Mainboard" else "SME",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isMainboard) SecondaryBlueDark else PrimaryOrangeDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Visual Accent Bar (Polished Indian FinTech aesthetic)
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PrimaryOrange)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.5.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = SecondaryBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SecondaryBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * High-fidelity, highly polished Indian IPO Mobile Card
 * Tailored for 360dp, 390dp, and 412dp devices
 */
@Composable
fun IpoCard(
    ipo: IpoItem,
    onIpoClick: (String) -> Unit,
    onWatchlistToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    onApplyClick: ((String) -> Unit)? = null,
    onAllotmentClick: ((String) -> Unit)? = null,
    onWatchLiveClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val retailLotsGain = (ipo.currentGmp * ipo.lotSize).coerceAtLeast(0.0)
    val hniLotsGain = (ipo.currentGmp * ipo.lotSize * 14).coerceAtLeast(0.0)
    val isMainboard = ipo.category == IpoCategory.MAINBOARD

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onIpoClick(ipo.id) }
            .testTag("ipo_card_${ipo.id}"),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp
    ) {
        Column {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                // 1. Company Name & Category Pill + Bookmark Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ipo.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CategoryBadge(category = ipo.category)
                    }

                    IconButton(
                        onClick = { onWatchlistToggle(ipo.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (ipo.isWatchlisted) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (ipo.isWatchlisted) PrimaryOrange else NeutralGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Logo Square + Specs Grid (Date, Price, Lot Size, Issue Size)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo Box with high-contrast initials and stylish tint
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isMainboard) SecondaryBlueLight else PrimaryOrangeLight)
                            .border(1.dp, if (isMainboard) SecondaryBlueContainer else PrimaryOrangeContainer, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ipo.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (isMainboard) SecondaryBlue else PrimaryOrange
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 4-row specs with responsive spacing
                    Column(modifier = Modifier.weight(1f)) {
                        IpoSpecRow(
                            label = "Date:",
                            value = "${DateUtils.formatDisplayDate(ipo.openDate)} - ${DateUtils.formatDisplayDate(ipo.closeDate)}"
                        )
                        IpoSpecRow(
                            label = "Price:",
                            value = "₹${ipo.priceBandMin.toInt()} - ₹${ipo.priceBandMax.toInt()}"
                        )
                        IpoSpecRow(
                            label = "Lot Size:",
                            value = "${ipo.lotSize} shares (Min ${CurrencyFormatter.formatRupee(ipo.minInvestment)})"
                        )
                        IpoSpecRow(
                            label = "Issue Size:",
                            value = "${CurrencyFormatter.formatCrores(ipo.issueSizeCr)}"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(7.dp))

                // 3. GMP Rumors* Section with Highlight Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GMP Rumors*:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (ipo.currentGmp > 0) {
                        Surface(
                            color = MarketGreenLight,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, MarketGreenBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "₹${ipo.currentGmp.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MarketGreen
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "(+${"%.1f".format(ipo.estimatedGainPercent)}%)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MarketGreen
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "N/A (Pending)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = NeutralGray
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Last Heard:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ipo.lastGmpUpdated,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))
                HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(7.dp))

                // 4. Allotment & Estimated Gain Grid (2 rows x 2 columns for neat responsive layout)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Allotment Date", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DateUtils.formatDisplayDate(ipo.allotmentDate), style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), fontWeight = FontWeight.SemiBold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Listing Date", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DateUtils.formatDisplayDate(ipo.listingDate), style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GMP x Lot (Retail)*", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (retailLotsGain > 0) CurrencyFormatter.formatRupee(retailLotsGain) else "--",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (retailLotsGain > 0) MarketGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GMP x Lots (sHNI)*", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (hniLotsGain > 0) CurrencyFormatter.formatRupee(hniLotsGain) else "--",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (hniLotsGain > 0) MarketGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Action Buttons based on IPO Lifecycle State Machine:
                // OPEN: [ VIEW ] [ SHARE ] [ APPLY ]
                // CLOSED + ALLOTMENT PENDING: [ VIEW ] [ SHARE ] [ WAITING ]
                // ALLOTMENT AVAILABLE: [ VIEW ] [ SHARE ] [ ALLOTMENT ]
                // LISTED: [ VIEW ] [ SHARE ] [ ALLOTMENT ] with [ WATCH LIVE ] centered below
                val lifecycleState = ipo.lifecycleState

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // VIEW Button (Same dimensions across all states)
                    OutlinedButton(
                        onClick = { onIpoClick(ipo.id) },
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("ipo_view_button_${ipo.id}")
                    ) {
                        Text(
                            text = "VIEW",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    // SHARE Button (Same dimensions across all states)
                    OutlinedButton(
                        onClick = { shareIpo(context, ipo) },
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("ipo_share_button_${ipo.id}")
                    ) {
                        Text(
                            text = "SHARE",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }

                    // Third Action Button based on Lifecycle
                    when (lifecycleState) {
                        IpoLifecycleState.OPEN -> {
                            // [ APPLY ] button
                            OutlinedButton(
                                onClick = { onApplyClick?.invoke(ipo.id) ?: onIpoClick(ipo.id) },
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, BorderLight),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("ipo_apply_button_${ipo.id}")
                            ) {
                                Text(
                                    text = "APPLY",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                        IpoLifecycleState.CLOSED_ALLOTMENT_PENDING -> {
                            // [ WAITING ] button (Gray background, dark gray/black text, disabled/non-clickable)
                            Button(
                                onClick = { /* Disabled / non-clickable */ },
                                enabled = false,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = Color(0xFFE2E8F0),
                                    disabledContentColor = Color(0xFF475569)
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("ipo_waiting_button_${ipo.id}")
                            ) {
                                Text(
                                    text = "WAITING",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.5.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569),
                                    maxLines = 1
                                )
                            }
                        }
                        IpoLifecycleState.ALLOTMENT_AVAILABLE, IpoLifecycleState.LISTED -> {
                            // [ ALLOTMENT ] button (Green background, white text, clickable)
                            Button(
                                onClick = { onAllotmentClick?.invoke(ipo.id) ?: onIpoClick(ipo.id) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MarketGreen,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("ipo_allotment_button_${ipo.id}")
                            ) {
                                Text(
                                    text = "ALLOTMENT",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // If officially LISTED: ADD [ WATCH LIVE ] button centered below the first row
                if (lifecycleState == IpoLifecycleState.LISTED) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { onWatchLiveClick?.invoke(ipo.id) ?: onIpoClick(ipo.id) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(34.dp)
                                .testTag("ipo_watch_live_button_${ipo.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4ADE80))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "WATCH LIVE",
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.5.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "* Info is indicative, not investment advice.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // 6. Bottom Full-Width Colored Ribbon matching card bottom radius
            Surface(
                color = if (isMainboard) SecondaryBlue else PrimaryOrange,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isMainboard) "MAINBOARD IPO" else "SME IPO",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun IpoSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Reusable Subscription Table (Category, Offered, Applied, Times)
 * Polished to perfection for 360dp, 390dp, and 412dp devices
 */
@Composable
fun SubscriptionTableView(
    subscriptionDetails: SubscriptionDetails,
    modifier: Modifier = Modifier,
    useCompactShares: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, TableBorder, RoundedCornerShape(8.dp))
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableHeaderBg)
                .padding(vertical = 7.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.3f)
            )
            Text(
                text = "Offered",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.1f),
                textAlign = TextAlign.End
            )
            Text(
                text = "Applied",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.1f),
                textAlign = TextAlign.End
            )
            Text(
                text = "Times",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.9f),
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider(color = TableBorder)

        // Rows
        subscriptionDetails.categoryRows.forEachIndexed { index, row ->
            val isTotal = row.category.contains("Total", ignoreCase = true)
            val isSubItem = row.category.startsWith("  -")
            val rowBg = if (isTotal) PrimaryOrangeLight else if (index % 2 == 1) TableRowAlt else MaterialTheme.colorScheme.surface

            val offeredText = if (useCompactShares) CurrencyFormatter.formatSharesCompact(row.offeredShares) else CurrencyFormatter.formatShares(row.offeredShares)
            val appliedText = if (useCompactShares) CurrencyFormatter.formatSharesCompact(row.appliedShares) else CurrencyFormatter.formatShares(row.appliedShares)

            val cleanCategoryName = row.category
                .replace("Qualified Institutional (", "")
                .replace("Non-Institutional (", "")
                .replace("Retail Individual (", "")
                .replace(")", "")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(rowBg)
                    .padding(vertical = 6.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cleanCategoryName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = if (isSubItem) 10.sp else 11.sp
                    ),
                    fontWeight = if (isTotal) FontWeight.Bold else if (isSubItem) FontWeight.Normal else FontWeight.Medium,
                    color = if (isTotal) PrimaryOrangeDark else if (isSubItem) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1.3f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = offeredText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                    fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = appliedText,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                    fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1.1f),
                    textAlign = TextAlign.End
                )
                Box(
                    modifier = Modifier.weight(0.9f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (row.times >= 1.0) {
                        Surface(
                            color = MarketGreenLight,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.8.dp, MarketGreenBorder)
                        ) {
                            Text(
                                text = "${"%.2f".format(row.times)}x",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = MarketGreen,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "${"%.2f".format(row.times)}x",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (!isTotal) {
                HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

/**
 * Dynamic Reusable Day-wise Subscription Table
 * Shows actual day-by-day progression for Day 1, Day 2, Day 3
 */
@Composable
fun DayWiseSubscriptionTableView(
    dayProgress: List<SubscriptionDayProgress>,
    modifier: Modifier = Modifier
) {
    if (dayProgress.isEmpty()) {
        Surface(
            color = ChipGray,
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = "Day-wise bidding progression data will be published at 5:00 PM IST on bidding days.",
                style = MaterialTheme.typography.bodySmall,
                color = NeutralGray,
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val days = dayProgress.take(3)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, TableBorder, RoundedCornerShape(8.dp))
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TableHeaderBg)
                .padding(vertical = 7.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Category", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
            days.forEach { day ->
                Text(
                    text = day.dayLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }
        HorizontalDivider(color = TableBorder)

        // QIB Row
        DayWiseDataRowDynamic(
            category = "QIB",
            values = days.map { "${"%.2f".format(it.qibTimes)}x" },
            isAlt = false
        )
        HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

        // NII Row
        DayWiseDataRowDynamic(
            category = "NII / HNI",
            values = days.map { "${"%.2f".format(it.niiTimes)}x" },
            isAlt = true
        )
        HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

        // Retail Row
        DayWiseDataRowDynamic(
            category = "Retail (RII)",
            values = days.map { "${"%.2f".format(it.retailTimes)}x" },
            isAlt = false
        )

        // Employee Row (if any day has employee times > 0)
        if (days.any { it.employeeTimes > 0 }) {
            HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            DayWiseDataRowDynamic(
                category = "Employee",
                values = days.map { "${"%.2f".format(it.employeeTimes)}x" },
                isAlt = true
            )
        }

        // Other Row (if any day has other times > 0)
        if (days.any { it.otherTimes > 0 }) {
            HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            DayWiseDataRowDynamic(
                category = "Other",
                values = days.map { "${"%.2f".format(it.otherTimes)}x" },
                isAlt = false
            )
        }

        HorizontalDivider(color = TableBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

        // Total Row
        DayWiseDataRowDynamic(
            category = "Total",
            values = days.map { "${"%.2f".format(it.overallTimes)}x" },
            isBold = true,
            isTotal = true
        )
    }
}

@Composable
private fun DayWiseDataRowDynamic(
    category: String,
    values: List<String>,
    isBold: Boolean = false,
    isAlt: Boolean = false,
    isTotal: Boolean = false
) {
    val bg = if (isTotal) PrimaryOrangeLight else if (isAlt) TableRowAlt else MaterialTheme.colorScheme.surface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(vertical = 6.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isTotal) PrimaryOrangeDark else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.3f)
        )
        values.forEach { v ->
            Text(
                text = v,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                color = if (isTotal) PrimaryOrangeDark else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

fun shareIpo(context: Context, ipo: IpoItem) {
    val text = buildString {
        appendLine("📊 ${ipo.name} (${ipo.symbol}) IPO Details")
        appendLine("• Status: ${ipo.status.name}")
        appendLine("• Price Band: ₹${ipo.priceBandMin.toInt()} - ₹${ipo.priceBandMax.toInt()}")
        appendLine("• Lot Size: ${ipo.lotSize} shares (Min: ${CurrencyFormatter.formatRupee(ipo.minInvestment)})")
        appendLine("• Issue Size: ${CurrencyFormatter.formatCrores(ipo.issueSizeCr)}")
        if (ipo.currentGmp > 0) {
            appendLine("• Current GMP: ₹${ipo.currentGmp.toInt()} (+${CurrencyFormatter.formatPercent(ipo.estimatedGainPercent, false)})")
        }
        if (ipo.currentSubscriptionTimes > 0) {
            appendLine("• Subscription: ${CurrencyFormatter.formatSubscription(ipo.currentSubscriptionTimes)}")
        }
        appendLine("• Issue Dates: ${DateUtils.formatDisplayDate(ipo.openDate)} to ${DateUtils.formatDisplayDate(ipo.closeDate)}")
        appendLine("• Listing Date: ${DateUtils.formatDisplayDate(ipo.listingDate)}")
        appendLine("Track, Compare & Understand Indian IPOs on IPODekho")
    }
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share IPO Info"))
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            tint = NeutralGray,
            modifier = Modifier.size(48.dp)
        )
    },
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(actionLabel, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Market Type Multi-Select Filter with Material 3 FilterChip / Checkbox styling.
 *
 * Enforces strict selection rules:
 * 1. Mainboard can be selected alone.
 * 2. SME can be selected alone.
 * 3. Mainboard and SME can both be selected.
 * 4. Mainboard and SME must NEVER both be unselected.
 * 5. At least one option must always remain selected.
 * 6. If user tries to unselect the last selected option, prevent action and emit warning.
 * 7. Calls onValidationWarning with "Please select at least one market type."
 */
@Composable
fun MarketTypeFilter(
    isMainboardSelected: Boolean,
    isSmeSelected: Boolean,
    onMarketTypeChanged: (mainboard: Boolean, sme: Boolean) -> Unit,
    onValidationWarning: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Market Type:",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Mainboard FilterChip
        FilterChip(
            selected = isMainboardSelected,
            onClick = {
                if (isMainboardSelected) {
                    if (!isSmeSelected) {
                        onValidationWarning("Please select at least one market type.")
                    } else {
                        onMarketTypeChanged(false, isSmeSelected)
                    }
                } else {
                    onMarketTypeChanged(true, isSmeSelected)
                }
            },
            label = {
                Text(
                    text = "Mainboard",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = if (isMainboardSelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = if (isMainboardSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = if (isMainboardSelected) "Mainboard selected" else "Mainboard unselected",
                    tint = if (isMainboardSelected) PrimaryOrange else NeutralGray,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PrimaryOrangeLight,
                selectedLabelColor = PrimaryOrangeDark,
                containerColor = MaterialTheme.colorScheme.surface,
                labelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isMainboardSelected,
                borderColor = if (isMainboardSelected) PrimaryOrange else BorderLight,
                selectedBorderColor = PrimaryOrange,
                borderWidth = 1.dp,
                selectedBorderWidth = 1.dp
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(32.dp)
                .testTag("filter_market_mainboard")
        )

        // SME FilterChip
        FilterChip(
            selected = isSmeSelected,
            onClick = {
                if (isSmeSelected) {
                    if (!isMainboardSelected) {
                        onValidationWarning("Please select at least one market type.")
                    } else {
                        onMarketTypeChanged(isMainboardSelected, false)
                    }
                } else {
                    onMarketTypeChanged(isMainboardSelected, true)
                }
            },
            label = {
                Text(
                    text = "SME",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = if (isSmeSelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = if (isSmeSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = if (isSmeSelected) "SME selected" else "SME unselected",
                    tint = if (isSmeSelected) SecondaryBlue else NeutralGray,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = SecondaryBlueLight,
                selectedLabelColor = SecondaryBlue,
                containerColor = MaterialTheme.colorScheme.surface,
                labelColor = MaterialTheme.colorScheme.onSurface
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSmeSelected,
                borderColor = if (isSmeSelected) SecondaryBlue else BorderLight,
                selectedBorderColor = SecondaryBlue,
                borderWidth = 1.dp,
                selectedBorderWidth = 1.dp
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(32.dp)
                .testTag("filter_market_sme")
        )
    }
}

/**
 * Professional, compact "Sort By ▼" dropdown control for IPO listing.
 *
 * Strict Styling Constraints:
 * - Sort option text must be BLACK or DARK GRAY.
 * - Do NOT use orange, blue, green or other colors for the sort text.
 * - Selected sort option has a subtle gray background.
 * - Compact and professional appearance.
 */
@Composable
fun IpoSortDropdown(
    selectedOption: IpoSortOption,
    onSortOptionSelected: (IpoSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF7F7F7),
            border = BorderStroke(1.dp, Color(0xFFD6D6D6)),
            modifier = Modifier
                .height(32.dp)
                .testTag("sort_by_dropdown_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E1E1E) // BLACK / DARK GRAY ONLY
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Sort options",
                    tint = Color(0xFF2E2E2E), // BLACK / DARK GRAY ONLY
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .testTag("sort_dropdown_menu")
        ) {
            IpoSortOption.values().forEach { option ->
                val isSelected = option == selectedOption
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF111111) else Color(0xFF333333) // BLACK / DARK GRAY ONLY
                                )
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF424242), // DARK GRAY ONLY
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSortOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .background(if (isSelected) Color(0xFFEEEEEE) else Color.Transparent) // Subtle gray background for selected
                        .testTag("sort_option_${option.name.lowercase()}")
                )
            }
        }
    }
}


