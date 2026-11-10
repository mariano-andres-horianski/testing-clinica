package test;

import static org.junit.Assert.*;

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

/* Test Case de la base de datos en un escenario SIN conexión (la conexión está cerrada)
 * Objetivos:
 *   - validar que las operaciones de lectura y escritura informen el fallo con SQLException
 *   - validar que iniciarBD() no recupere la conexión ni lance excepción hacia afuera
 *   - validar que el Singleton siga devolviendo la misma instancia y la misma conexión cerrada
 * */
public class TestBaseDeDatosSinConexion {
	private EscenarioBDSinConexion escenario;
	private BaseDeDatos bd;
	private DataAccessObject DAO;

	private static final String SELECT_POR_ID = "SELECT * FROM Asociados WHERE id=?";
	private static final String SELECT_TODOS = "SELECT * FROM Asociados";
	private static final String INSERT = "INSERT INTO Asociados(nombre, apellido, dni, calle, numero, ciudad, telefono) "
			+ "VALUES (?,?,?,?,?,?,?);";
	private static final String UPDATE = "UPDATE Asociados SET nombre=?, apellido=?, dni=?, calle=?, numero=?, ciudad=?, telefono=? "
			+ "WHERE id=?;";
	private static final String DELETE = "DELETE FROM Asociados WHERE dni=?;";
	private static final String DNI_NUEVO = "99999991";

	@Before
	public void setUp() throws Exception {
		this.escenario = new EscenarioBDSinConexion();
		this.bd = this.escenario.getBd();
		this.DAO = new DataAccessObject();
	}

	@After
	public void tearDown() throws Exception {
		// iniciarBD() no sirve acá: con la conexión cerrada no restaura nada
		this.escenario.restaurar();
	}

	private AsociadoDTO crearDTONuevo(String dni) {
		Asociado asociado = new Asociado(
				"Juan", "Pepe", dni, "123123123",
				new Domicilio("Avenida Siempreviva", 123, "Mar del Plata"));
		return this.DAO.CrearDTO(asociado);
	}

	// ---------- Verificación del escenario ----------

	@Test
	//validar el escenario: la conexión debe estar cerrada
	public void testConexionEstaCerrada() {
		try {
			assertNotNull("getConexion() no debería devolver null", this.bd.getConexion());
			assertTrue("La conexión debería estar cerrada", this.bd.getConexion().isClosed());
		} catch (SQLException e) {
			fail("No se pudo consultar el estado de la conexión");
		}
	}

	// ---------- Read ----------

	@Test
	//validar Read: consulta por id sobre conexión cerrada debe lanzar SQLException
	public void testEjecutarConsultaConParametro() {
		try {
			this.bd.ejecutarConsulta(SELECT_POR_ID, 1);
			fail("Una consulta sin conexión no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado
		}
	}

	@Test
	//validar Read: consulta sin parámetro (id = -1) sobre conexión cerrada debe lanzar SQLException
	public void testEjecutarConsultaSinParametro() {
		try {
			this.bd.ejecutarConsulta(SELECT_TODOS, -1);
			fail("Una consulta sin conexión no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado
		}
	}

	@Test
	//validar Read: cargarBD() del DAO debe propagar la falla de la conexión
	public void testCargarBDSinConexion() {
		try {
			ArrayList<AsociadoDTO> asociados = this.DAO.cargarBD();
			fail("cargarBD() sin conexión no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado
		}
	}

	// ---------- Create ----------

	@Test
	//validar Create: insertar un asociado válido sobre conexión cerrada debe lanzar SQLException
	public void testEjecutarInsertSinConexion() {
		try {
			this.bd.ejecutarInsert(INSERT, this.crearDTONuevo(DNI_NUEVO));
			fail("Un insert sin conexión no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado
		}
	}

	// ---------- Update ----------

	@Test
	//validar Update: actualizar sobre conexión cerrada debe lanzar SQLException
	public void testEjecutarActualizacionSinConexion() {
		try {
			this.bd.ejecutarActualizacion(UPDATE, this.crearDTONuevo(DNI_NUEVO));
			fail("Una actualización sin conexión no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado
		}
	}

	// ---------- Delete ----------

	@Test
	//validar Delete: borrar sobre conexión cerrada debe lanzar SQLException
	public void testEjecutarDeleteSinConexion() {
		try {
			this.bd.ejecutarDelete(DELETE, "12345678");
			fail("Un delete sin conexión no lanzó SQLException");
		} catch (SQLException e) {
			// comportamiento esperado
		}
	}

	// ---------- iniciarBD ----------

	@Test
	//validar inicialización: iniciarBD() sin conexión no lanza excepción hacia afuera y no reabre la conexión
	public void testIniciarBDSinConexion() {
		try {
			this.bd.iniciarBD();
		} catch (Exception e) {
			fail("iniciarBD() no debería propagar excepciones: " + e);
		}
		try {
			assertTrue("iniciarBD() no debería reabrir la conexión", this.bd.getConexion().isClosed());
		} catch (SQLException e) {
			fail("No se pudo consultar el estado de la conexión");
		}
	}

	// ---------- Singleton ----------

	@Test
	//validar Singleton: la instancia es la misma aunque la conexión esté cerrada
	public void testInstanciaUnicaSinConexion() {
		assertSame(this.bd, BaseDeDatos.getInstancia());
	}

	@Test
	//validar Singleton: ambas referencias comparten la misma conexión (cerrada)
	public void testConexionEsUnicaSinConexion() {
		BaseDeDatos segunda_bd = BaseDeDatos.getInstancia();
		assertSame(this.bd.getConexion(), segunda_bd.getConexion());
	}
}