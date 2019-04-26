package com.kairos.shiftplanning.domain.staff;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("Location")
public class Location {
	private Long id;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	protected String name;
    protected double latitude;
    protected double longitude;
    private List<LocationInfo> locationInfos;
    
    
    
    public List<LocationInfo> getLocationInfos() {
		return locationInfos;
	}

	public void setLocationInfos(List<LocationInfo> locationInfos) {
		this.locationInfos = locationInfos;
	}

	public Location() {
    }

    public Location(Long id,String name, double latitude, double longitude) {
        this.id=id;
    	this.name=name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public LocationInfo getLocationInfo(Long locationId){
    	for (LocationInfo locationInfo : locationInfos) {
    		if(locationInfo.getLocationId().equals(locationId))return locationInfo;
		}
    	return null;
    }
    /*
    In Kms
     */
	public long getDistanceFrom(Location otherLocation) {
		/*double latitudeDifference = location.latitude - latitude;
        double longitudeDifference = location.longitude - longitude;
        *//*long distance = new Double(Math.sqrt(
                (latitudeDifference * latitudeDifference) + (longitudeDifference * longitudeDifference))).longValue();*//*
        long distance= distance(location.latitude,latitude,location.longitude,longitude,0,0);
        return distance;*/
        if(otherLocation.getId().equals(this.getId())) return 0;
        LocationInfo otherLocationInfo = null;
        for(LocationInfo info:getLocationInfos()){
            if(info.getLocationId().equals(otherLocation.getId())){
                otherLocationInfo=info;
                break;
            }
        }
        return (long)otherLocationInfo.getDistance();

	}
	public String toString(){
		return name+"-"+latitude+"-"+longitude;
	}


    public  long distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //double distance = R * c * 1000; // convert to meters
        double distance = R * c;

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return new Double(Math.sqrt(distance)).longValue();
    }
}
