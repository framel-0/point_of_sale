package net.sipconsult.pos.ui.category

import androidx.lifecycle.ViewModel
import net.sipconsult.pos.data.repository.CategoryRepository
import net.sipconsult.pos.internal.lazyDeferred

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories by lazyDeferred {
        categoryRepository.getCategories()
    }
}
