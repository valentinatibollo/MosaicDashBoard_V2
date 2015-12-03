package it.fsm.mosaic.model;

public class HistogramObject {
	private String[] xAxisLabelArray;
	private int[] frequencyArray;
	
	public HistogramObject(String [] xAxis, int[] freq){
		this.xAxisLabelArray = xAxis;
		this.frequencyArray = freq;
		
	}

	public String[] getxAxisLabelArray() {
		return xAxisLabelArray;
	}

	public int[] getFrequencyArray() {
		return frequencyArray;
	}

	public void setxAxisLabelArray(String[] xAxisLabelArray) {
		this.xAxisLabelArray = xAxisLabelArray;
	}

	public void setFrequencyArray(int[] frequencyArray) {
		this.frequencyArray = frequencyArray;
	}

}
