import entities.OrderItem;
import entities.Wood;
import exceptions.DatabaseException;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import persistence.ConnectionPool;
import persistence.Facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main
{
	private static ConnectionPool connectionPool;
	private static JavaCSG csg;
	private static final int widthmm = 6000;
	private static final int lengthmm = 2400;
	private static double offsetZ = 0;

	public static void main(String[] args)
	{
		connectionPool = new ConnectionPool();
		csg = JavaCSGFactory.createDefault();


		try
		{
			List<Wood> woodItems = getWoods(Facade.getWoodOrderItemsByRecieptId(69, connectionPool));

			Geometry3D roof = getRoofModel(woodItems, widthmm, lengthmm, csg);

			Geometry3D rafters = getRafterModel(woodItems, widthmm, lengthmm, csg);


			/*Geometry3D box = csg.box3D(woodItems.get(1).getWidth(), woodItems.get(1).getLength(), woodItems.get(1).getHeight(), false);
			box = csg.translate3DX(woodItems.get(1).getWidth() + 10).transform(box);
			Geometry3D box1 = csg.translate3DX(woodItems.get(1).getWidth() + 30).transform(box);

			Geometry3D u = csg.union3D(box, box1);*/
			csg.view(csg.union3D(roof, rafters));

		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}

		connectionPool.close();

	}

	private static Geometry3D getRafterModel(List<Wood> woodItems, int widthmm, int lengthmm, JavaCSG csg)
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
		int amount = lengthmm / 550;
		int offset = lengthmm / amount;

		Geometry3D model = csg.box3D(rafterItem.getWidth() * 10, rafterItem.getLength() * 10, rafterItem.getHeight() * 10, false);

		for (int i = 0; i < amount; i++)
		{
			rafters.add(csg.translate3D(offset, 0, offsetZ).transform(model));
			offset *= 2;
		}

		offsetZ += rafterItem.getHeight() * 10;
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

	private static Geometry3D getRoofModel(List<Wood> woods, int widthmm, int lengthmm, JavaCSG csg)
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
