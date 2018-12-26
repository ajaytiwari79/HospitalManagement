package com.kairos.service.counter;

import com.kairos.dto.activity.counter.chart.BaseChart;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import org.springframework.stereotype.Service;

@Service
public class RepresentationService {
    public BaseChart getRepresentationData(CommonRepresentationData data){
        switch (data.getChartType()){
            case PIE: return getPieChartData(data);
            case GAUGE: return getGaugeChartData(data);
            case NUMBER_ONLY: return  getNumberOnlyRepresentation(data);
            default: break;
        }
        return null;
    }

    private BaseChart getPieChartData(CommonRepresentationData data){
        return null;
    }

    private BaseChart getGaugeChartData(CommonRepresentationData data){
        return null;
    }

    private BaseChart getNumberOnlyRepresentation(CommonRepresentationData data){
        return null;
    }
}
