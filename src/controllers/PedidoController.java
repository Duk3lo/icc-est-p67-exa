package controllers;

import java.util.*;
import models.Pedido;

public class PedidoController {

  public Stack<Pedido> filtrarPorZona(List<Pedido> pedidos, int umbral) {
    Stack<Pedido> stack = new Stack<>();

    for (Pedido pedido : pedidos) {
      if (pedido.getZona() > umbral) {
        stack.push(pedido);
      }
    }
    return stack;
  }

  public TreeSet<Pedido> ordenarPorZona(Stack<Pedido> pila) {
    TreeSet<Pedido> ordenado = new TreeSet<>((p1, p2) -> {
      int cmpZona = Integer.compare(p2.getZona(), p1.getZona());
      if (cmpZona != 0)
        return cmpZona;
      int cmpCliente = p1.getCliente()
          .compareToIgnoreCase(p2.getCliente());
      if (cmpCliente != 0)
        return cmpCliente;
      return 0;
    });

    ordenado.addAll(pila);
    return ordenado;
  }

  public TreeMap<Integer, Queue<Pedido>> agruparPorUrgencia(List<Pedido> pedidos) {
    TreeMap<Integer, Queue<Pedido>> mapa = new TreeMap<>();

    for (Pedido pedido : pedidos) {
      int urgencia = pedido.getUrgencia();

      mapa.putIfAbsent(urgencia, new LinkedList<>());
      mapa.get(urgencia).offer(pedido);
    }

    return mapa;
  }

  public Stack<Pedido> explotarGrupo(Map<Integer, Queue<Pedido>> mapa) {
    int maxCantidad = -1;
    int urgenciaSeleccionada = -1;

    for (Map.Entry<Integer, Queue<Pedido>> entry : mapa.entrySet()) {
      int cantidad = entry.getValue().size();
      int urgencia = entry.getKey();

      if (cantidad > maxCantidad ||
          (cantidad == maxCantidad && urgencia > urgenciaSeleccionada)) {

        maxCantidad = cantidad;
        urgenciaSeleccionada = urgencia;
      }
    }

    Stack<Pedido> resultado = new Stack<>();

    if (urgenciaSeleccionada == -1) {
      return resultado;
    }

    Queue<Pedido> cola = mapa.get(urgenciaSeleccionada);
    for (Pedido pedido : cola) {
      resultado.push(pedido);
    }

    return resultado;
  }
}
