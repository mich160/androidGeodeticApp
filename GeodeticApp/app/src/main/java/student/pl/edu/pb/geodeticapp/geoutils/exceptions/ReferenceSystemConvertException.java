package student.pl.edu.pb.geodeticapp.geoutils.exceptions;

public class ReferenceSystemConvertException extends Exception {
    public ReferenceSystemConvertException(Throwable cause){
        super("Couldn't convert coordinates!", cause);
    }
}
