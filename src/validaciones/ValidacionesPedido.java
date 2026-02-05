package validaciones;

import models.Pedido;
import org.junit.jupiter.api.Assertions;

import java.util.*;

/**
 * Utilidades de validación para las pruebas de PedidoController.
 * Contiene validadores usados por los tests unitarios:
 *  - validarCampoZona
 *  - validarCampoUrgencia
 *  - validarResultadoA
 *  - validarResultadoB
 *  - validarResultadoC
 *  - validarResultadoD
 *
 * Cada método lanza AssertionError (a través de JUnit Assertions) si la validación falla.
 */
public class ValidacionesPedido {

    /**
     * Valida que el campo zona del pedido coincide con los últimos 3 dígitos del código postal.
     *
     * @param p            Pedido a validar
     * @param zonaEsperada valor esperado (normalmente p.getZona())
     */
    public static void validarCampoZona(Pedido p, int zonaEsperada) {
        Assertions.assertNotNull(p, "Pedido nulo en validarCampoZona");
        String codigo = p.getCodigoPostal();
        Assertions.assertNotNull(codigo, "Código postal nulo en validarCampoZona");

        // Tomar la parte después del guion si existe, si no tomar los 3 últimos caracteres
        String ultimaParte;
        if (codigo.contains("-")) {
            String[] parts = codigo.split("-");
            ultimaParte = parts[parts.length - 1];
        } else {
            if (codigo.length() < 3) {
                Assertions.fail("Código postal inválido: " + codigo);
                return;
            }
            ultimaParte = codigo.substring(codigo.length() - 3);
        }

        // Parsear como entero (p.ej "080" -> 80)
        int zonaCalculada;
        try {
            zonaCalculada = Integer.parseInt(ultimaParte);
        } catch (NumberFormatException e) {
            Assertions.fail("No se pudo parsear la parte final del código postal: " + ultimaParte);
            return;
        }

        Assertions.assertEquals(zonaEsperada, zonaCalculada,
                () -> "Zona calculada incorrecta para cliente=" + p.getCliente()
                        + " códigoPostal=" + p.getCodigoPostal()
                        + " -> esperada=" + zonaEsperada + " calculada=" + zonaCalculada);
    }

    /**
     * Valida que el campo urgencia del pedido está calculado correctamente:
     * urgencia = (suma de prioridades que son múltiplos de 3) * (cantidad de vocales únicas en el nombre)
     *
     * @param p               Pedido a validar
     * @param urgenciaEsperada valor esperado (normalmente p.getUrgencia())
     */
    public static void validarCampoUrgencia(Pedido p, int urgenciaEsperada) {
        Assertions.assertNotNull(p, "Pedido nulo en validarCampoUrgencia");
        List<Integer> prioridades = p.getPrioridades();
        Assertions.assertNotNull(prioridades, "Prioridades nulas en validarCampoUrgencia");

        int sumaMultiplos3 = 0;
        for (Integer val : prioridades) {
            if (val != null && val % 3 == 0) sumaMultiplos3 += val;
        }

        // Contar vocales únicas en el nombre (ignorando mayúsculas/minúsculas)
        String nombre = p.getCliente() == null ? "" : p.getCliente().toLowerCase(Locale.ROOT);
        Set<Character> vocales = new HashSet<>();
        for (char c : nombre.toCharArray()) {
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                vocales.add(c);
            }
        }
        int cantidadVocalesUnicas = vocales.size();

        int urgenciaCalculada = sumaMultiplos3 * cantidadVocalesUnicas;

        Assertions.assertEquals(urgenciaEsperada, urgenciaCalculada,
                () -> "Urgencia calculada incorrecta para cliente=" + p.getCliente()
                        + " prioridades=" + prioridades
                        + " -> esperada=" + urgenciaEsperada + " calculada=" + urgenciaCalculada);
    }

    /**
     * Valida el resultado del método A:
     * - Todos los pedidos del conjunto deben tener zona > umbral
     * - El tamaño informado debe coincidir con el tamaño del conjunto
     *
     * @param conjuntoPedidosConLaPila conjunto creado a partir de la pila (HashSet por ejemplo)
     * @param tamañoPila               tamaño retornado por la pila original
     * @param umbral                   umbral usado en el filtrado
     */
    public static void validarResultadoA(Set<Pedido> conjuntoPedidosConLaPila, int tamañoPila, int umbral) {
        Assertions.assertNotNull(conjuntoPedidosConLaPila, "Conjunto nulo en validarResultadoA");
        Assertions.assertEquals(conjuntoPedidosConLaPila.size(), tamañoPila,
                "El tamaño de la pila no coincide con el tamaño del conjunto (duplicados?)");

        for (Pedido p : conjuntoPedidosConLaPila) {
            Assertions.assertTrue(p.getZona() > umbral,
                    "Encontrado pedido con zona <= umbral en resultado A: cliente=" + p.getCliente()
                            + " zona=" + p.getZona() + " umbral=" + umbral);
        }
    }

    /**
     * Valida el resultado del método B:
     * - Comprueba que la iteración del Set (TreeSet) cumple la ordenación:
     *   zona descendente; si zonas iguales cliente ascendente
     * - Comprueba que no existen duplicados por (cliente,zona)
     * - Comprueba que cada elemento proviene de la lista original (por cliente+zona)
     *
     * @param resultado Set devuelto por ordenarPorZona (TreeSet)
     * @param pedidos   lista original de pedidos
     */
    public static void validarResultadoB(Set<Pedido> resultado, List<Pedido> pedidos) {
        Assertions.assertNotNull(resultado, "Resultado nulo en validarResultadoB");
        Assertions.assertNotNull(pedidos, "Lista original nula en validarResultadoB");

        // 1) Comprobar orden y unicidad
        List<Pedido> lista = new ArrayList<>(resultado);

        // Unicidad (cliente+zona)
        Set<String> claves = new HashSet<>();
        for (Pedido p : lista) {
            String clave = p.getCliente() + "#" + p.getZona();
            Assertions.assertFalse(claves.contains(clave),
                    "Duplicado encontrado en resultado B para cliente+zona: " + clave);
            claves.add(clave);
        }

        // Orden: zona DESC, cliente ASC cuando zonas iguales
        for (int i = 0; i < lista.size() - 1; i++) {
            Pedido p1 = lista.get(i);
            Pedido p2 = lista.get(i + 1);

            if (p1.getZona() < p2.getZona()) {
                Assertions.fail("Orden incorrecto (zona descendente) en resultado B: "
                        + p1.getCliente() + "(" + p1.getZona() + ") debe ir antes de "
                        + p2.getCliente() + "(" + p2.getZona() + ")");
            }
            if (p1.getZona() == p2.getZona()) {
                // cliente ascendente (ignorar mayúsculas/minúsculas para la comprobación)
                if (p1.getCliente().compareToIgnoreCase(p2.getCliente()) > 0) {
                    Assertions.fail("Orden incorrecto (cliente ascendente) para misma zona en resultado B: "
                            + p1.getCliente() + " vs " + p2.getCliente() + " zona=" + p1.getZona());
                }
            }
        }

        // 2) Comprobar que cada elemento del resultado existe en la lista original (por cliente+zona)
        Set<String> originales = new HashSet<>();
        for (Pedido p : pedidos) {
            originales.add(p.getCliente() + "#" + p.getZona());
        }
        for (Pedido r : resultado) {
            String clave = r.getCliente() + "#" + r.getZona();
            Assertions.assertTrue(originales.contains(clave),
                    "Elemento en resultado B no pertenece a la lista original: " + clave);
        }
    }

    /**
     * Valida el resultado del método C:
     * - El TreeMap debe contener exactamente las claves de urgencia presentes en la lista original
     * - Para cada urgencia, la Queue debe contener los pedidos en el mismo orden que aparecen en la lista original
     *
     * @param resultado TreeMap<Integer, Queue<Pedido>> retornado por agruparPorUrgencia
     * @param pedidos   lista original
     */
    public static void validarResultadoC(Map<Integer, Queue<Pedido>> resultado, List<Pedido> pedidos) {
        Assertions.assertNotNull(resultado, "Resultado nulo en validarResultadoC");
        Assertions.assertNotNull(pedidos, "Lista original nula en validarResultadoC");

        // Construir el agrupamiento esperado
        Map<Integer, List<Pedido>> esperado = new TreeMap<>();
        for (Pedido p : pedidos) {
            esperado.putIfAbsent(p.getUrgencia(), new ArrayList<>());
            esperado.get(p.getUrgencia()).add(p);
        }

        // Comparar conjuntos de claves
        Set<Integer> clavesResultado = new TreeSet<>(resultado.keySet());
        Set<Integer> clavesEsperado = new TreeSet<>(esperado.keySet());
        Assertions.assertEquals(clavesEsperado, clavesResultado,
                "Conjunto de urgencias (claves) no coincide entre esperado y resultado");

        // Para cada urgencia, comparar elementos y orden
        for (Integer urg : clavesEsperado) {
            Queue<Pedido> colaRes = resultado.get(urg);
            List<Pedido> listaEsp = esperado.get(urg);

            Assertions.assertNotNull(colaRes, "Cola nula para urgencia " + urg);
            Assertions.assertEquals(listaEsp.size(), colaRes.size(),
                    "Tamaño diferente para urgencia " + urg);

            Iterator<Pedido> it = colaRes.iterator();
            int idx = 0;
            while (it.hasNext()) {
                Pedido actual = it.next();
                Pedido esperadoPedido = listaEsp.get(idx);

                // Comparación por cliente y zona (suficiente para identificar)
                Assertions.assertEquals(esperadoPedido.getCliente(), actual.getCliente(),
                        "Pedido distinto en posición " + idx + " para urgencia " + urg);
                Assertions.assertEquals(esperadoPedido.getZona(), actual.getZona(),
                        "Zona distinta en posición " + idx + " para urgencia " + urg);

                idx++;
            }
        }
    }

    /**
     * Valida el resultado del método D:
     * - Calcula cuál es el grupo esperado (la urgencia con mayor cantidad de pedidos;
     *   en caso de empate se elige la de mayor valor de urgencia)
     * - Construye la pila esperada (Stack) con los elementos de la Queue de ese grupo en orden LIFO
     * - Compara tamaño y contenido (por cliente y zona) con la pila retornada
     *
     * @param resultado pila retornada por explotarGrupo
     * @param pedidos   lista original
     */
    public static void validarResultadoD(Stack<Pedido> resultado, List<Pedido> pedidos) {
        Assertions.assertNotNull(resultado, "Resultado nulo en validarResultadoD");
        Assertions.assertNotNull(pedidos, "Lista original nula en validarResultadoD");

        // Agrupar por urgencia (igual que en el controller)
        Map<Integer, List<Pedido>> agrupado = new TreeMap<>();
        for (Pedido p : pedidos) {
            agrupado.putIfAbsent(p.getUrgencia(), new ArrayList<>());
            agrupado.get(p.getUrgencia()).add(p);
        }

        // Encontrar la urgencia con más elementos; si empate, la de mayor urgencia
        int maxCantidad = -1;
        int urgenciaSeleccionada = -1;
        for (Map.Entry<Integer, List<Pedido>> e : agrupado.entrySet()) {
            int urg = e.getKey();
            int cantidad = e.getValue().size();
            if (cantidad > maxCantidad || (cantidad == maxCantidad && urg > urgenciaSeleccionada)) {
                maxCantidad = cantidad;
                urgenciaSeleccionada = urg;
            }
        }

        // Construir la pila esperada a partir de la lista (la Queue en controller mantiene el orden de aparición)
        List<Pedido> colaEsperada = agrupado.getOrDefault(urgenciaSeleccionada, Collections.emptyList());
        Stack<Pedido> pilaEsperada = new Stack<>();
        for (Pedido p : colaEsperada) {
            pilaEsperada.push(p);
        }

        // Comparar tamaños
        Assertions.assertEquals(pilaEsperada.size(), resultado.size(),
                "Tamaño de pila explotada no coincide con lo esperado para urgencia " + urgenciaSeleccionada);

        // Comparar contenido por posición (Stack.get(i) permite comparar el mismo orden)
        for (int i = 0; i < pilaEsperada.size(); i++) {
            Pedido exp = pilaEsperada.get(i);
            Pedido act = resultado.get(i);

            Assertions.assertEquals(exp.getCliente(), act.getCliente(),
                    "Cliente distinto en pila explotada en índice " + i);
            Assertions.assertEquals(exp.getZona(), act.getZona(),
                    "Zona distinta en pila explotada en índice " + i);
        }
    }
}
