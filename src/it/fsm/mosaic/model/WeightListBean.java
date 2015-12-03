package it.fsm.mosaic.model;

import java.util.Date;
import java.util.List;

public class WeightListBean {
	
	private List<WeightBean> weightList;
	private Date maxEndDate;
	
	public WeightListBean(){
		
	}

	public List<WeightBean> getWeightList() {
		return weightList;
	}

	public Date getMaxEndDate() {
		return maxEndDate;
	}

	public void setWeightList(List<WeightBean> weightList) {
		this.weightList = weightList;
	}

	public void setMaxEndDate(Date maxEndDate) {
		this.maxEndDate = maxEndDate;
	}

}
