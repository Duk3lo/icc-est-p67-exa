package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pedido {
  private String cliente;
  private String codigoPostal;
  private List<Integer> prioridadaes;

  public Pedido(String cliente, String codigoPostal, List<Integer> prioridadaes) {
    this.cliente = cliente;
    this.codigoPostal = codigoPostal;
    this.prioridadaes = prioridadaes;
  }

  public String getCliente() {
    return cliente;
  }

  public String getCodigoPostal() {
    return codigoPostal;
  }

  public void setCodigoPostal(String codigoPostal) {
    this.codigoPostal = codigoPostal;
  }

  public List<Integer> getPrioridadaes() {
    return prioridadaes;
  }

  public void setPrioridadaes(List<Integer> prioridadaes) {
    this.prioridadaes = prioridadaes;
  }

  public int getZona() {
    return Integer.valueOf(codigoPostal.split("-")[1]);
  }

  public int getUrgencia() {
    int sumaPrioridades = 0;

    for (Integer p : prioridadaes) {
      if (p % 3 == 0) {
        sumaPrioridades += p;
      }
    }

    Set<Character> vocalesUnicas = new HashSet<>();
    String nombreLower = cliente.toLowerCase();

    for (char c : nombreLower.toCharArray()) {
      if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
        vocalesUnicas.add(c);
      }
    }

    int cantidadVocales = vocalesUnicas.size();

    return sumaPrioridades * cantidadVocales;
  }

  @Override
  public String toString() {
    return "Pedido{" +
        "cliente='" + cliente + '\'' +
        ", zona=" + getZona() +
        ", urgencia=" + getUrgencia() +
        '}';
  }

}
