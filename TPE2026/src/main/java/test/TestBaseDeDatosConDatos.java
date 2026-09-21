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


/* Test Case de la base de datos
 * Objetivos: 
 *   - validar uso correcto del patrón Singleton en la conexión, 
 *     la inicialización automática de tablas y los datos semilla
 *   - validar las operaciones CRUD a través de consultas con parámetros
 * */
public class TestBaseDeDatosConDatos {
	private BaseDeDatos bd;
	private ArrayList<AsociadoDTO> asociados;
	private DataAccessObject DAO;
	@Before
	public void setUp() throws Exception {
		EscenarioBDConDatos escenario = new EscenarioBDConDatos();
		bd = escenario.getBd();
		this.DAO = new DataAccessObject();
		this.asociados = this.DAO.cargarBD();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstanciaExitoso() {
		fail("Not yet implemented");
	}

	@Test
	public void testCerrarConexionExitoso() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetConexionExitoso() {
		fail("Not yet implemented");
	}

	@Test
	//validar Read
	public void testEjecutarConsultaExitosa() {
		fail("Not yet implemented");
	}

	@Test
	//validar Update
	public void testEjecutarActualizacionExitoso() {
		fail("Not yet implemented");
	}

	@Test
	//validar Create
	public void testEjecutarInsertExitoso() {
		String insert = "INSERT INTO Asociados(nombre, apellido, dni, calle, numero, ciudad, telefono) "
				+ "VALUES (?,?,?,?,?,?,?);";
		Asociado asociado = new Asociado(
		        "Juan", "Pepe", "22211111", "123123123",
		        new Domicilio("Avenida Siempreviva", 123, "Mar del Plata")
		    );
		AsociadoDTO dtoasociado_nuevo = this.DAO.CrearDTO(asociado);
		try {
			this.bd.ejecutarInsert(insert, dtoasociado_nuevo);
		}catch(Exception e) {

			fail("No se inserto un usuario pese a que era valido");
		}

		try {
			//actualizo el ArrayList de asociados para verificar que de la BD se extraiga el nuevo
			this.asociados = this.DAO.cargarBD();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//verifico que en el ArrayList se haya incluido mi nuevo Asociado
		int valido = this.asociados.indexOf(dtoasociado_nuevo);
		if (valido == -1) {
			fail("No se inserto exitosamente un usuario valido");
		}
	}
	@Test
	//validar Create
	public void testEjecutarInsertUsuarioRepetido() {
		//acá lo que tendría que hacer es sacar del ArrayList de la BD un usuario e insertarlo
		Asociado asociado = new Asociado(
		        "Rodrigo", "Bacidalupe", "11111111", "123123123",
		        new Domicilio("Avenida Siempreviva", 123, "Mar del Plata")
		    );

		AsociadoDTO dtoasociado_nuevo = this.DAO.CrearDTO(asociado);
		try {
			this.bd.ejecutarInsert("INSERT INTO Asociados(nombre, apellido, dni, calle, numero, ciudad, telefono) "
					+ "VALUES (?,?,?,?,?,?,?);", dtoasociado_nuevo);
			fail("se inserto un usuario repetido sin que lance excepcion");
		}catch(Exception e) {
			
		}
	}
	@Test
	//validar Create
	public void testEjecutarInsertUsuarioNulo() {
		//acá lo que tendría que hacer es sacar del ArrayList de la BD un usuario e insertarlo
		
		try {
			this.bd.ejecutarInsert("", null);
			fail("se inserto un usuario nulo sin que lance assertion error");
		}catch(Exception e) {
			
		}
	}
	
	@Test
	//validar Delete
	public void testEjecutarDeleteExitoso() {
		fail("Not yet implemented");
	}

	@Test
	public void testIniciarBDExitoso() {
		fail("Not yet implemented");
	}

}
