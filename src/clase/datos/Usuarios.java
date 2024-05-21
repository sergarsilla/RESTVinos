package clase.datos;

import java.util.ArrayList;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usuarios")
public class Usuarios {
	private Link siguientePagina = null;
	private Link anteriorPagina = null;
	private ArrayList<Usuario> usuarios;

	public Usuarios() {
		this.usuarios = new ArrayList<Usuario>();
	}

	@XmlElement(name="usuario")
	public ArrayList<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(ArrayList<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Link getAnteriorPagina() {
		return anteriorPagina;
	}

	public void setAnteriorPagina(Link anteriorPagina) {
		this.anteriorPagina = anteriorPagina;
	}

	public Link getSiguientePagina() {
		return siguientePagina;
	}

	public void setSiguientePagina(Link siguientePagina) {
		this.siguientePagina = siguientePagina;
	}
}
