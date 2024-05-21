package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "puntuaciones")
public class Puntuaciones {
	private Link siguientePagina = null;
	private Link anteriorPagina = null;
	private ArrayList<Puntuacion> puntuaciones;

	public Puntuaciones() {
		this.puntuaciones = new ArrayList<Puntuacion>();
	}

	@XmlElement(name="puntuacion")
	public ArrayList<Puntuacion> getPuntuaciones() {
		return puntuaciones;
	}

	public void setPuntuaciones(ArrayList<Puntuacion> puntuaciones) {
		this.puntuaciones = puntuaciones;
	}

	public Link getSiguientePagina() {
		return siguientePagina;
	}

	public void setSiguientePagina(Link siguientePagina) {
		this.siguientePagina = siguientePagina;
	}

	public Link getAnteriorPagina() {
		return anteriorPagina;
	}

	public void setAnteriorPagina(Link anteriorPagina) {
		this.anteriorPagina = anteriorPagina;
	}
}
