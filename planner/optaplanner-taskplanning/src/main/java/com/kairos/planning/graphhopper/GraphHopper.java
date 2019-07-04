package com.kairos.planning.graphhopper;

import com.graphhopper.directions.api.client.ApiException;
import com.graphhopper.directions.api.client.api.GeocodingApi;
import com.graphhopper.directions.api.client.api.MatrixApi;
import com.graphhopper.directions.api.client.model.GeocodingResponse;
import com.graphhopper.directions.api.client.model.MatrixResponse;
import com.kairos.planning.domain.Location;
import com.kairos.planning.domain.LocationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//@PropertySource("classpath:taskplanner.properties")
public class GraphHopper {
	Logger log = LoggerFactory.getLogger(GraphHopper.class);

	private String matrixKey = "7ba37151-7141-4ea4-a89f-28a2ae297a0a";


	public void getLocationData(List<Location> locations) {
		//getLatLongByAddress("");
		MatrixResponse response = getMatrixData(locations);
		assignLocationDataWithDistance(response, locations);
	}

	public MatrixResponse getMatrixData(List<Location> locations) {
		MatrixApi api = new MatrixApi();
		List<String> point = getLatsLongsFromLocations(locations);
		String fromPoint = null;
		String toPoint = null;
		List<String> requiredFields = Arrays.asList("weights", "distances", "times");
		String vehicle = "car";
		MatrixResponse response = null;
		try {
			response =  api.matrixGet(getApiKey(), point, fromPoint, toPoint, requiredFields, vehicle);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return response;
	}

	private void assignLocationDataWithDistance(MatrixResponse response, List<Location> locations) {
		for (int i = 0; i < response.getDistances().size(); i++) {
			List<LocationInfo> locationInfos = new ArrayList<>();
			for (int k = 0; k < response.getDistances().get(i).size(); k++) {
				if(i==k) continue;
				//if (!response.getDistances().get(i).get(k).equals(BigDecimal.ZERO)) {
					LocationInfo locationInfo = new LocationInfo();
					locationInfo.setDistance(Math.ceil((response.getDistances().get(i).get(k).doubleValue())/1000.0d));
					locationInfo.setTime(response.getTimes().get(i).get(k).doubleValue());
					locationInfo.setName(locations.get(k).getName());
					locationInfo.setLocationId(locations.get(k).getId());
					locationInfos.add(locationInfo);
				//}
			}
			locations.get(i).setLocationInfos(locationInfos);
		}
	}
	
	public List<Double> getLatLongByAddress(String address){
		GeocodingApi geoApi = new GeocodingApi();
		address = "DK,Odense,Østergade,45 D";
		GeocodingResponse response;
		try {
			response = geoApi.geocodeGet(getApiKey(), address, "en", 1, false, "", "default");
			log.info("latitude "+response.getHits().get(0).getPoint().getLat()+" longitude "+response.getHits().get(0).getPoint().getLat());
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getLatsLongsFromLocations(List<Location> locations) {
		List<String> allPoints = new ArrayList<>();
		for (Location location : locations) {
			allPoints.add(location.getLatitude() + "," + location.getLongitude());
		}
		return allPoints;
	}

	public String getApiKey() {
		  /*Properties prop = new Properties(); 
		  try { 
			  prop.load(new FileInputStream(new File("/media/pradeep/bak/multiOpta/task-planner/src/main/resources/taskplanner.properties")));
		 String key = prop.getProperty("graphhopper.key"); 
		 
		  } catch (IOException e) { e.printStackTrace(); }*/
		 
		log.info(matrixKey);
		return matrixKey;
	}

}
