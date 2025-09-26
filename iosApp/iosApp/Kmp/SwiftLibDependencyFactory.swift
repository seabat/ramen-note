import ComposeApp

class SwiftLibDependencyFactory: SwiftLibDependencyFactoryContract {

    static var shared = SwiftLibDependencyFactory()

    func provideLocalStorageDataSourceContract() -> any LocalStorageDataSourceContract {
        return IosLocalStorageDataSource()
    }
}

