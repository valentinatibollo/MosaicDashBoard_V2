package it.fsm.mosaic.model;

import java.util.Date;

public class WeightBean {
	
	private String weightDescr;
	private Date startDate;
	private Date endDate;
	
	public WeightBean(){
		
	}

	public String getWeightDescr() {
		return weightDescr;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setWeightDescr(String weightDescr) {
		this.weightDescr = weightDescr;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
