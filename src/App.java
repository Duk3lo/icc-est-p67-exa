import java.util.Arrays;
import java.util.List;

import controllers.PedidoController;
import models.Pedido;

public class App {

    public static void main(String[] args) throws Exception {
        // Crear la lista inicial de pedidos
        /// CODE
        List<Pedido> pedidos = crearPedidos();
        PedidoController controller = new PedidoController();

        var a = List.of(
                new Pedido("Ana", "28045-150", List.of(3, 6)),
                new Pedido("Luis", "28045-200", List.of(9)),
                new Pedido("Maria", "28045-100", List.of(12)),
                new Pedido("Pedro", "28045-250", List.of(6)));

        var pilaFiltrada = controller.filtrarPorZona(a, 150);
        pilaFiltrada.forEach(System.out::println);
        // MÉTODO A: filtrarPorZona(List<Pedido>, int umbral)
        // Debe devolver un Stack con los pedidos cuya zona sea MAYOR al umbral
        // Recorre la lista de pedidos y añade al Stack solo aquellos que cumplan la
        // condición

        System.out.println("=== MÉTODO A: Filtrar por zona > 150 ===");
        /// CODE

        var pilaFiltrada2 = controller.filtrarPorZona(List.copyOf(a), 150);
        pilaFiltrada2.forEach(System.out::println);

        // MÉTODO B: ordenarPorZona(Stack<Pedido>)
        // Debe devolver un Set (TreeSet) con los pedidos ordenados por zona ascendente
        // El TreeSet debe usar un Comparator que compare los pedidos por su zona
        // Importante: elimina duplicados basándose en cliente + zona

        System.out.println("=== MÉTODO B: Ordenar por zona ===");
        /// CODE
        var ordenados = controller.ordenarPorZona(pilaFiltrada);
        ordenados.forEach(System.out::println);

        // MÉTODO C: agruparPorUrgencia(List<Pedido>)
        // Debe devolver un TreeMap<Integer, Queue<Pedido>> donde:
        // - La clave es el nivel de urgencia
        // - El valor es una Queue (LinkedList) con todos los pedidos de esa urgencia
        // Recorre todos los pedidos y agrúpalos según su urgencia
        /// CODE
        var mapaUrgencias = controller.agruparPorUrgencia(List.copyOf(a));
        mapaUrgencias.forEach((urgencia, cola) -> System.out.println(urgencia + " -> " + cola));

        // MÉTODO D: explotarGrupo(TreeMap<Integer, Queue<Pedido>>)
        // Debe encontrar el grupo (Queue) con MÁS pedidos del TreeMap
        // Devolver un Stack con todos los pedidos de ese grupo más numeroso
        // Recorre el TreeMap, encuentra la Queue más grande y pasa sus elementos al
        // Stack
        /// CODE
        var pilaExplotada = controller.explotarGrupo(mapaUrgencias);
        pilaExplotada.forEach(System.out::println);
    }

    static List<Pedido> crearPedidos() {

        List<Pedido> pedidos = Arrays.asList(
                // Grupo 1: Pedidos con urgencia similar (~60-66)
                new Pedido("Ana Maria", "28045-220", Arrays.asList(6, 15, 8, 12, 5)), // zona=220, urgencia=66
                new Pedido("Luis Garcia", "28045-180", Arrays.asList(9, 12, 15)), // zona=180, urgencia=72
                new Pedido("Maria Lopez", "28045-150", Arrays.asList(6, 9, 18, 7)), // zona=150, urgencia=66

                // Grupo 2: Pedidos con urgencia alta (~100-120)
                new Pedido("Jose Luis", "28045-250", Arrays.asList(9, 18, 21, 12)), // zona=250, urgencia=120
                new Pedido("Carmen Rosa", "28045-200", Arrays.asList(15, 18, 12, 9)), // zona=200, urgencia=108
                new Pedido("Pedro Martinez", "28045-180", Arrays.asList(12, 24, 6)), // zona=180, urgencia=84

                // Grupo 3: Nombres repetidos con diferentes zonas
                new Pedido("Ana Maria", "28045-240", Arrays.asList(9, 18, 15)), // zona=240, urgencia=84
                new Pedido("Ana Maria", "28045-190", Arrays.asList(12, 6, 21)), // zona=190, urgencia=78
                new Pedido("Luis Garcia", "28045-210", Arrays.asList(6, 12, 18, 4)), // zona=210, urgencia=72

                // Grupo 4: Zonas bajas (< 150) - No pasarán filtro con umbral 150
                new Pedido("Roberto Silva", "28045-100", Arrays.asList(3, 9, 15, 12)), // zona=100, urgencia=78
                new Pedido("Sofia Ruiz", "28045-120", Arrays.asList(6, 18, 9)), // zona=120, urgencia=99
                new Pedido("Diego Torres", "28045-080", Arrays.asList(12, 24, 15)), // zona=80, urgencia=102
                new Pedido("Laura Gomez", "28045-140", Arrays.asList(9, 21, 6)), // zona=140, urgencia=108

                // Grupo 5: Urgencias iguales para probar agrupación (urgencia=90)
                new Pedido("Carlos Diaz", "28045-260", Arrays.asList(18, 15, 12)), // zona=260, urgencia=90
                new Pedido("Elena Mora", "28045-230", Arrays.asList(6, 9, 15, 12, 3)), // zona=230, urgencia=84
                new Pedido("Francisco Vega", "28045-270", Arrays.asList(21, 18, 6)), // zona=270, urgencia=90

                // Grupo 6: Pedidos duplicados (mismo cliente y misma zona - para probar
                // TreeSet)
                new Pedido("Ana Maria", "28045-220", Arrays.asList(6, 12, 9)), // zona=220 (DUPLICADO con línea 1)
                new Pedido("Luis Garcia", "28045-180", Arrays.asList(15, 18, 21)), // zona=180 (DUPLICADO con línea 2)

                // Grupo 7: Urgencias bajas (~30-45)
                new Pedido("Isabel Cruz", "28045-160", Arrays.asList(3, 6, 9, 2)), // zona=160, urgencia=36
                new Pedido("Miguel Angel", "28045-175", Arrays.asList(6, 12, 15)), // zona=175, urgencia=66
                new Pedido("Patricia Ramos", "28045-155", Arrays.asList(3, 9, 5)), // zona=155, urgencia=36

                // Grupo 8: Zonas altas (> 250)
                new Pedido("Alberto Rojas", "28045-290", Arrays.asList(18, 24, 21)), // zona=290, urgencia=126
                new Pedido("Beatriz Soto", "28045-280", Arrays.asList(12, 18, 24, 9)), // zona=280, urgencia=117
                new Pedido("Raul Jimenez", "28045-300", Arrays.asList(15, 21, 27)), // zona=300, urgencia=126

                // Grupo 9: Más pedidos para variedad
                new Pedido("Sandra Perez", "28045-165", Arrays.asList(9, 15, 18, 6)), // zona=165, urgencia=96
                new Pedido("Andres Castro", "28045-195", Arrays.asList(12, 18, 9)), // zona=195, urgencia=78
                new Pedido("Veronica Ortiz", "28045-225", Arrays.asList(6, 15, 21, 12)), // zona=225, urgencia=108
                new Pedido("Gabriel Nuñez", "28045-185", Arrays.asList(9, 18, 12, 3)), // zona=185, urgencia=78

                // Grupo 10: Urgencias medias (~75-85)
                new Pedido("Claudia Herrera", "28045-205", Arrays.asList(18, 9, 15)), // zona=205, urgencia=84
                new Pedido("Fernando Vargas", "28045-215", Arrays.asList(12, 21, 18)), // zona=215, urgencia=102
                new Pedido("Monica Reyes", "28045-170", Arrays.asList(9, 18, 6)), // zona=170, urgencia=66

                // Grupo 11: Casos límite y especiales
                new Pedido("Oscar Mendez", "28045-199", Arrays.asList(15, 30, 18)), // zona=199, urgencia=126
                new Pedido("Teresa Campos", "28045-245", Arrays.asList(12, 9, 21, 6)), // zona=245, urgencia=84
                new Pedido("Javier Santos", "28045-255", Arrays.asList(18, 12, 24)), // zona=255, urgencia=108

                // Grupo 12: Más repeticiones de nombres
                new Pedido("Jose Luis", "28045-265", Arrays.asList(15, 18, 9)), // zona=265, urgencia=84
                new Pedido("Carmen Rosa", "28045-235", Arrays.asList(9, 12, 21)), // zona=235, urgencia=84

                // Grupo 13: Completar hasta 40
                new Pedido("Daniela Flores", "28045-188", Arrays.asList(6, 18, 12, 9)), // zona=188, urgencia=90
                new Pedido("Ricardo Morales", "28045-277", Arrays.asList(21, 15, 24)), // zona=277, urgencia=120
                new Pedido("Adriana Paz", "28045-192", Arrays.asList(9, 15, 12)), // zona=192, urgencia=72
                new Pedido("Hector Cabrera", "28045-222", Arrays.asList(18, 21, 12, 6)), // zona=222, urgencia=102
                new Pedido("Natalia Rios", "28045-248", Arrays.asList(12, 24, 18)) // zona=248, urgencia=108
        );

        return pedidos;
    }
}
