package clase.datos;

public class Vino {

	private int id;
	private String nombre;
	private String fechaAdicion;
	private String uva;
	private String tipo;
	private String origen;
	private int ano;
	
	public Vino(int id){
		this.id = id;
	}

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

	public String getFechaAdicion() {
		return fechaAdicion;
	}

	public void setFechaAdicion(String fechaAdicion) {
		this.fechaAdicion = fechaAdicion;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public String getUva() {
		return uva;
	}

	public void setUva(String uva) {
		this.uva = uva;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
