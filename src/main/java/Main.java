import entities.OrderItem;
import entities.Wood;
import exceptions.DatabaseException;
import Printables.Pole;
import entities.Receipt;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import persistence.ConnectionPool;
import persistence.Facade;

import java.util.ArrayList;
import java.util.List;

public class Main
{
	private static ConnectionPool connectionPool;
	private static JavaCSG csg;
	private static Receipt receipt;
	private static double widthmm = 0;
	private static double lengthmm = 0;
	private static double offsetZ = 0;

	public static void main(String[] args) throws DatabaseException
	{
		connectionPool = new ConnectionPool(); // Shut up!
		receipt = Facade.getReceiptById(73, connectionPool);
		lengthmm = receipt.getLength()*10;
		widthmm = receipt.getWidth()*10;
		csg = JavaCSGFactory.createDefault();


		try
		{
			List<Wood> woodItems = getWoods(Facade.getWoodOrderItemsByRecieptId(receipt.getIdReceipt(), connectionPool));

			Geometry3D roof = getRoofModel(woodItems, widthmm, lengthmm, csg);

			Geometry3D rafters = getRafterModel(woodItems, widthmm, lengthmm, csg);

			Geometry3D rems = getRemModel(woodItems, widthmm, lengthmm, csg);

			Geometry3D poles = getPoleModel(woodItems, widthmm, lengthmm, csg);


			/*Geometry3D box = csg.box3D(woodItems.get(1).getWidth(), woodItems.get(1).getLength(), woodItems.get(1).getHeight(), false);
			box = csg.translate3DX(woodItems.get(1).getWidth() + 10).transform(box);
			Geometry3D box1 = csg.translate3DX(woodItems.get(1).getWidth() + 30).transform(box);

			Geometry3D u = csg.union3D(box, box1);*/
			csg.view(csg.union3D(roof, rafters, rems, poles));

		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		connectionPool.close();

	}

	private static Geometry3D getPoleModel(List<Wood> woodItems, double widthmm, double lengthmm, JavaCSG csg) throws DatabaseException
	{

		List<OrderItem> orderItemList = Facade.getWoodOrderItemsByRecieptId(receipt.getIdReceipt(), connectionPool);
		OrderItem poleItem = null;

		for (OrderItem oi: orderItemList)
		{
			if (oi.getMaterial().getVariant().equals("Stolpe"))
			{
				poleItem = oi;
				break;
			}
		}
		Wood wood = (Wood) poleItem.getMaterial();

		Geometry3D model = csg.box3D(wood.getWidth(), wood.getHeight(), wood.getLength() * 10, false);

		Geometry3D poles = csg.union3D(Pole.print(offsetZ, receipt.getIdReceipt()));

		csg.view(poles);

		int flyvl = 500;
		int flyvb = 350;

		List<Geometry3D> geometry3DS = new ArrayList<>();

		Geometry3D pos0 = csg.translate3D(-lengthmm / 2 - flyvl, -widthmm / 2 - flyvb, offsetZ).transform(model);
		Geometry3D pos1 = csg.translate3D(-lengthmm / 2 - flyvl, widthmm / 2 + flyvb, offsetZ).transform(model);
		Geometry3D pos2 = csg.translate3D(lengthmm / 2 + flyvl, -widthmm / 2 - flyvb, offsetZ).transform(model);
		Geometry3D pos3 = csg.translate3D(lengthmm / 2 + flyvl, widthmm / 2 + flyvb, offsetZ).transform(model);
		geometry3DS.add(pos0);
		geometry3DS.add(pos1);
		geometry3DS.add(pos2);
		geometry3DS.add(pos3);
		if(lengthmm > 3100 && lengthmm < 6201)
		{
			Geometry3D pos4 = csg.translate3D(0 , -widthmm / 2 - flyvb, offsetZ).transform(model);
			Geometry3D pos5 = csg.translate3D(0 , widthmm / 2 + flyvb, offsetZ).transform(model);
			geometry3DS.add(pos4);
			geometry3DS.add(pos5);
		}
		if(lengthmm > 6200)
		{
			double spaceX = -lengthmm/4;
			double firstExtra = lengthmm/2 + spaceX;
			double secondExtra = lengthmm/2 + spaceX*3;
			Geometry3D pos6 = csg.translate3D(firstExtra , widthmm / 2 + flyvb, offsetZ).transform(model);
			Geometry3D pos7 = csg.translate3D(secondExtra, widthmm / 2 + flyvb , offsetZ).transform(model);
			Geometry3D pos8 = csg.translate3D(firstExtra, -widthmm / 2 - flyvb, offsetZ).transform(model);
			Geometry3D pos9 = csg.translate3D(secondExtra, -widthmm / 2 - flyvb , offsetZ).transform(model);
			geometry3DS.add(pos6);
			geometry3DS.add(pos7);
			geometry3DS.add(pos8);
			geometry3DS.add(pos9);
		}
		if(widthmm > 3100)
		{
			Geometry3D pos10 = csg.translate3D(lengthmm / 2 + flyvb, 0, offsetZ).transform(model);
			Geometry3D pos11 = csg.translate3D(-lengthmm / 2 - flyvb, 0, offsetZ).transform(model);
			geometry3DS.add(pos10);
			geometry3DS.add(pos11);

		}
		return csg.union3D(geometry3DS);
	}

	private static Geometry3D getRemModel(List<Wood> woodItems, double widthmm, double lengthmm, JavaCSG csg)
	{
		int remSpacingRoff = 350;


		Wood remItem = null;

		for (Wood w: woodItems)
		{
			if (w.getVariant().equals("Rem"))
			{
				remItem = w;
				break;
			}
		}

		Geometry3D model = csg.box3D(remItem.getHeight() * 10, lengthmm, remItem.getWidth() * 10, false);
		Geometry3D rem0 = csg.translate3D(- (widthmm / 2) + remSpacingRoff, 0, offsetZ).transform(model);
		Geometry3D rem1 = csg.translate3D(widthmm / 2 - remSpacingRoff, 0, offsetZ).transform(model);

		offsetZ += remItem.getWidth() * 10;
		return csg.union3D(rem0, rem1);
	}

	// FIXME. Just bad.
	private static Geometry3D getRafterModel(List<Wood> woodItems, double widthmm, double lengthmm, JavaCSG csg)
	{
		Wood rafterItem = null;

		for (Wood w: woodItems)
		{
			if (w.getVariant().equals("Spær"))
			{
				rafterItem = w;
				break;
			}
		}

		List<Geometry3D> rafters = new ArrayList<>();
		double amount = Math.floor(lengthmm / 550.0);
		double offset = 0;
		double initialPos = -(lengthmm / 2) + rafterItem.getWidth() + 250;

		Geometry3D model = csg.box3D(widthmm - 600, rafterItem.getHeight() * 10, rafterItem.getWidth() * 10, false);

		for (int i = 0; i < amount; i++)
		{
			rafters.add(csg.translate3D(0, initialPos + offset, offsetZ).transform(model));
			offset = lengthmm / amount + offset;
		}

		offsetZ += rafterItem.getWidth() * 10;
		return csg.union3D(rafters);
	}

	private static List<Wood> getWoods(List<OrderItem> items)
	{
		List<Wood> woods = new ArrayList<>();

		for (OrderItem i: items)
		{
			woods.add((Wood) i.getMaterial());
		}

		return woods;
	}

	private static Geometry3D getRoofModel(List<Wood> woods, double widthmm, double lengthmm, JavaCSG csg)
	{
		Wood roofItem = null;

		for (Wood w: woods)
		{
			if (w.getVariant().equals("Tag"))
			{
				roofItem = w;
				break;
			}
		}
		offsetZ += roofItem.getHeight() * 10;
		return csg.box3D(widthmm, lengthmm, roofItem.getHeight() * 10, false);
	}
}
