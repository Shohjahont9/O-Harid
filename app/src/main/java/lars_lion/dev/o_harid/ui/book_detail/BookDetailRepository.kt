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
    suspend fun comments(bookId:String) = flow {
        emit(apiService.comments("Bearer ${prefs.token}", bookId))
    }.flowOn(Dispatchers.IO)

    suspend fun bookDetail(id:Int) = flow {
        emit(apiService.bookDetails("Bearer ${prefs.token}", id))
    }.flowOn(Dispatchers.IO)

    suspend fun addFavouriteBook(id:Int) = flow {
        emit(apiService.addFavouriteBook("Bearer ${prefs.token}", id))
    }.flowOn(Dispatchers.IO)

 suspend fun buyBook(id:Int) = flow {
        emit(apiService.buyBook("Bearer ${prefs.token}", id))
    }.flowOn(Dispatchers.IO)


 suspend fun addComment(text:String, id:String, evaluate:String) = flow {
        emit(apiService.addComment("Bearer ${prefs.token}", text, id, evaluate))
    }.flowOn(Dispatchers.IO)


}