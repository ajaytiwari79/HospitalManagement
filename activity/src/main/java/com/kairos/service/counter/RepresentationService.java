package com.kairos.service.counter;

import com.kairos.dto.activity.counter.chart.BaseChart;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import org.springframework.stereotype.Service;

@Service
public class RepresentationService {
    public BaseChart getRepresentationData(RawRepresentationData data){
        switch (data.getChartType()){
            case PIE: return getPieChartData(data);
            case GAUGE: return getGaugeChartData(data);
            case NUMBER_ONLY: return  getNumberOnlyRepresentation(data);
            default: break;
        }
        return null;
    }

    private BaseChart getPieChartData(RawRepresentationData data){
        return null;
    }

    private BaseChart getGaugeChartData(RawRepresentationData data){
        return null;
    }

    private BaseChart getNumberOnlyRepresentation(RawRepresentationData data){
        return null;
    }
}
