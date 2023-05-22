import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;

import java.util.ArrayList;
import java.util.List;

public class Rems
{
    private final JavaCSG csg;
    private final double carportLength;
    private final double carportWidth;
    private final double offsetZ; // if we make 3d in one print
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

    private List<Geometry3D> rems()
    {
        List<Geometry3D> remsList = new ArrayList<>();
        int rotate = 90;
        int offsetY = (int) (carportWidth / 2 - 45);


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
        return csg.union3D(rems().get(0), rems().get(1), box());
    }

    public Geometry3D generateAsOne()
    {
        return csg.union3D(rems());
    }

    public Geometry3D generateRem()
    {
        int rotate = 90;
        Geometry3D rem = csg.box3D(carportLength - CARPORT_HANG, 55, 20, true);
        rem = csg.rotate3DX(csg.degrees(rotate)).transform(rem);

        return rem;
    }
}
