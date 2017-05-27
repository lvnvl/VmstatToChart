package com.vmstat.main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.vmstat.xchart.MyXChartBuilder;

public class ToChart {

	public void searchDir(String dir, boolean verbose) {
		if (verbose) {
			System.out.println(" searching dir: " + dir);
		}
		File currentDir = new File(dir);
		File[] subFileStrings = currentDir.listFiles();
		String[] subLogs = currentDir.list(new LogFileFilter());
		int logNum = subLogs.length;
		// 如果有vmstat日志文件
		if (subLogs.length != 0) {
			List<HashMap<String, List<String>>> logDatas = new ArrayList<HashMap<String, List<String>>>();
			// 单独处理每个日志
			for (int i = 0; i < logNum; i++) {
				String logPath = dir + File.separator + subLogs[i];
				if (verbose) {
					System.out.println("processing file: " + logPath);
				}
				// 从日志中读取数据
				VmstatModel model = new VmstatModel(logPath);
				model.build();
				HashMap<String, List<String>> logData = model.getDataMap();
				logDatas.add(logData);
				/**
				 * 画图,
				 * r,b
				 * memory
				 * cpu
				 * swap
				 */
				new MyXChartBuilder(logPath, "r&b", "进程个数")
					.addIntegerSeries("r", logData.get("r").size(), logData.get("r"))
					.addIntegerSeries("b", logData.get("b").size(), logData.get("b"))
					.build();
				new MyXChartBuilder(logPath, "内存占用率", "百分比")
					.addFloatSeries("memory", logData.get("memory").size(), logData.get("memory"))
					.build();
				new MyXChartBuilder(logPath, "CPU占用率", "百分比")
					.addIntegerSeries("cpu", logData.get("cpu").size(), logData.get("cpu"))
					.build();
				new MyXChartBuilder(logPath, "虚拟内存占用", "使用（MB）")
				.addIntegerSeries("swpd", logData.get("swpd").size(), logData.get("swpd"))
				.build();
			
			}
			
			// 综合日志输出
			/**
			 * 画图,多服务器对比
			 * I/O wait
			 * memory
			 * cpu
			 */
			MyXChartBuilder ioWaitChartBuilder = new MyXChartBuilder(dir, "IO Wait", "");
			MyXChartBuilder memoryChartBuilder = new MyXChartBuilder(dir, "Memory", "%");
			MyXChartBuilder cpuChartBuilder = new MyXChartBuilder(dir, "CPU", "%");
			
			MyXChartBuilder swpdChartBuilder = new MyXChartBuilder(dir, "虚拟内存", "使用（MB）");
			
			for (int i = 0; i < logNum; i++) {
				String logPath = dir + File.separator + subLogs[i];
				
				File logFile = new File(logPath);
				int dot = logFile.getName().lastIndexOf(".");
				String ip = logFile.getName().substring(0, dot);
				if (logDatas.get(i).containsKey("wa")) {
					ioWaitChartBuilder.addIntegerSeries(
							ip, 
							logDatas.get(i).get("wa").size(), 
							logDatas.get(i).get("wa")
							);
				}
				if (logDatas.get(i).containsKey("memory")) {
					memoryChartBuilder.addFloatSeries(
							ip, 
							logDatas.get(i).get("memory").size(), 
							logDatas.get(i).get("memory")
							);
				}
				if (logDatas.get(i).containsKey("cpu")) {
					cpuChartBuilder.addIntegerSeries(
							ip, 
							logDatas.get(i).get("cpu").size(), 
							logDatas.get(i).get("cpu")
							);
				}
				if (logDatas.get(i).containsKey("swpd")) {
					swpdChartBuilder.addFloatSeries(
							ip,
							logDatas.get(i).get("swpd").size(),
							logDatas.get(i).get("swpd").stream()
								.map(swpd -> Float.valueOf(swpd).floatValue()/1000.0)
								.map(swpdFloat -> String.valueOf(swpdFloat))
								.collect(Collectors.toList())
							);
				}
			}
			ioWaitChartBuilder.build();
			memoryChartBuilder.build();
			cpuChartBuilder.build();
			swpdChartBuilder.build();
			return;
		}
		// 遍历文件夹
		for (int i = 0; i < subFileStrings.length; i++) {
			if (subFileStrings[i].isDirectory()) {
				searchDir(subFileStrings[i].getAbsolutePath(), verbose);
			}
		}
	}
	
	class LogFileFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".log");
		}
		
	}
	
	public static void main(String[] args) {
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("h", "help", false, "Show usage. This program will generate cpu, memeory and IO wait\n"
				+ "\tfor every found log file, \n"
				+ "\tand also will generate every item differ among log files defaultly.\n"
				+ "\tsource code: https://github.com/v73alice/VmstatToChart.git");
		options.addOption("v", "verbose", false, "Print out VERBOSE infomation");
		options.addOption("d", "directory", true, "Directory which contains the log files");
		
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean verbose = false;
		String directory = "";
		if (commandLine.hasOption('h')) {
			HelpFormatter hfFormatter = new HelpFormatter();
			hfFormatter.printHelp("ToChart", options, true);
			System.exit(0);
		}
		if (commandLine.hasOption('v')) {
			verbose = true;
		}
		if (commandLine.hasOption('d')) {
			directory = commandLine.getOptionValue('d');
		}
		new ToChart().searchDir(directory, verbose);
		System.out.println("DONE!...");
	}
	
}
