package com.vmstat.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VmstatModel {

	private List<String> items;
	private HashMap<String, Integer> index;
	private String fileName;
	private HashMap<String, List<String>> dataMap;
	
	public VmstatModel(String fileName) {
		this.fileName = fileName;
		this.items = new ArrayList<String>();
		this.index = new HashMap<String, Integer>();
		this.dataMap = new HashMap<String, List<String>>();
	}
	
	public HashMap<String, Integer> getIndex() {
		return index;
	}

	public void setIndex(HashMap<String, Integer> index) {
		this.index = index;
	}

	public void build() {
		boolean isInit = false;
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			while ((str = br.readLine()) != null) {
				// 标题行
				if (str.indexOf("-----") != -1) {
					continue;
				}
				// 指标行
				if (str.indexOf("swpd") != -1) {
					if (isInit) {
						continue;	
					} else {
						String[] strs = str.trim().split("\\s+");
						int i = 0;
						for (i = 0; i < strs.length; i ++) {
							index.put(strs[i], Integer.valueOf(i));
							items.add(strs[i]);
						}
						index.put("cpu", Integer.valueOf(i ++));
						items.add("cpu");
						index.put("memory", Integer.valueOf(i ++));
						items.add("memory");
						isInit = true;
						continue;
					}
				}
				// 数据行
				String[] numStrings = str.trim().split("\\s+");
//				System.out.println("ll: " + numStrings.length);
				for (int i = 0; i < numStrings.length; i ++) {
					String itemName = items.get(i);
//					System.out.println("itemName:" + itemName);
					if (! dataMap.containsKey(itemName)) {
						dataMap.put(itemName, new ArrayList<String>());
					}
					dataMap.get(itemName).add(numStrings[i]);
				}
				if (! dataMap.containsKey("cpu")) {
					dataMap.put("cpu", new ArrayList<String>());
				}
//				System.out.println("1: " + (dataMap.get("sy").size() - 1));
//				System.out.println("2: " + dataMap.get("sy").get(dataMap.get("sy").size() - 1));
//				System.out.println("3: " + Integer.valueOf(dataMap.get("sy").get(dataMap.get("sy").size() - 1)));
				dataMap.get("cpu").add(String.valueOf(Integer.valueOf(dataMap.get("us").get(dataMap.get("us").size() - 1)) +
						Integer.valueOf(dataMap.get("sy").get(dataMap.get("sy").size() - 1))));
				if (! dataMap.containsKey("memory")) {
					dataMap.put("memory", new ArrayList<String>());
				}
				//   free   buff  cache
//				long swpd = Long.valueOf(dataMap.get("swpd").get(dataMap.get("swpd").size() - 1));
				long free = Long.valueOf(dataMap.get("free").get(dataMap.get("free").size() - 1));
				long buff = Long.valueOf(dataMap.get("buff").get(dataMap.get("buff").size() - 1));
				long cache = Long.valueOf(dataMap.get("cache").get(dataMap.get("cache").size() - 1));
				// incorrect calculation !
//				dataMap.get("memory").add(String.format("%.4f",
//						(float)(swpd + buff + cache)/(float)(swpd + free + buff + cache)*100.0f));
				dataMap.get("memory").add(String.format("%.4f",
						(float)(buff + cache)/(float)(free + buff + cache)*100.0f));
			}
			br.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public HashMap<String, List<String>> getDataMap() {
		return dataMap;
	}
	public void setDataMap(HashMap<String, List<String>> dataMap) {
		this.dataMap = dataMap;
	}
	
}
