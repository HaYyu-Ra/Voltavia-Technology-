package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppMode
import com.example.data.model.BillingCycle
import com.example.data.model.CompanyConfig
import com.example.data.model.IctPlan
import com.example.data.model.PlanCatalog
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectMilestone
import com.example.data.model.QuoteRequest
import com.example.data.model.QuoteValidationHelper
import com.example.data.model.SampleProjects
import com.example.data.model.ServiceCatalog
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.SubmissionStatus
import com.example.data.model.SupportTicket
import com.example.data.model.TicketCategory
import com.example.data.model.TicketPriority
import com.example.data.model.TicketReply
import com.example.data.model.TicketStatus
import com.example.data.repository.VoltaviaRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class QuoteFormState(
    val customerName: String = "",
    val phone: String = "",
    val email: String = "",
    val organization: String = "",
    val cityOrSite: String = "",
    val selectedServiceId: String = "svc_solar",
    val requirements: String = "",
    val budgetEtbString: String = "",
    val targetDateString: String = "",
    val nameError: String? = null,
    val contactError: String? = null,
    val cityError: String? = null,
    val requirementsError: String? = null,
    val isReviewMode: Boolean = false
)

enum class ServiceSortOrder(val displayName: String) {
    RECOMMENDED("Recommended"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low")
}

class MainViewModel(
    private val repository: VoltaviaRepository
) : ViewModel() {

    val appMode: StateFlow<AppMode> = repository.appMode

    // Services state
    private val _serviceSearchQuery = MutableStateFlow("")
    val serviceSearchQuery: StateFlow<String> = _serviceSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ServiceCategory.ALL)
    val selectedCategory: StateFlow<ServiceCategory> = _selectedCategory.asStateFlow()

    private val _serviceSortOrder = MutableStateFlow(ServiceSortOrder.RECOMMENDED)
    val serviceSortOrder: StateFlow<ServiceSortOrder> = _serviceSortOrder.asStateFlow()

    val filteredServices: StateFlow<List<ServiceItem>> = combine(
        _serviceSearchQuery,
        _selectedCategory,
        _serviceSortOrder
    ) { query, category, sort ->
        filterServices(query, category, sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ServiceCatalog.services)

    fun filterServices(
        query: String = _serviceSearchQuery.value,
        category: ServiceCategory = _selectedCategory.value,
        sort: ServiceSortOrder = _serviceSortOrder.value
    ): List<ServiceItem> {
        val filtered = ServiceCatalog.services.filter { item ->
            val matchesCategory = category == ServiceCategory.ALL || item.category == category
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.shortSummary.contains(query, ignoreCase = true) ||
                    item.technologies.any { it.contains(query, ignoreCase = true) } ||
                    item.typicalScope.any { it.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }

        return when (sort) {
            ServiceSortOrder.RECOMMENDED -> filtered
            ServiceSortOrder.PRICE_LOW_HIGH -> filtered.sortedBy { it.indicativeStartingPriceEtb }
            ServiceSortOrder.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.indicativeStartingPriceEtb }
        }
    }

    fun setServiceSortOrder(sortOrder: ServiceSortOrder) {
        _serviceSortOrder.value = sortOrder
    }

    // Plans state
    private val _selectedBillingCycle = MutableStateFlow(BillingCycle.YEARLY)
    val selectedBillingCycle: StateFlow<BillingCycle> = _selectedBillingCycle.asStateFlow()

    private val _includeVat = MutableStateFlow(true)
    val includeVat: StateFlow<Boolean> = _includeVat.asStateFlow()

    // Quotes state
    val allQuotes: StateFlow<List<QuoteRequest>> = repository.getAllQuotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _quoteFormState = MutableStateFlow(QuoteFormState())
    val quoteFormState: StateFlow<QuoteFormState> = _quoteFormState.asStateFlow()

    private val _isSubmittingQuote = MutableStateFlow(false)
    val isSubmittingQuote: StateFlow<Boolean> = _isSubmittingQuote.asStateFlow()

    private val _lastSubmittedQuote = MutableStateFlow<QuoteRequest?>(null)
    val lastSubmittedQuote: StateFlow<QuoteRequest?> = _lastSubmittedQuote.asStateFlow()

    // Projects state
    val allProjects: StateFlow<List<ProjectItem>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SampleProjects.sampleProjects)

    // Support tickets state
    val allTickets: StateFlow<List<SupportTicket>> = repository.getAllTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSubmittingTicket = MutableStateFlow(false)
    val isSubmittingTicket: StateFlow<Boolean> = _isSubmittingTicket.asStateFlow()

    // One-shot UI events (Toast/Snackbar)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
            loadDraftIfExists()
        }
    }

    fun setAppMode(mode: AppMode) {
        repository.setAppMode(mode)
        viewModelScope.launch {
            _snackbarEvent.emit("Switched to ${if (mode == AppMode.OFFLINE_DEMO) "Offline Demo Mode" else "Production Cloud Mode"}")
        }
    }

    fun setSearchQuery(query: String) {
        _serviceSearchQuery.value = query
    }

    fun setCategory(category: ServiceCategory) {
        _selectedCategory.value = category
    }

    fun setBillingCycle(cycle: BillingCycle) {
        _selectedBillingCycle.value = cycle
    }

    fun toggleVat() {
        _includeVat.value = !_includeVat.value
    }

    // --- Quote Form Handling ---
    fun prefillQuoteForService(serviceId: String, note: String = "") {
        _quoteFormState.value = _quoteFormState.value.copy(
            selectedServiceId = serviceId,
            requirements = if (note.isNotBlank() && _quoteFormState.value.requirements.isBlank()) note else _quoteFormState.value.requirements,
            isReviewMode = false
        )
    }

    fun updateQuoteFormField(
        customerName: String? = null,
        phone: String? = null,
        email: String? = null,
        organization: String? = null,
        cityOrSite: String? = null,
        serviceId: String? = null,
        requirements: String? = null,
        budget: String? = null,
        targetDate: String? = null
    ) {
        val curr = _quoteFormState.value
        _quoteFormState.value = curr.copy(
            customerName = customerName ?: curr.customerName,
            phone = phone ?: curr.phone,
            email = email ?: curr.email,
            organization = organization ?: curr.organization,
            cityOrSite = cityOrSite ?: curr.cityOrSite,
            selectedServiceId = serviceId ?: curr.selectedServiceId,
            requirements = requirements ?: curr.requirements,
            budgetEtbString = budget ?: curr.budgetEtbString,
            targetDateString = targetDate ?: curr.targetDateString,
            // Clear errors on change
            nameError = if (customerName != null) null else curr.nameError,
            contactError = if (phone != null || email != null) null else curr.contactError,
            cityError = if (cityOrSite != null) null else curr.cityError,
            requirementsError = if (requirements != null) null else curr.requirementsError
        )
    }

    fun setReviewMode(enabled: Boolean) {
        if (enabled && !validateQuoteForm()) {
            return
        }
        _quoteFormState.value = _quoteFormState.value.copy(isReviewMode = enabled)
    }

    private fun validateQuoteForm(): Boolean {
        val form = _quoteFormState.value
        var isValid = true

        var nameErr: String? = null
        if (form.customerName.trim().isBlank()) {
            nameErr = "Full name is required."
            isValid = false
        }

        var contactErr: String? = null
        val hasPhone = form.phone.trim().isNotBlank()
        val hasEmail = form.email.trim().isNotBlank()

        if (!hasPhone && !hasEmail) {
            contactErr = "Please provide at least one contact method (Phone or Email)."
            isValid = false
        } else {
            if (hasPhone && !QuoteValidationHelper.isValidEthiopianPhone(form.phone)) {
                contactErr = "Enter a valid Ethiopian phone (+2519..., +2517..., 09..., or 07...)."
                isValid = false
            }
            if (hasEmail && !QuoteValidationHelper.isValidEmail(form.email)) {
                contactErr = "Enter a valid email address."
                isValid = false
            }
        }

        var cityErr: String? = null
        if (form.cityOrSite.trim().isBlank()) {
            cityErr = "City or site location is required."
            isValid = false
        }

        var reqErr: String? = null
        if (form.requirements.trim().isBlank()) {
            reqErr = "Please describe your project requirements."
            isValid = false
        }

        _quoteFormState.value = form.copy(
            nameError = nameErr,
            contactError = contactErr,
            cityError = cityErr,
            requirementsError = reqErr
        )
        return isValid
    }

    private suspend fun loadDraftIfExists() {
        val draft = repository.getActiveDraft() ?: return
        _quoteFormState.value = QuoteFormState(
            customerName = draft.customerName,
            phone = draft.phone,
            email = draft.email,
            organization = draft.organization,
            cityOrSite = draft.cityOrSite,
            selectedServiceId = draft.serviceId,
            requirements = draft.requirements,
            budgetEtbString = draft.budgetEtb?.toString() ?: "",
            targetDateString = draft.targetDate,
            isReviewMode = false
        )
    }

    fun saveDraft() {
        viewModelScope.launch {
            val form = _quoteFormState.value
            val service = ServiceCatalog.getById(form.selectedServiceId)
            val budget = form.budgetEtbString.trim().toLongOrNull()
            val normalizedPhone = if (form.phone.isNotBlank()) QuoteValidationHelper.normalizeEthiopianPhone(form.phone) else ""

            val draftQuote = QuoteRequest(
                id = UUID.randomUUID().toString(),
                referenceNumber = QuoteValidationHelper.generateReference(appMode.value == AppMode.PRODUCTION_CLOUD),
                customerName = form.customerName.trim(),
                phone = normalizedPhone,
                email = form.email.trim(),
                organization = form.organization.trim(),
                cityOrSite = form.cityOrSite.trim(),
                serviceId = form.selectedServiceId,
                serviceTitle = service?.title ?: "Custom Service",
                requirements = form.requirements.trim(),
                budgetEtb = budget,
                targetDate = form.targetDateString.trim(),
                isDraft = true,
                status = SubmissionStatus.DRAFT
            )
            repository.saveDraft(draftQuote)
            _snackbarEvent.emit("Draft saved to device successfully.")
        }
    }

    fun submitQuote(onSuccess: (QuoteRequest) -> Unit) {
        if (!validateQuoteForm()) return

        val form = _quoteFormState.value
        val service = ServiceCatalog.getById(form.selectedServiceId)
        val budget = form.budgetEtbString.trim().toLongOrNull()
        val normalizedPhone = if (form.phone.isNotBlank()) QuoteValidationHelper.normalizeEthiopianPhone(form.phone) else ""
        val isProd = appMode.value == AppMode.PRODUCTION_CLOUD

        viewModelScope.launch {
            _isSubmittingQuote.value = true
            val newQuote = QuoteRequest(
                id = UUID.randomUUID().toString(),
                referenceNumber = QuoteValidationHelper.generateReference(isProd),
                customerName = form.customerName.trim(),
                phone = normalizedPhone,
                email = form.email.trim(),
                organization = form.organization.trim(),
                cityOrSite = form.cityOrSite.trim(),
                serviceId = form.selectedServiceId,
                serviceTitle = service?.title ?: "Custom Service",
                requirements = form.requirements.trim(),
                budgetEtb = budget,
                targetDate = form.targetDateString.trim(),
                isDraft = false,
                status = if (isProd) SubmissionStatus.SUBMITTED else SubmissionStatus.SAVED_ON_DEVICE
            )

            val result = repository.submitQuote(newQuote, isProd)
            _isSubmittingQuote.value = false

            result.onSuccess { submitted ->
                _lastSubmittedQuote.value = submitted
                // Reset form state
                _quoteFormState.value = QuoteFormState()
                onSuccess(submitted)
            }.onFailure { err ->
                _snackbarEvent.emit(err.message ?: "Failed to submit quote.")
            }
        }
    }

    // --- Support Ticket Handling ---
    fun submitTicket(
        category: TicketCategory,
        priority: TicketPriority,
        subject: String,
        description: String,
        contactPerson: String,
        onSuccess: () -> Unit
    ) {
        if (subject.isBlank() || description.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit("Subject and description are required.")
            }
            return
        }

        viewModelScope.launch {
            _isSubmittingTicket.value = true
            val nowFormatted = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date())
            val randomSuffix = (1000..9999).random()
            val refCode = if (appMode.value == AppMode.PRODUCTION_CLOUD) "TKT-PROD-$randomSuffix" else "TKT-DEMO-$randomSuffix"

            val ticket = SupportTicket(
                id = UUID.randomUUID().toString(),
                referenceCode = refCode,
                category = category,
                priority = priority,
                subject = subject.trim(),
                description = description.trim(),
                contactPerson = contactPerson.trim().ifBlank { "Client Contact" },
                status = TicketStatus.OPEN,
                createdAtFormatted = nowFormatted
            )

            val result = repository.createTicket(ticket, appMode.value == AppMode.PRODUCTION_CLOUD)
            _isSubmittingTicket.value = false

            result.onSuccess {
                _snackbarEvent.emit("Support ticket $refCode created successfully.")
                onSuccess()
            }.onFailure { err ->
                _snackbarEvent.emit(err.message ?: "Failed to open support ticket.")
            }
        }
    }

    fun addTicketReply(ticketId: String, senderName: String, message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            repository.addTicketReply(
                ticketId = ticketId,
                senderName = senderName.ifBlank { "Client" },
                isClient = true,
                message = message
            )
            _snackbarEvent.emit("Reply posted.")
        }
    }

    fun resetDemoData(onCompleted: () -> Unit) {
        viewModelScope.launch {
            repository.resetDemoData()
            _quoteFormState.value = QuoteFormState()
            _snackbarEvent.emit("Demo sample data restored successfully.")
            onCompleted()
        }
    }

    fun getProjectMilestones(projectId: String): StateFlow<List<ProjectMilestone>> {
        return repository.getProjectMilestones(projectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getTicketReplies(ticketId: String): StateFlow<List<TicketReply>> {
        return repository.getTicketReplies(ticketId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}

class MainViewModelFactory(
    private val repository: VoltaviaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
