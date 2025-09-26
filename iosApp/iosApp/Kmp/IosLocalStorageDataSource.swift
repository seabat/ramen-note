import ComposeApp
import Foundation

class IosLocalStorageDataSource: LocalStorageDataSourceContract {
    
    private let imageFileName = "area_image.png"
    
    func __load() async throws -> KotlinByteArray? {
        do {
            let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            guard let documentsPath = documentsPath else {
                return nil
            }
            
            let fileURL = documentsPath.appendingPathComponent(imageFileName)
            
            // Check if file exists
            guard FileManager.default.fileExists(atPath: fileURL.path) else {
                return nil
            }
            
            let data = try Data(contentsOf: fileURL)
            return KotlinByteArray(size: Int32(data.count)) { index in
                KotlinByte(value: Int8(bitPattern: data[Int(index)]))
            }
        } catch {
            return nil
        }
    }

    func __save(imageBytes: KotlinByteArray) async throws {
        do {
            let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            guard let documentsPath = documentsPath else {
                return
            }
            
            let fileURL = documentsPath.appendingPathComponent(imageFileName)
            
            // Convert KotlinByteArray to Data
            var data = Data()
            for i in 0..<Int(imageBytes.size) {
                let byte = imageBytes.get(index: Int32(i))
                data.append(UInt8(bitPattern: byte))
            }
            
            try data.write(to: fileURL)
        } catch {
            // Handle error silently or log it
        }
    }
}

