package com.planner.service.locationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RunWith(BlockJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
public class GraphHopperServiceUnitTest {

    Logger log = LoggerFactory.getLogger(this.getClass());

  /*  @InjectMocks
    GraphHopperService graphHopperService;
    OptaLocationDTO optaLocationDTO = null;
    @Mock
    AppConfig appConfig;
    @Mock
    PlanningLocation planningLocation1;
    @Mock
    PlanningLocation planningLocation2;
    @Before
    public void setUp() throws Exception {
       // graphHopperService=new GraphHopperService();
       // appConfig = mock(AppConfig.class);
        optaLocationDTO = new OptaLocationDTO();
        optaLocationDTO.setCity("Gurugram");
        optaLocationDTO.setCountry("India");
        optaLocationDTO.setHouseNumber("spaze i-tech park");
        optaLocationDTO.setStreet("sector 49");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Ignore
    public void getMatrixData() throws Exception {
    }

    @Test
    @Ignore
    public void getLocationDistances() throws Exception {
    }

    @Test
    @Ignore
    public void getLocationDistancesByPlanningLocation() throws Exception {
    }

    @Test
    public void getRoute(){
        when(appConfig.getGraphhoperkey()).thenReturn("f3cdca17-5fe1-4b72-a057-75ebb28e93c5");
        graphHopperService.getRoute();
    }

    @Test
    public void getdistance() throws Exception {
        String s = "hellohy";
        s.concat("bye");
        System.out.print(s);
        when(planningLocation1.getLatitude()).thenReturn(28.4595);
        when(planningLocation1.getLongitude()).thenReturn(77.0266);
        when(planningLocation2.getLatitude()).thenReturn(28.5355);
        when(planningLocation2.getLongitude()).thenReturn(77.3910);
        when(appConfig.getGraphhoperkey()).thenReturn("f3cdca17-5fe1-4b72-a057-75ebb28e93c5");
        MatrixResponse response = graphHopperService.getdistance(planningLocation1,planningLocation2,"car");
        log.info("distance "+response.getDistances().get(0).get(0).doubleValue()+" times "+response.getTimes().get(0).get(0).doubleValue());
    }

    @Test
    public void getLatLongByAddress() throws Exception {
        when(appConfig.getGraphhoperkey()).thenReturn("f3cdca17-5fe1-4b72-a057-75ebb28e93c5");
        graphHopperService.getLatLongByAddress(optaLocationDTO);
    }

    @Test
    @Ignore
    public void getLatsLongsFromLocations() throws Exception {
    }

    @Test
    @Ignore
    public void getApiKey() throws Exception {
    }
*/
}