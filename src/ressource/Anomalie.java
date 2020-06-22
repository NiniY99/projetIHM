package ressource;

import java.util.*;

public class Anomalie {
	private Map<Integer, Annee> listeAnnee;
	private float anomalieMax;
	private float anomalieMin;
	
	public Anomalie() {
		anomalieMax = Float.MIN_VALUE;
		anomalieMin = Float.MAX_VALUE;
		listeAnnee = new HashMap<Integer, Annee>();
	}
	
	public void ajoutAnnee(int a, Annee annee) {
		listeAnnee.put(a, annee);
	}
	
	public ArrayList<Float> ListeAnomalieParAnnee(int a){
		Annee anne = listeAnnee.get(a);
		return anne.getListeTempParCoord();
	}
	
	public Map<Integer, Annee> getListeAnnee() {
		return listeAnnee;
	}

	public float getTempAnomalie (int a, String coord) throws ExceptionNonContains {
		Annee anne = listeAnnee.get(a);
		return anne.getTemp(coord);
	}
	
	public ArrayList<Float> ListeAnomalieParCoord(String coord) throws ExceptionNonContains {
		ArrayList<Float> l = new ArrayList<Float>();
		/*for (Map.Entry mapentry : listeAnnee.entrySet()){
			Annee annee = (Annee) mapentry.getValue();
			float d = annee.getTemp(coord);
			l.add(d);
		}
		*/
		for(int year = 1880; year < 2021; year++) {
			Annee annee = (Annee)listeAnnee.get(year);
			float d = annee.getTemp(coord);
			l.add(d);
		}
		return l;
	}
	
	public ArrayList<Integer> ListeToutesAnnee(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Map.Entry mapentry : listeAnnee.entrySet()){
			int annee = (int) mapentry.getKey();
			list.add(annee);
		}
		return list;
	}

	public float getAnomalieMax() {
		return anomalieMax;
	}

	public void setAnomalieMax(float anomalieMax) {
		this.anomalieMax = anomalieMax;
	}

	public float getAnomalieMin() {
		return anomalieMin;
	}

	public void setAnomalieMin(float anomalieMin) {
		this.anomalieMin = anomalieMin;
	}

	
	
}
