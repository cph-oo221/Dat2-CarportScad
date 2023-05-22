package Printables;

import entities.OrderItem;
import entities.Receipt;
import entities.Wood;
import exceptions.DatabaseException;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import persistence.ConnectionPool;
import persistence.Facade;

import javax.ejb.ApplicationException;
import java.util.ArrayList;
import java.util.List;

public class Pole
{
    public final JavaCSG csg;

    public static int polesX = 0;
    public static int polesY = 0;
    public static int xOffset= 0;
    public static int yOffset = 0;
    public static int xCounter = 0;
    public static int yCounter = 0;

    public Pole(JavaCSG csg)
    {
        this.csg = csg;
    }

    public static List<Geometry3D> print (double zOffset , int idReceipt) throws DatabaseException
    {
        ConnectionPool connectionPool = new ConnectionPool();

        Receipt receipt = Facade.getReceiptById(idReceipt, connectionPool);

        List<Geometry3D> poleList = new ArrayList<>();

        List<OrderItem> itemList = Facade.getWoodOrderItemsByRecieptId(idReceipt, connectionPool);

        Wood wood = null;

        int amount = 0;

        itemList.removeIf(o -> !o.getMaterial().getVariant().equals("Stolpe"));
        for (OrderItem o : itemList)
        {
            if(itemList != null)
            {
                amount = o.getAmount();
                wood = (Wood) o.getMaterial();
            }
        }

        JavaCSG csg = JavaCSGFactory.createDefault();
        getCurrentMethodX(receipt, connectionPool);


        Geometry3D poleBuffer = null;
        Geometry3D pole = null;
        for(int i = 0 ; i < amount ; i++)
        {
            currentMethod(receipt);
            if(poleBuffer == null)
            {
                poleBuffer = csg.box3D(wood.getHeight()/10, wood.getWidth()/10, wood.getLength(), false);
                poleBuffer = csg.translate3DZ(zOffset).transform(poleBuffer);
                poleList.add(poleBuffer);
            }
            pole = csg.translate3DX(xOffset).transform(poleBuffer);
            pole = csg.translate3DY(yOffset).transform(pole);
            poleList.add(pole);
            poleBuffer = pole;

        }
        return poleList;
    }

    private static void currentMethod(Receipt receipt)
    {
        if(xCounter != 0)
        {
            yOffset = 0;
            xOffset = receipt.getLength()/polesX;
            xCounter--;
        }
        if(xCounter == 0 && yCounter != 0)
        {
            xCounter = polesX;
            yOffset = receipt.getWidth()/polesY;
            yCounter--;
        }
    }

    private static void getCurrentMethodX(Receipt receipt , ConnectionPool connectionPool) throws DatabaseException
    {
        if(receipt.getLength() < 311)
        {
            polesX = 2;
        }
        else if(receipt.getLength() > 310 && receipt.getLength() < 621)
        {
            polesX = 3;
        }
        else if(receipt.getLength() > 620 && receipt.getLength() < 931)
        {
            polesX = 4;
        }
        else if(receipt.getLength() > 930)
        {
            polesX = 5;
        }


        if(receipt.getWidth() < 311)
        {
            polesY = 2;
        }
        else if(receipt.getWidth() > 310 && receipt.getWidth() < 621)
        {
            polesY = 3;
        }
        else if(receipt.getWidth() > 620 && receipt.getWidth() < 931)
        {
            polesY = 4;
        }
        else if(receipt.getWidth() > 930)
        {
            polesY = 5;
        }

        xCounter = polesX;
        yCounter = polesY;
    }
}
