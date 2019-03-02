package il.ac.colman.cs.util;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.cloudwatch.model.Dimension;

import java.awt.*;

public class Monitoring {
// To send the info to the Amazon Cloud Watch
    public static void CloudWatchTraffic(AmazonCloudWatch cw, double time, String dimensionName, String matric)
    {

        Dimension dimension = new Dimension().withName(dimensionName).withValue("URLS");
        MetricDatum datum = new MetricDatum().withMetricName(matric).withUnit(StandardUnit.None).withValue(time)
                .withDimensions(dimension);
        PutMetricDataRequest req = new PutMetricDataRequest().withNamespace("Custom Traffic").withMetricData(datum);
        PutMetricDataResult resp = cw.putMetricData(req);
    }
}