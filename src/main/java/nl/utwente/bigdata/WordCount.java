/*
 * Cloud9: A MapReduce Library for Hadoop
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package nl.utwente.bigdata;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
  
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;


import com.google.common.collect.Maps;

import edu.umd.cloud9.collection.wikipedia.WikipediaPage;

/**
 * Tool for building the mapping between Wikipedia internal ids (docids) and sequentially-numbered
 * ints (docnos).
 *
 */
public class WordCount extends Configured implements Tool {
  private static final Logger LOG = Logger.getLogger(WordCount.class);

  public static class MyMapper extends Mapper<Writable, Text, Text, IntWritable> {

    @Override
    public void setup(Context context) {
      
    }
  
    @Override
    public void map(Writable key, Text text, Context context)
        throws IOException, InterruptedException {
        Pattern wordPat = Pattern.compile("\\w+");
        Matcher m = wordPat.matcher(text.toString());
        while (m.find()) {
          context.write(new Text(m.group()), new IntWritable(1));
        }
    }
    
  }
  
  public static class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
          int sum = 0;
          for (IntWritable val: values) {
            sum += val.get();
          }
          context.write(key, new IntWritable(sum));
    }
  }

  public void run(String inputPath, String tmpPath) throws Exception {

    Job job = Job.getInstance(getConf());
    job.setJarByClass(WordCount.class);
    job.setJobName(String.format("%s-task1[%s, %s]", 
        this.getClass().getName(), 
        inputPath, 
        tmpPath));
    //job.setNumReduceTasks(1);

    job.setInputFormatClass(SequenceFileInputFormat.class);
    FileInputFormat.setInputPaths(job, new Path(inputPath));
    
    FileOutputFormat.setCompressOutput(job, false);

    job.setMapperClass(MyMapper.class); 
    job.setReducerClass(MyReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    
    job.setOutputFormatClass(SequenceFileOutputFormat.class);
    FileOutputFormat.setOutputPath(job, new Path(tmpPath));
    
    job.waitForCompletion(true);
    return;
  }
  
  
  @SuppressWarnings("static-access")
  @Override
  public int run(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path")
        .hasArg().withDescription("Input").create("input"));
    options.addOption(OptionBuilder.withArgName("path")
        .hasArg().withDescription("Output").create("output"));

    CommandLine cmdline;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      return -1;
    }

    String inputPath = cmdline.getOptionValue("input", System.getenv("input"));
    String outputPath = cmdline.getOptionValue("output", System.getenv("output"));
    
    if (inputPath == null || outputPath == null) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(this.getClass().getName(), options);
      ToolRunner.printGenericCommandUsage(System.out);
      return -1;
    }
    
    LOG.info("Tool name: " + this.getClass().getName());
    LOG.info(" - input: " + inputPath);
    LOG.info(" - output file: " + outputPath);
    
    FileSystem.get(getConf()).delete(new Path(outputPath), true);
    run(inputPath, outputPath);
    return 0;
  }

  public WordCount() {}

  public static void main(String[] args) throws Exception {
    ToolRunner.run(new WordCount(), args);
  }
}
