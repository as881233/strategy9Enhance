package mergeMs;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeMs {

	public static void main(String[] args) {
		String inputFolderPath = "input";
		String outputFolderPath = "mergeMs";
		mergeDataMs(inputFolderPath,outputFolderPath);
		System.out.println("����ρ���� �n��������"+outputFolderPath+"�Y�ϊA��");
	}

	public static void mergeDataMs(String inputFolderPath,String outputFolderPath)  {
		
		/**
		 * �˞�ρ��Y�σ���ͬ����ĺ���
		 * inputFolderPath:��̎���Y�Ϸ��õ��Y�ϊA���Q
		 * outputFolderPath:�Y��̎����ɷָ��ꮅ���Y�ϕ����õ��Y�ϊA����
		 */

		File file = new File(workPath() + "/"+inputFolderPath);
		File[] fileArr = file.listFiles();
		for (int fileNum = 0; fileNum < fileArr.length; fileNum++) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(fileArr[fileNum]));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File newFile = new File(workPath() + "/"+outputFolderPath+"/" + fileArr[fileNum].getName() + "__mergeMs.csv");
			if (!newFile.exists()) {
				try {
					newFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(newFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = null;
			List<String> localTime = new ArrayList<String>();
			List<String> tradeTime = new ArrayList<String>();
			List<String> price = new ArrayList<String>();
			List<String> volume = new ArrayList<String>();
			List<String> timeStamp = new ArrayList<String>();
			List<String> buyType = new ArrayList<String>();
			List<String> snull = new ArrayList<String>();

			List<String> adjustedLocalTime = new ArrayList<String>();
			List<String> adjustedTradeTime = new ArrayList<String>();
			List<String> adjustedPrice = new ArrayList<String>();
			List<String> adjustedVolume = new ArrayList<String>();
			List<String> adjustedTimeStamp = new ArrayList<String>();
			List<String> adjustedBuyType = new ArrayList<String>();
			List<String> adjustedSnull = new ArrayList<String>();

			try {
				while ((line = br.readLine()) != null) {
					String[] data = line.split(",");
					localTime.add(data[0]);
					timeStamp.add(data[1]);
					tradeTime.add(data[2]);
					price.add(data[3]);
					volume.add(data[4]);
					buyType.add(data[5]);
					snull.add(data[7]);
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

			int size = localTime.size();
			int getInto = 0;
			int count = 1;
			String tempLocalTime = "";
			double tempPrice = 0;
			String tempTradeTime = "";
			double tempVolume = 0;
			String tempTimeStamp = "";
			String tempBuyType = "";
			String tempSnull = "";

			for (int i = 0; i < size; i += count - 1) {
				count = 1;
				for (int j = 0; j < size; j++) {
					if (i + j < size) {
						if (localTime.get(i).equals(localTime.get(i + j))&&tradeTime.get(i).equals(tradeTime.get(i+j))) {
							// ���õ�һ�Ѓr����Д��Ƿ�锵�� ����ǔ��ֵ�Ԓ����]��title
							if ((isTitle(price.get(i)))) {
								adjustedLocalTime.add(localTime.get(i));
								adjustedPrice.add(price.get(i));
								adjustedTradeTime.add(tradeTime.get(i));
								adjustedVolume.add(volume.get(i));
								adjustedTimeStamp.add(timeStamp.get(i));
								adjustedBuyType.add(buyType.get(i));
								adjustedSnull.add(snull.get(i));
								count++;
								break;

							} else {
								tempLocalTime = localTime.get(i + j);
								tempPrice = Double.parseDouble(price.get(i + j));
								tempTradeTime = tradeTime.get(i + j);
								tempVolume += Double.parseDouble(volume.get(i + j));
								tempTimeStamp = timeStamp.get(i + j);
								tempBuyType = buyType.get(i + j);
								tempSnull = snull.get(i + j);
								count++;
								getInto = 1;
							}

						} else {

							if (getInto == 1) {
								adjustedLocalTime.add(tempLocalTime);
								adjustedPrice.add(String.valueOf(tempPrice));
								adjustedTradeTime.add(tempTradeTime);
								adjustedVolume.add(String.valueOf(tempVolume));
								adjustedTimeStamp.add(tempTimeStamp);
								adjustedBuyType.add(tempBuyType);
								adjustedSnull.add(tempSnull);
								tempVolume = 0;
								getInto = 0;
								break;
							} else {
								adjustedLocalTime.add(localTime.get(i));
								adjustedPrice.add(price.get(i));
								adjustedTradeTime.add(tradeTime.get(i));
								adjustedVolume.add(volume.get(i));
								adjustedTimeStamp.add(timeStamp.get(i));
								adjustedBuyType.add(buyType.get(i));
								adjustedSnull.add(snull.get(i));
								tempVolume = 0;
								getInto = 0;

								break;
							}

						}
					}
				}
			}

			// �Y�ό���csv
			for (int i = 0; i < adjustedLocalTime.size(); i++) {

				try {
					bw.write(adjustedLocalTime.get(i));
					bw.write(",");
					bw.write(adjustedTimeStamp.get(i));
					bw.write(",");
					bw.write(adjustedTradeTime.get(i));
					bw.write(",");
					bw.write(adjustedPrice.get(i));
					bw.write(",");
					bw.write(adjustedVolume.get(i));
					bw.write(",");
					bw.write(adjustedBuyType.get(i));
					bw.write(",");
					bw.write(adjustedTradeTime.get(i));
					bw.write(",");
					bw.write(adjustedSnull.get(i));
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

	public static String workPath() {
		return System.getProperty("user.dir");
	}

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

