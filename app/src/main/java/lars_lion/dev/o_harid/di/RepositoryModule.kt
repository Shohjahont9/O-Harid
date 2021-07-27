package lars_lion.dev.o_harid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import lars_lion.dev.o_harid.network.ApiService
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.main.MainRepository
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


}

