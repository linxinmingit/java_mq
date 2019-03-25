package kafka;

import kafka.admin.AdminUtils;
import kafka.admin.BrokerMetadata;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import scala.collection.Map;
import scala.collection.Seq;

import java.util.Properties;

/**
 * 主题管理： 实例化 ZkUtils 对象 创建主题：AdminUtils.createTopic()创建主题
 *
 * @author Administrator
 *
 */
public class MyTopic {

    // 系统参数定义
    // ZK 连接位置 可以多个
    // private final static String ZK_CONNECT = "192.168.246.100:2181,192.168.246.101:2181";
    private final static String ZK_CONNECT = "192.168.1.8:2181,192.168.1.9:2181,192.168.1.10:2181";
    // session 过期时间
    private final static int SESSION_TIMEOUT = 30000;
    // 连接超时时间
    private final static int CONNECT_TIMEOUT = 30000;


    // 入口函数
    public static void main(String[] args) {
        System.out.println("MyTopic.main()");
        Properties properties = new Properties();
        // 新建主题
        createTopic("HeSui", 3, 3, properties );
        // 修改主题
       // modifyTopicConfig("liuwen",properties);
        // 删除主题
        //delTopic("liuwen");
        // 调整分区
        // modifyPartition("liuwen",10,2);
        // 增加分区  因为只有一台机器，这个就不演示了
        // addPartition("liuwen",2,"1:1,2:2");
    }
    // 创建主题
    /**
     *
     * @param topic 主题名称
     * @param partition 分区数量
     * @param repilca 副本数量
     * @param properties 配置参数
     * 默认的创建主题的脚本为：
     * kafka-topics.sh --create --zookeeper server-1:2181,server-2:2181,server-3:2181 --replication-factor 2 --partition 3 --topic kafka-action
     */
    public static void createTopic(String topic,int partition, int repilca,Properties properties) {
        // 初始化 ZkUtils 工具对象，方便主题管理
        ZkUtils zkUtils = null;
        try {
            // 尝试创建主题
            // 实例化 主题  按照 当前配置的参数
            zkUtils = ZkUtils.apply(ZK_CONNECT, SESSION_TIMEOUT,CONNECT_TIMEOUT,JaasUtils.isZkSecurityEnabled());
            // AdminUtils 管理工具  如果主题不存在
            if(!AdminUtils.topicExists(zkUtils, topic)) {
                // 不存在主题 具体进行创建
                AdminUtils.createTopic(zkUtils, topic, partition, repilca, properties, AdminUtils.createTopic$default$6());
            }else {
                // 存在了 怎么着
                System.out.println("Topic: " + topic + " is all exist! ");
            }
        } catch (Exception e) {
            // TODO: 异常错误信息处理
            e.printStackTrace();
        } finally {
            // TODO: 无论怎么着都执行
            zkUtils.close();
        }

    }
    // 修改主题 级别配置
    /**
     * 根据主题名字，以及参数进行修改。
     * @param topic
     * @param properties
     */
    public static void modifyTopicConfig(String topic,Properties properties) {
        ZkUtils zkUtils = null;
        try {
            // 实例化 ZkUtils
            zkUtils = ZkUtils.apply(ZK_CONNECT,SESSION_TIMEOUT,CONNECT_TIMEOUT,JaasUtils.isZkSecurityEnabled());
            // 首先获取当前已有的配置，这里是查询主题级别的配置，因此指定配置类型为Topic
            Properties curProp = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(),topic);
            // 融合最新的主题配置
            curProp.putAll(properties);
            // 替换当前 主题配置执行信息
            AdminUtils.changeTopicConfig(zkUtils, topic, curProp);
        } catch (Exception e) {
            // TODO: 异常错误信息处理
            e.printStackTrace();
        } finally {
            // TODO: 无论怎么着都执行
            zkUtils.close();
        }
    }

    // 删除主题
    /**
     *
     * @param topic
     */
    public static void delTopic(String topic) {
        ZkUtils zkUtils = null;
        try {
            // 实例化 ZkUtils 关键是 连接地址
            zkUtils = ZkUtils.apply(ZK_CONNECT, SESSION_TIMEOUT,CONNECT_TIMEOUT,JaasUtils.isZkSecurityEnabled());
            // 执行删除指定主题动作
            AdminUtils.deleteTopic(zkUtils, topic);
        } catch (Exception e) {
            // TODO: 异常错误信息处理
            e.printStackTrace();
        } finally {
            // TODO: 无论怎么着都执行
            zkUtils.close();
        }
    }
    // 增加分区
    /**
     *
     * @param topic 主题名称
     * @param partition 分区总数，这个直接是最终的数量
     * @param partitions 副本分配方案 格式为 "2:1,3:1" 这个表示的是2个分区分别对应的副本情况，0分区对应brokerid为2,1的，1分区对应的副本ID为 3,1
     * 不同的分区的副本用逗号分隔，同一个分区的多个副本之间用冒号分隔
     * 同时需要注意的是，副本分配方案要包括已有分区的副本分配信息，根据分配顺序从左到右依次与分区对应，分区编号递增
     */
    public static void addPartition(String topic,int partition,String partitions) {
        ZkUtils zkUtils = null;
        try {
            // 实例化 ZkUtils 关键是 连接地址
            zkUtils = ZkUtils.apply(ZK_CONNECT, SESSION_TIMEOUT,CONNECT_TIMEOUT,JaasUtils.isZkSecurityEnabled());
            // 执行删除指定主题动作
            AdminUtils.addPartitions(zkUtils, topic, partition, partitions, true, AdminUtils.addPartitions$default$6());
        } catch (Exception e) {
            // TODO: 异常错误信息处理
            e.printStackTrace();
        } finally {
            // TODO: 无论怎么着都执行
            zkUtils.close();
        }
    }

    // 分区副本重新配置
    /**
     *
     * @param topic  主题
     * @param partition 分区数
     * @param repilca 副本数
     * 通过修改，达成重新分配的目的
     * 步骤：
     * 1 实例化 ZkUtils
     * 2 获取代理元数据 (BrokerMetadata) 信息
     * 3 生成分区副本分配方案，当然也可以自定义分配方案
     * 4 调用 createOrUpdateTopicPartitionAssignmentPathInZK()方法完成副本分配
     * 5 释放与 zookeeper的连接
     */												    //分区			//备份数	  //备份数不能大于节点数(brokers)
    public static void modifyPartition(String topic,int partition, int repilca) {
        ZkUtils zkUtils = null;
        try {
            // 1 实例化 ZkUtils
            zkUtils = ZkUtils.apply(ZK_CONNECT, SESSION_TIMEOUT,CONNECT_TIMEOUT, JaasUtils.isZkSecurityEnabled());
            // 2 获取代理元数据 (BrokerMetadata) 信息
            Seq<BrokerMetadata> brokerMetadata = AdminUtils.getBrokerMetadatas(zkUtils, AdminUtils.getBrokerMetadatas$default$2(), AdminUtils.getBrokerMetadatas$default$3());
            // 3 生成分区副本分配方案，当然也可以自定义分配方案
            Map<Object, Seq<Object>> replicaAssign = AdminUtils.assignReplicasToBrokers(brokerMetadata, partition, repilca, AdminUtils.assignReplicasToBrokers$default$4(), AdminUtils.assignReplicasToBrokers$default$5());
            // 4 调用 createOrUpdateTopicPartitionAssignmentPathInZK()方法完成副本分配
            AdminUtils.createOrUpdateTopicPartitionAssignmentPathInZK(zkUtils, topic, replicaAssign, null, true);
        } catch (Exception e) {
            // TODO: 异常错误信息处理
            e.printStackTrace();
        } finally {
            // TODO: 无论怎么着都执行
            // 5 释放与 zookeeper的连接
            zkUtils.close();
        }
    }
}
