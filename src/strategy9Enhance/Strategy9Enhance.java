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

//必須獲取每個時間段初的索引
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

		 writeTxt(workPath() + "/history.txt", "pattern共出現" + (int)patternCount + "次其中2根量大於前1根的情況有"+(int)patternDivideCount+"次 "+(int)patternCount+" 次中成交"
		 + (int)(profitTime + lossTime) + "次 總獲利 " + totalProfit + " 獲利次數 "
		+ (int)profitTime + " 虧損次數 " + (int)lossTime + " 勝率"
		 + ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "% 達到最大虧損"+maxloss+"次"+"  把產生虧損的單子全部相加所得的虧損總額為"+totalLoss+"點",
		 true);
		System.out.println("pattern共出現" + (int) patternCount + "次 其中2根量大於前1根的情況有" + (int) patternDivideCount + "次  "
				+ (int) patternCount + "次中成交" + (int) (profitTime + lossTime) + "次  總獲利 " + totalProfit + " 獲利次數 "
				+ (int) profitTime + " 虧損次數 " + (int) lossTime + " 勝率"
				+ ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "%  達到最大虧損" + maxloss + "次"+"  把產生虧損的單子全部相加所得的虧損總額為"+totalLoss+"點");

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
		// 定義List集合timeList裝時間
		List<String> timeList = new ArrayList<String>();
		// 定義List集合priceList裝成交價
		List<Double> priceList = new ArrayList<Double>();
		// 定義List集合volumeList裝成交量
		List<Double> volumeList = new ArrayList<Double>();
		// 定義List集合indexList裝每個時間段開始的索引
		List<Integer> indexList = new ArrayList<Integer>();
		// 定義List集合newIndexList把indexList中的連續數字取第一個放進newIndexList中
		List<Integer> newIndexList = new ArrayList<Integer>();
		// 定義List集合openNow裝每個時間段的開盤價
		List<Double> openNow = new ArrayList<Double>();
		// 定義List集合highNow裝每個時間段的最高價
		List<Double> highNow = new ArrayList<Double>();
		// 定義List集合lowNow裝每個時間段的最低價
		List<Double> lowNow = new ArrayList<Double>();
		// 定義List集合closeNow裝每個時間段的收盤價
		List<Double> closeNow = new ArrayList<Double>();
		// 定義List集合volumeNow裝每個時間段的總量
		List<Double> volumeNow = new ArrayList<Double>();
		// 定義List集合patternIndex裝pattern出現的時間段初的索引
		List<Integer> patternIndex = new ArrayList<Integer>();
		// 定義List集合patternIndex裝兩根量大於前一根的那種pattern出現的時間段初的索引
		List<Integer> patternDivideIndex = new ArrayList<Integer>();
		// 定義List集合keyPattern裝pattern出現的確切時間(已達掛單條件)
		List<Integer> keyPattern = new ArrayList<Integer>();
		// 定義List集合patternNewIndex裝pattern出現的確切時間對應newIndexList的索引位置
		List<Integer> patternNewIndex = new ArrayList<Integer>();
		// 定義List集合patternDivideNewIndex裝pattern出現的確切時間對應newIndexList的索引位置
		List<Integer> patternDivideNewIndex = new ArrayList<Integer>();
		// 定義List集合keyPatternIndex裝keyPattern索引所對應newIndexList的索引初位置
		List<Integer> keyPatternIndex = new ArrayList<Integer>();
		// 定義List集合dynamicHighPrice裝Pattern出現時的當前最高價
		List<Double> dynamicHighPrice = new ArrayList<Double>();
		// 定義List集合dynamicLowPrice裝Pattern出現時的當前最低價
		List<Double> dynamicLowPrice = new ArrayList<Double>();
		// 定義List集合buyType裝買進的種類(空單多單)
		List<Integer> buyType = new ArrayList<Integer>();
		// 定義List集合buyIndex裝成交單的時間索引
		List<Integer> buyIndex = new ArrayList<Integer>();
		// 定義List集合buyKeyPattern裝成交時間所對應的時間初索引
		List<Integer> buyKeyPattern = new ArrayList<Integer>();
		// 定義List集合buyPrice裝買進的準確價位
		List<Double> buyPrice = new ArrayList<Double>();

		int profitTime = 0;
		int lossTime = 0;
		double totalProfit = 0;
		double totalLoss = 0;
		double firstPercent = 50;
		double secondPercent = 50;

		// ======================處理時間List======================
		while ((line = br.readLine()) != null) {
			String[] item = line.split(",");
			if (isTitle(item[3])) {
				continue; // 判斷是否有title
			}
			timeList.add(item[2]);
			priceList.add(Double.parseDouble(item[3]));
			volumeList.add(Double.parseDouble(item[4]));
		}
		br.close();
		// 獲取總資料筆數
		int totalSize = timeList.size();

		// 遍歷交易時間
		// System.out.println(timeList);
		// 遍歷成交價
		// System.out.println(priceList);
		// 遍歷成交量
		// System.out.println(volumeList);

		// 獲取每個時間段初的索引
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

		// 獲取時間段初索引的筆數
		totalSize = indexList.size();

		// 把indexList中的連續數字取第一個放進newIndexList中
		// 先把indexList中的第一個索引放進newIndexList中
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
		// System.out.println(newIndexList); // 遍歷每個時間段初的索引

		// ======================處理開盤價List======================
		for (int i = 0; i < newIndexList.size(); i++) {
			openNow.add(priceList.get(newIndexList.get(i)));
		}
		// System.out.println(openNow); //遍歷每個時間段初的開盤價

		// ======================處理最高價List======================
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
		// System.out.println(highNow); //遍歷每個時間段的最高價

		// ======================處理最低價List======================
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
		// System.out.println(lowNow); //遍歷每個時間段的最低價

		// ======================處理收盤價List======================
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			closeNow.add(priceList.get(newIndexList.get(i + 1) - 1));
		}

		// System.out.println(closeNow); //遍歷每個時間段的收盤價

		// ======================處理時間段的總量List======================
		double volume = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				volume += volumeList.get(j);
			}
			volumeNow.add(volume);
			volume = 0;
		}
		// System.out.println(volumeNow); //遍歷每個時間段的總量

		// ======================處理符合pattern的時間段及對應newIndexList的索引======================
		// *****定義開盤or收盤????????*****
		// pattern出現的次數
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
		// System.out.println("pattern發生的時間段初" + patternIndex); //
		// 遍歷每個pattern發生的時間段初
		// System.out.println(patternCount);//pattern出現的次數
		// System.out.println(patternNewIndex);//遍歷每個pattern出現的時間對應的newIndexList的索引位置
		// System.out.println(patternIndex.get(0));
		// System.out.println(newIndexList.get(40));

		// ======================處理符合keyPattern的索引======================

		// ****加入動態的最高價及最低價(必須隨著keyPattern時間調整)
		double volumeNumber = 0;
		double dynamicHigh = 0;
		double dynamicLow = 10000;

		// ******pattern版******
		for (int i = 0; i < patternCount; i++) {
			// 從pattern出現的那刻時間段索引一直掃到下一個時間段
			for (int j = patternIndex.get(i); j < newIndexList.get(patternNewIndex.get(i) + 1); j++) {
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				volumeNumber += volumeList.get(j);
				// 如果那個時間段途中的量超過前一根量的multiple倍且動態最高價或是最低價距離市價超過spread的話則把詳細發生的時間索引加入keyPattern
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

		// ******dividePattern版******
		ArrayList<Integer> keyDividePattern = new ArrayList<Integer>();
		ArrayList<Integer> keyDividePatternIndex = new ArrayList<Integer>();
		ArrayList<Double> dynamicDivideHighPrice = new ArrayList<Double>();
		ArrayList<Double> dynamicDivideLowPrice = new ArrayList<Double>();
		for (int i = 0; i < patternDivideCount; i++) {
			// 從dividePattern出現的那刻時間段索引一直掃到下一個時間段

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
				// 如果那個時間段途中的量超過前一根量的multiple倍且動態最高價或是最低價距離市價超過spread的話則把詳細發生的時間索引加入keyDividePattern
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
		// System.out.println("keyDividePattern(已達掛單條件)的時間" +
		// keyDividePattern);// 遍歷每個keyDividePattern(已達掛單條件)的時間
		// System.out.println("keyPattern(已達掛單條件)的時間" + keyPattern); //
		// 遍歷每個keyPattern(已達掛單條件)的時間
		// System.out.println(keyPatternIndex);
		// System.out.println(newIndexList.get(keyPatternIndex.get(0)+1));
		// System.out.println(dynamicHighPrice);//遍歷每個達到keyPattern時當前紀錄的最高價
		// System.out.println(dynamicLowPrice);//遍歷每個達到keyPattern時當前紀錄的最低價

		// ======================處理掛單之後成交的詳細情況(pattern版)======================
		int countPatternVersion = 0;
		// System.out.println(priceList.get(patternIndex.get(0))); //獲取時間段初的價格
		for (int i = 0; i < keyPattern.size(); i++) {
			// 從keyPattern開始一直到那根索引結束
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
				// 如果keyPattern前的最高價減keyPattern當刻的價差大於spread的話就是準備向下預掛buy單
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
					// 買進價位priceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyPattern.get(i)) + markup);
					buyKeyPattern.add(keyPatternIndex.get(i));
					break;
				}
			}

		}
		// System.out.println(keyPattern);
		// System.out.println(buyType); // 遍歷買進的種類(BUYorSELL)
		// System.out.println(buyIndex); // 遍歷買進的時間索引
		// System.out.println(buyKeyPattern);// 遍歷買進時刻的時間初索引

		// System.out.println(newIndexList.size());
		// System.out.println(buyIndex.size());
		// ======================處理掛單之後成交的詳細情況(dividePattern版)======================
		for (int i = 0; i < keyDividePattern.size(); i++) {
			// 從keyPattern開始一直到那根索引結束
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
				// 如果keyPattern前的最高價減keyPattern當刻的價差大於spread的話就是準備向下預掛buy單
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
					// 買進價位priceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyDividePattern.get(i)) + markup);
					buyKeyPattern.add(keyDividePatternIndex.get(i));
					break;
				}
			}

		}

		// ======================處理成交之後的獲利或是停損情況======================

		for (int i = 0; i < buyIndex.size(); i++) {
			// newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1)
			// 防止資料爆掉
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			// 如果是buy單
			if (buyType.get(i) == 1) {
				// 從買進的索引一直掃到停損時間(laterNumber)的索引
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// 如果市價-買進價(buy)>-firstProfit
					if (priceList.get(j) - buyPrice.get(i) >= firstProfit) {

						totalProfit += firstProfit * firstPercent / 100;
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out
									.println("[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ ",買進時間段" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "賣出時間"
											+ timeList.get(j) + "當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利" + firstProfit + "點");
							writeTxt(workPath() + "/history.txt",
									"[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ ",買進時間段" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "賣出時間"
											+ timeList.get(j) + "當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利" + firstProfit + "點",
									true);
						} else {
							System.out
									.println("[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ ",買進時間段" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "賣出時間"
											+ timeList.get(j) + "當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" + firstProfit + "點");
							writeTxt(workPath() + "/history.txt",
									"[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ ",買進時間段" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "賣出時間"
											+ timeList.get(j) + "當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" + firstProfit + "點",
									true);
						}

						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								System.out.println("[Buy單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" + secondProfit + "點");
								writeTxt(workPath() + "/history.txt",
										"[Buy單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" + secondProfit + "點", true);
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
								System.out.println("[Buy單]***停損***,可惜在" + timeList.get(j) + "停損,獲利為"
										+ (priceList.get(j) - buyPrice.get(i)) + "點");
								writeTxt(workPath() + "/history.txt", "[Buy單]***停損***,可惜在" + timeList.get(j) + "停損,獲利為"
										+ (priceList.get(j) - buyPrice.get(i)) + "點", true);
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
							System.out.println("[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利為"
									+ (priceList.get(j) - buyPrice.get(i)) + "點");
							writeTxt(workPath() + "/history.txt",
									"[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j) + "停損,買進時間"
											+ timeList.get(buyIndex.get(i)) + ",買進時間段"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間"
											+ timeList.get(j) + ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利為"
											+ (priceList.get(j) - buyPrice.get(i)) + "點",
									true);
						} else {
							System.out.println("[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
									+ (priceList.get(j) - buyPrice.get(i)) + "點");
							writeTxt(workPath() + "/history.txt",
									"[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j) + "停損,買進時間"
											+ timeList.get(buyIndex.get(i)) + ",買進時間段"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間"
											+ timeList.get(j) + ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
											+ (priceList.get(j) - buyPrice.get(i)) + "點",
									true);
						}

						System.out.println("=============================================================");
						writeTxt(workPath() + "/history.txt",
								"=============================================================", true);
						break;
					}
				} // 如果是sell單
			} else if (buyType.get(i) == 2) {
				// 從買進的索引一直掃到停損時間(laterNumber)的索引
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// 如果買進價(sell)-市價>-firstProfit
					if (buyPrice.get(i) - priceList.get(j) >= firstProfit) {
						totalProfit += firstProfit * firstPercent / 100;
						// System.out.println("獲利增加"+firstProfit*firstPercent/100);
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間"
									+ timeList.get(buyIndex.get(i)) + "賣出時間" + timeList.get(j) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "當根量"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利" + firstProfit + "點");
							writeTxt(workPath() + "/history.txt",
									"[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ "賣出時間" + timeList.get(j) + ",買進時間段"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "當根量"
											+ volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利" + firstProfit + "點",
									true);
						} else {
							System.out.println("[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間"
									+ timeList.get(buyIndex.get(i)) + "賣出時間" + timeList.get(j) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "當根量"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" + firstProfit + "點");
							writeTxt(workPath() + "/history.txt",
									"[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ "賣出時間" + timeList.get(j) + ",買進時間段"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "當根量"
											+ volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" + firstProfit + "點",
									true);
						}

						// 從停利的索引一直掃到停損時間(laterNumber)的索引
						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								// System.out.println("獲利增加"+secondProfit*secondPercent/100);
								System.out.println("[Sell單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" + secondProfit + "點");
								writeTxt(workPath() + "/history.txt",
										"[Sell單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" + secondProfit + "點", true);
								System.out.println("=============================================================");
								writeTxt(workPath() + "/history.txt",
										"=============================================================", true);
								break;
							} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
								totalProfit += (buyPrice.get(i) - priceList.get(j)) * secondPercent / 100;
								// System.out.println("獲利增加"+(buyPrice.get(i) -
								// priceList.get(j))*secondPercent/100);
								if (firstProfit * firstPercent / 100
										+ (buyPrice.get(i) - priceList.get(j)) * secondPercent / 100 > 0) {
									profitTime++;
								} else {
									lossTime++;
								}
								System.out.println("[Sell單]***停損***,可惜在" + timeList.get(j) + "停損,獲利為"
										+ (buyPrice.get(i) - priceList.get(j)) + "點");
								writeTxt(workPath() + "/history.txt", "[Sell單]***停損***,可惜在" + timeList.get(j) + "停損,獲利為"
										+ (buyPrice.get(i) - priceList.get(j)) + "點", true);
								System.out.println("=============================================================");
								writeTxt(workPath() + "/history.txt",
										"=============================================================", true);
								break;
							}

						}
						break;
					} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
						totalProfit += buyPrice.get(i) - priceList.get(j);
						// System.out.println("獲利增加"+(buyPrice.get(i) -
						// priceList.get(j)));
						if (buyPrice.get(i) - priceList.get(j) > 0) {
							profitTime++;
						} else {
							lossTime++;
						}

						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利為"
									+ (buyPrice.get(i) - priceList.get(j)) + "點");
							writeTxt(workPath() + "/history.txt",
									"[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j) + "停損,買進時間"
											+ timeList.get(buyIndex.get(i)) + ",買進時間段"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間"
											+ timeList.get(j) + ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
													+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利為"
											+ (buyPrice.get(i) - priceList.get(j)) + "點",
									true);
						} else {
							System.out.println("[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
									+ (buyPrice.get(i) - priceList.get(j)) + "點");
							writeTxt(workPath() + "/history.txt",
									"[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j) + "停損,買進時間"
											+ timeList.get(buyIndex.get(i)) + ",買進時間段"
											+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間"
											+ timeList.get(j) + ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
											+ (buyPrice.get(i) - priceList.get(j)) + "點",
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
		// System.out.println("pattern共出現" + patternCount + "次 成交" + (profitTime
		// + lossTime) + "次 總獲利 " + totalProfit
		// + " 獲利次數 " + profitTime + " 虧損次數 " + lossTime + " 勝率"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%");
		// writeTxt(workPath() + "/history.txt",
		// "pattern共出現" + patternCount + "次 成交" + (profitTime + lossTime) + "次
		// 總獲利 " + totalProfit + " 獲利次數 "
		// + profitTime + " 虧損次數 " + lossTime + " 勝率"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%",
		// true);
		// System.out.println("總獲利計算方式為 (第一次停利點) " + firstProfit + " x
		// (第一次停利點賣出的倉位百分比) " + firstPercent + "%"
		// + " + (第二次停利點) " + secondProfit + " x (第二次停利點賣出的倉位百分比)" +
		// secondPercent + "%");

		// writeTxt(workPath() + "/history.txt", "總獲利計算方式為 (第一次停利點) " +
		// firstProfit + " x (第一次停利點賣出的倉位百分比) " + firstPercent
		// + "%" + " + (第二次停利點) " + secondProfit + " x (第二次停利點賣出的倉位百分比)" +
		// secondPercent + "%", true);

		writeTxt(workPath() + "/history.txt", "", true);

		// ======================處理最大虧損有無達到maxLoss======================
		int maxloss = 0;
		for (int i = 0; i < buyIndex.size(); i++) {
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
				// 如果是buy單
				if (buyType.get(i) == 1) {
					// 買價-市價如果大於maxLoss
					if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
						break;
					}
					if (buyPrice.get(i) - priceList.get(j) >= maxLoss) {
						maxloss++;
						writeTxt(workPath() + "/history.txt", "在" + timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i)
								+ ",買進時間" + timeList.get(buyIndex.get(i)), true);
						System.out.println("在" + timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i) + ",買進時間"
								+ timeList.get(buyIndex.get(i)));
						break;
					}
				} else if (buyType.get(i) == 2) {
					// 買價-市價如果大於maxLoss
					if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
						break;
					}
					if (priceList.get(j) - buyPrice.get(i) >= maxLoss) {
						maxloss++;
						writeTxt(workPath() + "/history.txt", "在" + timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i)
								+ ",買進時間" + timeList.get(buyIndex.get(i)), true);
						System.out.println("在" + timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i) + ",買進時間"
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
		// 定義List集合timeList裝時間
		List<String> timeList = new ArrayList<String>();
		// 定義List集合priceList裝成交價
		List<Double> priceList = new ArrayList<Double>();
		// 定義List集合volumeList裝成交量
		List<Double> volumeList = new ArrayList<Double>();
		// 定義List集合indexList裝每個時間段開始的索引
		List<Integer> indexList = new ArrayList<Integer>();
		// 定義List集合newIndexList把indexList中的連續數字取第一個放進newIndexList中
		List<Integer> newIndexList = new ArrayList<Integer>();
		// 定義List集合openNow裝每個時間段的開盤價
		List<Double> openNow = new ArrayList<Double>();
		// 定義List集合highNow裝每個時間段的最高價
		List<Double> highNow = new ArrayList<Double>();
		// 定義List集合lowNow裝每個時間段的最低價
		List<Double> lowNow = new ArrayList<Double>();
		// 定義List集合closeNow裝每個時間段的收盤價
		List<Double> closeNow = new ArrayList<Double>();
		// 定義List集合volumeNow裝每個時間段的總量
		List<Double> volumeNow = new ArrayList<Double>();
		// 定義List集合patternIndex裝pattern出現的時間段初的索引
		List<Integer> patternIndex = new ArrayList<Integer>();
		// 定義List集合patternIndex裝兩根量大於前一根的那種pattern出現的時間段初的索引
		List<Integer> patternDivideIndex = new ArrayList<Integer>();
		// 定義List集合keyPattern裝pattern出現的確切時間(已達掛單條件)
		List<Integer> keyPattern = new ArrayList<Integer>();
		// 定義List集合patternNewIndex裝pattern出現的確切時間對應newIndexList的索引位置
		List<Integer> patternNewIndex = new ArrayList<Integer>();
		// 定義List集合patternDivideNewIndex裝pattern出現的確切時間對應newIndexList的索引位置
		List<Integer> patternDivideNewIndex = new ArrayList<Integer>();
		// 定義List集合keyPatternIndex裝keyPattern索引所對應newIndexList的索引初位置
		List<Integer> keyPatternIndex = new ArrayList<Integer>();
		// 定義List集合dynamicHighPrice裝Pattern出現時的當前最高價
		List<Double> dynamicHighPrice = new ArrayList<Double>();
		// 定義List集合dynamicLowPrice裝Pattern出現時的當前最低價
		List<Double> dynamicLowPrice = new ArrayList<Double>();
		// 定義List集合buyType裝買進的種類(空單多單)
		List<Integer> buyType = new ArrayList<Integer>();
		// 定義List集合buyIndex裝成交單的時間索引
		List<Integer> buyIndex = new ArrayList<Integer>();
		// 定義List集合buyKeyPattern裝成交時間所對應的時間初索引
		List<Integer> buyKeyPattern = new ArrayList<Integer>();
		// 定義List集合buyPrice裝買進的準確價位
		List<Double> buyPrice = new ArrayList<Double>();

		int profitTime = 0;
		int lossTime = 0;
		double totalProfit = 0;
		double firstPercent = 50;
		double secondPercent = 50;

		// ======================處理時間List======================
		while ((line = br.readLine()) != null) {
			String[] item = line.split(",");
			if (isTitle(item[3])) {
				continue; // 判斷是否有title
			}
			timeList.add(item[2]);
			priceList.add(Double.parseDouble(item[3]));
			volumeList.add(Double.parseDouble(item[4]));
		}
		br.close();
		// 獲取總資料筆數
		int totalSize = timeList.size();

		// 遍歷交易時間
		// System.out.println(timeList);
		// 遍歷成交價
		// System.out.println(priceList);
		// 遍歷成交量
		// System.out.println(volumeList);

		// 獲取每個時間段初的索引
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

		// 獲取時間段初索引的筆數
		totalSize = indexList.size();

		// 把indexList中的連續數字取第一個放進newIndexList中
		// 先把indexList中的第一個索引放進newIndexList中
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
		// System.out.println(newIndexList); // 遍歷每個時間段初的索引

		// ======================處理開盤價List======================
		for (int i = 0; i < newIndexList.size(); i++) {
			openNow.add(priceList.get(newIndexList.get(i)));
		}
		// System.out.println(openNow); //遍歷每個時間段初的開盤價

		// ======================處理最高價List======================
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
		// System.out.println(highNow); //遍歷每個時間段的最高價

		// ======================處理最低價List======================
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
		// System.out.println(lowNow); //遍歷每個時間段的最低價

		// ======================處理收盤價List======================
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			closeNow.add(priceList.get(newIndexList.get(i + 1) - 1));
		}

		// System.out.println(closeNow); //遍歷每個時間段的收盤價

		// ======================處理時間段的總量List======================
		double volume = 0;
		for (int i = 0; i < newIndexList.size() - 1; i++) {
			for (int j = newIndexList.get(i); j < newIndexList.get(i + 1); j++) {
				volume += volumeList.get(j);
			}
			volumeNow.add(volume);
			volume = 0;
		}
		// System.out.println(volumeNow); //遍歷每個時間段的總量

		// ======================處理符合pattern的時間段及對應newIndexList的索引======================
		// *****定義開盤or收盤????????*****
		// pattern出現的次數
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
		// System.out.println("pattern發生的時間段初" + patternIndex); //
		// 遍歷每個pattern發生的時間段初
		// System.out.println(patternCount);//pattern出現的次數
		// System.out.println(patternNewIndex);//遍歷每個pattern出現的時間對應的newIndexList的索引位置
		// System.out.println(patternIndex.get(0));
		// System.out.println(newIndexList.get(40));

		// ======================處理符合keyPattern的索引======================

		// ****加入動態的最高價及最低價(必須隨著keyPattern時間調整)
		double volumeNumber = 0;
		double dynamicHigh = 0;
		double dynamicLow = 10000;

		// ******pattern版******
		for (int i = 0; i < patternCount; i++) {
			// 從pattern出現的那刻時間段索引一直掃到下一個時間段
			for (int j = patternIndex.get(i); j < newIndexList.get(patternNewIndex.get(i) + 1); j++) {
				if (priceList.get(j) > dynamicHigh) {
					dynamicHigh = priceList.get(j);
				}
				if (priceList.get(j) < dynamicLow) {
					dynamicLow = priceList.get(j);
				}
				volumeNumber += volumeList.get(j);
				// 如果那個時間段途中的量超過前一根量的multiple倍且動態最高價或是最低價距離市價超過spread的話則把詳細發生的時間索引加入keyPattern
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

		// ******dividePattern版******
		ArrayList<Integer> keyDividePattern = new ArrayList<Integer>();
		ArrayList<Integer> keyDividePatternIndex = new ArrayList<Integer>();
		ArrayList<Double> dynamicDivideHighPrice = new ArrayList<Double>();
		ArrayList<Double> dynamicDivideLowPrice = new ArrayList<Double>();
		for (int i = 0; i < patternDivideCount; i++) {
			// 從dividePattern出現的那刻時間段索引一直掃到下一個時間段

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
				// 如果那個時間段途中的量超過前一根量的multiple倍且動態最高價或是最低價距離市價超過spread的話則把詳細發生的時間索引加入keyDividePattern
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
		// System.out.println("keyDividePattern(已達掛單條件)的時間" +
		// keyDividePattern);// 遍歷每個keyDividePattern(已達掛單條件)的時間
		// System.out.println("keyPattern(已達掛單條件)的時間" + keyPattern); //
		// 遍歷每個keyPattern(已達掛單條件)的時間
		// System.out.println(keyPatternIndex);
		// System.out.println(newIndexList.get(keyPatternIndex.get(0)+1));
		// System.out.println(dynamicHighPrice);//遍歷每個達到keyPattern時當前紀錄的最高價
		// System.out.println(dynamicLowPrice);//遍歷每個達到keyPattern時當前紀錄的最低價

		// ======================處理掛單之後成交的詳細情況(pattern版)======================
		// System.out.println(priceList.get(patternIndex.get(0))); //獲取時間段初的價格
		int countPatternVersion = 0;
		for (int i = 0; i < keyPattern.size(); i++) {
			// 從keyPattern開始一直到那根索引結束
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
				// 如果keyPattern前的最高價減keyPattern當刻的價差大於spread的話就是準備向下預掛buy單
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
					// 買進價位priceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyPattern.get(i)) + markup);
					buyKeyPattern.add(keyPatternIndex.get(i));
					break;
				}
			}

		}
		// System.out.println(buyIndex.size());
		// System.out.println(keyPattern);
		// System.out.println(buyType); // 遍歷買進的種類(BUYorSELL)
		// System.out.println(buyIndex); // 遍歷買進的時間索引
		// System.out.println(buyKeyPattern);// 遍歷買進時刻的時間初索引

		// System.out.println(newIndexList.size());

		// ======================處理掛單之後成交的詳細情況(dividePattern版)======================
		for (int i = 0; i < keyDividePattern.size(); i++) {
			// 從keyPattern開始一直到那根索引結束
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
				// 如果keyPattern前的最高價減keyPattern當刻的價差大於spread的話就是準備向下預掛buy單
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
					// 買進價位priceList.get(keyPattern.get(i)) + markup
					buyPrice.add(priceList.get(keyDividePattern.get(i)) + markup);
					buyKeyPattern.add(keyDividePatternIndex.get(i));
					break;
				}
			}

		}
		// System.out.println(buyIndex.size());
		// ======================處理成交之後的獲利或是停損情況======================

		for (int i = 0; i < buyIndex.size(); i++) {
			// newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1)
			// 防止資料爆掉
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			// 如果是buy單
			if (buyType.get(i) == 1) {
				// 從買進的索引一直掃到停損時間(laterNumber)的索引
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// 如果市價-買進價(buy)>-firstProfit
					if (priceList.get(j) - buyPrice.get(i) >= firstProfit) {

						totalProfit += firstProfit * firstPercent / 100;
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out
									.println("[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ ",買進時間段" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "賣出時間"
											+ timeList.get(j) + "當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利" + firstProfit + "點");
						} else {
							System.out
									.println("[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" + timeList.get(buyIndex.get(i))
											+ ",買進時間段" + timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "賣出時間"
											+ timeList.get(j) + "當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
											+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" + firstProfit + "點");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Buy單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" +
						// timeList.get(buyIndex.get(i)) + ",買進時間段"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "賣出時間"
						// + timeList.get(j) + "當根量" +
						// volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" +
						// firstProfit + "點",
						// true);
						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								System.out.println("[Buy單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" + secondProfit + "點");
								// writeTxt(workPath() + "/history.txt",
								// "[Buy單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" +
								// secondProfit + "點", true);
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

								// [損失總額]
								if ((priceList.get(j) - buyPrice.get(i)) < 0) {
									totalLoss += (priceList.get(j) - buyPrice.get(i)) * secondPercent / 100;
								}
								System.out.println("[Buy單]***停損***,可惜在" + timeList.get(j) + "停損,獲利為"
										+ (priceList.get(j) - buyPrice.get(i)) + "點");
								// writeTxt(workPath() + "/history.txt",
								// "[Buy單]***停損***,可惜在" + timeList.get(j) +
								// "停損,獲利為"
								// + (priceList.get(j) - buyPrice.get(i)) + "點",
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
						// [損失總額]
						if ((priceList.get(j) - buyPrice.get(i)) < 0) {
							totalLoss += (priceList.get(j) - buyPrice.get(i)) * 1;
						}
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利為"
									+ (priceList.get(j) - buyPrice.get(i)) + "點");
						} else {
							System.out.println("[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
									+ (priceList.get(j) - buyPrice.get(i)) + "點");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Buy單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在"
						// + priceList.get(j) + "停損,買進時間"
						// + timeList.get(buyIndex.get(i)) + ",買進時間段"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "停損時間"
						// + timeList.get(j) + ",當根量" +
						// volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
						// + (priceList.get(j) - buyPrice.get(i)) + "點",
						// true);
						System.out.println("=============================================================");
						// writeTxt(workPath() + "/history.txt",
						// "=============================================================",
						// true);
						break;
					}
				} // 如果是sell單
			} else if (buyType.get(i) == 2) {
				// 從買進的索引一直掃到停損時間(laterNumber)的索引
				for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
					// 如果買進價(sell)-市價>-firstProfit
					if (buyPrice.get(i) - priceList.get(j) >= firstProfit) {
						totalProfit += firstProfit * firstPercent / 100;
						// System.out.println("獲利增加"+firstProfit*firstPercent/100);
						if (buyKeyPattern.get(i) - 2 < volumeNow.size() && i >= countPatternVersion) {
							System.out.println("[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間"
									+ timeList.get(buyIndex.get(i)) + "賣出時間" + timeList.get(j) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "當根量"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利" + firstProfit + "點");
						} else {
							System.out.println("[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間"
									+ timeList.get(buyIndex.get(i)) + "賣出時間" + timeList.get(j) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "當根量"
									+ volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" + firstProfit + "點");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Sell單]恭喜你,在" + buyPrice.get(i) + "買進,買進時間" +
						// timeList.get(buyIndex.get(i)) + "賣出時間"
						// + timeList.get(j) + ",買進時間段"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "當根量"
						// + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利" +
						// firstProfit + "點",
						// true);
						// 從停利的索引一直掃到停損時間(laterNumber)的索引
						for (; j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
							if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
								profitTime++;
								totalProfit += secondProfit * secondPercent / 100;
								// System.out.println("獲利增加"+secondProfit*secondPercent/100);
								System.out.println("[Sell單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利" + secondProfit + "點");
								// writeTxt(workPath() + "/history.txt",
								// "[Sell單]恭喜你,在" + timeList.get(j) + "又賣出了,獲利"
								// + secondProfit + "點", true);
								System.out.println("=============================================================");
								// writeTxt(workPath() + "/history.txt",
								// "=============================================================",
								// true);
								break;
							} else if (j == newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1) - 1) {
								totalProfit += (buyPrice.get(i) - priceList.get(j)) * (secondPercent / 100);
								// System.out.println("獲利增加"+(buyPrice.get(i) -
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
								System.out.println("[Sell單]***停損***,可惜在" + timeList.get(j) + "停損,獲利為"
										+ (buyPrice.get(i) - priceList.get(j)) + "點");
								// writeTxt(workPath() + "/history.txt",
								// "[Sell單]***停損***,可惜在" + timeList.get(j) +
								// "停損,獲利為"
								// + (buyPrice.get(i) - priceList.get(j)) + "點",
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
						// System.out.println("獲利增加"+(buyPrice.get(i) -
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
							System.out.println("[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + " 前前根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 2) + ",獲利為"
									+ (buyPrice.get(i) - priceList.get(j)) + "點");
						} else {
							System.out.println("[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在" + priceList.get(j)
									+ "停損,買進時間" + timeList.get(buyIndex.get(i)) + ",買進時間段"
									+ timeList.get(newIndexList.get(buyKeyPattern.get(i))) + "停損時間" + timeList.get(j)
									+ ",當根量" + volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
									+ volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
									+ (buyPrice.get(i) - priceList.get(j)) + "點");
						}

						// writeTxt(workPath() + "/history.txt",
						// "[Sell單]***停損***,在" + buyPrice.get(i) + "買進" + ",可惜在"
						// + priceList.get(j) + "停損,買進時間"
						// + timeList.get(buyIndex.get(i)) + ",買進時間段"
						// +
						// timeList.get(newIndexList.get(buyKeyPattern.get(i)))
						// + "停損時間"
						// + timeList.get(j) + ",當根量" +
						// volumeNow.get(buyKeyPattern.get(i)) + ",前一根量"
						// + volumeNow.get(buyKeyPattern.get(i) - 1) + ",獲利為"
						// + (buyPrice.get(i) - priceList.get(j)) + "點",
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
		// System.out.println("pattern共出現" + patternCount + "次 成交" + (profitTime
		// + lossTime) + "次 總獲利 " + totalProfit
		// + " 獲利次數 " + profitTime + " 虧損次數 " + lossTime + " 勝率"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%");
		// writeTxt(workPath() + "/history.txt",
		// "pattern共出現" + patternCount + "次 成交" + (profitTime + lossTime) + "次
		// 總獲利 " + totalProfit + " 獲利次數 "
		// + profitTime + " 虧損次數 " + lossTime + " 勝率"
		// + ((double) profitTime / (double) (profitTime + lossTime)) * 100 +
		// "%",
		// true);
		// System.out.println("總獲利計算方式為 (第一次停利點) " + firstProfit + " x
		// (第一次停利點賣出的倉位百分比) " + firstPercent + "%"
		// + " + (第二次停利點) " + secondProfit + " x (第二次停利點賣出的倉位百分比)" +
		// secondPercent + "%");

		// writeTxt(workPath() + "/history.txt", "總獲利計算方式為 (第一次停利點) " +
		// firstProfit + " x (第一次停利點賣出的倉位百分比) " + firstPercent
		// + "%" + " + (第二次停利點) " + secondProfit + " x (第二次停利點賣出的倉位百分比)" +
		// secondPercent + "%", true);

		// writeTxt(workPath() + "/history.txt", "", true);

		// ======================處理最大虧損有無達到maxLoss======================
		int maxloss = 0;
		for (int i = 0; i < buyIndex.size(); i++) {
			if (buyKeyPattern.get(i) + laterNumber + 1 > newIndexList.size() - 1) {
				break;
			}
			for (int j = buyIndex.get(i); j < newIndexList.get(buyKeyPattern.get(i) + laterNumber + 1); j++) {
				// 如果是buy單
				if (buyType.get(i) == 1) {
					// 買價-市價如果大於maxLoss
					if (priceList.get(j) - buyPrice.get(i) >= secondProfit) {
						break;
					}
					if (buyPrice.get(i) - priceList.get(j) >= maxLoss) {
						maxloss++;
						// writeTxt(workPath() + "/history.txt", "在" +
						// timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i)
						// + ",買進時間" + timeList.get(buyIndex.get(i)), true);
						System.out.println("在" + timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i) + ",買進時間"
								+ timeList.get(buyIndex.get(i)));
						break;
					}
				} else if (buyType.get(i) == 2) {
					// 買價-市價如果大於maxLoss
					if (buyPrice.get(i) - priceList.get(j) >= secondProfit) {
						break;
					}
					if (priceList.get(j) - buyPrice.get(i) >= maxLoss) {
						maxloss++;
						// writeTxt(workPath() + "/history.txt", "在" +
						// timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i)
						// + ",買進時間" + timeList.get(buyIndex.get(i)), true);
						System.out.println("在" + timeList.get(j) + "達到預設最大損失點,買進價位" + buyPrice.get(i) + ",買進時間"
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
			out.write(writeString + "\r\n"); // 寫入
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close(); // 關閉資源
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
				break; // 字串中出現一個非數值或是非.的字元就結束 loop, 因為這不是我們要的字串
			}
		}
		return flag;
	}

}
