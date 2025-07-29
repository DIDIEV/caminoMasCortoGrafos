
package teoria;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Grafo {
  private List<Vertice> vertices;
  private List<Arista> aristas;
  List<Camino> caminos;
  private Vertice origen;
  private Vertice destino;
  private float pesoActual;

  public Grafo() {

    this.vertices = new ArrayList<Vertice>();
    this.aristas = new ArrayList<Arista>();
    this.caminos = new ArrayList<Camino>();
    this.pesoActual = 0;
  }

  public void setPesoActual(float pesoActual) {
    this.pesoActual = pesoActual;
  }

  public void setOrigenGlob(Vertice origen) {
    this.origen = origen;
  }

  public Vertice getOrigenGlob() {
    return this.origen;
  }

  public void setDestinoGlob(Vertice destino) {
    this.destino = destino;
  }

  public Vertice getDestinoGlob() {
    return this.destino;
  }

  public void addVertice(String nombre) {
    this.vertices.add(new Vertice(nombre));
  }

  public void addArista(int origen, int destino, float peso) {
    Arista nuevaArista = new Arista(origen, destino, peso);
    boolean encontrada = false;

    // Buscar si ya existe una arista entre estos vértices
    for (int i = 0; i < this.aristas.size(); i++) {
      Arista aristaExistente = this.aristas.get(i);
      if (aristaExistente.getOrigen() == nuevaArista.getOrigen() && 
          aristaExistente.getDestino() == nuevaArista.getDestino()) {
        // Si la nueva arista tiene menor peso, reemplazar
        if (nuevaArista.getPeso() < aristaExistente.getPeso()) {
          this.aristas.set(i, nuevaArista);
        }
        encontrada = true;
        break;
      }
    }
    if (!encontrada) {
      this.aristas.add(nuevaArista);
    }
  }

  
  public float[][] crearMatrizAdyacencia(List<Arista> aristas) {
    int I = this.vertices.size();
    int J = this.vertices.size();
    float[][] matriz = new float[I][J];
    for (Arista arista : aristas) {
      int origen = arista.getOrigen();
      int destino = arista.getDestino();
      //System.out.println(Integer.toString(origen) + "," + Integer.toString(destino));
      matriz[origen][destino] = arista.getPeso();
    }
    return matriz;
  }

  public List<Vertice> getVertices() {
    return this.vertices;
  }

  public List<Arista> getAristas() {
    return this.aristas;
  }

  public List<Vertice> getAdyacentes(Vertice origen, float[][] miMatriz) {
    List<Vertice> adOrigen = new ArrayList<Vertice>();
    for (Vertice v : this.vertices) {
      if (miMatriz[origen.getId()][v.getId()] != 0) {
        adOrigen.add(v);
      }
    }
    return adOrigen;
  }

  public List<Vertice> getInsidentes(Vertice destino, float[][] miMatriz) {
    List<Vertice> insDestino = new ArrayList<Vertice>();
    for (Vertice v : this.vertices) {
      if (miMatriz[v.getId()][destino.getId()] != 0) {
        insDestino.add(v);
      }
    }
    return insDestino;
  }

  // Basicamente las condiciones especiales son pegar las partes de los caminos
  // que se van creando.
  public void condicionEspecialOrigen(Vertice origen, Camino camino, List<Camino> caminosContinuados) {
    if (!origen.equals(this.origen)) {
      if (camino.isTerminado() && !camino.getCamino().get(camino.getCamino().size() - 1).equals(this.destino)) {
        camino.noTerminado();
      }
      // Crear una copia de la lista para evitar ConcurrentModificationException
      List<Camino> caminosTemp = new ArrayList<Camino>(this.caminos);
      for (Camino cm : caminosTemp) {
        if (cm.getCamino().size() > 0) {
          Vertice verticeFinal = cm.getCamino().get(cm.getCamino().size() - 1);
          if (verticeFinal.getId() == origen.getId()) {
            caminosContinuados.add(cm);
            List<Vertice> caminoConBack = new ArrayList<Vertice>(cm.getCamino());
            if (camino.getCamino().size() > 1) {
              caminoConBack.addAll(camino.getCamino().subList(1, camino.getCamino().size()));
            }
            camino.setCamino(caminoConBack);
          }
        }
      }
    }
  }

  public void condicionEspecialDestino(Vertice destino, Camino camino, List<Camino> caminosContinuados) {
    if (!destino.equals(this.destino)) {
      if (camino.isTerminado() && !camino.getCamino().get(0).equals(this.origen)) {
        camino.noTerminado();
      }
      // Crear una copia de la lista para evitar ConcurrentModificationException
      List<Camino> caminosTemp = new ArrayList<Camino>(this.caminos);
      for (Camino cm : caminosTemp) {
        if (cm.getCamino().size() > 0) {
          Vertice verticeInicial = cm.getCamino().get(0);
          if (verticeInicial.getId() == destino.getId()) {
            caminosContinuados.add(cm);
            List<Vertice> caminoConBack = new ArrayList<Vertice>(camino.getCamino());
            if (cm.getCamino().size() > 1) {
              caminoConBack.addAll(cm.getCamino().subList(1, cm.getCamino().size()));
            }
            camino.setCamino(caminoConBack);
          }
        }
      }
    }
  }

  // Algoritmo.
  public String caminoMasCorto(Vertice origen, Vertice destino, float[][] matriz) {
      int n = this.vertices.size();
      float[] distancias = new float[n];
      Vertice[] anteriores = new Vertice[n];
      boolean[] visitados = new boolean[n];

      // Inicialización
      for (int i = 0; i < n; i++) {
          distancias[i] = Float.MAX_VALUE;
          anteriores[i] = null;
          visitados[i] = false;
      }

      int distanciaOrigen = origen.getId();
      System.out.println(distanciaOrigen);
      distancias[distanciaOrigen] = 0;

      for (int i = 0; i < n; i++) {
          // Buscar el vértice no visitado con menor distancia
          int u = -1;
          float minDist = Float.MAX_VALUE;
          for (int j = 0; j < n; j++) {
              if (!visitados[j] && distancias[j] < minDist) {
                  minDist = distancias[j];
                  u = j;
              }
          }

          if (u == -1) break; // No hay más vértices alcanzables

          visitados[u] = true;

          // Actualizar distancias de los vecinos
          for (int v = 0; v < n; v++) {
              float pesoUV = matriz[u][v];
              if (pesoUV > 0 && !visitados[v]) {
                  float nuevaDistancia = distancias[u] + pesoUV;
                  if (nuevaDistancia < distancias[v]) {
                      distancias[v] = nuevaDistancia;
                      anteriores[v] = this.vertices.get(u);
                  }
              }
          }
      }

      // Reconstruir camino como String
      LinkedList<Vertice> camino = new LinkedList<>();
      for (Vertice at = destino; at != null; at = anteriores[at.getId()]) {
          camino.addFirst(at);
      }

      if (camino.isEmpty() || !camino.getFirst().equals(origen)) {
          return "No hay camino entre " + origen.getNombre() + " y " + destino.getNombre();
      }

      StringBuilder resultado = new StringBuilder();
      for (int i = 0; i < camino.size(); i++) {
          resultado.append(camino.get(i).getNombre());
          if (i != camino.size() - 1) {
              resultado.append(" -> ");
          }
      }

      resultado.append(" (Costo total: ").append(distancias[destino.getId()]).append(")");
      return resultado.toString();
  }

}