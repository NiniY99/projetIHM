package application;

import java.io.File;
import java.util.ArrayList;

import ressource.Anomalie;
import ressource.ExceptionNonContains;

public class ModelEarth {

	private Anomalie anomalieTemp;
	
	public ModelEarth() {
		
			anomalieTemp = Recuperation.getDataFromCSVFile("src/tempanomaly_4x4grid.csv");
	}
	
	public ArrayList<Integer> getListeAnnee() {
		return anomalieTemp.ListeToutesAnnee();
	}
	
	public float getTemp(int a, String coord) throws ExceptionNonContains {
		return anomalieTemp.getTempAnomalie(a, coord);
	}
	
	public ArrayList<Float> getAnomalieParCoord(String coord) throws ExceptionNonContains {
		return anomalieTemp.ListeAnomalieParCoord(coord);
	}
	
	public ArrayList<Float> getAnomalieParAnnee(int a){
		return anomalieTemp.ListeAnomalieParAnnee(a);
	}
	
	public float getTempMin() {
		return anomalieTemp.getAnomalieMin();
	}
	
	public float getTempMax() {
		return anomalieTemp.getAnomalieMax();
	}
	
}
