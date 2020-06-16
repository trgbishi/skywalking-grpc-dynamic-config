package org.apache.skywalking.oap.server.configuration.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

public class ConfigServer {
    private static final Logger logger = Logger.getLogger(ConfigServer.class.getName());


    private int port = 50051;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new ConfigServer.GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on "+ port);

        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run(){

                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                ConfigServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop(){
        if (server != null){
            server.shutdown();
        }
    }

    // block 一直到退出程序
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
    }


    public  static  void main(String[] args) throws IOException, InterruptedException {

        final ConfigServer server = new ConfigServer();
        server.start();
        server.blockUntilShutdown();
    }


    // 实现 定义一个实现服务接口的类
    private class GreeterImpl extends ConfigurationServiceGrpc.ConfigurationServiceImplBase {

        @Override
        public void call(ConfigurationRequest req, StreamObserver<ConfigurationResponse> responseObserver){
            String name = "alarm.default.alarm-settings";


            //测试同一个指标 同一个服务，不同阈值的冲突情况。
            //测试结果：会采用第一条规则
            String value = "rules:\n" +
                    "  service_resp_time3_rule:\n" +
                    "    metrics-name: service_resp_time\n" +
                    "    include-names: \n" +
                    "        - 36-dc-job-test\n" +
                    "    op: \">\"\n" +
                    "    threshold: 5\n" +
                    "    period: 10\n" +
                    "    count: 1\n" +
                    "    silence-period: 5\n" +
                    "    message: dynamic alarm test3.\n" +
                    "  service_resp_time4_rule:\n" +
                    "    metrics-name: service_resp_time\n" +
                    "    include-names: \n" +
                    "        - 36-dc-job-test\n" +
                    "    op: \">\"\n" +
                    "    threshold: 4\n" +
                    "    period: 10\n" +
                    "    count: 1\n" +
                    "    silence-period: 5\n" +
                    "    message: dynamic alarm test4.";
            System.out.println("cluster: "+req.getClusterName());
            System.out.println("uuid: "+req.getUuid());

            //TODO 根据收到的req，从数据库抓取最新数据并返回

            Config config = new Config(name,value);
            ConfigurationResponse resp = ConfigurationResponse.newBuilder().setUuid("1").setConfigTable(0,config).build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}
