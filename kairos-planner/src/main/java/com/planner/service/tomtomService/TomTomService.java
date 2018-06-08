package com.planner.service.tomtomService;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.planner.domain.location.LocationDistance;
import com.planner.domain.tomtomResponse.Matrix;
import com.planner.domain.tomtomResponse.TomTomResponse;
import com.planner.repository.locationRepository.TomTomRepository;
import com.planner.service.locationService.LocationService;
import com.planner.service.taskService.TaskService;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class TomTomService {

    @Autowired
    private TaskService taskService;
    @Autowired private LocationService locationService;
    @Autowired private TomTomRepository tomTomRepository;

    public void getLocationData() {
        List<Task> tasks = taskService.getUniqueTask();
        List<List<Task>> subTaskList = getSubLists(tasks);
        int i = 0;
        for (List<Task> origin : subTaskList) {
            String request = getRequestMap(origin, tasks);
            i = i+5;
            System.out.println(i);
            /*try {
                PrintWriter out = new PrintWriter(new File(System.getProperty("user.dir") + "/location"));
                out.write(request);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            TomTomResponse response = submitToTomtom(request);
            tomTomRepository.save(response);
            List<LocationDistance> locationDistances = processLocationDistance(response,origin,tasks);
            locationService.saveLocation(locationDistances);
            break;
        }

    }

    private List<LocationDistance> processLocationDistance(TomTomResponse response,List<Task> origin,List<Task> destination){
        List<LocationDistance> locationDistances = new ArrayList<>();
        for(int i=0;i<origin.size();i++){
            List<Matrix> matrices = response.getMatrix().get(i);
            for (int j=0;j<destination.size();j++){
                Matrix matrix = matrices.get(j);
                locationDistances.add(new LocationDistance(origin.get(i).getIntallationNo(),destination.get(j).getIntallationNo(),(double)matrix.getResponse().getRouteSummary().getLengthInMeters(),(double)matrix.getResponse().getRouteSummary().getTravelTimeInSeconds()));
            }
        }
        return locationDistances;
    }


    private String getRequestMap(List<Task> fromTasks, List<Task> toTask) {
        Map<String, List<Map>> request = new HashMap<>();
        List<Map> points = new ArrayList<>();
        for (Task fromTask : fromTasks) {
            Map<String, Double> latLong = new HashMap<>();
            Map<String, Map> point = new HashMap<>();
            latLong.put("latitude", fromTask.getLattitude());
            latLong.put("longitude", fromTask.getLongitude());
            point.put("point", latLong);
            points.add(point);
        }
        request.put("origins", points);
        points = new ArrayList<>();
        for (Task task : toTask) {
            Map<String, Double> latLong = new HashMap<>();
            Map<String, Map> point = new HashMap<>();
            latLong.put("latitude", task.getLattitude());
            latLong.put("longitude", task.getLongitude());
            point.put("point", latLong);
            points.add(point);
        }
        request.put("destinations", points);
        return ObjectMapperUtils.objectToJsonString(request);
    }

    List<List<Task>> getSubLists(List<Task> tasks) {
        List<List<Task>> subTaskList = new ArrayList<>();
        int i = 0;
        while (true) {
            int j = i + 5;
            if (j >= tasks.size()) {
                j = tasks.size();
                subTaskList.add(tasks.subList(i, j));
                break;
            }
            subTaskList.add(tasks.subList(i, j));
            i = j;
        }
        return subTaskList;
    }

    private TomTomResponse submitToTomtom(String requestBody) {
        HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder = null;
        try {
            builder = new URIBuilder("https://api.tomtom.com/routing/1/matrix/json");
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("key", "pbSlE13OWkGMuPbTGM9XciGo0es4dGik"));
            params.add(new BasicNameValuePair("routeType", "shortest"));
            params.add(new BasicNameValuePair("travelMode", "car"));
            builder.setParameters(params);
            HttpPost httppost = new HttpPost(builder.build());
            httppost.setHeader("Content-Type","application/json");
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httppost.setEntity(new ByteArrayEntity(requestBody.getBytes("UTF-8")));
            HttpResponse response = httpclient.execute(httppost);
            ObjectMapper mapper = new ObjectMapper();
            TomTomResponse tomTomResponse = null;
            if(response.getHeaders("Location")!=null && tomTomResponse==null && response.getStatusLine().getStatusCode()==202){
                try {
                    Thread.sleep(180000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Header header[] = response.getHeaders("Location");
                tomTomResponse = getfromTomtom(header[0].getValue());
            }else {
                tomTomResponse = mapper.readValue(response.getEntity().getContent(), TomTomResponse.class);
            }
            return tomTomResponse;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TomTomResponse getfromTomtom(String headersUrl) {
        HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder = null;
        try {
            builder = new URIBuilder("https://api.tomtom.com"+headersUrl);
            //List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            //params.add(new BasicNameValuePair("key", "1FR9KYuSkBlEfgpCKpAZxadwQtaIGRg6"));
            //params.add(new BasicNameValuePair("routeType", "shortest"));
            //params.add(new BasicNameValuePair("travelMode", "car"));
            //builder.setParameters(params);
            HttpGet httppost = new HttpGet(builder.build());
            httppost.setHeader("Content-Type","application/json");
            //httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            //httppost.setEntity(new ByteArrayEntity(requestBody.getBytes("UTF-8")));
            HttpResponse response = httpclient.execute(httppost);
            ObjectMapper mapper = new ObjectMapper();
            TomTomResponse tomTomResponse = mapper.readValue(response.getEntity().getContent(), TomTomResponse.class);
            return tomTomResponse;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
