package student.pl.edu.pb.geodeticapp.geoutils;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import student.pl.edu.pb.geodeticapp.geoutils.exceptions.ReferenceSystemConvertException;

public class ReferenceSystemConverter {
    public enum ReferenceSystem{
        CS2000z5("PROJCS[\"ETRS89 / Poland CS2000 zone 5\",GEOGCS[\"ETRS89\",DATUM[\"European_Terrestrial_Reference_System_1989\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],AUTHORITY[\"EPSG\",\"6258\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4258\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",15],PARAMETER[\"scale_factor\",0.999923],PARAMETER[\"false_easting\",5500000],PARAMETER[\"false_northing\",0],AUTHORITY[\"EPSG\",\"2176\"],AXIS[\"y\",EAST],AXIS[\"x\",NORTH]]"),
        CS2000z6("PROJCS[\"ETRS89 / Poland CS2000 zone 6\",GEOGCS[\"ETRS89\",DATUM[\"European_Terrestrial_Reference_System_1989\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],AUTHORITY[\"EPSG\",\"6258\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4258\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",18],PARAMETER[\"scale_factor\",0.999923],PARAMETER[\"false_easting\",6500000],PARAMETER[\"false_northing\",0],AUTHORITY[\"EPSG\",\"2177\"],AXIS[\"y\",EAST],AXIS[\"x\",NORTH]]"),
        CS2000z7("PROJCS[\"ETRS89 / Poland CS2000 zone 7\",GEOGCS[\"ETRS89\",DATUM[\"European_Terrestrial_Reference_System_1989\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],AUTHORITY[\"EPSG\",\"6258\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4258\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",21],PARAMETER[\"scale_factor\",0.999923],PARAMETER[\"false_easting\",7500000],PARAMETER[\"false_northing\",0],AUTHORITY[\"EPSG\",\"2178\"],AXIS[\"y\",EAST],AXIS[\"x\",NORTH]]"),
        CS2000z8("PROJCS[\"ETRS89 / Poland CS2000 zone 8\",GEOGCS[\"ETRS89\",DATUM[\"European_Terrestrial_Reference_System_1989\",SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]],AUTHORITY[\"EPSG\",\"6258\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4258\"]],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",0],PARAMETER[\"central_meridian\",24],PARAMETER[\"scale_factor\",0.999923],PARAMETER[\"false_easting\",8500000],PARAMETER[\"false_northing\",0],AUTHORITY[\"EPSG\",\"2179\"],AXIS[\"y\",EAST],AXIS[\"x\",NORTH]]"),
        WS84("GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]");

        private CoordinateReferenceSystem referenceSystem;

        ReferenceSystem(String wkt){
            try {
                this.referenceSystem = CRS.parseWKT(wkt);
            } catch (FactoryException e) {
                e.printStackTrace();
            }
        }
    }

    public Coordinates2D convert(Coordinates2D coordinates2D, ReferenceSystem fromSystem, ReferenceSystem toSystem) throws ReferenceSystemConvertException {
        try {
            MathTransform mathTransform = CRS.findMathTransform(fromSystem.referenceSystem, toSystem.referenceSystem, true);
            double[] resultArray = new double[2];
            mathTransform.transform(coordinates2D.toArray(),0, resultArray, 0, 1);
            return Coordinates2D.ofArray(resultArray);
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
            throw new ReferenceSystemConvertException(e);
        }
    }
}
