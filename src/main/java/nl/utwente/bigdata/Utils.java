package nl.utwente.bigdata;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class Utils {
	public static void recursivelyAddInputPaths(Job job, Path path)
			throws IOException {
		FileSystem fs = FileSystem.get(URI.create(path.toString()),
				job.getConfiguration());
		FileInputFormat
				.setInputPathFilter(
						job,
						org.apache.hadoop.mapred.Utils.OutputFileUtils.OutputLogFilter.class);
		FileStatus[] ls;
		if (!path.toString().contains("*")) {
			ls = fs.listStatus(path);

		} else {
			ls = fs.globStatus(path);
		}
		if (ls == null) {
			throw new IOException("Path " + path + " doesn't exist");
		}
		for (FileStatus status : ls) {
			if (status.getPath().getName().startsWith("_")) {
				continue;
			}
			if (status.isDirectory()) {
				recursivelyAddInputPaths(job, status.getPath());
			}
			FileInputFormat.addInputPath(job, status.getPath());
		}
	}
	
	public static void addCacheFiles(Job job, String[] strings) throws IOException {
		FileSystem fs = FileSystem.get(job.getConfiguration());
		for (String file : strings) {
			Path p = new Path(file);	
			if (!fs.exists(p)) {
				fs.copyFromLocalFile(p, p);
			}
			DistributedCache.addCacheFile(p.toUri(), job.getConfiguration());
		}
	}
	
	private static JsonParser parser = new JsonParser();
	public static String getTweetText(String tweet) {
		try {
			return parser.parse(tweet).getAsJsonObject().get("text").getAsString();
		} catch (Exception e) {
			return "";
		}
	}
}
