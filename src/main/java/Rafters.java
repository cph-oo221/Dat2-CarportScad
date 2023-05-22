import entities.OrderItem;
import entities.Wood;
import exceptions.DatabaseException;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import persistence.ConnectionPool;
import persistence.Facade;

import java.util.ArrayList;
import java.util.List;

public class Rafters
{
    private final JavaCSG csg;
    private final double width;
    private final double height;
    private final double length;
    private final int amount;
    ConnectionPool connectionPool;

    public Rafters(JavaCSG csg, double width, double height, double length, int amount)
    {
        this.csg = csg;
        this.width = width;
        this.height = height;
        this.length = length;
        this.amount = amount;
    }

    private Geometry3D rafter()
    {
        // Make standard rafter
        Geometry3D rafter = csg.box3D(length, width, height, true);
        rafter = csg.rotate3DX(csg.degrees(90)).transform(rafter);
        return rafter;
    }

    private List<Geometry3D> poleList()
    {

        List<OrderItem> itemList = null;
        try
        {
            itemList = Facade.getWoodOrderItemsByRecieptId(23, connectionPool);
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < itemList.size(); i++)
        {
            if(itemList.get(i).getMaterial().getVariant().equals("Spær"))
            {
                Wood rafter = (Wood) itemList.get(i).getMaterial();
            }
        }


        List<Geometry3D> listForPoles = new ArrayList<>();
        Geometry3D rafter = rafter();

        int offset = 0;
        int offsetMulti = 55;

        for (int i = 0; i < amount; i++)
        {
            rafter = csg.translate3D(0, offset + offsetMulti, 0).transform(rafter);
            listForPoles.add(rafter);
        }

        return listForPoles;
    }

    public Geometry3D generate()
    {
        return csg.union3D(poleList());
    }
}
