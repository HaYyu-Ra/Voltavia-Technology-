package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillingCycle
import com.example.data.model.CompanyConfig
import com.example.data.model.IctPlan
import com.example.data.model.PlanCatalog
import com.example.ui.components.AppTopBar
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PlansScreen(
    viewModel: MainViewModel,
    onSelectPlanForQuote: (String, String) -> Unit,
    onSwitchMode: () -> Unit
) {
    val appMode by viewModel.appMode.collectAsState()
    val billingCycle by viewModel.selectedBillingCycle.collectAsState()
    val includeVat by viewModel.includeVat.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "ICT Support Plans",
            subtitle = "SLA maintenance & preventive care",
            appMode = appMode,
            canNavigateBack = false,
            onModeClick = onSwitchMode
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("plans_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Billing Cycle Tabs (Monthly vs Yearly)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        TabRow(
                            selectedTabIndex = if (billingCycle == BillingCycle.MONTHLY) 0 else 1,
                            containerColor = Color.Transparent,
                            modifier = Modifier.testTag("billing_cycle_tab_row")
                        ) {
                            Tab(
                                selected = billingCycle == BillingCycle.MONTHLY,
                                onClick = { viewModel.setBillingCycle(BillingCycle.MONTHLY) },
                                text = {
                                    Text(
                                        "Monthly Billing",
                                        fontWeight = if (billingCycle == BillingCycle.MONTHLY) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("tab_monthly")
                            )
                            Tab(
                                selected = billingCycle == BillingCycle.YEARLY,
                                onClick = { viewModel.setBillingCycle(BillingCycle.YEARLY) },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "Yearly",
                                            fontWeight = if (billingCycle == BillingCycle.YEARLY) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            color = Color(0xFFDCFCE7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                "Save ~20%",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF166534),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.testTag("tab_yearly")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Configurable Tax (VAT) Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Include 15% VAT estimate",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Configurable tax computation for Ethiopian standard invoicing",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Switch(
                                checked = includeVat,
                                onCheckedChange = { viewModel.toggleVat() },
                                modifier = Modifier.testTag("vat_toggle_switch")
                            )
                        }
                    }
                }
            }

            // Indicative Notice Card
            item {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Notice",
                            tint = Color(0xFF1D4ED8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prices shown are indicative estimates for planning purposes. Official binding contracts and payment terms are executed after preliminary IT audit.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1E40AF),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Plan Cards
            items(PlanCatalog.plans) { plan ->
                PlanCard(
                    plan = plan,
                    cycle = billingCycle,
                    includeVat = includeVat,
                    onSelect = {
                        val summary = plan.computeBilledAmount(billingCycle, includeVat)
                        val periodNote = "Selected Plan: ${plan.name} (${billingCycle.name.lowercase().capitalize()})\nEstimated Total: ETB %,d (Base: ETB %,d, VAT: ETB %,d)".format(
                            summary.totalAmountEtb, summary.baseAmountEtb, summary.vatAmountEtb
                        )
                        viewModel.prefillQuoteForService("svc_ict_support", periodNote)
                        onSelectPlanForQuote(plan.id, plan.name)
                    }
                )
            }
        }
    }
}

@Composable
fun PlanCard(
    plan: IctPlan,
    cycle: BillingCycle,
    includeVat: Boolean,
    onSelect: () -> Unit
) {
    val summary = plan.computeBilledAmount(cycle, includeVat)
    val hasSavings = cycle == BillingCycle.YEARLY && summary.annualSavingsEtb > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (plan.isPopular) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                } else Modifier
            )
            .testTag("plan_card_${plan.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = plan.tag,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
                if (plan.isPopular) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "POPULAR",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pricing Area
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "ETB %,d".format(summary.totalAmountEtb),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = if (cycle == BillingCycle.MONTHLY) " / month" else " / year",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }

            if (includeVat) {
                Text(
                    text = "Base: ETB %,d + 15%% VAT: ETB %,d".format(summary.baseAmountEtb, summary.vatAmountEtb),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            // Annual Savings Badge (shown only when configured prices support savings!)
            if (hasSavings) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Annual Savings: ETB %,d (~%d%% off regular monthly)".format(
                            summary.annualSavingsEtb,
                            plan.calculateAnnualSavingsPercent()
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SLA & Scope Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = plan.coverageHours,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SLA Response: ${plan.responseSla}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Devices,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Coverage: Up to ${plan.maxWorkstations} workstations • ${plan.preventiveVisitsPerMonth} visit(s)/month",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Features list
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                plan.includedFeatures.forEach { feature ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plan_select_button_${plan.id}"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Select & Request Quote")
            }
        }
    }
}
