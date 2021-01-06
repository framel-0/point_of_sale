package net.sipconsult.pos.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sipconsult.pos.data.repository.salesAgent.SalesAgentRepository

class HomeViewModelFactory(
    private val salesAgentRepository: SalesAgentRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(salesAgentRepository) as T
    }
}