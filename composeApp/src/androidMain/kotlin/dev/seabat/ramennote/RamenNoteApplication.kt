package dev.seabat.ramennote

import android.app.Application
import dev.seabat.ramennote.data.di.databaseModule
import dev.seabat.ramennote.data.di.factoryModule
import dev.seabat.ramennote.data.di.repositoryModule
import dev.seabat.ramennote.ui.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RamenNoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RamenNoteApplication)
            modules(
                viewModelModule,
                repositoryModule,
                databaseModule,
               factoryModule
            )
        }
    }
}