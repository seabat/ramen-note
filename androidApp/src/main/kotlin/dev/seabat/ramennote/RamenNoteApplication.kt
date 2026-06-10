package dev.seabat.ramennote

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import dev.seabat.ramennote.data.di.dataSourceModule
import dev.seabat.ramennote.data.di.databaseModule
import dev.seabat.ramennote.data.di.factoryModule
import dev.seabat.ramennote.data.di.repositoryModule
import dev.seabat.ramennote.domain.di.useCaseModule
import dev.seabat.ramennote.ui.di.uiModule
import dev.seabat.ramennote.ui.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RamenNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase App Check の初期化
        // デバッグビルド: Debug プロバイダー / リリースビルド: Play Integrity プロバイダー
        Firebase.initialize(this)
        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )

        startKoin {
            androidContext(this@RamenNoteApplication)
            modules(
                uiModule,
                viewModelModule,
                repositoryModule,
                databaseModule,
                factoryModule,
                useCaseModule,
                dataSourceModule
            )
        }
    }
}
