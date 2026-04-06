package Shapes;


/**
 * Write a description of class Crazy here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class CrazyLid extends Lid
{

    /**
     * Constructor for objects of class Crazy
     */
    public CrazyLid(int number, String color){
        super(number,color);
    }
    
    public String getType(){
        return "Crazy";
    }
}