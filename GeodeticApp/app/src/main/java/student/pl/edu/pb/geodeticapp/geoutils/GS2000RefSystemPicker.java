package student.pl.edu.pb.geodeticapp.geoutils;

public class GS2000RefSystemPicker {
    private final static double FIRST_LT = 16.5;
    private final static double SECOND_LT = 19.5;
    private final static double THIRD_LT = 22.5;

    public static ReferenceSystemConverter.ReferenceSystem getReferenceSystem(double longitude){
        if(longitude < FIRST_LT){
            return ReferenceSystemConverter.ReferenceSystem.CS2000z5;
        }
        else if(longitude >= FIRST_LT && longitude <SECOND_LT){
            return ReferenceSystemConverter.ReferenceSystem.CS2000z6;
        }
        else if(longitude >= SECOND_LT && longitude < THIRD_LT){
            return ReferenceSystemConverter.ReferenceSystem.CS2000z7;
        }
        else {
            return ReferenceSystemConverter.ReferenceSystem.CS2000z8;
        }
    }
}
