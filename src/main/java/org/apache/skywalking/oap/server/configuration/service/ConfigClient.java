package org.apache.skywalking.oap.server.configuration.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigClient {
    private final ManagedChannel channel;
    private final ConfigurationServiceGrpc.ConfigurationServiceBlockingStub blockingStub;
    private static final Logger logger = Logger.getLogger(ConfigClient.class.getName());

    public ConfigClient(String host, int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();

        blockingStub = ConfigurationServiceGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public  void greet(String clusterName,String uuid){
        ConfigurationRequest request = ConfigurationRequest.newBuilder().setClusterName(clusterName).setUuid(uuid).build();
        ConfigurationResponse response;
        try{
            response = blockingStub.call(request);
        } catch (StatusRuntimeException e)
        {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        response.getConfigTableList().forEach(config->{
            System.out.println(config.toString());
        });
        System.out.println(response.getUuid());

    }

    public static void main(String[] args) throws InterruptedException {
        ConfigClient client = new ConfigClient("127.0.0.1",50051);
        String clusterName = "cluster_1";
        String uuid = "";
        try{
            client.greet(clusterName,uuid);
        }finally {
            client.shutdown();
        }
    }
}
