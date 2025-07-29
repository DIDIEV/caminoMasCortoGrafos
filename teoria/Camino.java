package teoria;
import java.util.List;
import java.util.ArrayList;

public class Camino{
  private String id;
  private static int contador = 0;
  private List<Vertice> camino;
  private float peso;
  private boolean terminado;
  private float[][] matriz;

  public Camino(float[][] matriz){
    this.id = String.valueOf(contador++);
    this.camino = new ArrayList<Vertice>();
    this.terminado = false;
    this.matriz = matriz;
  }

  public String getId(){
    return this.id;
  }

  public boolean isTerminado(){
    return this.terminado;
  }

  public void siTerminado(){
    this.terminado = true;
  }
  public void noTerminado(){
    this.terminado = false;
  }
  
  public void addVertice(Vertice v){
      if (!camino.isEmpty()) {
          Vertice ultimo = camino.get(camino.size() - 1);
          float pesoArista = matriz[ultimo.getId()][v.getId()];
          setPeso(this.peso + pesoArista);
      }
      this.camino.add(v);
  }

  public List<Vertice> getCamino(){
    return this.camino;
  }

  public void setCamino(List<Vertice> camino){
    this.camino = camino;
  }
  
  public float getPeso(){
    return this.peso;
  }
  public void setPeso(float peso){
    this.peso = peso;
  }
}