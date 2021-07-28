package lars_lion.dev.o_harid.ui.book_detail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import javax.inject.Inject

class BookDetailRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: PreferencesManager
) {
    suspend fun comments() = flow {
        emit(apiService.comments("Bearer ${prefs.token}"))
    }.flowOn(Dispatchers.IO)

    suspend fun bookDetail(id:Int) = flow {
        emit(apiService.bookDetails("Bearer ${prefs.token}", id))
    }.flowOn(Dispatchers.IO)


}