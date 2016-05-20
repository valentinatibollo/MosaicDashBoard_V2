package it.fsm.mosaic.servlet;

import it.fsm.mosaic.model.CacheMongoObject;
import it.fsm.mosaic.model.HistogramObject;
import it.fsm.mosaic.model.I2B2ComorbidtyObservation;
import it.fsm.mosaic.model.I2B2Observation;
import it.fsm.mosaic.model.I2B2TherapyObservation;
import it.fsm.mosaic.model.WeightBean;
import it.fsm.mosaic.model.WeightListBean;
import it.fsm.mosaic.mongodb.MongoDbUtil;
import it.fsm.mosaic.util.DBUtil;
import it.fsm.mosaic.util.HistogramUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@SuppressWarnings("serial")
public class I2B2Servlet extends HttpServlet{
	private static Logger log = Logger.getLogger(I2B2Servlet.class);

	private static final String observationTable = "OBSERVATION_FACT";
	private static final String observationTableOld = "OBSERVATION_FACT_OLD";
	private static final HashMap<String, Integer> complicationsMap = new HashMap<String, Integer>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		PrintWriter out = resp.getWriter();
		String step = req.getParameter("step");
		String chartType = req.getParameter("chart_type");

		if(step.equalsIgnoreCase("1")){
			String sessionId = req.getParameter("session_id");
			if(chartType.equalsIgnoreCase("gender")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/GenderPieChart");
				out.println(getI2B2GenderData());
			}
			else if(chartType.equalsIgnoreCase("bmi")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/BMIBarChart");
				out.println(getI2B2BMIData());
			}
			else if(chartType.equalsIgnoreCase("comorbidity")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/ComorbidityPieChart");
				out.println(getI2B2ComorbidityData());
			}
			else if(chartType.equalsIgnoreCase("age_diagnosis")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/AgePieChart");
				out.println(getI2B2AgeDiagnosisData());
			}
			else if(chartType.equalsIgnoreCase("cvr")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/CVRPieChart");
				out.println(getI2B2CardiovascularRiskData());
			}else if (chartType.equalsIgnoreCase("time_to_complication")){
				log.info(sessionId+ "/CenterProfiling/Get/BarChart");
				out.println(getI2B2TimeToComplication());
			}else if (chartType.equalsIgnoreCase("bmi_pie")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/BMIPieChart");
				out.println(getI2B2BMIPIEData());
			}else if (chartType.equalsIgnoreCase("hba1c")){
				log.info(sessionId+ "/GeneralChart_Selection/Get/HBA1CPieChart");
				out.println(getI2B2Hba1cData());
			}
		}else if(step.equalsIgnoreCase("configure_session")){
			//out.println(req.getSession().getId());
			  UUID idOne = UUID.randomUUID();
			  log.info(idOne+ "/Session/Create/SessionId");
			  out.println(idOne);
		}
		else if(step.equalsIgnoreCase("2")){
			try {
				String selectedValue = req.getParameter("selected_value");
				String data_category = req.getParameter("data_category");

				if(chartType.equalsIgnoreCase("gender_process")){
					String sessionId = req.getParameter("session_id");
					String[] sessionIdToken = sessionId.split("_");
					if(data_category.equalsIgnoreCase("LOC")){
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Gender/"+sessionIdToken[1]);
						String data = getI2B2dataByGender_LOC(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Gender/"+sessionIdToken[1]+"/Get_LOC");
					}
					else if(data_category.equalsIgnoreCase("CVR")){
						String data = getI2B2dataByGender_CVR(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Gender/"+sessionIdToken[1]+"/Get_CVR");
					}
					else if(data_category.equalsIgnoreCase("DRUG")){
						String data = getI2B2dataByGender_DRUG(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Gender/"+sessionIdToken[1]+"/Get_DRUG");
					}
					else if(data_category.equalsIgnoreCase("COMPLICATION")){
						String data = getI2B2dataByGender_COMPLICATION(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Gender/"+sessionIdToken[1]+"/Get_COMPLICATION");
					}
					else if(data_category.equalsIgnoreCase("HOSPITALIZATION")){
						String data = getI2B2dataByGender_HOSPITALIZATION(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Gender/"+sessionIdToken[1]+"/Get_HOSPITALIZATION");
					}
				}
				else if(chartType.equalsIgnoreCase("age_process")){
					String sessionId = req.getParameter("session_id");
					String[] sessionIdToken = sessionId.split("_");
					if(data_category.equalsIgnoreCase("LOC")){
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_"+sessionIdToken[1]);
						String data = getI2B2dataByAge_LOC(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_"+sessionIdToken[1]+"/Get_LOC");
					}
					else if(data_category.equalsIgnoreCase("CVR")){
						String data = getI2B2dataByAge_CVR(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_"+sessionIdToken[1]+"/Get_CVR");
					}
					else if(data_category.equalsIgnoreCase("DRUG")){
						String data = getI2B2dataByAge_DRUG(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_"+sessionIdToken[1]+"/Get_DRUG");
					}
					else if(data_category.equalsIgnoreCase("COMPLICATION")){
						String data = getI2B2dataByAge_COMPLICATION(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_"+sessionIdToken[1]+"/Get_COMPLICATION");
					}
					else if(data_category.equalsIgnoreCase("HOSPITALIZATION")){
						String data = getI2B2dataByAge_HOSPITALIZATION(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_"+sessionIdToken[1]+"/Get_HOSPITALIZATION");
					}

				}
				else if(chartType.equalsIgnoreCase("cvr_process")){
					String sessionId = req.getParameter("session_id");
					String[] sessionIdToken = sessionId.split("_");
					if(data_category.equalsIgnoreCase("LOC")){
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_CVR/"+sessionIdToken[1]);
						String data = getI2B2dataByCVR_LOC(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_CVR/"+sessionIdToken[1]+"/Get_LOC");
					}
					else if(data_category.equalsIgnoreCase("CVR")){
						String data = getI2B2dataByCVR_CVR(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_CVR/"+sessionIdToken[1]+"/Get_CVR");
					}
					else if(data_category.equalsIgnoreCase("DRUG")){
						String data = getI2B2dataByCVR_DRUG(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_CVR/"+sessionIdToken[1]+"/Get_DRUG");
					}
					else if(data_category.equalsIgnoreCase("COMPLICATION")){
						String data = getI2B2dataByCVR_COMPLICATION(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_CVR/"+sessionIdToken[1]+"/Get_COMPLICATION");
					}
					else if(data_category.equalsIgnoreCase("HOSPITALIZATION")){
						String data = getI2B2dataByCVR_HOSPITALIZATION(Integer.parseInt(selectedValue));
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_CVR/"+sessionIdToken[1]+"/Get_HOSPITALIZATION");
					}
				}
				else if(chartType.equalsIgnoreCase("comorb_process")){
					String sessionId = req.getParameter("session_id");
					String[] sessionIdToken = sessionId.split("_");
					if(data_category.equalsIgnoreCase("LOC")){
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Complications/"+sessionIdToken[1]);
						String data = getI2B2dataByComorb_LOC(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Complications/"+sessionIdToken[1]+"/Get_LOC");
					}
					else if(data_category.equalsIgnoreCase("CVR")){
						String data = getI2B2dataByComorb_CVR(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Complications/"+sessionIdToken[1]+"/Get_CVR");
					}
					else if(data_category.equalsIgnoreCase("DRUG")){
						String data = getI2B2dataByComorb_DRUG(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Complications/"+sessionIdToken[1]+"/Get_DRUG");
					}
					else if(data_category.equalsIgnoreCase("COMPLICATION")){
						String data = getI2B2dataByComorb_COMPLICATION(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Complications/"+sessionIdToken[1]+"/Get_COMPLICATION");
					}
					else if(data_category.equalsIgnoreCase("HOSPITALIZATION")){
						String data = getI2B2dataByComorb_HOSPITALIZATION(selectedValue);
						out.println(callProcessWS(data_category,data));
						log.info(sessionIdToken[0]+ "/GeneralChart_Selection/FilterAction_Complications/"+sessionIdToken[1]+"/Get_HOSPITALIZATION");
					}
				}
				else if(chartType.equalsIgnoreCase("all_patient_process")){
					String sessionId = req.getParameter("session_id");	
					if(data_category.equalsIgnoreCase("LOC")){
						log.info(sessionId.concat("/GeneralChart_Selection/Filter/AllPatients"));
						String data = getI2B2dataAllPatients_LOC();
						out.println(callProcessWS(data_category,data));
						log.info(sessionId+ "/GeneralChart_Selection/SelectAllPatients/Get_LOC");
					}
					else if(data_category.equalsIgnoreCase("CVR")){
						String data = getI2B2dataAllPatients_CVR();
						out.println(callProcessWS(data_category,data));
						log.info(sessionId+ "/GeneralChart_Selection/SelectAllPatients/Get_CVR");
					}
					else if(data_category.equalsIgnoreCase("DRUG")){
						String data = getI2B2dataAllPatients_DRUG();
						out.println(callProcessWS(data_category,data));
						log.info(sessionId+ "/GeneralChart_Selection/SelectAllPatients/Get_DRUG");
					}
					else if(data_category.equalsIgnoreCase("COMPLICATION")){
						String data = getI2B2dataAllPatients_COMPLICATION();
						out.println(callProcessWS(data_category,data));
						log.info(sessionId+ "/GeneralChart_Selection/SelectAllPatients/Get_COMPLICATION");
					}
					else if(data_category.equalsIgnoreCase("HOSPITALIZATION")){
						String data = getI2B2dataAllPatients_HOSPITALIZATION();
						out.println(callProcessWS(data_category,data));
						log.info(sessionId+ "/GeneralChart_Selection/SelectAllPatients/Get_HOSPITALIZATION");
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else if(step.equalsIgnoreCase("3")){
			String patientNums = req.getParameter("patient_nums");
			String[] chartTypeToken = chartType.split(";");
			if(chartTypeToken[0].equalsIgnoreCase("comorb")){
				log.info(chartTypeToken[1]+ "/GeneralChart_TemporalPatterns/"+chartTypeToken[2]);
				String durationNums = req.getParameter("duration_nums");
				String num_classes = req.getParameter("num_classes");
				String max_duration = req.getParameter("max_duration");
				String min_duration = req.getParameter("min_duration");
				out.println(getI2B2DataForDrillDown(patientNums, durationNums, num_classes, max_duration,min_duration));
			}
		}else if(step.equalsIgnoreCase("0")){
			String patientId = req.getParameter("patient_id");
			String sessionId = req.getParameter("session_id");
			if(chartType.equalsIgnoreCase("hba1c")){
				out.println(getI2B2DataForHba1c(patientId));
			}else if (chartType.equalsIgnoreCase("therapy")){
				out.println(getI2B2DataForTherapy(patientId));
			}else if (chartType.equalsIgnoreCase("adherence")){
				out.println(getI2B2DataForTherapyAdherence(patientId));
			}else if (chartType.equalsIgnoreCase("adherence2")){
				out.println(getI2B2DataForTherapyAdherence2(patientId));
			}else if (chartType.equalsIgnoreCase("diet")){
				out.println(getI2B2DataForDiet(patientId));
			}else if (chartType.equalsIgnoreCase("atcList")){
				out.println(getI2B2DataForAtcList(patientId));
			}else if (chartType.equalsIgnoreCase("adherence3")){
				out.println(getI2B2DataForTherapyAdherence3(patientId));
			}else if (chartType.equalsIgnoreCase("adherence3Filtered")){
				String atcFilter = req.getParameter("atc_filter");
				out.println(getI2B2DataForTherapyAdherence3Filtered(patientId, atcFilter));
				sessionId = sessionId.concat("/").concat(atcFilter);
			}else if (chartType.equalsIgnoreCase("loc")){
				out.println(getI2B2DataForLOC(patientId));
			}else if (chartType.equalsIgnoreCase("cvr")){
				out.println(getI2B2DataForCVR(patientId));
			}else if (chartType.equalsIgnoreCase("weight")){
				out.println(getI2B2DataForWeight(patientId));
			}else if (chartType.equalsIgnoreCase("complication")){
				out.println(getI2B2DataForComplication(patientId));
			}else if (chartType.equalsIgnoreCase("complication2")){
				out.println(getI2B2DataForComplication2(patientId));
			}else if(chartType.equalsIgnoreCase("trafficlights")){
				out.println(getInfo4Trafficlights(patientId));
			}else if(chartType.equalsIgnoreCase("weightraw")){
				out.println(getI2B2DataForWeightRaw(patientId));
			}else if(chartType.equalsIgnoreCase("mvrR")){
				out.println(getI2B2DataForMvrRetinopaty(patientId));
			}else if(chartType.equalsIgnoreCase("mvrNu")){
				out.println(getI2B2DataForMvrNeuropaty(patientId));
			}else if(chartType.equalsIgnoreCase("mvrNe")){
				out.println(getI2B2DataForMvrNephropaty(patientId));
			}
			log.info(sessionId.concat("/").concat(patientId));
		}else if(step.equalsIgnoreCase("log")){
			log.info(chartType);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String dataIn = req.getParameter("data_in");
		out.println(getData4ComplicationDrillDown(dataIn));
	}

	@SuppressWarnings("unchecked")
	private String getI2B2GenderData() {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.NAME_CHAR, count(t1.PATIENT_NUM) as count " +
					"from "+observationTable+" t1, CONCEPT_DIMENSION  t2 " +
					"where t1.CONCEPT_CD in (?,?) " +
					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
					"GROUP by t2.NAME_CHAR";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("gender_male"));
			pstmt.setString(2, prop.getProperty("gender_female"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Gender");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", rs.getString(1));

				JSONObject row_2 = new JSONObject();
				row_2.put("v", rs.getInt(2));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}

			obj.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2B2BMIData() {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, q1.START_DATE, q1.NVAL_NUM " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) " +
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("bmi"));

			rs = pstmt.executeQuery();

			List<I2B2Observation> obs = new ArrayList<I2B2Observation>();

			while(rs.next()){
				I2B2Observation ob = new I2B2Observation();
				ob.setPatientNum(rs.getInt(1));
				ob.setStartDate(rs.getDate(2));
				ob.setnValNum(rs.getDouble(3));
				obs.add(ob);
			}
			jsonText = getBMIjson(obs);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2ComorbidityData(){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			//get counter for MACRO MICRO NONVASCULAR CLASS
			String sql = "select concept_cd, observation_blob, patient_num " +
					"from "+observationTable+" t1 " +
					"where t1.CONCEPT_CD  like ? ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));

			rs = pstmt.executeQuery();
			HashMap<String, I2B2ComorbidtyObservation> comorbidityMap = new HashMap<String, I2B2ComorbidtyObservation>();

			while(rs.next()){
				String conceptCd = rs.getString("concept_cd");
				Integer patientNumFromRS = rs.getInt("patient_num");
				String complClass = conceptCd.substring(0,6);
				I2B2ComorbidtyObservation cObs = comorbidityMap.get(conceptCd);
				if(cObs==null){ //concept_cd non presente
					//metto l'obs relativa alla singola comorbidità
					I2B2ComorbidtyObservation cObsNew = new I2B2ComorbidtyObservation();
					List<Integer> patientNumList = new ArrayList<Integer>();
					patientNumList.add(patientNumFromRS);
					cObsNew.setComorbidityDescr(rs.getString("observation_blob"));
					cObsNew.setPatientNumList(patientNumList);
					cObsNew.setConceptCd(conceptCd);
					comorbidityMap.put(conceptCd, cObsNew);
					//metto l'obs relativa alla classe (MACRO MICRO o NONVASCULAR)					
					I2B2ComorbidtyObservation cObs4Class = comorbidityMap.get(complClass);
					if(cObs4Class==null){ //classe non presente
						I2B2ComorbidtyObservation cObsNew4Class = new I2B2ComorbidtyObservation();
						List<Integer> patientNumList4Class = new ArrayList<Integer>();
						patientNumList4Class.add(rs.getInt("patient_num"));
						if(complClass.equals("COM|MA")){
							cObsNew4Class.setComorbidityDescr("Macro");
							cObsNew4Class.setConceptCd("_Macro");
							cObsNew.setComorbClassId(0);
						}else if(complClass.equals("COM|MI")){
							cObsNew4Class.setComorbidityDescr("Micro");
							cObsNew4Class.setConceptCd("_Micro");
							cObsNew.setComorbClassId(1);
						}else if(complClass.equals("COM|NV")){
							cObsNew4Class.setComorbidityDescr("Non vascular");
							cObsNew4Class.setConceptCd("_NotVascular");
							cObsNew.setComorbClassId(2);
						}
						cObsNew4Class.setPatientNumList(patientNumList4Class);
						comorbidityMap.put(complClass, cObsNew4Class);
					}else{ //classe presente
						//controllo se c'è il patientNum
						List<Integer> patientNumList4Class2 = cObs4Class.getPatientNumList();
						if(!patientNumList4Class2.contains(patientNumFromRS)){ //se non c'è lo aggiungo
							patientNumList4Class2.add(patientNumFromRS);
						}
						//setto la classe
						if(complClass.equals("COM|MA")){
							cObsNew.setComorbClassId(0);
						}else if(complClass.equals("COM|MI")){
							cObsNew.setComorbClassId(1);
						}else if(complClass.equals("COM|NV")){
							cObsNew.setComorbClassId(2);
						}
					}	
				}else{ //concept_cd già presente
					//controllo il paziente
					List<Integer> patientNumList4Obs = cObs.getPatientNumList();
					if(!patientNumList4Obs.contains(patientNumFromRS)){
						patientNumList4Obs.add(patientNumFromRS);
						//controllo se il paz c'è nelle classi (potrebbe anche esserci già)
						I2B2ComorbidtyObservation cObs4Class = comorbidityMap.get(complClass); //la classe c'è di sicuro xke c'è il conceptcd
						List<Integer> patientNumList4Class2 = cObs4Class.getPatientNumList();
						if(!patientNumList4Class2.contains(patientNumFromRS)){ //se non c'è lo aggiungo
							patientNumList4Class2.add(patientNumFromRS);
						}
					}
					//se il paz c'è qui, c'è anche nella categoria macro (x forza, quindi non controllo nemmeno)				
				}
			}
			//Creo gli oggetti
			JSONObject objOuter = new JSONObject();
			JSONObject objComplicationClassContainer = new JSONObject();
			JSONObject objMacroContainer = new JSONObject();
			JSONObject objMicroContainer = new JSONObject();
			JSONObject objNonVascularContainer = new JSONObject();

			JSONObject objComplicationClassChartData = new JSONObject();
			JSONObject objMacroChartData = new JSONObject();
			JSONObject objMicroChartData = new JSONObject();
			JSONObject objNonVascularChartData= new JSONObject();

			JSONArray objComplicationClassRawData = new JSONArray();
			JSONArray objMacroRawData = new JSONArray();
			JSONArray objMicroRawData = new JSONArray();
			JSONArray objNonVascularRawData = new JSONArray();

			objComplicationClassContainer.put("chart_data",objComplicationClassChartData);
			objMacroContainer.put("chart_data",objMacroChartData);
			objMicroContainer.put("chart_data",objMicroChartData);
			objNonVascularContainer.put("chart_data",objNonVascularChartData);
			objComplicationClassContainer.put("raw_data",objComplicationClassRawData);
			objMacroContainer.put("raw_data",objMacroRawData);
			objMicroContainer.put("raw_data",objMicroRawData);
			objNonVascularContainer.put("raw_data",objNonVascularRawData);

			JSONArray colsClass = new JSONArray();
			JSONArray colsMacro = new JSONArray();
			JSONArray colsMicro = new JSONArray();
			JSONArray colsNotVascular = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			colsClass.add(col_1);
			colsClass.add(col_2);
			colsMacro.add(col_1);
			colsMacro.add(col_2);
			colsMicro.add(col_1);
			colsMicro.add(col_2);
			colsNotVascular.add(col_1);
			colsNotVascular.add(col_2);

			objComplicationClassChartData.put("cols", colsClass);
			objMacroChartData.put("cols", colsMacro);
			objMicroChartData.put("cols", colsMicro);
			objNonVascularChartData.put("cols", colsNotVascular);

			Set<String> keys = comorbidityMap.keySet();
			List<I2B2ComorbidtyObservation> obsClassList = new ArrayList<I2B2ComorbidtyObservation>();
			List<I2B2ComorbidtyObservation> obsList = new ArrayList<I2B2ComorbidtyObservation>();
			for(String key : keys){
				I2B2ComorbidtyObservation obs = comorbidityMap.get(key);
				if(obs.getConceptCd().startsWith("_")){
					obsClassList.add(obs);
				}else{
					obsList.add(obs);
				}
			}
			//ordine alfabetico in base a observation_blob in modo che il piechart non mi incasini le slice
			Collections.sort(obsClassList, I2B2ComorbidtyObservation.nameComparator);
			Collections.sort(obsList, I2B2ComorbidtyObservation.nameComparator);

			JSONArray rows = new JSONArray();
			for(I2B2ComorbidtyObservation ob : obsClassList){			
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", ob.getComorbidityDescr());

				JSONObject row_2 = new JSONObject();
				row_2.put("v", ob.getPatientNumList().size());

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);

				String patientList = ob.createPatientNumListString();
				JSONObject raw_data= new JSONObject();
				raw_data.put("patient_nums", patientList);
				objComplicationClassRawData.add(raw_data);

			}
			objComplicationClassChartData.put("rows", rows);

			JSONArray rowsMacro = new JSONArray();
			JSONArray rowsMicro = new JSONArray();
			JSONArray rowsNotVascular = new JSONArray();
			for(I2B2ComorbidtyObservation ob : obsList){			
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", ob.getComorbidityDescr());

				JSONObject row_2 = new JSONObject();
				row_2.put("v", ob.getPatientNumList().size());

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);

				String patientList = ob.createPatientNumListString();
				JSONObject raw_data= new JSONObject();
				raw_data.put("patient_nums", patientList);

				if(ob.getComorbClassId()==0){
					rowsMacro.add(row_obj);
					objMacroRawData.add(raw_data);
				}else if (ob.getComorbClassId()==1){
					rowsMicro.add(row_obj);
					objMicroRawData.add(raw_data);
				}else if (ob.getComorbClassId()==2){
					rowsNotVascular.add(row_obj);
					objNonVascularRawData.add(raw_data);
				}
			}
			objMacroChartData.put("rows", rowsMacro);
			objMicroChartData.put("rows", rowsMicro);
			objNonVascularChartData.put("rows", rowsNotVascular);

			objOuter.put("comorb_class", objComplicationClassContainer);
			objOuter.put("macro", objMacroContainer);
			objOuter.put("micro", objMicroContainer);
			objOuter.put("not_vascular", objNonVascularContainer);

			StringWriter out = new StringWriter();
			objOuter.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2CardiovascularRiskData(){

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			//			String sql = "select t2.NAME_CHAR, count(distinct t1.PATIENT_NUM) as count " +
			//					"from "+observationTable+"  t1, I2B2DEMODATA.CONCEPT_DIMENSION  t2 " +
			//					"where t1.CONCEPT_CD  like (?) " +
			//					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
			//					"GROUP by t2.NAME_CHAR";

			String sql = "select c.name_char, count(patient_num) as count "+
					"from (select patient_num, concept_cd, start_date, rank() over "
					+ "(partition by patient_num order by start_date desc) rn from "
					+observationTable+" where CONCEPT_CD  like (?)) s, concept_dimension c "
					+ "where c.concept_cd = s.concept_cd and s.rn=1 "
					+ "group by c.name_char order by c.name_char";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", rs.getString(1));
				//row_1.put("f", null);

				JSONObject row_2 = new JSONObject();
				row_2.put("v", rs.getInt(2));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}

			obj.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2AgeDiagnosisData(){

		int age_range_0_10 = 0;
		int age_range_10_20 = 0;
		int age_range_20_30 = 0;
		int age_range_30_40 = 0;
		int age_range_40_50 = 0;
		int age_range_50_60 = 0;
		int age_range_60_70 = 0;
		int age_range_70_80 = 0;
		int age_range_80_90 = 0;
		int age_range_90_100 = 0;
		int age_range_sup_100 = 0;

		String patient_num_0_10 = "";
		String patient_num_10_20 = "";
		String patient_num_20_30 = "";
		String patient_num_30_40 = "";
		String patient_num_40_50 = "";
		String patient_num_50_60 = "";
		String patient_num_60_70 = "";
		String patient_num_70_80 = "";
		String patient_num_80_90 = "";
		String patient_num_90_100 = "";
		String patient_num_sup_100 = "";

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.PATIENT_NUM, t2.NVAL_NUM as \"YOD\", q1.NVAL_NUM as \"YOB\", t2.NVAL_NUM-q1.NVAL_NUM as \"AGE\" " +
					"from "+observationTable+" t2, " +
					"(select t1.PATIENT_NUM, t1.NVAL_NUM " +
					"from "+observationTable+" t1 " +
					"where t1.CONCEPT_CD like ?) q1 " + //YOB
					"where t2.PATIENT_NUM = q1.patient_num " +
					"and t2.CONCEPT_CD like ?"; //AN:Y

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("year_of_birth"));
			pstmt.setString(2, prop.getProperty("year_of_diagnosis"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();
			JSONArray raw_values = new JSONArray(); 
			JSONObject chart_json = new JSONObject();

			obj.put("raw_values", raw_values);
			obj.put("chart_json", chart_json);

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "AgeRange");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			chart_json.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				int age = rs.getInt(4);

				if(age<=10){
					age_range_0_10++;
					patient_num_0_10 = patient_num_0_10.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>10 && age<=20){
					age_range_10_20++;
					patient_num_10_20 = patient_num_10_20.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>20 && age<=30){
					age_range_20_30++;
					patient_num_20_30 = patient_num_20_30.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>30 && age<=40){
					patient_num_30_40 = patient_num_30_40.concat("-").concat(Integer.toString(rs.getInt(1)));
					age_range_30_40++;
				}
				else if(age>40 && age<=50){
					age_range_40_50++;
					patient_num_40_50 = patient_num_40_50.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>50 && age<=60){
					age_range_50_60++;
					patient_num_50_60 = patient_num_50_60.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>60 && age<=70){
					age_range_60_70++;
					patient_num_60_70 = patient_num_60_70.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>70 && age<=80){
					age_range_70_80++;
					patient_num_70_80 = patient_num_70_80.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>80 && age<=90){
					age_range_80_90++;
					patient_num_80_90 = patient_num_80_90.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>90 && age<=100){
					age_range_90_100++;
					patient_num_90_100 = patient_num_90_100.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>100){
					age_range_sup_100++;
					patient_num_sup_100 = patient_num_sup_100.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
			}

			HashMap<String, Integer> ageMap = new HashMap<String, Integer>();
			ageMap.put("0-10", age_range_0_10);
			ageMap.put("10-20", age_range_10_20);
			ageMap.put("20-30", age_range_20_30);
			ageMap.put("30-40", age_range_30_40);
			ageMap.put("40-50", age_range_40_50);
			ageMap.put("50-60", age_range_50_60);
			ageMap.put("60-70", age_range_60_70);
			ageMap.put("70-80", age_range_70_80);
			ageMap.put("80-90", age_range_80_90);
			ageMap.put("90-100", age_range_90_100);
			ageMap.put("sup-100", age_range_sup_100);

			HashMap<String, String> patientNumsMap = new HashMap<String, String>();
			patientNumsMap.put("0-10", patient_num_0_10);
			patientNumsMap.put("10-20", patient_num_10_20);
			patientNumsMap.put("20-30", patient_num_20_30);
			patientNumsMap.put("30-40", patient_num_30_40);
			patientNumsMap.put("40-50", patient_num_40_50);
			patientNumsMap.put("50-60", patient_num_50_60);
			patientNumsMap.put("60-70", patient_num_60_70);
			patientNumsMap.put("70-80", patient_num_70_80);
			patientNumsMap.put("80-90", patient_num_80_90);
			patientNumsMap.put("90-100", patient_num_90_100);
			patientNumsMap.put("sup-100", patient_num_sup_100);

			for(int i=0; i<=10; i++){
				String key = "";

				if(i<10){
					key = (i*10)+"-"+((i+1)*10);
				}
				else{
					key ="sup-100";
				}

				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				JSONObject row_2 = new JSONObject();

				row_1.put("v", key);
				row_2.put("v", ageMap.get(key));
				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);

				JSONObject raw_data= new JSONObject();
				raw_data.put("id", i);
				raw_data.put("patient_nums", patientNumsMap.get(key));

				raw_values.add(raw_data);
			}

			chart_json.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	@SuppressWarnings("unchecked")
	private String getI2B2BMIPIEData(){

		int very_severely_underweight = 0;
		int severely_underweight = 0;
		int underweight = 0;
		int normal = 0;
		int overweight = 0;
		int obese_class_i = 0;
		int obese_class_ii = 0;
		int obese_class_iii = 0;

		String patient_very_severely_underweight = "";
		String patient_severely_underweight = "";
		String patient_underweight = "";
		String patient_normal = "";
		String patient_overweight = "";
		String patient_obese_class_i = "";
		String patient_obese_class_ii = "";
		String patient_obese_class_iii = "";

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<Integer> patientList = new ArrayList<Integer>();

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, q1.START_DATE, q1.NVAL_NUM " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("bmi"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();
			JSONArray raw_values = new JSONArray(); 
			JSONObject chart_json = new JSONObject();

			obj.put("raw_values", raw_values);
			obj.put("chart_json", chart_json);

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "BMIRange");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			chart_json.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				double bmi = rs.getDouble(3);
				if(!patientList.contains(rs.getInt(1))){


					//				Very severely underweight 	less than 15 	less than 0.60
					//				Severely underweight 	from 15.0 to 16.0 	from 0.60 to 0.64
					//				Underweight 	from 16.0 to 18.5 	from 0.64 to 0.74
					//				Normal (healthy weight) 	from 18.5 to 25 	from 0.74 to 1.0
					//				Overweight 	from 25 to 30 	from 1.0 to 1.2
					//				Obese Class I (Moderately obese) 	from 30 to 35 	from 1.2 to 1.4
					//				Obese Class II (Severely obese) 	from 35 to 40 	from 1.4 to 1.6
					//				Obese Class III (Very severely obese) 	over 40

					if(bmi<=15.00){
						very_severely_underweight++;
						patient_very_severely_underweight = patient_very_severely_underweight.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(bmi>15.00 && bmi<=16.00){
						severely_underweight++;
						patient_severely_underweight = patient_severely_underweight.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(bmi>16.00 && bmi<=18.5){
						underweight++;
						patient_underweight = patient_underweight.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(bmi>18.5 && bmi<=25.00){
						patient_normal = patient_normal.concat("-").concat(Integer.toString(rs.getInt(1)));
						normal++;
					}
					else if(bmi>25.00 && bmi<=30.00){
						overweight++;
						patient_overweight = patient_overweight.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(bmi>30.00 && bmi<=35.00){
						obese_class_i++;
						patient_obese_class_i = patient_obese_class_i.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(bmi>35.00 && bmi<=40.00){
						obese_class_ii++;
						patient_obese_class_ii = patient_obese_class_ii.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(bmi>40.00){
						obese_class_iii++;
						patient_obese_class_iii = patient_obese_class_iii.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					patientList.add(rs.getInt(1));
				}
			}


			LinkedHashMap<String, Integer> bmiMap = new LinkedHashMap<String, Integer>();
			bmiMap.put("very_severely_underweight", very_severely_underweight);
			bmiMap.put("severely_underweight", severely_underweight);
			bmiMap.put("underweight", underweight);
			bmiMap.put("normal", normal);
			bmiMap.put("overweight", overweight);
			bmiMap.put("obese_class_i", obese_class_i);
			bmiMap.put("obese_class_ii", obese_class_ii);
			bmiMap.put("obese_class_iii", obese_class_iii);

			LinkedHashMap<String, String> patientNumsMap = new LinkedHashMap<String, String>();
			patientNumsMap.put("very_severely_underweight", patient_very_severely_underweight);
			patientNumsMap.put("severely_underweight", patient_severely_underweight);
			patientNumsMap.put("underweight", patient_underweight);
			patientNumsMap.put("normal", patient_normal);
			patientNumsMap.put("overweight", patient_overweight);
			patientNumsMap.put("obese_class_i", patient_obese_class_i);
			patientNumsMap.put("obese_class_ii", patient_obese_class_ii);
			patientNumsMap.put("obese_class_iii", patient_obese_class_iii);

			Set<String> keys = bmiMap.keySet();

			for(String key : keys){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				JSONObject row_2 = new JSONObject();

				row_1.put("v", key);
				row_2.put("v", bmiMap.get(key));
				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);

				JSONObject raw_data= new JSONObject();
				raw_data.put("id", key);
				raw_data.put("patient_nums", patientNumsMap.get(key));

				raw_values.add(raw_data);
			}
			chart_json.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	
	@SuppressWarnings("unchecked")
	private String getI2B2Hba1cData(){

		int class_i = 0;
		int class_ii = 0;
		int class_iii = 0;
		int class_iv = 0;
		int class_v = 0;

		String patient_class_i = "";
		String patient_class_ii = "";
		String patient_class_iii = "";
		String patient_class_iv = "";
		String patient_class_v = "";

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<Integer> patientList = new ArrayList<Integer>();

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, q1.START_DATE, q1.NVAL_NUM " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("hba1c"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();
			JSONArray raw_values = new JSONArray(); 
			JSONObject chart_json = new JSONObject();

			obj.put("raw_values", raw_values);
			obj.put("chart_json", chart_json);

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Hba1cRange");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			chart_json.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				double hba1c = rs.getDouble(3);
//				int class_i = 0;
//				int class_ii = 0;
//				int class_iii = 0;
//				int class_iv = 0;
//				int class_v = 0;
				if(!patientList.contains(rs.getInt(1))){
					if(hba1c<53.00){
						class_i++;
						patient_class_i = patient_class_i.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(hba1c>=53.00 && hba1c<64.00){
						class_ii++;
						patient_class_ii = patient_class_ii.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(hba1c>=64.00 && hba1c<75.00){
						class_iii++;
						patient_class_iii = patient_class_iii.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					else if(hba1c>=75.00 && hba1c<86.00){
						patient_class_iv = patient_class_iv.concat("-").concat(Integer.toString(rs.getInt(1)));
						class_iv++;
					}
					else if(hba1c>=86.00){
						class_v++;
						patient_class_v = patient_class_v.concat("-").concat(Integer.toString(rs.getInt(1)));
					}
					patientList.add(rs.getInt(1));
				}
			}


			LinkedHashMap<String, Integer> hba1cMap = new LinkedHashMap<String, Integer>();
			hba1cMap.put("<53 mmol/mol", class_i);
			hba1cMap.put("[53-64) mmol/mol", class_ii);
			hba1cMap.put("[64-75) mmol/mol", class_iii);
			hba1cMap.put("[75-86) mmol/mol", class_iv);
			hba1cMap.put(">= 86 mmol/mol", class_v);

			LinkedHashMap<String, String> patientNumsMap = new LinkedHashMap<String, String>();
			patientNumsMap.put("<53 mmol/mol", patient_class_i);
			patientNumsMap.put("[53-64) mmol/mol", patient_class_ii);
			patientNumsMap.put("[64-75) mmol/mol", patient_class_iii);
			patientNumsMap.put("[75-86) mmol/mol", patient_class_iv);
			patientNumsMap.put(">= 86 mmol/mol", patient_class_v);

			Set<String> keys = hba1cMap.keySet();

			for(String key : keys){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				JSONObject row_2 = new JSONObject();

				row_1.put("v", key);
				row_2.put("v", hba1cMap.get(key));
				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);

				JSONObject raw_data= new JSONObject();
				raw_data.put("id", key);
				raw_data.put("patient_nums", patientNumsMap.get(key));

				raw_values.add(raw_data);
			}
			chart_json.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_LOC(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql ="select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year_start, "+
					//					"extract (month from q1.start_date) as h_month_start, "+
					//					"extract (day from q1.start_date) as h_day_start, "+
					//					"extract (year from q1.end_date) as h_year_end, "+
					//					"extract (month from q1.end_date) as h_month_end, "+
					//					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like  ? " +
					"and q1.patient_num in "+
					"(select distinct q2.patient_num from "+observationTable+" q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_CVR(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? " +
					"and q1.patient_num in "+
					"(select distinct q2.patient_num from "+observationTable+" q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}

				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_DRUG(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);


		try {
			prop.load(input);
			String sql = "select patient_num, atc_class, min(start_date) as first_presc, max(start_date) as last_presc "+
					"from ( " +
					"select q1.PATIENT_NUM,  " +
					"q1.start_date, " +
					"d.atc_class as atc_class, innerquery.first_visit as first_visit " +
					"from "+observationTable+" q1 , DRUG_CLASSES d ,  " +
					//modificare min start_date: inutile, ci deve essere una sola visita, invece lo lascio cosi
					//perchè non c'è una sola visita
					"(select patient_num, min(start_date) as first_visit  " +
					"from "+observationTable+" where concept_cd like ? group by patient_num) innerquery " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num = innerquery.patient_num and q1.patient_num in  " +
					"(select distinct q2.patient_num from "+observationTable+" q2  " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"group by q1.PATIENT_NUM,  " +
					"q1.start_date, atc_class, first_visit " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc) where start_date < first_visit " +
					"group by patient_num, atc_class";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("first_visit"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("first_presc")));
				observation.put("end_date", df.format(rs.getDate("last_presc")));
				observation.put("obs_label", rs.getString("atc_class"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_COMPLICATION(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);


		try {
			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num in "+
					"(select distinct q2.patient_num from "+observationTable+" q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("obs_blob"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "COMPLICATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_HOSPITALIZATION(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.tval_char as tvalchar " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"(select distinct q2.patient_num from "+observationTable+" q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("contact_details_course"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));
				observation.put("obs_label", rs.getString("tvalchar"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "HOSPITALIZATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_LOC(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}

		try {
			prop.load(input);

			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");
				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_CVR(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}
		try {
			prop.load(input);
			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}
				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);		
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_DRUG(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}
		try {
			prop.load(input);	
			String sql = "select patient_num, atc_class, min(start_date) as first_presc, max(start_date) as last_presc "+
					"from ( " +
					"select q1.PATIENT_NUM,  " +
					"q1.start_date, " +
					"d.atc_class as atc_class, innerquery.first_visit as first_visit " +
					"from "+observationTable+" q1 , DRUG_CLASSES d ,  " +
					//modificare min start_date: inutile, ci deve essere una sola visita (lascio cosi perchè ci sono visite duplicate)
					"(select patient_num, min(start_date) as first_visit  " +
					"from "+observationTable+" where concept_cd like ? group by patient_num) innerquery " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num = innerquery.patient_num and q1.patient_num in  " +
					"("+patientNums.replaceAll("-", ",")+" )"+
					"group by q1.PATIENT_NUM,  " +
					"q1.start_date, atc_class, first_visit " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc) where start_date < first_visit " +
					"group by patient_num, atc_class";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("first_visit"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("first_presc")));
				observation.put("end_date", df.format(rs.getDate("last_presc")));
				observation.put("obs_label", rs.getString("atc_class"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);		
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_COMPLICATION(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}

		try {
			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("obs_blob"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);		
			}
			obj.put("patients", patients);
			obj.put("concept", "COMPLICATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_HOSPITALIZATION(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}

		try {
			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.tval_char as tvalchar " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, prop.getProperty("contact_details_course"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));
				observation.put("obs_label", rs.getString("tvalchar"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);		
			}
			obj.put("patients", patients);
			obj.put("concept", "HOSPITALIZATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_LOC(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}

			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"(select distinct patient_num from (select patient_num, concept_cd, start_date, "
					+ "rank() over (partition by patient_num order by start_date desc) rn "
					+ "from "+observationTable+" where CONCEPT_CD  like 'PAT|CVR:%') s, concept_dimension c "
					+ "where c.concept_cd = s.concept_cd and rn=1 and c.concept_cd like ?)"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			pstmt.setString(2, rcvConcept);
			rs = pstmt.executeQuery();
			int patientNum = -1;
			int encounterNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");
				int currentEncounterNum = rs.getInt(2);
				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_CVR(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}

			String sql ="select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year_start, "+
					//					"extract (month from q1.start_date) as h_month_start, "+
					//					"extract (day from q1.start_date) as h_day_start, "+
					//					"extract (year from q1.end_date) as h_year_end, "+
					//					"extract (month from q1.end_date) as h_month_end, "+
					//					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, q1.concept_cd,"+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"(select distinct patient_num from (select patient_num, concept_cd, start_date, "
					+ "rank() over (partition by patient_num order by start_date desc) rn "
					+ "from "+observationTable+" where CONCEPT_CD  like 'PAT|CVR:%') s, concept_dimension c "
					+ "where c.concept_cd = s.concept_cd and rn=1 and c.concept_cd like ?)"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			pstmt.setString(2, rcvConcept);
			rs = pstmt.executeQuery();	

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}
				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);

			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_DRUG(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}
			String sql = "select patient_num, atc_class, min(start_date) as first_presc, max(start_date) as last_presc "+
					"from ( " +
					"select q1.PATIENT_NUM,  " +
					"q1.start_date, " +
					"d.atc_class as atc_class, innerquery.first_visit as first_visit " +
					"from "+observationTable+" q1 , DRUG_CLASSES d ,  " +
					//modificare min start_date: inutile, ci deve essere una sola visita (lascio cosi x visite doppie)
					"(select patient_num, min(start_date) as first_visit  " +
					"from "+observationTable+" where concept_cd like ? group by patient_num) innerquery " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num = innerquery.patient_num and q1.patient_num in  " +
					"(select distinct patient_num from (select patient_num, concept_cd, start_date, "
					+ "rank() over (partition by patient_num order by start_date desc) rn "
					+ "from "+observationTable+" where CONCEPT_CD  like 'PAT|CVR:%') s, concept_dimension c "
					+ "where c.concept_cd = s.concept_cd and rn=1 and c.concept_cd like ?)"+
					"group by q1.PATIENT_NUM,  " +
					"q1.start_date, atc_class, first_visit " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc) where start_date < first_visit " +
					"group by patient_num, atc_class";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("first_visit"));
			pstmt.setString(2, rcvConcept);
			rs = pstmt.executeQuery();	

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("first_presc")));
				observation.put("end_date", df.format(rs.getDate("last_presc")));
				observation.put("obs_label", rs.getString("atc_class"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_COMPLICATION(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}
			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) "+
					"and q1.patient_num in "+
					"(select distinct patient_num from (select patient_num, concept_cd, start_date, "
					+ "rank() over (partition by patient_num order by start_date desc) rn "
					+ "from "+observationTable+" where CONCEPT_CD  like 'PAT|CVR:%') s, concept_dimension c "
					+ "where c.concept_cd = s.concept_cd and rn=1 and c.concept_cd like ?)"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));
			pstmt.setString(4, rcvConcept);
			rs = pstmt.executeQuery();	

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("obs_blob"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "COMPLICATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_HOSPITALIZATION(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}
			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.tval_char as tvalchar " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? "+
					"and q1.patient_num in "+
					"(select distinct patient_num from (select patient_num, concept_cd, start_date, "
					+ "rank() over (partition by patient_num order by start_date desc) rn "
					+ "from "+observationTable+" where CONCEPT_CD  like 'PAT|CVR:%') s, concept_dimension c "
					+ "where c.concept_cd = s.concept_cd and rn=1 and c.concept_cd like ?)"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("contact_details_course"));
			pstmt.setString(2, rcvConcept);
			rs = pstmt.executeQuery();	

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));
				observation.put("obs_label", rs.getString("tvalchar"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);

			}
			obj.put("patients", patients);
			obj.put("concept", "HOSPITALIZATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_LOC(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);

			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_CVR(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);
			String sql ="select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year_start, "+
					//					"extract (month from q1.start_date) as h_month_start, "+
					//					"extract (day from q1.start_date) as h_day_start, "+
					//					"extract (year from q1.end_date) as h_year_end, "+
					//					"extract (month from q1.end_date) as h_month_end, "+
					//					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, q1.concept_cd,"+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}
				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_DRUG(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);
			String sql = "select patient_num, atc_class, min(start_date) as first_presc, max(start_date) as last_presc "+
					"from ( " +
					"select q1.PATIENT_NUM,  " +
					"q1.start_date, " +
					"d.atc_class as atc_class, innerquery.first_visit as first_visit " +
					"from "+observationTable+" q1 , DRUG_CLASSES d ,  " +
					//modificare min start_date: inutile, ci deve essere una sola visita (lascio cosi per visite doppie)
					"(select patient_num, min(start_date) as first_visit  " +
					"from "+observationTable+" where concept_cd like ? group by patient_num) innerquery " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num = innerquery.patient_num and q1.patient_num in  " +
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"group by q1.PATIENT_NUM,  " +
					"q1.start_date, atc_class, first_visit " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc) where start_date < first_visit " +
					"group by patient_num, atc_class";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("first_visit"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("first_presc")));
				observation.put("end_date", df.format(rs.getDate("last_presc")));
				observation.put("obs_label", rs.getString("atc_class"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_COMPLICATION(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");
				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("obs_blob"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "COMPLICATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_HOSPITALIZATION(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.tval_char as tvalchar " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);		
			pstmt.setString(1, prop.getProperty("contact_details_course"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");
				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));
				observation.put("obs_label", rs.getString("tvalchar"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "HOSPITALIZATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	@SuppressWarnings("unchecked")
	private String getI2B2dataAllPatients_LOC(){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like  ? " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataAllPatients_CVR(){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}

				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataAllPatients_DRUG(){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);


		try {
			prop.load(input);
			String sql = "select patient_num, atc_class, min(start_date) as first_presc, max(start_date) as last_presc "+
					"from ( " +
					"select q1.PATIENT_NUM,  " +
					"q1.start_date, " +
					"d.atc_class as atc_class, innerquery.first_visit as first_visit " +
					"from "+observationTable+" q1 , DRUG_CLASSES d ,  " +
					//modificare min start_date: inutile, ci deve essere una sola visita, invece lo lascio cosi
					//perchè non c'è una sola visita
					"(select patient_num, min(start_date) as first_visit  " +
					"from "+observationTable+" where concept_cd like ? group by patient_num) innerquery " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num = innerquery.patient_num "+
					"group by q1.PATIENT_NUM,  " +
					"q1.start_date, atc_class, first_visit " +
					"order by q1.PATIENT_NUM, q1.START_DATE desc) where start_date < first_visit " +
					"group by patient_num, atc_class";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("first_visit"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("first_presc")));
				observation.put("end_date", df.format(rs.getDate("last_presc")));
				observation.put("obs_label", rs.getString("atc_class"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataAllPatients_COMPLICATION(){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);


		try {
			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("obs_blob"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "COMPLICATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	@SuppressWarnings("unchecked")
	private String getI2B2dataAllPatients_HOSPITALIZATION(){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.tval_char as tvalchar " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("contact_details_course"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));
				observation.put("obs_label", rs.getString("tvalchar"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "HOSPITALIZATION");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}



	@SuppressWarnings("unchecked")
	private String getBMIjson(List<I2B2Observation> obs) throws IOException {
		List<Integer> patientNumList = new ArrayList<Integer>();
		HashMap<String, Integer[]> rangeTable = new HashMap<String, Integer[]>();
		int min_inf_14_range = 0;
		int min_14_15_range = 0;
		int min_15_16_range = 0;
		int min_16_17_range = 0;
		int min_17_18_range = 0;
		int min_18_19_range = 0;
		int min_19_20_range = 0;
		int min_20_21_range = 0;
		int min_21_22_range = 0;
		int min_22_23_range = 0;
		int min_23_24_range = 0;
		int min_24_25_range = 0;
		int min_25_26_range = 0;
		int min_26_27_range = 0;
		int min_27_28_range = 0;
		int min_28_29_range = 0;
		int min_29_30_range = 0;
		int min_30_31_range = 0;
		int min_31_32_range = 0;
		int min_32_33_range = 0;
		int min_33_34_range = 0;
		int min_34_35_range = 0;
		int min_35_36_range = 0;
		int min_36_37_range = 0;
		int min_37_38_range = 0;
		int min_38_39_range = 0;
		int min_39_40_range = 0;
		int min_40_41_range = 0;
		int min_41_42_range = 0;
		int min_42_43_range = 0;
		int min_43_44_range = 0;
		int min_44_45_range = 0;
		int min_45_46_range = 0;
		int min_46_47_range = 0;
		int min_47_48_range = 0;
		int min_48_49_range = 0;
		int min_49_50_range = 0;
		int min_50_51_range = 0;
		int min_51_52_range = 0;
		int min_52_53_range = 0;
		int min_53_54_range = 0;
		int min_54_55_range = 0;
		int min_sup_55_range = 0;

		int max_inf_14_range = 0;
		int max_14_15_range = 0;
		int max_15_16_range = 0;
		int max_16_17_range = 0;
		int max_17_18_range = 0;
		int max_18_19_range = 0;
		int max_19_20_range = 0;
		int max_20_21_range = 0;
		int max_21_22_range = 0;
		int max_22_23_range = 0;
		int max_23_24_range = 0;
		int max_24_25_range = 0;
		int max_25_26_range = 0;
		int max_26_27_range = 0;
		int max_27_28_range = 0;
		int max_28_29_range = 0;
		int max_29_30_range = 0;
		int max_30_31_range = 0;
		int max_31_32_range = 0;
		int max_32_33_range = 0;
		int max_33_34_range = 0;
		int max_34_35_range = 0;
		int max_35_36_range = 0;
		int max_36_37_range = 0;
		int max_37_38_range = 0;
		int max_38_39_range = 0;
		int max_39_40_range = 0;
		int max_40_41_range = 0;
		int max_41_42_range = 0;
		int max_42_43_range = 0;
		int max_43_44_range = 0;
		int max_44_45_range = 0;
		int max_45_46_range = 0;
		int max_46_47_range = 0;
		int max_47_48_range = 0;
		int max_48_49_range = 0;
		int max_49_50_range = 0;
		int max_50_51_range = 0;
		int max_51_52_range = 0;
		int max_52_53_range = 0;
		int max_53_54_range = 0;
		int max_54_55_range = 0;
		int max_sup_55_range = 0;

		Date minDate = null;
		Date maxDate = null;
		double minValue = -1;
		double maxValue = -1;
		int prevPatientNum = -1;

		JSONObject obj = new JSONObject();

		JSONArray cols = new JSONArray();

		JSONObject col_1 = new JSONObject();
		col_1.put("id", 1);
		col_1.put("label", "BMI_Range");
		col_1.put("type", "string");

		JSONObject col_2 = new JSONObject();
		col_2.put("id", 2);
		col_2.put("label", "Baseline");
		col_2.put("type", "number");

		JSONObject col_3 = new JSONObject();
		col_3.put("id", 3);
		col_3.put("label", "LastVisit");
		col_3.put("type", "number");

		cols.add(col_1);
		cols.add(col_2);
		cols.add(col_3);

		obj.put("cols", cols);

		JSONArray rows = new JSONArray();

		for(I2B2Observation ob : obs){
			if(!patientNumList.contains(ob.getPatientNum()) || (obs.indexOf(ob)==obs.size()-1)){
				patientNumList.add(ob.getPatientNum());

				if(prevPatientNum>0){
					//MIN RANGES
					if(minValue<=14){
						min_inf_14_range++;
					}
					else if(14<minValue && minValue <= 15){
						min_14_15_range++;
					}
					else if(15<minValue && minValue <= 16){
						min_15_16_range++;
					}
					else if(16<minValue && minValue <= 17){
						min_16_17_range++;
					}
					else if(17<minValue && minValue <= 18){
						min_17_18_range++;
					}
					else if(18<minValue && minValue <= 19){
						min_18_19_range++;
					}
					else if(19<minValue && minValue <= 20){
						min_19_20_range++;
					}
					else if(20<minValue && minValue <= 21){
						min_20_21_range++;
					}
					else if(21<minValue && minValue <= 22){
						min_21_22_range++;
					}
					else if(22<minValue && minValue <= 23){
						min_22_23_range++;
					}
					else if(23<minValue && minValue <= 24){
						min_23_24_range++;
					}
					else if(24<minValue && minValue <= 25){
						min_24_25_range++;
					}
					else if(25<minValue && minValue <= 26){
						min_25_26_range++;
					}
					else if(26<minValue && minValue <= 27){
						min_26_27_range++;
					}
					else if(27<minValue && minValue <= 28){
						min_27_28_range++;
					}
					else if(28<minValue && minValue <= 29){
						min_28_29_range++;
					}
					else if(29<minValue && minValue <= 30){
						min_29_30_range++;
					}
					else if(30<minValue && minValue <= 31){
						min_30_31_range++;
					}
					else if(31<minValue && minValue <= 32){
						min_31_32_range++;
					}
					else if(32<minValue && minValue <= 33){
						min_32_33_range++;
					}
					else if(33<minValue && minValue <= 34){
						min_33_34_range++;
					}
					else if(34<minValue && minValue <= 35){
						min_34_35_range++;
					}
					else if(35<minValue && minValue <= 36){
						min_35_36_range++;
					}
					else if(36<minValue && minValue <= 37){
						min_36_37_range++;
					}
					else if(37<minValue && minValue <= 38){
						min_37_38_range++;
					}
					else if(38<minValue && minValue <= 39){
						min_38_39_range++;
					}
					else if(39<minValue && minValue <= 40){
						min_39_40_range++;
					}
					else if(40<minValue && minValue <= 41){
						min_40_41_range++;
					}
					else if(41<minValue && minValue <= 42){
						min_41_42_range++;
					}
					else if(42<minValue && minValue <= 43){
						min_42_43_range++;
					}
					else if(43<minValue && minValue <= 44){
						min_43_44_range++;
					}
					else if(44<minValue && minValue <= 45){
						min_44_45_range++;
					}
					else if(45<minValue && minValue <= 46){
						min_45_46_range++;
					}
					else if(46<minValue && minValue <= 47){
						min_46_47_range++;
					}
					else if(47<minValue && minValue <= 48){
						min_47_48_range++;
					}
					else if(48<minValue && minValue <= 49){
						min_48_49_range++;
					}
					else if(49<minValue && minValue <= 50){
						min_49_50_range++;
					}
					else if(50<minValue && minValue <= 51){
						min_50_51_range++;
					}
					else if(51<minValue && minValue <= 52){
						min_51_52_range++;
					}
					else if(52<minValue && minValue <= 53){
						min_52_53_range++;
					}
					else if(53<minValue && minValue <= 54){
						min_53_54_range++;
					}
					else if(54<minValue && minValue <= 55){
						min_54_55_range++;
					}
					else if(55<minValue ){
						min_sup_55_range++;
					}

					//MAX RANGES
					if(maxValue<=14){
						max_inf_14_range++;
					}
					else if(14<maxValue && maxValue <= 15){
						max_14_15_range++;
					}
					else if(15<maxValue && maxValue <= 16){
						max_15_16_range++;
					}
					else if(16<maxValue && maxValue <= 17){
						max_16_17_range++;
					}
					else if(17<maxValue && maxValue <= 18){
						max_17_18_range++;
					}
					else if(18<maxValue && maxValue <= 19){
						max_18_19_range++;
					}
					else if(19<maxValue && maxValue <= 20){
						max_19_20_range++;
					}
					else if(20<maxValue && maxValue <= 21){
						max_20_21_range++;
					}
					else if(21<maxValue && maxValue <= 22){
						max_21_22_range++;
					}
					else if(22<maxValue && maxValue <= 23){
						max_22_23_range++;
					}
					else if(23<maxValue && maxValue <= 24){
						max_23_24_range++;
					}
					else if(24<maxValue && maxValue <= 25){
						max_24_25_range++;
					}
					else if(25<maxValue && maxValue <= 26){
						max_25_26_range++;
					}
					else if(26<maxValue && maxValue <= 27){
						max_26_27_range++;
					}
					else if(27<maxValue && maxValue <= 28){
						max_27_28_range++;
					}
					else if(28<maxValue && maxValue <= 29){
						max_28_29_range++;
					}
					else if(29<maxValue && maxValue <= 30){
						max_29_30_range++;
					}
					else if(30<maxValue && maxValue <= 31){
						max_30_31_range++;
					}
					else if(31<maxValue && maxValue <= 32){
						max_31_32_range++;
					}
					else if(32<maxValue && maxValue <= 33){
						max_32_33_range++;
					}
					else if(33<maxValue && maxValue <= 34){
						max_33_34_range++;
					}
					else if(34<maxValue && maxValue <= 35){
						max_34_35_range++;
					}
					else if(35<maxValue && maxValue <= 36){
						max_35_36_range++;
					}
					else if(36<maxValue && maxValue <= 37){
						max_36_37_range++;
					}
					else if(37<maxValue && maxValue <= 38){
						max_37_38_range++;
					}
					else if(38<maxValue && maxValue <= 39){
						max_38_39_range++;
					}
					else if(39<maxValue && maxValue <= 40){
						max_39_40_range++;
					}
					else if(40<maxValue && maxValue <= 41){
						max_40_41_range++;
					}
					else if(41<maxValue && maxValue <= 42){
						max_41_42_range++;
					}
					else if(42<maxValue && maxValue <= 43){
						max_42_43_range++;
					}
					else if(43<maxValue && maxValue <= 44){
						max_43_44_range++;
					}
					else if(44<maxValue && maxValue <= 45){
						max_44_45_range++;
					}
					else if(45<maxValue && maxValue <= 46){
						max_45_46_range++;
					}
					else if(46<maxValue && maxValue <= 47){
						max_46_47_range++;
					}
					else if(47<maxValue && maxValue <= 48){
						max_47_48_range++;
					}
					else if(48<maxValue && maxValue <= 49){
						max_48_49_range++;
					}
					else if(49<maxValue && maxValue <= 50){
						max_49_50_range++;
					}
					else if(50<maxValue && maxValue <= 51){
						max_50_51_range++;
					}
					else if(51<maxValue && maxValue <= 52){
						max_51_52_range++;
					}
					else if(52<maxValue && maxValue <= 53){
						max_52_53_range++;
					}
					else if(53<maxValue && maxValue <= 54){
						max_53_54_range++;
					}
					else if(54<maxValue && maxValue <= 55){
						max_54_55_range++;
					}
					else if(55<maxValue ){
						max_sup_55_range++;
					}
				}

				minDate = ob.getStartDate();
				maxDate = ob.getStartDate();
				minValue = ob.getnValNum();
				maxValue = ob.getnValNum();

			}

			if(ob.getStartDate().compareTo(minDate)<0){
				minDate = ob.getStartDate();
				minValue = ob.getnValNum();
			}
			else if(ob.getStartDate().compareTo(maxDate)>0){
				maxDate = ob.getStartDate();
				maxValue = ob.getnValNum();
			}

			prevPatientNum = ob.getPatientNum();
		}

		rangeTable.put("inf-14", new Integer[]{min_inf_14_range, max_inf_14_range});
		rangeTable.put("14-15", new Integer[]{min_14_15_range, max_14_15_range});
		rangeTable.put("15-16", new Integer[]{min_15_16_range, max_15_16_range});
		rangeTable.put("16-17", new Integer[]{min_16_17_range, max_16_17_range});
		rangeTable.put("17-18", new Integer[]{min_17_18_range, max_17_18_range});
		rangeTable.put("18-19", new Integer[]{min_18_19_range, max_18_19_range});
		rangeTable.put("19-20", new Integer[]{min_19_20_range, max_19_20_range});
		rangeTable.put("20-21", new Integer[]{min_20_21_range, max_20_21_range});
		rangeTable.put("21-22", new Integer[]{min_21_22_range, max_21_22_range});
		rangeTable.put("22-23", new Integer[]{min_22_23_range, max_22_23_range});
		rangeTable.put("23-24", new Integer[]{min_23_24_range, max_23_24_range});
		rangeTable.put("24-25", new Integer[]{min_24_25_range, max_24_25_range});
		rangeTable.put("25-26", new Integer[]{min_25_26_range, max_25_26_range});
		rangeTable.put("26-27", new Integer[]{min_26_27_range, max_26_27_range});
		rangeTable.put("27-28", new Integer[]{min_27_28_range, max_27_28_range});
		rangeTable.put("28-29", new Integer[]{min_28_29_range, max_28_29_range});
		rangeTable.put("29-30", new Integer[]{min_29_30_range, max_29_30_range});
		rangeTable.put("30-31", new Integer[]{min_30_31_range, max_30_31_range});
		rangeTable.put("31-32", new Integer[]{min_31_32_range, max_31_32_range});
		rangeTable.put("32-33", new Integer[]{min_32_33_range, max_32_33_range});
		rangeTable.put("33-34", new Integer[]{min_33_34_range, max_33_34_range});
		rangeTable.put("34-35", new Integer[]{min_34_35_range, max_34_35_range});
		rangeTable.put("35-36", new Integer[]{min_35_36_range, max_35_36_range});
		rangeTable.put("36-37", new Integer[]{min_36_37_range, max_36_37_range});
		rangeTable.put("37-38", new Integer[]{min_37_38_range, max_37_38_range});
		rangeTable.put("38-39", new Integer[]{min_38_39_range, max_38_39_range});
		rangeTable.put("39-40", new Integer[]{min_39_40_range, max_39_40_range});
		rangeTable.put("40-41", new Integer[]{min_40_41_range, max_40_41_range});
		rangeTable.put("41-42", new Integer[]{min_41_42_range, max_41_42_range});
		rangeTable.put("42-43", new Integer[]{min_42_43_range, max_42_43_range});
		rangeTable.put("43-44", new Integer[]{min_43_44_range, max_43_44_range});
		rangeTable.put("44-45", new Integer[]{min_44_45_range, max_44_45_range});
		rangeTable.put("45-46", new Integer[]{min_45_46_range, max_45_46_range});
		rangeTable.put("46-47", new Integer[]{min_46_47_range, max_46_47_range});
		rangeTable.put("47-48", new Integer[]{min_47_48_range, max_47_48_range});
		rangeTable.put("48-49", new Integer[]{min_48_49_range, max_48_49_range});
		rangeTable.put("49-50", new Integer[]{min_49_50_range, max_49_50_range});
		rangeTable.put("50-51", new Integer[]{min_50_51_range, max_50_51_range});
		rangeTable.put("51-52", new Integer[]{min_51_52_range, max_51_52_range});
		rangeTable.put("52-53", new Integer[]{min_52_53_range, max_52_53_range});
		rangeTable.put("53-54", new Integer[]{min_53_54_range, max_53_54_range});
		rangeTable.put("54-55", new Integer[]{min_54_55_range, max_54_55_range});
		rangeTable.put("sup-55", new Integer[]{min_sup_55_range, max_sup_55_range});

		for(int i=13; i<=55; i++){
			JSONArray row_arr = new JSONArray();
			JSONObject row_obj = new JSONObject();
			JSONObject row_1 = new JSONObject();
			JSONObject row_2 = new JSONObject();
			JSONObject row_3 = new JSONObject();

			String key = "";

			if(i==13){
				key = "inf-14";	
			}
			else if(i==55){
				key = "sup-55";			
			}
			else{
				key = i+"-"+(i+1);
			}
			//System.out.println("key: " + key);
			row_1.put("v", key);
			row_2.put("v", rangeTable.get(key)[0]);
			row_3.put("v", rangeTable.get(key)[1]);

			row_arr.add(row_1);
			row_arr.add(row_2);
			row_arr.add(row_3);
			row_obj.put("c",row_arr);
			rows.add(row_obj);
		}

		obj.put("rows", rows);

		StringWriter out = new StringWriter();
		obj.writeJSONString(out);

		return out.toString();
	}

	private String getI2B2DataForDrillDown(String patientNums, String durationNums, String numClasses, String maxDuration, String minDuration){
		String comorbChartJSON = getI2B2Data_Step3(patientNums, durationNums, numClasses, maxDuration, minDuration);
		return comorbChartJSON;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2ComorbByPatientNum(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.NAME_CHAR, count(distinct t1.PATIENT_NUM) as count " +
					"from "+observationTable+"  t1, CONCEPT_DIMENSION  t2 " +
					"where t1.CONCEPT_CD  like (?) " +
					"and t1.PATIENT_NUM in ("+ patientNums.substring(0,patientNums.lastIndexOf(";")).replaceAll(";", ",")+")"+
					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
					"GROUP by t2.NAME_CHAR";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", rs.getString(1));
				//row_1.put("f", null);

				JSONObject row_2 = new JSONObject();
				row_2.put("v", rs.getInt(2));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}
			obj.put("rows", rows);
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2b2TimeToComorbByPatientNum(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select * from (select o.PATIENT_NUM, c.NAME_CHAR, min(o.START_DATE), 2 as ord " +
					"from "+observationTable+" o, CONCEPT_DIMENSION c " +
					"where o.PATIENT_NUM in ("+patientNums.substring(0,patientNums.lastIndexOf(";")).replaceAll(";", ",")+") " +
					"and o.CONCEPT_CD = c.CONCEPT_CD " +
					"and c.CONCEPT_CD like ? " +
					"group by o.PATIENT_NUM, c.NAME_CHAR " +
					"union " +
					"select o.PATIENT_NUM, c.NAME_CHAR, o.START_DATE, 1 as ord " +
					"from "+observationTable+" o, CONCEPT_DIMENSION c " +
					"where o.PATIENT_NUM in ("+patientNums.substring(0,patientNums.lastIndexOf(";")).replaceAll(";", ",")+") " +
					"and o.CONCEPT_CD = c.CONCEPT_CD " +
					"and c.CONCEPT_CD like ?) q1 " +
					"order by q1.PATIENT_NUM, Q1.ord";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));
			pstmt.setString(2, prop.getProperty("year_of_diagnosis"));

			rs = pstmt.executeQuery();

			//			JSONObject obj = new JSONObject();
			//			
			//			JSONArray cols = new JSONArray();
			//			
			//			JSONObject col_1 = new JSONObject();
			//			col_1.put("id", 1);
			//			col_1.put("label", "Comorbidity");
			//			col_1.put("type", "string");
			//			
			//			JSONObject col_2 = new JSONObject();
			//			col_2.put("id", 2);
			//			col_2.put("label", "Count");
			//			col_2.put("type", "number");
			//			
			//			cols.add(col_1);
			//			cols.add(col_2);
			//			
			//			obj.put("cols", cols);
			//			
			//			JSONArray rows = new JSONArray();
			//			
			//			while(rs.next()){
			//				JSONArray row_arr = new JSONArray();
			//				JSONObject row_obj = new JSONObject();
			//				
			//				JSONObject row_1 = new JSONObject();
			//				row_1.put("v", rs.getString(1));
			//				//row_1.put("f", null);
			//				
			//				JSONObject row_2 = new JSONObject();
			//				row_2.put("v", rs.getInt(2));
			//				
			//				row_arr.add(row_1);
			//				row_arr.add(row_2);
			//				
			//				row_obj.put("c",row_arr);
			//				rows.add(row_obj);
			//			}
			//			
			//			obj.put("rows", rows);
			//			
			//			StringWriter out = new StringWriter();
			//		    obj.writeJSONString(out);
			//		    jsonText = out.toString();

			//	    System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	//	private String callProcessWS(String data){
	//		/*
	//		 * Cache system with MongoDB
	//		 */
	//		MongoDbUtil mdb = new MongoDbUtil();
	//		CacheMongoObject mongoObj = mdb.insertObj(data);
	//		int mongoObjStatus = mongoObj.getStatus();
	//		String jsonResult="";
	//
	//		switch (mongoObjStatus) {
	//		case 0:
	//			jsonResult = callMatlabService(mongoObj.getMongoObj().get("patients").toString());
	//			mdb.updateCacheObj(jsonResult);
	//			System.out.println("MATLAB SERVICE CALLED");
	//			jsonResult = JSON.parse(jsonResult).toString();
	//			break;
	//		case 1:
	//			jsonResult = "JSON RESULT MISSING";
	//			break;
	//		case 2:
	//			jsonResult = mongoObj.getMongoObj().get("results").toString();
	//			System.out.println("RESULTS FROM CACHE");
	//			break;
	//		}
	//		//		System.out.println("jsonResult: " + jsonResult);
	//		return jsonResult;
	//
	//	}

	private String callProcessWS(String data_category,String data) throws ParseException, IOException{
		Properties prop = new Properties();
		InputStream input = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		prop.load(input);

		/*
		 * Cache system with MongoDB
		 */
		MongoDbUtil mdb = new MongoDbUtil(prop.getProperty("mongodb_server"), prop.getProperty("mongodb_server_port"),
				prop.getProperty("mongodb_db1"),prop.getProperty("mongodb_collection"));

		CacheMongoObject mongoObj = mdb.insertObj(data);
		int mongoObjStatus = mongoObj.getStatus();
		String jsonResult="";

		switch (mongoObjStatus) {
		case 0:
			System.out.println("case 0 - MATLAB SERVICE CALLED");
			jsonResult = callMatlabService(mongoObj.getMongoObj().get("_id").toString());
			//jsonResult = callMatlabService(mongoObj.getMongoObj().get("patients").toString());
			mdb.updateCacheObj(jsonResult);
			System.out.println("MongoDB obj updated: " + mongoObj.getMongoObj().get("_id").toString());
			jsonResult = JSON.parse(jsonResult).toString();
			break;
		case 1:
			System.out.println("case 1 - JSON RESULT MISSING");
			jsonResult = callMatlabService(mongoObj.getMongoObj().get("_id").toString());
			//jsonResult = callMatlabService(mongoObj.getMongoObj().get("patients").toString());
			//System.out.println(jsonResult);
			mdb.updateCacheObj(jsonResult);
			System.out.println("MongoDB obj updated: " + mongoObj.getMongoObj().get("_id").toString());
			jsonResult = JSON.parse(jsonResult).toString();
			break;
		case 2:
			jsonResult = mongoObj.getMongoObj().get("results").toString();
			System.out.println("case 2- RESULTS FROM CACHE");
			break;
		}
		//System.out.println("process json results: " + jsonResult);
		JSONParser pars = new JSONParser();
		JSONObject json = (JSONObject)pars.parse(jsonResult);
		json.put("data_category", data_category);
		StringWriter out = new StringWriter();
		json.writeJSONString(out);
		return out.toString();
	}

	private String callMatlabService (String jsonIn){
		StringBuffer result = new StringBuffer();
		Properties prop = new Properties();
		InputStream input = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);

			String url = prop.getProperty("process_url");

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);

			// add header
			//post.setHeader("User-Agent", USER_AGENT);
			post.addHeader("content-type", "application/x-www-form-urlencoded");


			//params: cont=0&ths=0&length=0&path=test

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("cont", prop.getProperty("cont_default_value")));
			urlParameters.add(new BasicNameValuePair("ths", prop.getProperty("ths_default_value")));
			urlParameters.add(new BasicNameValuePair("length", prop.getProperty("length_default_value")));
			urlParameters.add(new BasicNameValuePair("path", jsonIn.replaceAll(" ", "")));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			//		System.out.println("Response Code : " 
			//	                + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line ="";

			while ((line = rd.readLine()) != null) {
				result.append(line);
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}

		String encoded_result = "";;
		try {
			encoded_result = URLDecoder.decode(result.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encoded_result;

	}

	private String getI2B2DataForHba1c(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as hba1c_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("hba1c"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Hba1c");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				int year = rs.getInt("h_year");
				int month = rs.getInt("h_month")-1;
				int day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				BigDecimal myTipe = rs.getBigDecimal("hba1c_value");
				row_2.put("v", myTipe);
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForWeightRaw(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as weight_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("weight"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Weight");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				int year = rs.getInt("h_year");
				int month = rs.getInt("h_month")-1;
				int day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				BigDecimal myTipe = rs.getBigDecimal("weight_value");
				row_2.put("v", myTipe);
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForMvrRetinopaty(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Evaluation date");
			col_1.put("type", "date");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Risk Value");
			col_2.put("type", "number");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "C");
			col_3.put("label", "C");
			col_3.put("type", "string");
			col_3.put("role", "style");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();

			Date retinopatyDate = new Date();

			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			//Ricerco le tre comorbidità associate ai calcolatori del rischio (Retinopatia, Neuropatia, Nefropatia)
			String sql4 = "select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day "+
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD=?  and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql4);
			pstmt.setString(1, prop.getProperty("com_mic_ret"));
			rs = pstmt.executeQuery();
			while(rs.next()){	
				retinopatyDate = rs.getDate("start_date");	
			}

			pstmt.close();
			rs.close();
			conn.close();

			String sql = "select q1.PATIENT_NUM, q1.start_date, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as mvr_r_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("micro_vascular_risk_retinopaty"));

			rs = pstmt.executeQuery();

			while(rs.next()){
				if(retinopatyDate!=null){
					if(retinopatyDate.after(rs.getDate("start_date"))){
						JSONObject row_obj = new JSONObject();
						JSONArray row_arr = new JSONArray();
						JSONObject row_1 = new JSONObject();
						int year = rs.getInt("h_year");
						int month = rs.getInt("h_month")-1;
						int day = rs.getInt("h_day");
						row_1.put("v","Date("+year+","+month+","+day+")");

						JSONObject row_2 = new JSONObject();
						BigDecimal myTipe = rs.getBigDecimal("mvr_r_value");
						row_2.put("v", myTipe);
						JSONObject row_3 = new JSONObject();
						String myStyle = "point { size: 3; shape-type: circle; fill-color: #";
						if(myTipe.doubleValue()<=0.3){
							myStyle = myStyle.concat("77BC74");
						}else if(myTipe.doubleValue()>0.3 && myTipe.doubleValue()<=0.5){
							myStyle = myStyle.concat("ECE023");
						}else if(myTipe.doubleValue()>0.5 ){
							myStyle = myStyle.concat("C96262");
						}
						myStyle = myStyle.concat("}");
						row_3.put("v", myStyle);
						row_arr.add(row_1);
						row_arr.add(row_2);
						row_arr.add(row_3);
						row_obj.put("c",row_arr);
						rows.add(row_obj);
					}
				}
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForMvrNephropaty(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			Date nephropatyDate = new Date();
			
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			//Ricerco le tre comorbidità associate ai calcolatori del rischio (Retinopatia, Neuropatia, Nefropatia)
			String sql4 = "select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day "+
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD=?  and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql4);
			pstmt.setString(1, prop.getProperty("com_mic_neph"));
			rs = pstmt.executeQuery();
			while(rs.next()){	
				nephropatyDate = rs.getDate("start_date");	
			}
			pstmt.close();
			rs.close();
			conn.close();
			

			String sql = "select q1.PATIENT_NUM, q1.start_date, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as mvr_ne_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("micro_vascular_risk_nephropaty"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Evaluation date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Risk Value");
			col_2.put("type", "number");
			
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "C");
			col_3.put("label", "C");
			col_3.put("type", "string");
			col_3.put("role", "style");

			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				if(nephropatyDate!=null){
					if(nephropatyDate.after(rs.getDate("start_date"))){
						JSONObject row_obj = new JSONObject();
						JSONArray row_arr = new JSONArray();
						JSONObject row_1 = new JSONObject();
						int year = rs.getInt("h_year");
						int month = rs.getInt("h_month")-1;
						int day = rs.getInt("h_day");
						row_1.put("v","Date("+year+","+month+","+day+")");

						JSONObject row_2 = new JSONObject();
						BigDecimal myTipe = rs.getBigDecimal("mvr_ne_value");
						row_2.put("v", myTipe);
						JSONObject row_3 = new JSONObject();
						String myStyle = "point { size: 3; shape-type: circle; fill-color: #";
						if(myTipe.doubleValue()<=0.3){
							myStyle = myStyle.concat("77BC74");
						}else if(myTipe.doubleValue()>0.3 && myTipe.doubleValue()<=0.5){
							myStyle = myStyle.concat("ECE023");
						}else if(myTipe.doubleValue()>0.5 ){
							myStyle = myStyle.concat("C96262");
						}
						myStyle = myStyle.concat("}");
						row_3.put("v", myStyle);
						row_arr.add(row_1);
						row_arr.add(row_2);
						row_arr.add(row_3);
						row_obj.put("c",row_arr);
						rows.add(row_obj);
					}
				}
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForMvrNeuropaty(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Date neuropatyDate = new Date();
			
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			//Ricerco le tre comorbidità associate ai calcolatori del rischio (Retinopatia, Neuropatia, Nefropatia)
			String sql4 = "select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day "+
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD=?  and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql4);
			pstmt.setString(1, prop.getProperty("com_mic_neu"));
			rs = pstmt.executeQuery();
			while(rs.next()){	
				neuropatyDate = rs.getDate("start_date");	
			}
			pstmt.close();
			rs.close();
			conn.close();
			

			String sql = "select q1.PATIENT_NUM, q1.start_date, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as mvr_nu_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD in (?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("micro_vascular_risk_neuropaty"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Evaluation date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Risk Value");
			col_2.put("type", "number");
			
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "C");
			col_3.put("label", "C");
			col_3.put("type", "string");
			col_3.put("role", "style");

			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				if(neuropatyDate!=null){
					if(neuropatyDate.after(rs.getDate("start_date"))){
						JSONObject row_obj = new JSONObject();
						JSONArray row_arr = new JSONArray();
						JSONObject row_1 = new JSONObject();
						int year = rs.getInt("h_year");
						int month = rs.getInt("h_month")-1;
						int day = rs.getInt("h_day");
						row_1.put("v","Date("+year+","+month+","+day+")");

						JSONObject row_2 = new JSONObject();
						BigDecimal myTipe = rs.getBigDecimal("mvr_nu_value");
						row_2.put("v", myTipe);
						JSONObject row_3 = new JSONObject();
						String myStyle = "point { size: 3; shape-type: circle; fill-color: #";
						if(myTipe.doubleValue()<=0.3){
							myStyle = myStyle.concat("77BC74");
						}else if(myTipe.doubleValue()>0.3 && myTipe.doubleValue()<=0.5){
							myStyle = myStyle.concat("ECE023");
						}else if(myTipe.doubleValue()>0.5 ){
							myStyle = myStyle.concat("C96262");
						}
						myStyle = myStyle.concat("}");
						row_3.put("v", myStyle);
						row_arr.add(row_1);
						row_arr.add(row_2);
						row_arr.add(row_3);
						row_obj.put("c",row_arr);
						rows.add(row_obj);
					}
				}
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}



	private String getI2B2DataForTherapy(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs3 = null;
		PreparedStatement pstmt4 = null;
		ResultSet rs4 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();
			String sqlDateRange = "select q1.PATIENT_NUM,extract (year from min(start_date)) as minsd, " +
					"extract (year from max(start_date)) as maxsd "+
					"from "+observationTable+" q1 , DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num =" +patientId+" "+
					"group by patient_num";

			pstmt2 = conn.prepareStatement(sqlDateRange);
			rs2 = pstmt2.executeQuery();
			int minDate = 1990;
			int maxDate = Calendar.getInstance().get(Calendar.YEAR);
			while(rs2.next()){
				minDate = rs2.getInt("minsd");
				maxDate = rs2.getInt("maxsd");
			}

			String sqlMaxValue = "select max(q1.NVAL_NUM) as ddd_value, d.atc_class as atc_class "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num= " +patientId+" "+
					"group by atc_class";

			pstmt3 = conn.prepareStatement(sqlMaxValue);
			rs3 = pstmt3.executeQuery();
			HashMap<String, Double> maxValueMap = new HashMap<String, Double>();
			while(rs3.next()){
				maxValueMap.put(rs3.getString("atc_class"), rs3.getDouble("ddd_value"));
			}

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as ddd_value, " +
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc "+
					"from "+observationTable+" q1 , DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";


			String sqlMagicSquare = "select q1.PATIENT_NUM, "+
					"q1.observation_blob, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			HashMap<String, HashMap<String, List<I2B2TherapyObservation>>> myOuterMap = new HashMap<String, HashMap<String,List<I2B2TherapyObservation>>>();
			//			HashMap<String, List<I2B2TherapyObservation>> myMap = new HashMap<String, List<I2B2TherapyObservation>>();
			while(rs.next()){
				String atcClass = rs.getString("atc_class");
				if(myOuterMap.get(atcClass)!=null){//classe già presente nella mappa
					String atcDescr = rs.getString("atc_descr");
					HashMap<String, List<I2B2TherapyObservation>> innerMap = myOuterMap.get(atcClass);
					if(innerMap.get(atcDescr)!=null){ //farmaco già presente nella mappa inner
						List<I2B2TherapyObservation> therapyList = innerMap.get(atcDescr);
						I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
						newTherapyObs.setAtcClass(atcClass);
						newTherapyObs.setAtcCode(rs.getString("atc"));
						newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
						newTherapyObs.setnValNum(rs.getDouble("ddd_value"));
						newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
						newTherapyObs.setStartDateDay(rs.getInt("h_day"));
						newTherapyObs.setStartDateMonth(rs.getInt("h_month"));
						newTherapyObs.setStartDateYear(rs.getInt("h_year"));
						therapyList.add(newTherapyObs);
					}else{ //farmaco da aggiungere alla mappa inner
						List<I2B2TherapyObservation> therapyList = new ArrayList<I2B2TherapyObservation>();
						I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
						newTherapyObs.setAtcClass(atcClass);
						newTherapyObs.setAtcCode(rs.getString("atc"));
						newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
						newTherapyObs.setnValNum(rs.getDouble("ddd_value"));
						newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
						newTherapyObs.setStartDateDay(rs.getInt("h_day"));
						newTherapyObs.setStartDateMonth(rs.getInt("h_month"));
						newTherapyObs.setStartDateYear(rs.getInt("h_year"));
						therapyList.add(newTherapyObs);
						innerMap.put(atcDescr, therapyList);
					}	
				}else{ //classe da inserire nella mappa
					String atcDescr = rs.getString("atc_descr");
					HashMap<String, List<I2B2TherapyObservation>> innerMap = new HashMap<String, List<I2B2TherapyObservation>>();
					List<I2B2TherapyObservation> therapyList = new ArrayList<I2B2TherapyObservation>();
					I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
					newTherapyObs.setAtcClass(atcClass);
					newTherapyObs.setAtcCode(rs.getString("atc"));
					newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
					newTherapyObs.setnValNum(rs.getDouble("ddd_value"));
					newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
					newTherapyObs.setStartDateDay(rs.getInt("h_day"));
					newTherapyObs.setStartDateMonth(rs.getInt("h_month"));
					newTherapyObs.setStartDateYear(rs.getInt("h_year"));
					therapyList.add(newTherapyObs);
					innerMap.put(atcDescr, therapyList);
					myOuterMap.put(atcClass, innerMap);
				}
			}//fine del while

			Set<String> atcClassesSet = myOuterMap.keySet();
			JSONObject jsonContainer = new JSONObject();
			JSONArray resultsJsonArray = new JSONArray();
			for(String atcClass : atcClassesSet){
				//mappa dei farmaci relativa a una classe di farmaci
				HashMap<String, List<I2B2TherapyObservation>> innerMap = myOuterMap.get(atcClass);
				//1 oggetto con due attributi
				JSONObject colClass = new JSONObject();
				colClass.put("atc_class", atcClass);
				//metto il valore massimo
				Double maxValue = maxValueMap.get(atcClass);
				Double maxRoundedValue = roundMaxValue(maxValue);
				colClass.put("max_value", maxRoundedValue);
				JSONObject chartData = new JSONObject();
				JSONArray cols = new JSONArray();
				JSONObject col_1 = new JSONObject();
				col_1.put("id", "A");
				col_1.put("label", "A");
				col_1.put("type", "date");
				cols.add(col_1);
				//faccio tante colonne quante sono i farmaci
				Set<String> atcSet = innerMap.keySet();
				for(String atc : atcSet){
					JSONObject col_2 = new JSONObject();
					col_2.put("id", atc);
					col_2.put("label", atc);
					col_2.put("type", "number");
					cols.add(col_2);
				}	
				//				JSONObject col_22 = new JSONObject();
				//				col_22.put("id", "D");
				//				col_22.put("label", "D");
				//				col_22.put("type", "number");
				//				JSONObject col_3 = new JSONObject();
				//				col_3.put("id", "C");
				//				col_3.put("label", "C");
				//				col_3.put("type", "string");
				//				col_3.put("role", "tooltip");			
				//				cols.add(col_22);
				//cols.add(col_3);
				JSONArray rows = new JSONArray();
				for(String atc : atcSet){ //per ogni farmaco
					List<I2B2TherapyObservation> therapyList = innerMap.get(atc);
					for(I2B2TherapyObservation to : therapyList){
						JSONObject row_obj = new JSONObject();
						JSONArray row_arr = new JSONArray();
						JSONObject row_1 = new JSONObject();
						int year = to.getStartDateYear();
						int month = to.getStartDateMonth()-1;
						int day =to.getStartDateDay();
						row_1.put("v","Date("+year+","+month+","+day+")");  //scrivo il primo elemento della riga
						row_arr.add(row_1);
						//devo cercare in che colonna mettere la dose!
						double dose = to.getnValNum();
						for(int i=1; i<cols.size(); i++){ //la prima la salto perchè è la data
							JSONObject col = (JSONObject) cols.get(i);
							JSONObject row_2 = new JSONObject();
							if(col.containsValue(atc)){
								row_2.put("v", dose);
							}else{
								row_2.put("v", null);
							}
							row_arr.add(row_2);
						}
						row_obj.put("c",row_arr);
						rows.add(row_obj);
					}
				}
				chartData.put("cols", cols);
				chartData.put("rows", rows);
				colClass.put("data", chartData);
				resultsJsonArray.add(colClass);
			}

			//MagicBox Section
			pstmt4 = conn.prepareStatement(sqlMagicSquare);
			rs4 = pstmt4.executeQuery();
			HashMap<String,String> atcBlobMap = new HashMap<String,String>();
			//JSONObject myObj = new JSONObject();
			while(rs4.next()){
				String atcClass = rs4.getString("atc_class");		
				if(atcBlobMap.get(atcClass)!=null){//farmaco già presente nella mappa
					//do nothing
				}else{
					//myObj.put("js", rs.getString("observation_blob").replace("\"", ""));
					atcBlobMap.put(atcClass,rs4.getString("observation_blob"));
				}
			}//fine while

			Set<String> keySet = atcBlobMap.keySet();
			//String JsonDIY = "{\"my_data\":[";
			JSONArray magicBoxArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				JSONParser jParser = new JSONParser();
				JSONObject blobObj = (JSONObject) jParser.parse(atcBlobMap.get(s));
				atcObj.put("atcBlob", blobObj);
				magicBoxArray.add(atcObj);
			}
			jsonContainer.put("results", resultsJsonArray);
			jsonContainer.put("endYear", maxDate);
			jsonContainer.put("startYear", minDate);
			jsonContainer.put("result4MagicBox", magicBoxArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(rs3 != null){
					rs3.close();
				}
				if(pstmt3 != null){
					pstmt3.close();
				}
				if(rs4 != null){
					rs4.close();
				}
				if(pstmt4 != null){
					pstmt4.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}
	private String getI2B2DataForTherapyAdherence(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			HashMap<String, List<I2B2TherapyObservation>> myOuterMap = new HashMap<String, List<I2B2TherapyObservation>>();
			while(rs.next()){
				String atcClass = rs.getString("atc_class");
				if(myOuterMap.get(atcClass)!=null){//classe già presente nella mappa
					List<I2B2TherapyObservation> therapyList = myOuterMap.get(atcClass);
					I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
					newTherapyObs.setAtcClass(atcClass);
					newTherapyObs.setAtcCode(rs.getString("atc"));
					newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
					newTherapyObs.settValChar(rs.getString("aderenza"));
					newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
					newTherapyObs.setStartDateDay(rs.getInt("h_day_start"));
					newTherapyObs.setStartDateMonth(rs.getInt("h_month_start"));
					newTherapyObs.setStartDateYear(rs.getInt("h_year_start"));
					newTherapyObs.setEndDateDay(rs.getInt("h_day_end"));
					newTherapyObs.setEndDateMonth(rs.getInt("h_month_end"));
					newTherapyObs.setEndDateYear(rs.getInt("h_year_end"));
					therapyList.add(newTherapyObs);
				}else{ //classe da inserire nella mappa
					List<I2B2TherapyObservation> therapyList = new ArrayList<I2B2TherapyObservation>();
					I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
					newTherapyObs.setAtcClass(atcClass);
					newTherapyObs.setAtcCode(rs.getString("atc"));
					newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
					newTherapyObs.settValChar(rs.getString("aderenza"));
					newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
					newTherapyObs.setStartDateDay(rs.getInt("h_day_start"));
					newTherapyObs.setStartDateMonth(rs.getInt("h_month_start"));
					newTherapyObs.setStartDateYear(rs.getInt("h_year_start"));
					newTherapyObs.setEndDateDay(rs.getInt("h_day_end"));
					newTherapyObs.setEndDateMonth(rs.getInt("h_month_end"));
					newTherapyObs.setEndDateYear(rs.getInt("h_year_end"));
					therapyList.add(newTherapyObs);
					myOuterMap.put(atcClass, therapyList);
				}
			}//fine del while

			Set<String> atcClassesSet = myOuterMap.keySet();
			JSONObject jsonContainer = new JSONObject();
			JSONArray resultsJsonArray = new JSONArray();
			for(String atcClass : atcClassesSet){ //faccio una timeline per ogni classe di farmaci
				//1 oggetto con due attributi
				JSONObject colClass = new JSONObject();
				colClass.put("atc_class", atcClass);
				JSONObject chartData = new JSONObject();
				JSONArray cols = new JSONArray();
				JSONObject col_1 = new JSONObject();
				col_1.put("id", "Therapy");
				col_1.put("label", "Therapy");
				col_1.put("type", "string");

				JSONObject col_2 = new JSONObject();
				col_2.put("id", "TherapyName");
				col_2.put("label", "TherapyName");
				col_2.put("type", "string");

				JSONObject col_3 = new JSONObject();
				col_3.put("id", "Start");
				col_3.put("label", "Start");
				col_3.put("type", "date");

				JSONObject col_4 = new JSONObject();
				col_4.put("id", "End");
				col_4.put("label", "End");
				col_4.put("type", "date");
				cols.add(col_1);
				cols.add(col_2);
				cols.add(col_3);
				cols.add(col_4);
				List<I2B2TherapyObservation> therapyList = myOuterMap.get(atcClass);
				JSONArray rows = new JSONArray();
				for(I2B2TherapyObservation to : therapyList){
					JSONObject row_obj = new JSONObject();
					JSONArray row_arr = new JSONArray();
					JSONObject row_1 = new JSONObject();
					row_1.put("v",to.getAtcDescr());

					JSONObject row_2 = new JSONObject();
					row_2.put("v",to.gettValChar());

					JSONObject row_3 = new JSONObject();
					int monthStart = to.getStartDateMonth()-1;
					row_3.put("v", "Date("+to.getStartDateYear()+","+monthStart+","+to.getStartDateDay()+")");

					JSONObject row_4 = new JSONObject();
					int monthEnd = to.getEndDateMonth()-1;
					row_4.put("v", "Date("+to.getEndDateYear()+","+monthEnd+","+to.getEndDateDay()+")");

					row_arr.add(row_1);
					row_arr.add(row_2);
					row_arr.add(row_3);
					row_arr.add(row_4);
					row_obj.put("c",row_arr);
					rows.add(row_obj);
				}
				chartData.put("cols", cols);
				chartData.put("rows", rows);
				colClass.put("data", chartData);
				resultsJsonArray.add(colClass);
			}
			jsonContainer.put("results", resultsJsonArray);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForTherapyAdherence2(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String> atcDescrSet = new ArrayList<String>();
			List<String>  aderenzaLabelSet = new ArrayList<String>();
			String sql2 = "select distinct q1.tval_char "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId;

			pstmt2 = conn.prepareStatement(sql2);
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				aderenzaLabelSet.add(rs2.getString("tval_char"));
			}

			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Therapy");
			col_1.put("label", "Therapy");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "TherapyName");
			col_2.put("label", "TherapyName");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			//			JSONObject col_5 = new JSONObject();
			//			col_5.put("id", "atc");
			//			col_5.put("label", "atc");
			//			col_5.put("type", "string");
			//			JSONObject col_6 = new JSONObject();
			//			col_6.put("id", "C");
			//			col_6.put("label", "C");
			//			col_6.put("type", "string");
			//			col_6.put("role", "tooltip");	
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			//			cols.add(col_5);
			//			cols.add(col_6);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			while(rs.next()){
				//Lista ordinata di farmaci per data di comparizione
				String atcDescr = rs.getString("atc_descr");
				if(!atcDescrSet.contains(atcDescr)){
					atcDescrSet.add(atcDescr);
				}
				//Mappa per colori			
				if(atcLabelMap.get(atcDescr)!=null){//farmaco già presente nella mappa
					List<String> labelOrder4Atc = atcLabelMap.get(atcDescr);
					if(!labelOrder4Atc.contains(rs.getString("aderenza"))){
						labelOrder4Atc.add(rs.getString("aderenza"));
					}
				}else{
					List<String> labelOrder4Atc = new ArrayList<String>();
					labelOrder4Atc.add(rs.getString("aderenza"));
					atcLabelMap.put(atcDescr, labelOrder4Atc);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v",rs.getString("atc_descr"));

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));
				row_2.put("v",rs.getString("aderenza"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");
				//				JSONObject row_5 = new JSONObject();
				//				row_5.put("v",rs.getString("aderenza"));
				//				
				//				JSONObject row_6 = new JSONObject();
				//				row_6.put("v","pippo");
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				//				row_arr.add(row_5);
				//				row_arr.add(row_6);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("therapyData", obj);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			List<String> labels = getLabelOrder(atcDescrSet, aderenzaLabelSet, atcLabelMap);
			JSONArray labelsArray = new JSONArray();
			for(String s:labels){
				labelsArray.add(s);
			}
			jsonContainer.put("labelsArray", labelsArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	private String getI2B2DataForTherapyAdherence3(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String> atcDescrSet = new ArrayList<String>();
			List<String>  aderenzaLabelSet = new ArrayList<String>();
			String sql2 = "select distinct q1.tval_char "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId;

			pstmt2 = conn.prepareStatement(sql2);
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				aderenzaLabelSet.add(rs2.getString("tval_char"));
			}

			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd ,q1.nval_num "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Therapy");
			col_1.put("label", "Therapy");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "TherapyName");
			col_2.put("label", "TherapyName");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			//			JSONObject col_5 = new JSONObject();
			//			col_5.put("id", "atc");
			//			col_5.put("label", "atc");
			//			col_5.put("type", "string");
			//			JSONObject col_6 = new JSONObject();
			//			col_6.put("id", "C");
			//			col_6.put("label", "C");
			//			col_6.put("type", "string");
			//			col_6.put("role", "tooltip");	
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			//			cols.add(col_5);
			//			cols.add(col_6);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			HashMap<String, List<String>> atcMap = new HashMap<String, List<String>>();	
			while(rs.next()){
				//Lista ordinata di farmaci per data di comparizione
				String atcDescr = rs.getString("atc_descr");
				if(!atcDescrSet.contains(atcDescr)){
					atcDescrSet.add(atcDescr);
				}
				//Mappa per colori			
				Double aderenzaValue = rs.getDouble("nval_num");
				String aderenzaRangeString = roundAdherenceValue2(aderenzaValue);
				if(atcLabelMap.get(atcDescr)!=null){//farmaco già presente nella mappa
					List<String> labelOrder4Atc = atcLabelMap.get(atcDescr);
					//					if(!labelOrder4Atc.contains(rs.getString("aderenza"))){
					//						labelOrder4Atc.add(rs.getString("aderenza"));
					//					}
					if(!labelOrder4Atc.contains(aderenzaRangeString)){
						labelOrder4Atc.add(aderenzaRangeString);
					}
				}else{
					List<String> labelOrder4Atc = new ArrayList<String>();
					//labelOrder4Atc.add(rs.getString("aderenza"));
					labelOrder4Atc.add(aderenzaRangeString);
					atcLabelMap.put(atcDescr, labelOrder4Atc);
				}
				//Mappa dei farmaci
				String atcClass = rs.getString("atc_class");		
				if(atcMap.get(atcClass)!=null){//farmaco già presente nella mappa
					List<String> atcList = atcMap.get(atcClass);
					if(!atcList.contains(atcDescr)){
						atcList.add(atcDescr);
					}
				}else{
					List<String> atcList = new ArrayList<String>();
					atcList.add(atcDescr);
					atcMap.put(atcClass, atcList);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v",rs.getString("atc_descr"));

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));
				//row_2.put("v",rs.getString("aderenza"));
				row_2.put("v",aderenzaRangeString);

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				//				JSONObject row_5 = new JSONObject();
				//				row_5.put("v",rs.getString("aderenza"));
				//				
				//				JSONObject row_6 = new JSONObject();
				//				row_6.put("v","pippo");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				//				row_arr.add(row_5);
				//				row_arr.add(row_6);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("therapyData", obj);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			List<String> labels = getLabelOrder(atcDescrSet, aderenzaLabelSet, atcLabelMap);
			JSONArray labelsArray = new JSONArray();
			for(String s:labels){
				labelsArray.add(s);
			}
			jsonContainer.put("labelsArray", labelsArray);
			//Mappa farmaci per bottoni
			Set<String> keySet = atcMap.keySet();
			JSONArray outerArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				List<String> atcs = atcMap.get(s);
				JSONArray atcArray = new JSONArray();
				for(String atc: atcs){
					atcArray.add(atc);
				}
				atcObj.put("atcList", atcArray);
				outerArray.add(atcObj);
			}
			jsonContainer.put("atcListData", outerArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForTherapyAdherence3Filtered(String patientId, String atcFilter) {
		String[] atcArrayFilter = atcFilter.substring(0, atcFilter.length()-1).split(",");
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String> atcDescrSet = new ArrayList<String>();
			List<String>  aderenzaLabelSet = new ArrayList<String>();
			String sql2 = "select distinct q1.tval_char "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and d.atc_descr not in (";
			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd ,q1.nval_num "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' " +
					"and d.atc_descr not in (";

			for (String atc : atcArrayFilter){
				sql2 = sql2.concat("'"+atc+"',");
				sql = sql.concat("'"+atc+"',");	
			}
			sql = sql.substring(0, sql.length()-1);
			sql2 = sql2.substring(0, sql2.length()-1);
			sql2 = sql2.concat(") and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId);
			sql = sql.concat(") and q1.patient_num= " +patientId+" order by q1.START_DATE");
			pstmt2 = conn.prepareStatement(sql2);
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				aderenzaLabelSet.add(rs2.getString("tval_char"));
			}
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Therapy");
			col_1.put("label", "Therapy");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "TherapyName");
			col_2.put("label", "TherapyName");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			//			JSONObject col_5 = new JSONObject();
			//			col_5.put("id", "atc");
			//			col_5.put("label", "atc");
			//			col_5.put("type", "string");
			//			JSONObject col_6 = new JSONObject();
			//			col_6.put("id", "C");
			//			col_6.put("label", "C");
			//			col_6.put("type", "string");
			//			col_6.put("role", "tooltip");	
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			//			cols.add(col_5);
			//			cols.add(col_6);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			HashMap<String, List<String>> atcMap = new HashMap<String, List<String>>();	
			while(rs.next()){
				//Lista ordinata di farmaci per data di comparizione
				String atcDescr = rs.getString("atc_descr");
				if(!atcDescrSet.contains(atcDescr)){
					atcDescrSet.add(atcDescr);
				}
				//Mappa per colori			
				Double aderenzaValue = rs.getDouble("nval_num");
				String aderenzaRangeString = roundAdherenceValue2(aderenzaValue);
				if(atcLabelMap.get(atcDescr)!=null){//farmaco già presente nella mappa
					List<String> labelOrder4Atc = atcLabelMap.get(atcDescr);
					//					if(!labelOrder4Atc.contains(rs.getString("aderenza"))){
					//						labelOrder4Atc.add(rs.getString("aderenza"));
					//					}
					if(!labelOrder4Atc.contains(aderenzaRangeString)){
						labelOrder4Atc.add(aderenzaRangeString);
					}
				}else{
					List<String> labelOrder4Atc = new ArrayList<String>();
					//labelOrder4Atc.add(rs.getString("aderenza"));
					labelOrder4Atc.add(aderenzaRangeString);
					atcLabelMap.put(atcDescr, labelOrder4Atc);
				}
				//Mappa dei farmaci
				String atcClass = rs.getString("atc_class");		
				if(atcMap.get(atcClass)!=null){//farmaco già presente nella mappa
					List<String> atcList = atcMap.get(atcClass);
					if(!atcList.contains(atcDescr)){
						atcList.add(atcDescr);
					}
				}else{
					List<String> atcList = new ArrayList<String>();
					atcList.add(atcDescr);
					atcMap.put(atcClass, atcList);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v",rs.getString("atc_descr"));

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));
				//row_2.put("v",rs.getString("aderenza"));
				row_2.put("v",aderenzaRangeString);

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");
				//				JSONObject row_5 = new JSONObject();
				//				row_5.put("v",rs.getString("aderenza"));
				//				
				//				JSONObject row_6 = new JSONObject();
				//				row_6.put("v","pippo");
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				//				row_arr.add(row_5);
				//				row_arr.add(row_6);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("therapyData", obj);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			List<String> labels = getLabelOrder(atcDescrSet, aderenzaLabelSet, atcLabelMap);
			JSONArray labelsArray = new JSONArray();
			for(String s:labels){
				labelsArray.add(s);
			}
			jsonContainer.put("labelsArray", labelsArray);
			//Mappa farmaci per bottoni
			Set<String> keySet = atcMap.keySet();
			JSONArray outerArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				List<String> atcs = atcMap.get(s);
				JSONArray atcArray = new JSONArray();
				for(String atc: atcs){
					atcArray.add(atc);
				}
				atcObj.put("atcList", atcArray);
				outerArray.add(atcObj);
			}
			jsonContainer.put("atcListData", outerArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForLOC(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String>  locLabelSet = new ArrayList<String>();
			String sql2 = "select q1.observation_blob as obs_blob "+
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" order by q1.START_DATE";

			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, prop.getProperty("level_of_complexity"));
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				if(!locLabelSet.contains(rs2.getString("obs_blob"))){
					locLabelSet.add(rs2.getString("obs_blob"));
				}
			}
			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "LOC");
			col_1.put("label", "LOC");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "LOC_DESCR");
			col_2.put("label", "LOC_DESCR");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int prevYear =0;
			int prevMonth = 0;
			int prevDay = 0;
			int i=0;
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v","Level of Complexity");

				JSONObject row_2 = new JSONObject();
				//row_2.put("v",rs.getString("loc_value"));
				row_2.put("v",rs.getString("obs_blob"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				//QUANDO MANCA END DATE
				//				if(i==0){
				//					prevYear = Calendar.getInstance().get(Calendar.YEAR);
				//					prevMonth = Calendar.getInstance().get(Calendar.MONTH);
				//					prevDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				//				}
				//				row_4.put("v", "Date("+prevYear+","+prevMonth+","+prevDay+")");
				//				prevYear = rs.getInt("h_year_start");
				//				prevMonth = rs.getInt("h_month_start")-1;
				//				prevDay = rs.getInt("h_day_start");
				//				i++;

				//END DATE DISPONIBILE
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("locData", obj);
			JSONArray labelsArray = new JSONArray();
			for(String s:locLabelSet){
				labelsArray.add(s);
			}
			jsonContainer.put("locLabels", labelsArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	private String getI2B2DataForCVR(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "CVR");
			col_2.put("type", "number");

			JSONObject col_3 = new JSONObject();
			col_3.put("id", "C");
			col_3.put("label", "C");
			col_3.put("type", "string");
			col_3.put("role", "style");

			JSONObject col_4 = new JSONObject();
			col_4.put("id", "D");
			col_4.put("label", "D");
			col_4.put("type", "string");
			col_4.put("role", "tooltip");

			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				int year = rs.getInt("h_year");
				int month = rs.getInt("h_month")-1;
				int day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				double myType = rs.getDouble("cvr_value");
				myType = Math.round(myType);

				String myStyle = "point { size: 5; shape-type: circle; fill-color: #";
				if(myType<5){
					myStyle = myStyle.concat("84d1ad");
					row_2.put("v", 1);
				}else if(myType>=5 && myType<10){
					myStyle = myStyle.concat("01ae76");
					row_2.put("v", 2);
				}else if(myType>=10 && myType<15){
					myStyle = myStyle.concat("fdd687");
					row_2.put("v", 3);
				}else if(myType>=15 && myType<20){
					myStyle = myStyle.concat("ff944c");
					row_2.put("v", 4);
				}else if(myType>=20 && myType<30){
					myStyle = myStyle.concat("f34930");
					row_2.put("v", 5);
				}else if(myType>=30){
					myStyle = myStyle.concat("c479b1");
					row_2.put("v", 6);
				}

				JSONObject row_3 = new JSONObject();
				myStyle = myStyle.concat("}");
				row_3.put("v", myStyle);

				JSONObject row_4 = new JSONObject();
				row_4.put("v", year+"/"+(month+1)+"/"+day + "\nCVR: " + myType);

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	private String getI2B2DataForDiet(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			conn = DBUtil.getI2B2Connection();
			List<String>  dietLabelSet = new ArrayList<String>();
			String sql2 = "select q1.observation_blob as obs_blob "+
					"from "+observationTable+"  q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" order by q1.START_DATE";
			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, prop.getProperty("diet_ta_good"));
			pstmt2.setString(2, prop.getProperty("diet_ta_bad"));
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				if(!dietLabelSet.contains(rs2.getString("obs_blob"))){
					dietLabelSet.add(rs2.getString("obs_blob"));
				}
			}
			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.observation_blob as diet, "+
					"q1.concept_cd as concept_cd "+
					"from "+observationTable+" q1 "+
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("diet_ta_good"));
			pstmt.setString(2, prop.getProperty("diet_ta_bad"));
			rs = pstmt.executeQuery();

			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Diet");
			col_1.put("label", "Diet");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "DietAdherence");
			col_2.put("label", "DietAdherence");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v","Diet");

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));
				row_2.put("v",rs.getString("diet"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");
				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}
			obj.put("rows", rows);
			jsonContainer.put("dietData", obj);
			JSONArray labelsArray = new JSONArray();
			for(String s:dietLabelSet){
				labelsArray.add(s);
			}
			jsonContainer.put("dietLabels", labelsArray);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForAtcList(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			conn = DBUtil.getI2B2Connection();
			String sql = "select q1.PATIENT_NUM, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from "+observationTable+" q1 , DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			while(rs.next()){
				String atcClass = rs.getString("atc_class");		
				if(atcLabelMap.get(atcClass)!=null){//farmaco già presente nella mappa
					List<String> atcList = atcLabelMap.get(atcClass);
					if(!atcList.contains(rs.getString("atc_descr"))){
						atcList.add(rs.getString("atc_descr"));
					}
				}else{
					List<String> atcList = new ArrayList<String>();
					atcList.add(rs.getString("atc_descr"));
					atcLabelMap.put(atcClass, atcList);
				}
			}//fine while
			Set<String> keySet = atcLabelMap.keySet();
			JSONArray outerArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				List<String> atcs = atcLabelMap.get(s);
				JSONArray labelsArray = new JSONArray();
				for(String atc: atcs){
					labelsArray.add(atc);
				}
				atcObj.put("atcList", labelsArray);
				outerArray.add(atcObj);
			}
			jsonContainer.put("myData", outerArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForWeight(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			conn = DBUtil.getI2B2Connection();
			List<String>  weightLabelSet = new ArrayList<String>();
			List<String>  distinctWeightLabelList = new ArrayList<String>();
			List<WeightBean> weightList = new ArrayList<WeightBean>();
			String sql2 = "select q1.observation_blob as obs_blob "+
					"from "+observationTable+"  q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) " +
					" and q1.patient_num= " +patientId+" order by q1.START_DATE";

			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, prop.getProperty("weight_timetotarget"));
			pstmt2.setString(2, prop.getProperty("weight_decrease"));
			pstmt2.setString(3, prop.getProperty("weight_increase"));
			pstmt2.setString(4, prop.getProperty("weight_stationary"));
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				if(!weightLabelSet.contains(rs2.getString("obs_blob"))){
					weightLabelSet.add(rs2.getString("obs_blob"));
					//System.out.println(rs2.getString("obs_blob"));
				}
			}
			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.observation_blob as obs_blob, q1.start_date, q1.end_date "+
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) " +
					"and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("weight_timetotarget"));
			pstmt.setString(2, prop.getProperty("weight_decrease"));
			pstmt.setString(3, prop.getProperty("weight_increase"));
			pstmt.setString(4, prop.getProperty("weight_stationary"));
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "WEIGHT");
			col_1.put("label", "WEIGHT");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "WEIGHT_DESCR");
			col_2.put("label", "WEIGHT_DESCR");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int prevYear =0;
			int prevMonth = 0;
			int prevDay = 0;
			int i=0;
			while(rs.next()){
				WeightBean wb = new WeightBean();
				wb.setEndDate(rs.getDate("end_date"));
				wb.setStartDate(rs.getDate("start_date"));
				wb.setWeightDescr(rs.getString("obs_blob"));
				weightList.add(wb);
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v","WEIGHT");

				JSONObject row_2 = new JSONObject();
				//row_2.put("v",rs.getString("loc_value"));
				row_2.put("v",rs.getString("obs_blob"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				//QUANDO MANCA END DATE
				//				if(i==0){
				//					prevYear = Calendar.getInstance().get(Calendar.YEAR);
				//					prevMonth = Calendar.getInstance().get(Calendar.MONTH);
				//					prevDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				//				}
				//				row_4.put("v", "Date("+prevYear+","+prevMonth+","+prevDay+")");
				//				prevYear = rs.getInt("h_year_start");
				//				prevMonth = rs.getInt("h_month_start")-1;
				//				prevDay = rs.getInt("h_day_start");
				//				i++;

				//END DATE DISPONIBILE
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("weightData", obj);
			JSONArray labelsArray = new JSONArray();
			for(String s:weightLabelSet){
				labelsArray.add(s);
			}


			//Creazione delle label distinte
			//ROWMAP per ANTIARRHYTHMIC
			HashMap<Integer, WeightListBean> rowMapAA = new HashMap<Integer, WeightListBean>();
			int rowCounterAA = 0;
			for(int k=0; k < weightList.size(); k++){
				WeightBean ther = weightList.get(k);
				Date kEndDate = ther.getEndDate();
				if(kEndDate==null) kEndDate= new Date(); //setto a oggi la data di fine se mancante
				if(k==0) {
					//deve andare a capo, creo una nuova riga
					WeightListBean therListBean = new WeightListBean();
					List<WeightBean> therList = new ArrayList<WeightBean>();
					therList.add(ther);
					therListBean.setWeightList(therList);
					therListBean.setMaxEndDate(kEndDate);
					rowMapAA.put(rowCounterAA, therListBean);// per ogni riga mi setto l'ultima endDate
					rowCounterAA++;
				}
				else{
					boolean found = false;
					Date kPrevEndDate = weightList.get(k-1).getEndDate();				
					if(kPrevEndDate==null) kPrevEndDate= new Date(); //setto a oggi la data di fine se mancante		
					Set<Integer> keysetIntegers = rowMapAA.keySet();
					for(Integer h : keysetIntegers){
						WeightListBean therList2check = (WeightListBean) rowMapAA.get(h);
						if(ther.getStartDate().after(therList2check.getMaxEndDate()) || ther.getStartDate().equals(therList2check.getMaxEndDate())){
							List<WeightBean> therListinMap = therList2check.getWeightList();
							therListinMap.add(ther);
							therList2check.setMaxEndDate(kEndDate);
							found = true;
							break;
						}			

					}
					if(!found){
						WeightListBean therListBean = new WeightListBean();
						List<WeightBean> therList = new ArrayList<WeightBean>();
						therList.add(ther);
						therListBean.setWeightList(therList);
						therListBean.setMaxEndDate(kEndDate);
						rowMapAA.put(rowCounterAA, therListBean); // per ogni riga mi setto l'ultima endDate
						rowCounterAA++;
					}
				}
			}

			distinctWeightLabelList = new ArrayList<String>();
			Set<Integer> keysetIntegers = rowMapAA.keySet();
			for(Integer h : keysetIntegers){
				WeightListBean therList2check = (WeightListBean) rowMapAA.get(h);
				for(WeightBean t : therList2check.getWeightList()){
					if(!distinctWeightLabelList.contains(t.getWeightDescr()))distinctWeightLabelList.add(t.getWeightDescr());	
				}
			}

			//			System.out.println("______________");
			//			for(String s: distinctWeightLabelList){
			//				System.out.println(s);
			//			}

			jsonContainer.put("weightLabels", distinctWeightLabelList); //--> vecchio metodo
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	private String getI2B2DataForComplication(String patientId) {
		//setting ComplicationsMap
		complicationsMap.put("COM|MAC:AMI",6);
		complicationsMap.put("COM|MAC:ANG",7);
		complicationsMap.put("COM|MAC:CIHD",8);
		complicationsMap.put("COM|MAC:OCC",9);
		complicationsMap.put("COM|MAC:PAOD",10);
		complicationsMap.put("COM|MAC:STR",11);
		complicationsMap.put("COM|NV:DF",3);
		complicationsMap.put("COM|MIC:NEPH",4);
		complicationsMap.put("COM|MIC:RET",5);
		complicationsMap.put("COM|NV:FLD",1);
		complicationsMap.put("COM|MIC:NEU",2);

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.concept_cd, " +
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Complication");
			col_2.put("type", "number");

			JSONObject col_3 = new JSONObject();
			col_3.put("id", 3);
			col_3.put("label", "Tooltip");
			col_3.put("type", "string");
			col_3.put("role", "tooltip");

			JSONObject col_mac = new JSONObject();
			col_mac.put("id", 4);
			col_mac.put("label", "MAC");
			col_mac.put("type", "number");

			JSONObject col_min = new JSONObject();
			col_min.put("id", 5);
			col_min.put("label", "MIN");
			col_min.put("type", "number");

			JSONObject col_nv = new JSONObject();
			col_nv.put("id", 6);
			col_nv.put("label", "NV");
			col_nv.put("type", "number");

			JSONObject col_annotation = new JSONObject();
			col_annotation.put("id", 7);
			col_annotation.put("label", "Annotation");
			col_annotation.put("type", "string");
			col_annotation.put("role", "annotation");

			//			JSONObject col_style = new JSONObject();
			//			col_style.put("id", "C");
			//			col_style.put("label", "C");
			//			col_style.put("type", "string");
			//			col_style.put("role", "style");

			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_annotation);
			cols.add(col_3);
			cols.add(col_mac);
			cols.add(col_min);
			cols.add(col_nv);

			//			cols.add(col_style);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			int year = 0;
			int month =0;
			int day =0;
			int i=0;
			int complicationNum = 0;
			String tooltip = "";
			while(rs.next()){
				year = rs.getInt("h_year");
				if(i==0){//creo una riga finta 01/01/annoPrimaComplicanza per non avere le obs addossate allo start
					JSONObject row_objPlus = new JSONObject();
					JSONArray row_arrPlus = new JSONArray();
					JSONObject row_1Plus = new JSONObject();
					row_1Plus.put("v","Date("+year+",0,01)");

					JSONObject row_2Plus = new JSONObject();
					row_2Plus.put("v", null);

					JSONObject row_3Plus = new JSONObject();
					row_3Plus.put("v", null);

					JSONObject row_macPlus = new JSONObject();
					row_macPlus.put("v", 12);

					JSONObject row_micPlus = new JSONObject();
					row_micPlus.put("v", 5.5);

					JSONObject row_nvPlus = new JSONObject();
					row_nvPlus.put("v", 2.5);

					JSONObject row_annotationPlus = new JSONObject();
					row_annotationPlus.put("v", null);

					row_arrPlus.add(row_1Plus);
					row_arrPlus.add(row_2Plus);
					row_arrPlus.add(row_annotationPlus);
					row_arrPlus.add(row_3Plus);
					row_arrPlus.add(row_macPlus);
					row_arrPlus.add(row_micPlus);
					row_arrPlus.add(row_nvPlus);
					row_objPlus.put("c",row_arrPlus);
					rows.add(row_objPlus);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				//year = rs.getInt("h_year");
				month = rs.getInt("h_month")-1;
				day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				String conceptCd = rs.getString("concept_cd");
				complicationNum = complicationsMap.get(conceptCd);
				row_2.put("v", complicationNum);

				JSONObject row_3 = new JSONObject();
				String dateString = "Date: ";
				dateString = dateString.concat(String.valueOf(day)).concat("/").concat(String.valueOf(month+1)).concat("/").concat(String.valueOf(year)).concat("\n");
				tooltip = dateString.concat(rs.getString("obs_blob"));
				row_3.put("v", tooltip);

				JSONObject row_mac = new JSONObject();
				row_mac.put("v", 12);

				JSONObject row_mic = new JSONObject();
				row_mic.put("v", 5.5);

				JSONObject row_nv = new JSONObject();
				row_nv.put("v", 2.5);

				JSONObject row_annotation = new JSONObject();
				row_annotation.put("v", rs.getString("obs_blob"));

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_annotation);
				row_arr.add(row_3);
				row_arr.add(row_mac);
				row_arr.add(row_mic);
				row_arr.add(row_nv);				
				row_obj.put("c",row_arr);
				rows.add(row_obj);
				i++;
			}//fine del while
			//			if(i==1){
			//				JSONObject row_obj = new JSONObject();
			//				JSONArray row_arr = new JSONArray();
			//				JSONObject row_1 = new JSONObject();
			//				year = year-1;
			//				row_1.put("v","Date("+year+","+month+","+day+")");
			//
			//				JSONObject row_2 = new JSONObject();
			//				row_2.put("v", null);
			//
			//				JSONObject row_3 = new JSONObject();
			//				row_3.put("v", null);
			//
			//				JSONObject row_mac = new JSONObject();
			//				row_mac.put("v", 12);
			//
			//				JSONObject row_mic = new JSONObject();
			//				row_mic.put("v", 5.5);
			//
			//				JSONObject row_nv = new JSONObject();
			//				row_nv.put("v", 2.5);
			//
			//				JSONObject row_annotation = new JSONObject();
			//				row_annotation.put("v", null);
			//
			//				row_arr.add(row_1);
			//				row_arr.add(row_2);
			//				row_arr.add(row_annotation);
			//				row_arr.add(row_3);
			//				row_arr.add(row_mac);
			//				row_arr.add(row_mic);
			//				row_arr.add(row_nv);
			//				row_obj.put("c",row_arr);
			//				rows.add(row_obj);
			//			}
			JSONObject row_objPlus = new JSONObject();
			JSONArray row_arrPlus = new JSONArray();
			JSONObject row_1Plus = new JSONObject();
			row_1Plus.put("v","Date("+year+",11,31)");

			JSONObject row_2Plus = new JSONObject();
			row_2Plus.put("v", null);

			JSONObject row_3Plus = new JSONObject();
			row_3Plus.put("v", null);

			JSONObject row_macPlus = new JSONObject();
			row_macPlus.put("v", 12);

			JSONObject row_micPlus = new JSONObject();
			row_micPlus.put("v", 5.5);

			JSONObject row_nvPlus = new JSONObject();
			row_nvPlus.put("v", 2.5);

			JSONObject row_annotationPlus = new JSONObject();
			row_annotationPlus.put("v", null);

			row_arrPlus.add(row_1Plus);
			row_arrPlus.add(row_2Plus);
			row_arrPlus.add(row_annotationPlus);
			row_arrPlus.add(row_3Plus);
			row_arrPlus.add(row_macPlus);
			row_arrPlus.add(row_micPlus);
			row_arrPlus.add(row_nvPlus);
			row_objPlus.put("c",row_arrPlus);
			rows.add(row_objPlus);
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	private String getI2B2DataForComplication2(String patientId) {
		//setting ComplicationsMap
		complicationsMap.put("COM|MAC:AMI",6);
		complicationsMap.put("COM|MAC:ANG",7);
		complicationsMap.put("COM|MAC:CIHD",8);
		complicationsMap.put("COM|MAC:OCC",9);
		complicationsMap.put("COM|MAC:PAOD",10);
		complicationsMap.put("COM|MAC:STR",11);
		complicationsMap.put("COM|NV:DF",2);
		complicationsMap.put("COM|MIC:NEPH",4);
		complicationsMap.put("COM|MIC:RET",5);
		complicationsMap.put("COM|NV:FLD",1);
		complicationsMap.put("COM|MIC:NEU",3);

		//		182	1994	6	15	COM|MAC:AMI	Acutemyocardialinfarction
		//		182	1994	6	15	COM|MAC:CIHD	Chronicischemicheartdisease
		//		182	1995	6	15	COM|MAC:AMI	Acutemyocardialinfarction
		//		182	2001	7	31	COM|NV:NEU	Neuropathy
		//		182	2001	8	24	COM|MAC:PAOD	Peripheralvasculardisease
		//		182	2002	5	17	COM|MIC:RET	Retinopathy
		//		182	2007	3	29	COM|MAC:OCC	Occlusionandstenosisofcarotidartery
		//		182	2014	2	4	COM|MIC:DF	DiabeticFoot

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.concept_cd, " +
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2_mac = new JSONObject();
			col_2_mac.put("id", 2);
			col_2_mac.put("label", "Macro");
			col_2_mac.put("type", "number");

			JSONObject col_tooltip_mac = new JSONObject();
			col_tooltip_mac.put("id", 3);
			col_tooltip_mac.put("label", "Tooltip MAC");
			col_tooltip_mac.put("type", "string");
			col_tooltip_mac.put("role", "tooltip");

			JSONObject col_annotation_mac = new JSONObject();
			col_annotation_mac.put("id",4);
			col_annotation_mac.put("label", "Annotation MAC");
			col_annotation_mac.put("type", "string");
			col_annotation_mac.put("role", "annotation");

			JSONObject col_2_mic = new JSONObject();
			col_2_mic.put("id", 5);
			col_2_mic.put("label", "Micro");
			col_2_mic.put("type", "number");

			JSONObject col_tooltip_mic = new JSONObject();
			col_tooltip_mic.put("id", 6);
			col_tooltip_mic.put("label", "Tooltip MIC");
			col_tooltip_mic.put("type", "string");
			col_tooltip_mic.put("role", "tooltip");

			JSONObject col_annotation_mic = new JSONObject();
			col_annotation_mic.put("id",7);
			col_annotation_mic.put("label", "Annotation MIC");
			col_annotation_mic.put("type", "string");
			col_annotation_mic.put("role", "annotation");

			JSONObject col_2_nv = new JSONObject();
			col_2_nv.put("id", 8);
			col_2_nv.put("label", "Non Vascular");
			col_2_nv.put("type", "number");

			JSONObject col_tooltip_nv = new JSONObject();
			col_tooltip_nv.put("id", 9);
			col_tooltip_nv.put("label", "Tooltip NV");
			col_tooltip_nv.put("type", "string");
			col_tooltip_nv.put("role", "tooltip");

			JSONObject col_annotation_nv = new JSONObject();
			col_annotation_nv.put("id",10);
			col_annotation_nv.put("label", "Annotation NV");
			col_annotation_nv.put("type", "string");
			col_annotation_nv.put("role", "annotation");

			JSONObject col_mac = new JSONObject();
			col_mac.put("id", 11);
			col_mac.put("label", "MAC");
			col_mac.put("type", "number");

			JSONObject col_min = new JSONObject();
			col_min.put("id", 12);
			col_min.put("label", "MIN");
			col_min.put("type", "number");

			JSONObject col_nv = new JSONObject();
			col_nv.put("id", 13);
			col_nv.put("label", "NV");
			col_nv.put("type", "number");

			cols.add(col_1);
			cols.add(col_2_mac);	
			cols.add(col_tooltip_mac);
			cols.add(col_annotation_mac);
			cols.add(col_2_mic);
			cols.add(col_tooltip_mic);
			cols.add(col_annotation_mic);
			cols.add(col_2_nv);
			cols.add(col_tooltip_nv);
			cols.add(col_annotation_nv);
			cols.add(col_mac);
			cols.add(col_min);
			cols.add(col_nv);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			int year = 0;
			int month =0;
			int day =0;
			int i=0;
			int complicationNum = 0;
			String tooltip = "";
			while(rs.next()){
				year = rs.getInt("h_year");
				if(i==0){//creo una riga finta 01/01/annoPrimaComplicanza per non avere le obs addossate allo start
					JSONObject row_objPlus = new JSONObject();
					JSONArray row_arrPlus = new JSONArray();
					JSONObject row_1Plus = new JSONObject();
					row_1Plus.put("v","Date("+year+",00,01)");

					JSONObject row_2_mac_Plus = new JSONObject();
					row_2_mac_Plus.put("v", null);

					JSONObject row_tooltipPlus_mac = new JSONObject();
					row_tooltipPlus_mac.put("v", null);

					JSONObject row_annotationPlus_mac = new JSONObject();
					row_annotationPlus_mac.put("v", null);

					JSONObject row_2_mic_Plus = new JSONObject();
					row_2_mic_Plus.put("v", null);

					JSONObject row_tooltipPlus_mic = new JSONObject();
					row_tooltipPlus_mic.put("v", null);

					JSONObject row_annotationPlus_mic = new JSONObject();
					row_annotationPlus_mic.put("v", null);

					JSONObject row_2_nv_Plus = new JSONObject();
					row_2_nv_Plus.put("v", null);

					JSONObject row_tooltipPlus_nv = new JSONObject();
					row_tooltipPlus_nv.put("v", null);

					JSONObject row_annotationPlus_nv = new JSONObject();
					row_annotationPlus_nv.put("v", null);

					JSONObject row_macPlus = new JSONObject();
					row_macPlus.put("v", 12);

					JSONObject row_micPlus = new JSONObject();
					row_micPlus.put("v", 5.5);

					JSONObject row_nvPlus = new JSONObject();
					row_nvPlus.put("v", 2.5);

					row_arrPlus.add(row_1Plus);
					row_arrPlus.add(row_2_mac_Plus);
					row_arrPlus.add(row_tooltipPlus_mac);
					row_arrPlus.add(row_annotationPlus_mac);
					row_arrPlus.add(row_2_mic_Plus);
					row_arrPlus.add(row_tooltipPlus_mic);
					row_arrPlus.add(row_annotationPlus_mic);
					row_arrPlus.add(row_2_nv_Plus);
					row_arrPlus.add(row_tooltipPlus_nv);
					row_arrPlus.add(row_annotationPlus_nv);
					row_arrPlus.add(row_macPlus);
					row_arrPlus.add(row_micPlus);
					row_arrPlus.add(row_nvPlus);
					row_objPlus.put("c",row_arrPlus);
					rows.add(row_objPlus);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				//year = rs.getInt("h_year");
				month = rs.getInt("h_month")-1;
				day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				String conceptCd = rs.getString("concept_cd");
				complicationNum = complicationsMap.get(conceptCd);
				JSONObject row_2_mac = new JSONObject();
				JSONObject row_2_mic = new JSONObject();
				JSONObject row_2_nv = new JSONObject();
				JSONObject row_annotation_mac = new JSONObject();
				JSONObject row_annotation_mic = new JSONObject();
				JSONObject row_annotation_nv = new JSONObject();
				JSONObject row_tooltip_mac = new JSONObject();
				JSONObject row_tooltip_mic = new JSONObject();
				JSONObject row_tooltip_nv = new JSONObject();
				String dateString = "Date: ";
				dateString = dateString.concat(String.valueOf(day)).concat("/").concat(String.valueOf(month+1)).concat("/").concat(String.valueOf(year)).concat("\n");
				tooltip = dateString.concat(rs.getString("obs_blob"));
				if(complicationNum<=2){
					row_2_mac.put("v", null);
					row_2_mic.put("v", null);
					row_2_nv.put("v", complicationNum);
					row_tooltip_mac.put("v", null);
					row_tooltip_mic.put("v", null);
					row_tooltip_nv.put("v", tooltip);
					row_annotation_mac.put("v", null);
					row_annotation_mic.put("v", null);
					row_annotation_nv.put("v", rs.getString("obs_blob"));
				}else if(complicationNum>2 && complicationNum<=5){
					row_2_mac.put("v", null);
					row_2_mic.put("v", complicationNum);
					row_2_nv.put("v", null);
					row_tooltip_mac.put("v", null);
					row_tooltip_mic.put("v", tooltip);
					row_tooltip_nv.put("v", null);
					row_annotation_mac.put("v", null);
					row_annotation_mic.put("v", rs.getString("obs_blob"));
					row_annotation_nv.put("v", null);
				}else if(complicationNum>5 && complicationNum<=11){
					row_2_mac.put("v", complicationNum);
					row_2_mic.put("v", null);
					row_2_nv.put("v", null);
					row_tooltip_mac.put("v", tooltip);
					row_tooltip_mic.put("v", null);
					row_tooltip_nv.put("v", null);
					row_annotation_mac.put("v", rs.getString("obs_blob"));
					row_annotation_mic.put("v", null);
					row_annotation_nv.put("v", null);
				}
				JSONObject row_mac = new JSONObject();
				row_mac.put("v", 12);

				JSONObject row_mic = new JSONObject();
				row_mic.put("v", 5.5);

				JSONObject row_nv = new JSONObject();
				row_nv.put("v", 2.5);

				row_arr.add(row_1);
				row_arr.add(row_2_mac);
				row_arr.add(row_tooltip_mac);
				row_arr.add(row_annotation_mac);
				row_arr.add(row_2_mic);
				row_arr.add(row_tooltip_mic);
				row_arr.add(row_annotation_mic);
				row_arr.add(row_2_nv);
				row_arr.add(row_tooltip_nv);
				row_arr.add(row_annotation_nv);
				row_arr.add(row_mac);
				row_arr.add(row_mic);
				row_arr.add(row_nv);			

				row_obj.put("c",row_arr);
				rows.add(row_obj);
				i++;
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	private double roundMaxValue(double myValue){
		double result = 0;
		if(myValue>=0 && myValue<=10){
			result = 15;
		} else if (myValue>10 && myValue<=30){
			result = 35;
		}else if (myValue>30 && myValue<=50){
			result = 60;
		}else if (myValue>50 && myValue<=100){
			result = 120;
		}else if (myValue>100 && myValue<=150){
			result = 200;
		}else if (myValue>150 && myValue<=200){
			result = 250;
		}else if (myValue>200 && myValue<=400){
			result = 450;
		}else if (myValue>400 && myValue<=800){
			result = 850;
		}else if (myValue>800){
			result = myValue;
		}
		return result;
	}

	//	private String roundAdherenceValue(double myValue){
	//		String result = "";
	//		if(myValue==0){
	//			result = "INTERRUPTION";
	//		}else if(myValue>0 && myValue<=20){
	//			result = "[0-20]";
	//		} else if (myValue>20 && myValue<=40){
	//			result = "[20-40]";
	//		}else if (myValue>40 && myValue<=60){
	//			result = "[40-60]";
	//		}else if (myValue>60 && myValue<=80){
	//			result = "[60-80]";
	//		}else if (myValue>80 && myValue<=100){
	//			result = "[80-100]";
	//		}else if (myValue>100 && myValue<=120){
	//			result = "[100-120]";
	//		}else if (myValue>120){
	//			result = "OVER";
	//		}
	//		return result;
	//	}

	private String roundAdherenceValue2(double myValue){
		String result = "";
		if(myValue==0){
			result = "INTERRUPTION";
		}else if(myValue>0 && myValue<=40){
			result = "[0-40]";
		}else if(myValue>40 && myValue<=80){
			result = "[40-80]";
		} else if (myValue>80 && myValue<=100){
			result = "[80-100]";
		}else if (myValue>100){
			result = "OVER";
		}
		return result;
	}

	private List<String> getLabelOrder (List<String> atcDescrSet, List<String> aderenzaLabelSet, HashMap<String, List<String>> atcLabelMap){
		List<String> result = new ArrayList<String>();
		for(String atcDescr: atcDescrSet){
			List<String> aderenzaLabel = atcLabelMap.get(atcDescr);
			for(String label : aderenzaLabel){
				if(!result.contains(label)){
					result.add(label);
				}
			}
			//if(result.size()==aderenzaLabelSet.size()){
			if(result.size()==7){
				break;
			}			
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2Data_Step3(String patients_num, String duration_nums, String numClasses, String maxDuration, String minDuration){

		Double numClassessDouble = Double.parseDouble(numClasses);
		int maxDurationInt = Integer.parseInt(maxDuration);
		int minDurationInt = Integer.parseInt(minDuration);

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);

			//get counter for MACRO MICRO NONVASCULAR CLASS
			String sql = "select concept_cd, observation_blob, patient_num " +
					"from "+observationTable+" t1 " +
					"where t1.CONCEPT_CD  like (?) "+
					"and t1.PATIENT_NUM in ("+ patients_num.substring(0,patients_num.lastIndexOf(","))+")";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));

			rs = pstmt.executeQuery();
			HashMap<String, I2B2ComorbidtyObservation> comorbidityMap = new HashMap<String, I2B2ComorbidtyObservation>();

			while(rs.next()){
				String conceptCd = rs.getString("concept_cd");
				Integer patientNumFromRS = rs.getInt("patient_num");
				String complClass = conceptCd.substring(0,6);
				I2B2ComorbidtyObservation cObs = comorbidityMap.get(conceptCd);
				if(cObs==null){ //concept_cd non presente
					//metto l'obs relativa alla singola comorbidità
					I2B2ComorbidtyObservation cObsNew = new I2B2ComorbidtyObservation();
					List<Integer> patientNumList = new ArrayList<Integer>();
					patientNumList.add(patientNumFromRS);
					cObsNew.setComorbidityDescr(rs.getString("observation_blob"));
					cObsNew.setPatientNumList(patientNumList);
					cObsNew.setConceptCd(conceptCd);
					comorbidityMap.put(conceptCd, cObsNew);
					//metto l'obs relativa alla classe (MACRO MICRO o NONVASCULAR)					
					I2B2ComorbidtyObservation cObs4Class = comorbidityMap.get(complClass);
					if(cObs4Class==null){ //classe non presente
						I2B2ComorbidtyObservation cObsNew4Class = new I2B2ComorbidtyObservation();
						List<Integer> patientNumList4Class = new ArrayList<Integer>();
						patientNumList4Class.add(rs.getInt("patient_num"));
						if(complClass.equals("COM|MA")){
							cObsNew4Class.setComorbidityDescr("Macro");
							cObsNew4Class.setConceptCd("_Macro");
							cObsNew.setComorbClassId(0);
						}else if(complClass.equals("COM|MI")){
							cObsNew4Class.setComorbidityDescr("Micro");
							cObsNew4Class.setConceptCd("_Micro");
							cObsNew.setComorbClassId(1);
						}else if(complClass.equals("COM|NV")){
							cObsNew4Class.setComorbidityDescr("Non vascular");
							cObsNew4Class.setConceptCd("_NotVascular");
							cObsNew.setComorbClassId(2);
						}
						cObsNew4Class.setPatientNumList(patientNumList4Class);
						comorbidityMap.put(complClass, cObsNew4Class);
					}else{ //classe presente
						//controllo se c'è il patientNum
						List<Integer> patientNumList4Class2 = cObs4Class.getPatientNumList();
						if(!patientNumList4Class2.contains(patientNumFromRS)){ //se non c'è lo aggiungo
							patientNumList4Class2.add(patientNumFromRS);
						}
						//setto la classe
						if(complClass.equals("COM|MA")){
							cObsNew.setComorbClassId(0);
						}else if(complClass.equals("COM|MI")){
							cObsNew.setComorbClassId(1);
						}else if(complClass.equals("COM|NV")){
							cObsNew.setComorbClassId(2);
						}
					}	
				}else{ //concept_cd già presente
					//controllo il paziente
					List<Integer> patientNumList4Obs = cObs.getPatientNumList();
					if(!patientNumList4Obs.contains(patientNumFromRS)){
						patientNumList4Obs.add(patientNumFromRS);
						//controllo se il paz c'è nelle classi (potrebbe anche esserci già)
						I2B2ComorbidtyObservation cObs4Class = comorbidityMap.get(complClass); //la classe c'è di sicuro xke c'è il conceptcd
						List<Integer> patientNumList4Class2 = cObs4Class.getPatientNumList();
						if(!patientNumList4Class2.contains(patientNumFromRS)){ //se non c'è lo aggiungo
							patientNumList4Class2.add(patientNumFromRS);
						}
					}
					//se il paz c'è qui, c'è anche nella categoria macro (x forza, quindi non controllo nemmeno)				
				}
			}

			//Creo gli oggetti
			JSONObject objOuter = new JSONObject();
			JSONObject objComplicationClassContainer = new JSONObject();
			JSONObject objComplicationDetailsContainer = new JSONObject();
			JSONObject objComplicationClassChartData = new JSONObject();
			JSONObject objComplicationDetailsChartData = new JSONObject();
			JSONArray objComplicationClassRawData = new JSONArray();
			JSONArray objComplicationDetailsRawData = new JSONArray();
			objComplicationClassContainer.put("chart_data",objComplicationClassChartData);
			objComplicationDetailsContainer.put("chart_data",objComplicationDetailsChartData);
			objComplicationClassContainer.put("raw_data",objComplicationClassRawData);
			objComplicationDetailsContainer.put("raw_data",objComplicationDetailsRawData);
			JSONArray colsClass = new JSONArray();
			JSONArray colsDetails = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			colsClass.add(col_1);
			colsClass.add(col_2);
			colsDetails.add(col_1);
			colsDetails.add(col_2);

			objComplicationClassChartData.put("cols", colsClass);
			objComplicationDetailsChartData.put("cols", colsDetails);

			Set<String> keys = comorbidityMap.keySet();
			List<I2B2ComorbidtyObservation> obsClassList = new ArrayList<I2B2ComorbidtyObservation>();
			List<I2B2ComorbidtyObservation> obsList = new ArrayList<I2B2ComorbidtyObservation>();
			for(String key : keys){
				I2B2ComorbidtyObservation obs = comorbidityMap.get(key);
				if(obs.getConceptCd().startsWith("_")){
					obsClassList.add(obs);
				}else{
					obsList.add(obs);
				}
			}
			//ordine alfabetico in base a observation_blob in modo che il piechart non mi incasini le slice
			Collections.sort(obsClassList, I2B2ComorbidtyObservation.nameComparator);
			Collections.sort(obsList, I2B2ComorbidtyObservation.nameComparator);
			JSONArray rows = new JSONArray();
			for(I2B2ComorbidtyObservation ob : obsClassList){			
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();
				JSONObject row_1 = new JSONObject();
				row_1.put("v", ob.getComorbidityDescr());
				JSONObject row_2 = new JSONObject();
				row_2.put("v", ob.getPatientNumList().size());
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
				String patientList = ob.createPatientNumListString();
				JSONObject raw_data= new JSONObject();
				raw_data.put("patient_nums", patientList);
				objComplicationClassRawData.add(raw_data);

			}
			objComplicationClassChartData.put("rows", rows);
			JSONArray rowsDetails = new JSONArray();
			for(I2B2ComorbidtyObservation ob : obsList){			
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();
				JSONObject row_1 = new JSONObject();
				row_1.put("v", ob.getComorbidityDescr());
				JSONObject row_2 = new JSONObject();
				row_2.put("v", ob.getPatientNumList().size());
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_obj.put("c",row_arr);
				String patientList = ob.createPatientNumListString();
				JSONObject raw_data= new JSONObject();
				raw_data.put("patient_nums", patientList);
				rowsDetails.add(row_obj);
				objComplicationDetailsRawData.add(raw_data);
			}
			objComplicationDetailsChartData.put("rows", rowsDetails);
			//****************column chart per durata LOC***************************+
			JSONObject objDuration = new JSONObject();
			JSONArray colsDuration = new JSONArray();
			JSONObject col_1Duration = new JSONObject();
			col_1Duration.put("id", 1);
			col_1Duration.put("label", "Patient IdCod");
			col_1Duration.put("type", "string");
			JSONObject col_2Duration = new JSONObject();
			col_2Duration.put("id", 2);
			col_2Duration.put("label", "Duration");
			col_2Duration.put("type", "number");
			colsDuration.add(col_1Duration);
			colsDuration.add(col_2Duration);
			objDuration.put("cols", colsDuration);
			JSONArray rowsDuration = new JSONArray();
			String[] durationArray = duration_nums.split(",");
			int[] durationArrayInt = new int[durationArray.length];
			String[] patientArray = patients_num.split(",");
			for(int i=0; i< durationArray.length; i++){
				int duration = Integer.parseInt(durationArray[i]);
				durationArrayInt[i] = duration;
				String patientIdCod = patientArray[i];
				JSONArray row_arrDuration = new JSONArray();
				JSONObject row_objDuration = new JSONObject();
				JSONObject row_1Duration = new JSONObject();
				JSONObject row_2Duration = new JSONObject();
				row_1Duration.put("v", patientIdCod);
				row_2Duration.put("v",duration);
				row_arrDuration.add(row_1Duration);
				row_arrDuration.add(row_2Duration);
				row_objDuration.put("c",row_arrDuration);
				rowsDuration.add(row_objDuration);
			}
			objDuration.put("rows", rowsDuration);
			//***************************************************************************************
			//***************** generazione istogramma e creazione bin asse x ***********************		
			HistogramObject myHist = HistogramUtil.calculateBin(minDurationInt, maxDurationInt, numClassessDouble, durationArrayInt);
			JSONObject objHist = new JSONObject();
			JSONArray colsHist= new JSONArray();
			JSONObject col_1Hist = new JSONObject();
			col_1Hist.put("id", 1);
			col_1Hist.put("label", "Days");
			col_1Hist.put("type", "string");
			JSONObject col_2Hist = new JSONObject();
			col_2Hist.put("id", 2);
			col_2Hist.put("label", "Patients");
			col_2Hist.put("type", "number");
			colsHist.add(col_1Hist);
			colsHist.add(col_2Hist);
			objHist.put("cols", colsHist);
			JSONArray rowsHist = new JSONArray();

			for(int i=0; i< myHist.getFrequencyArray().length; i++){
				int duration = myHist.getFrequencyArray()[i];
				String xLabel = myHist.getxAxisLabelArray()[i];
				JSONArray row_arrHist = new JSONArray();
				JSONObject row_objHist = new JSONObject();
				JSONObject row_1Hist = new JSONObject();
				JSONObject row_2Hist = new JSONObject();
				row_1Hist.put("v", xLabel);
				row_2Hist.put("v",duration);
				row_arrHist.add(row_1Hist);
				row_arrHist.add(row_2Hist);
				row_objHist.put("c",row_arrHist);
				rowsHist.add(row_objHist);
			}
			objHist.put("rows", rowsHist);
			//***************************************************************************++			

			objOuter.put("comorb_class", objComplicationClassContainer);
			objOuter.put("comorb_details", objComplicationDetailsContainer);
			objOuter.put("duration_details", objDuration);
			objOuter.put("hist_details", objHist);
			StringWriter out = new StringWriter();
			objOuter.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getData4ComplicationDrillDown(String jsonIn){
		Properties prop = new Properties();
		InputStream input = null;
		String result = "";
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);
			int yStep = Integer.parseInt(prop.getProperty("complicationDrillDownYStep"));

			//cambiare quando le storie saranno reali
			//
			//			String jsonIn_old = "{\"histories\":[{\"label\":\"story_Angina\",\"steps\": [{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":18436,\"duration\":0},{\"idcod\":19676,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":27, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.771213,\"patients\": [{\"idcod\":1091,\"duration\":0},{\"idcod\":1195,\"duration\":0},{\"idcod\":2483,\"duration\":0},{\"idcod\":4238,\"duration\":0},{\"idcod\":4447,\"duration\":0},{\"idcod\":7526,\"duration\":0},{\"idcod\":8082,\"duration\":0},{\"idcod\":8271,\"duration\":0},{\"idcod\":9318,\"duration\":0},{\"idcod\":11000,\"duration\":0},{\"idcod\":13379,\"duration\":0},{\"idcod\":14564,\"duration\":0},{\"idcod\":14740,\"duration\":0},{\"idcod\":15703,\"duration\":0},{\"idcod\":16897,\"duration\":0},{\"idcod\":17288,\"duration\":0},{\"idcod\":18107,\"duration\":0},{\"idcod\":18289,\"duration\":0},{\"idcod\":18443,\"duration\":0},{\"idcod\":19086,\"duration\":0},{\"idcod\":20432,\"duration\":0},{\"idcod\":20547,\"duration\":0},{\"idcod\":20781,\"duration\":0},{\"idcod\":21078,\"duration\":0},{\"idcod\":21263,\"duration\":0},{\"idcod\":21592,\"duration\":0},{\"idcod\":21599,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Angina\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":1250,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2030,\"25prctile\":2030,\"75prctile\":2030,\"patients\": [{\"idcod\":1250,\"duration\":2030}]},{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":1250,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":221,\"duration\":0},{\"idcod\":9100,\"duration\":0},{\"idcod\":19014,\"duration\":0},{\"idcod\":20946,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":1569,\"25prctile\":605,\"75prctile\":2716,\"patients\": [{\"idcod\":221,\"duration\":3203},{\"idcod\":9100,\"duration\":2229},{\"idcod\":19014,\"duration\":909},{\"idcod\":20946,\"duration\":300}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":221,\"duration\":0},{\"idcod\":9100,\"duration\":0},{\"idcod\":19014,\"duration\":0},{\"idcod\":20946,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11464,\"duration\":0},{\"idcod\":15580,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":4788,\"25prctile\":3026,\"75prctile\":6549,\"patients\": [{\"idcod\":11464,\"duration\":3026},{\"idcod\":15580,\"duration\":6549}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11464,\"duration\":0},{\"idcod\":15580,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":920,\"25prctile\":474,\"75prctile\":1365,\"patients\": [{\"idcod\":11464,\"duration\":1365},{\"idcod\":15580,\"duration\":474}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11464,\"duration\":0},{\"idcod\":15580,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":16778,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3372,\"25prctile\":195,\"75prctile\":6549,\"patients\": [{\"idcod\":16778,\"duration\":6549}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":16778,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":240,\"25prctile\":6,\"75prctile\":474,\"patients\": [{\"idcod\":16778,\"duration\":474}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":16778,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18414,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3822,\"25prctile\":3822,\"75prctile\":3822,\"patients\": [{\"idcod\":18414,\"duration\":3822}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18414,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18414,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1902,\"25prctile\":1902,\"75prctile\":1902,\"patients\": [{\"idcod\":18414,\"duration\":1902}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18414,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14191,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":6331,\"25prctile\":6331,\"75prctile\":6331,\"patients\": [{\"idcod\":14191,\"duration\":6331}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14191,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2142,\"25prctile\":2142,\"75prctile\":2142,\"patients\": [{\"idcod\":14191,\"duration\":2142}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14191,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":539,\"25prctile\":539,\"75prctile\":539,\"patients\": [{\"idcod\":14191,\"duration\":539}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14191,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Peripheralvasculardisease_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11766,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3299,\"25prctile\":3299,\"75prctile\":3299,\"patients\": [{\"idcod\":11766,\"duration\":3299}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11766,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":513,\"25prctile\":513,\"75prctile\":513,\"patients\": [{\"idcod\":11766,\"duration\":513}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11766,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1175,\"25prctile\":1175,\"75prctile\":1175,\"patients\": [{\"idcod\":11766,\"duration\":1175}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11766,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4852,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1979,\"25prctile\":1979,\"75prctile\":1979,\"patients\": [{\"idcod\":4852,\"duration\":1979}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4852,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":663,\"25prctile\":663,\"75prctile\":663,\"patients\": [{\"idcod\":4852,\"duration\":663}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4852,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4852,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":10719,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4225,\"25prctile\":1900,\"75prctile\":6549,\"patients\": [{\"idcod\":10719,\"duration\":6549}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":10719,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":659,\"25prctile\":474,\"75prctile\":843,\"patients\": [{\"idcod\":10719,\"duration\":474}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":10719,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Retinopathy_Nephropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20902,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1444,\"25prctile\":1444,\"75prctile\":1444,\"patients\": [{\"idcod\":20902,\"duration\":1444}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20902,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3,\"25prctile\":3,\"75prctile\":3,\"patients\": [{\"idcod\":20902,\"duration\":3}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20902,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":755,\"25prctile\":755,\"75prctile\":755,\"patients\": [{\"idcod\":20902,\"duration\":755}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20902,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5,\"25prctile\":5,\"75prctile\":5,\"patients\": [{\"idcod\":20902,\"duration\":5}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20902,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":20042,\"duration\":0},{\"idcod\":20918,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":605,\"25prctile\":209,\"75prctile\":4083,\"patients\": [{\"idcod\":20042,\"duration\":7257},{\"idcod\":20918,\"duration\":300}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":20042,\"duration\":0},{\"idcod\":20918,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Nephropathy_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":238,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2160,\"25prctile\":2160,\"75prctile\":2160,\"patients\": [{\"idcod\":238,\"duration\":2160}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":238,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2174,\"25prctile\":2174,\"75prctile\":2174,\"patients\": [{\"idcod\":238,\"duration\":2174}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":238,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":770,\"25prctile\":770,\"75prctile\":770,\"patients\": [{\"idcod\":238,\"duration\":770}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":238,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Nephropathy_FatLiverDisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6202,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1369,\"25prctile\":1369,\"75prctile\":1369,\"patients\": [{\"idcod\":6202,\"duration\":1369}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6202,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":306,\"25prctile\":306,\"75prctile\":306,\"patients\": [{\"idcod\":6202,\"duration\":306}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6202,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1773,\"25prctile\":1773,\"75prctile\":1773,\"patients\": [{\"idcod\":6202,\"duration\":1773}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6202,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Neuropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9690,\"duration\":0},{\"idcod\":21133,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1166,\"25prctile\":605,\"75prctile\":1683,\"patients\": [{\"idcod\":9690,\"duration\":1944},{\"idcod\":21133,\"duration\":300}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9690,\"duration\":0},{\"idcod\":21133,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Neuropathy_Occlusionandstenosisofcarotidartery_Retinopathy_Peripheralvasculardisease_DiabeticFoot\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14900,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":59,\"25prctile\":59,\"75prctile\":59,\"patients\": [{\"idcod\":14900,\"duration\":59}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14900,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":46,\"25prctile\":46,\"75prctile\":46,\"patients\": [{\"idcod\":14900,\"duration\":46}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14900,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":334,\"25prctile\":334,\"75prctile\":334,\"patients\": [{\"idcod\":14900,\"duration\":334}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14900,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2219,\"25prctile\":2219,\"75prctile\":2219,\"patients\": [{\"idcod\":14900,\"duration\":2219}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14900,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1282,\"25prctile\":1282,\"75prctile\":1282,\"patients\": [{\"idcod\":14900,\"duration\":1282}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14900,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Neuropathy_Retinopathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7800,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":491,\"25prctile\":491,\"75prctile\":491,\"patients\": [{\"idcod\":7800,\"duration\":491}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7800,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":322,\"25prctile\":322,\"75prctile\":322,\"patients\": [{\"idcod\":7800,\"duration\":322}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7800,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":20, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":1223,\"duration\":0},{\"idcod\":5581,\"duration\":0},{\"idcod\":6422,\"duration\":0},{\"idcod\":7174,\"duration\":0},{\"idcod\":7433,\"duration\":0},{\"idcod\":7665,\"duration\":0},{\"idcod\":9863,\"duration\":0},{\"idcod\":13252,\"duration\":0},{\"idcod\":16415,\"duration\":0},{\"idcod\":16786,\"duration\":0},{\"idcod\":18421,\"duration\":0},{\"idcod\":18580,\"duration\":0},{\"idcod\":19228,\"duration\":0},{\"idcod\":19446,\"duration\":0},{\"idcod\":19989,\"duration\":0},{\"idcod\":19993,\"duration\":0},{\"idcod\":20036,\"duration\":0},{\"idcod\":20151,\"duration\":0},{\"idcod\":20361,\"duration\":0},{\"idcod\":20388,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":20, \"time\":1959,\"25prctile\":317,\"75prctile\":4622,\"patients\": [{\"idcod\":1223,\"duration\":8719},{\"idcod\":5581,\"duration\":59},{\"idcod\":6422,\"duration\":4465},{\"idcod\":7174,\"duration\":337},{\"idcod\":7433,\"duration\":775},{\"idcod\":7665,\"duration\":8056},{\"idcod\":9863,\"duration\":5563},{\"idcod\":13252,\"duration\":427},{\"idcod\":16415,\"duration\":2466},{\"idcod\":16786,\"duration\":83},{\"idcod\":18421,\"duration\":3592},{\"idcod\":18580,\"duration\":2146},{\"idcod\":19228,\"duration\":2438},{\"idcod\":19446,\"duration\":8646},{\"idcod\":19989,\"duration\":712},{\"idcod\":19993,\"duration\":296},{\"idcod\":20036,\"duration\":1772},{\"idcod\":20151,\"duration\":31},{\"idcod\":20361,\"duration\":104},{\"idcod\":20388,\"duration\":4778}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":20, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":1223,\"duration\":0},{\"idcod\":5581,\"duration\":0},{\"idcod\":6422,\"duration\":0},{\"idcod\":7174,\"duration\":0},{\"idcod\":7433,\"duration\":0},{\"idcod\":7665,\"duration\":0},{\"idcod\":9863,\"duration\":0},{\"idcod\":13252,\"duration\":0},{\"idcod\":16415,\"duration\":0},{\"idcod\":16786,\"duration\":0},{\"idcod\":18421,\"duration\":0},{\"idcod\":18580,\"duration\":0},{\"idcod\":19228,\"duration\":0},{\"idcod\":19446,\"duration\":0},{\"idcod\":19989,\"duration\":0},{\"idcod\":19993,\"duration\":0},{\"idcod\":20036,\"duration\":0},{\"idcod\":20151,\"duration\":0},{\"idcod\":20361,\"duration\":0},{\"idcod\":20388,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8042,\"duration\":0},{\"idcod\":20558,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":2482,\"25prctile\":29,\"75prctile\":4934,\"patients\": [{\"idcod\":8042,\"duration\":4934},{\"idcod\":20558,\"duration\":29}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8042,\"duration\":0},{\"idcod\":20558,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1167,\"25prctile\":743,\"75prctile\":1591,\"patients\": [{\"idcod\":8042,\"duration\":1591},{\"idcod\":20558,\"duration\":743}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8042,\"duration\":0},{\"idcod\":20558,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17643,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":233,\"25prctile\":29,\"75prctile\":436,\"patients\": [{\"idcod\":17643,\"duration\":29}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17643,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1362,\"25prctile\":743,\"75prctile\":1981,\"patients\": [{\"idcod\":17643,\"duration\":743}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17643,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":7541,\"duration\":0},{\"idcod\":8868,\"duration\":0},{\"idcod\":15051,\"duration\":0},{\"idcod\":15157,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":7039,\"25prctile\":4931,\"75prctile\":8865,\"patients\": [{\"idcod\":7541,\"duration\":7868},{\"idcod\":8868,\"duration\":9862},{\"idcod\":15051,\"duration\":6209},{\"idcod\":15157,\"duration\":3653}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":7541,\"duration\":0},{\"idcod\":8868,\"duration\":0},{\"idcod\":15051,\"duration\":0},{\"idcod\":15157,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":2365,\"25prctile\":1690,\"75prctile\":4264,\"patients\": [{\"idcod\":7541,\"duration\":1056},{\"idcod\":8868,\"duration\":2405},{\"idcod\":15051,\"duration\":6123},{\"idcod\":15157,\"duration\":2324}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":7541,\"duration\":0},{\"idcod\":8868,\"duration\":0},{\"idcod\":15051,\"duration\":0},{\"idcod\":15157,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_FatLiverDisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7459,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":6940,\"25prctile\":6940,\"75prctile\":6940,\"patients\": [{\"idcod\":7459,\"duration\":6940}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7459,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":357,\"25prctile\":357,\"75prctile\":357,\"patients\": [{\"idcod\":7459,\"duration\":357}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7459,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":737,\"25prctile\":737,\"75prctile\":737,\"patients\": [{\"idcod\":7459,\"duration\":737}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7459,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Retinopathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":16741,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4931,\"25prctile\":1855,\"75prctile\":8036,\"patients\": [{\"idcod\":16741,\"duration\":3653}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":16741,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2365,\"25prctile\":1726,\"75prctile\":4264,\"patients\": [{\"idcod\":16741,\"duration\":2324}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":16741,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":14304,\"duration\":0},{\"idcod\":16864,\"duration\":0},{\"idcod\":19415,\"duration\":0},{\"idcod\":19940,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":1959,\"25prctile\":200,\"75prctile\":3783,\"patients\": [{\"idcod\":14304,\"duration\":3974},{\"idcod\":16864,\"duration\":3407},{\"idcod\":19415,\"duration\":38},{\"idcod\":19940,\"duration\":4778}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":14304,\"duration\":0},{\"idcod\":16864,\"duration\":0},{\"idcod\":19415,\"duration\":0},{\"idcod\":19940,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19948,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":12418,\"25prctile\":12418,\"75prctile\":12418,\"patients\": [{\"idcod\":19948,\"duration\":12418}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19948,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":42,\"25prctile\":42,\"75prctile\":42,\"patients\": [{\"idcod\":19948,\"duration\":42}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19948,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":811,\"25prctile\":811,\"75prctile\":811,\"patients\": [{\"idcod\":19948,\"duration\":811}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19948,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Neuropathy_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6683,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4134,\"25prctile\":4134,\"75prctile\":4134,\"patients\": [{\"idcod\":6683,\"duration\":4134}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6683,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":120,\"25prctile\":120,\"75prctile\":120,\"patients\": [{\"idcod\":6683,\"duration\":120}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6683,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":879,\"25prctile\":879,\"75prctile\":879,\"patients\": [{\"idcod\":6683,\"duration\":879}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6683,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1,\"25prctile\":1,\"75prctile\":1,\"patients\": [{\"idcod\":6683,\"duration\":1}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6683,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17491,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5323,\"25prctile\":5323,\"75prctile\":5323,\"patients\": [{\"idcod\":17491,\"duration\":5323}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17491,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17491,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":6009,\"25prctile\":6009,\"75prctile\":6009,\"patients\": [{\"idcod\":8611,\"duration\":6009}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1578,\"25prctile\":1578,\"75prctile\":1578,\"patients\": [{\"idcod\":8611,\"duration\":1578}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11572,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":32,\"25prctile\":32,\"75prctile\":32,\"patients\": [{\"idcod\":11572,\"duration\":32}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11572,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11572,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1323,\"25prctile\":1323,\"75prctile\":1323,\"patients\": [{\"idcod\":11572,\"duration\":1323}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11572,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Retinopathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":5433,\"duration\":0},{\"idcod\":8440,\"duration\":0},{\"idcod\":13439,\"duration\":0},{\"idcod\":15565,\"duration\":0},{\"idcod\":19717,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":5, \"time\":1754,\"25prctile\":362,\"75prctile\":4185,\"patients\": [{\"idcod\":5433,\"duration\":1735},{\"idcod\":8440,\"duration\":511},{\"idcod\":13439,\"duration\":7242},{\"idcod\":15565,\"duration\":753},{\"idcod\":19717,\"duration\":4778}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":5433,\"duration\":0},{\"idcod\":8440,\"duration\":0},{\"idcod\":13439,\"duration\":0},{\"idcod\":15565,\"duration\":0},{\"idcod\":19717,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Retinopathy_Neuropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10076,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":202,\"25prctile\":202,\"75prctile\":202,\"patients\": [{\"idcod\":10076,\"duration\":202}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10076,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1777,\"25prctile\":1777,\"75prctile\":1777,\"patients\": [{\"idcod\":10076,\"duration\":1777}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10076,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5647,\"duration\":0},{\"idcod\":19402,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1032,\"25prctile\":21,\"75prctile\":2043,\"patients\": [{\"idcod\":5647,\"duration\":21},{\"idcod\":19402,\"duration\":2043}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5647,\"duration\":0},{\"idcod\":19402,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":2125,\"25prctile\":147,\"75prctile\":4103,\"patients\": [{\"idcod\":5647,\"duration\":4103},{\"idcod\":19402,\"duration\":147}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5647,\"duration\":0},{\"idcod\":19402,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Retinopathy_Peripheralvasculardisease_Nephropathy_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20869,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2557,\"25prctile\":2557,\"75prctile\":2557,\"patients\": [{\"idcod\":20869,\"duration\":2557}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20869,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":487,\"25prctile\":487,\"75prctile\":487,\"patients\": [{\"idcod\":20869,\"duration\":487}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20869,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":577,\"25prctile\":577,\"75prctile\":577,\"patients\": [{\"idcod\":20869,\"duration\":577}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20869,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":774,\"25prctile\":774,\"75prctile\":774,\"patients\": [{\"idcod\":20869,\"duration\":774}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20869,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":222,\"25prctile\":222,\"75prctile\":222,\"patients\": [{\"idcod\":20869,\"duration\":222}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20869,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Stroke_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9240,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5856,\"25prctile\":5856,\"75prctile\":5856,\"patients\": [{\"idcod\":9240,\"duration\":5856}]},{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9240,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1919,\"25prctile\":1919,\"75prctile\":1919,\"patients\": [{\"idcod\":9240,\"duration\":1919}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9240,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.771213,\"patients\": [{\"idcod\":18183,\"duration\":0},{\"idcod\":21582,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot_Neuropathy\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6142,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3307,\"25prctile\":3307,\"75prctile\":3307,\"patients\": [{\"idcod\":6142,\"duration\":3307}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6142,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20139,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":850,\"25prctile\":850,\"75prctile\":850,\"patients\": [{\"idcod\":20139,\"duration\":850}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20139,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1210,\"25prctile\":1210,\"75prctile\":1210,\"patients\": [{\"idcod\":20139,\"duration\":1210}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20139,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":54, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":3579,\"duration\":0},{\"idcod\":4499,\"duration\":0},{\"idcod\":4755,\"duration\":0},{\"idcod\":5061,\"duration\":0},{\"idcod\":5333,\"duration\":0},{\"idcod\":5497,\"duration\":0},{\"idcod\":5622,\"duration\":0},{\"idcod\":5743,\"duration\":0},{\"idcod\":5926,\"duration\":0},{\"idcod\":6068,\"duration\":0},{\"idcod\":6264,\"duration\":0},{\"idcod\":6448,\"duration\":0},{\"idcod\":6473,\"duration\":0},{\"idcod\":6857,\"duration\":0},{\"idcod\":6893,\"duration\":0},{\"idcod\":7231,\"duration\":0},{\"idcod\":7562,\"duration\":0},{\"idcod\":7676,\"duration\":0},{\"idcod\":8231,\"duration\":0},{\"idcod\":8283,\"duration\":0},{\"idcod\":9772,\"duration\":0},{\"idcod\":10573,\"duration\":0},{\"idcod\":11067,\"duration\":0},{\"idcod\":11375,\"duration\":0},{\"idcod\":11435,\"duration\":0},{\"idcod\":12550,\"duration\":0},{\"idcod\":13156,\"duration\":0},{\"idcod\":13365,\"duration\":0},{\"idcod\":14370,\"duration\":0},{\"idcod\":14641,\"duration\":0},{\"idcod\":15240,\"duration\":0},{\"idcod\":15413,\"duration\":0},{\"idcod\":15620,\"duration\":0},{\"idcod\":15839,\"duration\":0},{\"idcod\":17067,\"duration\":0},{\"idcod\":17207,\"duration\":0},{\"idcod\":17390,\"duration\":0},{\"idcod\":17724,\"duration\":0},{\"idcod\":18086,\"duration\":0},{\"idcod\":18278,\"duration\":0},{\"idcod\":18322,\"duration\":0},{\"idcod\":18397,\"duration\":0},{\"idcod\":19064,\"duration\":0},{\"idcod\":19085,\"duration\":0},{\"idcod\":19202,\"duration\":0},{\"idcod\":19342,\"duration\":0},{\"idcod\":19499,\"duration\":0},{\"idcod\":19538,\"duration\":0},{\"idcod\":19663,\"duration\":0},{\"idcod\":19886,\"duration\":0},{\"idcod\":20401,\"duration\":0},{\"idcod\":20491,\"duration\":0},{\"idcod\":20497,\"duration\":0},{\"idcod\":21316,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":10708,\"duration\":0},{\"idcod\":20043,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":595,\"25prctile\":278,\"75prctile\":912,\"patients\": [{\"idcod\":10708,\"duration\":912},{\"idcod\":20043,\"duration\":278}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":10708,\"duration\":0},{\"idcod\":20043,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Chronicischemicheartdisease_Neuropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11084,\"duration\":0},{\"idcod\":19139,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":905,\"25prctile\":149,\"75prctile\":1661,\"patients\": [{\"idcod\":11084,\"duration\":1661},{\"idcod\":19139,\"duration\":149}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11084,\"duration\":0},{\"idcod\":19139,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1784,\"25prctile\":1272,\"75prctile\":2295,\"patients\": [{\"idcod\":11084,\"duration\":2295},{\"idcod\":19139,\"duration\":1272}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11084,\"duration\":0},{\"idcod\":19139,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Nephropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":14, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":5422,\"duration\":0},{\"idcod\":5425,\"duration\":0},{\"idcod\":6511,\"duration\":0},{\"idcod\":6693,\"duration\":0},{\"idcod\":12994,\"duration\":0},{\"idcod\":15897,\"duration\":0},{\"idcod\":16495,\"duration\":0},{\"idcod\":16868,\"duration\":0},{\"idcod\":16993,\"duration\":0},{\"idcod\":17101,\"duration\":0},{\"idcod\":18054,\"duration\":0},{\"idcod\":18244,\"duration\":0},{\"idcod\":19073,\"duration\":0},{\"idcod\":20310,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":14, \"time\":771,\"25prctile\":368,\"75prctile\":1643,\"patients\": [{\"idcod\":5422,\"duration\":823},{\"idcod\":5425,\"duration\":368},{\"idcod\":6511,\"duration\":3866},{\"idcod\":6693,\"duration\":606},{\"idcod\":12994,\"duration\":718},{\"idcod\":15897,\"duration\":1643},{\"idcod\":16495,\"duration\":1161},{\"idcod\":16868,\"duration\":209},{\"idcod\":16993,\"duration\":899},{\"idcod\":17101,\"duration\":1841},{\"idcod\":18054,\"duration\":328},{\"idcod\":18244,\"duration\":2585},{\"idcod\":19073,\"duration\":687},{\"idcod\":20310,\"duration\":241}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":14, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":5422,\"duration\":0},{\"idcod\":5425,\"duration\":0},{\"idcod\":6511,\"duration\":0},{\"idcod\":6693,\"duration\":0},{\"idcod\":12994,\"duration\":0},{\"idcod\":15897,\"duration\":0},{\"idcod\":16495,\"duration\":0},{\"idcod\":16868,\"duration\":0},{\"idcod\":16993,\"duration\":0},{\"idcod\":17101,\"duration\":0},{\"idcod\":18054,\"duration\":0},{\"idcod\":18244,\"duration\":0},{\"idcod\":19073,\"duration\":0},{\"idcod\":20310,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":4025,\"duration\":0},{\"idcod\":9031,\"duration\":0},{\"idcod\":9647,\"duration\":0},{\"idcod\":14647,\"duration\":0},{\"idcod\":17888,\"duration\":0},{\"idcod\":19629,\"duration\":0},{\"idcod\":20340,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":7, \"time\":512,\"25prctile\":209,\"75prctile\":1841,\"patients\": [{\"idcod\":4025,\"duration\":2700},{\"idcod\":9031,\"duration\":336},{\"idcod\":9647,\"duration\":1904},{\"idcod\":14647,\"duration\":0},{\"idcod\":17888,\"duration\":0},{\"idcod\":19629,\"duration\":47},{\"idcod\":20340,\"duration\":241}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":4025,\"duration\":0},{\"idcod\":9031,\"duration\":0},{\"idcod\":9647,\"duration\":0},{\"idcod\":14647,\"duration\":0},{\"idcod\":17888,\"duration\":0},{\"idcod\":19629,\"duration\":0},{\"idcod\":20340,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11805,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1899,\"25prctile\":1899,\"75prctile\":1899,\"patients\": [{\"idcod\":11805,\"duration\":1899}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11805,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":7,\"25prctile\":7,\"75prctile\":7,\"patients\": [{\"idcod\":11805,\"duration\":7}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11805,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy_Nephropathy_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8128,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2568,\"25prctile\":2568,\"75prctile\":2568,\"patients\": [{\"idcod\":8128,\"duration\":2568}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8128,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1873,\"25prctile\":1873,\"75prctile\":1873,\"patients\": [{\"idcod\":8128,\"duration\":1873}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8128,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":666,\"25prctile\":666,\"75prctile\":666,\"patients\": [{\"idcod\":8128,\"duration\":666}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8128,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":9,\"25prctile\":9,\"75prctile\":9,\"patients\": [{\"idcod\":8128,\"duration\":9}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8128,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":116,\"25prctile\":116,\"75prctile\":116,\"patients\": [{\"idcod\":8128,\"duration\":116}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8128,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy_Retinopathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20008,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":9,\"25prctile\":9,\"75prctile\":9,\"patients\": [{\"idcod\":20008,\"duration\":9}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20008,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":393,\"25prctile\":393,\"75prctile\":393,\"patients\": [{\"idcod\":20008,\"duration\":393}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20008,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":1079,\"duration\":0},{\"idcod\":8573,\"duration\":0},{\"idcod\":17654,\"duration\":0},{\"idcod\":18744,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":793,\"25prctile\":241,\"75prctile\":1841,\"patients\": [{\"idcod\":1079,\"duration\":4869},{\"idcod\":8573,\"duration\":409},{\"idcod\":17654,\"duration\":2218},{\"idcod\":18744,\"duration\":241}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":1079,\"duration\":0},{\"idcod\":8573,\"duration\":0},{\"idcod\":17654,\"duration\":0},{\"idcod\":18744,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8471,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":551,\"25prctile\":551,\"75prctile\":551,\"patients\": [{\"idcod\":8471,\"duration\":551}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8471,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":302,\"25prctile\":302,\"75prctile\":302,\"patients\": [{\"idcod\":8471,\"duration\":302}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8471,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Occlusionandstenosisofcarotidartery_Neuropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20180,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":21,\"25prctile\":21,\"75prctile\":21,\"patients\": [{\"idcod\":20180,\"duration\":21}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20180,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20180,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":11758,\"duration\":0},{\"idcod\":15424,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":793,\"25prctile\":209,\"75prctile\":1725,\"patients\": [{\"idcod\":11758,\"duration\":185},{\"idcod\":15424,\"duration\":241}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":11758,\"duration\":0},{\"idcod\":15424,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19311,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":694,\"25prctile\":694,\"75prctile\":694,\"patients\": [{\"idcod\":19311,\"duration\":694}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19311,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":774,\"25prctile\":774,\"75prctile\":774,\"patients\": [{\"idcod\":19311,\"duration\":774}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19311,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":10615,\"duration\":0},{\"idcod\":17356,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":892,\"25prctile\":241,\"75prctile\":1841,\"patients\": [{\"idcod\":10615,\"duration\":2038},{\"idcod\":17356,\"duration\":241}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":10615,\"duration\":0},{\"idcod\":17356,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":780,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":650,\"25prctile\":650,\"75prctile\":650,\"patients\": [{\"idcod\":780,\"duration\":650}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":780,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2336,\"25prctile\":2336,\"75prctile\":2336,\"patients\": [{\"idcod\":780,\"duration\":2336}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":780,\"duration\":0}]}]},{\"label\":\"story_Nephropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":18, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":2386,\"duration\":0},{\"idcod\":5927,\"duration\":0},{\"idcod\":6086,\"duration\":0},{\"idcod\":6864,\"duration\":0},{\"idcod\":11037,\"duration\":0},{\"idcod\":12151,\"duration\":0},{\"idcod\":12592,\"duration\":0},{\"idcod\":14661,\"duration\":0},{\"idcod\":15533,\"duration\":0},{\"idcod\":15755,\"duration\":0},{\"idcod\":16222,\"duration\":0},{\"idcod\":17269,\"duration\":0},{\"idcod\":18271,\"duration\":0},{\"idcod\":18463,\"duration\":0},{\"idcod\":19043,\"duration\":0},{\"idcod\":19991,\"duration\":0},{\"idcod\":20743,\"duration\":0},{\"idcod\":20811,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20784,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":323,\"25prctile\":323,\"75prctile\":323,\"patients\": [{\"idcod\":20784,\"duration\":323}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20784,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":3633,\"duration\":0},{\"idcod\":16801,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":628,\"25prctile\":164,\"75prctile\":1091,\"patients\": [{\"idcod\":3633,\"duration\":164},{\"idcod\":16801,\"duration\":1091}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":3633,\"duration\":0},{\"idcod\":16801,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9802,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2379,\"25prctile\":2379,\"75prctile\":2379,\"patients\": [{\"idcod\":9802,\"duration\":2379}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9802,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9802,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":14,\"25prctile\":14,\"75prctile\":14,\"patients\": [{\"idcod\":7397,\"duration\":14}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":445,\"25prctile\":445,\"75prctile\":445,\"patients\": [{\"idcod\":7397,\"duration\":445}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4720,\"25prctile\":4720,\"75prctile\":4720,\"patients\": [{\"idcod\":7397,\"duration\":4720}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Neuropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17104,\"duration\":0},{\"idcod\":20410,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":180,\"25prctile\":0,\"75prctile\":359,\"patients\": [{\"idcod\":17104,\"duration\":359},{\"idcod\":20410,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17104,\"duration\":0},{\"idcod\":20410,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Neuropathy_Retinopathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21029,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21029,\"duration\":0}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21029,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":4670,\"duration\":0},{\"idcod\":9219,\"duration\":0},{\"idcod\":11368,\"duration\":0},{\"idcod\":13180,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":1055,\"25prctile\":491,\"75prctile\":1471,\"patients\": [{\"idcod\":4670,\"duration\":868},{\"idcod\":9219,\"duration\":113},{\"idcod\":11368,\"duration\":1242},{\"idcod\":13180,\"duration\":1700}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":4670,\"duration\":0},{\"idcod\":9219,\"duration\":0},{\"idcod\":11368,\"duration\":0},{\"idcod\":13180,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11600,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":548,\"25prctile\":548,\"75prctile\":548,\"patients\": [{\"idcod\":11600,\"duration\":548}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11600,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":731,\"25prctile\":731,\"75prctile\":731,\"patients\": [{\"idcod\":11600,\"duration\":731}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11600,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Retinopathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":18971,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":716,\"25prctile\":151,\"75prctile\":1471,\"patients\": [{\"idcod\":18971,\"duration\":1700}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":18971,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Retinopathy_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16596,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":274,\"25prctile\":274,\"75prctile\":274,\"patients\": [{\"idcod\":16596,\"duration\":274}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16596,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":792,\"25prctile\":792,\"75prctile\":792,\"patients\": [{\"idcod\":16596,\"duration\":792}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16596,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":327,\"25prctile\":327,\"75prctile\":327,\"patients\": [{\"idcod\":16596,\"duration\":327}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16596,\"duration\":0}]}]},{\"label\":\"story_Neuropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":15, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":3992,\"duration\":0},{\"idcod\":7631,\"duration\":0},{\"idcod\":8054,\"duration\":0},{\"idcod\":14387,\"duration\":0},{\"idcod\":14718,\"duration\":0},{\"idcod\":16249,\"duration\":0},{\"idcod\":16264,\"duration\":0},{\"idcod\":16350,\"duration\":0},{\"idcod\":17531,\"duration\":0},{\"idcod\":18378,\"duration\":0},{\"idcod\":18772,\"duration\":0},{\"idcod\":18943,\"duration\":0},{\"idcod\":19603,\"duration\":0},{\"idcod\":20801,\"duration\":0},{\"idcod\":21598,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5652,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":566,\"25prctile\":566,\"75prctile\":566,\"patients\": [{\"idcod\":5652,\"duration\":566}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5652,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Chronicischemicheartdisease_Peripheralvasculardisease_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16457,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2517,\"25prctile\":2517,\"75prctile\":2517,\"patients\": [{\"idcod\":16457,\"duration\":2517}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16457,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":179,\"25prctile\":179,\"75prctile\":179,\"patients\": [{\"idcod\":16457,\"duration\":179}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16457,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":71,\"25prctile\":71,\"75prctile\":71,\"patients\": [{\"idcod\":16457,\"duration\":71}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16457,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_FatLiverDisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14876,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":10,\"25prctile\":10,\"75prctile\":10,\"patients\": [{\"idcod\":14876,\"duration\":10}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14876,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2532,\"25prctile\":2532,\"75prctile\":2532,\"patients\": [{\"idcod\":14876,\"duration\":2532}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14876,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5781,\"duration\":0},{\"idcod\":11615,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":482,\"25prctile\":427,\"75prctile\":537,\"patients\": [{\"idcod\":5781,\"duration\":427},{\"idcod\":11615,\"duration\":537}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5781,\"duration\":0},{\"idcod\":11615,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1365,\"25prctile\":0,\"75prctile\":2730,\"patients\": [{\"idcod\":5781,\"duration\":2730},{\"idcod\":11615,\"duration\":0}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5781,\"duration\":0},{\"idcod\":11615,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":12949,\"duration\":0},{\"idcod\":15082,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1284,\"25prctile\":1262,\"75prctile\":1305,\"patients\": [{\"idcod\":12949,\"duration\":1262},{\"idcod\":15082,\"duration\":1305}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":12949,\"duration\":0},{\"idcod\":15082,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Nephropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3354,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1392,\"25prctile\":1392,\"75prctile\":1392,\"patients\": [{\"idcod\":3354,\"duration\":1392}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3354,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":523,\"25prctile\":523,\"75prctile\":523,\"patients\": [{\"idcod\":3354,\"duration\":523}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3354,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":1242,\"duration\":0},{\"idcod\":3960,\"duration\":0},{\"idcod\":7202,\"duration\":0},{\"idcod\":8305,\"duration\":0},{\"idcod\":11169,\"duration\":0},{\"idcod\":14321,\"duration\":0},{\"idcod\":14499,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":7, \"time\":1100,\"25prctile\":421,\"75prctile\":1901,\"patients\": [{\"idcod\":1242,\"duration\":517},{\"idcod\":3960,\"duration\":1489},{\"idcod\":7202,\"duration\":28},{\"idcod\":8305,\"duration\":2449},{\"idcod\":11169,\"duration\":1100},{\"idcod\":14321,\"duration\":389},{\"idcod\":14499,\"duration\":2038}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":1242,\"duration\":0},{\"idcod\":3960,\"duration\":0},{\"idcod\":7202,\"duration\":0},{\"idcod\":8305,\"duration\":0},{\"idcod\":11169,\"duration\":0},{\"idcod\":14321,\"duration\":0},{\"idcod\":14499,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery_Angina\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11207,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2198,\"25prctile\":2198,\"75prctile\":2198,\"patients\": [{\"idcod\":11207,\"duration\":2198}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11207,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1974,\"25prctile\":1974,\"75prctile\":1974,\"patients\": [{\"idcod\":11207,\"duration\":1974}]},{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11207,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17314,\"duration\":0},{\"idcod\":21014,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":468,\"25prctile\":61,\"75prctile\":874,\"patients\": [{\"idcod\":17314,\"duration\":61},{\"idcod\":21014,\"duration\":874}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17314,\"duration\":0},{\"idcod\":21014,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":538,\"25prctile\":478,\"75prctile\":597,\"patients\": [{\"idcod\":17314,\"duration\":478},{\"idcod\":21014,\"duration\":597}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17314,\"duration\":0},{\"idcod\":21014,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery_Retinopathy_DiabeticFoot\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19055,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4,\"25prctile\":4,\"75prctile\":4,\"patients\": [{\"idcod\":19055,\"duration\":4}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19055,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1,\"25prctile\":1,\"75prctile\":1,\"patients\": [{\"idcod\":19055,\"duration\":1}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19055,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":812,\"25prctile\":812,\"75prctile\":812,\"patients\": [{\"idcod\":19055,\"duration\":812}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19055,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery_Retinopathy_FatLiverDisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10874,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4086,\"25prctile\":4086,\"75prctile\":4086,\"patients\": [{\"idcod\":10874,\"duration\":4086}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10874,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":21,\"25prctile\":21,\"75prctile\":21,\"patients\": [{\"idcod\":10874,\"duration\":21}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10874,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":48,\"25prctile\":48,\"75prctile\":48,\"patients\": [{\"idcod\":10874,\"duration\":48}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10874,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":20727,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1100,\"25prctile\":183,\"75prctile\":1901,\"patients\": [{\"idcod\":20727,\"duration\":2038}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":20727,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_FatLiverDisease_Nephropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17091,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":9,\"25prctile\":9,\"75prctile\":9,\"patients\": [{\"idcod\":17091,\"duration\":9}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17091,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":9,\"25prctile\":9,\"75prctile\":9,\"patients\": [{\"idcod\":17091,\"duration\":9}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17091,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":704,\"25prctile\":704,\"75prctile\":704,\"patients\": [{\"idcod\":17091,\"duration\":704}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17091,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1095,\"25prctile\":1095,\"75prctile\":1095,\"patients\": [{\"idcod\":17091,\"duration\":1095}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17091,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4890,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2421,\"25prctile\":2421,\"75prctile\":2421,\"patients\": [{\"idcod\":4890,\"duration\":2421}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4890,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1004,\"25prctile\":1004,\"75prctile\":1004,\"patients\": [{\"idcod\":4890,\"duration\":1004}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4890,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20804,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":365,\"25prctile\":365,\"75prctile\":365,\"patients\": [{\"idcod\":20804,\"duration\":365}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20804,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20804,\"duration\":0}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20804,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":929,\"25prctile\":929,\"75prctile\":929,\"patients\": [{\"idcod\":20804,\"duration\":929}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20804,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Retinopathy_Nephropathy_DiabeticFoot\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7304,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":7,\"25prctile\":7,\"75prctile\":7,\"patients\": [{\"idcod\":7304,\"duration\":7}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7304,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7304,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1008,\"25prctile\":1008,\"75prctile\":1008,\"patients\": [{\"idcod\":7304,\"duration\":1008}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7304,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":518,\"25prctile\":518,\"75prctile\":518,\"patients\": [{\"idcod\":7304,\"duration\":518}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7304,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4007,\"25prctile\":4007,\"75prctile\":4007,\"patients\": [{\"idcod\":7304,\"duration\":4007}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7304,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":21183,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1100,\"25prctile\":376,\"75prctile\":1901,\"patients\": [{\"idcod\":21183,\"duration\":2038}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":21183,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Nephropathy_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4,\"25prctile\":4,\"75prctile\":4,\"patients\": [{\"idcod\":10045,\"duration\":4}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":966,\"25prctile\":966,\"75prctile\":966,\"patients\": [{\"idcod\":10045,\"duration\":966}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1795,\"25prctile\":1795,\"75prctile\":1795,\"patients\": [{\"idcod\":10045,\"duration\":1795}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":172,\"25prctile\":172,\"75prctile\":172,\"patients\": [{\"idcod\":10045,\"duration\":172}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2501,\"25prctile\":2501,\"75prctile\":2501,\"patients\": [{\"idcod\":12315,\"duration\":2501}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":542,\"25prctile\":542,\"75prctile\":542,\"patients\": [{\"idcod\":12315,\"duration\":542}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1244,\"25prctile\":1244,\"75prctile\":1244,\"patients\": [{\"idcod\":12315,\"duration\":1244}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19227,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":667,\"25prctile\":667,\"75prctile\":667,\"patients\": [{\"idcod\":19227,\"duration\":667}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19227,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":555,\"25prctile\":555,\"75prctile\":555,\"patients\": [{\"idcod\":19227,\"duration\":555}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19227,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":48, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":80,\"duration\":0},{\"idcod\":124,\"duration\":0},{\"idcod\":1643,\"duration\":0},{\"idcod\":3809,\"duration\":0},{\"idcod\":5692,\"duration\":0},{\"idcod\":5928,\"duration\":0},{\"idcod\":6071,\"duration\":0},{\"idcod\":6424,\"duration\":0},{\"idcod\":7151,\"duration\":0},{\"idcod\":8479,\"duration\":0},{\"idcod\":8524,\"duration\":0},{\"idcod\":9064,\"duration\":0},{\"idcod\":9276,\"duration\":0},{\"idcod\":9370,\"duration\":0},{\"idcod\":9972,\"duration\":0},{\"idcod\":10165,\"duration\":0},{\"idcod\":10180,\"duration\":0},{\"idcod\":10266,\"duration\":0},{\"idcod\":10275,\"duration\":0},{\"idcod\":10314,\"duration\":0},{\"idcod\":10342,\"duration\":0},{\"idcod\":11160,\"duration\":0},{\"idcod\":11174,\"duration\":0},{\"idcod\":12895,\"duration\":0},{\"idcod\":13239,\"duration\":0},{\"idcod\":13825,\"duration\":0},{\"idcod\":13897,\"duration\":0},{\"idcod\":14146,\"duration\":0},{\"idcod\":14726,\"duration\":0},{\"idcod\":15360,\"duration\":0},{\"idcod\":16245,\"duration\":0},{\"idcod\":17281,\"duration\":0},{\"idcod\":17535,\"duration\":0},{\"idcod\":17844,\"duration\":0},{\"idcod\":17881,\"duration\":0},{\"idcod\":18353,\"duration\":0},{\"idcod\":18549,\"duration\":0},{\"idcod\":18774,\"duration\":0},{\"idcod\":19169,\"duration\":0},{\"idcod\":19296,\"duration\":0},{\"idcod\":20280,\"duration\":0},{\"idcod\":20625,\"duration\":0},{\"idcod\":20935,\"duration\":0},{\"idcod\":21101,\"duration\":0},{\"idcod\":21270,\"duration\":0},{\"idcod\":21544,\"duration\":0},{\"idcod\":21578,\"duration\":0},{\"idcod\":21583,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Angina\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6643,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2015,\"25prctile\":2015,\"75prctile\":2015,\"patients\": [{\"idcod\":6643,\"duration\":2015}]},{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6643,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":5820,\"duration\":0},{\"idcod\":12472,\"duration\":0},{\"idcod\":19405,\"duration\":0},{\"idcod\":21297,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":982,\"25prctile\":840,\"75prctile\":2786,\"patients\": [{\"idcod\":5820,\"duration\":991},{\"idcod\":12472,\"duration\":4580},{\"idcod\":19405,\"duration\":973},{\"idcod\":21297,\"duration\":707}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":5820,\"duration\":0},{\"idcod\":12472,\"duration\":0},{\"idcod\":19405,\"duration\":0},{\"idcod\":21297,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6774,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1148,\"25prctile\":1148,\"75prctile\":1148,\"patients\": [{\"idcod\":6774,\"duration\":1148}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6774,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2325,\"25prctile\":2325,\"75prctile\":2325,\"patients\": [{\"idcod\":6774,\"duration\":2325}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6774,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9464,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":840,\"25prctile\":424,\"75prctile\":2777,\"patients\": [{\"idcod\":9464,\"duration\":707}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9464,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9249,\"duration\":0},{\"idcod\":18609,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":945,\"25prctile\":812,\"75prctile\":1060,\"patients\": [{\"idcod\":9249,\"duration\":916},{\"idcod\":18609,\"duration\":707}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9249,\"duration\":0},{\"idcod\":18609,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Nephropathy_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21189,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":476,\"25prctile\":476,\"75prctile\":476,\"patients\": [{\"idcod\":21189,\"duration\":476}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21189,\"duration\":0}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21189,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Neuropathy\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":10055,\"duration\":0},{\"idcod\":11350,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":738,\"25prctile\":356,\"75prctile\":871,\"patients\": [{\"idcod\":10055,\"duration\":5},{\"idcod\":11350,\"duration\":707}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":10055,\"duration\":0},{\"idcod\":11350,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9036,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":759,\"25prctile\":759,\"75prctile\":759,\"patients\": [{\"idcod\":9036,\"duration\":759}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9036,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1313,\"25prctile\":1313,\"75prctile\":1313,\"patients\": [{\"idcod\":9036,\"duration\":1313}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9036,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":4026,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":871,\"25prctile\":738,\"75prctile\":1930,\"patients\": [{\"idcod\":4026,\"duration\":707}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":4026,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3324,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":933,\"25prctile\":933,\"75prctile\":933,\"patients\": [{\"idcod\":3324,\"duration\":933}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3324,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":255,\"25prctile\":255,\"75prctile\":255,\"patients\": [{\"idcod\":3324,\"duration\":255}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3324,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_Chronicischemicheartdisease_DiabeticFoot\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1185,\"25prctile\":1185,\"75prctile\":1185,\"patients\": [{\"idcod\":5453,\"duration\":1185}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2595,\"25prctile\":2595,\"75prctile\":2595,\"patients\": [{\"idcod\":5453,\"duration\":2595}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_DiabeticFoot\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20961,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1491,\"25prctile\":1491,\"75prctile\":1491,\"patients\": [{\"idcod\":20961,\"duration\":1491}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20961,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":936,\"25prctile\":936,\"75prctile\":936,\"patients\": [{\"idcod\":20961,\"duration\":936}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20961,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_Neuropathy\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14844,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5422,\"25prctile\":5422,\"75prctile\":5422,\"patients\": [{\"idcod\":14844,\"duration\":5422}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14844,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":290,\"25prctile\":290,\"75prctile\":290,\"patients\": [{\"idcod\":14844,\"duration\":290}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14844,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Retinopathy\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":20176,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":738,\"25prctile\":447,\"75prctile\":871,\"patients\": [{\"idcod\":20176,\"duration\":707}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":20176,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":2457,\"duration\":0},{\"idcod\":17468,\"duration\":0},{\"idcod\":20656,\"duration\":0},{\"idcod\":21318,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14676,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2427,\"25prctile\":2427,\"75prctile\":2427,\"patients\": [{\"idcod\":14676,\"duration\":2427}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":14676,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16500,\"duration\":0}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16500,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16500,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_DiabeticFoot\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21556,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":330,\"25prctile\":330,\"75prctile\":330,\"patients\": [{\"idcod\":21556,\"duration\":330}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21556,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_FatLiverDisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":16589,\"duration\":0},{\"idcod\":20144,\"duration\":0},{\"idcod\":20518,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":119,\"25prctile\":43,\"75prctile\":777,\"patients\": [{\"idcod\":16589,\"duration\":18},{\"idcod\":20144,\"duration\":119},{\"idcod\":20518,\"duration\":996}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":16589,\"duration\":0},{\"idcod\":20144,\"duration\":0},{\"idcod\":20518,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":10338,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":885,\"25prctile\":311,\"75prctile\":968,\"patients\": [{\"idcod\":10338,\"duration\":996}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":10338,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":714,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":30,\"25prctile\":30,\"75prctile\":30,\"patients\": [{\"idcod\":714,\"duration\":30}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":714,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2540,\"25prctile\":2540,\"75prctile\":2540,\"patients\": [{\"idcod\":714,\"duration\":2540}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":714,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11057,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":631,\"25prctile\":631,\"75prctile\":631,\"patients\": [{\"idcod\":11057,\"duration\":631}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11057,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":409,\"25prctile\":409,\"75prctile\":409,\"patients\": [{\"idcod\":11057,\"duration\":409}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11057,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12117,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":48,\"25prctile\":48,\"75prctile\":48,\"patients\": [{\"idcod\":12117,\"duration\":48}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12117,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":7,\"25prctile\":7,\"75prctile\":7,\"patients\": [{\"idcod\":12117,\"duration\":7}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12117,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":27,\"25prctile\":27,\"75prctile\":27,\"patients\": [{\"idcod\":12117,\"duration\":27}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12117,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":13483,\"duration\":0},{\"idcod\":18432,\"duration\":0},{\"idcod\":20584,\"duration\":0},{\"idcod\":20949,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":149,\"25prctile\":0,\"75prctile\":1932,\"patients\": [{\"idcod\":13483,\"duration\":3566},{\"idcod\":18432,\"duration\":0},{\"idcod\":20584,\"duration\":297},{\"idcod\":20949,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":13483,\"duration\":0},{\"idcod\":18432,\"duration\":0},{\"idcod\":20584,\"duration\":0},{\"idcod\":20949,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12111,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1747,\"25prctile\":1747,\"75prctile\":1747,\"patients\": [{\"idcod\":12111,\"duration\":1747}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12111,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1535,\"25prctile\":1535,\"75prctile\":1535,\"patients\": [{\"idcod\":12111,\"duration\":1535}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12111,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Nephropathy_Neuropathy\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15991,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15991,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":948,\"25prctile\":948,\"75prctile\":948,\"patients\": [{\"idcod\":15991,\"duration\":948}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15991,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":11,\"25prctile\":11,\"75prctile\":11,\"patients\": [{\"idcod\":15991,\"duration\":11}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15991,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Nephropathy_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":196,\"25prctile\":196,\"75prctile\":196,\"patients\": [{\"idcod\":10222,\"duration\":196}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":154,\"25prctile\":154,\"75prctile\":154,\"patients\": [{\"idcod\":10222,\"duration\":154}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":709,\"25prctile\":709,\"75prctile\":709,\"patients\": [{\"idcod\":10222,\"duration\":709}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1348,\"25prctile\":1348,\"75prctile\":1348,\"patients\": [{\"idcod\":10222,\"duration\":1348}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Neuropathy_DiabeticFoot\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5062,\"25prctile\":5062,\"75prctile\":5062,\"patients\": [{\"idcod\":5979,\"duration\":5062}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Retinopathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18942,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18942,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":140,\"25prctile\":140,\"75prctile\":140,\"patients\": [{\"idcod\":18942,\"duration\":140}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18942,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1603,\"25prctile\":1603,\"75prctile\":1603,\"patients\": [{\"idcod\":18942,\"duration\":1603}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18942,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Retinopathy_Chronicischemicheartdisease_DiabeticFoot\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8520,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":68,\"25prctile\":68,\"75prctile\":68,\"patients\": [{\"idcod\":8520,\"duration\":68}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8520,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":147,\"25prctile\":147,\"75prctile\":147,\"patients\": [{\"idcod\":8520,\"duration\":147}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8520,\"duration\":0}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8520,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Retinopathy_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Retinopathy_Occlusionandstenosisofcarotidartery_Nephropathy_DiabeticFoot\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":13626,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2009,\"25prctile\":2009,\"75prctile\":2009,\"patients\": [{\"idcod\":13626,\"duration\":2009}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":13626,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2972,\"25prctile\":2972,\"75prctile\":2972,\"patients\": [{\"idcod\":13626,\"duration\":2972}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":13626,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":132,\"25prctile\":132,\"75prctile\":132,\"patients\": [{\"idcod\":13626,\"duration\":132}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":13626,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1352,\"25prctile\":1352,\"75prctile\":1352,\"patients\": [{\"idcod\":13626,\"duration\":1352}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":13626,\"duration\":0}]}]},{\"label\":\"story_Retinopathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":10, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":845,\"duration\":0},{\"idcod\":1803,\"duration\":0},{\"idcod\":4385,\"duration\":0},{\"idcod\":11823,\"duration\":0},{\"idcod\":14871,\"duration\":0},{\"idcod\":15605,\"duration\":0},{\"idcod\":15800,\"duration\":0},{\"idcod\":19842,\"duration\":0},{\"idcod\":20185,\"duration\":0},{\"idcod\":21045,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17247,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1676,\"25prctile\":1676,\"75prctile\":1676,\"patients\": [{\"idcod\":17247,\"duration\":1676}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17247,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Chronicischemicheartdisease_FatLiverDisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11624,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4386,\"25prctile\":4386,\"75prctile\":4386,\"patients\": [{\"idcod\":11624,\"duration\":4386}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11624,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":332,\"25prctile\":332,\"75prctile\":332,\"patients\": [{\"idcod\":11624,\"duration\":332}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11624,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_FatLiverDisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11905,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":756,\"25prctile\":756,\"75prctile\":756,\"patients\": [{\"idcod\":11905,\"duration\":756}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11905,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12974,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":17,\"25prctile\":17,\"75prctile\":17,\"patients\": [{\"idcod\":12974,\"duration\":17}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12974,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":80,\"25prctile\":80,\"75prctile\":80,\"patients\": [{\"idcod\":12974,\"duration\":80}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12974,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_FatLiverDisease_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":885,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1141,\"25prctile\":1141,\"75prctile\":1141,\"patients\": [{\"idcod\":885,\"duration\":1141}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":885,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1263,\"25prctile\":1263,\"75prctile\":1263,\"patients\": [{\"idcod\":885,\"duration\":1263}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":885,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":701,\"25prctile\":701,\"75prctile\":701,\"patients\": [{\"idcod\":885,\"duration\":701}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":885,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":334,\"25prctile\":334,\"75prctile\":334,\"patients\": [{\"idcod\":885,\"duration\":334}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":885,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":18342,\"duration\":0},{\"idcod\":21594,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":846,\"25prctile\":230,\"75prctile\":1461,\"patients\": [{\"idcod\":18342,\"duration\":230},{\"idcod\":21594,\"duration\":1461}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":18342,\"duration\":0},{\"idcod\":21594,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Nephropathy_Neuropathy_DiabeticFoot\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19698,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":963,\"25prctile\":963,\"75prctile\":963,\"patients\": [{\"idcod\":19698,\"duration\":963}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19698,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19698,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":293,\"25prctile\":293,\"75prctile\":293,\"patients\": [{\"idcod\":19698,\"duration\":293}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19698,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Neuropathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17734,\"duration\":0},{\"idcod\":20234,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":14,\"25prctile\":13,\"75prctile\":14,\"patients\": [{\"idcod\":17734,\"duration\":14},{\"idcod\":20234,\"duration\":13}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17734,\"duration\":0},{\"idcod\":20234,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":545,\"25prctile\":194,\"75prctile\":896,\"patients\": [{\"idcod\":17734,\"duration\":896},{\"idcod\":20234,\"duration\":194}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17734,\"duration\":0},{\"idcod\":20234,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8346,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":12,\"25prctile\":11,\"75prctile\":13,\"patients\": [{\"idcod\":8346,\"duration\":13}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8346,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":498,\"25prctile\":194,\"75prctile\":802,\"patients\": [{\"idcod\":8346,\"duration\":194}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8346,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20162,\"duration\":0},{\"idcod\":20451,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":559,\"25prctile\":218,\"75prctile\":899,\"patients\": [{\"idcod\":20162,\"duration\":899},{\"idcod\":20451,\"duration\":218}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20162,\"duration\":0},{\"idcod\":20451,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":15676,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":117,\"25prctile\":16,\"75prctile\":218,\"patients\": [{\"idcod\":15676,\"duration\":218}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":15676,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Peripheralvasculardisease_Chronicischemicheartdisease_FatLiverDisease_Nephropathy_Neuropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17978,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":10,\"25prctile\":10,\"75prctile\":10,\"patients\": [{\"idcod\":17978,\"duration\":10}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17978,\"duration\":0}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17978,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":319,\"25prctile\":319,\"75prctile\":319,\"patients\": [{\"idcod\":17978,\"duration\":319}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17978,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":123,\"25prctile\":123,\"75prctile\":123,\"patients\": [{\"idcod\":17978,\"duration\":123}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17978,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":903,\"25prctile\":903,\"75prctile\":903,\"patients\": [{\"idcod\":17978,\"duration\":903}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17978,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6814,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1827,\"25prctile\":1827,\"75prctile\":1827,\"patients\": [{\"idcod\":6814,\"duration\":1827}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6814,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2250,\"25prctile\":2250,\"75prctile\":2250,\"patients\": [{\"idcod\":6814,\"duration\":2250}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6814,\"duration\":0}]}]},{\"label\":\"story_Stroke\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":10963,\"duration\":0},{\"idcod\":18542,\"duration\":0}]}]},{\"label\":\"story_Stroke_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6847,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":707,\"25prctile\":707,\"75prctile\":707,\"patients\": [{\"idcod\":6847,\"duration\":707}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6847,\"duration\":0}]}]},{\"label\":\"story_Stroke_FatLiverDisease_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11887,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1176,\"25prctile\":1176,\"75prctile\":1176,\"patients\": [{\"idcod\":11887,\"duration\":1176}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11887,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":562,\"25prctile\":562,\"75prctile\":562,\"patients\": [{\"idcod\":11887,\"duration\":562}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11887,\"duration\":0}]}]},{\"label\":\"story_Stroke_Retinopathy_Neuropathy_Nephropathy_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":65,\"25prctile\":65,\"75prctile\":65,\"patients\": [{\"idcod\":11796,\"duration\":65}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2,\"25prctile\":2,\"75prctile\":2,\"patients\": [{\"idcod\":11796,\"duration\":2}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":7,\"25prctile\":7,\"75prctile\":7,\"patients\": [{\"idcod\":11796,\"duration\":7}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":942,\"25prctile\":942,\"75prctile\":942,\"patients\": [{\"idcod\":11796,\"duration\":942}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":570,\"25prctile\":570,\"75prctile\":570,\"patients\": [{\"idcod\":11796,\"duration\":570}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]}]}]}";
			//			String jsonIn1 = "{\"histories\":[{\"label\":\"story_Angina\",\"steps\": [{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":18436,\"duration\":0},{\"idcod\":19676,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":27, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.771213,\"patients\": [{\"idcod\":1091,\"duration\":0},{\"idcod\":1195,\"duration\":0},{\"idcod\":2483,\"duration\":0},{\"idcod\":4238,\"duration\":0},{\"idcod\":4447,\"duration\":0},{\"idcod\":7526,\"duration\":0},{\"idcod\":8082,\"duration\":0},{\"idcod\":8271,\"duration\":0},{\"idcod\":9318,\"duration\":0},{\"idcod\":11000,\"duration\":0},{\"idcod\":13379,\"duration\":0},{\"idcod\":14564,\"duration\":0},{\"idcod\":14740,\"duration\":0},{\"idcod\":15703,\"duration\":0},{\"idcod\":16897,\"duration\":0},{\"idcod\":17288,\"duration\":0},{\"idcod\":18107,\"duration\":0},{\"idcod\":18289,\"duration\":0},{\"idcod\":18443,\"duration\":0},{\"idcod\":19086,\"duration\":0},{\"idcod\":20432,\"duration\":0},{\"idcod\":20547,\"duration\":0},{\"idcod\":20781,\"duration\":0},{\"idcod\":21078,\"duration\":0},{\"idcod\":21263,\"duration\":0},{\"idcod\":21592,\"duration\":0},{\"idcod\":21599,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":221,\"duration\":0},{\"idcod\":9100,\"duration\":0},{\"idcod\":19014,\"duration\":0},{\"idcod\":20946,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":1569,\"25prctile\":605,\"75prctile\":2716,\"patients\": [{\"idcod\":221,\"duration\":3203},{\"idcod\":9100,\"duration\":2229},{\"idcod\":19014,\"duration\":909},{\"idcod\":20946,\"duration\":300}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":221,\"duration\":0},{\"idcod\":9100,\"duration\":0},{\"idcod\":19014,\"duration\":0},{\"idcod\":20946,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_FatLiverDisease_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11464,\"duration\":0},{\"idcod\":15580,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":4788,\"25prctile\":3026,\"75prctile\":6549,\"patients\": [{\"idcod\":11464,\"duration\":3026},{\"idcod\":15580,\"duration\":6549}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11464,\"duration\":0},{\"idcod\":15580,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":920,\"25prctile\":474,\"75prctile\":1365,\"patients\": [{\"idcod\":11464,\"duration\":1365},{\"idcod\":15580,\"duration\":474}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":11464,\"duration\":0},{\"idcod\":15580,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":20042,\"duration\":0},{\"idcod\":20918,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":605,\"25prctile\":209,\"75prctile\":4083,\"patients\": [{\"idcod\":20042,\"duration\":7257},{\"idcod\":20918,\"duration\":300}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":20042,\"duration\":0},{\"idcod\":20918,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Neuropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9690,\"duration\":0},{\"idcod\":21133,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1166,\"25prctile\":605,\"75prctile\":1683,\"patients\": [{\"idcod\":9690,\"duration\":1944},{\"idcod\":21133,\"duration\":300}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9690,\"duration\":0},{\"idcod\":21133,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":20, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":1223,\"duration\":0},{\"idcod\":5581,\"duration\":0},{\"idcod\":6422,\"duration\":0},{\"idcod\":7174,\"duration\":0},{\"idcod\":7433,\"duration\":0},{\"idcod\":7665,\"duration\":0},{\"idcod\":9863,\"duration\":0},{\"idcod\":13252,\"duration\":0},{\"idcod\":16415,\"duration\":0},{\"idcod\":16786,\"duration\":0},{\"idcod\":18421,\"duration\":0},{\"idcod\":18580,\"duration\":0},{\"idcod\":19228,\"duration\":0},{\"idcod\":19446,\"duration\":0},{\"idcod\":19989,\"duration\":0},{\"idcod\":19993,\"duration\":0},{\"idcod\":20036,\"duration\":0},{\"idcod\":20151,\"duration\":0},{\"idcod\":20361,\"duration\":0},{\"idcod\":20388,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":20, \"time\":1959,\"25prctile\":317,\"75prctile\":4622,\"patients\": [{\"idcod\":1223,\"duration\":8719},{\"idcod\":5581,\"duration\":59},{\"idcod\":6422,\"duration\":4465},{\"idcod\":7174,\"duration\":337},{\"idcod\":7433,\"duration\":775},{\"idcod\":7665,\"duration\":8056},{\"idcod\":9863,\"duration\":5563},{\"idcod\":13252,\"duration\":427},{\"idcod\":16415,\"duration\":2466},{\"idcod\":16786,\"duration\":83},{\"idcod\":18421,\"duration\":3592},{\"idcod\":18580,\"duration\":2146},{\"idcod\":19228,\"duration\":2438},{\"idcod\":19446,\"duration\":8646},{\"idcod\":19989,\"duration\":712},{\"idcod\":19993,\"duration\":296},{\"idcod\":20036,\"duration\":1772},{\"idcod\":20151,\"duration\":31},{\"idcod\":20361,\"duration\":104},{\"idcod\":20388,\"duration\":4778}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":20, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":1223,\"duration\":0},{\"idcod\":5581,\"duration\":0},{\"idcod\":6422,\"duration\":0},{\"idcod\":7174,\"duration\":0},{\"idcod\":7433,\"duration\":0},{\"idcod\":7665,\"duration\":0},{\"idcod\":9863,\"duration\":0},{\"idcod\":13252,\"duration\":0},{\"idcod\":16415,\"duration\":0},{\"idcod\":16786,\"duration\":0},{\"idcod\":18421,\"duration\":0},{\"idcod\":18580,\"duration\":0},{\"idcod\":19228,\"duration\":0},{\"idcod\":19446,\"duration\":0},{\"idcod\":19989,\"duration\":0},{\"idcod\":19993,\"duration\":0},{\"idcod\":20036,\"duration\":0},{\"idcod\":20151,\"duration\":0},{\"idcod\":20361,\"duration\":0},{\"idcod\":20388,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8042,\"duration\":0},{\"idcod\":20558,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":2482,\"25prctile\":29,\"75prctile\":4934,\"patients\": [{\"idcod\":8042,\"duration\":4934},{\"idcod\":20558,\"duration\":29}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8042,\"duration\":0},{\"idcod\":20558,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1167,\"25prctile\":743,\"75prctile\":1591,\"patients\": [{\"idcod\":8042,\"duration\":1591},{\"idcod\":20558,\"duration\":743}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":8042,\"duration\":0},{\"idcod\":20558,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":7541,\"duration\":0},{\"idcod\":8868,\"duration\":0},{\"idcod\":15051,\"duration\":0},{\"idcod\":15157,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":7039,\"25prctile\":4931,\"75prctile\":8865,\"patients\": [{\"idcod\":7541,\"duration\":7868},{\"idcod\":8868,\"duration\":9862},{\"idcod\":15051,\"duration\":6209},{\"idcod\":15157,\"duration\":3653}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":7541,\"duration\":0},{\"idcod\":8868,\"duration\":0},{\"idcod\":15051,\"duration\":0},{\"idcod\":15157,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":2365,\"25prctile\":1690,\"75prctile\":4264,\"patients\": [{\"idcod\":7541,\"duration\":1056},{\"idcod\":8868,\"duration\":2405},{\"idcod\":15051,\"duration\":6123},{\"idcod\":15157,\"duration\":2324}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":7541,\"duration\":0},{\"idcod\":8868,\"duration\":0},{\"idcod\":15051,\"duration\":0},{\"idcod\":15157,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":14304,\"duration\":0},{\"idcod\":16864,\"duration\":0},{\"idcod\":19415,\"duration\":0},{\"idcod\":19940,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":1959,\"25prctile\":200,\"75prctile\":3783,\"patients\": [{\"idcod\":14304,\"duration\":3974},{\"idcod\":16864,\"duration\":3407},{\"idcod\":19415,\"duration\":38},{\"idcod\":19940,\"duration\":4778}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":14304,\"duration\":0},{\"idcod\":16864,\"duration\":0},{\"idcod\":19415,\"duration\":0},{\"idcod\":19940,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Retinopathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":5433,\"duration\":0},{\"idcod\":8440,\"duration\":0},{\"idcod\":13439,\"duration\":0},{\"idcod\":15565,\"duration\":0},{\"idcod\":19717,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":5, \"time\":1754,\"25prctile\":362,\"75prctile\":4185,\"patients\": [{\"idcod\":5433,\"duration\":1735},{\"idcod\":8440,\"duration\":511},{\"idcod\":13439,\"duration\":7242},{\"idcod\":15565,\"duration\":753},{\"idcod\":19717,\"duration\":4778}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.336767,\"patients\": [{\"idcod\":5433,\"duration\":0},{\"idcod\":8440,\"duration\":0},{\"idcod\":13439,\"duration\":0},{\"idcod\":15565,\"duration\":0},{\"idcod\":19717,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5647,\"duration\":0},{\"idcod\":19402,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1032,\"25prctile\":21,\"75prctile\":2043,\"patients\": [{\"idcod\":5647,\"duration\":21},{\"idcod\":19402,\"duration\":2043}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5647,\"duration\":0},{\"idcod\":19402,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":2125,\"25prctile\":147,\"75prctile\":4103,\"patients\": [{\"idcod\":5647,\"duration\":4103},{\"idcod\":19402,\"duration\":147}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5647,\"duration\":0},{\"idcod\":19402,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.771213,\"patients\": [{\"idcod\":18183,\"duration\":0},{\"idcod\":21582,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot_Neuropathy\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6142,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3307,\"25prctile\":3307,\"75prctile\":3307,\"patients\": [{\"idcod\":6142,\"duration\":3307}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6142,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20139,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":850,\"25prctile\":850,\"75prctile\":850,\"patients\": [{\"idcod\":20139,\"duration\":850}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20139,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1210,\"25prctile\":1210,\"75prctile\":1210,\"patients\": [{\"idcod\":20139,\"duration\":1210}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20139,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":54, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":3579,\"duration\":0},{\"idcod\":4499,\"duration\":0},{\"idcod\":4755,\"duration\":0},{\"idcod\":5061,\"duration\":0},{\"idcod\":5333,\"duration\":0},{\"idcod\":5497,\"duration\":0},{\"idcod\":5622,\"duration\":0},{\"idcod\":5743,\"duration\":0},{\"idcod\":5926,\"duration\":0},{\"idcod\":6068,\"duration\":0},{\"idcod\":6264,\"duration\":0},{\"idcod\":6448,\"duration\":0},{\"idcod\":6473,\"duration\":0},{\"idcod\":6857,\"duration\":0},{\"idcod\":6893,\"duration\":0},{\"idcod\":7231,\"duration\":0},{\"idcod\":7562,\"duration\":0},{\"idcod\":7676,\"duration\":0},{\"idcod\":8231,\"duration\":0},{\"idcod\":8283,\"duration\":0},{\"idcod\":9772,\"duration\":0},{\"idcod\":10573,\"duration\":0},{\"idcod\":11067,\"duration\":0},{\"idcod\":11375,\"duration\":0},{\"idcod\":11435,\"duration\":0},{\"idcod\":12550,\"duration\":0},{\"idcod\":13156,\"duration\":0},{\"idcod\":13365,\"duration\":0},{\"idcod\":14370,\"duration\":0},{\"idcod\":14641,\"duration\":0},{\"idcod\":15240,\"duration\":0},{\"idcod\":15413,\"duration\":0},{\"idcod\":15620,\"duration\":0},{\"idcod\":15839,\"duration\":0},{\"idcod\":17067,\"duration\":0},{\"idcod\":17207,\"duration\":0},{\"idcod\":17390,\"duration\":0},{\"idcod\":17724,\"duration\":0},{\"idcod\":18086,\"duration\":0},{\"idcod\":18278,\"duration\":0},{\"idcod\":18322,\"duration\":0},{\"idcod\":18397,\"duration\":0},{\"idcod\":19064,\"duration\":0},{\"idcod\":19085,\"duration\":0},{\"idcod\":19202,\"duration\":0},{\"idcod\":19342,\"duration\":0},{\"idcod\":19499,\"duration\":0},{\"idcod\":19538,\"duration\":0},{\"idcod\":19663,\"duration\":0},{\"idcod\":19886,\"duration\":0},{\"idcod\":20401,\"duration\":0},{\"idcod\":20491,\"duration\":0},{\"idcod\":20497,\"duration\":0},{\"idcod\":21316,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Nephropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":14, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":5422,\"duration\":0},{\"idcod\":5425,\"duration\":0},{\"idcod\":6511,\"duration\":0},{\"idcod\":6693,\"duration\":0},{\"idcod\":12994,\"duration\":0},{\"idcod\":15897,\"duration\":0},{\"idcod\":16495,\"duration\":0},{\"idcod\":16868,\"duration\":0},{\"idcod\":16993,\"duration\":0},{\"idcod\":17101,\"duration\":0},{\"idcod\":18054,\"duration\":0},{\"idcod\":18244,\"duration\":0},{\"idcod\":19073,\"duration\":0},{\"idcod\":20310,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":14, \"time\":771,\"25prctile\":368,\"75prctile\":1643,\"patients\": [{\"idcod\":5422,\"duration\":823},{\"idcod\":5425,\"duration\":368},{\"idcod\":6511,\"duration\":3866},{\"idcod\":6693,\"duration\":606},{\"idcod\":12994,\"duration\":718},{\"idcod\":15897,\"duration\":1643},{\"idcod\":16495,\"duration\":1161},{\"idcod\":16868,\"duration\":209},{\"idcod\":16993,\"duration\":899},{\"idcod\":17101,\"duration\":1841},{\"idcod\":18054,\"duration\":328},{\"idcod\":18244,\"duration\":2585},{\"idcod\":19073,\"duration\":687},{\"idcod\":20310,\"duration\":241}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":14, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":5422,\"duration\":0},{\"idcod\":5425,\"duration\":0},{\"idcod\":6511,\"duration\":0},{\"idcod\":6693,\"duration\":0},{\"idcod\":12994,\"duration\":0},{\"idcod\":15897,\"duration\":0},{\"idcod\":16495,\"duration\":0},{\"idcod\":16868,\"duration\":0},{\"idcod\":16993,\"duration\":0},{\"idcod\":17101,\"duration\":0},{\"idcod\":18054,\"duration\":0},{\"idcod\":18244,\"duration\":0},{\"idcod\":19073,\"duration\":0},{\"idcod\":20310,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":4025,\"duration\":0},{\"idcod\":9031,\"duration\":0},{\"idcod\":9647,\"duration\":0},{\"idcod\":14647,\"duration\":0},{\"idcod\":17888,\"duration\":0},{\"idcod\":19629,\"duration\":0},{\"idcod\":20340,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":7, \"time\":512,\"25prctile\":209,\"75prctile\":1841,\"patients\": [{\"idcod\":4025,\"duration\":2700},{\"idcod\":9031,\"duration\":336},{\"idcod\":9647,\"duration\":1904},{\"idcod\":14647,\"duration\":0},{\"idcod\":17888,\"duration\":0},{\"idcod\":19629,\"duration\":47},{\"idcod\":20340,\"duration\":241}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":4025,\"duration\":0},{\"idcod\":9031,\"duration\":0},{\"idcod\":9647,\"duration\":0},{\"idcod\":14647,\"duration\":0},{\"idcod\":17888,\"duration\":0},{\"idcod\":19629,\"duration\":0},{\"idcod\":20340,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":1079,\"duration\":0},{\"idcod\":8573,\"duration\":0},{\"idcod\":17654,\"duration\":0},{\"idcod\":18744,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":793,\"25prctile\":241,\"75prctile\":1841,\"patients\": [{\"idcod\":1079,\"duration\":4869},{\"idcod\":8573,\"duration\":409},{\"idcod\":17654,\"duration\":2218},{\"idcod\":18744,\"duration\":241}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":1079,\"duration\":0},{\"idcod\":8573,\"duration\":0},{\"idcod\":17654,\"duration\":0},{\"idcod\":18744,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":11758,\"duration\":0},{\"idcod\":15424,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":793,\"25prctile\":209,\"75prctile\":1725,\"patients\": [{\"idcod\":11758,\"duration\":185},{\"idcod\":15424,\"duration\":241}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":11758,\"duration\":0},{\"idcod\":15424,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19311,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":694,\"25prctile\":694,\"75prctile\":694,\"patients\": [{\"idcod\":19311,\"duration\":694}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19311,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":774,\"25prctile\":774,\"75prctile\":774,\"patients\": [{\"idcod\":19311,\"duration\":774}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19311,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":10615,\"duration\":0},{\"idcod\":17356,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":892,\"25prctile\":241,\"75prctile\":1841,\"patients\": [{\"idcod\":10615,\"duration\":2038},{\"idcod\":17356,\"duration\":241}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.820427,\"patients\": [{\"idcod\":10615,\"duration\":0},{\"idcod\":17356,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":780,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":650,\"25prctile\":650,\"75prctile\":650,\"patients\": [{\"idcod\":780,\"duration\":650}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":780,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2336,\"25prctile\":2336,\"75prctile\":2336,\"patients\": [{\"idcod\":780,\"duration\":2336}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":780,\"duration\":0}]}]},{\"label\":\"story_Nephropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":18, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":2386,\"duration\":0},{\"idcod\":5927,\"duration\":0},{\"idcod\":6086,\"duration\":0},{\"idcod\":6864,\"duration\":0},{\"idcod\":11037,\"duration\":0},{\"idcod\":12151,\"duration\":0},{\"idcod\":12592,\"duration\":0},{\"idcod\":14661,\"duration\":0},{\"idcod\":15533,\"duration\":0},{\"idcod\":15755,\"duration\":0},{\"idcod\":16222,\"duration\":0},{\"idcod\":17269,\"duration\":0},{\"idcod\":18271,\"duration\":0},{\"idcod\":18463,\"duration\":0},{\"idcod\":19043,\"duration\":0},{\"idcod\":19991,\"duration\":0},{\"idcod\":20743,\"duration\":0},{\"idcod\":20811,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":3633,\"duration\":0},{\"idcod\":16801,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":628,\"25prctile\":164,\"75prctile\":1091,\"patients\": [{\"idcod\":3633,\"duration\":164},{\"idcod\":16801,\"duration\":1091}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":3633,\"duration\":0},{\"idcod\":16801,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":14,\"25prctile\":14,\"75prctile\":14,\"patients\": [{\"idcod\":7397,\"duration\":14}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":445,\"25prctile\":445,\"75prctile\":445,\"patients\": [{\"idcod\":7397,\"duration\":445}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4720,\"25prctile\":4720,\"75prctile\":4720,\"patients\": [{\"idcod\":7397,\"duration\":4720}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7397,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Neuropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17104,\"duration\":0},{\"idcod\":20410,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":180,\"25prctile\":0,\"75prctile\":359,\"patients\": [{\"idcod\":17104,\"duration\":359},{\"idcod\":20410,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17104,\"duration\":0},{\"idcod\":20410,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Neuropathy_Retinopathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21029,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21029,\"duration\":0}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":21029,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":4670,\"duration\":0},{\"idcod\":9219,\"duration\":0},{\"idcod\":11368,\"duration\":0},{\"idcod\":13180,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":1055,\"25prctile\":491,\"75prctile\":1471,\"patients\": [{\"idcod\":4670,\"duration\":868},{\"idcod\":9219,\"duration\":113},{\"idcod\":11368,\"duration\":1242},{\"idcod\":13180,\"duration\":1700}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":4670,\"duration\":0},{\"idcod\":9219,\"duration\":0},{\"idcod\":11368,\"duration\":0},{\"idcod\":13180,\"duration\":0}]}]},{\"label\":\"story_Neuropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":15, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":3992,\"duration\":0},{\"idcod\":7631,\"duration\":0},{\"idcod\":8054,\"duration\":0},{\"idcod\":14387,\"duration\":0},{\"idcod\":14718,\"duration\":0},{\"idcod\":16249,\"duration\":0},{\"idcod\":16264,\"duration\":0},{\"idcod\":16350,\"duration\":0},{\"idcod\":17531,\"duration\":0},{\"idcod\":18378,\"duration\":0},{\"idcod\":18772,\"duration\":0},{\"idcod\":18943,\"duration\":0},{\"idcod\":19603,\"duration\":0},{\"idcod\":20801,\"duration\":0},{\"idcod\":21598,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5781,\"duration\":0},{\"idcod\":11615,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":482,\"25prctile\":427,\"75prctile\":537,\"patients\": [{\"idcod\":5781,\"duration\":427},{\"idcod\":11615,\"duration\":537}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5781,\"duration\":0},{\"idcod\":11615,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1365,\"25prctile\":0,\"75prctile\":2730,\"patients\": [{\"idcod\":5781,\"duration\":2730},{\"idcod\":11615,\"duration\":0}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5781,\"duration\":0},{\"idcod\":11615,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":12949,\"duration\":0},{\"idcod\":15082,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":1284,\"25prctile\":1262,\"75prctile\":1305,\"patients\": [{\"idcod\":12949,\"duration\":1262},{\"idcod\":15082,\"duration\":1305}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":12949,\"duration\":0},{\"idcod\":15082,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Nephropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3354,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1392,\"25prctile\":1392,\"75prctile\":1392,\"patients\": [{\"idcod\":3354,\"duration\":1392}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3354,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":523,\"25prctile\":523,\"75prctile\":523,\"patients\": [{\"idcod\":3354,\"duration\":523}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3354,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":1242,\"duration\":0},{\"idcod\":3960,\"duration\":0},{\"idcod\":7202,\"duration\":0},{\"idcod\":8305,\"duration\":0},{\"idcod\":11169,\"duration\":0},{\"idcod\":14321,\"duration\":0},{\"idcod\":14499,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":7, \"time\":1100,\"25prctile\":421,\"75prctile\":1901,\"patients\": [{\"idcod\":1242,\"duration\":517},{\"idcod\":3960,\"duration\":1489},{\"idcod\":7202,\"duration\":28},{\"idcod\":8305,\"duration\":2449},{\"idcod\":11169,\"duration\":1100},{\"idcod\":14321,\"duration\":389},{\"idcod\":14499,\"duration\":2038}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":1242,\"duration\":0},{\"idcod\":3960,\"duration\":0},{\"idcod\":7202,\"duration\":0},{\"idcod\":8305,\"duration\":0},{\"idcod\":11169,\"duration\":0},{\"idcod\":14321,\"duration\":0},{\"idcod\":14499,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17314,\"duration\":0},{\"idcod\":21014,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":468,\"25prctile\":61,\"75prctile\":874,\"patients\": [{\"idcod\":17314,\"duration\":61},{\"idcod\":21014,\"duration\":874}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17314,\"duration\":0},{\"idcod\":21014,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":538,\"25prctile\":478,\"75prctile\":597,\"patients\": [{\"idcod\":17314,\"duration\":478},{\"idcod\":21014,\"duration\":597}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17314,\"duration\":0},{\"idcod\":21014,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":21183,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1100,\"25prctile\":376,\"75prctile\":1901,\"patients\": [{\"idcod\":21183,\"duration\":2038}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":21183,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Nephropathy_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4,\"25prctile\":4,\"75prctile\":4,\"patients\": [{\"idcod\":10045,\"duration\":4}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":966,\"25prctile\":966,\"75prctile\":966,\"patients\": [{\"idcod\":10045,\"duration\":966}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1795,\"25prctile\":1795,\"75prctile\":1795,\"patients\": [{\"idcod\":10045,\"duration\":1795}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":172,\"25prctile\":172,\"75prctile\":172,\"patients\": [{\"idcod\":10045,\"duration\":172}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10045,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2501,\"25prctile\":2501,\"75prctile\":2501,\"patients\": [{\"idcod\":12315,\"duration\":2501}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":542,\"25prctile\":542,\"75prctile\":542,\"patients\": [{\"idcod\":12315,\"duration\":542}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1244,\"25prctile\":1244,\"75prctile\":1244,\"patients\": [{\"idcod\":12315,\"duration\":1244}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12315,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19227,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":667,\"25prctile\":667,\"75prctile\":667,\"patients\": [{\"idcod\":19227,\"duration\":667}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19227,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":555,\"25prctile\":555,\"75prctile\":555,\"patients\": [{\"idcod\":19227,\"duration\":555}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19227,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":48, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":80,\"duration\":0},{\"idcod\":124,\"duration\":0},{\"idcod\":1643,\"duration\":0},{\"idcod\":3809,\"duration\":0},{\"idcod\":5692,\"duration\":0},{\"idcod\":5928,\"duration\":0},{\"idcod\":6071,\"duration\":0},{\"idcod\":6424,\"duration\":0},{\"idcod\":7151,\"duration\":0},{\"idcod\":8479,\"duration\":0},{\"idcod\":8524,\"duration\":0},{\"idcod\":9064,\"duration\":0},{\"idcod\":9276,\"duration\":0},{\"idcod\":9370,\"duration\":0},{\"idcod\":9972,\"duration\":0},{\"idcod\":10165,\"duration\":0},{\"idcod\":10180,\"duration\":0},{\"idcod\":10266,\"duration\":0},{\"idcod\":10275,\"duration\":0},{\"idcod\":10314,\"duration\":0},{\"idcod\":10342,\"duration\":0},{\"idcod\":11160,\"duration\":0},{\"idcod\":11174,\"duration\":0},{\"idcod\":12895,\"duration\":0},{\"idcod\":13239,\"duration\":0},{\"idcod\":13825,\"duration\":0},{\"idcod\":13897,\"duration\":0},{\"idcod\":14146,\"duration\":0},{\"idcod\":14726,\"duration\":0},{\"idcod\":15360,\"duration\":0},{\"idcod\":16245,\"duration\":0},{\"idcod\":17281,\"duration\":0},{\"idcod\":17535,\"duration\":0},{\"idcod\":17844,\"duration\":0},{\"idcod\":17881,\"duration\":0},{\"idcod\":18353,\"duration\":0},{\"idcod\":18549,\"duration\":0},{\"idcod\":18774,\"duration\":0},{\"idcod\":19169,\"duration\":0},{\"idcod\":19296,\"duration\":0},{\"idcod\":20280,\"duration\":0},{\"idcod\":20625,\"duration\":0},{\"idcod\":20935,\"duration\":0},{\"idcod\":21101,\"duration\":0},{\"idcod\":21270,\"duration\":0},{\"idcod\":21544,\"duration\":0},{\"idcod\":21578,\"duration\":0},{\"idcod\":21583,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":5820,\"duration\":0},{\"idcod\":12472,\"duration\":0},{\"idcod\":19405,\"duration\":0},{\"idcod\":21297,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":982,\"25prctile\":840,\"75prctile\":2786,\"patients\": [{\"idcod\":5820,\"duration\":991},{\"idcod\":12472,\"duration\":4580},{\"idcod\":19405,\"duration\":973},{\"idcod\":21297,\"duration\":707}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":5820,\"duration\":0},{\"idcod\":12472,\"duration\":0},{\"idcod\":19405,\"duration\":0},{\"idcod\":21297,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9249,\"duration\":0},{\"idcod\":18609,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":945,\"25prctile\":812,\"75prctile\":1060,\"patients\": [{\"idcod\":9249,\"duration\":916},{\"idcod\":18609,\"duration\":707}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":9249,\"duration\":0},{\"idcod\":18609,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Neuropathy\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":10055,\"duration\":0},{\"idcod\":11350,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":738,\"25prctile\":356,\"75prctile\":871,\"patients\": [{\"idcod\":10055,\"duration\":5},{\"idcod\":11350,\"duration\":707}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":10055,\"duration\":0},{\"idcod\":11350,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_Chronicischemicheartdisease_DiabeticFoot\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1185,\"25prctile\":1185,\"75prctile\":1185,\"patients\": [{\"idcod\":5453,\"duration\":1185}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2595,\"25prctile\":2595,\"75prctile\":2595,\"patients\": [{\"idcod\":5453,\"duration\":2595}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1185,\"25prctile\":1185,\"75prctile\":1185,\"patients\": [{\"idcod\":5453,\"duration\":1185}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5453,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease_DiabeticFoot\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20961,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1491,\"25prctile\":1491,\"75prctile\":1491,\"patients\": [{\"idcod\":20961,\"duration\":1491}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20961,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":936,\"25prctile\":936,\"75prctile\":936,\"patients\": [{\"idcod\":20961,\"duration\":936}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20961,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":2457,\"duration\":0},{\"idcod\":17468,\"duration\":0},{\"idcod\":20656,\"duration\":0},{\"idcod\":21318,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_FatLiverDisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":16589,\"duration\":0},{\"idcod\":20144,\"duration\":0},{\"idcod\":20518,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":119,\"25prctile\":43,\"75prctile\":777,\"patients\": [{\"idcod\":16589,\"duration\":18},{\"idcod\":20144,\"duration\":119},{\"idcod\":20518,\"duration\":996}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":16589,\"duration\":0},{\"idcod\":20144,\"duration\":0},{\"idcod\":20518,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":10338,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":885,\"25prctile\":311,\"75prctile\":968,\"patients\": [{\"idcod\":10338,\"duration\":996}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":10338,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":13483,\"duration\":0},{\"idcod\":18432,\"duration\":0},{\"idcod\":20584,\"duration\":0},{\"idcod\":20949,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":149,\"25prctile\":0,\"75prctile\":1932,\"patients\": [{\"idcod\":13483,\"duration\":3566},{\"idcod\":18432,\"duration\":0},{\"idcod\":20584,\"duration\":297},{\"idcod\":20949,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":13483,\"duration\":0},{\"idcod\":18432,\"duration\":0},{\"idcod\":20584,\"duration\":0},{\"idcod\":20949,\"duration\":0}]}]},{\"label\":\"story_Retinopathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":10, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":845,\"duration\":0},{\"idcod\":1803,\"duration\":0},{\"idcod\":4385,\"duration\":0},{\"idcod\":11823,\"duration\":0},{\"idcod\":14871,\"duration\":0},{\"idcod\":15605,\"duration\":0},{\"idcod\":15800,\"duration\":0},{\"idcod\":19842,\"duration\":0},{\"idcod\":20185,\"duration\":0},{\"idcod\":21045,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17247,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1676,\"25prctile\":1676,\"75prctile\":1676,\"patients\": [{\"idcod\":17247,\"duration\":1676}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17247,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":18342,\"duration\":0},{\"idcod\":21594,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":846,\"25prctile\":230,\"75prctile\":1461,\"patients\": [{\"idcod\":18342,\"duration\":230},{\"idcod\":21594,\"duration\":1461}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":18342,\"duration\":0},{\"idcod\":21594,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Neuropathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17734,\"duration\":0},{\"idcod\":20234,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":14,\"25prctile\":13,\"75prctile\":14,\"patients\": [{\"idcod\":17734,\"duration\":14},{\"idcod\":20234,\"duration\":13}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17734,\"duration\":0},{\"idcod\":20234,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":545,\"25prctile\":194,\"75prctile\":896,\"patients\": [{\"idcod\":17734,\"duration\":896},{\"idcod\":20234,\"duration\":194}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":17734,\"duration\":0},{\"idcod\":20234,\"duration\":0}]}]},{\"label\":\"story_Stroke\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":10963,\"duration\":0},{\"idcod\":18542,\"duration\":0}]}]},{\"label\":\"story_Stroke_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6847,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":707,\"25prctile\":707,\"75prctile\":707,\"patients\": [{\"idcod\":6847,\"duration\":707}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6847,\"duration\":0}]}]},{\"label\":\"story_Stroke_FatLiverDisease_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11887,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1176,\"25prctile\":1176,\"75prctile\":1176,\"patients\": [{\"idcod\":11887,\"duration\":1176}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11887,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":562,\"25prctile\":562,\"75prctile\":562,\"patients\": [{\"idcod\":11887,\"duration\":562}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11887,\"duration\":0}]}]},{\"label\":\"story_Stroke_Retinopathy_Neuropathy_Nephropathy_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":65,\"25prctile\":65,\"75prctile\":65,\"patients\": [{\"idcod\":11796,\"duration\":65}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":200,\"25prctile\":200,\"75prctile\":200,\"patients\": [{\"idcod\":11796,\"duration\":200}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":700,\"25prctile\":700,\"75prctile\":700,\"patients\": [{\"idcod\":11796,\"duration\":700}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":942,\"25prctile\":942,\"75prctile\":942,\"patients\": [{\"idcod\":11796,\"duration\":942}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":570,\"25prctile\":570,\"75prctile\":570,\"patients\": [{\"idcod\":11796,\"duration\":570}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11796,\"duration\":0}]}]}]}";
			//			String jsonIn2 = "{\"histories\":[{\"label\":\"story_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":14564,\"duration\":0},{\"idcod\":16897,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":6, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.593838,\"patients\": [{\"idcod\":8148,\"duration\":0},{\"idcod\":14370,\"duration\":0},{\"idcod\":15418,\"duration\":0},{\"idcod\":17738,\"duration\":0},{\"idcod\":19064,\"duration\":0},{\"idcod\":21213,\"duration\":0}]}]},{\"label\":\"story_Nephropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.593838,\"patients\": [{\"idcod\":14661,\"duration\":0},{\"idcod\":19043,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16801,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1091,\"25prctile\":1091,\"75prctile\":1091,\"patients\": [{\"idcod\":16801,\"duration\":1091}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":16801,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9802,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2379,\"25prctile\":2379,\"75prctile\":2379,\"patients\": [{\"idcod\":9802,\"duration\":2379}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9802,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":200,\"25prctile\":200,\"75prctile\":200,\"patients\": [{\"idcod\":9802,\"duration\":200}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":9802,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Retinopathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18971,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":189,\"25prctile\":189,\"75prctile\":189,\"patients\": [{\"idcod\":18971,\"duration\":189}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18971,\"duration\":0}]}]},{\"label\":\"story_Neuropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":6, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.593838,\"patients\": [{\"idcod\":8054,\"duration\":0},{\"idcod\":14387,\"duration\":0},{\"idcod\":14718,\"duration\":0},{\"idcod\":16249,\"duration\":0},{\"idcod\":18943,\"duration\":0},{\"idcod\":19758,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17314,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":61,\"25prctile\":61,\"75prctile\":61,\"patients\": [{\"idcod\":17314,\"duration\":61}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17314,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":478,\"25prctile\":478,\"75prctile\":478,\"patients\": [{\"idcod\":17314,\"duration\":478}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17314,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4043,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":34,\"25prctile\":34,\"75prctile\":34,\"patients\": [{\"idcod\":4043,\"duration\":34}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4043,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4603,\"25prctile\":4603,\"75prctile\":4603,\"patients\": [{\"idcod\":4043,\"duration\":4603}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4043,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":300,\"25prctile\":300,\"75prctile\":300,\"patients\": [{\"idcod\":4043,\"duration\":300}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":4043,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_Retinopathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11567,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":362,\"25prctile\":362,\"75prctile\":362,\"patients\": [{\"idcod\":11567,\"duration\":362}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11567,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1388,\"25prctile\":1388,\"75prctile\":1388,\"patients\": [{\"idcod\":11567,\"duration\":1388}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11567,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6906,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1596,\"25prctile\":1596,\"75prctile\":1596,\"patients\": [{\"idcod\":6906,\"duration\":1596}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6906,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1509,\"25prctile\":1509,\"75prctile\":1509,\"patients\": [{\"idcod\":6906,\"duration\":1509}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6906,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":10, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.333333,\"patients\": [{\"idcod\":80,\"duration\":0},{\"idcod\":5121,\"duration\":0},{\"idcod\":10180,\"duration\":0},{\"idcod\":14256,\"duration\":0},{\"idcod\":17181,\"duration\":0},{\"idcod\":17281,\"duration\":0},{\"idcod\":17881,\"duration\":0},{\"idcod\":18353,\"duration\":0},{\"idcod\":19169,\"duration\":0},{\"idcod\":20935,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19405,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":973,\"25prctile\":973,\"75prctile\":973,\"patients\": [{\"idcod\":19405,\"duration\":973}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19405,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6840,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":14,\"25prctile\":14,\"75prctile\":14,\"patients\": [{\"idcod\":6840,\"duration\":14}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6840,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Neuropathy_Peripheralvasculardisease_DiabeticFoot\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8548,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":240,\"25prctile\":240,\"75prctile\":240,\"patients\": [{\"idcod\":8548,\"duration\":240}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8548,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":679,\"25prctile\":679,\"75prctile\":679,\"patients\": [{\"idcod\":8548,\"duration\":679}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8548,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":4382,\"25prctile\":4382,\"75prctile\":4382,\"patients\": [{\"idcod\":8548,\"duration\":4382}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8548,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.333333,\"patients\": [{\"idcod\":6662,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Nephropathy_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":196,\"25prctile\":196,\"75prctile\":196,\"patients\": [{\"idcod\":10222,\"duration\":196}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":354,\"25prctile\":354,\"75prctile\":354,\"patients\": [{\"idcod\":10222,\"duration\":354}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":709,\"25prctile\":709,\"75prctile\":709,\"patients\": [{\"idcod\":10222,\"duration\":709}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1348,\"25prctile\":1348,\"75prctile\":1348,\"patients\": [{\"idcod\":10222,\"duration\":1348}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10222,\"duration\":0}]}]},{\"label\":\"story_Retinopathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.333333,\"patients\": [{\"idcod\":5353,\"duration\":0},{\"idcod\":20185,\"duration\":0},{\"idcod\":20994,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_FatLiverDisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11422,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":23,\"25prctile\":23,\"75prctile\":23,\"patients\": [{\"idcod\":11422,\"duration\":23}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11422,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19960,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":421,\"25prctile\":421,\"75prctile\":421,\"patients\": [{\"idcod\":19960,\"duration\":421}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":19960,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease_Neuropathy_DiabeticFoot\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17262,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":3,\"25prctile\":3,\"75prctile\":3,\"patients\": [{\"idcod\":17262,\"duration\":3}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17262,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":786,\"25prctile\":786,\"75prctile\":786,\"patients\": [{\"idcod\":17262,\"duration\":786}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17262,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1489,\"25prctile\":1489,\"75prctile\":1489,\"patients\": [{\"idcod\":17262,\"duration\":1489}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17262,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":606,\"25prctile\":606,\"75prctile\":606,\"patients\": [{\"idcod\":17262,\"duration\":606}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17262,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Stroke_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20004,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":153,\"25prctile\":153,\"75prctile\":153,\"patients\": [{\"idcod\":20004,\"duration\":153}]},{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20004,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":471,\"25prctile\":471,\"75prctile\":471,\"patients\": [{\"idcod\":20004,\"duration\":471}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20004,\"duration\":0}]}]},{\"label\":\"story_Stroke\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.333333,\"patients\": [{\"idcod\":18542,\"duration\":0}]}]}]}";
			//			String jsonIn3 = "{\"histories\":[{\"label\":\"story_Angina\",\"steps\": [{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5367,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":12, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.597271,\"patients\": [{\"idcod\":212,\"duration\":0},{\"idcod\":4480,\"duration\":0},{\"idcod\":5302,\"duration\":0},{\"idcod\":5931,\"duration\":0},{\"idcod\":9136,\"duration\":0},{\"idcod\":9389,\"duration\":0},{\"idcod\":10121,\"duration\":0},{\"idcod\":11871,\"duration\":0},{\"idcod\":17055,\"duration\":0},{\"idcod\":17837,\"duration\":0},{\"idcod\":19732,\"duration\":0},{\"idcod\":20761,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Nephropathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":7760,\"duration\":0},{\"idcod\":20471,\"duration\":0},{\"idcod\":21539,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":1664,\"25prctile\":1353,\"75prctile\":1851,\"patients\": [{\"idcod\":7760,\"duration\":1664},{\"idcod\":20471,\"duration\":1913},{\"idcod\":21539,\"duration\":1249}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":7760,\"duration\":0},{\"idcod\":20471,\"duration\":0},{\"idcod\":21539,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":6, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.593838,\"patients\": [{\"idcod\":3679,\"duration\":0},{\"idcod\":6279,\"duration\":0},{\"idcod\":7671,\"duration\":0},{\"idcod\":8387,\"duration\":0},{\"idcod\":11284,\"duration\":0},{\"idcod\":20049,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":6, \"time\":1931,\"25prctile\":208,\"75prctile\":3506,\"patients\": [{\"idcod\":3679,\"duration\":2880},{\"idcod\":6279,\"duration\":208},{\"idcod\":7671,\"duration\":4287},{\"idcod\":8387,\"duration\":981},{\"idcod\":11284,\"duration\":3506},{\"idcod\":20049,\"duration\":58}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":6, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.593838,\"patients\": [{\"idcod\":3679,\"duration\":0},{\"idcod\":6279,\"duration\":0},{\"idcod\":7671,\"duration\":0},{\"idcod\":8387,\"duration\":0},{\"idcod\":11284,\"duration\":0},{\"idcod\":20049,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Retinopathy\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20209,\"duration\":0},{\"idcod\":21288,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":2876,\"25prctile\":720,\"75prctile\":5031,\"patients\": [{\"idcod\":20209,\"duration\":5031},{\"idcod\":21288,\"duration\":720}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20209,\"duration\":0},{\"idcod\":21288,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":310,\"25prctile\":5,\"75prctile\":56,\"patients\": [{\"idcod\":20209,\"duration\":5},{\"idcod\":21288,\"duration\":56}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20209,\"duration\":0},{\"idcod\":21288,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_Retinopathy_DiabeticFoot\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18254,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":776,\"25prctile\":776,\"75prctile\":776,\"patients\": [{\"idcod\":18254,\"duration\":776}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18254,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":150,\"25prctile\":150,\"75prctile\":150,\"patients\": [{\"idcod\":18254,\"duration\":150}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18254,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1078,\"25prctile\":1078,\"75prctile\":1078,\"patients\": [{\"idcod\":18254,\"duration\":1078}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18254,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1302,\"25prctile\":1302,\"75prctile\":1302,\"patients\": [{\"idcod\":18254,\"duration\":1302}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":18254,\"duration\":0}]}]},{\"label\":\"story_DiabeticFoot_Neuropathy_Peripheralvasculardisease_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10808,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":100,\"25prctile\":100,\"75prctile\":100,\"patients\": [{\"idcod\":10808,\"duration\":100}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10808,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":203,\"25prctile\":203,\"75prctile\":203,\"patients\": [{\"idcod\":10808,\"duration\":203}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10808,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":633,\"25prctile\":633,\"75prctile\":633,\"patients\": [{\"idcod\":10808,\"duration\":633}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10808,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":160,\"25prctile\":160,\"75prctile\":160,\"patients\": [{\"idcod\":10808,\"duration\":160}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":10808,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":54, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":537,\"duration\":0},{\"idcod\":772,\"duration\":0},{\"idcod\":2255,\"duration\":0},{\"idcod\":2825,\"duration\":0},{\"idcod\":4154,\"duration\":0},{\"idcod\":4404,\"duration\":0},{\"idcod\":4718,\"duration\":0},{\"idcod\":4732,\"duration\":0},{\"idcod\":5514,\"duration\":0},{\"idcod\":5796,\"duration\":0},{\"idcod\":5836,\"duration\":0},{\"idcod\":6319,\"duration\":0},{\"idcod\":6419,\"duration\":0},{\"idcod\":6632,\"duration\":0},{\"idcod\":8134,\"duration\":0},{\"idcod\":8148,\"duration\":0},{\"idcod\":8253,\"duration\":0},{\"idcod\":8265,\"duration\":0},{\"idcod\":8333,\"duration\":0},{\"idcod\":8976,\"duration\":0},{\"idcod\":9033,\"duration\":0},{\"idcod\":9585,\"duration\":0},{\"idcod\":10350,\"duration\":0},{\"idcod\":10667,\"duration\":0},{\"idcod\":10700,\"duration\":0},{\"idcod\":10743,\"duration\":0},{\"idcod\":11130,\"duration\":0},{\"idcod\":12011,\"duration\":0},{\"idcod\":12135,\"duration\":0},{\"idcod\":13079,\"duration\":0},{\"idcod\":14529,\"duration\":0},{\"idcod\":14594,\"duration\":0},{\"idcod\":14700,\"duration\":0},{\"idcod\":15418,\"duration\":0},{\"idcod\":15669,\"duration\":0},{\"idcod\":15934,\"duration\":0},{\"idcod\":16759,\"duration\":0},{\"idcod\":17621,\"duration\":0},{\"idcod\":17738,\"duration\":0},{\"idcod\":17756,\"duration\":0},{\"idcod\":18041,\"duration\":0},{\"idcod\":18143,\"duration\":0},{\"idcod\":18273,\"duration\":0},{\"idcod\":18369,\"duration\":0},{\"idcod\":18725,\"duration\":0},{\"idcod\":19112,\"duration\":0},{\"idcod\":19309,\"duration\":0},{\"idcod\":19515,\"duration\":0},{\"idcod\":19837,\"duration\":0},{\"idcod\":20285,\"duration\":0},{\"idcod\":20350,\"duration\":0},{\"idcod\":20827,\"duration\":0},{\"idcod\":21213,\"duration\":0},{\"idcod\":21507,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Nephropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":329,\"duration\":0},{\"idcod\":8293,\"duration\":0},{\"idcod\":11340,\"duration\":0},{\"idcod\":12487,\"duration\":0},{\"idcod\":12743,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":5, \"time\":527,\"25prctile\":355,\"75prctile\":1149,\"patients\": [{\"idcod\":329,\"duration\":988},{\"idcod\":8293,\"duration\":322},{\"idcod\":11340,\"duration\":1632},{\"idcod\":12487,\"duration\":527},{\"idcod\":12743,\"duration\":366}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":329,\"duration\":0},{\"idcod\":8293,\"duration\":0},{\"idcod\":11340,\"duration\":0},{\"idcod\":12487,\"duration\":0},{\"idcod\":12743,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":6162,\"duration\":0},{\"idcod\":7771,\"duration\":0},{\"idcod\":9185,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":366,\"25prctile\":5,\"75prctile\":705,\"patients\": [{\"idcod\":6162,\"duration\":1240},{\"idcod\":7771,\"duration\":7},{\"idcod\":9185,\"duration\":366}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":6162,\"duration\":0},{\"idcod\":7771,\"duration\":0},{\"idcod\":9185,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":7191,\"duration\":0},{\"idcod\":14877,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":366,\"25prctile\":111,\"75prctile\":1787,\"patients\": [{\"idcod\":7191,\"duration\":148},{\"idcod\":14877,\"duration\":366}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":7191,\"duration\":0},{\"idcod\":14877,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Retinopathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":12986,\"duration\":0},{\"idcod\":17554,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":527,\"25prctile\":275,\"75prctile\":575,\"patients\": [{\"idcod\":12986,\"duration\":627},{\"idcod\":17554,\"duration\":366}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":12986,\"duration\":0},{\"idcod\":17554,\"duration\":0}]}]},{\"label\":\"story_Nephropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":6, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":2839,\"duration\":0},{\"idcod\":4438,\"duration\":0},{\"idcod\":9213,\"duration\":0},{\"idcod\":16602,\"duration\":0},{\"idcod\":20113,\"duration\":0},{\"idcod\":20345,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5844,\"duration\":0},{\"idcod\":8750,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":263,\"25prctile\":95,\"75prctile\":431,\"patients\": [{\"idcod\":5844,\"duration\":95},{\"idcod\":8750,\"duration\":431}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":5844,\"duration\":0},{\"idcod\":8750,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Retinopathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":19497,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":301,\"25prctile\":171,\"75prctile\":431,\"patients\": [{\"idcod\":19497,\"duration\":431}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":19497,\"duration\":0}]}]},{\"label\":\"story_Neuropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":6982,\"duration\":0},{\"idcod\":15133,\"duration\":0},{\"idcod\":18032,\"duration\":0},{\"idcod\":18411,\"duration\":0},{\"idcod\":19758,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":15016,\"duration\":0},{\"idcod\":20579,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":523,\"25prctile\":194,\"75prctile\":851,\"patients\": [{\"idcod\":15016,\"duration\":851},{\"idcod\":20579,\"duration\":194}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":15016,\"duration\":0},{\"idcod\":20579,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_FatLiverDisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":14797,\"duration\":0},{\"idcod\":19501,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":579,\"25prctile\":18,\"75prctile\":1139,\"patients\": [{\"idcod\":14797,\"duration\":1139},{\"idcod\":19501,\"duration\":18}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":14797,\"duration\":0},{\"idcod\":19501,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Peripheralvasculardisease_Retinopathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11567,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":362,\"25prctile\":362,\"75prctile\":362,\"patients\": [{\"idcod\":11567,\"duration\":362}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11567,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1388,\"25prctile\":1388,\"75prctile\":1388,\"patients\": [{\"idcod\":11567,\"duration\":1388}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":11567,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6906,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1596,\"25prctile\":1596,\"75prctile\":1596,\"patients\": [{\"idcod\":6906,\"duration\":1596}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6906,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1509,\"25prctile\":1509,\"75prctile\":1509,\"patients\": [{\"idcod\":6906,\"duration\":1509}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6906,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":53, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":381,\"duration\":0},{\"idcod\":512,\"duration\":0},{\"idcod\":752,\"duration\":0},{\"idcod\":1347,\"duration\":0},{\"idcod\":3474,\"duration\":0},{\"idcod\":4010,\"duration\":0},{\"idcod\":4067,\"duration\":0},{\"idcod\":4483,\"duration\":0},{\"idcod\":4567,\"duration\":0},{\"idcod\":5121,\"duration\":0},{\"idcod\":5243,\"duration\":0},{\"idcod\":5361,\"duration\":0},{\"idcod\":5719,\"duration\":0},{\"idcod\":5774,\"duration\":0},{\"idcod\":6197,\"duration\":0},{\"idcod\":6568,\"duration\":0},{\"idcod\":6828,\"duration\":0},{\"idcod\":7287,\"duration\":0},{\"idcod\":7863,\"duration\":0},{\"idcod\":7995,\"duration\":0},{\"idcod\":8024,\"duration\":0},{\"idcod\":8457,\"duration\":0},{\"idcod\":9912,\"duration\":0},{\"idcod\":10535,\"duration\":0},{\"idcod\":11349,\"duration\":0},{\"idcod\":11548,\"duration\":0},{\"idcod\":11553,\"duration\":0},{\"idcod\":11814,\"duration\":0},{\"idcod\":11934,\"duration\":0},{\"idcod\":12671,\"duration\":0},{\"idcod\":12712,\"duration\":0},{\"idcod\":13008,\"duration\":0},{\"idcod\":14256,\"duration\":0},{\"idcod\":14660,\"duration\":0},{\"idcod\":15348,\"duration\":0},{\"idcod\":16172,\"duration\":0},{\"idcod\":16809,\"duration\":0},{\"idcod\":17181,\"duration\":0},{\"idcod\":17295,\"duration\":0},{\"idcod\":17921,\"duration\":0},{\"idcod\":18345,\"duration\":0},{\"idcod\":19261,\"duration\":0},{\"idcod\":19496,\"duration\":0},{\"idcod\":19966,\"duration\":0},{\"idcod\":20396,\"duration\":0},{\"idcod\":20861,\"duration\":0},{\"idcod\":20971,\"duration\":0},{\"idcod\":20983,\"duration\":0},{\"idcod\":21095,\"duration\":0},{\"idcod\":21173,\"duration\":0},{\"idcod\":21193,\"duration\":0},{\"idcod\":21375,\"duration\":0},{\"idcod\":21575,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_FatLiverDisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":1792,\"duration\":0},{\"idcod\":6840,\"duration\":0},{\"idcod\":16132,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":14,\"25prctile\":7,\"75prctile\":1076,\"patients\": [{\"idcod\":1792,\"duration\":4},{\"idcod\":6840,\"duration\":14},{\"idcod\":16132,\"duration\":1430}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":1792,\"duration\":0},{\"idcod\":6840,\"duration\":0},{\"idcod\":16132,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Nephropathy\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":16807,\"duration\":0},{\"idcod\":20169,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":587,\"25prctile\":540,\"75prctile\":1219,\"patients\": [{\"idcod\":16807,\"duration\":524},{\"idcod\":20169,\"duration\":1430}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":16807,\"duration\":0},{\"idcod\":20169,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Neuropathy\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":14177,\"duration\":0},{\"idcod\":19570,\"duration\":0},{\"idcod\":20046,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":1607,\"25prctile\":402,\"75prctile\":2599,\"patients\": [{\"idcod\":14177,\"duration\":1607},{\"idcod\":19570,\"duration\":2929},{\"idcod\":20046,\"duration\":0}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":14177,\"duration\":0},{\"idcod\":19570,\"duration\":0},{\"idcod\":20046,\"duration\":0}]}]},{\"label\":\"story_Retinopathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":10, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":6.774646,\"patients\": [{\"idcod\":4369,\"duration\":0},{\"idcod\":5353,\"duration\":0},{\"idcod\":14260,\"duration\":0},{\"idcod\":15476,\"duration\":0},{\"idcod\":16143,\"duration\":0},{\"idcod\":18507,\"duration\":0},{\"idcod\":19367,\"duration\":0},{\"idcod\":20529,\"duration\":0},{\"idcod\":20994,\"duration\":0},{\"idcod\":21294,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_FatLiverDisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":5891,\"duration\":0},{\"idcod\":8069,\"duration\":0},{\"idcod\":11422,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":34,\"25prctile\":26,\"75prctile\":277,\"patients\": [{\"idcod\":5891,\"duration\":358},{\"idcod\":8069,\"duration\":34},{\"idcod\":11422,\"duration\":23}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":5891,\"duration\":0},{\"idcod\":8069,\"duration\":0},{\"idcod\":11422,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":8370,\"duration\":0},{\"idcod\":17259,\"duration\":0},{\"idcod\":19960,\"duration\":0},{\"idcod\":21586,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":4, \"time\":477,\"25prctile\":363,\"75prctile\":1059,\"patients\": [{\"idcod\":8370,\"duration\":533},{\"idcod\":17259,\"duration\":1585},{\"idcod\":19960,\"duration\":421},{\"idcod\":21586,\"duration\":304}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.006867,\"patients\": [{\"idcod\":8370,\"duration\":0},{\"idcod\":17259,\"duration\":0},{\"idcod\":19960,\"duration\":0},{\"idcod\":21586,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":3817,\"duration\":0},{\"idcod\":9737,\"duration\":0},{\"idcod\":16659,\"duration\":0},{\"idcod\":20466,\"duration\":0},{\"idcod\":21446,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":5, \"time\":677,\"25prctile\":377,\"75prctile\":891,\"patients\": [{\"idcod\":3817,\"duration\":677},{\"idcod\":9737,\"duration\":1166},{\"idcod\":16659,\"duration\":799},{\"idcod\":20466,\"duration\":471},{\"idcod\":21446,\"duration\":95}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":5, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.329900,\"patients\": [{\"idcod\":3817,\"duration\":0},{\"idcod\":9737,\"duration\":0},{\"idcod\":16659,\"duration\":0},{\"idcod\":20466,\"duration\":0},{\"idcod\":21446,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Stroke_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20004,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":153,\"25prctile\":153,\"75prctile\":153,\"patients\": [{\"idcod\":20004,\"duration\":153}]},{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20004,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":271,\"25prctile\":271,\"75prctile\":271,\"patients\": [{\"idcod\":20004,\"duration\":271}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20004,\"duration\":0}]}]},{\"label\":\"story_Stroke_FatLiverDisease\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8785,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":46,\"25prctile\":46,\"75prctile\":46,\"patients\": [{\"idcod\":8785,\"duration\":46}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8785,\"duration\":0}]}]}]}";
			//
			//			String jsonIn4 = "{\"histories\":[{\"label\":\"story_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":212,\"duration\":0},{\"idcod\":4238,\"duration\":0},{\"idcod\":7526,\"duration\":0},{\"idcod\":11000,\"duration\":0},{\"idcod\":17837,\"duration\":0},{\"idcod\":18289,\"duration\":0},{\"idcod\":21592,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":3679,\"duration\":0},{\"idcod\":5581,\"duration\":0},{\"idcod\":6422,\"duration\":0},{\"idcod\":13252,\"duration\":0},{\"idcod\":16415,\"duration\":0},{\"idcod\":18580,\"duration\":0},{\"idcod\":20151,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":7, \"time\":2146,\"25prctile\":151,\"75prctile\":2777,\"patients\": [{\"idcod\":3679,\"duration\":2880},{\"idcod\":5581,\"duration\":59},{\"idcod\":6422,\"duration\":4465},{\"idcod\":13252,\"duration\":427},{\"idcod\":16415,\"duration\":2466},{\"idcod\":18580,\"duration\":2146},{\"idcod\":20151,\"duration\":31}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":7, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":3.816993,\"patients\": [{\"idcod\":3679,\"duration\":0},{\"idcod\":5581,\"duration\":0},{\"idcod\":6422,\"duration\":0},{\"idcod\":13252,\"duration\":0},{\"idcod\":16415,\"duration\":0},{\"idcod\":18580,\"duration\":0},{\"idcod\":20151,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Occlusionandstenosisofcarotidartery_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15051,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":6209,\"25prctile\":6209,\"75prctile\":6209,\"patients\": [{\"idcod\":15051,\"duration\":6209}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15051,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":6123,\"25prctile\":6123,\"75prctile\":6123,\"patients\": [{\"idcod\":15051,\"duration\":6123}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15051,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17491,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5323,\"25prctile\":5323,\"75prctile\":5323,\"patients\": [{\"idcod\":17491,\"duration\":5323}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17491,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"patients\": [{\"idcod\":17491,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":17491,\"duration\":0}]}]},{\"label\":\"story_Chronicischemicheartdisease_Peripheralvasculardisease_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":6009,\"25prctile\":6009,\"75prctile\":6009,\"patients\": [{\"idcod\":8611,\"duration\":6009}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1578,\"25prctile\":1578,\"75prctile\":1578,\"patients\": [{\"idcod\":8611,\"duration\":1578}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8611,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":12, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.597271,\"patients\": [{\"idcod\":2255,\"duration\":0},{\"idcod\":3579,\"duration\":0},{\"idcod\":4755,\"duration\":0},{\"idcod\":5836,\"duration\":0},{\"idcod\":8231,\"duration\":0},{\"idcod\":10350,\"duration\":0},{\"idcod\":18086,\"duration\":0},{\"idcod\":19085,\"duration\":0},{\"idcod\":19515,\"duration\":0},{\"idcod\":20401,\"duration\":0},{\"idcod\":20491,\"duration\":0},{\"idcod\":21507,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Neuropathy\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":4025,\"duration\":0},{\"idcod\":9185,\"duration\":0},{\"idcod\":20340,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":960,\"25prctile\":240,\"75prctile\":2265,\"patients\": [{\"idcod\":4025,\"duration\":2700},{\"idcod\":9185,\"duration\":0},{\"idcod\":20340,\"duration\":960}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":4025,\"duration\":0},{\"idcod\":9185,\"duration\":0},{\"idcod\":20340,\"duration\":0}]}]},{\"label\":\"story_FatLiverDisease_Peripheralvasculardisease\",\"steps\": [{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":11758,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":185,\"25prctile\":46,\"75prctile\":766,\"patients\": [{\"idcod\":11758,\"duration\":960}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":11758,\"duration\":0}]}]},{\"label\":\"story_Nephropathy\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":4, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.597271,\"patients\": [{\"idcod\":4438,\"duration\":0},{\"idcod\":15755,\"duration\":0},{\"idcod\":17269,\"duration\":0},{\"idcod\":20811,\"duration\":0}]}]},{\"label\":\"story_Nephropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20784,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":323,\"25prctile\":323,\"75prctile\":323,\"patients\": [{\"idcod\":20784,\"duration\":323}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":20784,\"duration\":0}]}]},{\"label\":\"story_Neuropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":4.597271,\"patients\": [{\"idcod\":17531,\"duration\":0},{\"idcod\":18032,\"duration\":0},{\"idcod\":21598,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15016,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":851,\"25prctile\":851,\"75prctile\":851,\"patients\": [{\"idcod\":15016,\"duration\":851}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":15016,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Nephropathy\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12949,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1262,\"25prctile\":1262,\"75prctile\":1262,\"patients\": [{\"idcod\":12949,\"duration\":1262}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":12949,\"duration\":0}]}]},{\"label\":\"story_Neuropathy_Retinopathy_Nephropathy_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7367,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":300,\"25prctile\":300,\"75prctile\":300,\"patients\": [{\"idcod\":7367,\"duration\":300}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7367,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":199,\"25prctile\":199,\"75prctile\":199,\"patients\": [{\"idcod\":7367,\"duration\":199}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7367,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2039,\"25prctile\":2039,\"75prctile\":2039,\"patients\": [{\"idcod\":7367,\"duration\":2039}]},{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":7367,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":29, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.874660,\"patients\": [{\"idcod\":512,\"duration\":0},{\"idcod\":1347,\"duration\":0},{\"idcod\":3474,\"duration\":0},{\"idcod\":4010,\"duration\":0},{\"idcod\":4067,\"duration\":0},{\"idcod\":6071,\"duration\":0},{\"idcod\":7863,\"duration\":0},{\"idcod\":8024,\"duration\":0},{\"idcod\":8457,\"duration\":0},{\"idcod\":9370,\"duration\":0},{\"idcod\":9972,\"duration\":0},{\"idcod\":10275,\"duration\":0},{\"idcod\":11174,\"duration\":0},{\"idcod\":12671,\"duration\":0},{\"idcod\":12895,\"duration\":0},{\"idcod\":13008,\"duration\":0},{\"idcod\":13897,\"duration\":0},{\"idcod\":14660,\"duration\":0},{\"idcod\":14726,\"duration\":0},{\"idcod\":16245,\"duration\":0},{\"idcod\":16809,\"duration\":0},{\"idcod\":17295,\"duration\":0},{\"idcod\":17844,\"duration\":0},{\"idcod\":18774,\"duration\":0},{\"idcod\":20396,\"duration\":0},{\"idcod\":21270,\"duration\":0},{\"idcod\":21575,\"duration\":0},{\"idcod\":21578,\"duration\":0},{\"idcod\":21583,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Angina\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6643,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":2015,\"25prctile\":2015,\"75prctile\":2015,\"patients\": [{\"idcod\":6643,\"duration\":2015}]},{\"label\":\"Angina\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":6643,\"duration\":0}]}]},{\"label\":\"story_Occlusionandstenosisofcarotidartery_Chronicischemicheartdisease\",\"steps\": [{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":5820,\"duration\":0},{\"idcod\":12472,\"duration\":0},{\"idcod\":21297,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":991,\"25prctile\":778,\"75prctile\":3683,\"patients\": [{\"idcod\":5820,\"duration\":991},{\"idcod\":12472,\"duration\":4580},{\"idcod\":21297,\"duration\":707}]},{\"label\":\"Chronicischemicheartdisease\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":5820,\"duration\":0},{\"idcod\":12472,\"duration\":0},{\"idcod\":21297,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.874660,\"patients\": [{\"idcod\":13126,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_FatLiverDisease\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20144,\"duration\":0},{\"idcod\":20518,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":558,\"25prctile\":119,\"75prctile\":996,\"patients\": [{\"idcod\":20144,\"duration\":119},{\"idcod\":20518,\"duration\":996}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.003433,\"patients\": [{\"idcod\":20144,\"duration\":0},{\"idcod\":20518,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy_Occlusionandstenosisofcarotidartery_DiabeticFoot\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":200,\"25prctile\":200,\"75prctile\":200,\"patients\": [{\"idcod\":5979,\"duration\":200}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":500,\"25prctile\":500,\"75prctile\":500,\"patients\": [{\"idcod\":5979,\"duration\":500}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":5062,\"25prctile\":5062,\"75prctile\":5062,\"patients\": [{\"idcod\":5979,\"duration\":5062}]},{\"label\":\"DiabeticFoot\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":5979,\"duration\":0}]}]},{\"label\":\"story_Peripheralvasculardisease_Neuropathy_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Peripheralvasculardisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":1000,\"25prctile\":1000,\"75prctile\":1000,\"patients\": [{\"idcod\":3175,\"duration\":1000}]},{\"label\":\"Neuropathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":700,\"25prctile\":700,\"75prctile\":700,\"patients\": [{\"idcod\":3175,\"duration\":700}]},{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":300,\"25prctile\":300,\"75prctile\":300,\"patients\": [{\"idcod\":3175,\"duration\":300}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":3175,\"duration\":0}]}]},{\"label\":\"story_Retinopathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":5.874660,\"patients\": [{\"idcod\":1803,\"duration\":0},{\"idcod\":15476,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Nephropathy\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":8370,\"duration\":0},{\"idcod\":18342,\"duration\":0},{\"idcod\":21586,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":3, \"time\":304,\"25prctile\":249,\"75prctile\":476,\"patients\": [{\"idcod\":8370,\"duration\":533},{\"idcod\":18342,\"duration\":230},{\"idcod\":21586,\"duration\":304}]},{\"label\":\"Nephropathy\", \"id\":\"event\", \"n_pts\":3, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":8370,\"duration\":0},{\"idcod\":18342,\"duration\":0},{\"idcod\":21586,\"duration\":0}]}]},{\"label\":\"story_Retinopathy_Occlusionandstenosisofcarotidartery\",\"steps\": [{\"label\":\"Retinopathy\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":20162,\"duration\":0},{\"idcod\":21446,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":2, \"time\":504,\"25prctile\":147,\"75prctile\":750,\"patients\": [{\"idcod\":20162,\"duration\":899},{\"idcod\":21446,\"duration\":504}]},{\"label\":\"Occlusionandstenosisofcarotidartery\", \"id\":\"event\", \"n_pts\":2, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":2.590404,\"patients\": [{\"idcod\":20162,\"duration\":0},{\"idcod\":21446,\"duration\":0}]}]}]}";
			//			String[] jsonInArray = new String[4];
			//			jsonInArray[0] = jsonIn1;
			//			jsonInArray[1] = jsonIn2;
			//			jsonInArray[2] = jsonIn3;
			//			jsonInArray[3] = jsonIn4;

			//selettore casuale da 0-3 (4 tipi)
			Random rand = new Random();
			// nextInt is normally exclusive of the top value,
			// so add 1 to make it inclusive
			int randomNum = rand.nextInt((3 - 0) + 1) + 0;
			//System.out.println("Numero casuale: "+randomNum);

			//jsonIn = jsonInArray[randomNum];
			//jsonIn ="{\"histories\":[{\"label\":\"story_Stroke_FatLiverDisease\",\"steps\": [{\"label\":\"Stroke\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8785,\"duration\":0}]},{\"label\":\"wait\", \"id\":\"transition\", \"n_pts\":1, \"time\":46,\"25prctile\":46,\"75prctile\":46,\"patients\": [{\"idcod\":8785,\"duration\":46}]},{\"label\":\"FatLiverDisease\", \"id\":\"event\", \"n_pts\":1, \"time\":0,\"25prctile\":0,\"75prctile\":0,\"min\":0,\"max\":0,\"h\":0.000000,\"num_classes\":1.000000,\"patients\": [{\"idcod\":8785,\"duration\":0}]}]}]}";
			//System.out.println(jsonIn);

			JSONParser jParser = new JSONParser();
			JSONObject objInput = (JSONObject) jParser.parse(jsonIn);
			JSONArray histories = (JSONArray)objInput.get("histories");
			//object principale
			JSONObject jsonResultContainer = new JSONObject();
			JSONArray historyNames = new JSONArray();
			JSONArray pzTot4historyNames = new JSONArray();
			//Cerco il primo token della history per costruire un json per ogni storia (raggruppate per primo token)
			List<Integer> storyLengthList = new ArrayList<Integer>(); //quante storie ci sono che cominciano con quel token (cioè quante serie devo fare)
			List<Integer> pz4StoryList = new ArrayList<Integer>(); //quanti pz in ogni storia che comincia con quel token
			Integer storyLength = 0;
			Integer pz4Story = 0;
			for(int i=0; i < histories.size(); i++){
				JSONObject history = (JSONObject) histories.get(i);
				String historyName = (String) history.get("label");
				JSONArray steps = (JSONArray) history.get("steps");	
				JSONObject step = (JSONObject)steps.get(0); //prendo nPts dal primo step xke npts è uguale in tutti gli step
				Integer nPts = ((Long)step.get("n_pts")).intValue();
				String[] historyNameSplit = historyName.split("_");
				String firstToken = historyNameSplit[1]; //xke 0: "story"
				if(!historyNames.contains(firstToken)){
					historyNames.add(firstToken);		//aggiungo solo il primo token di ogni storia		
					if(i>0){
						storyLengthList.add(storyLength);
						storyLength=0;
						pz4StoryList.add(pz4Story);
						pz4Story = 0;
					}
				}
				if(historyNameSplit.length>2)pz4Story = pz4Story+nPts; //non sommo nPts delle storie con un solo token (xke sono gestite a parte)
				storyLength++;
			}
			storyLengthList.add(storyLength); //aggiungo l'ultimo
			pz4StoryList.add(pz4Story);//aggiungo l'ultimo
			jsonResultContainer.put("h_names", historyNames);
			
			for(int h=0; h<historyNames.size(); h++){
				pzTot4historyNames.add(pz4StoryList.get(h));
				//System.out.println(historyNames.get(h)+" tot: "+pz4StoryList.get(h));
				//							System.out.println(historyNames.get(h));
				//							System.out.println(storyLengthList.get(h));
			}
			
			//			for(int h=0; h<historyNames.size(); h++){
			//				System.out.println(historyNames.get(h));
			//				System.out.println(storyLengthList.get(h));
			//			}
			//
			jsonResultContainer.put("pz_tot_h_names", pzTot4historyNames);
			JSONArray stepsArray = new JSONArray();
			JSONArray optionalInfosArrayOuter = new JSONArray();
			JSONArray optionalInfosArrayInner = new JSONArray();
			JSONArray historyOneStepArray = new JSONArray();
			int childNumber = 1;
			int storyLenghtCounter=1;
			String previousFirstToken = "";
			int numCols = 0;
			JSONArray cols = new JSONArray();
			JSONArray rows = new JSONArray();		
			JSONObject objCols_Rows = new JSONObject();		
			boolean doAdd = false;
			int nodesCounter = 0;
			for(int i=0; i < histories.size(); i++){
				JSONObject history = (JSONObject) histories.get(i);
				String historyName = (String) history.get("label");
				String[] historyNameSplit = historyName.split("_");
				if(historyNameSplit.length==2){//storie da 1 solo token
					JSONArray onestep = (JSONArray) history.get("steps");	
					JSONArray patientArray = (JSONArray) ((JSONObject) onestep.get(0)).get("patients");
					JSONObject historyOneStep = new JSONObject();
					historyOneStep.put("historyName", historyNameSplit[1].replaceAll("(.)([A-Z])", "$1 $2"));
					historyOneStep.put("patientCounter", patientArray.size());
					historyOneStepArray.add(historyOneStep);
				}
				String firstToken = historyNameSplit[1]; //xke 0: "story"		
				if(i==0){ //al primo passo = prima storia
					//Creo le colonne 3*storyLength+1
					doAdd = true;
					previousFirstToken = firstToken;
					numCols = storyLengthList.get(0);
					cols = new JSONArray();
					rows = new JSONArray();		
					JSONObject col_1 = new JSONObject();
					col_1.put("id", 1);
					col_1.put("label", "Giorni");
					col_1.put("type", "number");
					cols.add(col_1);
					for(int k=0; k < numCols; k++){
						JSONObject col_2 = new JSONObject();
						col_2.put("id", 2);
						col_2.put("label", "Complication");
						col_2.put("type", "number");

						JSONObject col_tooltip = new JSONObject();
						col_tooltip.put("id", 3);
						col_tooltip.put("label", "Tooltip");
						col_tooltip.put("type", "string");
						col_tooltip.put("role", "tooltip");

						JSONObject col_annotation = new JSONObject();
						col_annotation.put("id",4);
						col_annotation.put("label", "Annotation");
						col_annotation.put("type", "string");
						col_annotation.put("role", "annotation");	

						cols.add(col_2);	
						cols.add(col_tooltip);
						cols.add(col_annotation);
					}									
					objCols_Rows.put("cols", cols);
					//stepsArray.add(objCols_Rows);	
					childNumber = 1;
				}else{ //ai passi successivi
					if(previousFirstToken.equals(firstToken)){
						childNumber++;
						doAdd=false;
					}else{
						doAdd=true;
						objCols_Rows = new JSONObject();			
						previousFirstToken = firstToken;
						numCols = storyLengthList.get(storyLenghtCounter);
						storyLenghtCounter++;
						cols = new JSONArray();
						rows = new JSONArray();		
						JSONObject col_1 = new JSONObject();
						col_1.put("id", 1);
						col_1.put("label", "Giorni");
						col_1.put("type", "number");
						cols.add(col_1);
						for(int k=0; k < numCols; k++){
							JSONObject col_2 = new JSONObject();
							col_2.put("id", 2);
							col_2.put("label", "Complication");
							col_2.put("type", "number");

							JSONObject col_tooltip = new JSONObject();
							col_tooltip.put("id", 3);
							col_tooltip.put("label", "Tooltip");
							col_tooltip.put("type", "string");
							col_tooltip.put("role", "tooltip");

							JSONObject col_annotation = new JSONObject();
							col_annotation.put("id",4);
							col_annotation.put("label", "Annotation");
							col_annotation.put("type", "string");
							col_annotation.put("role", "annotation");	

							cols.add(col_2);	
							cols.add(col_tooltip);
							cols.add(col_annotation);
						}
						objCols_Rows.put("cols", cols);
						childNumber = 1;
						optionalInfosArrayOuter.add(optionalInfosArrayInner);
						optionalInfosArrayInner = new JSONArray();
						nodesCounter = 0;
					}			
				}	
				JSONArray steps = (JSONArray) history.get("steps");	
				int previousTime=0;
				int timeToAdd = 0;
				for(int j=0; j < steps.size(); j++){
					JSONObject step = (JSONObject)steps.get(j);
					if(!step.get("label").equals("wait")){
						//*************************** OPTIONAL INFO ***********************
						nodesCounter++;
						if(j==0){
							//JSONObject prevstep = (JSONObject)steps.get(j-1);
							JSONObject optionInfoObj = new JSONObject();
							optionInfoObj.put("idNode",nodesCounter);
							optionInfoObj.put("max", -1);
							optionInfoObj.put("num_classes", step.get("num_classes"));
							optionInfoObj.put("min", -1);
							optionInfoObj.put("25prctile", step.get("25prctile"));
							optionInfoObj.put("75prctile", step.get("75prctile"));	
							optionInfoObj.put("n_pts", step.get("n_pts"));	
							optionInfoObj.put("history", history.get("label"));	
							optionInfoObj.put("step", step.get("label"));	
							JSONArray patientArray = (JSONArray) step.get("patients");
							String patientString="";
							String patientDurationString="";
							for (int p=0; p < patientArray.size(); p++){
								JSONObject patient = (JSONObject) patientArray.get(p);
								patientString = patientString.concat(String.valueOf(patient.get("idcod"))).concat(",");
								patientDurationString = patientDurationString.concat(String.valueOf(patient.get("duration"))).concat(",");	
							}
							optionInfoObj.put("patientString", patientString);
							optionInfoObj.put("patientDurationString", patientDurationString);
							optionalInfosArrayInner.add(optionInfoObj);
						}else{
							JSONObject prevstep = (JSONObject)steps.get(j-1);
							JSONObject optionInfoObj = new JSONObject();
							optionInfoObj.put("idNode",nodesCounter);
							optionInfoObj.put("max", -1);
							optionInfoObj.put("num_classes", step.get("num_classes"));
							optionInfoObj.put("min", -1);
							optionInfoObj.put("25prctile", prevstep.get("25prctile"));
							optionInfoObj.put("75prctile", prevstep.get("75prctile"));	
							optionInfoObj.put("n_pts", prevstep.get("n_pts"));	
							optionInfoObj.put("history", history.get("label"));	
							optionInfoObj.put("step", step.get("label"));	
							JSONArray patientArray = (JSONArray) prevstep.get("patients");
							String patientString="";
							String patientDurationString="";
							for (int p=0; p < patientArray.size(); p++){
								JSONObject patient = (JSONObject) patientArray.get(p);
								patientString = patientString.concat(String.valueOf(patient.get("idcod"))).concat(",");
								patientDurationString = patientDurationString.concat(String.valueOf(patient.get("duration"))).concat(",");	
							}
							optionInfoObj.put("patientString", patientString);
							optionInfoObj.put("patientDurationString", patientDurationString);
							optionalInfosArrayInner.add(optionInfoObj);
						}
						//*****************************************************************
						JSONObject row_obj = new JSONObject();
						JSONArray row_arr = new JSONArray();	
						//la prima la scrivo sempre
						JSONObject row_1 = new JSONObject();
						if(j==0){
							row_1.put("v",0);
						}else{
							JSONObject previousStep = (JSONObject)steps.get(j-1);
							timeToAdd = previousTime+Integer.parseInt(String.valueOf(previousStep.get("time")));
							previousTime= timeToAdd;
							row_1.put("v", timeToAdd);
						}
						row_arr.add(row_1);
						int firstIndex2write = cols.size()-(numCols-(childNumber-1))*3;
						int lastIndex2write = firstIndex2write+3;
						//System.out.println("FirstIndex2write :"+ firstIndex2write+" LastIndex2write: "+lastIndex2write);
						for(int w=1; w <= cols.size(); w++){
							if(w==firstIndex2write){
								JSONObject row_2 = new JSONObject();
								if(j==0){
									row_2.put("v", yStep);
								}else{
									row_2.put("v", yStep*childNumber);
								}
								JSONObject row_tooltip = new JSONObject();
								String labelReplaced = (String) step.get("label");
								labelReplaced = labelReplaced.replaceAll("(.)([A-Z])", "$1 $2");
								if(j==0){
									row_tooltip.put("v", labelReplaced+" \n N.paz: "+step.get("n_pts"));
								}else{
									JSONObject previousStep = (JSONObject)steps.get(j-1);
									row_tooltip.put("v", labelReplaced+" - Days from last step: "+
											previousStep.get("time")+" - Days from first step: "+timeToAdd+"\n N.paz: "+previousStep.get("n_pts"));
								}								
								JSONObject row_annotation = new JSONObject();
								
								row_annotation.put("v", labelReplaced);									
								row_arr.add(row_2);
								row_arr.add(row_tooltip);
								row_arr.add(row_annotation);
							}else if (w>lastIndex2write || w <firstIndex2write){
								JSONObject rowNull = new JSONObject();
								rowNull.put("v", null);	
								row_arr.add(rowNull);
							}
						}						
						//Attacco altre righe					
						row_obj.put("c",row_arr);
						rows.add(row_obj);	
					}
				}	
				if(doAdd){
					objCols_Rows.put("rows", rows);	
					stepsArray.add(objCols_Rows);	
				}
			}//Fine ciclo storie
			optionalInfosArrayOuter.add(optionalInfosArrayInner); //attacco l'ultimo
			jsonResultContainer.put("complicationChart", stepsArray);	
			jsonResultContainer.put("optionalInfo", optionalInfosArrayOuter);		
			jsonResultContainer.put("historyOneStepArray", historyOneStepArray);
			StringWriter out = new StringWriter();
			jsonResultContainer.writeJSONString(out);
			result = out.toString();
			//System.out.println(result);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	private String getInfo4Trafficlights(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		HashMap<String, Integer> trafficLightMap = new HashMap<String, Integer>();
		trafficLightMap.put("PAT|LOC:S", 0);
		trafficLightMap.put("PAT|LOC:1", 1);
		trafficLightMap.put("PAT|LOC:2", 1);
		trafficLightMap.put("PAT|LOC:3", 2);
		trafficLightMap.put("CON|DIE:B", 2);
		trafficLightMap.put("CON|DIE:G", 0);
		trafficLightMap.put("PAT|CVR:I", 0);
		trafficLightMap.put("PAT|CVR:II", 1);
		trafficLightMap.put("PAT|CVR:III", 1);
		trafficLightMap.put("PAT|CVR:IV", 2);
		trafficLightMap.put("PAT|CVR:V", 2);
		trafficLightMap.put("PAT|CVR:VI", 2);
		trafficLightMap.put("CON|PHI:I", 0);
		trafficLightMap.put("CON|PHI:L", 1);
		trafficLightMap.put("CON|PHI:M", 0);
		trafficLightMap.put("CON|PHI:N", 2);

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			JSONObject objResult = new JSONObject();
			JSONArray myResultsArray = new JSONArray();
			SimpleDateFormat simpleDF = new SimpleDateFormat("dd/MM/yyyy");
			
			//Cerco la data di nascita
			Date birthDate = new Date();
			String sqlDOB = "select birth_date from patient_dimension "+
					"where patient_num= " +patientId;
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sqlDOB);
			rs = pstmt.executeQuery();
			while(rs.next()){	
				birthDate = rs.getDate("birth_date");
			}
			pstmt.close();
			rs.close();
			conn.close();
			
			//cerco l'ultima visita
			Date lastVisitDate = new Date();
			String sql3 = "select PATIENT_NUM, max(start_date) as my_date " +
					"from VISIT_DIMENSION " +
					"where patient_num= " +patientId+" and sourcesystem_cd like ? group by patient_num";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql3);
			pstmt.setString(1, prop.getProperty("visit_sourcesystem_cd"));
			rs = pstmt.executeQuery();
			while(rs.next()){	
				String myDate = simpleDF.format( rs.getDate("my_date"));
				lastVisitDate = rs.getDate("my_date");
				objResult.put("last_visit", myDate);
			}

			pstmt.close();
			rs.close();
			conn.close();

			String sql = "select * from (select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date,  cast(q1.observation_blob as varchar2(2000)) as my_value, 'LOC' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2) "+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, q1.tval_char as my_value, 'DIET' as my_id  " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD = ? or q1.CONCEPT_CD = ?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2)"+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.observation_blob as varchar2(2000)) as my_value, 'CVR' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2)"+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, q1.tval_char as my_value, 'PHISICAL_ACTIVITY' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2)"+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'BMI' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2)"+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'BLOOD_PRESS_SYSTOLIC' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2)"+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'BLOOD_PRESS_DIASTOLIC' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2)"+
					"union "+
					"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'Hba1c' as my_id  " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2) "+

"union "+
"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'MICROVASCULAR_RISK_RETINOPATY' as my_id  " +
"from "+observationTable+" q1 " +
"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2) "+

"union "+
"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'MICROVASCULAR_RISK_NEUROPATY' as my_id  " +
"from "+observationTable+" q1 " +
"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2) "+

"union "+
"select * from (select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, cast(q1.nval_num as varchar2(2000)) as my_value, 'MICROVASCULAR_RISK_NEPHROPATY' as my_id  " +
"from "+observationTable+" q1 " +
"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
"order by q1.PATIENT_NUM, q1.START_DATE desc ) where rownum in (1,2) "+
") order by my_id, start_date desc";


			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			pstmt.setString(2, prop.getProperty("diet_good"));
			pstmt.setString(3, prop.getProperty("diet_bad"));
			pstmt.setString(4, prop.getProperty("cardiovascular_risk"));
			pstmt.setString(5, prop.getProperty("phisical_activity"));
			pstmt.setString(6, prop.getProperty("bmi"));
			pstmt.setString(7, prop.getProperty("systolic_bp"));
			pstmt.setString(8, prop.getProperty("diastolic_bp"));
			pstmt.setString(9, prop.getProperty("hba1c"));
			pstmt.setString(10, prop.getProperty("micro_vascular_risk_retinopaty"));
			pstmt.setString(11, prop.getProperty("micro_vascular_risk_neuropaty"));
			pstmt.setString(12, prop.getProperty("micro_vascular_risk_nephropaty"));

			rs = pstmt.executeQuery();
			while(rs.next()){
				String myDate = simpleDF.format( rs.getDate("start_date"));
				if(rs.getString("my_id").equals("BMI")){
					Float bmiValue = Float.parseFloat( rs.getString("my_value").replaceAll(",", "."));
					int category = 0;
					if(bmiValue<=18.49){
						category=1;
					}else if(bmiValue>18.49 && bmiValue<=24.9){
						category=0;
					}else if(bmiValue>24.9 && bmiValue<=29.9){
						category=1;
					}else if(bmiValue>29.9){
						category=2;
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date",myDate);
					objLOC.put("obs_value",  rs.getString("my_value"));
					objLOC.put("category",category);
					myResultsArray.add(objLOC);
				}else if(rs.getString("my_id").equals("BLOOD_PRESS_SYSTOLIC")){
					int bpValue = Integer.parseInt( rs.getString("my_value"));
					int category = 0;
					if(bpValue<90){
						category=1;
					}else if(bpValue>=90 && bpValue<=119){
						category=0;
					}else if(bpValue>119 && bpValue<=139){
						category=1;
					}else if(bpValue>=140){
						category=2;
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date",myDate);
					objLOC.put("obs_value",  rs.getString("my_value"));
					objLOC.put("category",category);
					myResultsArray.add(objLOC);
				}else if(rs.getString("my_id").equals("BLOOD_PRESS_DIASTOLIC")){
					int bpValue = Integer.parseInt( rs.getString("my_value"));
					int category = 0;
					if(bpValue<60){
						category=1;
					}else if(bpValue>=60 && bpValue<=79){
						category=0;
					}else if(bpValue>79 && bpValue<=89){
						category=1;
					}else if(bpValue>=90){
						category=2;
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date",myDate);
					objLOC.put("obs_value",  rs.getString("my_value"));
					objLOC.put("category",category);
					myResultsArray.add(objLOC);
				}else if(rs.getString("my_id").equals("Hba1c")){
					float hba1cValue = Float.parseFloat(rs.getString("my_value").replaceAll(",", "."));
					int category =0;
					long lastVisitMillis = lastVisitDate.getTime();
					long birthDateMillis = birthDate.getTime();
					long differenceMillis = lastVisitMillis-birthDateMillis;
					double anniTraDueDate = differenceMillis / (86400000.0*365.0);
					//System.out.println("Patient Dates: birtDate "+birthDate+" lastVisit "+lastVisitDate+" diff: "+anniTraDueDate);
					if(anniTraDueDate<70.00){
						//System.out.println("Patient minore 70");
						if(hba1cValue<=53){
							category=0;
						}else if(hba1cValue>53 && hba1cValue<=64){
							category=1;
						}else if(hba1cValue>64){
							category=2;
						}
					}else{
						//System.out.println("Patient maggiore 70");
						if(hba1cValue<=64){
							category=0;
						}else if(hba1cValue>64 && hba1cValue<=75){
							category=1;
						}else if(hba1cValue>75){
							category=2;
						}
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date", myDate);
					objLOC.put("obs_value", rs.getString("my_value"));
					objLOC.put("category", category);
					myResultsArray.add(objLOC);
					//TODO
				}else if(rs.getString("my_id").equals("MICROVASCULAR_RISK_RETINOPATY")){
					Float mvr_R_Value = Float.parseFloat( rs.getString("my_value").replaceAll(",", "."));
					int category = 0;
					if(mvr_R_Value<=0.300){
						category=0;
					}else if(mvr_R_Value>0.300 && mvr_R_Value<=0.500){
						category=1;
					}else if(mvr_R_Value>0.500){
						category=2;
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date",myDate);
					objLOC.put("obs_value",  rs.getString("my_value"));
					objLOC.put("category",category);
					myResultsArray.add(objLOC);
				}else if(rs.getString("my_id").equals("MICROVASCULAR_RISK_NEUROPATY")){
					Float mvr_Nu_Value = Float.parseFloat( rs.getString("my_value").replaceAll(",", "."));
					int category = 0;
					if(mvr_Nu_Value<=0.300){
						category=0;
					}else if(mvr_Nu_Value>0.300 && mvr_Nu_Value<=0.500){
						category=1;
					}else if(mvr_Nu_Value>0.500){
						category=2;
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date",myDate);
					objLOC.put("obs_value",  rs.getString("my_value"));
					objLOC.put("category",category);
					myResultsArray.add(objLOC);
				}else if(rs.getString("my_id").equals("MICROVASCULAR_RISK_NEPHROPATY")){
					Float mvr_Ne_Value = Float.parseFloat( rs.getString("my_value").replaceAll(",", "."));
					int category = 0;
					if(mvr_Ne_Value<=0.300){
						category=0;
					}else if(mvr_Ne_Value>0.300 && mvr_Ne_Value<=0.500){
						category=1;
					}else if(mvr_Ne_Value>0.500){
						category=2;
					}
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date",myDate);
					objLOC.put("obs_value",  rs.getString("my_value"));
					objLOC.put("category",category);
					myResultsArray.add(objLOC);
				}else{
					JSONObject objLOC = new JSONObject();
					objLOC.put("id", rs.getString("my_id"));
					objLOC.put("obs_date", myDate);
					objLOC.put("obs_value", rs.getString("my_value"));
					objLOC.put("category", trafficLightMap.get(rs.getString("concept_cd")));
					myResultsArray.add(objLOC);
				}
				//System.out.println(rs.getString("my_id")+" "+myDate+" "+ rs.getString("my_value"));
			}
			objResult.put("traffic_light_array", myResultsArray);
			pstmt.close();
			rs.close();
			conn.close();

			//Ultima visita e year(onset)
			String sql2 = "select q1.PATIENT_NUM, q1.concept_cd, q1.start_date, q1.nval_num " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql2);
			pstmt.setString(1, prop.getProperty("year_of_diagnosis"));
			rs = pstmt.executeQuery();
			while(rs.next()){	
				objResult.put("onset_year", rs.getInt("nval_num"));
			}

			pstmt.close();
			rs.close();
			conn.close();

			//Ricerco le tre comorbidità associate ai calcolatori del rischio (Retinopatia, Neuropatia, Nefropatia)
			String sql4 = "select q1.PATIENT_NUM, q1.concept_cd, q1.start_date " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD=? or q1.CONCEPT_CD=? or q1.CONCEPT_CD=?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql4);
			pstmt.setString(1, prop.getProperty("com_mic_ret"));
			pstmt.setString(2, prop.getProperty("com_mic_neph"));
			pstmt.setString(3, prop.getProperty("com_mic_neu"));
			rs = pstmt.executeQuery();
			while(rs.next()){	
				String myDate = simpleDF.format( rs.getDate("start_date"));
				if(rs.getString("concept_cd").equals(prop.getProperty("com_mic_ret"))){
					objResult.put("retinopaty",myDate);
				}else if(rs.getString("concept_cd").equals(prop.getProperty("com_mic_neph"))){
					objResult.put("nephropaty", myDate);
				}else if(rs.getString("concept_cd").equals(prop.getProperty("com_mic_neu"))){
					objResult.put("neuropaty", myDate);
				}			
			}


			StringWriter swout = new StringWriter();
			objResult.writeJSONString(swout);
			jsonText = swout.toString();
			//			System.out.println("TRAFFIC LIGHTS");
			//		System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2TimeToComplication(){
		//System.out.println("DO TIME TO COMPLICATION");

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			JSONObject generalComorbObj = getTimeToComplicationJson(prop.getProperty("comorbidity"), prop.getProperty("first_visit"));
			JSONObject amiComorbObj = getTimeToComplicationJson(prop.getProperty("com_mac_ami"), prop.getProperty("first_visit"));
			JSONObject angComorbObj = getTimeToComplicationJson(prop.getProperty("com_mac_ang"), prop.getProperty("first_visit"));
			JSONObject cihdComorbObj = getTimeToComplicationJson(prop.getProperty("com_mac_cihd"), prop.getProperty("first_visit"));
			JSONObject occComorbObj = getTimeToComplicationJson(prop.getProperty("com_mac_occ"), prop.getProperty("first_visit"));
			JSONObject paodComorbObj = getTimeToComplicationJson(prop.getProperty("com_mac_paod"), prop.getProperty("first_visit"));
			JSONObject strComorbObj = getTimeToComplicationJson(prop.getProperty("com_mac_str"), prop.getProperty("first_visit"));
			JSONObject dfComorbObj = getTimeToComplicationJson(prop.getProperty("com_nv_df"), prop.getProperty("first_visit"));
			JSONObject nephComorbObj = getTimeToComplicationJson(prop.getProperty("com_mic_neph"), prop.getProperty("first_visit"));
			JSONObject retComorbObj = getTimeToComplicationJson(prop.getProperty("com_mic_ret"), prop.getProperty("first_visit"));
			JSONObject fldComorbObj = getTimeToComplicationJson(prop.getProperty("com_nv_fld"), prop.getProperty("first_visit"));
			JSONObject neuComorbObj = getTimeToComplicationJson(prop.getProperty("com_mic_neu"), prop.getProperty("first_visit"));


			JSONObject outerObj = new JSONObject();

			outerObj.put("generic_complication", generalComorbObj);
			outerObj.put("ami_complication", amiComorbObj);
			outerObj.put("ang_complication", angComorbObj);
			outerObj.put("cihd_complication", cihdComorbObj);
			outerObj.put("occ_complication", occComorbObj);
			outerObj.put("paod_complication", paodComorbObj);
			outerObj.put("str_complication", strComorbObj);
			outerObj.put("df_complication", dfComorbObj);
			outerObj.put("neph_complication", nephComorbObj);
			outerObj.put("ret_complication", retComorbObj);
			outerObj.put("fld_complication", fldComorbObj);
			outerObj.put("neu_complication", neuComorbObj);
			StringWriter out = new StringWriter();
			outerObj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}

		return jsonText;
	}

	private JSONObject getTimeToComplicationJson (String conceptCd, String visitConceptCd){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HashMap<Integer, String> histLabelMap = new HashMap<Integer, String>();
		HashMap<Integer, Integer> histFreqMap = new HashMap<Integer, Integer>();
		JSONObject objHist = new JSONObject();

		try {

			String sql = "select mincom.*, f.patient_num, f.start_date, (mincom.mindate - f.start_date) as mydiff from "+
					"(select min(start_date) as mindate, patient_num from "+observationTable+
					" where concept_cd like ? group by patient_num) mincom, "+observationTable+" f "+
					"where mincom.patient_num = f.patient_num and f.concept_cd like ? order by mydiff desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, conceptCd);
			pstmt.setString(2, visitConceptCd);

			rs = pstmt.executeQuery();
			JSONArray colsHist= new JSONArray();
			JSONObject col_1Hist = new JSONObject();
			col_1Hist.put("id", 1);
			col_1Hist.put("label", "Days");
			col_1Hist.put("type", "string");
			JSONObject col_2Hist = new JSONObject();
			col_2Hist.put("id", 2);
			col_2Hist.put("label", "Patients");
			col_2Hist.put("type", "number");
			colsHist.add(col_1Hist);
			colsHist.add(col_2Hist);
			objHist.put("cols", colsHist);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				double distanceYears = rs.getDouble("mydiff")/(double)365;
				String histLabel = "";
				int histCategory = 0;
				if(distanceYears>0){
					histCategory = (int) Math.ceil(distanceYears);
					histLabel = String.valueOf(histCategory).concat(" after");
				}else if(distanceYears < 0){
					histCategory = (int) Math.floor(distanceYears);
					histLabel = String.valueOf(Math.abs(histCategory)).concat(" before");
				}else if(distanceYears==0){
					histCategory = 1;
					histLabel = String.valueOf(histCategory).concat(" after");
				}

				if(histLabelMap.containsKey(histCategory)){
					//etichetta esistente: aggiorno quantità
					int freqValue = histFreqMap.get(histCategory);
					freqValue = freqValue+1;
					histFreqMap.put(histCategory, freqValue);
				}else{
					//nuova etichetta
					histLabelMap.put(histCategory, histLabel);
					histFreqMap.put(histCategory, 1);					
				}
				//System.out.println("Result "+rs.getInt("mydiff")+" - "+distanceYears+" - "+histLabel );
			}

			Set<Integer> keySet = histLabelMap.keySet();
			List<Integer> list = new ArrayList(keySet);
			Collections.sort(list);
			for(Integer mykey : list){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", histLabelMap.get(mykey));

				JSONObject row_2 = new JSONObject();
				row_2.put("v", histFreqMap.get(mykey));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
				objHist.put("rows", rows);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		finally{

			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return objHist;
	}
}


