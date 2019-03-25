package cn.yr.partitions.Thread;
import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * @Date Nov 3, 2016
 *
 * @Author lin
 *
 * @Note 先 Hash 再取模，得到分区索引
 */
public class CustomerPartitioner implements Partitioner {

    public CustomerPartitioner(VerifiableProperties props) {
    }

    public int partition(Object key, int numPartitions) {
    	int partition =0;
        String stringKey = (String) key;
        int offset = stringKey.lastIndexOf('.');
        if (offset > 0) {
        	partition = Math.abs(Integer.parseInt(stringKey.substring(offset + 1))) % numPartitions;
           // partition = Integer.parseInt(stringKey.substring(offset + 1)) % numPartitions;
        }
        /*System.out.println("指定分区[Partitions = " + partition + "] , [key = "+ key + " ]");
        return partition; */
        if(partition%2==0){
        	System.out.println("指定分区[Partitions = " + partition + "] , [key = "+ key + " ]");
            return 1;
        }else{
        	System.out.println("指定分区[Partitions = " + partition + "] , [key = "+ key + " ]");
            return 2;
        }
    }
}