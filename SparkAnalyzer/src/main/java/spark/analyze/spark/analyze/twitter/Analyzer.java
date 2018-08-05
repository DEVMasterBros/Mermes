package spark.analyze.spark.analyze.twitter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import java.util.HashMap;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import scala.Tuple2;

public class Analyzer {
	public void start() throws InterruptedException {

		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("KafkaTest");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.seconds(2));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		params.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		params.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		params.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-2");
		params.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		// params.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000);

		Function<String, Boolean> function = k -> (k != "");
		List<String> topics = Arrays.asList("baro4");

		JavaInputDStream<ConsumerRecord<String, String>> ds = KafkaUtils.createDirectStream(ssc,
				LocationStrategies.PreferConsistent(), ConsumerStrategies.<String, String>Subscribe(topics, params));

		ds.flatMap((ConsumerRecord<String, String> record) -> Arrays.asList(record.value().split("[^ 가-힣]|(\\s)+"))
				.iterator()).filter(function).mapToPair((String word) -> new Tuple2<String, Integer>(word, 1))
				.reduceByKey((Integer v1, Integer v2) -> v1 + v2).saveAsHadoopFiles("", "");

		/*ds.flatMap((ConsumerRecord<String, String> record) -> Arrays.asList(record.value().split("[^ 가-힣]|(\\s)+"))
				.iterator()).filter(function).mapToPair((String word) -> new Tuple2<String, Integer>(word, 1))
				.reduceByKey((Integer v1, Integer v2) -> v1 + v2).print();*/
		

		ssc.start();
		ssc.awaitTermination();
	}
}
