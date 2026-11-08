package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

/* Test Case de la base de datos partiendo de un escenario SIN datos previos
 * Objetivos:
 *   - validar que iniciarBD() cree las tablas y los datos semilla sobre una BD sin datos
 *   - validar las operaciones CRUD tomando como base el estado inicial que deja iniciarBD()
 *   - validar el patrón Singleton en la conexión
 * */
public class TestBaseDeDatosSinDatos {
	private BaseDeDatos bd;
	private DataAccessObject DAO;
	private int cantidadInicial;

	private static final String INSERT = "INSERT INTO Asociados(nombre, apellido, dni, calle, numero, ciudad, telefono) "
			+ "VALUES (?,?,?,?,?,?,?);";
	private static final String DELETE = "DELETE FROM Asociados WHERE dni=?;";
	// DNI que no debería coincidir con ningún dato semilla
	private static final String DNI_NUEVO = "99999991";
	private static final String DNI_NUEVO_2 = "99999992";

	@Before
	public void setUp() throws Exception {
		EscenarioBDSinDatos escenario = new EscenarioBDSinDatos();
		this.bd = escenario.getBd();
		this.DAO = new DataAccessObject();
		// estado inicial: lo que haya dejado iniciarBD() (datos semilla)
		this.cantidadInicial = this.DAO.cargarBD().size();
	}

	@After
	public void tearDown() throws Exception {
		this.bd.iniciarBD();
	}

	private AsociadoDTO crearDTONuevo(String dni) {
		Asociado asociado = new Asociado(
				"Juan", "Pepe", dni, "123123123",
				new Domicilio("Avenida Siempreviva", 123, "Mar del Plata"));
		return this.DAO.CrearDTO(asociado);
	}

	private boolean existeDni(String dni) throws SQLException {
		for (AsociadoDTO a : this.DAO.cargarBD()) {
			if (a.getDni().equals(dni)) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void testGetConexionExitoso() {
	}

	// ---------- iniciarBD ----------

	@Test
	//validar inicialización: sobre una BD sin datos se crean las tablas y los datos semilla
	public void testIniciarBDExitoso() {
		try {
			this.bd.iniciarBD();
			assertEquals("No se crearon los 20 datos semilla", 20, this.DAO.cargarBD().size());
		} catch (SQLException e) {
			fail("No se pudo cargar la BD luego de llamar a iniciarBD()");
		}
	}

	@Test
	//validar inicialización: llamar iniciarBD() de nuevo no debe duplicar los datos semilla
	public void testIniciarBDNoDuplicaDatosSemilla() {
		try {
			this.bd.iniciarBD();
			this.bd.iniciarBD();
			assertEquals("iniciarBD() duplicó los datos semilla", 20, this.DAO.cargarBD().size());
		} catch (SQLException e) {
			fail("No se pudo cargar la BD luego de llamar a iniciarBD()");
		}
	}

	// ---------- Read ----------

	@Test
	//validar Read: la carga inicial devuelve los datos semilla
	public void testCargarBDInicial() {
		try {
			ArrayList<AsociadoDTO> asociados = this.DAO.cargarBD();
			assertNotNull("cargarBD() no debería devolver null", asociados);
			assertFalse("Deberían existir los datos semilla", asociados.isEmpty());
		} catch (SQLException e) {
			fail("No se pudo cargar la BD");
		}
	}

	@Test
	//validar Read: consulta por id existente
	public void testEjecutarConsultaExitosa() {
		try {
			ResultSet rs = this.bd.ejecutarConsulta("SELECT * FROM Asociados WHERE id=?", 1);
			assertTrue("La consulta no devolvió filas para un id existente", rs.next());
			assertFalse("La consulta por id devolvió más de una fila", rs.next());
		} catch (SQLException e) {
			fail("No se ejecutó correctamente la consulta.");
		}
	}

	@Test
	//validar Read: id inexistente devuelve resultado vacío
	public void testEjecutarConsultaIdInexistente() {
		try {
			ResultSet rs = this.bd.ejecutarConsulta("SELECT * FROM Asociados WHERE id=?", 99999);
			assertFalse("Se devolvieron filas para un id inexistente", rs.next());
		} catch (SQLException e) {
			fail("Una consulta válida sin resultados no debería lanzar excepción");
		}
	}

	@Test
	//validar Read: id = -1 ejecuta la consulta sin parámetro
	public void testEjecutarConsultaSinParametro() {
		try {
			ResultSet rs = this.bd.ejecutarConsulta("SELECT * FROM Asociados", -1);
			int filas = 0;
			while (rs.next()) {
				filas++;
			}
			assertEquals("La cantidad de filas no coincide con la BD", this.cantidadInicial, filas);
		} catch (SQLException e) {
			fail("No se ejecutó correctamente la consulta sin parámetro");
		}
	}

	@Test
	//validar Read: SQL inválido debe lanzar SQLException
	public void testEjecutarConsultaSqlInvalido() {
		try {
			this.bd.ejecutarConsulta("SELECT FROM Asociados WHERE id=?", 1);
			fail("Una consulta inválida no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado según el contrato
		}
	}

	// ---------- Create ----------

	@Test
	//validar Create: insertar suma exactamente un asociado
	public void testEjecutarInsertExitoso() {
		AsociadoDTO dtoNuevo = this.crearDTONuevo(DNI_NUEVO);
		try {
			this.bd.ejecutarInsert(INSERT, dtoNuevo);
		} catch (Exception e) {
			fail("No se insertó un asociado pese a que era válido");
		}

		try {
			assertEquals("Debería haberse sumado exactamente un asociado",
					this.cantidadInicial + 1, this.DAO.cargarBD().size());
			assertTrue("El asociado insertado no está en la BD", this.existeDni(DNI_NUEVO));
		} catch (SQLException e) {
			fail("No se pudo cargar la BD luego del insert");
		}
	}

	@Test
	//validar Create: insertar dos asociados distintos
	public void testEjecutarInsertDosAsociadosDistintos() {
		try {
			this.bd.ejecutarInsert(INSERT, this.crearDTONuevo(DNI_NUEVO));
			this.bd.ejecutarInsert(INSERT, this.crearDTONuevo(DNI_NUEVO_2));
			assertEquals("Deberían haberse insertado dos asociados",
					this.cantidadInicial + 2, this.DAO.cargarBD().size());
		} catch (Exception e) {
			fail("No se insertaron dos asociados válidos y distintos");
		}
	}

	@Test
	//validar Create: insertar un asociado repetido debe lanzar excepción
	public void testEjecutarInsertUsuarioRepetido() {
		try {
			AsociadoDTO existente = this.DAO.cargarBD().get(0);
			this.bd.ejecutarInsert(INSERT, existente);
			fail("Se insertó un usuario repetido sin que lance excepción");
		} catch (SQLException e) {
			// comportamiento esperado
		} catch (Exception e) {
			// comportamiento esperado (la firma de ejecutarInsert puede lanzar otra excepción)
		}
	}

	@Test
	//validar Create: insertar un asociado nulo debe fallar
	public void testEjecutarInsertUsuarioNulo() {
		try {
			this.bd.ejecutarInsert(INSERT, null);
			fail("Se insertó un usuario nulo sin que lance excepción");
		} catch (AssertionError | NullPointerException | SQLException e) {
			// comportamiento esperado: assert (con -ea) o NullPointerException (sin -ea)
		}
	}

	// ---------- Update ----------
	@Test
	public void testEjecutarActualizacionExitoso() {
		String update = "UPDATE Asociados SET nombre=?, apellido=?, dni=?, calle=?, numero=?, ciudad=?, telefono=? "
				+ "WHERE id=?;";
		try {
			// cualquier asociado existente; se evita el 1 para no pasar por casualidad
			AsociadoDTO dto = this.DAO.cargar(2);
			dto.setNombre("NombreModificado");

			this.bd.ejecutarActualizacion(update, dto);

			AsociadoDTO recargado = this.DAO.cargar(2);
			assertEquals("El asociado no se actualizó", "NombreModificado", recargado.getNombre());
		} catch (SQLException e) {
			fail("No se pudo actualizar o recargar el asociado");
		}
	}
	@Test
	//validar Update: debe actualizar el asociado indicado, no uno fijo
	public void testEjecutarActualizacionAfectaSoloAlAsociadoIndicado() {
		String update = "UPDATE Asociados SET nombre=?, apellido=?, dni=?, calle=?, numero=?, ciudad=?, telefono=? "
				+ "WHERE id=?;";
		try {
			AsociadoDTO dtoPrimero = this.DAO.cargar(1);
			AsociadoDTO dtoObjetivo = this.DAO.cargar(2);
			String nombrePrimeroOriginal = dtoPrimero.getNombre();

			dtoObjetivo.setNombre("NombreModificado");

			try {
				this.bd.ejecutarActualizacion(update, dtoObjetivo);
			} catch (SQLException e) {
				fail("No se pudo ejecutar una actualización válida");
			}

			AsociadoDTO objetivoRecargado = this.DAO.cargar(2);
			AsociadoDTO primeroRecargado = this.DAO.cargar(1);

			assertEquals("No se actualizó el asociado indicado", "NombreModificado", objetivoRecargado.getNombre());
			assertEquals("Se modificó un asociado que no correspondía", nombrePrimeroOriginal, primeroRecargado.getNombre());
		} catch (SQLException e) {
			fail("No se pudo cargar un asociado de la BD");
		}
	}
	// ---------- Delete ----------
	@Test
	//validar Delete
	public void testEjecutarDeleteExitoso() {
		AsociadoDTO asociado_a_borrar;
		try {
			asociado_a_borrar = this.DAO.cargar(1);
			try {
				this.bd.ejecutarDelete("DELETE FROM Asociados WHERE dni=?;", asociado_a_borrar.getDni());
			}catch(SQLException e) {
				fail("No se pudo borrar un asociado existente");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("No se pudo cargar un asociado");
		}
	}
	@Test
	//validar Delete: borrar un asociado inexistente no afecta la BD
	public void testEjecutarDeleteInexistenteNoAfectaLaBD() {
		try {
			this.bd.ejecutarDelete(DELETE, DNI_NUEVO);
			assertEquals("No debería haberse borrado ningún asociado",
					this.cantidadInicial, this.DAO.cargarBD().size());
		} catch (SQLException e) {
			fail("Borrar un asociado inexistente no debería lanzar excepción");
		}
	}

	@Test
	//validar Delete: insertar y borrar devuelve la BD a su estado inicial
	public void testEjecutarDeleteLuegoDeInsert() {
		AsociadoDTO dto = this.crearDTONuevo(DNI_NUEVO);
		try {
			this.bd.ejecutarInsert(INSERT, dto);
			assertEquals("El insert previo no sumó un asociado",
					this.cantidadInicial + 1, this.DAO.cargarBD().size());

			this.bd.ejecutarDelete(DELETE, dto.getDni());
			assertEquals("El asociado no se borró", this.cantidadInicial, this.DAO.cargarBD().size());
			assertFalse("El asociado borrado sigue en la BD", this.existeDni(DNI_NUEVO));
		} catch (Exception e) {
			fail("No se pudo insertar y borrar un asociado válido");
		}
	}

	// ---------- Singleton ----------

	@Test
	public void testInstanciaUnica() {
		//Creamos dos variables BD, ejecutamos una operación en una y verificamos que se ejecute en la otra
		//Es de integración, pues necesitamos que los métodos de operaciones funcionen
		BaseDeDatos segunda_bd = BaseDeDatos.getInstancia();

		//primero verificamos que getInstancia() devuelva lo mismo
		assertSame(this.bd, segunda_bd);
	}

	@Test
	public void conexion_esUnica() {
		//Verificamos que la conexión que ambas instancias nos retornan sean la misma
		BaseDeDatos segunda_bd = BaseDeDatos.getInstancia();

		Connection conexion_bd = this.bd.getConexion();
		Connection conexion_segunda_bd = segunda_bd.getConexion();

		assertSame(conexion_bd, conexion_segunda_bd);
	}
}