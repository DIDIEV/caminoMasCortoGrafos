import java.util.List;
import java.util.ArrayList;

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
    float[][] matriz = new float[this.vertices.size()][this.vertices.size()];
    for (Arista arista : aristas) {
      matriz[arista.getOrigen()][arista.getDestino()] = arista.getPeso();
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
  public List<Camino> caminoMasCorto(Vertice origen, Vertice destino, float[][] miMatriz) {
    boolean hayCamino = false;
    List<Camino> caminosContinuados = new ArrayList<Camino>();
    List<Vertice> adOrigen = getAdyacentes(origen, miMatriz);
    List<Vertice> insDestino = getInsidentes(destino, miMatriz);

    if (adOrigen.size() < insDestino.size()) {
        for (Vertice v : adOrigen) {
            if (v == destino) {
                Camino camino = new Camino(miMatriz);
                camino.addVertice(origen);
                camino.addVertice(v);
                camino.siTerminado();
                condicionEspecialOrigen(origen, camino, caminosContinuados);
                this.caminos.add(camino);
                if (camino.isTerminado()) {
                    hayCamino = true;
                    if (this.pesoActual != 0) {
                        if (camino.getPeso() <= pesoActual) {
                            setPesoActual(camino.getPeso());
                        } else {
                            this.caminos.remove(camino);
                        }
                    } else {
                        setPesoActual(camino.getPeso());
                    }
                }
                continue;
            } else {
                if (this.pesoActual == 0) {
                    Camino camino = new Camino(miMatriz);
                    camino.addVertice(origen);
                    camino.addVertice(v);
                    condicionEspecialOrigen(origen, camino, caminosContinuados);
                    this.caminos.add(camino);
                } else {
                    Camino camino = new Camino(miMatriz);
                    camino.addVertice(origen);
                    camino.addVertice(v);
                    condicionEspecialOrigen(origen, camino, caminosContinuados);
                    if (camino.getPeso() <= pesoActual) {
                        this.caminos.add(camino);
                    } else {
                        this.caminos.remove(camino);
                    }
                }
            }
        }

        //Recursividad
        do {
            for (Camino camino : new ArrayList<>(this.caminos)) {
                if (camino.isTerminado()) {
                    continue;
                }
                Vertice ultimoVertice = camino.getCamino().get(camino.getCamino().size() - 1);
                caminoMasCorto(ultimoVertice, this.destino, miMatriz);
            }
        } while (!todosLosCaminosTerminados());

    } else {
        for (Vertice v : insDestino) {
            if (v == origen) {
                Camino camino = new Camino(miMatriz);
                camino.addVertice(v);
                camino.addVertice(destino);
                camino.siTerminado();
                condicionEspecialDestino(destino, camino, caminosContinuados);
                this.caminos.add(camino);
                if (camino.isTerminado()) {
                    hayCamino = true;
                    if (this.pesoActual != 0) {
                        if (camino.getPeso() <= pesoActual) {
                            setPesoActual(camino.getPeso());
                        } else {
                            this.caminos.remove(camino);
                        }
                    } else {
                        setPesoActual(camino.getPeso());
                    }
                }
                continue;
            } else {
                if (this.pesoActual == 0) {
                    Camino camino = new Camino(miMatriz);
                    camino.addVertice(v);
                    camino.addVertice(destino);
                    condicionEspecialDestino(destino, camino, caminosContinuados);
                    this.caminos.add(camino);
                } else {
                    Camino camino = new Camino(miMatriz);
                    camino.addVertice(v);
                    camino.addVertice(destino);
                    condicionEspecialDestino(destino, camino, caminosContinuados);
                    if (camino.getPeso() <= pesoActual) {
                        this.caminos.add(camino);
                    } else {
                        this.caminos.remove(camino);
                    }
                }
            }
        }

        //Recursividad
        do {
            for (Camino camino : new ArrayList<>(this.caminos)) {
                if (camino.isTerminado()) {
                    continue;
                }
                Vertice primerVertice = camino.getCamino().get(0);
                caminoMasCorto(this.origen, primerVertice, miMatriz);
            }
        } while (!todosLosCaminosTerminados());
    }

    return this.caminos;
  }

  // Método auxiliar para verificar si todos los caminos están terminados
  private boolean todosLosCaminosTerminados() {
    for (Camino camino : this.caminos) {
        if (!camino.isTerminado()) {
            return false;
        }
    }
    return true;
  }
}