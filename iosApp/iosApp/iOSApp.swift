import ComposeApp
import SwiftUI
import FirebaseCore
import FirebaseAppCheck

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase

    init() {
        // Firebase App Check の初期化（FirebaseApp.configure() より前に設定）
        #if DEBUG
        let providerFactory = AppCheckDebugProviderFactory()
        #else
        let providerFactory = DeviceCheckProviderFactory()
        #endif
        AppCheck.setAppCheckProviderFactory(providerFactory)

        FirebaseApp.configure()

        KoinHelperKt.doInitKoin(
            onKoinStart: {
                IosModuleKt.createSwiftLibDependencyModule(
                    factory: SwiftLibDependencyFactory.shared
                )
            }
        )
    }

    var body: some Scene {
        WindowGroup {
            ZStack {
                ContentView()

                // バックグラウンド遷移時にスプラッシュオーバーレイを表示
                if scenePhase != .active {
                    ZStack {
                        Color(red: 1.000, green: 0.973, blue: 0.965)
                            .ignoresSafeArea()
                        Image("SplashIcon")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 120, height: 120)
                    }
                }
            }
        }
    }
}
