import ComposeApp
import Foundation
import UIKit

class IosNoImageDataSource: NoImageDataSourceContract {
    func create() -> KotlinByteArray {
        guard let image = UIImage(named: "noimage") else {
            // 画像が見つからない場合は空のByteArrayを返す
            return KotlinByteArray(size: 0)
        }
        
        guard let imageData = image.pngData() else {
            // PNGデータの変換に失敗した場合は空のByteArrayを返す
            return KotlinByteArray(size: 0)
        }
        
        // NSDataをKotlinByteArrayに変換
        let byteArray = KotlinByteArray(size: Int32(imageData.count))
        imageData.withUnsafeBytes { bytes in
            for i in 0..<imageData.count {
                byteArray.set(index: Int32(i), value: bytes[i])
            }
        }
        
        return byteArray
    }
}

