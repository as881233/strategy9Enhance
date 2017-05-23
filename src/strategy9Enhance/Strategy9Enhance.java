package strategy9Enhance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//��횫@ȡÿ���r�g�γ�������
public class Strategy9Enhance {

	public static void main(String[] args) throws IOException {
		
		ArrayList<Double> result = new ArrayList<Double>();
		double patternCount = 0;
		double profitTime = 0;
		double lossTime = 0;
		double totalProfit = 0;
		double maxloss = 0;
		double patternDivideCount = 0;
		double totalLoss = 0;
		File file = new File(workPath() + "/input");
		File[] fileArr = file.listFiles();
		for (int i = 0; i < fileArr.length; i++) {

			result = strategy9(fileArr[i]);
			patternCount += result.get(0);
			patternCount += result.get(5);
			profitTime += result.get(1);
			lossTime += result.get(2);
			totalProfit += result.get(3);
			maxloss += result.get(4);
			patternDivideCount += result.get(5);
			totalLoss+=result.get(6);

		}

		 writeTxt(workPath() + "/history.txt", "pattern�����F" + (int)patternCount + "������2�������ǰ1������r��"+(int)patternDivideCount+"�� "+(int)patternCount+" ���гɽ�"
		 + (int)(profitTime + lossTime) + "�� ���@�� " + totalProfit + " �@���Δ� "
		+ (int)profitTime + " ̝�p�Δ� " + (int)lossTime + " ����"
		 + ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "% �_�����̝�p"+maxloss+"��"+"  �Ѯa��̝�p�Ć���ȫ��������õ�̝�p���~��"+totalLoss+"�c",
		 true);
		System.out.println("pattern�����F" + (int) patternCount + "�� ����2�������ǰ1������r��" + (int) patternDivideCount + "��  "
				+ (int) patternCount + "���гɽ�" + (int) (profitTime + lossTime) + "��  ���@�� " + totalProfit + " �@���Δ� "
				+ (int) profitTime + " ̝�p�Δ� " + (int) lossTime + " ����"
				+ ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "%  �_�����̝�p" + maxloss + "��"+"  �Ѯa��̝�p�Ć���ȫ��������õ�̝�p���~��"+totalLoss+"�c");

	}

	// File file,int timeRange,double spread,double markup,int multiple,int
	// laterNumber,int firstProfit,int secondProfit,int maxLoss
	public static ArrayList<Double> strategy9(File file) throws IOException {

		ArrayList<Double> result = new ArrayList<Double>();
		Properties properties = new Properties();
		File fileConfig = new File(workPath() + "/config.ini");
		FileInputStream fis = new FileInputStream(fileConfig);
		properties.load(fis);
		fis.close();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		int timeRange = Integer.parseInt(properties.getProperty("timeRange"));
		double spread = Integer.parseInt(properties.getProperty("spread"));
		double markup = Integer.parseInt(properties.getProperty("markup"));
		int multiple = Integer.parseInt(properties.getProperty("multiple"));
		int laterNumber = Integer.parseInt(properties.getProperty("laterNumber"));
		int firstProfit = Integer.parseInt(properties.getProperty("firstProfit"));
		int secondProfit = Integer.parseInt(properties.getProperty("secondProfit"));
		int maxLoss = Integer.parseInt(properties.getProperty("maxLoss"));
		// ���xList����timeList�b�r�g
		List<String> timeList = new ArrayList<String>();
		// ���xList����priceList�b�ɽ��r
		List<Double> priceList = new ArrayList<Double>();
		// ���xList����volumeList�b�ɽ���
		List<Double> volumeList = new ArrayList<Double>();
		// ���xList����indexList�bÿ���r�g���_ʼ������
		List<Integer> indexList = new ArrayList<Integer>();
		// ���xList����newIndexList��indexList�е��B�m����ȡ��һ�����MnewIndexList��
		List<Integer> newIndexList = new ArrayList<Integer>();
		// ���xList����openNow�bÿ���r�g�ε��_�P�r
		List<Double> openNow = new ArrayList<Double>();
		// ���xList����highNow�bÿ���r�g�ε���߃r
		List<Double> highNow = new ArrayList<Double>();
		// ���xList����lowNow�bÿ���r�g�ε���̓r
		List<Double> lowNow = new ArrayList<Double>();
		// ���xList����closeNow�bÿ���r�g�ε��ձP�r
		List<Double> closeNow = new ArrayList<Double>();
		// ���xList����volumeNow�bÿ���r�g�εĿ���
		List<Double> volumeNow = new ArrayList<Double>();
		// ���xList����patternIndex�bpattern���F�ĕr�g�γ�������
		List<Integer> patternIndex = new ArrayList<Integer>();
		// ���xList����patternIndex�b�ɸ������ǰһ�����ǷNpattern���F�ĕr�g�γ�������
		List<Integer> patternDivideIndex = new ArrayList<Integer>();
		// ���xList����keyPattern�bpattern���F�Ĵ_�Еr�g(���_��Ηl��)
		List<Integer> keyPattern = new ArrayList<Integer>();
		// ���xList����patternNewIndex�bpattern���F�Ĵ_�Еr�g����newIndexList������λ��
		List<Integer> patternNewIndex = new ArrayList<Integer>();
		// ���xList����patternDivideNewIndex�bpattern���F�Ĵ_�Еr�g����newIndexList������λ��
		List<Integer> patternDivideNewIndex = new ArrayList<Integer>();
		// ���xList����keyPatternIndex�bkeyPattern����������newIndexList��������λ��
		List<Integer> keyPatternIndex = new ArrayList<Integer>();
		// ���xList����dynamicHighPrice�bPattern���F�r�Į�ǰ��߃r
		List<Double> dynamicHighPrice = new ArrayList<Double>();
		// ���xList����dynamicLowPrice�bPattern���F�r�Į�ǰ��̓r
		List<Double> dynamicLowPrice = new ArrayList<Double>();
		// ���xList����buyType�b�I�M�ķN�(�Նζ���)
		List<Integer> buyType = new ArrayList<Integer>();
		// ���xList����buyIndex�b�ɽ��εĕr�g����
		List<Integer> buyIndex = new ArrayList<Integer>();
		// ���xList����buyKeyPattern�b�ɽ��r�g�������ĕr�g������
		List<Integer> buyKeyPattern = new ArrayList<Integer>();
		// ���xList����buyPrice�b�I�M�Ĝʴ_�rλ
		List<Double> buyPrice = new ArrayList<Double>();

		int profitTime = 0;
		int lossTime = 0;
		double totalProfit = 0;
		double totalLoss = 0;
		double firstPercent = 50;
		double secondPercent = 50;

		// ======================̎��r�gList======================
		while ((line = br.readLine()) != null) {
			String[] item = line.split(",");
			if (isTitle(item[3])) {
				continue; // �Д��Ƿ���title
			}
			timeList.add(item[2]);
			priceList.add(Double.parseDouble(item[3]));
			volumeList.add(Double.parseDouble(item[4]));
		}
		br.close();
		// �@ȡ���Y�ϹP��
		int totalSize = timeList.size();

		// ��v���וr�g
		// System.out.println(timeList);
		// ��v�ɽ��r
		// System.out.println(priceList);
		// ��v�ɽ���
		// System.out.println(volumeList);

		// �@ȡÿ���r�g�γ�������
		int timeRangeToHour = timeRange / 60;
		for (int i = 0; i < totalSize; i++) {
			int minute = Integer.parseInt(timeList.get(i).substring(15, 17));
			int hour = Integer.parseInt(timeList.get(i).substring(12, 14));

			// System.out.println(timeList.get(i));
			if (timeRange <= 60) {
				if (minute % timeRange == 0) {
					indexList.add(i);
				}
			} else {
				if (hour % timeRangeToHour == 0 && minute % timeRange == 0) {
					indexList.add(i);
				}
			}

		}

		// �@ȡ�r�g�γ������ĹP��
		totalSize = indexList.size();

		// ��indexList�е��B�m����ȡ��һ�����MnewIndexList��
		// �Ȱ�indexList�еĵ�һ���������MnewIndexList��
		// System.out.println(indexList);
		if (indexList.isEmpty()) {
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			return result;
		}

		newIndexList.add(indexList.get(0));
		for (int i = 0; i < totalSize - 1; i++) {
			if (i + 1 < totalSize - 1 && indexList.get(i + 1) - indexList.get(i) != 1) {
				newIndexList.add(indexList.get(i + 1));
			}
		}
		// System.out.println(newIndexList); // ��vÿ���r�g�γ�������

		// ======================̎���_�P�rList======================
		for (int i = 0; i < newIndexList.size(); i++) {
			openNow.add(priceList.get(newIndexList.get(i)));
		}
		// System.out.println(openNow); //��vÿ���r�g�γ����_�P�r

		// ======================̎����߃rList======================
		double maxPrice = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				if (priceList.get(j) > maxPrice) {
					maxPrice = priceList.get(j);
				}
			}
			highNow.add(maxPrice);
			maxPrice = 0;
		}
		// System.out.println(highNow); //��vÿ���r�g�ε���߃r

		// ======================̎����̓rList======================
		double minPrice = 10000;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				if (priceList.get(j) < minPrice) {
					minPrice = priceList.get(j);
				}
			}
			lowNow.add(minPrice);
			minPrice = 10000;
		}
		// System.out.println(lowNow); //��vÿ���r�g�ε���̓r

		// ======================̎���ձP�rList======================
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			closeNow.add(priceList.get(newIndexList.get(i + 1) - 1));
		}

		// System.out.println(closeNow); //��vÿ���r�g�ε��ձP�r

		// ======================̎��r�g�εĿ���List======================
		double volume = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				volume += volumeList.get(j);
			}
			volumeNow.add(volume);
			volume = 0;
		}
		// System.out.println(volumeNow); //��vÿ���r�g�εĿ���

		// ======================̎�����pattern�ĕr�g�μ�����newIndexList������======================
		// *****���x�_�Por�ձP????????*****
		// pattern���F�ĴΔ�
		int patternCount = 0;
		int patternDivideCount = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			if (i - 2 >= 0) {
				if ((Math.abs(lowNow.get(i) - highNow.get(i)) >= spread)
						&& (volumeNow.get(i) / volumeNow.get(i - 1) >= multiple)) {
					patternIndex.add(newIndexList.get(i));
					patternNewIndex.add(i);
					patternCount++;

				} else {
					if ((Math.abs(lowNow.get(i) - highNow.get(i)) >= spread
							&& (volumeNow.get(i) + volumeNow.get(i - 1)) / volumeNow.get(i - 2) >= multiple)) {
						if(volumeNow.get(i - 1)/volumeNow.get(i - 2)<=multiple||(volumeNow.get(i - 1)/volumeNow.get(i - 2)>=multiple&&Math.abs(lowNow.get(i-1) - highNow.get(i-1)) < spread)){
							patternDivideIndex.add(newIndexList.get(i));
							patternDivideNewIndex.add(i);
							patternDivideCount++;
						}
							
						
						
					}
				}
			}

		}
		// System.out.println(patternCount);
		// System.out.println(patternDivideCount);
		// System.out.println("pattern�l���ĕr�g�γ�" + patternIndex); //
		// ��vÿ��pattern�l���ĕr�g�γ�
		// System.out.println(patternCount);//pattern���F�ĴΔ�
		// System.out.println(patternNewIndex);//��vÿ��pattern���F�ĕr�g������newIndexList������λ��
		// System.out.println(patternIndex.get(0));
		// System.out.println(newIndexList.get(40));

		// ======================̎�����keyPattern������======================

		// ****����ӑB����߃r����̓r(����S��keyPattern�r�g�{��)
		double volumeNumber = 0;
		double dynamicHigh = 0;
		double dynamicLow = 10000;

		// ******pattern��******
		for (int i = 0; i < patternCount; i++) {
			// ��pattern���F���ǿ̕r�g������һֱ�ߵ���һ���r�g��
			for (int j = patternIndex.get(i); j < newIndexList.get(patternNewIndex.get(i) + 1); j++) {
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				volumeNumber += volumeList.get(j);
				// ����ǂ��r�g��;�е������^ǰһ������multiple���҄ӑB��߃r������̓r���x�Ѓr���^spread��Ԓ�t��Ԕ���l���ĕr�g��������keyPattern
				if (volumeNumber > volumeNow.get(patternNewIndex.get(i) - 1) * multiple
						&& (dynamicHigh - priceList.get(j) >= spread || priceList.get(j) - dynamicLow >= spread)) {
					keyPattern.add(j);
					keyPatternIndex.add(patternNewIndex.get(i));
					dynamicHighPrice.add(dynamicHigh);
					dynamicLowPrice.add(dynamicLow);
					break;
				}
			}
			volumeNumber = 0;
			dynamicHigh = 0;
			dynamicLow = 10000;
		}

		// ******dividePattern��******
		ArrayList<Integer> keyDividePattern = new ArrayList<Integer>();
		ArrayList<Integer> keyDividePatternIndex = new ArrayList<Integer>();
		ArrayList<Double> dynamicDivideHighPrice = new ArrayList<Double>();
		ArrayList<Double> dynamicDivideLowPrice = new ArrayList<Double>();
		for (int i = 0; i < patternDivideCount; i++) {
			// ��dividePattern���F���ǿ̕r�g������һֱ�ߵ���һ���r�g��

			volumeNumber += volumeNow.get(patternDivideNewIndex.get(i) - 1);

			for (int j = patternDivideIndex.get(i); j < newIndexList.get(patternDivideNewIndex.get(i) + 1); j++) {
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}

				volumeNumber += volumeList.get(j);
				// System.out.println(volumeNumber);
				// ����ǂ��r�g��;�е������^ǰһ������multiple���҄ӑB��߃r������̓r���x�Ѓr���^spread��Ԓ�t��Ԕ���l���ĕr�g��������keyDividePattern
				if (volumeNumber > volumeNow.get(patternDivideNewIndex.get(i) - 2) * multiple
						&& (dynamicHigh - priceList.get(j) >= spread || priceList.get(j) - dynamicLow >= spread)) {
					keyDividePattern.add(j);
					keyDividePatternIndex.add(patternDivideNewIndex.get(i));
					dynamicDivideHighPrice.add(dynamicHigh);
					dynamicDivideLowPrice.add(dynamicLow);
					// System.out.println(volumeNow.get(patternDivideNewIndex.get(i)
					// - 2));
					// System.out.println(dynamicHigh);
					// System.out.println(dynamicLow);
					break;
				}
			}
			volumeNumber = 0;
			dynamicHigh = 0;
			dynamicLow = 10000;
		}
		// System.out.println("keyDividePattern(���_��Ηl��)�ĕr�g" +
		// keyDividePattern);// ��vÿ��keyDividePattern(���_��Ηl��)�ĕr�g
		// System.out.println("keyPattern(���_��Ηl��)�ĕr�g" + keyPattern); //
		// ��vÿ��keyPattern(���_��Ηl��)�ĕr�g
		// System.out.println(keyPatternIndex);
		// System.out.println(newIndexList.get(keyPatternIndex.get(0)+1));
		// System.out.println(dynamicHighPrice);//��vÿ���_��keyPattern�r��ǰ�o䛵���߃r
		// System.out.println(dynamicLowPrice);//��vÿ���_��keyPattern�r��ǰ�o䛵���̓r

		// ======================̎����֮��ɽ���Ԕ����r(pattern��)======================
		int countPatternVersion = 0;
		// System.out.println(priceList.get(patternIndex.get(0))); //�@ȡ�r�g�γ��ăr��
		for (int i = 0; i < keyPattern.size(); i++) {
			// ��keyPattern�_ʼһֱ���Ǹ������Y��
			// dynamicHigh = dynamicHighPrice.get(i);
			// dynamicLow = dynamicLowPrice.get(i);
			// System.out.println(dynamicHigh);
			// System.out.println(dynamicLow);
			dynamicHigh = 0;
			dynamicLow = 10000;

			for (int j = keyPattern.get(i); j < newIndexList.get(keyPatternIndex.get(i) + 1); j++) {

				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				// ���keyPatternǰ����߃r�pkeyPattern���̵ăr����spread��Ԓ���ǜʂ������A��buy��
				if (dynamicHighPrice.get(i) - priceList.get(keyPattern.get(i)) > spread
						&& priceList.get(j) <= priceList.get(keyPattern.get(i)) - markup
						&& priceList.get(patternIndex.get(i)) - priceList.get(keyPattern.get(i)) > 0) {

					buyType.add(1);
					buyIndex.add(j);
					buyPrice.add(priceList.get(keyPattern.get(i)) - markup);
					buyKeyPattern.add(keyPatternIndex.get(i));
					countPatternVersion++;
					break;
				} else if (priceList.get(keyPattern.get(i)) - dynamicLowPrice.get(i) > spread
						&& priceList.get(j) >= priceList.get(keyPattern.get(i)) + markup
						&& priceList.get(keyPattern.get(i)) - priceList.get(patternIndex.get(i)) > 0) {
					// System.out.println(dynamicHigh);
					// System.out.println(dynamicLow);
					buyType.add(2);
					buyIndex.add(j);
					// �I�M�rλpriceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyPattern.get(i)) + markup);
					buyKeyPattern.add(keyPatternIndex.get(i));
					break;
				}
			}

		}
		// System.out.println(keyPattern);
		// System.out.println(buyType); // ��v�I�M�ķN�(BUYorSELL)
		// System.out.println(buyIndex); // ��v�I�M�ĕr�g����
		// System.out.println(buyKeyPattern);// ��v�I�M�r�̵ĕr�g������

		// System.out.println(newIndexList.size());
		// System.out.println(buyIndex.size());
		// ======================̎����֮��ɽ���Ԕ����r(dividePattern��)======================
		for (int i = 0; i < keyDividePattern.size(); i++) {
			// ��keyPattern�_ʼһֱ���Ǹ������Y��
			// dynamicHigh = dynamicHighPrice.get(i);
			// dynamicLow = dynamicLowPrice.get(i);
			// System.out.println(dynamicHigh);
			// System.out.println(dynamicLow);
			dynamicHigh = 0;
			dynamicLow = 10000;
			for (int j = keyDividePattern.get(i); j < newIndexList.get(keyDividePatternIndex.get(i) + 1); j++) {
				
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				// ���keyPatternǰ����߃r�pkeyPattern���̵ăr����spread��Ԓ���ǜʂ������A��buy��
				if (dynamicDivideHighPrice.get(i) - priceList.get(keyDividePattern.get(i)) > spread
						&& priceList.get(j) <= priceList.get(keyDividePattern.get(i)) - markup
						&& priceList.get(patternDivideIndex.get(i)) - priceList.get(keyDividePattern.get(i)) > 0) {

					buyType.add(1);
					buyIndex.add(j);
					buyPrice.add(priceList.get(keyDividePattern.get(i)) - markup);
					buyKeyPattern.add(keyDividePatternIndex.get(i));
					break;
				} else if (priceList.get(keyDividePattern.get(i)) - dynamicDivideLowPrice.get(i) > spread
						&& priceList.get(j) >= priceList.get(keyDividePattern.get(i)) + markup
						&& priceList.get(keyDividePattern.get(i)) - priceList.get(patternDivideIndex.get(i)) > 0) {
					// System.out.println(dynamicHigh);
					// System.out.println(dynamicLow);
					buyType.add(2);
					buyIndex.add(j);
					// �I�M�rλpriceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyDividePattern.get(i)) + markup);
					buyKeyPattern.add(keyDividePatternIndex.get(i));
					break;
				}
			}

		}

		// ======================̎��ɽ�֮��ī@������ͣ�p��r======================

		for (int i = 0; i < buyIndex.size(); i++) {
			// newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1)
			// ��ֹ�Y�ϱ���
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			// �����buy��
			if (buyType.get(i) == 1) {
				// ���I�M������һֱ�ߵ�ͣ�p�r�g(laterNumber)������
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// ����Ѓr-�I�M�r(buy)>-firstProfit
					if (priceList.get(j) - buyPrice.get(i) >= firstProfit) {

						totalProfit += firstProfit * firstPercent / 100;
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out
									.println("[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ ",�I�M�r�g��" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "�u���r�g"
											+ timeList.get(j) + "������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@��" + firstProfit + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ ",�I�M�r�g��" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "�u���r�g"
											+ timeList.get(j) + "������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@��" + firstProfit + "�c",
									true);
						} else {
							System.out
									.println("[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ ",�I�M�r�g��" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "�u���r�g"
											+ timeList.get(j) + "������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" + firstProfit + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ ",�I�M�r�g��" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "�u���r�g"
											+ timeList.get(j) + "������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" + firstProfit + "�c",
									true);
						}

						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								System.out.println("[Buy��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" + secondProfit + "�c");
								writeTxt(workPath() + "/history.txt",
										"[Buy��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" + secondProfit + "�c", true);
								System.out.println("=============================================================");
								writeTxt(workPath() + "/history.txt",
										"=============================================================", true);
								break;
							} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
								totalProfit += (priceList.get(j) - buyPrice.get(i)) * secondPercent / 100;
								if (firstProfit * firstPercent / 100
										+ (priceList.get(j) - buyPrice.get(i)) * secondPercent / 100 > 0) {
									profitTime++;
								} else {
									lossTime++;
								}
								System.out.println("[Buy��]***ͣ�p***,��ϧ��" + timeList.get(j) + "ͣ�p,�@����"
										+ (priceList.get(j) - buyPrice.get(i)) + "�c");
								writeTxt(workPath() + "/history.txt", "[Buy��]***ͣ�p***,��ϧ��" + timeList.get(j) + "ͣ�p,�@����"
										+ (priceList.get(j) - buyPrice.get(i)) + "�c", true);
								System.out.println("=============================================================");
								writeTxt(workPath() + "/history.txt",
										"=============================================================", true);
								break;
							}

						}
						break;
					} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
						totalProfit += priceList.get(j) - buyPrice.get(i);
						if (priceList.get(j) - buyPrice.get(i) > 0) {
							profitTime++;
						} else {
							lossTime++;
						}
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@����"
									+ (priceList.get(j) - buyPrice.get(i)) + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j) + "ͣ�p,�I�M�r�g"
											+ timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g"
											+ timeList.get(j) + ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@����"
											+ (priceList.get(j) - buyPrice.get(i)) + "�c",
									true);
						} else {
							System.out.println("[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
									+ (priceList.get(j) - buyPrice.get(i)) + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j) + "ͣ�p,�I�M�r�g"
											+ timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g"
											+ timeList.get(j) + ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
											+ (priceList.get(j) - buyPrice.get(i)) + "�c",
									true);
						}

						System.out.println("=============================================================");
						writeTxt(workPath() + "/history.txt",
								"=============================================================", true);
						break;
					}
				} // �����sell��
			} else if (buyType.get(i) == 2) {
				// ���I�M������һֱ�ߵ�ͣ�p�r�g(laterNumber)������
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// ����I�M�r(sell)-�Ѓr>-firstProfit
					if (buyPrice.get(i) - priceList.get(j) >= firstProfit) {
						totalProfit += firstProfit * firstPercent / 100;
						// System.out.println("�@������"+firstProfit*firstPercent/100);
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g"
									+ timeList.get(buyIndex.get(i)) + "�u���r�g" + timeList.get(j) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "������"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@��" + firstProfit + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ "�u���r�g" + timeList.get(j) + ",�I�M�r�g��"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "������"
											+ volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@��" + firstProfit + "�c",
									true);
						} else {
							System.out.println("[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g"
									+ timeList.get(buyIndex.get(i)) + "�u���r�g" + timeList.get(j) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "������"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" + firstProfit + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ "�u���r�g" + timeList.get(j) + ",�I�M�r�g��"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "������"
											+ volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" + firstProfit + "�c",
									true);
						}

						// ��ͣ��������һֱ�ߵ�ͣ�p�r�g(laterNumber)������
						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								// System.out.println("�@������"+secondProfit*secondPercent/100);
								System.out.println("[Sell��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" + secondProfit + "�c");
								writeTxt(workPath() + "/history.txt",
										"[Sell��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" + secondProfit + "�c", true);
								System.out.println("=============================================================");
								writeTxt(workPath() + "/history.txt",
										"=============================================================", true);
								break;
							} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
								totalProfit += (buyPrice.get(i) - priceList.get(j)) * secondPercent / 100;
								// System.out.println("�@������"+(buyPrice.get(i) -
								// priceList.get(j))*secondPercent/100);
								if (firstProfit * firstPercent / 100
										+ (buyPrice.get(i) - priceList.get(j)) * secondPercent / 100 > 0) {
									profitTime++;
								} else {
									lossTime++;
								}
								System.out.println("[Sell��]***ͣ�p***,��ϧ��" + timeList.get(j) + "ͣ�p,�@����"
										+ (buyPrice.get(i) - priceList.get(j)) + "�c");
								writeTxt(workPath() + "/history.txt", "[Sell��]***ͣ�p***,��ϧ��" + timeList.get(j) + "ͣ�p,�@����"
										+ (buyPrice.get(i) - priceList.get(j)) + "�c", true);
								System.out.println("=============================================================");
								writeTxt(workPath() + "/history.txt",
										"=============================================================", true);
								break;
							}

						}
						break;
					} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
						totalProfit += buyPrice.get(i) - priceList.get(j);
						// System.out.println("�@������"+(buyPrice.get(i) -
						// priceList.get(j)));
						if (buyPrice.get(i) - priceList.get(j) > 0) {
							profitTime++;
						} else {
							lossTime++;
						}

						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@����"
									+ (buyPrice.get(i) - priceList.get(j)) + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j) + "ͣ�p,�I�M�r�g"
											+ timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g"
											+ timeList.get(j) + ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@����"
											+ (buyPrice.get(i) - priceList.get(j)) + "�c",
									true);
						} else {
							System.out.println("[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
									+ (buyPrice.get(i) - priceList.get(j)) + "�c");
							writeTxt(workPath() + "/history.txt",
									"[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j) + "ͣ�p,�I�M�r�g"
											+ timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g"
											+ timeList.get(j) + ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
											+ (buyPrice.get(i) - priceList.get(j)) + "�c",
									true);
						}

						System.out.println("=============================================================");
						writeTxt(workPath() + "/history.txt",
								"=============================================================", true);
						break;
					}
				}
			}
		}
		// System.out.println("pattern�����F" + patternCount + "�� �ɽ�" + (profitTime
		// + lossTime) + "�� ���@�� " + totalProfit
		// + " �@���Δ� " + profitTime + " ̝�p�Δ� " + lossTime + " ����"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%");
		// writeTxt(workPath() + "/history.txt",
		// "pattern�����F" + patternCount + "�� �ɽ�" + (profitTime + lossTime) + "��
		// ���@�� " + totalProfit + " �@���Δ� "
		// + profitTime + " ̝�p�Δ� " + lossTime + " ����"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%",
		// true);
		// System.out.println("���@��Ӌ�㷽ʽ�� (��һ��ͣ���c) " + firstProfit + " x
		// (��һ��ͣ���c�u���Ă}λ�ٷֱ�) " + firstPercent + "%"
		// + " + (�ڶ���ͣ���c) " + secondProfit + " x (�ڶ���ͣ���c�u���Ă}λ�ٷֱ�)" +
		// secondPercent + "%");

		// writeTxt(workPath() + "/history.txt", "���@��Ӌ�㷽ʽ�� (��һ��ͣ���c) " +
		// firstProfit + " x (��һ��ͣ���c�u���Ă}λ�ٷֱ�) " + firstPercent
		// + "%" + " + (�ڶ���ͣ���c) " + secondProfit + " x (�ڶ���ͣ���c�u���Ă}λ�ٷֱ�)" +
		// secondPercent + "%", true);

		writeTxt(workPath() + "/history.txt", "", true);

		// ======================̎�����̝�p�Пo�_��maxLoss======================
		int maxloss = 0;
		for (int i = 0; i < buyIndex.size(); i++) {
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
				// �����buy��
				if (buyType.get(i) == 1) {
					// �I�r-�Ѓr������maxLoss
					if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
						break;
					}
					if (buyPrice.get(i) - priceList.get(j) >= maxLoss) {
						maxloss++;
						writeTxt(workPath() + "/history.txt", "��" + timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i)
								+ ",�I�M�r�g" + timeList.get(buyIndex.get(i)), true);
						System.out.println("��" + timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i) + ",�I�M�r�g"
								+ timeList.get(buyIndex.get(i)));
						break;
					}
				} else if (buyType.get(i) == 2) {
					// �I�r-�Ѓr������maxLoss
					if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
						break;
					}
					if (priceList.get(j) - buyPrice.get(i) >= maxLoss) {
						maxloss++;
						writeTxt(workPath() + "/history.txt", "��" + timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i)
								+ ",�I�M�r�g" + timeList.get(buyIndex.get(i)), true);
						System.out.println("��" + timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i) + ",�I�M�r�g"
								+ timeList.get(buyIndex.get(i)));
						break;
					}
				}
			}
		}

		result.add((double) patternCount);
		result.add((double) profitTime);
		result.add((double) lossTime);
		result.add(totalProfit);
		result.add((double) maxloss);
		result.add((double) patternDivideCount);
		result.add(totalLoss);
		return result;
	}

	public static ArrayList<Double> strategyRandom9(File file, int timeRange, double spread, double markup,
			int multiple, int laterNumber, int firstProfit, int secondProfit, int maxLoss) throws IOException {
		double totalLoss = 0;
		ArrayList<Double> result = new ArrayList<Double>();
		Properties properties = new Properties();
		File fileConfig = new File(workPath() + "/config.ini");
		FileInputStream fis = new FileInputStream(fileConfig);
		properties.load(fis);
		fis.close();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		/*
		 * int timeRange =
		 * Integer.parseInt(properties.getProperty("timeRange")); double spread
		 * = Integer.parseInt(properties.getProperty("spread")); double markup =
		 * Integer.parseInt(properties.getProperty("markup")); int multiple =
		 * Integer.parseInt(properties.getProperty("multiple")); int laterNumber
		 * = Integer.parseInt(properties.getProperty("laterNumber")); int
		 * firstProfit =
		 * Integer.parseInt(properties.getProperty("firstProfit")); int
		 * secondProfit =
		 * Integer.parseInt(properties.getProperty("secondProfit")); int maxLoss
		 * = Integer.parseInt(properties.getProperty("maxLoss"));
		 */
		// ���xList����timeList�b�r�g
		List<String> timeList = new ArrayList<String>();
		// ���xList����priceList�b�ɽ��r
		List<Double> priceList = new ArrayList<Double>();
		// ���xList����volumeList�b�ɽ���
		List<Double> volumeList = new ArrayList<Double>();
		// ���xList����indexList�bÿ���r�g���_ʼ������
		List<Integer> indexList = new ArrayList<Integer>();
		// ���xList����newIndexList��indexList�е��B�m����ȡ��һ�����MnewIndexList��
		List<Integer> newIndexList = new ArrayList<Integer>();
		// ���xList����openNow�bÿ���r�g�ε��_�P�r
		List<Double> openNow = new ArrayList<Double>();
		// ���xList����highNow�bÿ���r�g�ε���߃r
		List<Double> highNow = new ArrayList<Double>();
		// ���xList����lowNow�bÿ���r�g�ε���̓r
		List<Double> lowNow = new ArrayList<Double>();
		// ���xList����closeNow�bÿ���r�g�ε��ձP�r
		List<Double> closeNow = new ArrayList<Double>();
		// ���xList����volumeNow�bÿ���r�g�εĿ���
		List<Double> volumeNow = new ArrayList<Double>();
		// ���xList����patternIndex�bpattern���F�ĕr�g�γ�������
		List<Integer> patternIndex = new ArrayList<Integer>();
		// ���xList����patternIndex�b�ɸ������ǰһ�����ǷNpattern���F�ĕr�g�γ�������
		List<Integer> patternDivideIndex = new ArrayList<Integer>();
		// ���xList����keyPattern�bpattern���F�Ĵ_�Еr�g(���_��Ηl��)
		List<Integer> keyPattern = new ArrayList<Integer>();
		// ���xList����patternNewIndex�bpattern���F�Ĵ_�Еr�g����newIndexList������λ��
		List<Integer> patternNewIndex = new ArrayList<Integer>();
		// ���xList����patternDivideNewIndex�bpattern���F�Ĵ_�Еr�g����newIndexList������λ��
		List<Integer> patternDivideNewIndex = new ArrayList<Integer>();
		// ���xList����keyPatternIndex�bkeyPattern����������newIndexList��������λ��
		List<Integer> keyPatternIndex = new ArrayList<Integer>();
		// ���xList����dynamicHighPrice�bPattern���F�r�Į�ǰ��߃r
		List<Double> dynamicHighPrice = new ArrayList<Double>();
		// ���xList����dynamicLowPrice�bPattern���F�r�Į�ǰ��̓r
		List<Double> dynamicLowPrice = new ArrayList<Double>();
		// ���xList����buyType�b�I�M�ķN�(�Նζ���)
		List<Integer> buyType = new ArrayList<Integer>();
		// ���xList����buyIndex�b�ɽ��εĕr�g����
		List<Integer> buyIndex = new ArrayList<Integer>();
		// ���xList����buyKeyPattern�b�ɽ��r�g�������ĕr�g������
		List<Integer> buyKeyPattern = new ArrayList<Integer>();
		// ���xList����buyPrice�b�I�M�Ĝʴ_�rλ
		List<Double> buyPrice = new ArrayList<Double>();

		int profitTime = 0;
		int lossTime = 0;
		double totalProfit = 0;
		double firstPercent = 50;
		double secondPercent = 50;

		// ======================̎��r�gList======================
		while ((line = br.readLine()) != null) {
			String[] item = line.split(",");
			if (isTitle(item[3])) {
				continue; // �Д��Ƿ���title
			}
			timeList.add(item[2]);
			priceList.add(Double.parseDouble(item[3]));
			volumeList.add(Double.parseDouble(item[4]));
		}
		br.close();
		// �@ȡ���Y�ϹP��
		int totalSize = timeList.size();

		// ��v���וr�g
		// System.out.println(timeList);
		// ��v�ɽ��r
		// System.out.println(priceList);
		// ��v�ɽ���
		// System.out.println(volumeList);

		// �@ȡÿ���r�g�γ�������
		int timeRangeToHour = timeRange / 60;
		for (int i = 0; i < totalSize; i++) {
			int minute = Integer.parseInt(timeList.get(i).substring(15, 17));
			int hour = Integer.parseInt(timeList.get(i).substring(12, 14));

			// System.out.println(timeList.get(i));
			if (timeRange <= 60) {
				if (minute % timeRange == 0) {
					indexList.add(i);
				}
			} else {
				if (hour % timeRangeToHour == 0 && minute % timeRange == 0) {
					indexList.add(i);
				}
			}

		}

		// �@ȡ�r�g�γ������ĹP��
		totalSize = indexList.size();

		// ��indexList�е��B�m����ȡ��һ�����MnewIndexList��
		// �Ȱ�indexList�еĵ�һ���������MnewIndexList��
		// System.out.println(indexList);
		if (indexList.isEmpty()) {
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			result.add((double) 0);
			return result;
		}

		newIndexList.add(indexList.get(0));
		for (int i = 0; i < totalSize - 1; i++) {
			if (i + 1 < totalSize - 1 && indexList.get(i + 1) - indexList.get(i) != 1) {
				newIndexList.add(indexList.get(i + 1));
			}
		}
		// System.out.println(newIndexList); // ��vÿ���r�g�γ�������

		// ======================̎���_�P�rList======================
		for (int i = 0; i < newIndexList.size(); i++) {
			openNow.add(priceList.get(newIndexList.get(i)));
		}
		// System.out.println(openNow); //��vÿ���r�g�γ����_�P�r

		// ======================̎����߃rList======================
		double maxPrice = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				if (priceList.get(j) > maxPrice) {
					maxPrice = priceList.get(j);
				}
			}
			highNow.add(maxPrice);
			maxPrice = 0;
		}
		// System.out.println(highNow); //��vÿ���r�g�ε���߃r

		// ======================̎����̓rList======================
		double minPrice = 10000;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				if (priceList.get(j) < minPrice) {
					minPrice = priceList.get(j);
				}
			}
			lowNow.add(minPrice);
			minPrice = 10000;
		}
		// System.out.println(lowNow); //��vÿ���r�g�ε���̓r

		// ======================̎���ձP�rList======================
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			closeNow.add(priceList.get(newIndexList.get(i + 1) - 1));
		}

		// System.out.println(closeNow); //��vÿ���r�g�ε��ձP�r

		// ======================̎��r�g�εĿ���List======================
		double volume = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				volume += volumeList.get(j);
			}
			volumeNow.add(volume);
			volume = 0;
		}
		// System.out.println(volumeNow); //��vÿ���r�g�εĿ���

		// ======================̎�����pattern�ĕr�g�μ�����newIndexList������======================
		// *****���x�_�Por�ձP????????*****
		// pattern���F�ĴΔ�
		int patternCount = 0;
		int patternDivideCount = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			if (i - 2 >= 0) {
				if ((Math.abs(lowNow.get(i) - highNow.get(i)) >= spread)
						&& (volumeNow.get(i) / volumeNow.get(i - 1) >= multiple)) {
					patternIndex.add(newIndexList.get(i));
					patternNewIndex.add(i);
					patternCount++;

				} else {
					if ((Math.abs(lowNow.get(i) - highNow.get(i)) >= spread
							&& (volumeNow.get(i) + volumeNow.get(i - 1)) / volumeNow.get(i - 2) >= multiple)) {
						if((volumeNow.get(i - 1)/volumeNow.get(i - 2)<=multiple)||(volumeNow.get(i - 1)/volumeNow.get(i - 2)>=multiple&&Math.abs(lowNow.get(i-1) - highNow.get(i-1)) < spread)){
							patternDivideIndex.add(newIndexList.get(i));
							patternDivideNewIndex.add(i);
							patternDivideCount++;
						}
			
					}
				}
			}

		}
		// System.out.println(patternCount);
		// System.out.println(patternDivideCount);
		// System.out.println("pattern�l���ĕr�g�γ�" + patternIndex); //
		// ��vÿ��pattern�l���ĕr�g�γ�
		// System.out.println(patternCount);//pattern���F�ĴΔ�
		// System.out.println(patternNewIndex);//��vÿ��pattern���F�ĕr�g������newIndexList������λ��
		// System.out.println(patternIndex.get(0));
		// System.out.println(newIndexList.get(40));

		// ======================̎�����keyPattern������======================

		// ****����ӑB����߃r����̓r(����S��keyPattern�r�g�{��)
		double volumeNumber = 0;
		double dynamicHigh = 0;
		double dynamicLow = 10000;

		// ******pattern��******
		for (int i = 0; i < patternCount; i++) {
			// ��pattern���F���ǿ̕r�g������һֱ�ߵ���һ���r�g��
			for (int j = patternIndex.get(i); j < newIndexList.get(patternNewIndex.get(i) + 1); j++) {
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				volumeNumber += volumeList.get(j);
				// ����ǂ��r�g��;�е������^ǰһ������multiple���҄ӑB��߃r������̓r���x�Ѓr���^spread��Ԓ�t��Ԕ���l���ĕr�g��������keyPattern
				if (volumeNumber > volumeNow.get(patternNewIndex.get(i) - 1) * multiple
						&& (dynamicHigh - priceList.get(j) >= spread || priceList.get(j) - dynamicLow >= spread)) {
					keyPattern.add(j);
					keyPatternIndex.add(patternNewIndex.get(i));
					dynamicHighPrice.add(dynamicHigh);
					dynamicLowPrice.add(dynamicLow);
					break;
				}
			}
			volumeNumber = 0;
			dynamicHigh = 0;
			dynamicLow = 10000;
		}

		// ******dividePattern��******
		ArrayList<Integer> keyDividePattern = new ArrayList<Integer>();
		ArrayList<Integer> keyDividePatternIndex = new ArrayList<Integer>();
		ArrayList<Double> dynamicDivideHighPrice = new ArrayList<Double>();
		ArrayList<Double> dynamicDivideLowPrice = new ArrayList<Double>();
		for (int i = 0; i < patternDivideCount; i++) {
			// ��dividePattern���F���ǿ̕r�g������һֱ�ߵ���һ���r�g��

			volumeNumber += volumeNow.get(patternDivideNewIndex.get(i) - 1);

			for (int j = patternDivideIndex.get(i); j < newIndexList.get(patternDivideNewIndex.get(i) + 1); j++) {
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}

				volumeNumber += volumeList.get(j);
				// System.out.println(volumeNumber);
				// ����ǂ��r�g��;�е������^ǰһ������multiple���҄ӑB��߃r������̓r���x�Ѓr���^spread��Ԓ�t��Ԕ���l���ĕr�g��������keyDividePattern
				if (volumeNumber > volumeNow.get(patternDivideNewIndex.get(i) - 2) * multiple
						&& (dynamicHigh - priceList.get(j) >= spread || priceList.get(j) - dynamicLow >= spread)) {
					keyDividePattern.add(j);
					keyDividePatternIndex.add(patternDivideNewIndex.get(i));
					dynamicDivideHighPrice.add(dynamicHigh);
					dynamicDivideLowPrice.add(dynamicLow);
					// System.out.println(volumeNow.get(patternDivideNewIndex.get(i)
					// - 2));
					// System.out.println(dynamicHigh);
					// System.out.println(dynamicLow);
					break;
				}
			}
			volumeNumber = 0;
			dynamicHigh = 0;
			dynamicLow = 10000;
		}
		// System.out.println("keyDividePattern(���_��Ηl��)�ĕr�g" +
		// keyDividePattern);// ��vÿ��keyDividePattern(���_��Ηl��)�ĕr�g
		// System.out.println("keyPattern(���_��Ηl��)�ĕr�g" + keyPattern); //
		// ��vÿ��keyPattern(���_��Ηl��)�ĕr�g
		// System.out.println(keyPatternIndex);
		// System.out.println(newIndexList.get(keyPatternIndex.get(0)+1));
		// System.out.println(dynamicHighPrice);//��vÿ���_��keyPattern�r��ǰ�o䛵���߃r
		// System.out.println(dynamicLowPrice);//��vÿ���_��keyPattern�r��ǰ�o䛵���̓r

		// ======================̎����֮��ɽ���Ԕ����r(pattern��)======================
		// System.out.println(priceList.get(patternIndex.get(0))); //�@ȡ�r�g�γ��ăr��
		int countPatternVersion = 0;
		for (int i = 0; i < keyPattern.size(); i++) {
			// ��keyPattern�_ʼһֱ���Ǹ������Y��
			// dynamicHigh = dynamicHighPrice.get(i);
			// dynamicLow = dynamicLowPrice.get(i);
			// System.out.println(dynamicHigh);
			// System.out.println(dynamicLow);
			dynamicHigh = 0;
			dynamicLow = 10000;
			for (int j = keyPattern.get(i); j < newIndexList.get(keyPatternIndex.get(i) + 1); j++) {

				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				// ���keyPatternǰ����߃r�pkeyPattern���̵ăr����spread��Ԓ���ǜʂ������A��buy��
				if (dynamicHighPrice.get(i) - priceList.get(keyPattern.get(i)) > spread
						&& priceList.get(j) <= priceList.get(keyPattern.get(i)) - markup
						&& priceList.get(patternIndex.get(i)) - priceList.get(keyPattern.get(i)) > 0) {

					buyType.add(1);
					buyIndex.add(j);
					buyPrice.add(priceList.get(keyPattern.get(i)) - markup);
					buyKeyPattern.add(keyPatternIndex.get(i));
					countPatternVersion++;
					break;
				} else if (priceList.get(keyPattern.get(i)) - dynamicLowPrice.get(i) > spread
						&& priceList.get(j) >= priceList.get(keyPattern.get(i)) + markup
						&& priceList.get(keyPattern.get(i)) - priceList.get(patternIndex.get(i)) > 0) {
					// System.out.println(dynamicHigh);
					// System.out.println(dynamicLow);
					buyType.add(2);
					buyIndex.add(j);
					// �I�M�rλpriceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyPattern.get(i)) + markup);
					buyKeyPattern.add(keyPatternIndex.get(i));
					break;
				}
			}

		}
		// System.out.println(buyIndex.size());
		// System.out.println(keyPattern);
		// System.out.println(buyType); // ��v�I�M�ķN�(BUYorSELL)
		// System.out.println(buyIndex); // ��v�I�M�ĕr�g����
		// System.out.println(buyKeyPattern);// ��v�I�M�r�̵ĕr�g������

		// System.out.println(newIndexList.size());

		// ======================̎����֮��ɽ���Ԕ����r(dividePattern��)======================
		for (int i = 0; i < keyDividePattern.size(); i++) {
			// ��keyPattern�_ʼһֱ���Ǹ������Y��
			// dynamicHigh = dynamicHighPrice.get(i);
			// dynamicLow = dynamicLowPrice.get(i);
			// System.out.println(dynamicHigh);
			// System.out.println(dynamicLow);
			dynamicHigh = 0;
			dynamicLow = 10000;
			for (int j = keyDividePattern.get(i); j < newIndexList.get(keyDividePatternIndex.get(i) + 1); j++) {

				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				// ���keyPatternǰ����߃r�pkeyPattern���̵ăr����spread��Ԓ���ǜʂ������A��buy��
				if (dynamicDivideHighPrice.get(i) - priceList.get(keyDividePattern.get(i)) > spread
						&& priceList.get(j) <= priceList.get(keyDividePattern.get(i)) - markup
						&& priceList.get(patternDivideIndex.get(i)) - priceList.get(keyDividePattern.get(i)) > 0) {

					buyType.add(1);
					buyIndex.add(j);
					buyPrice.add(priceList.get(keyDividePattern.get(i)) - markup);
					buyKeyPattern.add(keyDividePatternIndex.get(i));
					break;
				} else if (priceList.get(keyDividePattern.get(i)) - dynamicDivideLowPrice.get(i) > spread
						&& priceList.get(j) >= priceList.get(keyDividePattern.get(i)) + markup
						&& priceList.get(keyDividePattern.get(i)) - priceList.get(patternDivideIndex.get(i)) > 0) {
					// System.out.println(dynamicHigh);
					// System.out.println(dynamicLow);
					buyType.add(2);
					buyIndex.add(j);
					// �I�M�rλpriceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyDividePattern.get(i)) + markup);
					buyKeyPattern.add(keyDividePatternIndex.get(i));
					break;
				}
			}

		}
		// System.out.println(buyIndex.size());
		// ======================̎��ɽ�֮��ī@������ͣ�p��r======================

		for (int i = 0; i < buyIndex.size(); i++) {
			// newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1)
			// ��ֹ�Y�ϱ���
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			// �����buy��
			if (buyType.get(i) == 1) {
				// ���I�M������һֱ�ߵ�ͣ�p�r�g(laterNumber)������
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// ����Ѓr-�I�M�r(buy)>-firstProfit
					if (priceList.get(j) - buyPrice.get(i) >= firstProfit) {

						totalProfit += firstProfit * firstPercent / 100;
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out
									.println("[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ ",�I�M�r�g��" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "�u���r�g"
											+ timeList.get(j) + "������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@��" + firstProfit + "�c");
						} else {
							System.out
									.println("[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" + timeList.get(buyIndex.get(i))
											+ ",�I�M�r�g��" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "�u���r�g"
											+ timeList.get(j) + "������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" + firstProfit + "�c");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Buy��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" +
						// timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "�u���r�g"
						// + timeList.get(j) + "������" +
						// volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" +
						// firstProfit + "�c",
						// true);
						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								System.out.println("[Buy��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" + secondProfit + "�c");
								// writeTxt(workPath() + "/history.txt",
								// "[Buy��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" +
								// secondProfit + "�c", true);
								System.out.println("=============================================================");
								// writeTxt(workPath() + "/history.txt",
								// "=============================================================",
								// true);
								break;
							} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
								totalProfit += (priceList.get(j) - buyPrice.get(i)) * secondPercent / 100;
								if (firstProfit * firstPercent / 100
										+ (priceList.get(j) - buyPrice.get(i)) * secondPercent / 100 > 0) {
									profitTime++;
								} else {
									lossTime++;
								}

								// [�pʧ���~]
								if ((priceList.get(j) - buyPrice.get(i)) < 0) {
									totalLoss += (priceList.get(j) - buyPrice.get(i)) * secondPercent / 100;
								}
								System.out.println("[Buy��]***ͣ�p***,��ϧ��" + timeList.get(j) + "ͣ�p,�@����"
										+ (priceList.get(j) - buyPrice.get(i)) + "�c");
								// writeTxt(workPath() + "/history.txt",
								// "[Buy��]***ͣ�p***,��ϧ��" + timeList.get(j) +
								// "ͣ�p,�@����"
								// + (priceList.get(j) - buyPrice.get(i)) + "�c",
								// true);
								System.out.println("=============================================================");
								// writeTxt(workPath() + "/history.txt",
								// "=============================================================",
								// true);
								break;
							}

						}
						break;
					} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
						totalProfit += priceList.get(j) - buyPrice.get(i);
						if (priceList.get(j) - buyPrice.get(i) > 0) {
							profitTime++;
						} else {
							lossTime++;
						}
						// [�pʧ���~]
						if ((priceList.get(j) - buyPrice.get(i)) < 0) {
							totalLoss += (priceList.get(j) - buyPrice.get(i)) * 1;
						}
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@����"
									+ (priceList.get(j) - buyPrice.get(i)) + "�c");
						} else {
							System.out.println("[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
									+ (priceList.get(j) - buyPrice.get(i)) + "�c");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Buy��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��"
						// + priceList.get(j) + "ͣ�p,�I�M�r�g"
						// + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "ͣ�p�r�g"
						// + timeList.get(j) + ",������" +
						// volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
						// + (priceList.get(j) - buyPrice.get(i)) + "�c",
						// true);
						System.out.println("=============================================================");
						// writeTxt(workPath() + "/history.txt",
						// "=============================================================",
						// true);
						break;
					}
				} // �����sell��
			} else if (buyType.get(i) == 2) {
				// ���I�M������һֱ�ߵ�ͣ�p�r�g(laterNumber)������
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// ����I�M�r(sell)-�Ѓr>-firstProfit
					if (buyPrice.get(i) - priceList.get(j) >= firstProfit) {
						totalProfit += firstProfit * firstPercent / 100;
						// System.out.println("�@������"+firstProfit*firstPercent/100);
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g"
									+ timeList.get(buyIndex.get(i)) + "�u���r�g" + timeList.get(j) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "������"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@��" + firstProfit + "�c");
						} else {
							System.out.println("[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g"
									+ timeList.get(buyIndex.get(i)) + "�u���r�g" + timeList.get(j) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "������"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" + firstProfit + "�c");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Sell��]��ϲ��,��" + buyPrice.get(i) + "�I�M,�I�M�r�g" +
						// timeList.get(buyIndex.get(i)) + "�u���r�g"
						// + timeList.get(j) + ",�I�M�r�g��"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "������"
						// + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@��" +
						// firstProfit + "�c",
						// true);
						// ��ͣ��������һֱ�ߵ�ͣ�p�r�g(laterNumber)������
						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								// System.out.println("�@������"+secondProfit*secondPercent/100);
								System.out.println("[Sell��]��ϲ��,��" + timeList.get(j) + "���u����,�@��" + secondProfit + "�c");
								// writeTxt(workPath() + "/history.txt",
								// "[Sell��]��ϲ��,��" + timeList.get(j) + "���u����,�@��"
								// + secondProfit + "�c", true);
								System.out.println("=============================================================");
								// writeTxt(workPath() + "/history.txt",
								// "=============================================================",
								// true);
								break;
							} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
								totalProfit += (buyPrice.get(i) - priceList.get(j)) * (secondPercent / 100);
								// System.out.println("�@������"+(buyPrice.get(i) -
								// priceList.get(j))*secondPercent/100);
								if (firstProfit * firstPercent / 100
										+ (buyPrice.get(i) - priceList.get(j)) * secondPercent / 100 > 0) {
									profitTime++;
								} else {
									lossTime++;
								}

								if ((buyPrice.get(i) - priceList.get(j)) < 0) {
									totalLoss += (buyPrice.get(i) - priceList.get(j)) * (secondPercent / 100);
								}
								System.out.println("[Sell��]***ͣ�p***,��ϧ��" + timeList.get(j) + "ͣ�p,�@����"
										+ (buyPrice.get(i) - priceList.get(j)) + "�c");
								// writeTxt(workPath() + "/history.txt",
								// "[Sell��]***ͣ�p***,��ϧ��" + timeList.get(j) +
								// "ͣ�p,�@����"
								// + (buyPrice.get(i) - priceList.get(j)) + "�c",
								// true);
								System.out.println("=============================================================");
								// writeTxt(workPath() + "/history.txt",
								// "=============================================================",
								// true);
								break;
							}

						}
						break;
					} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
						totalProfit += buyPrice.get(i) - priceList.get(j);
						// System.out.println("�@������"+(buyPrice.get(i) -
						// priceList.get(j)));
						if (buyPrice.get(i) - priceList.get(j) > 0) {
							profitTime++;
						} else {
							lossTime++;
						}
						if ((buyPrice.get(i) - priceList.get(j)) < 0) {
							totalLoss += (buyPrice.get(i) - priceList.get(j)) * 1;
						}

						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " ǰǰ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",�@����"
									+ (buyPrice.get(i) - priceList.get(j)) + "�c");
						} else {
							System.out.println("[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��" + priceList.get(j)
									+ "ͣ�p,�I�M�r�g" + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "ͣ�p�r�g" + timeList.get(j)
									+ ",������" + volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
									+ (buyPrice.get(i) - priceList.get(j)) + "�c");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Sell��]***ͣ�p***,��" + buyPrice.get(i) + "�I�M" + ",��ϧ��"
						// + priceList.get(j) + "ͣ�p,�I�M�r�g"
						// + timeList.get(buyIndex.get(i)) + ",�I�M�r�g��"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "ͣ�p�r�g"
						// + timeList.get(j) + ",������" +
						// volumeNow.get(buyKeyPattern.get(i)) + ",ǰһ����"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",�@����"
						// + (buyPrice.get(i) - priceList.get(j)) + "�c",
						// true);
						System.out.println("=============================================================");
						// writeTxt(workPath() + "/history.txt",
						// "=============================================================",
						// true);
						break;
					}
				}
			}
		}
		// System.out.println("pattern�����F" + patternCount + "�� �ɽ�" + (profitTime
		// + lossTime) + "�� ���@�� " + totalProfit
		// + " �@���Δ� " + profitTime + " ̝�p�Δ� " + lossTime + " ����"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%");
		// writeTxt(workPath() + "/history.txt",
		// "pattern�����F" + patternCount + "�� �ɽ�" + (profitTime + lossTime) + "��
		// ���@�� " + totalProfit + " �@���Δ� "
		// + profitTime + " ̝�p�Δ� " + lossTime + " ����"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%",
		// true);
		// System.out.println("���@��Ӌ�㷽ʽ�� (��һ��ͣ���c) " + firstProfit + " x
		// (��һ��ͣ���c�u���Ă}λ�ٷֱ�) " + firstPercent + "%"
		// + " + (�ڶ���ͣ���c) " + secondProfit + " x (�ڶ���ͣ���c�u���Ă}λ�ٷֱ�)" +
		// secondPercent + "%");

		// writeTxt(workPath() + "/history.txt", "���@��Ӌ�㷽ʽ�� (��һ��ͣ���c) " +
		// firstProfit + " x (��һ��ͣ���c�u���Ă}λ�ٷֱ�) " + firstPercent
		// + "%" + " + (�ڶ���ͣ���c) " + secondProfit + " x (�ڶ���ͣ���c�u���Ă}λ�ٷֱ�)" +
		// secondPercent + "%", true);

		// writeTxt(workPath() + "/history.txt", "", true);

		// ======================̎�����̝�p�Пo�_��maxLoss======================
		int maxloss = 0;
		for (int i = 0; i < buyIndex.size(); i++) {
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
				// �����buy��
				if (buyType.get(i) == 1) {
					// �I�r-�Ѓr������maxLoss
					if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
						break;
					}
					if (buyPrice.get(i) - priceList.get(j) >= maxLoss) {
						maxloss++;
						// writeTxt(workPath() + "/history.txt", "��" +
						// timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i)
						// + ",�I�M�r�g" + timeList.get(buyIndex.get(i)), true);
						System.out.println("��" + timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i) + ",�I�M�r�g"
								+ timeList.get(buyIndex.get(i)));
						break;
					}
				} else if (buyType.get(i) == 2) {
					// �I�r-�Ѓr������maxLoss
					if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
						break;
					}
					if (priceList.get(j) - buyPrice.get(i) >= maxLoss) {
						maxloss++;
						// writeTxt(workPath() + "/history.txt", "��" +
						// timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i)
						// + ",�I�M�r�g" + timeList.get(buyIndex.get(i)), true);
						System.out.println("��" + timeList.get(j) + "�_���A�O���pʧ�c,�I�M�rλ" + buyPrice.get(i) + ",�I�M�r�g"
								+ timeList.get(buyIndex.get(i)));
						break;
					}
				}
			}
		}

		result.add((double) patternCount);
		result.add((double) profitTime);
		result.add((double) lossTime);
		result.add(totalProfit);
		result.add((double) maxloss);
		result.add((double) patternDivideCount);
		result.add(totalLoss);
		return result;
	}

	public static String workPath() {
		return System.getProperty("user.dir");
	}

	public static void writeTxt(String filePath, String writeString, boolean noCover) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, noCover), "UTF-8"));
			out.write(writeString + "\r\n"); // ����
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close(); // �P�]�YԴ
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isTitle(String str) {
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
