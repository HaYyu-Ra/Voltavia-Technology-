package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TicketCategory
import com.example.data.model.TicketPriority
import com.example.ui.components.AppTopBar
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SupportCreateScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onTicketCreated: () -> Unit
) {
    val appMode by viewModel.appMode.collectAsState()
    val isSubmitting by viewModel.isSubmittingTicket.collectAsState()

    var selectedCategory by remember { mutableStateOf(TicketCategory.NETWORK_OUTAGE) }
    var selectedPriority by remember { mutableStateOf(TicketPriority.MEDIUM) }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }

    var subjectError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Open Support Ticket",
            subtitle = "Voltavia 24/7 Engineering Desk",
            appMode = appMode,
            canNavigateBack = true,
            onNavigateBack = onNavigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("support_create_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Selector
            item {
                Text(
                    text = "Issue Category *",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Category",
                                modifier = Modifier.clickable { categoryDropdownExpanded = true }
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryDropdownExpanded = true }
                            .testTag("input_ticket_category"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        TicketCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Priority Level
            item {
                Text(
                    text = "Priority Level *",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TicketPriority.values()) { prio ->
                        val isSelected = selectedPriority == prio
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPriority = prio },
                            label = { Text(prio.displayName, fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.testTag("priority_chip_${prio.name.lowercase()}")
                        )
                    }
                }
            }

            // Subject / Title
            item {
                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        if (it.isNotBlank()) subjectError = null
                    },
                    label = { Text("Subject / Issue Title *") },
                    placeholder = { Text("e.g. 2nd floor core switch packet drops") },
                    leadingIcon = { Icon(Icons.Default.Subject, contentDescription = null) },
                    isError = subjectError != null,
                    supportingText = subjectError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_ticket_subject"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Detailed Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (it.isNotBlank()) descriptionError = null
                    },
                    label = { Text("Detailed Description *") },
                    placeholder = { Text("Describe symptoms, affected equipment, error messages, and what steps were already attempted…") },
                    minLines = 4,
                    maxLines = 8,
                    isError = descriptionError != null,
                    supportingText = descriptionError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_ticket_description"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Contact Person Name
            item {
                OutlinedTextField(
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    label = { Text("On-Site Contact Person Name") },
                    placeholder = { Text("e.g. Abebe Kebede (IT Officer)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_ticket_contact_person"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Bottom Submission Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        var valid = true
                        if (subject.trim().isBlank()) {
                            subjectError = "Subject is required."
                            valid = false
                        }
                        if (description.trim().isBlank()) {
                            descriptionError = "Description is required."
                            valid = false
                        }

                        if (valid) {
                            viewModel.submitTicket(
                                category = selectedCategory,
                                priority = selectedPriority,
                                subject = subject,
                                description = description,
                                contactPerson = contactPerson,
                                onSuccess = onTicketCreated
                            )
                        }
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_ticket_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit Support Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
