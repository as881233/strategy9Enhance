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
		
		System.out.println("�Y�Ϸָ���� �n��������"+outputFolderPath+"�Y�ϊA��");
		
	}

	public static void mergeMultipleFile(String inputFolderPath,String outputFolderPath, int min) {
		
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
			System.out.println("�����xȡ"+fileArr[dataIndex].getName());
			try {
				while ((line = br.readLine()) != null) {

					String[] item = line.split(",");
					if (isTitle(item[3])) {
						continue; // �Д��Ƿ���title,��title�����^
					}
					td.setTimeMs(item[0]);
					td.setTid(item[1]);
					td.setTime(item[2]);
					td.setPrice(item[3]);
					td.setVolume(item[4]);
					td.setType(item[5]);
					td.setDate(item[6]);
					td.setOther(item[7]);
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
			if (tidMap.get(data.get(i).getTid()) == null) {
				tidMap.put(data.get(i).getTid(), "true");
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd__HH:mm:ss:SSS");

		for (int i = 0; i < data.size(); i++) {
			if (isTitle(data.get(i).getPrice())) {
				continue; // �Д��Ƿ���title,��title�����^
			} else {

				long time1 = 0;
				long time2 = 0;
				if (i + 1 < data.size()) {
					try {
						// ����һ�ڵĕr�g��ʽ�D�Q�ɕr�g��
						time1 = sdf.parse(data.get(i).getTimeMs()).getTime();
						time2 = sdf.parse(data.get(i + 1).getTimeMs()).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// �����һ�P�Y�ϵĕr�g��Ŀǰ�Y�ϵĕr�g��5��犵�Ԓ
				if (i + 1 < data.size() && time2 - time1 > min*60*1000) {
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
				String start = data.get(index.get(i)).getDate().replaceAll("/", "-");
				start = start.replaceAll(":", "-");
				String end = data.get(index.get(i + 1)).getDate().replaceAll("/", "-");
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
