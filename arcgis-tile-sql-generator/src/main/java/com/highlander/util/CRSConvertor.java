package com.highlander.util;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;

public class CRSConvertor {

    public static void main(String[] args) {

        double x = 107.33280960718788;
        double y = 19.956161191908173;
        y = 18.04;
        double[] target = transform(x, y);
        System.out.println("#####" + target[0] + "," + target[1]);
    }

    private static CRSFactory crsFactory = new CRSFactory();

    private static CoordinateReferenceSystem srcCRS = crsFactory.createFromName("EPSG:3395");
    private static CoordinateReferenceSystem tgtCRS = crsFactory.createFromName("EPSG:3857");
    private static BasicCoordinateTransform t = new BasicCoordinateTransform(srcCRS, tgtCRS);

    public static double[] transform(double x, double y) {
        try {

            // t.transform(src, tgt)

            // Point.Double gps = new Double(x, y);
            
            ProjCoordinate tCoor = new ProjCoordinate();
            t.transform(new ProjCoordinate(x, y), tCoor);

            return new double[] { tCoor.x, tCoor.y };
            // Point.Double targetPoint = new Point.Double(tCoor.x, tCoor.y);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
