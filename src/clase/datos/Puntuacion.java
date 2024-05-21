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

@XmlRootElement(name = "puntuacion")
public class Puntuacion {
	private Double puntuacion;
	private int idVino;

	public Puntuacion(int idVino, Double puntuacion) {
		this.idVino = idVino;
		this.puntuacion = puntuacion;
	}

	public Puntuacion(int idVino) {
		this.idVino = idVino;
		puntuacion = null;
	}
	
	public Puntuacion(){
		
	}

	public int getIdVino() {
		return idVino;
	}

	public void setIdVino(int idVino) {
		this.idVino = idVino;
	}

	@XmlAttribute(required = false)
	public Double getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(Double puntuacion) {
		this.puntuacion = puntuacion;
	}
}
