package nl.utwente.bigdata;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;

public class WordCountTest {

  private MapDriver<Writable, Text, Text, IntWritable> mapDriver;
  private ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
  
  @Before
  public void setUp() {
    WordCount.MyMapper mapper = new WordCount.MyMapper();
    WordCount.MyReducer reducer = new WordCount.MyReducer();
  
    mapDriver = MapDriver.newMapDriver(mapper);
    reduceDriver = ReduceDriver.newReduceDriver(reducer);
  }
  
  @Test
  public void testMapper() throws IOException {
    mapDriver.addInput(NullWritable.get(), new Text("hello"));
    
    mapDriver.withOutput(new Text("hello"), new IntWritable(1));
    
    mapDriver.runTest();
  }

  @Test
  public void testReduce() throws IOException {
    
    reduceDriver.withInput(new Text("hello"), Lists.newArrayList(new IntWritable(1), new IntWritable(2), new IntWritable(3)));
        
    reduceDriver.withOutput(new Text("hello"), new IntWritable(6));
		
    reduceDriver.runTest();
  }
}
