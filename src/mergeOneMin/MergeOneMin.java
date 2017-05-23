package mergeOneMin;

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

public class MergeOneMin {

	public static void main(String[] args) {
		String inputFolderPath = "multipleMerge";
		String outputFolderPath = "Merge";
		mergeMultipleFile(inputFolderPath,outputFolderPath);
		System.out.println("�Y�Ϸָ���� �n��������"+outputFolderPath+"�Y�ϊA��");
		
	}

	public static void mergeMultipleFile(String inputFolderPath,String outputFolderPath) {
		
		/**
		 * �˞�ρ��M�Y�ϵĺ���,���B�m�Y�����Д���Ĳ����M�зָ�
		 * inputFolderPath:��̎���Y�Ϸ��õ��Y�ϊA���Q
		 * outputFolderPath:�Y��̎����ɷָ��ꮅ���Y�ϕ����õ��Y�ϊA����
		 */

		// �Ȱ��Y�ϊA�ȵ�ȫ�������Y�Ϗ�����һ��Ȼ�����Д��Ă��ط���Ҫ����Y��

		// ����List����data��ÿһ�еĽ����Y��
		List<tradeData> data = new ArrayList<tradeData>();

		// ����List����index��ÿһ�Εr�g���������
		List<Integer> index = new ArrayList<Integer>();

		tradeData td = new tradeData();

		// [�xȡ�Y�ϊA�ȵ������Y�ρKȫ�����õ�data������]
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
			try {
				while ((line = br.readLine()) != null) {

					String[] item = line.split(",");
					if (isTitle(item[3])) {
						continue; // �Д��Ƿ���title,��title�����^
					}
					td.setTime(item[0]);
					td.setOpen(item[1]);
					td.setHigh(item[2]);
					td.setLow(item[3]);
					td.setClose(item[4]);
					td.setVolume(item[5]);
					td.setBtcAmount(item[6]);
					td.setTimeStamp(item[7]);
					data.add(td); // ȫ���Y�Ϸ���data������
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

		// �Ƴ����}�Y��
		Map<String, String> tidMap = new HashMap<String, String>();
		for (int i = 0; i < data.size(); i++) {
			if (tidMap.get(data.get(i).getTime()) == null) {
				tidMap.put(data.get(i).getTime(), "true");
			} else {
				data.remove(i);
				if (i == i - 1) {
					break; // ����һ�P��
				} else {
					i--;
				}

			}
		}

		System.out.println("�Y���������ꮅ���F���_ʼ�и��Y��");

		// �����Ȱѵ�һ�P�Y�ϵ��������Mindex������
		index.add(0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd__HH:mm");

		for (int i = 0; i < data.size(); i++) {
			if (isTitle(data.get(i).getOpen())) {
				continue; // �Д��Ƿ���title,��title�����^
			} else {

				long time1 = 0;
				long time2 = 0;
				if (i + 1 < data.size()) {
					try {
						// ����һ�ڵĕr�g��ʽ�D�Q�ɕr�g��
						time1 = sdf.parse(data.get(i).getTime()).getTime();
						time2 = sdf.parse(data.get(i + 1).getTime()).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// �����һ�P�Y�ϵĕr�g��Ŀǰ�Y�ϵĕr�g��5��犵�Ԓ
				if (i + 1 < data.size() && time2 - time1 > 300000) {
					// ���Д�����Y���������Mindex������
					index.add(i);
					index.add(i + 1);
					// System.out.println(data.get(i).getTimeStamp());
					// System.out.println(data.get(i + 1).getTimeStamp());
				} else {
					// ��P����
					// System.out.println(data.get(i).getTimeStamp());
				}
			}
		}
		// ������һ�P�Y�ϵ��������Mindex������
		index.add(data.size() - 1);

		// System.out.println(index);

		// [�ָ������ęn��]
		for (int i = 0; i < index.size(); i += 2) {
			if (i + 1 < index.size()) {
				// ��Q�r�g��ʽ�����ļ������r�ļ������e
				String start = data.get(index.get(i)).getTime().replaceAll("/", "-");
				start = start.replaceAll(":", "-");
				String end = data.get(index.get(i + 1)).getTime().replaceAll("/", "-");
				end = end.replaceAll(":", "-");
				System.out.println("�Y���и��_ʼ�r�g" + start);
				System.out.println("�Y���и�Y���r�g" + end);
				File mergeFile = new File(workPath() + "/"+outputFolderPath+"/" + start + "-" + end + ".csv");
				if (!mergeFile.exists()) {
					// �n�������ڄt����
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

		// [��ÿ�������ęn�������Y��]
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
						bw.write(data.get(start).getTime());
						bw.write(",");
						bw.write(data.get(start).getOpen());
						bw.write(",");
						bw.write(data.get(start).getHigh());
						bw.write(",");
						bw.write(data.get(start).getLow());
						bw.write(",");
						bw.write(data.get(start).getClose());
						bw.write(",");
						bw.write(data.get(start).getVolume());
						bw.write(",");
						bw.write(data.get(start).getBtcAmount());
						bw.write(",");
						bw.write(data.get(start).getTimeStamp());
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
		String time;
		String open;
		String high;
		String low;
		String close;
		String volume;
		String btcAmount;
		String timeStamp;
		
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getOpen() {
			return open;
		}
		public void setOpen(String open) {
			this.open = open;
		}
		public String getHigh() {
			return high;
		}
		public void setHigh(String high) {
			this.high = high;
		}
		public String getLow() {
			return low;
		}
		public void setLow(String low) {
			this.low = low;
		}
		public String getClose() {
			return close;
		}
		public void setClose(String close) {
			this.close = close;
		}
		public String getVolume() {
			return volume;
		}
		public void setVolume(String volume) {
			this.volume = volume;
		}
		public String getBtcAmount() {
			return btcAmount;
		}
		public void setBtcAmount(String btcAmount) {
			this.btcAmount = btcAmount;
		}
		public String getTimeStamp() {
			return timeStamp;
		}
		public void setTimeStamp(String timeStamp) {
			this.timeStamp = timeStamp;
		}
		

		
	}

	// �Д��Ƿ���title
	// true ��
	// false ����
	public static boolean isTitle(String str) {
		/**
		 * �˞��Д��ִ��Ƿ�]�Д���
		 * ����true:��ʾ��ǔ��ֽM��
		 * ����false:��ʾ�锵�ֽM��
		 */
		boolean flag = false;
		for (int index = 0; index < str.length(); index++) {
			if (!(Character.isDigit(str.charAt(index)) || str.charAt(index) == '.')) {
				flag = true;
				break; // �ִ��г��Fһ���ǔ�ֵ���Ƿ�.����Ԫ�ͽY�� loop, ����@�����҂�Ҫ���ִ�
			}
		}
		return flag;
	}

}

