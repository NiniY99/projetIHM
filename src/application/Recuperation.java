package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ressource.Annee;
import ressource.Anomalie;

public class Recuperation {

	public static Anomalie getDataFromCSVFile(String csvFilePath) {
		String coord;
		float temp;
		int year;
		Anomalie a = new Anomalie();
        String[] data = null;
        float max = Float.MIN_VALUE;;
        float min = Float.MAX_VALUE;
        
        
        try {
        	FileReader file = new FileReader(csvFilePath);
        	BufferedReader bufferedReader = new BufferedReader(file);
        	
        	String line = bufferedReader.readLine();
        	data = line.replace("\"", "").split(",");
        	for(int i = 2; i<data.length; i++) {
        		year = Integer.parseInt(data[i]);
        		Annee annee  = new Annee(year);
        		a.ajoutAnnee(year, annee);
        		
        	}
        	while((line =bufferedReader.readLine()) != null) {
        		//line = bufferedReader.readLine();
        		data = line.split(",");
        		coord = data[0] +","+ data[1];
        		int i = 2;
        		Map<Integer, Annee> map = a.getListeAnnee();
        		for (Map.Entry mapentry : map.entrySet()){

        			try {
        				temp = Float.parseFloat(data[i]);
        				if(temp < min) {
        					min = temp;
        				}
        				if(temp > max) {
        					max = temp;
        				}
        			}catch(Exception exception) {
        				
        				temp = Float.NaN;
        			}
        			((Annee) mapentry.getValue()).ajoutCoord(coord, temp);
        				i += 1;

        		}
        	}
        	a.setAnomalieMax(max);
        	a.setAnomalieMin(min);
        	
        	
        	
        }catch (IOException e) {
        		e.printStackTrace();
        }
        
        return a;

	}
}
