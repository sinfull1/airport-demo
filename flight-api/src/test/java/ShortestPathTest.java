import com.example.demo.AnalyticsQueryController;
import com.example.demo.dto.AirportEdgeResult;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.*;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
public class ShortestPathTest {


    HashMap<String, Page<AirportEdgeResult>> store = new HashMap<>();
    @MockBean
    StreamBridge streamBridge;
    @MockBean
    AirlineGuestRepo airlineGuestRepo;
    @MockBean
    OntimeRepo ontimeRepo;
    @MockBean
    CarrierRepo carrierRepo;
    @MockBean
    EdgeListRepo edgeListRepo;

    @MockBean
    AirportEdgeResultRepo airportEdgeResultRepo;
    @Mock
    Page<AirportEdgeResult> pages1;
    @Mock
    Page<AirportEdgeResult> pages2;

    @Mock
    Page<AirportEdgeResult> pages3;
    @Mock
    Page<AirportEdgeResult> pages4;
    @Mock
    Page<AirportEdgeResult> pages5;
    @Mock
    Page<AirportEdgeResult> pages6;

    @BeforeAll
    public void init() {

        List<AirportEdgeResult> results1 = new ArrayList<>();
        List<AirportEdgeResult> results2 = new ArrayList<>();
        List<AirportEdgeResult> results3 = new ArrayList<>();
        List<AirportEdgeResult> results4 = new ArrayList<>();
        List<AirportEdgeResult> results5 = new ArrayList<>();
        List<AirportEdgeResult> results6 = new ArrayList<>();
        Mockito.when(pages1.get()).thenReturn(results1.stream());
        Mockito.when(pages2.get()).thenReturn(results2.stream());
        Mockito.when(pages3.get()).thenReturn(results3.stream());
        Mockito.when(pages4.get()).thenReturn(results4.stream());
        Mockito.when(pages5.get()).thenReturn(results5.stream());
        Mockito.when(pages6.get()).thenReturn(results6.stream());
        AirportEdgeResult airportEdgeResult = new AirportEdgeResult("11", "SPN", "HNL", 20L, 10L, "DL");
        AirportEdgeResult airportEdgeResult1 = new AirportEdgeResult("11", "SPN", "SFO", 20L, 10L, "DL");
        AirportEdgeResult airportEdgeResult2 = new AirportEdgeResult("11", "SPN", "GFG", 20L, 10L, "DL");

        AirportEdgeResult airportEdgeResult4 = new AirportEdgeResult("11", "HNL", "TGI", 22L, 20L, "DL");
        AirportEdgeResult airportEdgeResult5 = new AirportEdgeResult("11", "HNL", "PDO", 24L, 20L, "DL");

        AirportEdgeResult airportEdgeResult6 = new AirportEdgeResult("11", "SFO", "NYC", 23L, 20L, "DL");
        AirportEdgeResult airportEdgeResult7 = new AirportEdgeResult("11", "SFO", "MTY", 21L, 20L, "DL");


        AirportEdgeResult airportEdgeResult10 = new AirportEdgeResult("11", "PDO", "PGO", 27L, 25L, "DL");

        AirportEdgeResult airportEdgeResult8 = new AirportEdgeResult("11", "MTY", "PGO", 28L, 22L, "DL");

        AirportEdgeResult airportEdgeResult9 = new AirportEdgeResult("11", "GFG", "PGO", 35L, 24L, "DL");
        //SPN
        results1.add(airportEdgeResult);
        results1.add(airportEdgeResult1);
        results1.add(airportEdgeResult2);

        //HNL
        results2.add(airportEdgeResult4);
        results2.add(airportEdgeResult5);

        //SFO
        results3.add(airportEdgeResult6);
        results3.add(airportEdgeResult7);

        //PDO
        results4.add(airportEdgeResult10);
        //MTY
        results5.add(airportEdgeResult8);
        //GFG
        results6.add(airportEdgeResult9);
        Mockito.when(airportEdgeResultRepo
                        .findByOriginAndDepTimeBetween(eq("SPN"), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(pages1);
        Mockito.when(airportEdgeResultRepo
                        .findByOriginAndDepTimeBetween(eq("HNL"), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(pages2);
        Mockito.when(airportEdgeResultRepo
                        .findByOriginAndDepTimeBetween(eq("SFO"), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(pages3);
        Mockito.when(airportEdgeResultRepo
                        .findByOriginAndDepTimeBetween(eq("PDO"), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(pages4);
        Mockito.when(airportEdgeResultRepo
                        .findByOriginAndDepTimeBetween(eq("MTY"), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(pages5);
        Mockito.when(airportEdgeResultRepo
                        .findByOriginAndDepTimeBetween(eq("GFG"), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(pages6);
    }

    @Test
    public void test() {
        AnalyticsQueryController analyticsQueryController = new AnalyticsQueryController(streamBridge, airlineGuestRepo
                , ontimeRepo, carrierRepo, edgeListRepo, airportEdgeResultRepo);
        GraphPath<CustomNode, CustomWeightEdge> result = analyticsQueryController.dests("SPN", "PGO", "19700101").block();
        assert (result.getEndVertex().getArrTime() == 27);


    }

}

