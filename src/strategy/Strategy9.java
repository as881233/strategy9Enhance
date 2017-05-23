package strategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class Strategy9 {
	public static void main(String[] args) throws IOException {
		
			//BufferedWriter bw2 = new BufferedWriter(new FileWriter("random8.txt",true));
			Properties properties = new Properties();
			File fileConfig = new File(workPath() + "/config.ini");
			FileInputStream fis = new FileInputStream(fileConfig);
			properties.load(fis);
			fis.close();
			
			File f = new File("input\\");
			File[] fileInput = f.listFiles();
			
			int totalNumber = 0; // 總資料筆數
			BufferedWriter bw = new BufferedWriter(new FileWriter("history.txt"));
			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null) {
					totalNumber++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader brr = new BufferedReader(new FileReader(fileConfig));
			String parameter = null;
			Integer[] finalParameter = new Integer[13];
			int z = 0;
			while ((parameter = brr.readLine()) != null) {
				String[] parameterArr = parameter.split("=");
				finalParameter[z] = Integer.parseInt(parameterArr[1]);
				z++;
			}
			
			
			 /*亂數跑最佳化*/
			/*
			Random random = new Random();
			
			int[] randomTimeRange = {5,15,30,60};
			int[] randomSpread = {6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25};
			int[] randomLaterNumber = {1,2,3};
			int[] randomMultiple = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
			int[] randomMarkup = {1,2,3,4,5,6,7,8,9,10};
			int[] randomFirstProfit = {5,6,7,8,9};
			int[] randomSecondProfit = {10,15,20,25};
			int[] randomSpreadChecking = {0};
			int aTimeRange =random.nextInt(4);
			int aSpread = random.nextInt(20);
			int aLaterNumber = random.nextInt(3);
			int aMultiple = random.nextInt(19);
			int aMarkup = random.nextInt(10);
			int aFirstProfit = random.nextInt(5);
			int asecondProfit = random.nextInt(4);
			int aRandomSpreadChecking = random.nextInt(1);
			
			int timeRange = randomTimeRange[aTimeRange];
			int nowNumber = totalNumber / timeRange;
			double spread = randomSpread[aSpread];
			double markup = randomMarkup[aMarkup];
			int laterNumber = randomLaterNumber[aLaterNumber];
			int multiple = randomMultiple[aMultiple];
			int firstProfit = randomFirstProfit[aFirstProfit];
			int secondProfit = randomSecondProfit[asecondProfit];
			int setCheckMultiple = Integer.parseInt(properties.getProperty("setCheckMultiple"));
			int checkTimeRange = Integer.parseInt(properties.getProperty("checkTimeRange"));
			int nowCheckNumber = totalNumber / checkTimeRange;
			int checkingTimeRange = 5;
			int spreadChecking = randomSpreadChecking[aRandomSpreadChecking];
			int maxLoss = Integer.parseInt(properties.getProperty("maxLoss"));
			*/
			
			
			
			int timeRange = Integer.parseInt(properties.getProperty("timeRange"));
			int nowNumber = totalNumber / timeRange;
			double spread = Integer.parseInt(properties.getProperty("spread"));
			double markup = Integer.parseInt(properties.getProperty("markup"));
			int laterNumber = Integer.parseInt(properties.getProperty("laterNumber"));
			int multiple = Integer.parseInt(properties.getProperty("multiple"));
			int firstProfit = Integer.parseInt(properties.getProperty("firstProfit"));
			int secondProfit = Integer.parseInt(properties.getProperty("secondProfit"));
			int setCheckMultiple = Integer.parseInt(properties.getProperty("setCheckMultiple"));
			int checkTimeRange = Integer.parseInt(properties.getProperty("checkTimeRange"));
			int nowCheckNumber = totalNumber / checkTimeRange;
			int checkingTimeRange = 5;
			int spreadChecking = Integer.parseInt(properties.getProperty("spreadChecking"));
			int maxLoss = Integer.parseInt(properties.getProperty("maxLoss"));
			

			String[] volume = new String[totalNumber];
			String[] date = new String[totalNumber];
			double[] close = new double[totalNumber];
			double[] open = new double[totalNumber];
			double[] highest = new double[totalNumber];
			double[] lowest = new double[totalNumber];
			String[] dateNow = new String[nowNumber];
			double[] volumeNow = new double[nowNumber];
			double[] openNow = new double[nowNumber];
			double[] highestNow = new double[nowNumber];
			double[] lowestNow = new double[nowNumber];
			double[] closeNow = new double[nowNumber];
			double totalProfit = 0.0;
			int profitTime = 0;
			int lossTime = 0;
			double firstLotPercent = Integer.parseInt((properties.getProperty("firstLotPercent")));
			double secondLotPercent = Integer.parseInt((properties.getProperty("secondLotPercent")));

			int a = 0;

			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null) {
					String[] item = line.split(",");
					volume[a] = item[5];
					a++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for(int i:volume){ System.out.println(i);
			 * 
			 * }
			 */ // 遍歷每分鐘量

			// 以上為統計每分鐘的量並放在volume數組裡面
			// ==========================================
			int j = 0;
			int count = 0;
			for (int i = 0; i < nowNumber; i++) {
				j = i * timeRange;
				for (; j < totalNumber; j++) {
					count++;
					volumeNow[i] += Integer.parseInt(volume[j]);
					if ((j != 0) && (count % timeRange == 0)) {
						break;
					}
				}

			}
			/*
			 * for(double i:volumeNow){ System.out.println(i); }
			 */ // 遍歷當前每timeRange分鐘總量

			// 以上為計算當前volume(timeRange分鐘)的總量
			// =============================================

			a = 0;
			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null && a < totalNumber) {
					String[] item = line.split(",");
					date[a] = item[0];
					a++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for(String i:date){ System.out.println(i);
			 * 
			 * }
			 */ // 遍歷每timeRange分鐘的開盤時間
			// 以上為統計每timeRange分鐘的開盤時間並放在date數組裡面
			// =============================================

			a = 0;
			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null && a < totalNumber) {
					String[] item = line.split(",");
					open[a] = Double.parseDouble(item[1]);
					a++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for(double i:open){ System.out.println(i);
			 * 
			 * }
			 */ // 遍歷每分鐘開盤價
			// 以上為統計每分鐘的開盤價並放在open數組裡面
			// =============================================

			a = 0;
			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null && a < totalNumber) {
					String[] item = line.split(",");
					highest[a] = Double.parseDouble(item[2]);
					a++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for(double i:highest){ System.out.println(i);
			 * 
			 * }
			 */ // 遍歷每分鐘最高價
			// 以上為統計每分鐘的最高價並放在highest數組裡面
			// =============================================

			a = 0;
			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null && a < totalNumber) {
					String[] item = line.split(",");
					lowest[a] = Double.parseDouble(item[3]);
					a++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for(double i:lowest){ System.out.println(i);
			 * 
			 * }
			 */ // 遍歷每分鐘最低價
			// 以上為統計每分鐘的開盤價並放在lowest數組裡面
			// =============================================

			a = 0;
			try {
				BufferedReader read = new BufferedReader(new FileReader(fileInput[0]));
				String line = null;
				while ((line = read.readLine()) != null && a < totalNumber) {
					String[] item = line.split(",");
					close[a] = Double.parseDouble(item[4]);
					a++;
				}
				read.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * for(double i:close){ System.out.println(i);
			 * 
			 * }
			 */ // 遍歷每分鐘收盤價
			// 以上為統計每分鐘的開盤價並放在close數組裡面
			// =============================================
			count = 0;
			for (int i = 0; i < totalNumber; i++) {
				if (i % timeRange == 0) {
					openNow[count] = open[i];
					count++;
				}
			}

			/*
			 * for(double i:openNow){ System.out.println(i); }
			 */ // 遍歷timeRange分鐘開盤價

			// 以上為統計timeRange分鐘開盤價並放在openNow數組裡面
			// =============================================

			count = 0;
			double max = 0;
			for (int i = 0; i < nowNumber; i++) {
				for (int k = 0; k < totalNumber; k++) {
					count++;
					if (highest[k + timeRange * i] >= max) {
						max = highest[k + timeRange * i];

					}

					if (count % timeRange == 0) {
						highestNow[i] = max;
						max = 0;
						break;
					}
				}
			}

			/*
			 * for(double i:highestNow){ System.out.println(i); }
			 */ // 遍歷timeRange分鐘最高價

			// System.out.println(highestNow[1]);
			// 以上為統計timeRange分鐘最高價並放在highestNow數組裡面
			// =============================================

			count = 0;
			double min = 10000;
			for (int i = 0; i < nowNumber; i++) {
				for (int k = 0; k < totalNumber; k++) {
					count++;
					if (lowest[k + timeRange * i] <= min) {
						min = lowest[k + timeRange * i];

					}

					if (count % timeRange == 0) {
						lowestNow[i] = min;
						min = 10000;
						break;

					}

				}

			}

			/*
			 * for(double i:lowestNow){ System.out.println(i); }
			 */ // 遍歷timeRange分鐘最高價

			// System.out.println(lowestNow[1]);
			// 以上為統計timeRange分鐘最高價並放在lowestNow數組裡面
			// =============================================

			count = 0;
			for (int i = 0; i < totalNumber; i++) {
				if (i % timeRange == 0) {
					if (i % timeRange == 0) {
						closeNow[count] = close[i + (timeRange - 1)];

					}
					count++;
				}
			}

			/*
			 * for(double i:closeNow){ System.out.println(i); }
			 */ // 遍歷timeRange分鐘收盤價

			// 以上為統計timeRange分鐘收盤價並放在closeNow數組裡面
			// =============================================

			count = 0;
			for (int i = 0; i < totalNumber; i++) {
				if (i % timeRange == 0) {
					dateNow[count] = date[i];
					count++;
				}
			}

			/*
			 * for(String i:dateNow){ System.out.println(i); }
			 */ // 遍歷每timeRange分鐘的開盤時間

			// 以上為統計timeRange分鐘的開盤時間並放在dateNow數組裡面
			// =============================================

			// System.out.println(dateNow[950]+","+openNow[95]+","+highestNow[95]+","+lowestNow[95]+","+closeNow[95]+","+volumeNow[950]);
			int patternCount = 0;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < nowNumber; i++) {
				if (i - 1 >= 0) {
					if ((Math.abs(openNow[i] - highestNow[i]) >= spread || Math.abs(openNow[i] - lowestNow[i]) >= spread)
							&& volumeNow[i] / volumeNow[i - 1] >= multiple) {
						sb.append(dateNow[i] + " ");
						patternCount++;
						// System.out.println("符合pattern的日期"+dateNow[i]);
					}
				}

			}
			String patternDate = sb.toString().trim();
			String[] patternDateArr = patternDate.split(" ");
			// 以上為判斷pattern出現的個數並存放在StringBuilder(sb)中
			// =============================================

			Integer[] patternIndex = new Integer[patternCount];
			int location = 0;
			for (int i = 0; i < patternCount; i++) {
				for (int k = location; k < totalNumber; k++) {
					if (patternDateArr[i].matches(date[k])) {
						patternIndex[i] = k;
						location = k;
						// System.out.println("符合第"+(i+1)+"個pattern"+"的索引位置為"+k+"
						// 時間為"+date[k]);
					}
				}
			}
			// 以上為判斷pattern所在的索引(1分鐘索引)並存放在patternIndex的數組中
			// =============================================

			Integer[] patternIndexNow = new Integer[patternCount];
			location = 0;
			for (int i = 0; i < patternCount; i++) {
				for (int k = location; k < nowNumber; k++) {
					if (patternDateArr[i].matches(dateNow[k])) {
						patternIndexNow[i] = k;
						location = k;
						// System.out.println("符合第"+(i+1)+"個pattern"+"的索引位置為"+k+"
						// 時間為"+dateNow[k]);
					}
				}
			}
			// 以上為判斷pattern所在的索引(timeRange分鐘索引)並存放在patternIndexNow的數組中
			// =============================================

			int volumeNumber = 0;
			Integer[] keyPatternOccur = new Integer[patternCount];
			for (int i = 0; i < patternCount; i++) {
				for (int k = patternIndex[i]; k < patternIndex[i] + timeRange; k++) {
					volumeNumber += Integer.parseInt(volume[k]);
					if (volumeNumber > (volumeNow[patternIndexNow[i] - 1] * multiple)) {
						keyPatternOccur[i] = k;
						 //System.out.println("在分鐘索引"+k+"時 pattern出現");
						volumeNumber = 0;
						break;
					}
				}
			}
			// 將pattern發生的詳細時間索引放在keyPatternOccur裡面
			// =============================================
			Integer[] fiveMinuteCheck = new Integer[patternCount];
			int check = 0;
			int checkCount = 0;
			
			
			double[] volumeCheck = new double[nowCheckNumber];
			for (int i = 0; i < nowCheckNumber; i++) {
				check = i * checkTimeRange;
				for (; check < totalNumber; check++) {
					checkCount++;
					volumeCheck[i] += Integer.parseInt(volume[check]);
					if ((check != 0) && (checkCount % checkTimeRange == 0)) {
						break;
					}
				}

			}
			
			if(setCheckMultiple!=0){
				for(int i =0;i<patternCount;i++){
					for(int m =0;m<(timeRange/checkTimeRange)-1;m++){
						if(volumeCheck[(patternIndex[i]/checkTimeRange)+m+1]/volumeCheck[(patternIndex[i]/checkTimeRange)+m]>=setCheckMultiple){
							fiveMinuteCheck[i] = 1;
							//System.out.println("在"+patternIndex[i]+"時間段有"+setCheckMultiple+"出現"+"======"+i);
							break;
						}else{
							fiveMinuteCheck[i] = 0;
						}
					}
					
				}
			}else{
				for(int i=0;i<patternCount;i++){
					fiveMinuteCheck[i] =1;
				}
			}
			
			
			int nowCheckingNumber = totalNumber/checkingTimeRange;
					double[]checkingOpen = new double[nowCheckingNumber];
					double[]checkingHighest = new double[nowCheckingNumber];
					double[]checkingLowest = new double[nowCheckingNumber];
					Integer[] checkingSpread = new Integer[patternCount];

					count = 0;
							double checkingMax = 0;
							for (int i = 0; i < nowCheckingNumber; i++) {
								for (int k = 0; k < totalNumber; k++) {
									count++;
									if (highest[k + checkingTimeRange * i] >= checkingMax) {
										checkingMax = highest[k + checkingTimeRange * i];

									}

									if (count % checkingTimeRange == 0) {
										checkingHighest[i] = checkingMax;
										checkingMax = 0;
										break;
									}
								}
							}


					count = 0;
							for (int i = 0; i < totalNumber; i++) {
								if (i % checkingTimeRange == 0) {
									checkingOpen[count] = open[i];
									count++;
								}
							}

					count = 0;
							double checkingMin = 10000;
							for (int i = 0; i < nowCheckingNumber; i++) {
								for (int k = 0; k < totalNumber; k++) {
									count++;
									if (lowest[k + checkingTimeRange * i] <= checkingMin) {
										checkingMin = lowest[k + checkingTimeRange * i];
										
									}

									if (count % checkingTimeRange == 0) {
										checkingLowest[i] = checkingMin;
										checkingMin = 10000;
										break;

									}

								}

							}
	/*for(double i:keyPatternOccur){
		System.out.println(i);
	}*/

					for(int i =0;i<patternCount;i++){
								
									if(Math.abs(checkingOpen[keyPatternOccur[i]/5]-checkingHighest[keyPatternOccur[i]/5])>=spreadChecking||Math.abs(checkingOpen[keyPatternOccur[i]/5]-checkingLowest[keyPatternOccur[i]/5])>=spreadChecking){
										checkingSpread[i] = 1;
										//System.out.println("在"+keyPatternOccur[i]+"的分鐘段有超過spread");
									}else{
										checkingSpread[i] = 0;
										//System.out.println("在"+keyPatternOccur[i]+"的分鐘段沒有超過spread");
									}
									//abs(五分鐘的開盤價-最高價)orabs(五分鐘的開盤價-最低價)>=spread2
								
							}
					
			//以上為判斷5分鐘內有沒有爆大量以及5分鐘內的spread2有沒有超過預設值
			double[] buy = new double[patternCount];
			double[] buyIndex = new double[patternCount];
			double[] buyType = new double[patternCount];
			double openFix; //把markup考慮進去
			for (int i = 0; i < patternCount; i++) {
				for (int record = keyPatternOccur[i]; record < patternIndex[i] + timeRange; record++) {
					//上方從有出現spread&&7倍的地方開始掃
					if (Math.abs(open[record] - open[patternIndex[i]]) >= spread&&fiveMinuteCheck[i]==1&&checkingSpread[i]==1) {
						if (open[record] - open[patternIndex[i]] < 0) {
							
							openFix = open[record] - markup;
							for (; record < patternIndex[i] + timeRange; record++) {
								if (lowest[record] < openFix) {
									buyType[i] = 1;
									buy[i] = openFix; // 買在keypattern出現過後有markup的地方
									buyIndex[i] = record;
									break;
						
								}
							}	
							break;
							}
							if (open[record] - open[patternIndex[i]] > 0) {
								
								openFix = open[record] + markup;
								//System.out.println(keyPatternOccur[i]);
								for (; record < patternIndex[i] + timeRange; record++) {
									if (highest[record] > openFix) {
										buyType[i] = 2;
										buy[i] = openFix; // 空在keypattern出現過後有markup的地方
										buyIndex[i] = record;
										break;
							
									}
								}	
								break;
							}
						
						/*
						 * if (open[patternIndex[i]] > open[record]) { buyType[i] =
						 * 1; buy[i] = (highest[record]*1/5+open[record]*4/5); //
						 * 買在keypattern出現當分鐘的最高點 buyIndex[i] = record; } else {
						 * buy[i] = (lowest[record]*1/5+open[record]*4/5);
						 * buyIndex[i] = record; // 空在買在keypattern出現當分鐘的最低點
						 * buyType[i] = 2; } break;
						 */
					} else if (Math.abs(highest[record] - open[patternIndex[i]]) >= spread&&fiveMinuteCheck[i]==1&&checkingSpread[i]==1) {
						if (highest[record] - open[patternIndex[i]] < 0) {
							openFix = highest[record] - markup;
							for (; record < patternIndex[i] + timeRange; record++) {
								if (lowest[record] < openFix) {
									buyType[i] = 1;
									buy[i] = openFix; // 買在keypattern出現過後有markup的地方
									buyIndex[i] = record;
									break;
						
								}
							}	
							break;
							}
							if (highest[record] - open[patternIndex[i]] > 0) {

								openFix = highest[record] + markup;
								for (; record < patternIndex[i] + timeRange; record++) {
									if (highest[record] > openFix) {
										buyType[i] = 2;
										buy[i] = openFix; // 空在keypattern出現過後有markup的地方
										buyIndex[i] = record;
										break;
							
									}
								}	
								break;
							}
					} else if (Math.abs(lowest[record] - open[patternIndex[i]]) >= spread&&fiveMinuteCheck[i]==1&&checkingSpread[i]==1) {

						if (lowest[record] - open[patternIndex[i]] < 0) {
							openFix = lowest[record] - markup;
							for (; record < patternIndex[i] + timeRange; record++) {
								if (lowest[record] < openFix) {
									buyType[i] = 1;
									buy[i] = openFix; // 買在keypattern出現過後有markup的地方
									buyIndex[i] = record;
									break;
						
								}
							}		
							break;
							}
							if (lowest[record] - open[patternIndex[i]] > 0) {

								openFix = lowest[record] + markup;
								for (; record < patternIndex[i] + timeRange; record++) {
									if (lowest[record] > openFix) {
										buyType[i] = 2;
										buy[i] = openFix; // 空在keypattern出現過後有markup的地方
										buyIndex[i] = record;
										break;
							
									}
								}	
								break;
							}
					}
				}

			}
			/*
			 * for(double k:buy){ System.out.println(k); }
			 */ // 遍歷買進價格

			/*
			 * for(double k:buyIndex){ System.out.println(k); }
			 */ // 遍歷買進價格時的索引

			// ============================
			// 以上為統計詳細的買進價格

			/*
			 * int result = 0; for(int i =0;i<patternCount;i++){ for(int record =
			 * (int)buyIndex[i];record<patternIndex[i]+(laterNumber+1)*timeRange;
			 * record++){ if(buy[i]!=0){
			 * if(highest[record]-buy[i]>=firstProfit&&buyType[i]==1){
			 * System.out.println("BUY單 恭喜你在"+buy[i]+"買進"+(buy[i]+firstProfit+"賣出")
			 * +" 買進時間"+date[(int)buyIndex[i]]+" 買進時間段"+date[patternIndex[i]]+"當根量"+
			 * volumeNow[patternIndexNow[i]]+"前一根量"+volumeNow[patternIndexNow[i]-1])
			 * ; for(;record<patternIndex[i]+(laterNumber+1)*timeRange;record++){
			 * if(highest[record]-buy[i]>=secondProfit){ result = 1;
			 * System.out.println("BUY單 恭喜你在"+(buy[i]+secondProfit)+"又成功賣出了\n");
			 * result = 0; break; }else if(record ==
			 * patternIndex[i]+(laterNumber+1)*timeRange-1 && result==0 ){
			 * System.out.println("(停損)BUY單 可惜在"+(close[patternIndex[i]+((
			 * laterNumber+1)*timeRange)-1])+"停損了 停損時間"+date[patternIndex[i]+((
			 * laterNumber+1)*timeRange)-1]+" 買進時間"+date[(int)buyIndex[i]]+"\n");
			 * break; } } break; }else
			 * if(buy[i]-lowest[record]>=firstProfit&&buyType[i]==2){
			 * System.out.println("Sell單 恭喜你在"+buy[i]+"買進"+"在"+(buy[i]-firstProfit)+
			 * "賣出"+" 買進時間"+date[(int)buyIndex[i]]+" 買進時間段"+date[patternIndex[i]]+
			 * "當根量"+volumeNow[patternIndexNow[i]]+"前一根量"+volumeNow[patternIndexNow[
			 * i]-1]);
			 * for(;record<patternIndex[i]+(laterNumber+1)*timeRange;record++){
			 * if(buy[i]-lowest[record]>=secondProfit){ result = 1;
			 * System.out.println("Sell單 恭喜你在"+(buy[i]-secondProfit)+"又成功賣出了\n");
			 * result = 0; break; }else if(record ==
			 * patternIndex[i]+(laterNumber+1)*timeRange-1 && result==0 ){
			 * System.out.println("(停損)Sell單 可惜在"+(close[patternIndex[i]+((
			 * laterNumber+1)*timeRange)-1])+"停損了 停損時間"+date[patternIndex[i]+((
			 * laterNumber+1)*timeRange)-1]+" 買進時間"+date[(int)buyIndex[i]]+"\n"+
			 * "當根量"+volumeNow[patternIndexNow[i]]+"前一根量"+volumeNow[patternIndexNow[
			 * i]-1]+"\n"); break; } } break; }else{ if(buyType[i]==1){
			 * System.out.println("(停損BUY)單 在"+buy[i]+"買進 可惜在"+(close[patternIndex[i
			 * ]+((laterNumber+1)*timeRange)-1])+"停損了 停損時間"+date[patternIndex[i]+((
			 * laterNumber+1)*timeRange)-1]+" 買進時間"+date[(int)buyIndex[i]]+"當根量"+
			 * volumeNow[patternIndexNow[i]]+"前一根量"+volumeNow[patternIndexNow[i]-1]+
			 * "\n"); }else if(buyType[i]==2){
			 * System.out.println("(停損Sell)單 在"+buy[i]+"買進 可惜在"+(close[patternIndex[
			 * i]+((laterNumber+1)*timeRange)-1])+"停損了 停損時間"+date[patternIndex[i]+((
			 * laterNumber+1)*timeRange)-1]+" 買進時間"+date[(int)buyIndex[i]]+"當根量"+
			 * volumeNow[patternIndexNow[i]]+"前一根量"+volumeNow[patternIndexNow[i]-1]+
			 * "\n"); }
			 * 
			 * break; } } } }
			 */

			int result = 0;
			int getInto = 0;
			for (int i = 0; i < patternCount; i++) {
				getInto = 0;
				if (buy[i] != 0) {
					if (buyType[i] == 1) {
						for (int record = (int) buyIndex[i]+1; record < patternIndex[i]
								+ (laterNumber + 1) * timeRange; record++) {
							if (highest[record] - buy[i] >= firstProfit) {
								//System.out.println("賣出時間"+date[record]);
								getInto = 1;
								totalProfit += firstProfit * (firstLotPercent/100);
								bw.write("BUY單 恭喜你在" + buy[i] + "買進" + (buy[i] + firstProfit + "賣出") + " 買進時間"
										+ date[(int) buyIndex[i]] + " 買進時間段" + date[patternIndex[i]] +" 賣出時間 "+date[record]+ "當根量"
										+ volumeNow[patternIndexNow[i]] + "前一根量" + volumeNow[patternIndexNow[i] - 1]
										+ "   獲利為" + firstProfit + "點");
								bw.newLine();
								System.out.println("BUY單 恭喜你在" + buy[i] + "買進" + (buy[i] + firstProfit + "賣出") + " 買進時間"
										+ date[(int) buyIndex[i]] + " 買進時間段" + date[patternIndex[i]] +" 賣出時間 "+date[record]+ "當根量"
										+ volumeNow[patternIndexNow[i]] + "前一根量" + volumeNow[patternIndexNow[i] - 1]
										+ "   獲利為" + firstProfit + "點");
								bw.flush();

								for (; record < patternIndex[i] + (laterNumber + 1) * timeRange; record++) {
									if (highest[record] - buy[i] >= secondProfit) {
										result = 1;
										profitTime++;
										totalProfit += secondProfit * (secondLotPercent/100);
										bw.write("BUY單 恭喜你在" + (buy[i] + secondProfit) + "又成功賣出了     獲利為" + secondProfit
												+ "點"+" 賣出時間 "+date[record]+"\n");
										bw.newLine();
										System.out.println("BUY單 恭喜你在" + (buy[i] + secondProfit) + "又成功賣出了     獲利為"
												+ secondProfit + "點"+" 賣出時間 "+date[record]+"\n");
										bw.write("=======================================================================");
										bw.newLine();
										bw.flush();
										result = 0;
										/* important */getInto = 0;
										break;
									} else if (record == patternIndex[i] + (laterNumber + 1) * timeRange - 1
											&& result == 0) {
										if (firstProfit + (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]
												- buy[i]) > 0) {
											profitTime++;
										} else {
											lossTime++;
										}
										totalProfit += (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]
												- buy[i])*secondLotPercent/100;
										bw.write("(停損)BUY單 可惜在"
												+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])
												+ "停損了 停損時間" + date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]
												+ " 買進時間" + date[(int) buyIndex[i]] + "   獲利為"
												+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] - buy[i])
												+ "點\n");
										bw.newLine();
										System.out.println("(停損)BUY單 可惜在"
												+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])
												+ "停損了 停損時間" + date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]
												+ " 買進時間" + date[(int) buyIndex[i]] + "   獲利為"
												+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] - buy[i])
												+ "點\n");
										bw.write("=======================================================================");
										bw.newLine();
										bw.flush();
										break;
									}
								}

								break;
							} else if (getInto == 0 && record == patternIndex[i] + (laterNumber + 1) * timeRange - 1) {

								if (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] - buy[i] > 0) {
									profitTime++;
								} else {
									lossTime++;
								}
								totalProfit += close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] - buy[i];
								bw.write("(停損BUY)單 在" + buy[i] + "買進 可惜在"
										+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) + "停損了 停損時間"
										+ date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] + " 買進時間"
										+ date[(int) buyIndex[i]] + "當根量" + volumeNow[patternIndexNow[i]] + "前一根量"
										+ volumeNow[patternIndexNow[i] - 1] + "   獲利為"
										+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] - buy[i]) + "\n");
								bw.newLine();
								System.out.println("(停損BUY)單 在" + buy[i] + "買進 可惜在"
										+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) + "停損了 停損時間"
										+ date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] + " 買進時間"
										+ date[(int) buyIndex[i]] + "當根量" + volumeNow[patternIndexNow[i]] + "前一根量"
										+ volumeNow[patternIndexNow[i] - 1] + "   獲利為"
										+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] - buy[i]) + "\n");
								bw.write("=======================================================================");
								bw.newLine();
								bw.flush();
								break;

							}

						}

					} else if (buyType[i] == 2) {
						for (int record = (int) buyIndex[i]+1; record < patternIndex[i]
								+ (laterNumber + 1) * timeRange; record++) {
							if (buy[i] - lowest[record] >= firstProfit) {
								//System.out.println("賣出時間"+date[record]);
								totalProfit += firstProfit * (firstLotPercent/100);
								getInto = 1;
								bw.write("Sell單 恭喜你在" + buy[i] + "買進" + "在" + (buy[i] - firstProfit) + "賣出" + " 買進時間"
										+ date[(int) buyIndex[i]] + " 買進時間段" + date[patternIndex[i]]+" 賣出時間 "+date[record] + "當根量"
										+ volumeNow[patternIndexNow[i]] + "前一根量" + volumeNow[patternIndexNow[i] - 1]
										+ "   獲利為" + firstProfit + "點");
								System.out.println("Sell單 恭喜你在" + buy[i] + "買進" + "在" + (buy[i] - firstProfit) + "賣出"
										+ " 買進時間" + date[(int) buyIndex[i]] + " 買進時間段" + date[patternIndex[i]] +" 賣出時間 "+date[record]+ "當根量"
										+ volumeNow[patternIndexNow[i]] + "前一根量" + volumeNow[patternIndexNow[i] - 1]
										+ "   獲利為" + firstProfit + "點");
								bw.newLine();
								bw.flush();

								for (; record < patternIndex[i] + (laterNumber + 1) * timeRange; record++) {
									if (buy[i] - lowest[record] >= secondProfit) {
										result = 1;
										totalProfit += secondProfit * (secondLotPercent/100);
										profitTime++;
										bw.write("Sell單 恭喜你在" + (buy[i] - secondProfit) + "又成功賣出了   獲利為" + secondProfit
												+ "點"+" 賣出時間 "+date[record]+"\n");
										System.out.println("Sell單 恭喜你在" + (buy[i] - secondProfit) + "又成功賣出了   獲利為"
												+ secondProfit + "點"+" 賣出時間 "+date[record]+"\n");
										bw.newLine();
										bw.write("=======================================================================");
										bw.newLine();
										bw.flush();
										result = 0;
										/* important */ getInto = 0;
										break;
									} else if (record == patternIndex[i] + (laterNumber + 1) * timeRange - 1
											&& result == 0) {
										if (firstProfit + (buy[i]
												- close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) < 0) {
											lossTime++;
										} else {
											profitTime++;
										}
										totalProfit += (buy[i]
												- close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])*(secondLotPercent/100);
										bw.write("(停損)Sell單 可惜在"
												+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])
												+ "停損了 停損時間" + date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]
												+ " 買進時間" + date[(int) buyIndex[i]] + "\n" + "當根量"
												+ volumeNow[patternIndexNow[i]] + "前一根量" + volumeNow[patternIndexNow[i] - 1]
												+ "   獲利為"
												+ (buy[i] - close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])
												+ "點\n");
										System.out.println("(停損)Sell單 可惜在"
												+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])
												+ "停損了 停損時間" + date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]
												+ " 買進時間" + date[(int) buyIndex[i]] + "\n" + "當根量"
												+ volumeNow[patternIndexNow[i]] + "前一根量" + volumeNow[patternIndexNow[i] - 1]
												+ "   獲利為"
												+ (buy[i] - close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1])
												+ "點\n");
										bw.newLine();
										bw.write("=======================================================================");
										bw.newLine();
										bw.flush();
										break;
									}
								}

								break;

							} else if (getInto == 0 && record == patternIndex[i] + (laterNumber + 1) * timeRange - 1) {

								if (buy[i] - close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] > 0) {
									profitTime++;
								} else {
									lossTime++;
								}
								totalProfit += buy[i] - close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1];
								bw.write("(停損Sell)單 在" + buy[i] + "買進 可惜在"
										+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) + "停損了 停損時間"
										+ date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] + " 買進時間"
										+ date[(int) buyIndex[i]] + "當根量" + volumeNow[patternIndexNow[i]] + "前一根量"
										+ volumeNow[patternIndexNow[i] - 1] + "   獲利為"
										+ (buy[i] - close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) + "\n");
								System.out.println("(停損Sell)單 在" + buy[i] + "買進 可惜在"
										+ (close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) + "停損了 停損時間"
										+ date[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1] + " 買進時間"
										+ date[(int) buyIndex[i]] + "當根量" + volumeNow[patternIndexNow[i]] + "前一根量"
										+ volumeNow[patternIndexNow[i] - 1] + "   獲利為"
										+ (buy[i] - close[patternIndex[i] + ((laterNumber + 1) * timeRange) - 1]) + "\n");
								bw.newLine();
								bw.write("=======================================================================");
								bw.newLine();
								bw.flush();
								break;

							}

						}

					}

				}
			}

			
			//以下為計算買進後有無虧損大於maxLoss
			for(int i =0;i<patternCount;i++){
				for(int k =(int)buyIndex[i];k<patternIndex[i] + (laterNumber + 1) * timeRange;k++){
					if(buyType[i]==1){
						if(buy[i]-lowest[k]>=maxLoss){
							System.out.println("BUY單   在"+date[k]+"時達到預設最大損失 買進價"+buy[i]+"當前價格"+lowest[i]);
							bw.write("BUY單   在"+date[k]+"時達到預設最大損失 買進價"+buy[i]+"當前價格"+lowest[i]);
							bw.newLine();
							break;
						}
					}
					
					if(buyType[i]==2){
						if(highest[k]-buy[i]>=maxLoss){
							System.out.println("Sell單   在"+date[k]+"時達到預設最大損失 買進價"+buy[i]+"當前價格"+highest[i]);
							bw.write("Sell單   在"+date[k]+"時達到預設最大損失 買進價"+buy[i]+"當前價格"+highest[i]);
							bw.newLine();
							break;
						}
					}
					
				}
			}
			bw.write("pattern共出現" + patternCount + "次  成交" + (profitTime + lossTime) + "次  總獲利 " + totalProfit + " 獲利次數 "
					+ profitTime + " 虧損次數 " + lossTime + " 勝率"
					+ ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "%");
			System.out.println("pattern共出現" + patternCount + "次  成交" + (profitTime + lossTime) + "次  總獲利 " + totalProfit
					+ " 獲利次數 " + profitTime + " 虧損次數 " + lossTime + " 勝率"
					+ ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "%");
			bw.newLine();
			bw.newLine(); //    \r\n
			bw.newLine();
			if(totalProfit>200&(((double) profitTime / (double) (profitTime + lossTime)) * 100)>85){
			//bw2.write("pattern共出現" + patternCount + "次  成交" + (profitTime + lossTime) + "次  總獲利 " + totalProfit + " 獲利次數 "
					//+ profitTime + " 虧損次數 " + lossTime + " 勝率"
					//+ ((double) profitTime / (double) (profitTime + lossTime)) * 100 + "%");
			//bw2.newLine();
			//bw2.flush();
			}
			System.out.println("總獲利計算方式為 (第一次停利點) " + firstProfit + " x (第一次停利點賣出的倉位百分比) " + firstLotPercent  + "%"
					+ " + (第二次停利點) " + secondProfit + " x (第二次停利點賣出的倉位百分比)" + secondLotPercent  + "%");
			bw.write("總獲利計算方式為 (第一次停利點) " + firstProfit + " x (第一次停利點賣出的倉位百分比) " + firstLotPercent + "%"
					+ " + (第二次停利點) " + secondProfit + " x (第二次停利點賣出的倉位百分比)" + secondLotPercent  + "%");
			bw.flush();
			bw.close();
			
			if(totalProfit>200&(((double) profitTime / (double) (profitTime + lossTime)) * 100)>85){
			//bw2.write("參數設定為 timeRange = "+timeRange+" spread = "+spread+" laterNumber = "+laterNumber+" multiple = "+multiple+" markup = "+markup+" firstProfit = "+firstProfit+" secondProfit = "+secondProfit+" spreadChecking = "+spreadChecking);
			//bw2.newLine();
			//bw2.newLine();
			//bw2.newLine();
			//bw2.flush();
			}
		brr.close();
		bw.close();
		
		
	}

	
	
	// 取得目前工作目錄
	public static String workPath() {
		return System.getProperty("user.dir");
	}
	
	/**
	 * writeTxt  對filePath此檔案寫入writeString。 (存取HTML)  (true=不會覆蓋接著, false=會覆蓋)
	 * @param filePath		檔案路徑
	 * @param writeString	寫入內容
	 */
	public static void writeTxt(String filePath, String writeString ,boolean noCover) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath, noCover), "UTF-8"));
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
}
