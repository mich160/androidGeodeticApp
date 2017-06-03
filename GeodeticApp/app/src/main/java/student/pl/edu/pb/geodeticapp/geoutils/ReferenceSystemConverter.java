package student.pl.edu.pb.geodeticapp.geoutils;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import student.pl.edu.pb.geodeticapp.geoutils.exceptions.ReferenceSystemConversionException;

public class ReferenceSystemConverter {

    public enum ReferenceSystem {
        CS2000z5("CS2000z5", "+proj=tmerc +lat_0=0 +lon_0=15 +k=0.999923 +x_0=5500000 +y_0=0 +ellps=GRS80 +units=m +no_defs"),
        CS2000z6("CS2000z6", "+proj=tmerc +lat_0=0 +lon_0=18 +k=0.999923 +x_0=6500000 +y_0=0 +ellps=GRS80 +units=m +no_defs"),
        CS2000z7("CS2000z7", "+proj=tmerc +lat_0=0 +lon_0=21 +k=0.999923 +x_0=7500000 +y_0=0 +ellps=GRS80 +units=m +no_defs"),
        CS2000z8("CS2000z8", "+proj=tmerc +lat_0=0 +lon_0=24 +k=0.999923 +x_0=8500000 +y_0=0 +ellps=GRS80 +units=m +no_defs"),
        WSG84("WSG84", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");

        private CoordinateReferenceSystem referenceSystem;
        private String name;

        ReferenceSystem(String name, String proj4) {
            CRSFactory crsFactory = new CRSFactory();
            referenceSystem = crsFactory.createFromParameters(name, proj4);
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private CoordinateTransformFactory transformFactory;

    public ReferenceSystemConverter() {
        transformFactory = new CoordinateTransformFactory();
    }

    public Coordinates2D convert(Coordinates2D coordinates2D, ReferenceSystem fromSystem, ReferenceSystem toSystem) throws ReferenceSystemConversionException {
        CoordinateTransform coordinateTransform = transformFactory.createTransform(fromSystem.referenceSystem, toSystem.referenceSystem);
        ProjCoordinate result = new ProjCoordinate();
        coordinateTransform.transform(projFromCoords2D(coordinates2D), result);
        return coords2DFromProj(result);
    }

    private ProjCoordinate projFromCoords2D(Coordinates2D coordinates2D) {
        return new ProjCoordinate(coordinates2D.getX(), coordinates2D.getY());
    }

    private Coordinates2D coords2DFromProj(ProjCoordinate projCoordinate) {
        return new Coordinates2D(projCoordinate.x, projCoordinate.y);
    }
}
