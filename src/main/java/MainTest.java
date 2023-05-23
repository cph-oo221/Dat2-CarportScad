import exceptions.DatabaseException;

public class MainTest
{
    public static void main(String[] args)
    {
        Model3D model3D = new Model3D(75);
        model3D.generate3D();
    }
}
