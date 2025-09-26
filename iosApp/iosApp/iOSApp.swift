import ComposeApp
import SwiftUI

@main
struct iOSApp: App {

    init() {
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