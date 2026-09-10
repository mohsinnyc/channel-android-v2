package com.channel.feature.onboarding.di

import com.channel.feature.onboarding.data.OnboardingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object OnboardingNetworkModule {

    @Provides
    @Singleton
    fun provideOnboardingService(retrofit: Retrofit): OnboardingService = retrofit.create(OnboardingService::class.java)
}
