package com.example.ipotracker.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipotracker.data.model.FinancialYearData
import com.example.ipotracker.data.model.GmpHistoryItem
import com.example.ipotracker.data.model.SubscriptionDayProgress
import com.example.ipotracker.data.model.SubscriptionDetails
import com.example.ui.theme.*

@Composable
fun SubscriptionGrowthChart(
    days: List<SubscriptionDayProgress>,
    modifier: Modifier = Modifier
) {
    if (days.isEmpty()) return

    val maxVal = (days.maxOfOrNull { it.overallTimes } ?: 1.0).coerceAtLeast(1.0) * 1.25

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "SUBSCRIPTION PROGRESSION (DAYS)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Bars with day labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    val barHeightFraction = (day.overallTimes / maxVal).toFloat().coerceIn(0.05f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "%.2fx".format(day.overallTimes),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = SecondaryBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(barHeightFraction)
                                .width(28.dp)
                                .background(
                                    color = SecondaryBlue,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GmpTrendChart(
    history: List<GmpHistoryItem>,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) return

    val minGmp = history.minOf { it.gmp }
    val maxGmp = history.maxOf { it.gmp }.coerceAtLeast(minGmp + 1.0)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GMP TREND (₹ PER SHARE)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Latest: ₹${history.last().gmp.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MarketGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val width = size.width
                val height = size.height
                val stepX = width / (history.size - 1).coerceAtLeast(1)

                val points = history.mapIndexed { index, item ->
                    val x = index * stepX
                    val normY = ((item.gmp - minGmp) / (maxGmp - minGmp)).toFloat().coerceIn(0f, 1f)
                    val y = height - (normY * (height - 30f) + 15f)
                    Offset(x, y)
                }

                // Draw path line
                val path = Path()
                points.forEachIndexed { i, pt ->
                    if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                }

                drawPath(
                    path = path,
                    color = MarketGreen,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw points
                points.forEach { pt ->
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = MarketGreen,
                        radius = 3.dp.toPx(),
                        center = pt
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                history.forEach { item ->
                    Text(
                        text = item.date.take(6),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialsBarChart(
    financials: List<FinancialYearData>,
    modifier: Modifier = Modifier
) {
    if (financials.isEmpty()) return

    val maxVal = financials.maxOf { it.revenueCr }.coerceAtLeast(1.0) * 1.15

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder(),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REVENUE & PROFIT TREND (₹ CR)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(SecondaryBlue, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revenue", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(MarketGreen, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PAT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                financials.forEach { item ->
                    val revHeight = (item.revenueCr / maxVal).toFloat().coerceIn(0.08f, 1f)
                    val patHeight = (item.patCr / maxVal).toFloat().coerceIn(0.04f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxHeight(0.85f)
                        ) {
                            // Revenue Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(revHeight)
                                    .width(14.dp)
                                    .background(
                                        color = SecondaryBlue,
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                            // PAT Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(patHeight)
                                    .width(14.dp)
                                    .background(
                                        color = MarketGreen,
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.fiscalYear,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

/**
 * Polished Category-Wise Subscription Bar Chart for Mobile
 * Displays Overall, QIB, NII/HNI, Retail, Employee, Other with full 1.0x oversubscription benchmark
 */
@Composable
fun CategorySubscriptionBarChart(
    subscriptionDetails: SubscriptionDetails,
    modifier: Modifier = Modifier
) {
    data class CategoryItem(val name: String, val times: Double, val color: Color, val tag: String)
    val items = mutableListOf<CategoryItem>()

    // Overall
    items.add(CategoryItem("Overall Issue", subscriptionDetails.overallTimes, PrimaryOrange, "TOTAL"))
    // QIB
    if (subscriptionDetails.qibTimes > 0) {
        items.add(CategoryItem("QIB (Institutional)", subscriptionDetails.qibTimes, SecondaryBlue, "QIB"))
    }
    // NII/HNI
    if (subscriptionDetails.niiTimes > 0) {
        items.add(CategoryItem("NII / HNI", subscriptionDetails.niiTimes, Color(0xFF7C3AED), "NII"))
    }
    // Retail
    if (subscriptionDetails.retailTimes > 0) {
        items.add(CategoryItem("Retail Individual (RII)", subscriptionDetails.retailTimes, MarketGreen, "RII"))
    }
    // Employee
    if (subscriptionDetails.employeeTimes > 0) {
        items.add(CategoryItem("Employee Quota", subscriptionDetails.employeeTimes, Color(0xFFD97706), "EMP"))
    }
    // Other
    if (subscriptionDetails.otherTimes > 0) {
        items.add(CategoryItem("Other / Shareholders", subscriptionDetails.otherTimes, Color(0xFF0D9488), "OTH"))
    }

    if (items.isEmpty()) return

    val maxTimes = (items.maxOfOrNull { it.times } ?: 1.0).coerceAtLeast(1.0)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CATEGORY SUBSCRIPTION MULTIPLES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    color = MarketGreenLight,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MarketGreenBorder)
                ) {
                    Text(
                        text = "≥ 1.0x Subscribed",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = MarketGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                val fraction = (item.times / maxTimes).toFloat().coerceIn(0.04f, 1f)
                val isOverSubscribed = item.times >= 1.0

                Column(modifier = Modifier.padding(vertical = 5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = item.color.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = item.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = item.color,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "${"%.2f".format(item.times)}x",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (isOverSubscribed) item.color else NeutralGray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ChipGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(item.color)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Polished Day 1 / Day 2 / Day 3 Trend Chart for Mobile
 * Visualizes the demand acceleration across bidding days for QIB, NII, Retail, and Overall
 */
@Composable
fun DayWiseProgressionTrendChart(
    days: List<SubscriptionDayProgress>,
    modifier: Modifier = Modifier
) {
    if (days.isEmpty()) return

    val maxVal = (days.maxOfOrNull { it.overallTimes } ?: 1.0).coerceAtLeast(1.0) * 1.18

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAY 1 → DAY 2 → DAY 3 DEMAND TREND",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "BSE + NSE Bids",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day Milestone Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                days.forEachIndexed { index, day ->
                    val isFinal = index == days.size - 1
                    Surface(
                        color = if (isFinal) PrimaryOrangeLight else ChipGray,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isFinal) PrimaryOrange else BorderLight),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = if (isFinal) PrimaryOrangeDark else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${"%.2f".format(day.overallTimes)}x",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                fontWeight = FontWeight.Bold,
                                color = if (isFinal) PrimaryOrange else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = day.date,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-category comparison grouped bars across days
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${"%.2f".format(day.overallTimes)}x",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxHeight(0.78f)
                        ) {
                            // QIB Bar
                            val qibH = (day.qibTimes / maxVal).toFloat().coerceIn(0.04f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(qibH)
                                    .width(7.dp)
                                    .background(SecondaryBlue, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            )
                            // NII Bar
                            val niiH = (day.niiTimes / maxVal).toFloat().coerceIn(0.04f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(niiH)
                                    .width(7.dp)
                                    .background(Color(0xFF7C3AED), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            )
                            // Retail Bar
                            val retH = (day.retailTimes / maxVal).toFloat().coerceIn(0.04f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(retH)
                                    .width(7.dp)
                                    .background(MarketGreen, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            )
                            // Total Overall Bar
                            val totH = (day.overallTimes / maxVal).toFloat().coerceIn(0.04f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(totH)
                                    .width(11.dp)
                                    .background(PrimaryOrange, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartLegendItem("QIB", SecondaryBlue)
                Spacer(modifier = Modifier.width(10.dp))
                ChartLegendItem("NII", Color(0xFF7C3AED))
                Spacer(modifier = Modifier.width(10.dp))
                ChartLegendItem("Retail", MarketGreen)
                Spacer(modifier = Modifier.width(10.dp))
                ChartLegendItem("Total", PrimaryOrange)
            }
        }
    }
}

@Composable
private fun ChartLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

