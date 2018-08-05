package spark.analyze.spark.analyze.twitter;


public class Main {

	public static void main(String[]args) {
		Analyzer consumer=new Analyzer();
		try {
			consumer.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
