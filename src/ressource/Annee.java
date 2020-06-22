package ressource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Annee {
	private int annee;
	private Map<String, Float> coordTemp;
	
	public Annee(int annee) {
		this.annee = annee;
		coordTemp = new HashMap<String, Float>();
	}
	
	public void ajoutCoord(String coord, float temp) {
		coordTemp.put(coord, temp);
	}
	
	public float getTemp(String coord) throws ExceptionNonContains{
		//float temp = (float)coordTemp.get(coord);
		if(!coordTemp.containsKey(coord)) {
			throw new ExceptionNonContains("coordonnee recherche n'existe pas : " + coord);
		}
		if(Float.isNaN(coordTemp.get(coord))) {
			return Float.NaN;
		}
		
		return coordTemp.get(coord);
	}

	public ArrayList<String> getZoneConnu() {
		ArrayList<String> l = new ArrayList<String>();
		for (Map.Entry mapentry : coordTemp.entrySet()){
			l.add((String) mapentry.getKey());
		}
		return l;
	}
	
	public ArrayList<Float> getListeTempParCoord(){
		ArrayList<Float> list = new ArrayList<Float>();
		for(int latitude = -90; latitude < 90; latitude += 4) {
        	for(int longitude = -180; longitude < 180; longitude += 4) {
        		String lat = Integer.toString(latitude);
        		String lon = Integer.toString(longitude);
        		String localisation = lat+","+lon;
        		Float f = coordTemp.get(localisation);
        		list.add(f);
        		
        	}
		}
		return list;
	}
	
}
