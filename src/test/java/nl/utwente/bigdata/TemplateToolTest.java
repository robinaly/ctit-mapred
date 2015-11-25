package nl.utwente.bigdata;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TemplateToolTest {
	// has to match the definitions in TemplateTool.java
	private MapDriver<Writable, Text, Text, IntWritable> mapDriver;
	private ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;

	/*
	 * Setup the 
	 */
	@Before
	public void setUp() {
		TemplateTool.MyMapper mapper = new TemplateTool.MyMapper();
		TemplateTool.MyReducer reducer = new TemplateTool.MyReducer();

		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
	}

	@Test
	public void testMapper() throws IOException {
		mapDriver.addInput(NullWritable.get(),
				new Text("{\"text\": \"hello\"}"));
		mapDriver.runTest();
	}

	@Test
	public void testReduce() throws IOException {

		reduceDriver.withInput(new Text("hello"), Lists.newArrayList(
				new IntWritable(1), new IntWritable(2), new IntWritable(3)));

		reduceDriver.runTest();
	}
}
