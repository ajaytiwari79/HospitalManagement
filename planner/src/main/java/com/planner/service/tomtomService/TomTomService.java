package com.planner.service.tomtomService;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.util.ObjectUtils;
import com.kairos.planner.vrp.taskplanning.model.LocationPair;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.kairos.planner.vrp.taskplanning.routes.Route;
import com.kairos.planner.vrp.taskplanning.routes.RouteInfo;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.planner.domain.location.LocationDistance;
import com.planner.domain.tomtomResponse.AtoBRoute;
import com.planner.domain.tomtomResponse.Matrix;
import com.planner.domain.tomtomResponse.TomTomResponse;
import com.planner.repository.locationRepository.AtoBRouteRepository;
import com.planner.repository.locationRepository.TomTomRepository;
import com.planner.service.locationService.LocationService;
import com.planner.service.taskService.TaskService;
import com.planner.util.executor.ExecutorUtil;
import com.planner.util.executor.runnables.TomtomRouteRunnable;
import org.apache.http.Header;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class TomTomService {
    private static final String ARRIVE_RIGHT ="ARRIVE_RIGHT" ;
    private static Logger log= LoggerFactory.getLogger(TomTomService.class);
    @Autowired
    private TaskService taskService;
    @Autowired private LocationService locationService;
    @Autowired private TomTomRepository tomTomRepository;
    @Autowired private AtoBRouteRepository atoBRouteRepository;

    int key = 0;

    String [] keys = {"xvb1VJX66SrM187drGq52jrAo5wo1kRL","pxlAcwIFHNCPKpVODeMlmew1qoLx9ZGx","I0JgGGfWVTAipGAoCKyzwLTQ6RAaYqGF","nUocn0Xi8thXJZYGHYVbSIxpxCzK6Jca"};
    //String [] newKeys ={"MlRZQYS39ql0SysefVIcbLoud7nbw6dg"};
    String [] newKeys ={"rGNzk0sexiCRFZnFSEkSwPHvVuCpSA14"};

    public void getLocationData() {
        List<Task> tasks = taskService.getUniqueTask().stream().filter(ObjectUtils.distinctByKey(task -> task.getLatitude()+task.getLongitude())).collect(toList());
        List<List<Task>> subTaskList = getSubLists(tasks.subList(96,tasks.size()));
        //int i = 0;
        for (List<Task> origin : subTaskList) {
            String request = getRequestMap(origin, tasks);

            TomTomResponse response = submitToTomtomForMatrix(request);
            //i++;
           // tomTomRepository.save(response);
            List<LocationDistance> locationDistances = processLocationDistance(response,origin,tasks);
            locationService.saveLocation(locationDistances);
            //if(i==2){
                key++;
            /*    i=0;
            }*/
            //break;
        }


    }

    private List<LocationDistance> processLocationDistance(TomTomResponse response,List<Task> origin,List<Task> destination){
        List<LocationDistance> locationDistances = new ArrayList<>();
        for(int i=0;i<origin.size();i++){
            List<Matrix> matrices = response.getMatrix().get(i);
            for (int j=0;j<destination.size();j++){
                Matrix matrix = matrices.get(j);
                matrix.setFirstLatitude(origin.get(i).getLatitude());
                matrix.setFirstLongitude(origin.get(i).getLongitude());
                matrix.setSecondLattitude(destination.get(j).getLatitude());
                matrix.setSecondLongitude(destination.get(j).getLongitude());
                locationDistances.add(new LocationDistance(origin.get(i).getInstallationNo(),destination.get(j).getInstallationNo(),(double)matrix.getResponse().getRouteSummary().getLengthInMeters(),(double)matrix.getResponse().getRouteSummary().getTravelTimeInSeconds()));
            }
        }
        tomTomRepository.save(response);
        return locationDistances;
    }


    private String getRequestMap(List<Task> fromTasks, List<Task> toTask) {
        Map<String, List<Map>> request = new HashMap<>();
        List<Map> points = new ArrayList<>();
        for (Task fromTask : fromTasks) {
            Map<String, Double> latLong = new HashMap<>();
            Map<String, Map> point = new HashMap<>();
            latLong.put("latitude", fromTask.getLatitude());
            latLong.put("longitude", fromTask.getLongitude());
            point.put("point", latLong);
            points.add(point);
        }
        request.put("origins", points);
        points = new ArrayList<>();
        for (Task task : toTask) {
            Map<String, Double> latLong = new HashMap<>();
            Map<String, Map> point = new HashMap<>();
            latLong.put("latitude", task.getLatitude());
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
            int j = i + 6;
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

    public TomTomResponse submitToTomtomForMatrix(String requestBody) {
        HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder = null;
        try {
            builder = new URIBuilder("https://api.tomtom.com/routing/1/matrix/json");
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("key", keys[key]));
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

    public Route submitToTomtomForRoute(double fromLat,double fromLong,double toLat,double toLong) throws IOException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response=null;
        try {
            URIBuilder builder = new URIBuilder("https://api.tomtom.com/routing/1/calculateRoute/"+fromLat+","+fromLong+":"+toLat+","+toLong+"/json");
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("key", newKeys[0]));
            params.add(new BasicNameValuePair("instructionsType", "coded"));
            params.add(new BasicNameValuePair("routeRepresentation", "polyline"));
            params.add(new BasicNameValuePair("language", "en-GB"));
            builder.setParameters(params);
            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.setHeader("Content-Type","application/json");
            response = httpclient.execute(httpGet);
            ObjectMapper mapper = ObjectMapperUtils.getObjectMapper();

            List<Route> routes = mapper.readValue(response.getEntity().getContent(), RouteInfo.class).getRoutes();
            Route route = routes.get(0);
            log.info("done with this:");
            return route;
        } catch (URISyntaxException | IOException e) {
            log.error(fromLat+","+fromLong+":"+toLat+","+toLong+":::::");
            try {
                Thread.currentThread().sleep(10000l);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    private TomTomResponse getfromTomtom(String headersUrl) {
        HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder = null;
        try {
            builder = new URIBuilder("https://api.tomtom.com"+headersUrl);
            HttpGet httppost = new HttpGet(builder.build());
            httppost.setHeader("Content-Type","application/json");
            HttpResponse response = httpclient.execute(httppost);
            ObjectMapper mapper = new ObjectMapper();
            TomTomResponse tomTomResponse = mapper.readValue(response.getEntity().getContent(), TomTomResponse.class);
            return tomTomResponse;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Matrix> getMatrix(){
        List<Matrix> matrices = new ArrayList<>();
        tomTomRepository.findAll().forEach(t->{
            matrices.addAll(t.getMatrix().stream().flatMap(m->m.stream()).collect(Collectors.toList()));
        });
        return matrices;
    }
    public void initEmptyRouteMatrix(){
        List<Task> tasks = taskService.getUniqueTask().stream().filter(ObjectUtils.distinctByKey(task -> task.getLatitude()+task.getLongitude())).collect(toList());
        List<AtoBRoute> atoBRoutes =new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < tasks.size(); j++) {
                AtoBRoute atoBRoute= new AtoBRoute(tasks.get(i).getLatitude(),tasks.get(i).getLongitude(),tasks.get(j).getLatitude(),tasks.get(j).getLongitude(),null);
                atoBRoutes.add(atoBRoute);
            }
            atoBRouteRepository.saveAll(atoBRoutes);
            atoBRoutes.clear();
        }
    }

    public List<AtoBRoute> fillRouteMatrix(){
        List<AtoBRoute> atoBRoutes = atoBRouteRepository.findAll();
        atoBRoutes=atoBRoutes.stream().filter(a->a.getRoute()==null && !a.getFirstLatitude().equals(a.getSecondLattitude())).collect(Collectors.toList());
        ExecutorService executorService = ExecutorUtil.getExecutorService();
        for(AtoBRoute atoBRoute:atoBRoutes){
            if(atoBRoute.getRoute()!=null){
                continue;
            }
            //new TomtomRouteRunnable(this,atoBRouteRepository,atoBRoute);
            executorService.submit(new TomtomRouteRunnable(this,atoBRouteRepository,atoBRoute));

        }

        return atoBRoutes;
    }

    private Route getTomtomRoute(String headersUrl) {
        HttpClient httpclient = HttpClients.createDefault();
        URIBuilder builder = null;
        try {
            builder = new URIBuilder("https://api.tomtom.com"+headersUrl);
            HttpGet httppost = new HttpGet(builder.build());
            httppost.setHeader("Content-Type","application/json");
            HttpResponse response = httpclient.execute(httppost);
            ObjectMapper mapper = new ObjectMapper();
            Route route = mapper.readValue(response.getEntity().getContent(), Route.class);
            return route;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadRouteMatrix() {
        //initEmptyRouteMatrix();
        fillRouteMatrix();
    }
    public List<AtoBRoute> getRouteMatrix() {
        List<AtoBRoute> atoBRoutes = atoBRouteRepository.findAll();
        return atoBRoutes;
    }

    /**
     *
     * @return map with value true if arrival side is right
     */
    public Map<LocationPair,Boolean> getOnArriveSideMatrix(){
        List<AtoBRoute> routeMatrix = getRouteMatrix();
        Map<LocationPair,Boolean> onArriveSideMatrix= new HashMap<>();
        for(AtoBRoute atoBRoute:routeMatrix){
            LocationPair locationPair = new LocationPair(atoBRoute.getFirstLatitude(), atoBRoute.getFirstLongitude(), atoBRoute.getSecondLattitude(), atoBRoute.getSecondLongitude());
            if(atoBRoute.getRoute()!=null){
                String onReachManeuver=atoBRoute.getRoute().getGuidance().getInstructions().get(atoBRoute.getRoute().getGuidance().getInstructions().size()-1).getManeuver();
                if(!onReachManeuver.startsWith("ARRIVE")){
                    log.error("Problem with route:"+atoBRoute.getId());
                }
                onArriveSideMatrix.put(locationPair,
                        ARRIVE_RIGHT.equals(onReachManeuver));
            }else{
                assert atoBRoute.getFirstLatitude()==atoBRoute.getSecondLattitude();
                onArriveSideMatrix.put(locationPair,true);
            }

        }
        return onArriveSideMatrix;
    }

}
