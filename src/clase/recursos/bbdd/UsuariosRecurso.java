package clase.recursos.bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import clase.datos.*;

@Path("/usuarios")
public class UsuariosRecurso {

	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public UsuariosRecurso() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			NamingContext envCtx = (NamingContext) ctx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/vinos");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Crea un nuevo usuario en el sistema
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUsuario(Usuario user) {

		List<Object> parametros = new ArrayList<Object>();
		String sql = "INSERT INTO vinos.usuario (nombre, fechaNacimiento, email) VALUES (?, ?, ?);";
		parametros.add(user.getNombre());
		parametros.add(user.getFechaNacimiento());
		parametros.add(user.getEmail());

		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			if (menorEdad(user)) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("El usuario debe ser mayor de edad").build();
			}
			if (emailExiste(user, conn)) {
				return Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("El email proporcionado está asociado a otro usuario")
						.build();
			}

			for (int i = 0; i < parametros.size(); i++) {
				ps.setObject(i + 1, parametros.get(i));
			}
			int affectedRows = ps.executeUpdate();

			ResultSet generatedID = ps.getGeneratedKeys();
			if (generatedID.next()) {
				user.setId(generatedID.getInt(1));
				String location = uriInfo.getAbsolutePath() + "/"
						+ user.getId();
				return Response.status(Response.Status.CREATED).entity(user)
						.header("Location", location)
						.header("Content-Location", location).build();

			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo crear el usuario").build();

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo crear el usuario").build();
		}
	}

	/*
	 * Añade un vino a la lista del usuario y le agrega una puntuación
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/puntuaciones")
	public Response createPuntuacion(Puntuacion puntuacion,
			@PathParam("usuario_id") int idUsuario) {

		String sql;
		List<Object> parametros = new ArrayList<Object>();
		if (puntuacion.getPuntuacion() == null) {
			sql = "INSERT INTO vinos.puntuaciones (ID_usuario, ID_vino) VALUES (?, ?);";
		} else {
			sql = "INSERT INTO vinos.puntuaciones (puntuacion, ID_usuario, ID_vino) VALUES (?, ?, ?);";
			parametros.add(puntuacion.getPuntuacion());
		}
		parametros.add(idUsuario);
		parametros.add(puntuacion.getIdVino());

		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (int i = 0; i < parametros.size(); i++) {
				ps.setObject(i + 1, parametros.get(i));
			}
			int affectedRows = ps.executeUpdate();

			if (affectedRows == 1) {
				String location = uriInfo.getAbsolutePath() + "/" + idUsuario
						+ "/puntuaciones/" + puntuacion.getIdVino();
				return Response.status(Response.Status.CREATED)
						.entity(puntuacion).header("Location", location)
						.header("Content-Location", location).build();

			}
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo añadir el vino a la lista del usuario\n")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Añade un seguidor existente a la lista de seguidores
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/seguidores")
	public Response createSeguidor(@PathParam("usuario_id") int id,
			Usuario seguidor) {

		try (Connection conn = ds.getConnection()) {
			if (seguidor.getId() == id)
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("No puedes seguirte a ti mismo").build();
			String sql = "SELECT * FROM vinos.usuario WHERE ID="
					+ seguidor.getId() + ";";
			try (PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("El seguidor no existe\n").build();
				}

				List<Object> parametros = new ArrayList<Object>();
				sql = "INSERT INTO vinos.seguidores (ID_usuario, ID_seguidor) VALUES (?, ?);";
				parametros.add(id);
				parametros.add(seguidor.getId());
				try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
					for (int i = 0; i < parametros.size(); i++) {
						ps2.setObject(i + 1, parametros.get(i));
					}

					int affectedRows = ps2.executeUpdate();

					if (affectedRows == 1) {
						String location = uriInfo.getAbsolutePath() + "/" + id
								+ "/seguidores/" + seguidor.getId();
						return Response.status(Response.Status.CREATED)
								.entity(seguidor).header("Location", location)
								.header("Content-Location", location).build();

					}
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("No se pudo añadir el seguidor").build();
				}
			}

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo añadir el seguidor").build();
		}
	}

	/*
	 * Genera una lista JSON de los usuarios existentes
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsuarios(
			@QueryParam("nombre") @DefaultValue("") String patronNombre,
			@QueryParam("desde") @DefaultValue("0") String desde,
			@QueryParam("count") @DefaultValue("10") String count) {

		String sql;
		List<Object> parametros = new ArrayList<Object>();
		try (Connection conn = ds.getConnection()) {
			int int_desde = Integer.parseInt(desde);
			int int_count = Integer.parseInt(count);
			if (!patronNombre.equals("")) {
				patronNombre = "%" + patronNombre + "%";
				sql = "SELECT * FROM vinos.usuario WHERE nombre LIKE ? ORDER BY ID";
				parametros.add(patronNombre);
			} else
				sql = "SELECT * FROM vinos.usuario ORDER BY ID";
			
			 sql += " LIMIT " + int_desde + "," + int_count + ";";


			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}

				try (ResultSet rs = ps.executeQuery()) {
					Usuarios u = new Usuarios();
					ArrayList<Usuario> usuarios = u.getUsuarios();

					int i = 0;
					while (rs.next()) {
						usuarios.add(userFromRS(rs));
						i++;
					}
					if (i == int_count) {
						u.setSiguientePagina(new Link(urlSiguientePagina(
								int_desde, int_count), "self"));
					}
					return Response.status(Response.Status.OK).entity(u)
							.build();
				}
			}

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Devuelve los datos básicos del usuario especificado
	 */
	@GET
	@Path("{usuario_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsuario(@PathParam("usuario_id") int id) {

		String sql = "SELECT * FROM vinos.usuario WHERE ID=" + id + ";";
		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				Usuario user = userFromRS(rs);
				return Response.status(Response.Status.OK).entity(user).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Elemento no encontrado").build();
			}
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Genera una lista JSON de los vinos de un usuario
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/puntuaciones")
	public Response getPuntuaciones(@PathParam("usuario_id") int id,
			@QueryParam("vino") @DefaultValue("") String nombre,
			@QueryParam("fechaAdicion") @DefaultValue("") String fechaAdicion,
			@QueryParam("tipo") @DefaultValue("") String tipo,
			@QueryParam("origen") @DefaultValue("") String origen,
			@QueryParam("ano") @DefaultValue("") String ano,
			@QueryParam("uva") @DefaultValue("") String uva,
			@QueryParam("desde") @DefaultValue("0") String desde,
			@QueryParam("count") @DefaultValue("10") String count) {

		try {
			int int_ano, int_desde, int_count;
			int_desde = Integer.parseInt(desde);
			int_count = Integer.parseInt(count);

			List<Object> parametros = new ArrayList<>();
			StringBuilder sql = new StringBuilder(
					"SELECT * FROM vinos.puntuaciones p INNER JOIN vinos.vino v WHERE p.ID_usuario="
							+ id + " AND p.ID_vino=v.ID");

			if (!nombre.equals("")) {
				sql.append(" AND v.nombre=?");
				parametros.add(nombre);
			}
			if (!tipo.equals("")) {
				sql.append(" AND v.tipo=?");
				parametros.add(tipo);
			}
			if (!origen.equals("")) {
				sql.append(" AND v.origen=?");
				parametros.add(origen);
			}
			if (!uva.equals("")) {
				sql.append(" AND v.uva=?");
				parametros.add(uva);
			}
			if (!ano.equals("")) {
				int_ano = Integer.parseInt(ano);
				sql.append(" AND v.ano=?");
				parametros.add(int_ano);
			}
			if (!fechaAdicion.equals("")) {
				if (fechaAdicion.equals("ascendente"))
					fechaAdicion = "ASC";
				else if (fechaAdicion.equals("descendente"))
					fechaAdicion = "DESC";
				else
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity("El parámetro fechaAdicion debe ser ascendente o descendente")
							.build();
				sql.append(" ORDER BY v.fechaAdicion " + fechaAdicion);
			}
			sql.append(" LIMIT ?,?;");
			parametros.add(int_desde);
			parametros.add(int_count);

			try (Connection conn = ds.getConnection();
					PreparedStatement ps = conn
							.prepareStatement(sql.toString())) {

				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}

				try (ResultSet rs = ps.executeQuery()) {
					Puntuaciones p = new Puntuaciones();
					ArrayList<Puntuacion> puntuaciones = p.getPuntuaciones();

					int i = 0;
					while (rs.next()) {
						puntuaciones.add(puntuacionFromRS(rs));
						i++;
					}
					if (i == int_count) {
						p.setSiguientePagina(new Link(urlSiguientePagina(
								int_desde, int_count), "self"));
					}
					return Response.status(Response.Status.OK).entity(p)
							.build();
				}
			}

		} catch (NumberFormatException e) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("Los parámetros desde, count y año deben ser de tipo numérico.")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Genera una lista JSON de los seguidores del usuario especificado
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/seguidores")
	public Response getSeguidores(
			@QueryParam("nombre") @DefaultValue("") String patronNombre,
			@QueryParam("desde") @DefaultValue("0") String desde,
			@QueryParam("count") @DefaultValue("10") String count,
			@PathParam("usuario_id") int id) {

		String sql;
		List<Object> parametros = new ArrayList<Object>();

		try (Connection conn = ds.getConnection()) {
			int int_desde = Integer.parseInt(desde);
			int int_count = Integer.parseInt(count);
			if (!patronNombre.equals("")) {
				patronNombre = "%" + patronNombre + "%";
				sql = "SELECT * FROM vinos.seguidores s INNER JOIN vinos.usuario u WHERE s.ID_seguidor=u.ID AND s.ID_usuario="
						+ id + " AND u.nombre LIKE ? ORDER BY ID";
				parametros.add(patronNombre);
			} else
				sql = "SELECT * FROM vinos.seguidores s INNER JOIN vinos.usuario u WHERE s.ID_seguidor=u.ID AND s.ID_usuario="
						+ id + " ORDER BY ID";
			sql += " LIMIT " + int_desde + "," + int_count + ";";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}
				try (ResultSet rs = ps.executeQuery()) {
					Usuarios u = new Usuarios();
					ArrayList<Usuario> usuarios = u.getUsuarios();

					int i = 0;
					while (rs.next()) {
						usuarios.add(userFromRS(rs));
						i++;
					}
					if (i == int_count) {
						u.setSiguientePagina(new Link(urlSiguientePagina(
								int_desde, int_count), "self"));
					}
					return Response.status(Response.Status.OK).entity(u)
							.build();
				}
			}
		} catch (NumberFormatException e) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("Los parámetros desde y count deben ser de tipo numérico.")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Genera una lista JSON de los vinos de un usuario
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/seguidores/{seguidor_id}/puntuaciones")
	public Response getPuntuacionesSeguidor(
			@PathParam("usuario_id") int idUsuario,
			@PathParam("seguidor_id") int idSeguidor,
			@QueryParam("vino") @DefaultValue("") String nombre,
			@QueryParam("fechaAdicion") @DefaultValue("") String fechaAdicion,
			@QueryParam("tipo") @DefaultValue("") String tipo,
			@QueryParam("origen") @DefaultValue("") String origen,
			@QueryParam("ano") @DefaultValue("") String ano,
			@QueryParam("uva") @DefaultValue("") String uva,
			@QueryParam("desde") @DefaultValue("0") String desde,
			@QueryParam("count") @DefaultValue("10") String count) {

		try {

			int int_ano, int_desde, int_count;
			int_desde = Integer.parseInt(desde);
			int_count = Integer.parseInt(count);

			List<Object> parametros = new ArrayList<>();
			StringBuilder sql = new StringBuilder(
					"SELECT * FROM vinos.puntuaciones p INNER JOIN vinos.vino v INNER JOIN vinos.seguidores s WHERE p.ID_usuario=s.ID_seguidor AND s.ID_usuario="
							+ idUsuario
							+ " AND s.ID_seguidor="
							+ idSeguidor
							+ " AND v.ID=p.ID_vino");

			if (!nombre.equals("")) {
				sql.append(" AND v.nombre=?");
				parametros.add(nombre);
			}
			if (!tipo.equals("")) {
				sql.append(" AND v.tipo=?");
				parametros.add(tipo);
			}
			if (!origen.equals("")) {
				sql.append(" AND v.origen=?");
				parametros.add(origen);
			}
			if (!uva.equals("")) {
				sql.append(" AND v.uva=?");
				parametros.add(uva);
			}
			if (!ano.equals("")) {
				int_ano = Integer.parseInt(ano);
				sql.append(" AND v.ano=?");
				parametros.add(int_ano);
			}
			if (!fechaAdicion.equals("")) {
				if (fechaAdicion.equals("ascendente"))
					fechaAdicion = "ASC";
				else if (fechaAdicion.equals("descendente"))
					fechaAdicion = "DESC";
				else
					return Response
							.status(Response.Status.BAD_REQUEST)
							.entity("El parámetro fechaAdicion debe ser ascendente o descendente")
							.build();
				sql.append(" ORDER BY v.fechaAdicion " + fechaAdicion);
			}

			sql.append(" LIMIT ?,?;");
			parametros.add(int_desde);
			parametros.add(int_count);

			try (Connection conn = ds.getConnection();
					PreparedStatement ps = conn
							.prepareStatement(sql.toString())) {
				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}

				try (ResultSet rs = ps.executeQuery()) {
					Puntuaciones p = new Puntuaciones();
					ArrayList<Puntuacion> puntuaciones = p.getPuntuaciones();

					int i = 0;
					while (rs.next()) {
						puntuaciones.add(puntuacionFromRS(rs));
						i++;
					}
					if (i == int_count) {
						p.setSiguientePagina(new Link(urlSiguientePagina(
								int_desde, int_count), "self"));
					}
					return Response.status(Response.Status.OK).entity(p)
							.build();
				}
			}
		} catch (NumberFormatException e) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("Los parámetros desde, count y año deben ser de tipo numérico.")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Genera una lista JSON que contiene la información del usuario
	 * especificado, sus 5 últimos vinos añadidos, sus 5 vinos favoritos y un
	 * listado con los 5 vinos mejores puntuados por sus seguidores
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/recomendacion")
	public Response getRecomendacion(@PathParam("usuario_id") int id) {

		try (Connection conn = ds.getConnection()) {
			Recomendacion recomendacion;
			Usuario usuario;
			Vino vino;
			ArrayList<Vino> vinos = new ArrayList<>();
			// Query que devuelve datos básicos del usuario
			String sql = "SELECT * FROM vinos.usuario WHERE ID=" + id + ";";
			try (PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					usuario = userFromRS(rs);
					recomendacion = new Recomendacion(usuario);

				} else {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("El usuario no existe").build();
				}
			}

			// Query que devuelve 5 últimos vinos añadidos
			sql = "SELECT * FROM vinos.vino v INNER JOIN vinos.puntuaciones p WHERE p.ID_vino=v.ID AND p.ID_usuario="
					+ id + " ORDER BY p.orden_anadido DESC LIMIT 5;";
			try (PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					vino = vinoFromRS(rs);
					vinos.add(vino);
				}
				if (vinos.size() > 5) {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Error en la consulta").build();
				}
				recomendacion.setUltimosVinos(vinos);
			}

			// Query que devuelve los 5 vinos con mayor puntuación
			vinos = new ArrayList<>();
			sql = "SELECT * FROM vinos.vino v INNER JOIN vinos.puntuaciones p WHERE p.ID_vino=v.ID AND p.ID_usuario="
					+ id + " ORDER BY p.puntuacion DESC LIMIT 5;";
			try (PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					vino = vinoFromRS(rs);
					vinos.add(vino);
				}
				if (vinos.size() > 5) {
					return Response
							.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Error en la consulta").build();
				}
				recomendacion.setMejoresVinos(vinos);
			}

			// Query que devuelve un listado con los 5 mejores vinos de todos
			// sus amigos
			vinos = new ArrayList<>();
			sql = "SELECT * FROM vinos.vino v INNER JOIN vinos.puntuaciones p INNER JOIN vinos.seguidores s WHERE p.ID_vino=v.ID AND p.ID_usuario=s.ID_seguidor AND s.ID_usuario="
					+ id + " ORDER BY p.puntuacion DESC;";
			try (PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
				while (rs.next() && vinos.size() < 5) {
					vino = vinoFromRS(rs);
					if (!existeVino(vinos, vino))
						vinos.add(vino);
				}
				recomendacion.setMejoresVinosAmigos(vinos);
			}

			return Response.status(Response.Status.OK).entity(recomendacion)
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error de acceso a BBDD").build();
		}
	}

	/*
	 * Modifica los datos básicos del usuario
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}")
	public Response updateUsuario(@PathParam("usuario_id") int id,
			Usuario newUser) {

		try (Connection conn = ds.getConnection()) {
			if (menorEdad(newUser))
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("El usuario debe ser mayor de edad").build();
			if (emailExiste(newUser, conn))
				return Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity("El email proporcionado está asociado a otro usuario")
						.build();
			Usuario user;
			String sql = "SELECT * FROM vinos.usuario WHERE ID=" + id + ";";
			try (PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					user = userFromRS(rs);
				else
					return Response.status(Response.Status.NOT_FOUND)
							.entity("Elemento no encontrado").build();

				user.setNombre(newUser.getNombre());
				user.setFechaNacimiento(newUser.getFechaNacimiento());
				user.setEmail(newUser.getEmail());
			}

			List<Object> parametros = new ArrayList<Object>();
			sql = "UPDATE vinos.usuario SET nombre=?, fechaNacimiento=?, email=? WHERE ID="
					+ id + ";";
			parametros.add(user.getNombre());
			parametros.add(user.getFechaNacimiento());
			parametros.add(user.getEmail());
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}
				int affectedRows = ps.executeUpdate();

				String location = uriInfo.getBaseUri() + "usuarios/"
						+ user.getId();
				return Response.status(Response.Status.OK).entity(user)
						.header("Content-Location", location).build();
			}

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo actualizar el usuario").build();
		}
	}

	/*
	 * Modifica la puntuación del vino especificado
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{usuario_id}/puntuaciones")
	public Response updatePuntuacion(@PathParam("usuario_id") int idUsuario,
			Puntuacion nuevaPuntuacion) {

		try (Connection conn = ds.getConnection()) {
			List<Object> parametros = new ArrayList<Object>();
			String sql = "SELECT * FROM vinos.puntuaciones WHERE ID_usuario="
					+ idUsuario + " AND ID_vino=?;";
			parametros.add(nuevaPuntuacion.getIdVino());
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next())
						return Response.status(Response.Status.NOT_FOUND)
								.entity("Elemento no encontrado").build();
				}
			}

			parametros = new ArrayList<Object>();
			sql = "UPDATE vinos.puntuaciones SET puntuacion=? WHERE ID_usuario="
					+ idUsuario + " AND ID_vino=?";
			parametros.add(nuevaPuntuacion.getPuntuacion());
			parametros.add(nuevaPuntuacion.getIdVino());
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < parametros.size(); i++) {
					ps.setObject(i + 1, parametros.get(i));
				}
				int affectedRows = ps.executeUpdate();

				String location = uriInfo.getBaseUri() + "usuarios/"
						+ idUsuario + "/puntuaciones/"
						+ nuevaPuntuacion.getIdVino();
				return Response.status(Response.Status.OK)
						.entity(nuevaPuntuacion).build();
			}

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo actualizar la puntuacion").build();
		}
	}

	/*
	 * Elimina el usuario especificado del sistema
	 */
	@DELETE
	@Path("{usuario_id}")
	public Response deleteUsuario(@PathParam("usuario_id") int id) {
		String sql = "DELETE FROM vinos.usuario WHERE ID=" + id + ";";
		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Elemento no encontrado").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo eliminar el usuario").build();
		}
	}

	/*
	 * Elimina el vino especificado de la lista del usuario
	 */
	@DELETE
	@Path("{usuario_id}/puntuaciones/{vino_id}")
	public Response deletePuntuacion(@PathParam("usuario_id") int idUsuario,
			@PathParam("vino_id") int idVino) {
		String sql = "DELETE FROM vinos.puntuaciones WHERE ID_usuario="
				+ idUsuario + " AND ID_vino=" + idVino + ";";
		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Elemento no encontrado").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo eliminar el vino de la lista").build();
		}
	}

	/*
	 * Elimina el usuario especificado del sistema
	 */
	@DELETE
	@Path("{usuario_id}/seguidores/{seguidor_id}")
	public Response deleteSeguidor(@PathParam("usuario_id") int idUsuario,
			@PathParam("seguidor_id") int idSeguidor) {
		String sql = "DELETE FROM vinos.seguidores WHERE ID_usuario="
				+ idUsuario + " AND ID_seguidor=" + idSeguidor + ";";
		try (Connection conn = ds.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else
				return Response.status(Response.Status.NOT_FOUND)
						.entity("Elemento no encontrado").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo eliminar el seguidor").build();
		}
	}

	private Usuario userFromRS(ResultSet rs) throws SQLException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(rs.getDate("fechaNacimiento"));
		Usuario user = new Usuario(rs.getString("nombre"), date,
				rs.getString("email"));
		user.setId(rs.getInt("ID"));
		return user;
	}

	private boolean menorEdad(Usuario user) {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(user.getFechaNacimiento(), formatter);
		Period age = Period.between(date, currentDate);
		return age.getYears() < 18;
	}

	private boolean emailExiste(Usuario user, Connection conn)
			throws SQLException {
		boolean result = false;
		List<Object> parametros = new ArrayList<Object>();
		String sql = "SELECT * FROM vinos.usuario WHERE email=?;";
		parametros.add(user.getEmail());
		PreparedStatement ps = conn.prepareStatement(sql);
		for (int i = 0; i < parametros.size(); i++) {
			ps.setObject(i + 1, parametros.get(i));
		}
		ResultSet rs = ps.executeQuery();
		if (rs.next())
			result = true;
		ps.close();
		rs.close();
		return result;
	}

	private boolean existeVino(ArrayList<Vino> vinos, Vino vino) {
		boolean existe = false;
		int i = 0;
		while (!existe && i < vinos.size()) {
			existe = vinos.get(i).getId() == vino.getId();
			i++;
		}
		return existe;
	}

	private Vino vinoFromRS(ResultSet rs) throws SQLException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(rs.getDate("fechaAdicion"));
		Vino vino = new Vino(rs.getInt("ID"));
		vino.setNombre(rs.getString("nombre"));
		vino.setFechaAdicion(date);
		vino.setTipo(rs.getString("tipo"));
		vino.setUva(rs.getString("uva"));
		vino.setNombre(rs.getString("nombre"));
		vino.setOrigen(rs.getString("origen"));
		vino.setAno(rs.getInt("ano"));
		return vino;
	}

	private Puntuacion puntuacionFromRS(ResultSet rs) throws SQLException {
		Puntuacion p = new Puntuacion(rs.getInt("ID_vino"));
		if (rs.getObject("puntuacion") != null)
			p.setPuntuacion(rs.getDouble("puntuacion"));
		return p;
	}

	private String urlSiguientePagina(int int_desde, int int_count) {
		String url = uriInfo.getRequestUri().toString();
		String newUrl;
		int posicionDesde = url.indexOf("desde=");
		if (posicionDesde != -1) {
			String nuevoDesde = "desde=" + (int_desde + int_count);
			String target = "desde=\\d+";
			newUrl = url.replaceAll(target, nuevoDesde);
		} else {
			if (url.contains("?"))
				url += "&desde=" + String.valueOf(int_desde + int_count);
			else
				url += "?desde=" + String.valueOf(int_desde + int_count);
			newUrl = url;
		}
		return newUrl;
	}

}
