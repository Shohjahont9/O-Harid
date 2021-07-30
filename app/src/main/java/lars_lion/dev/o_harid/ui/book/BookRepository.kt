package lars_lion.dev.o_harid.ui.book

import kotlinx.coroutines.flow.flow
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {
    suspend fun paidBooks() = flow {
        emit(apiService.paidBooks("Bearer ${prefs.token}"))
    }
}