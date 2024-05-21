package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.*;

import java.sql.Date;
import java.util.*;

@XmlRootElement(name = "usuario")
public class Usuario {
	private int id;
	private String nombre;
	private String fechaNacimiento;
	private String email;
	private ArrayList<Puntuacion> puntuaciones;
	private ArrayList<Usuario> seguidores;

	public Usuario(String nombre, String fechaNacimiento, String email) {
		this.nombre = nombre;
		this.fechaNacimiento = fechaNacimiento;
		this.email = email;
//		puntuaciones = new ArrayList<Puntuacion>();
//		seguidores = new ArrayList<Usuario>();
	}

	public Usuario() {

	}

	@XmlAttribute(required = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElementWrapper(name = "puntuaciones")
	@XmlElement(name = "puntuacion")
	public ArrayList<Puntuacion> getPuntuaciones() {
		return puntuaciones;
	}

	public void setPuntuaciones(ArrayList<Puntuacion> puntuaciones) {
		this.puntuaciones = puntuaciones;
	}

	@XmlElementWrapper(name = "seguidores")
	@XmlElement(name = "usuario")
	public ArrayList<Usuario> getSeguidores() {
		return seguidores;
	}

	public void setSeguidores(ArrayList<Usuario> seguidores) {
		this.seguidores = seguidores;
	}

}
