import ComposeApp
import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase

    init() {
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
                        Color(red: 0.949, green: 0.949, blue: 0.949)
                            .ignoresSafeArea()
                        Image("Logo")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 120, height: 120)
                    }
                }
            }
        }
    }
}
