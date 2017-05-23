package multipleFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleFile {

	public static void main(String[] args) {
		String inputFolderPath = "multipleMerge";
		String outputFolderPath = "Merge";
		int min = (args!=null && args.length>0)? Integer.valueOf(args[0]):5;
		/*
		int min = 5;
		if (args!=null && args.length>0){
			min = Integer.valueOf(args[0]);
		}
		*/
		
		mergeMultipleFile(inputFolderPath,outputFolderPath,min);
		
		System.out.println("資料分割完成 檔案放置在"+outputFolderPath+"資料夾中");
		
	}

	public static void mergeMultipleFile(String inputFolderPath,String outputFolderPath, int min) {
		
		/**
		 * 此為合併多組資料的函數,將連續資料中有斷掉的部分進行分割
		 * inputFolderPath:待處理資料放置的資料夾名稱
		 * outputFolderPath:資料處理完成分割完畢的資料會放置的資料夾名抽
		 */

		// 先把資料夾內的全部交易資料彙集在一起然後再判斷哪個地方需要拆掉資料

		// 創建List集合data放每一列的交易資料
		List<tradeData> data = new ArrayList<tradeData>();

		// 創建List集合index放每一次時間斷掉的索引
		List<Integer> index = new ArrayList<Integer>();

		tradeData td = new tradeData();

		// [讀取資料夾內的所有資料並全部放置到data集合中]
		File file = new File(workPath() + "/"+inputFolderPath);
		File[] fileArr = file.listFiles();

		for (int dataIndex = 0; dataIndex < fileArr.length; dataIndex++) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(fileArr[dataIndex]));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = null;
			System.out.println("正在讀取"+fileArr[dataIndex].getName());
			try {
				while ((line = br.readLine()) != null) {

					String[] item = line.split(",");
					if (isTitle(item[3])) {
						continue; // 判斷是否有title,有title就跳過
					}
					td.setTimeMs(item[0]);
					td.setTid(item[1]);
					td.setTime(item[2]);
					td.setPrice(item[3]);
					td.setVolume(item[4]);
					td.setType(item[5]);
					td.setDate(item[6]);
					td.setOther(item[7]);
					data.add(td); // 全部資料放在data集合中
					td = new tradeData();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 移除重複資料
		Map<String, String> tidMap = new HashMap<String, String>();
		for (int i = 0; i < data.size(); i++) {
			if (tidMap.get(data.get(i).getTid()) == null) {
				tidMap.put(data.get(i).getTid(), "true");
			} else {
				data.remove(i);
				if (i == i - 1) {
					break; // 最後一筆了
				} else {
					i--;
				}

			}
		}

		System.out.println("資料已修正完畢，現在開始切割資料");

		// 首先先把第一筆資料的索引放進index集合中
		index.add(0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd__HH:mm:ss:SSS");

		for (int i = 0; i < data.size(); i++) {
			if (isTitle(data.get(i).getPrice())) {
				continue; // 判斷是否有title,有title就跳過
			} else {

				long time1 = 0;
				long time2 = 0;
				if (i + 1 < data.size()) {
					try {
						// 將第一欄的時間格式轉換成時間戳
						time1 = sdf.parse(data.get(i).getTimeMs()).getTime();
						time2 = sdf.parse(data.get(i + 1).getTimeMs()).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 如果下一筆資料的時間比目前資料的時間多5分鐘的話
				if (i + 1 < data.size() && time2 - time1 > min*60*1000) {
					// 將有斷掉的資料索引放進index集合中
					index.add(i);
					index.add(i + 1);
					// System.out.println(data.get(i).getTimeStamp());
					// System.out.println(data.get(i + 1).getTimeStamp());
				} else {
					// 逐筆寫入
					// System.out.println(data.get(i).getTimeStamp());
				}
			}
		}
		// 將最後一筆資料的索引放進index集合中
		index.add(data.size() - 1);

		// System.out.println(index);

		// [分割創建斷掉的檔案]
		for (int i = 0; i < index.size(); i += 2) {
			if (i + 1 < index.size()) {
				// 替換時間格式避免文件創建時文件名報錯
				String start = data.get(index.get(i)).getDate().replaceAll("/", "-");
				start = start.replaceAll(":", "-");
				String end = data.get(index.get(i + 1)).getDate().replaceAll("/", "-");
				end = end.replaceAll(":", "-");
				System.out.println("資料切割開始時間" + start);
				System.out.println("資料切割結束時間" + end);
				File mergeFile = new File(workPath() + "/"+outputFolderPath+"/" + start + "-" + end + ".csv");
				if (!mergeFile.exists()) {
					// 檔案不存在則創建
					try {
						mergeFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		// System.out.println(index.size());

		File mergeFile = new File(workPath() + "/"+outputFolderPath);
		File[] writeFile = mergeFile.listFiles();

		// [對每個創建的檔案寫入資料]
		for (int i = 0; i < writeFile.length; i++) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(writeFile[i]));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (2 * i < index.size()) {
				int start = index.get(2 * i);
				int end = index.get(2 * i + 1);
				for (; start < end; start++) {
					try {
						bw.write(data.get(start).getTimeMs());
						bw.write(",");
						bw.write(data.get(start).getTid());
						bw.write(",");
						bw.write(data.get(start).getTime());
						bw.write(",");
						bw.write(data.get(start).getPrice());
						bw.write(",");
						bw.write(data.get(start).getVolume());
						bw.write(",");
						bw.write(data.get(start).getType());
						bw.write(",");
						bw.write(data.get(start).getDate());
						bw.write(",");
						bw.write(data.get(start).getOther());
						bw.newLine();
						bw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	}

	public static String workPath() {
		return System.getProperty("user.dir");
	}

	public static class tradeData {
		String timeMs;
		String tid;
		String time;
		String price;
		String volume;
		String type;
		String date;
		String other;

		public String getTimeMs() {
			return timeMs;
		}

		public void setTimeMs(String timeMs) {
			this.timeMs = timeMs;
		}

		public String getTid() {
			return tid;
		}

		public void setTid(String tid) {
			this.tid = tid;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public String getVolume() {
			return volume;
		}

		public void setVolume(String volume) {
			this.volume = volume;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getOther() {
			return other;
		}

		public void setOther(String other) {
			this.other = other;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
	}

	// 判斷是否有title
	// true 是
	// false 不是
	public static boolean isTitle(String str) {
		/**
		 * 此為判斷字串是否沒有數字
		 * 返回true:表示為非數字組成
		 * 返回false:表示為數字組成
		 */
		boolean flag = false;
		for (int index = 0; index < str.length(); index++) {
			if (!(Character.isDigit(str.charAt(index)) || str.charAt(index) == '.')) {
				flag = true;
				break; // 字串中出現一個非數值或是非.的字元就結束 loop, 因為這不是我們要的字串
			}
		}
		return flag;
	}

}
