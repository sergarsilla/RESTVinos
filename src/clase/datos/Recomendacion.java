package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "recomendacion")
public class Recomendacion {
	private Usuario usuario;
	private ArrayList<Vino> ultimosVinos;
	private ArrayList<Vino> mejoresVinos;
	private ArrayList<Vino> mejoresVinosAmigos;

	public Recomendacion() {

	}

	public Recomendacion(Usuario usuario) {
		this.usuario = usuario;
		ultimosVinos = new ArrayList<>();
		mejoresVinos = new ArrayList<>();
		mejoresVinosAmigos = new ArrayList<>();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ArrayList<Vino> getUltimosVinos() {
		return ultimosVinos;
	}

	public void setUltimosVinos(ArrayList<Vino> ultimosVinos) {
		this.ultimosVinos = ultimosVinos;
	}

	public ArrayList<Vino> getMejoresVinos() {
		return mejoresVinos;
	}

	public void setMejoresVinos(ArrayList<Vino> mejoresVinos) {
		this.mejoresVinos = mejoresVinos;
	}

	public ArrayList<Vino> getMejoresVinosAmigos() {
		return mejoresVinosAmigos;
	}

	public void setMejoresVinosAmigos(ArrayList<Vino> mejoresVinosAmigos) {
		this.mejoresVinosAmigos = mejoresVinosAmigos;
	}
}
