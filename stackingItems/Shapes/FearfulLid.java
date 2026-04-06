package Shapes;


/**
 * Write a description of class Fearful here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class FearfulLid extends Lid
{

    /**
     * Constructor for objects of class Fearful
     */
    public FearfulLid(int number, String color){
        super(number,color);
    }

    public String getType(){
        return "Fearfull";
    }
}