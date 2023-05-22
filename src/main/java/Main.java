import entities.OrderItem;
import entities.Wood;
import exceptions.DatabaseException;
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
	private static final double widthmm = 3000;
	private static final double lengthmm = 7200;
	private static double offsetZ = 0;

	public static void main(String[] args)
	{
		connectionPool = new ConnectionPool(); // Shut up!
		csg = JavaCSGFactory.createDefault();


		try
		{
			List<Wood> woodItems = getWoods(Facade.getWoodOrderItemsByRecieptId(69, connectionPool));

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

	private static Geometry3D getPoleModel(List<Wood> woodItems, double widthmm, double lengthmm, JavaCSG csg)
	{
		Wood poleItem = null;

		for (Wood w: woodItems)
		{
			if (w.getVariant().equals("Stolpe"))
			{
				poleItem = w;
				break;
			}
		}

		Geometry3D model = csg.box3D(poleItem.getWidth(), poleItem.getHeight(), poleItem.getLength() * 10, false);

		int amount;
		if (lengthmm > 3100)
		{
			// TODO: Do some calculation
			amount = 6;
		}

		else
		{
			amount = 4;
		}

		Geometry3D pos0 = csg.translate3D(-widthmm / 2 + 1100, -lengthmm / 2, offsetZ).transform(model);
		Geometry3D pos1 = csg.translate3D(-widthmm / 2 + 1100, lengthmm / 2, offsetZ).transform(model);
		Geometry3D pos2 = csg.translate3D(widthmm / 2 - 1100, -lengthmm / 2, offsetZ).transform(model);
		Geometry3D pos3 = csg.translate3D(widthmm / 2 - 1100, lengthmm / 2, offsetZ).transform(model);
		return csg.union3D(pos0, pos1, pos2, pos3);
	}

	private static Geometry3D getRemModel(List<Wood> woodItems, double widthmm, double lengthmm, JavaCSG csg)
	{
		int carportHang = 1000;
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

		Geometry3D model = csg.box3D(remItem.getHeight() * 10, lengthmm - carportHang, remItem.getWidth() * 10, false);

		Geometry3D rem0 = csg.translate3D(- (widthmm / 2) + remSpacingRoff, 0, offsetZ).transform(model);
		Geometry3D rem1 = csg.translate3D(widthmm / 2 - remSpacingRoff, 0, offsetZ).transform(model);
		
//		Geometry3D model = csg.box3D(remItem.getHeight() * 10, remItem.getLength() * 10, remItem.getWidth() * 10, false);
//		Geometry3D rem0 = csg.translate3D(-widthmm / 2 + 1100, 0, offsetZ).transform(model);
//		Geometry3D rem1 = csg.translate3D(widthmm / 2 - 1100, 0, offsetZ).transform(model);

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
		double initialPos = -(lengthmm / 2) + rafterItem.getWidth() + 50;

		Geometry3D model = csg.box3D(rafterItem.getLength() * 10, rafterItem.getHeight() * 10, rafterItem.getWidth() * 10, false);

		for (int i = 0; i < amount; i++)
		{
			rafters.add(csg.translate3D(0, initialPos + offset, offsetZ).transform(model));
			offset = lengthmm / amount + offset + 130;
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
		return csg.box3D(widthmm, lengthmm, roofItem.getHeight(), false);
	}
}
