import sharedUI

class SwiftLibDependencyFactory: SwiftLibDependencyFactoryContract {

    static var shared = SwiftLibDependencyFactory()

    func provideLocalStorageDataSourceContract() -> any LocalStorageDataSourceContract {
        return IosLocalStorageDataSource()
    }
    
    func provideNoImageDataSourceContract() -> any NoImageDataSourceContract {
        return IosNoImageDataSource()
    }

    func provideShopAiDataSourceContract() -> any ShopAiDataSourceContract {
        return IosShopAiDataSource()
    }
}

