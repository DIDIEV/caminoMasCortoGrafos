public class Arista{
  
  private int origen;
  private int destino;
  private float peso;

  public Arista(int origen, int destino, float peso){
    this.origen = origen;
    this.destino = destino;
    this.peso = peso;
  }
  
  public int getOrigen() {
  	return origen;
  }
    public void setOrigen(int origen) {
  	this.origen = origen;
  }

  public int getDestino(){
    return destino;
  }

  public void setDestino(int destino){
    this.destino = destino;
  }
  
  public float getPeso() {
  	return peso;
  }
  
  public void setPeso(float peso) {
  	this.peso = peso;
  }

  
}