package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppMode
import com.example.data.model.QuoteValidationHelper
import com.example.data.model.ServiceCatalog
import com.example.data.model.SubmissionStatus
import com.example.ui.components.AppTopBar
import com.example.ui.components.SubmissionStatusChip
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteFormScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onQuoteSubmitted: () -> Unit,
    onSwitchMode: () -> Unit
) {
    val appMode by viewModel.appMode.collectAsState()
    val formState by viewModel.quoteFormState.collectAsState()
    val isSubmitting by viewModel.isSubmittingQuote.collectAsState()
    var serviceDropdownExpanded by remember { mutableStateOf(false) }

    val selectedService = ServiceCatalog.getById(formState.selectedServiceId) ?: ServiceCatalog.services.first()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = if (formState.isReviewMode) "Review Quote Request" else "Request a Quote",
            subtitle = if (appMode == AppMode.OFFLINE_DEMO) "Offline Demo • Saved locally" else "Production Cloud",
            appMode = appMode,
            canNavigateBack = true,
            onNavigateBack = {
                if (formState.isReviewMode) {
                    viewModel.setReviewMode(false)
                } else {
                    onNavigateBack()
                }
            },
            onModeClick = onSwitchMode
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("quote_form_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!formState.isReviewMode) {
                // Intro Note
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Enter your project details. At least one contact method (Ethiopian phone or email) is required. You can save a draft anytime.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }

                // Full Name
                item {
                    OutlinedTextField(
                        value = formState.customerName,
                        onValueChange = { viewModel.updateQuoteFormField(customerName = it) },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Abebe Bikila") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = formState.nameError != null,
                        supportingText = formState.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Phone Input (Ethiopian phone formats)
                item {
                    OutlinedTextField(
                        value = formState.phone,
                        onValueChange = { viewModel.updateQuoteFormField(phone = it) },
                        label = { Text("Phone Number (+251 or 09/07)") },
                        placeholder = { Text("e.g. +251 91 123 4567 or 0911234567") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Email Input
                item {
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { viewModel.updateQuoteFormField(email = it) },
                        label = { Text("Email Address") },
                        placeholder = { Text("e.g. abebe@company.et") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_email"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Contact Method Error Display
                if (formState.contactError != null) {
                    item {
                        Text(
                            text = formState.contactError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Organization / Company
                item {
                    OutlinedTextField(
                        value = formState.organization,
                        onValueChange = { viewModel.updateQuoteFormField(organization = it) },
                        label = { Text("Organization / Company (Optional)") },
                        placeholder = { Text("e.g. Sheger Trading PLC") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_organization"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // City / Site Location
                item {
                    OutlinedTextField(
                        value = formState.cityOrSite,
                        onValueChange = { viewModel.updateQuoteFormField(cityOrSite = it) },
                        label = { Text("City / Site Location *") },
                        placeholder = { Text("e.g. Addis Ababa, Bole Sub-City") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        isError = formState.cityError != null,
                        supportingText = formState.cityError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_city_site"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Service Selector Dropdown
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedService.title,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Engineering Service *") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Service",
                                    modifier = Modifier.clickable { serviceDropdownExpanded = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { serviceDropdownExpanded = true }
                                .testTag("select_service_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        DropdownMenu(
                            expanded = serviceDropdownExpanded,
                            onDismissRequest = { serviceDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            ServiceCatalog.services.forEach { svc ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(svc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(svc.category.displayName, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateQuoteFormField(serviceId = svc.id)
                                        serviceDropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("service_option_${svc.id}")
                                )
                            }
                        }
                    }
                }

                // Requirements & Scope
                item {
                    OutlinedTextField(
                        value = formState.requirements,
                        onValueChange = { viewModel.updateQuoteFormField(requirements = it) },
                        label = { Text("Project Requirements & Scope Description *") },
                        placeholder = { Text("Detail your building size, camera count, kW solar capacity, software specs, or network ports needed…") },
                        minLines = 4,
                        maxLines = 8,
                        isError = formState.requirementsError != null,
                        supportingText = formState.requirementsError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_requirements"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Budget & Date Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.budgetEtbString,
                            onValueChange = { viewModel.updateQuoteFormField(budget = it) },
                            label = { Text("Budget (ETB, Opt)") },
                            placeholder = { Text("e.g. 150000") },
                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_budget"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = formState.targetDateString,
                            onValueChange = { viewModel.updateQuoteFormField(targetDate = it) },
                            label = { Text("Target Date (Opt)") },
                            placeholder = { Text("e.g. Apr 2026") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_target_date"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

            } else {
                // REVIEW MODE CARD
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quote_review_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Please Confirm Your Details",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            ReviewRow("Customer Name", formState.customerName)
                            if (formState.phone.isNotBlank()) {
                                val norm = QuoteValidationHelper.normalizeEthiopianPhone(formState.phone)
                                ReviewRow("Phone (Normalized)", norm)
                            }
                            if (formState.email.isNotBlank()) {
                                ReviewRow("Email", formState.email)
                            }
                            if (formState.organization.isNotBlank()) {
                                ReviewRow("Organization", formState.organization)
                            }
                            ReviewRow("Site Location", formState.cityOrSite)
                            ReviewRow("Selected Service", selectedService.title)
                            ReviewRow("Scope / Requirements", formState.requirements)
                            if (formState.budgetEtbString.isNotBlank()) {
                                ReviewRow("Indicative Budget", "ETB ${formState.budgetEtbString}")
                            }
                            if (formState.targetDateString.isNotBlank()) {
                                ReviewRow("Target Date", formState.targetDateString)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            val modeNotice = if (appMode == AppMode.OFFLINE_DEMO) {
                                "Demo Mode: This quote will be saved on this device (offline in Room) with a unique local reference VLT-DEMO-2026-XXXX."
                            } else {
                                "Production Mode: This quote will be securely submitted to Voltavia cloud servers."
                            }

                            Surface(
                                color = if (appMode == AppMode.OFFLINE_DEMO) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = modeNotice,
                                    modifier = Modifier.padding(10.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = if (appMode == AppMode.OFFLINE_DEMO) Color(0xFF92400E) else Color(0xFF166534)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!formState.isReviewMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.saveDraft() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("action_save_draft_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Draft")
                        }

                        Button(
                            onClick = { viewModel.setReviewMode(true) },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("action_review_submit_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Review Details")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setReviewMode(false) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("action_back_edit_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back to Edit")
                        }

                        Button(
                            onClick = {
                                viewModel.submitQuote {
                                    onQuoteSubmitted()
                                }
                            },
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp)
                                .testTag("submit_quote_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (appMode == AppMode.OFFLINE_DEMO) "Save Locally" else "Submit Now")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}
