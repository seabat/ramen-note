import ComposeApp
import Foundation

class IosLocalStorageDataSource: LocalStorageDataSourceContract {
    
    func __load(name: String) async throws -> KotlinByteArray? {
        do {
            let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            guard let documentsPath = documentsPath else {
                return nil
            }
            
            let fileName = "\(name).png"
            let fileURL = documentsPath.appendingPathComponent(fileName)
            
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

    func __save(imageBytes: KotlinByteArray, name: String) async throws {
        do {
            let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            guard let documentsPath = documentsPath else {
                return
            }
            
            let fileName = "\(name).png"
            let fileURL = documentsPath.appendingPathComponent(fileName)
            
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

    func __rename(oldName: String, newName: String) async throws {
        do {
            let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            guard let documentsPath = documentsPath else {
                return
            }
            
            let oldFileName = "\(oldName).png"
            let newFileName = "\(newName).png"
            let oldFileURL = documentsPath.appendingPathComponent(oldFileName)
            let newFileURL = documentsPath.appendingPathComponent(newFileName)
            
            if FileManager.default.fileExists(atPath: oldFileURL.path) {
                try FileManager.default.moveItem(at: oldFileURL, to: newFileURL)
            }
        } catch {
            // Handle error silently or log it
        }
    }

    func __delete(name: String) async throws {
        do {
            let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            guard let documentsPath = documentsPath else {
                return
            }
            
            let fileName = "\(name).png"
            let fileURL = documentsPath.appendingPathComponent(fileName)
            
            if FileManager.default.fileExists(atPath: fileURL.path) {
                try FileManager.default.removeItem(at: fileURL)
            }
        } catch {
            // Handle error silently or log it
        }
    }
}

