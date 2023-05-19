import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;

import java.util.ArrayList;
import java.util.List;

public class Rems
{
    private final JavaCSG csg;
    private final double carportLength;
    private final double carportWidth;
    private final double offsetZ;
    private final int CARPORT_HANG = 100;

    public Rems(JavaCSG csg, double carportLength, double carportWidth, double offsetZ)
    {
        this.csg = csg;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.offsetZ = offsetZ;
    }

    private Geometry3D box()
    {
        Geometry3D box = csg.box3D(carportLength, carportWidth, 20, true);

        return box;
    }

    private List<Geometry3D> rem()
    {
        List<Geometry3D> remsList = new ArrayList<>();
        int rotate = 90;


        Geometry3D remLeft = csg.box3D(carportLength - CARPORT_HANG, 55, 20, true);
        remLeft = csg.rotate3DX(csg.degrees(rotate)).transform(remLeft);
        remLeft = csg.translate3D(0, carportWidth / 2 - 45, offsetZ).transform(remLeft);
        remsList.add(remLeft);


        Geometry3D remRight = csg.box3D(carportLength - CARPORT_HANG, 55, 20, true);
        remRight = csg.rotate3DX(csg.degrees(rotate)).transform(remRight);
        remRight = csg.translate3D(0, - (carportWidth / 2 - 45), offsetZ).transform(remRight);
        remsList.add(remRight);


        return remsList;
    }





    public Geometry3D testGen()
    {
        return csg.union3D(rem().get(0), rem().get(1), box());
    }

    public Geometry3D generate()
    {
        return null;
    }
}
