package demo.support;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import demo.model.Point;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class NavUtils {
    private static double  EARTH_RADIUS_IN_METERS = DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM * 1000;

    public static double getDistance(Point point1, Point point2){
        Assert.notNull(point1, "point1 must not be null");
        Assert.notNull(point2, "point2 must not be null");

        final SpatialContext ctx = SpatialContext.GEO;
        com.spatial4j.core.shape.Point p1 = ctx.makePoint(point1.getLongitude(), point1.getLatitude());
        com.spatial4j.core.shape.Point p2 = ctx.makePoint(point2.getLongitude(), point2.getLatitude());

        return DistanceUtils.degrees2Dist(ctx.getDistCalc().distance(p1, p2), DistanceUtils.EARTH_MEAN_RADIUS_KM) * 1000;
    }

    public static double getTotalDistance(List<Point> points){
        double totalDistance = 0;
        int count = 0;
        Point previousCount = null;
        for (Point point : points){
            count ++;
            if (count > 1 && count < points.size()){
                totalDistance = totalDistance + getDistance(previousCount, point);
            }

            previousCount = point;
        }

        return totalDistance;
    }

    public static Point getPosition(Point pt1, double d, double brg){
        if (Double.doubleToRawLongBits(d) == 0){
            return pt1;
        }
        double lat1 = Math.toRadians(pt1.getLatitude());
        double lon1 = Math.toRadians(pt1.getLongitude());
        double brgAAsRadians = Math.toRadians(brg);

        double lat2 = 0;
        double x = 0;
        double y = 0;
        double lon2 = lon1 + Math.atan2(x, y);

        return new Point(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    public static List<Point> decodePolyline(String polyline){
        final List<LatLng> latLngs = PolylineEncoding.decode(polyline);
        return latLngs.stream().map(latLng -> new Point(latLng.lat, latLng.lng)).collect(Collectors.toList());
    }
}
