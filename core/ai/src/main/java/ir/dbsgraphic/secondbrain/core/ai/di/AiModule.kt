package ir.dbsgraphic.secondbrain.core.ai.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dbsgraphic.secondbrain.core.ai.AIProvider
import ir.dbsgraphic.secondbrain.core.ai.DefaultAIProvider
import ir.dbsgraphic.secondbrain.core.ai.OpenAiClient
import javax.inject.Singleton

/**
 * Binds the single [AIProvider]. DefaultAIProvider is settings-driven and acts
 * as a no-op whenever AI is disabled/unconfigured, so the rest of the app can
 * depend on AIProvider unconditionally (engineering spine).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiProvider(impl: DefaultAIProvider): AIProvider

    companion object {
        @Provides
        @Singleton
        fun provideOpenAiClient(): OpenAiClient = OpenAiClient()
    }
}
