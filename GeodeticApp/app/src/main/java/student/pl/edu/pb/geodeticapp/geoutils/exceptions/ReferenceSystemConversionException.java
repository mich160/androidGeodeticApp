package student.pl.edu.pb.geodeticapp.geoutils.exceptions;

public class ReferenceSystemConversionException extends Exception {
    public ReferenceSystemConversionException(Throwable cause) {
        super("Couldn't convert coordinates!", cause);
    }
}
