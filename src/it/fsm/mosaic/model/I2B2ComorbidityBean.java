package it.fsm.mosaic.model;

import java.util.Date;

public class I2B2ComorbidityBean {
	
	private String conceptCd;
	private Date startDate;
	
	public I2B2ComorbidityBean(){
		
	}

	public String getConceptCd() {
		return conceptCd;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setConceptCd(String conceptCd) {
		this.conceptCd = conceptCd;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
