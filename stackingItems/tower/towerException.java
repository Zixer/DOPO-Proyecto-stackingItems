package tower;


/**
 * Write a description of class towerException here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class towerException extends Exception
{
    public static final String NON_EXISTENT_TYPE = "Tipo no valido";
    public static final String DUPLICATED_SIZE = "Tamaño duplicado";
    
    public towerException (String message){
        super(message);
    }
}