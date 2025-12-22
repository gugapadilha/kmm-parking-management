package com.example.gestodeestacionamento.di

import com.example.gestodeestacionamento.data.remote.ApiService
import com.example.gestodeestacionamento.data.remote.ApiServiceImpl
import com.example.gestodeestacionamento.data.repository.*
import com.example.gestodeestacionamento.domain.repository.*
import com.example.gestodeestacionamento.domain.usecase.CalculateParkingFeeUseCase
import com.example.gestodeestacionamento.platform.*
import com.example.gestodeestacionamento.presentation.viewmodel.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Platform Storage
    single { PlatformStorage() }
    
    // Database Factory
    single { DatabaseFactory() }
    
    // Database
    single {
        get<DatabaseFactory>().createDatabase()
    }

    // DAOs
    single { get<AppDatabase>().vehicleDao() }
    single { get<AppDatabase>().priceTableDao() }
    single { get<AppDatabase>().paymentMethodDao() }
    single { get<AppDatabase>().paymentDao() }

    // API Service
    single<ApiService> { ApiServiceImpl() }

    // Repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            apiService = get(),
            storage = get()
        )
    }

    single<VehicleRepository> {
        VehicleRepositoryImpl(
            database = get(),
            priceTableDao = get()
        )
    }

    single<PriceTableRepository> {
        PriceTableRepositoryImpl(
            database = get()
        )
    }

    single<PaymentRepository> {
        PaymentRepositoryImpl(
            database = get()
        )
    }

    single<SyncRepository> {
        SyncRepositoryImpl(
            apiService = get()
        )
    }

    // Use Cases
    single { CalculateParkingFeeUseCase() }

    // ViewModels
    viewModel { LoginViewModel(authRepository = get()) }
    viewModel {
        HomeViewModel(
            vehicleRepository = get(),
            paymentRepository = get(),
            priceTableRepository = get(),
            syncRepository = get(),
            authRepository = get()
        )
    }
    viewModel {
        VehicleEntryViewModel(
            vehicleRepository = get(),
            priceTableRepository = get()
        )
    }
    viewModel { VehicleListViewModel(vehicleRepository = get()) }
    viewModel {
        VehicleDetailViewModel(
            vehicleRepository = get(),
            priceTableRepository = get(),
            paymentRepository = get(),
            calculateParkingFeeUseCase = get()
        )
    }
}

