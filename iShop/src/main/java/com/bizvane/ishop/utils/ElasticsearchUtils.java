//package com.bizvane.ishop.utils;
//
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Created by tgg on 16-3-17.
// */
//public class ElasticsearchUtils {
//
//    private Settings setting;
//
//    private Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();
//
//    private Map<String, Integer> ips = new HashMap<String, Integer>(); // hostname port
//
//    private String clusterName = "elasticsearch";
//
//    private ElasticsearchUtils() {
//        init();
//        //TO-DO 添加你需要的client到helper
//    }
//
//    public static final ElasticsearchUtils getInstance() {
//        return ClientHolder.INSTANCE;
//    }
//
//    private static class ClientHolder {
//        private static final ElasticsearchUtils INSTANCE = new ElasticsearchUtils();
//    }
//
//    /**
//     * 初始化默认的client
//     */
//    public void init() {
//        ips.put("http://172.16.200.157", 9300);
//        setting = ImmutableSettings
//                .settingsBuilder()
//                .put("client.transport.sniff",true)
//                .put("cluster.name","elasticsearch").build();
//        addClient(setting, getAllAddress(ips));
//    }
//
//    /**
//     * 获得所有的地址端口
//     *
//     * @return
//     */
//    public List<InetSocketTransportAddress> getAllAddress(Map<String, Integer> ips) {
//        List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();
//        for (String ip : ips.keySet()) {
//            addressList.add(new InetSocketTransportAddress(ip, ips.get(ip)));
//        }
//        return addressList;
//    }
//
//    public Client getClient() {
//        return getClient(clusterName);
//    }
//
//    public Client getClient(String clusterName) {
//        return clientMap.get(clusterName);
//    }
//
//    public void addClient(Settings setting, List<InetSocketTransportAddress> transportAddress) {
//        Client client = new TransportClient(setting)
//                .addTransportAddresses(transportAddress
//                        .toArray(new InetSocketTransportAddress[transportAddress.size()]));
//        clientMap.put(setting.get("cluster.name"), client);
//    }
//}