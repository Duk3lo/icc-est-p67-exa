package test;

import models.Pedido;
import validaciones.ValidacionesPedido;

import java.util.*;

import org.junit.jupiter.api.Test;

import controllers.PedidoController;

public class PedidoControllerTest {

    PedidoController controller = new PedidoController();

    static List<Pedido> crearPedidos() {
        // 40 pedidos con características variadas para probar todos los casos
        List<Pedido> pedidos = Arrays.asList(
                // Grupo 1: Pedidos con urgencia similar (~60-66)
                new Pedido("Ana Maria", "28045-220", Arrays.asList(6, 15, 8, 12, 5)),
                new Pedido("Luis Garcia", "28045-180", Arrays.asList(9, 12, 15)),
                new Pedido("Maria Lopez", "28045-150", Arrays.asList(6, 9, 18, 7)),

                // Grupo 2: Pedidos con urgencia alta (~100-120)
                new Pedido("Jose Luis", "28045-250", Arrays.asList(9, 18, 21, 12)),
                new Pedido("Carmen Rosa", "28045-200", Arrays.asList(15, 18, 12, 9)),
                new Pedido("Pedro Martinez", "28045-180", Arrays.asList(12, 24, 6)),

                // Grupo 3: Nombres repetidos con diferentes zonas
                new Pedido("Ana Maria", "28045-240", Arrays.asList(9, 18, 15)),
                new Pedido("Ana Maria", "28045-190", Arrays.asList(12, 6, 21)),
                new Pedido("Luis Garcia", "28045-210", Arrays.asList(6, 12, 18, 4)),

                // Grupo 4: Zonas bajas (< 150) - No pasarán filtro con umbral 150
                new Pedido("Roberto Silva", "28045-100", Arrays.asList(3, 9, 15, 12)),
                new Pedido("Sofia Ruiz", "28045-120", Arrays.asList(6, 18, 9)),
                new Pedido("Diego Torres", "28045-080", Arrays.asList(12, 24, 15)),
                new Pedido("Laura Gomez", "28045-140", Arrays.asList(9, 21, 6)),

                // Grupo 5: Urgencias iguales para probar agrupación (urgencia=90)
                new Pedido("Carlos Diaz", "28045-260", Arrays.asList(18, 15, 12)),
                new Pedido("Elena Mora", "28045-230", Arrays.asList(6, 9, 15, 12, 3)),
                new Pedido("Francisco Vega", "28045-270", Arrays.asList(21, 18, 6)),

                // Grupo 6: Pedidos duplicados (mismo cliente y misma zona - para probar
                // TreeSet)
                new Pedido("Ana Maria", "28045-220", Arrays.asList(6, 12, 9)),
                new Pedido("Luis Garcia", "28045-180", Arrays.asList(15, 18, 21)),

                // Grupo 7: Urgencias bajas (~30-45)
                new Pedido("Isabel Cruz", "28045-160", Arrays.asList(3, 6, 9, 2)),
                new Pedido("Miguel Angel", "28045-175", Arrays.asList(6, 12, 15)),
                new Pedido("Patricia Ramos", "28045-155", Arrays.asList(3, 9, 5)),

                // Grupo 8: Zonas altas (> 250)
                new Pedido("Alberto Rojas", "28045-290", Arrays.asList(18, 24, 21)),
                new Pedido("Beatriz Soto", "28045-280", Arrays.asList(12, 18, 24, 9)),
                new Pedido("Raul Jimenez", "28045-300", Arrays.asList(15, 21, 27)),

                // Grupo 9: Más pedidos para variedad
                new Pedido("Sandra Perez", "28045-165", Arrays.asList(9, 15, 18, 6)),
                new Pedido("Andres Castro", "28045-195", Arrays.asList(12, 18, 9)),
                new Pedido("Veronica Ortiz", "28045-225", Arrays.asList(6, 15, 21, 12)),
                new Pedido("Gabriel Nuñez", "28045-185", Arrays.asList(9, 18, 12, 3)),

                // Grupo 10: Urgencias medias (~75-85)
                new Pedido("Claudia Herrera", "28045-205", Arrays.asList(18, 9, 15)),
                new Pedido("Fernando Vargas", "28045-215", Arrays.asList(12, 21, 18)),
                new Pedido("Monica Reyes", "28045-170", Arrays.asList(9, 18, 6)),

                // Grupo 11: Casos límite y especiales
                new Pedido("Oscar Mendez", "28045-199", Arrays.asList(15, 30, 18)),
                new Pedido("Teresa Campos", "28045-245", Arrays.asList(12, 9, 21, 6)),
                new Pedido("Javier Santos", "28045-255", Arrays.asList(18, 12, 24)),

                // Grupo 12: Más repeticiones de nombres
                new Pedido("Jose Luis", "28045-265", Arrays.asList(15, 18, 9)),
                new Pedido("Carmen Rosa", "28045-235", Arrays.asList(9, 12, 21)),

                // Grupo 13: Completar hasta 40
                new Pedido("Daniela Flores", "28045-188", Arrays.asList(6, 18, 12, 9)),
                new Pedido("Ricardo Morales", "28045-277", Arrays.asList(21, 15, 24)),
                new Pedido("Adriana Paz", "28045-192", Arrays.asList(9, 15, 12)),
                new Pedido("Hector Cabrera", "28045-222", Arrays.asList(18, 21, 12, 6)),
                new Pedido("Natalia Rios", "28045-248", Arrays.asList(12, 24, 18)));
        return pedidos;
    }

    @Test
    public void testMakeZona() {
        Pedido p = new Pedido("Juan Perez", "28045-180", Arrays.asList(10, 15, 20));
        ValidacionesPedido.validarCampoZona(p, p.getZona()); // 180 son los últimos 3 dígitos

        p = new Pedido("Maria Santos", "28045-250", Arrays.asList(6, 14, 3));
        ValidacionesPedido.validarCampoZona(p, p.getZona());

        p = new Pedido("Carlos Rodriguez", "28045-099", Arrays.asList(17, 11, 12));
        ValidacionesPedido.validarCampoZona(p, p.getZona());
    }

    @Test
    public void testMakeUrgencia() {
        // Ejemplo: "Ana Maria" tiene vocales: a, i (2 únicas)
        // Prioridades [6, 15, 8, 12, 5]: múltiplos de 3 son 6, 15, 12 = 33
        // Urgencia = 33 * 2 = 66
        Pedido p = new Pedido("Ana Maria", "28045-220", Arrays.asList(6, 15, 8, 12, 5));
        System.out.println("Ejecutando testMakeUrgencia");
        ValidacionesPedido.validarCampoUrgencia(p, p.getUrgencia());

        // Otro ejemplo: "Jose Luis" tiene vocales: o, e, u, i (4 únicas)
        // Prioridades [9, 18, 21, 12]: múltiplos de 3 son 9, 18, 21, 12 = 60
        // Urgencia = 60 * 4 = 240
        p = new Pedido("Jose Luis", "28045-250", Arrays.asList(9, 18, 21, 12));
        ValidacionesPedido.validarCampoUrgencia(p, p.getUrgencia());
    }

    @Test
    public void testFiltrarPorZona() {
        List<Pedido> pedidos = crearPedidos();
        Stack<Pedido> resultado = controller.filtrarPorZona(pedidos, 150);
        ValidacionesPedido.validarResultadoA(new HashSet<>(resultado), resultado.size(), 150);
    }

    @Test
    public void testOrdenarPorZona() {
        List<Pedido> pedidos = crearPedidos();
        Stack<Pedido> pila = controller.filtrarPorZona(pedidos, 150);
        ValidacionesPedido.validarResultadoA(new HashSet<>(pila), pila.size(), 150);

        Set<Pedido> resultado = controller.ordenarPorZona(pila);
        ValidacionesPedido.validarResultadoB(resultado, pedidos);
    }

    @Test
    public void testAgruparPorUrgencia() {
        List<Pedido> pedidos = crearPedidos();
        Map<Integer, Queue<Pedido>> resultado = controller.agruparPorUrgencia(pedidos);
        ValidacionesPedido.validarResultadoC(resultado, pedidos);
    }

    @Test
    public void testExplotarGrupo() {
        List<Pedido> pedidos = crearPedidos();
        Map<Integer, Queue<Pedido>> mapa = controller.agruparPorUrgencia(pedidos);
        Stack<Pedido> resultado = controller.explotarGrupo(mapa);

        ValidacionesPedido.validarResultadoD(resultado, pedidos);
    }
}
