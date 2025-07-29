import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ejemploPrueba();
        Scanner scanner = new Scanner(System.in);

        // Crear el grafo
        Grafo grafo = new Grafo();

        // Agregar vértices
        System.out.println("=== CREACIÓN DEL GRAFO ===");
        System.out.print("Ingrese el número de vértices: ");
        int numVertices = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        for (int i = 0; i < numVertices; i++) {
            System.out.print("Ingrese el nombre del vértice " + i + ": ");
            String nombre = scanner.nextLine();
            grafo.addVertice(nombre);
        }

        // Mostrar vértices creados
        System.out.println("\nVértices creados:");
        List<Vertice> vertices = grafo.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            System.out.println("ID: " + i + " -> " + vertices.get(i).getNombre());
        }

        // Agregar aristas
        System.out.println("\n=== AGREGAR ARISTAS ===");
        System.out.print("Ingrese el número de aristas: ");
        int numAristas = scanner.nextInt();

        for (int i = 0; i < numAristas; i++) {
            System.out.println("\nArista " + (i + 1) + ":");
            System.out.print("Vértice origen (ID): ");
            int origen = scanner.nextInt();
            System.out.print("Vértice destino (ID): ");
            int destino = scanner.nextInt();
            System.out.print("Peso: ");
            float peso = scanner.nextFloat();

            grafo.addArista(origen, destino, peso);
        }

        // Crear matriz de adyacencia
        float[][] matriz = grafo.crearMatrizAdyacencia(grafo.getAristas());

        // Mostrar matriz de adyacencia
        System.out.println("\n=== MATRIZ DE ADYACENCIA ===");
        mostrarMatriz(matriz, vertices);

        // Buscar camino más corto
        System.out.println("\n=== BUSCAR CAMINO MÁS CORTO ===");
        System.out.print("Ingrese el ID del vértice origen: ");
        int origenId = scanner.nextInt();
        System.out.print("Ingrese el ID del vértice destino: ");
        int destinoId = scanner.nextInt();

        if (origenId >= 0 && origenId < vertices.size() && 
            destinoId >= 0 && destinoId < vertices.size()) {

            Vertice origen = vertices.get(origenId);
            Vertice destino = vertices.get(destinoId);

            // Establecer origen y destino globales
            grafo.setOrigenGlob(origen);
            grafo.setDestinoGlob(destino);

            System.out.println("\nBuscando camino más corto de " + 
                             origen.getNombre() + " a " + destino.getNombre() + "...");

            List<Camino> caminos = grafo.caminoMasCorto(origen, destino, matriz);

            // Mostrar resultados
            mostrarResultados(caminos, vertices);

        } else {
            System.out.println("Error: IDs de vértices inválidos.");
        }

        scanner.close();
    }

    private static void mostrarMatriz(float[][] matriz, List<Vertice> vertices) {
        System.out.print("    ");
        for (int i = 0; i < vertices.size(); i++) {
            System.out.printf("%8s", vertices.get(i).getNombre());
        }
        System.out.println();

        for (int i = 0; i < matriz.length; i++) {
            System.out.printf("%3s ", vertices.get(i).getNombre());
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j] == 0) {
                    System.out.printf("%8s", "∞");
                } else {
                    System.out.printf("%8.1f", matriz[i][j]);
                }
            }
            System.out.println();
        }
    }

    private static void mostrarResultados(List<Camino> caminos, List<Vertice> vertices) {
        if (caminos.isEmpty()) {
            System.out.println("\nNo se encontraron caminos.");
            return;
        }

        System.out.println("\n=== RESULTADOS ===");
        System.out.println("Total de caminos encontrados: " + caminos.size());

        // Encontrar el camino más corto
        Camino caminoMasCorto = null;
        float pesoMinimo = Float.MAX_VALUE;

        for (Camino camino : caminos) {
            if (camino.isTerminado() && camino.getPeso() < pesoMinimo) {
                pesoMinimo = camino.getPeso();
                caminoMasCorto = camino;
            }
        }

        if (caminoMasCorto != null) {
            System.out.println("\n*** CAMINO MÁS CORTO ***");
            System.out.print("Camino: ");
            List<Vertice> ruta = caminoMasCorto.getCamino();
            for (int i = 0; i < ruta.size(); i++) {
                System.out.print(ruta.get(i).getNombre());
                if (i < ruta.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println("\nPeso total: " + caminoMasCorto.getPeso());
        }

        System.out.println("\n*** TODOS LOS CAMINOS ***");
        int contador = 1;
        for (Camino camino : caminos) {
            System.out.print("Camino " + contador + " (ID: " + camino.getId() + "): ");
            List<Vertice> ruta = camino.getCamino();
            for (int i = 0; i < ruta.size(); i++) {
                System.out.print(ruta.get(i).getNombre());
                if (i < ruta.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println(" | Peso: " + camino.getPeso() + 
                             " | Terminado: " + (camino.isTerminado() ? "Sí" : "No"));
            contador++;
        }
    }

    // Método para crear un ejemplo de prueba predefinido
    public static void ejemploPrueba() {
        System.out.println("=== EJEMPLO DE PRUEBA ===");
        Grafo grafo = new Grafo();

        // Crear vértices
        grafo.addVertice("A");
        grafo.addVertice("B");
        grafo.addVertice("C");
        grafo.addVertice("D");
        grafo.addVertice("E");

        // Crear aristas
        grafo.addArista(0, 1, 4.0f);  // A -> B (4)
        grafo.addArista(0, 2, 2.0f);  // A -> C (2)
        grafo.addArista(1, 2, 1.0f);  // B -> C (1)
        grafo.addArista(1, 3, 5.0f);  // B -> D (5)
        grafo.addArista(2, 3, 8.0f);  // C -> D (8)
        grafo.addArista(2, 4, 10.0f); // C -> E (10)
        grafo.addArista(3, 4, 2.0f);  // D -> E (2)

        float[][] matriz = grafo.crearMatrizAdyacencia(grafo.getAristas());

        List<Vertice> vertices = grafo.getVertices();
        Vertice origen = vertices.get(0); // A
        Vertice destino = vertices.get(4); // E

        grafo.setOrigenGlob(origen);
        grafo.setDestinoGlob(destino);

        System.out.println("Buscando camino más corto de A a E...");
        List<Camino> caminos = grafo.caminoMasCorto(origen, destino, matriz);

        mostrarResultados(caminos, vertices);
    }
}