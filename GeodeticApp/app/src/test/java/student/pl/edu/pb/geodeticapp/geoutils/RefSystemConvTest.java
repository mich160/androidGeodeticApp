package student.pl.edu.pb.geodeticapp.geoutils;

import org.junit.Test;

import student.pl.edu.pb.geodeticapp.BuildConfig;
import student.pl.edu.pb.geodeticapp.geoutils.exceptions.ReferenceSystemConversionException;

public class RefSystemConvTest {
    @Test
    public void convertTest() {
        ReferenceSystemConverter converter = new ReferenceSystemConverter();
        try {
            //ZONE 5 TEST
            Coordinates2D wsg84Coordinates = new Coordinates2D(14.49414462741, 52.42443636727);
            double expectedZ5X = 5465591.141312;
            double expectedZ5Y = 5810244.142346;
            Coordinates2D resultCoordinates = converter.convert(wsg84Coordinates, ReferenceSystemConverter.ReferenceSystem.WSG84, CS2000RefSystemPicker.getReferenceSystem(wsg84Coordinates.getX()));
            if (BuildConfig.DEBUG && difference(expectedZ5X, resultCoordinates.getX()) > 1 && difference(expectedZ5Y, resultCoordinates.getY()) > 1) {
                throw new AssertionError();
            }
            //ZONE 6 TEST
            wsg84Coordinates = new Coordinates2D(17.357058563959, 53.175316873378);
            double expectedZ6X = 6457013.311331;
            double expectedZ6Y = 5893870.244391;
            resultCoordinates = converter.convert(wsg84Coordinates, ReferenceSystemConverter.ReferenceSystem.WSG84, CS2000RefSystemPicker.getReferenceSystem(wsg84Coordinates.getX()));
            if (BuildConfig.DEBUG && difference(expectedZ6X, resultCoordinates.getX()) > 1 && difference(expectedZ6Y, resultCoordinates.getY()) > 1) {
                throw new AssertionError();
            }
            //ZONE 7 TEST
            wsg84Coordinates = new Coordinates2D(21.104791593393, 52.228672314642);
            double expectedZ7X = 7507159.59808;
            double expectedZ7Y = 5788347.248294;
            resultCoordinates = converter.convert(wsg84Coordinates, ReferenceSystemConverter.ReferenceSystem.WSG84, CS2000RefSystemPicker.getReferenceSystem(wsg84Coordinates.getX()));
            if (BuildConfig.DEBUG && difference(expectedZ7X, resultCoordinates.getX()) > 1 && difference(expectedZ7Y, resultCoordinates.getY()) > 1) {
                throw new AssertionError();
            }
            //ZONE 8 TEST
            wsg84Coordinates = new Coordinates2D(23.142700195313, 53.135478283293);
            double expectedZ8X = 8442628.61201;
            double expectedZ8Y = 5889587.322149;
            resultCoordinates = converter.convert(wsg84Coordinates, ReferenceSystemConverter.ReferenceSystem.WSG84, CS2000RefSystemPicker.getReferenceSystem(wsg84Coordinates.getX()));
            if (BuildConfig.DEBUG && difference(expectedZ8X, resultCoordinates.getX()) > 1 && difference(expectedZ8Y, resultCoordinates.getY()) > 1) {
                throw new AssertionError();
            }
            //REVERSE TEST
            Coordinates2D cs2000z8Coordinates = new Coordinates2D(expectedZ8X, expectedZ8Y);
            resultCoordinates = converter.convert(cs2000z8Coordinates, ReferenceSystemConverter.ReferenceSystem.CS2000z8, ReferenceSystemConverter.ReferenceSystem.WSG84);
            if (BuildConfig.DEBUG && difference(wsg84Coordinates.getX(), resultCoordinates.getX()) > 0.1 && difference(wsg84Coordinates.getY(), resultCoordinates.getY()) > 0.1) {
                throw new AssertionError();
            }
        } catch (ReferenceSystemConversionException e) {
            e.printStackTrace();
        }
    }

    private double difference(double a, double b) {
        return Math.abs(b - a);
    }
}
