package cn.edu.gzu.fjbai;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

//import cn.edu.gzu.fjbai.Test.Counter;

public class MapReduceExample {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, FloatWritable> {

		// private final static IntWritable one = new IntWritable(1);
		// private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			try {
				String[] lineSplit = line.split(",");
				String date = lineSplit[1];
				// String distributor = lineSplit[2];
				// String goodsnum = lineSplit[3];
				// String number = lineSplit[4];
				String price = lineSplit[5];
				// String weather = lineSplit[6];
				// String address = lineSplit[7];
				context.write(new Text(date), new FloatWritable(Float.parseFloat(price)));
			} catch (ArrayIndexOutOfBoundsException e) {
				// context.getCounter(Counter.LINESKIP).increment(1);
				return;
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, FloatWritable, Text, FloatWritable> {
		// private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<FloatWritable> values, Context context)
				throws IOException, InterruptedException {
			float totalPrice = 0.0f;
			for (FloatWritable value : values) {
				totalPrice += value.get();
			}
			context.write(key, new FloatWritable(totalPrice));
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "suyulong");
		job.setJarByClass(MapReduceExample.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}