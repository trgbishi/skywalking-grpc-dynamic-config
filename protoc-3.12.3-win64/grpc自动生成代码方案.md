https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.9.1/protoc-gen-grpc-java-1.9.1-windows-x86_64.exe
https://github.com/protocolbuffers/protobuf/releases/download/v3.12.3/protoc-3.12.3-win64.zip

### 自动生成Service文件
protoc解压自protoc-3.12.3-win64.zip的bin目录下，第一个test为.proto文件所在路径，java_out的路径为生产输出路径
protoc -I=test --java_out=test helloworld.proto


### 自动生成xxx.Grpc
proto_path为.proto的路径，java_out为输出路径
protoc --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java-1.9.1-windows-x86_64.exe --grpc-java_out=test --proto_path=test test/helloworld.proto
protoc --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java-1.26.0-windows-x86_64.exe --grpc-java_out=test --proto_path=test test/helloworld.proto

