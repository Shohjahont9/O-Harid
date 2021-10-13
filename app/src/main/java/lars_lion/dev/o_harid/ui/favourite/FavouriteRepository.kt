package lars_lion.dev.o_harid.ui.favourite

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject


@ViewModelScoped
class FavouriteRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {

    suspend fun favouriteBook() = flow {
        emit(apiService.favouriteBook("Bearer ${prefs.token}"))
    }.flowOn(Dispatchers.IO)

    suspend fun deleteBook(bookId: String) = flow {
        emit(apiService.deleteBookFromLibrary("Bearer ${prefs.token}", bookId))
    }
}