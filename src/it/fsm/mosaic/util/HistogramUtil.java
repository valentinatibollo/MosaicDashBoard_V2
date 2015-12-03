package it.fsm.mosaic.util;

import it.fsm.mosaic.model.HistogramObject;

import java.util.ArrayList;
import java.util.List;

public class HistogramUtil {

	public static HistogramObject calculateBin(int min, int max, double numClasses, int[] durationArray){
		//		int max = 12218;
		//		double numClasses = 4.333333;
		//		int[] durationArray = {4957,6035,3447,3824,943,10103,1322,5370,382,12218};
		if(min<0 && max <0){
			//calcolo minimo e massimo
			int myMin =0;
			int myMax =0;
			for(int k=0; k<durationArray.length; k++){
				if(k==0){
					myMin = durationArray[k];
					myMax = durationArray[k];
				}else{
					if(durationArray[k]>= myMax) myMax = durationArray[k];
					if(durationArray[k]<= myMin) myMin = durationArray[k];
				}	
			}
			min = myMin;
			max = myMax;
			//System.out.println("IN COMPLICATION NO MIN NO MAX: Massimo: "+max+" - Minimo: "+min);
		}
		
		double ampiezza = (max-min)/numClasses;
		Double ampiezzaRounded = Math.ceil(ampiezza);
		int ampiezzaInteger = ampiezzaRounded.intValue();
		//System.out.println("Massimo: "+max+" - NumeroClassi: "+numClasses+" - AmpiezzaNotRounded: "+ampiezza+" - AmpiezzaRounded: "+ampiezzaInteger);
		List<String> binLabelList = new ArrayList<String>();
		int lastBound = 0;
		int firstBound = min;
		List<Integer> firstBoundList = new ArrayList<Integer>();
		List<Integer> lastBoundList = new ArrayList<Integer>();
		while(lastBound<max){
			lastBound = firstBound+ampiezzaInteger;
			String binLabel = "["+firstBound+"-"+lastBound+"]";
			binLabelList.add(binLabel);
			firstBoundList.add(firstBound);
			lastBoundList.add(lastBound);
			firstBound = lastBound;
		}
		String[] binLabelArray = binLabelList.toArray(new String[binLabelList.size()]);
		Integer[] firstBoundArray = firstBoundList.toArray(new Integer[firstBoundList.size()]);
		Integer[] lastBoundArray = lastBoundList.toArray(new Integer[lastBoundList.size()]);
//		for(int i=0; i< binLabelArray.length; i++){
//			System.out.println(binLabelArray[i]);
//		}

		int[] freqArray = new int[binLabelArray.length];
		if(binLabelArray.length==1){
			for(int j=0; j < durationArray.length; j++){
				freqArray[0] = freqArray[0]+1;
			}
		}else{
			for(int j=0; j < durationArray.length; j++){
				for(int k=0; k < firstBoundArray.length; k++){
					if(durationArray[j]>= firstBoundArray[k] && durationArray[j]<= lastBoundArray[k]){
						freqArray[k] = freqArray[k]+1;
						break;
					}
				}
//				double divisione = durationArray[j]/ampiezzaInteger;
//				Double kLong = Math.floor(divisione);
//				int k = kLong.intValue()-1;
//				freqArray[k] = freqArray[k]+1;
			}
		}
		HistogramObject result = new HistogramObject(binLabelArray, freqArray);
		return result;
	}


}
