import ComposeApp
import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
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
            ContentView()
        }
    }
}
