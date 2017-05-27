package com.vmstat.xchart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class MyXChartBuilder {
//	private XYChartBuilder chartBuilder;
	private String fileName;
	private XYChart chart;
	private String title;
	
	/**
	 * 
	 * @param fileName 图片保存位置，传入文件夹则直接保存到title.png。传入文件则保存到同级路径下的ip_title.png
	 * @param title     图片 标题
	 * @param yTitle    纵轴 标题（横轴标题默认为“时间轴”）
	 */
	public MyXChartBuilder(String fileName, String title, String yTitle) {
		this.fileName = fileName;
		this.title = title;
		chart = new XYChartBuilder().
				width(800).
				height(600).
				title(title).
				theme(ChartTheme.Matlab).
				xAxisTitle("时间轴").
				yAxisTitle(yTitle).
				build();

	    // Customize Chart
	    chart.getStyler().setPlotGridLinesVisible(false);
	    chart.getStyler().setXAxisTickMarkSpacingHint(100);

	}

	/**
	 * 
	 * @author ZYC
	 * @param name   当前曲线名称
	 * @param xRange x轴的数据范围。
	 * @param yData
	 * @return
	 * 2017年4月19日
	 */
	public MyXChartBuilder addIntegerSeries(String name, int xRange, List<String> yData) {
		List<Integer> xData = new ArrayList<Integer>(xRange);
		List<Integer> yIntegerData = new ArrayList<Integer>(xRange);
	    for (int i = 0; i < xRange; i++) {
	    	xData.add(i);
	    	yIntegerData.add(Integer.valueOf(yData.get(i)));
	    }
		XYSeries series = chart.addSeries(name, xData, yIntegerData);
	    series.setMarker(SeriesMarkers.NONE);
		return this;
	}
	
	public MyXChartBuilder addFloatSeries(String name, int xRange, List<String> yData) {
		List<Integer> xData = new ArrayList<Integer>(xRange);
		List<Float> yFloatData = new ArrayList<Float>(xRange);
	    for (int i = 0; i < xRange; i++) {
	    	xData.add(i);
	    	yFloatData.add(Float.valueOf(yData.get(i)));
	    }
		XYSeries series = chart.addSeries(name, xData, yFloatData);
	    series.setMarker(SeriesMarkers.NONE);
		return this;
	}
	
	/**
	 * 用来将图标以PNG格式保存
	 * @return 是否生成成功
	 * @throws IOException 
	 */
	public boolean build() {
		File logFile = new File(fileName);
		String pngName = "";
		if (logFile.isDirectory()) {
			pngName = fileName + File.separator + "_" + title;
		} else {
			int dot = logFile.getName().lastIndexOf(".");
			String ip = logFile.getName().substring(0, dot);
			pngName = logFile.getParent() + File.separator + ip + "_" + title;
		}
		 
		try {
			BitmapEncoder.saveBitmapWithDPI(chart, pngName, BitmapFormat.PNG, 300);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
