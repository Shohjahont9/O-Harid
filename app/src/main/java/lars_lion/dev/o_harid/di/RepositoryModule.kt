package lars_lion.dev.o_harid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.book_detail.BookDetailRepository
import lars_lion.dev.o_harid.ui.card.CardRepository
import lars_lion.dev.o_harid.ui.card.verifyCard.VerifyCardRepository
import lars_lion.dev.o_harid.ui.favourite.FavouriteRepository
import lars_lion.dev.o_harid.ui.main.MainRepository
import lars_lion.dev.o_harid.ui.profile.ProfileRepository
import lars_lion.dev.o_harid.ui.registration.login.LoginRepository
import lars_lion.dev.o_harid.ui.registration.reg.RegisstrationRepository


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideRegisterRepository(
        apiService: ApiService
    ): RegisstrationRepository = RegisstrationRepository(apiService)


    @Provides
    @ViewModelScoped
    fun provideLoginRepository(
        apiService: ApiService
    ): LoginRepository = LoginRepository(apiService)


    @Provides
    @ViewModelScoped
    fun provideMainRepository(
        apiService: ApiService,
    ): MainRepository = MainRepository(apiService)


    @Provides
    @ViewModelScoped
    fun provideBookDetailRepository(
        apiService: ApiService,
        prefs: PreferencesManager
    ): BookDetailRepository = BookDetailRepository(apiService, prefs)


    @Provides
    @ViewModelScoped
    fun provideFavouriteRepository(
        apiService: ApiService,
        prefs: PreferencesManager
    ): FavouriteRepository = FavouriteRepository(apiService, prefs)


    @Provides
    @ViewModelScoped
    fun provideCardRepository(
        apiService: ApiService,
        prefs: PreferencesManager
    ): CardRepository = CardRepository(apiService, prefs)

    @Provides
    @ViewModelScoped
    fun provideVerifyCardRepository(
        apiService: ApiService,
        prefs: PreferencesManager
    ): VerifyCardRepository = VerifyCardRepository(apiService, prefs)


    @Provides
    @ViewModelScoped
    fun provideProfileRepository(
        apiService: ApiService,
        prefs: PreferencesManager
    ): ProfileRepository = ProfileRepository(apiService, prefs)


}

