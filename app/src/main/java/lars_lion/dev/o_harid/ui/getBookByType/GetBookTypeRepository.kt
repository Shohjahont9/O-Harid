package lars_lion.dev.o_harid.ui.getBookByType

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class GetBookTypeRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {

    suspend fun getBookType(typeId: String) = flow {
        emit(apiService.getBookByBookType("Bearer ${prefs.token}", typeId))
    }.flowOn(Dispatchers.IO)

    suspend fun addFavouriteBook(id: Int) = flow {
        emit(apiService.addFavouriteBook("Bearer ${prefs.token}", id))
    }.flowOn(Dispatchers.IO)


}