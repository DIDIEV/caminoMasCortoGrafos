
public class Vertice{
  private String nombre;
  private static int contador = 0;
  private final int id;


  public Vertice(String nombre){
    this.id = contador++;
    this.nombre = nombre;
  }

  public int getId(){
    return this.id;
  }
  
  public String getNombre(){
    return this.nombre;
  }


}