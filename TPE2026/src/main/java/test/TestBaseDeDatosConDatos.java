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


/* Test Case de la base de datos
 * Objetivos: 
 *   - validar uso correcto del patrón Singleton en la conexión, 
 *     la inicialización automática de tablas y los datos semilla
 *   - validar las operaciones CRUD a través de consultas con parámetros
 * */
public class TestBaseDeDatosConDatos {
	private BaseDeDatos bd;
	private ArrayList<AsociadoDTO> asociados, asociados_escenario;
	private DataAccessObject DAO;
	@Before
	public void setUp() throws Exception {
		EscenarioBDConDatos escenario = new EscenarioBDConDatos();
		bd = escenario.getBd();
		this.asociados_escenario = escenario.getAsociados();
		this.DAO = new DataAccessObject();
		this.asociados = this.DAO.cargarBD();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetConexionExitoso() {
	}

	@Test
	//validar Read
	public void testEjecutarConsultaExitosa() {
		try {
			AsociadoDTO esperado = this.DAO.cargar(1);
			ResultSet rs = this.bd.ejecutarConsulta("SELECT * FROM Asociados WHERE id=?", 1);
			assertTrue("La consulta no devolvió filas para un id existente", rs.next());
			assertEquals("El dni devuelto no coincide", esperado.getDni(), rs.getString("dni"));
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
			int esperados = this.DAO.cargarBD().size();
			ResultSet rs = this.bd.ejecutarConsulta("SELECT * FROM Asociados", -1);
			int filas = 0;
			while (rs.next()) {
				filas++;
			}
			assertEquals("La cantidad de filas no coincide con la BD", esperados, filas);
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
	@Test
	//validar Update: el asociado modificado debe quedar actualizado en la BD
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
		ArrayList<AsociadoDTO> asociadosBD;
		try {
			asociadosBD = this.DAO.cargarBD();
			AsociadoDTO dtoasociado_nuevo = asociadosBD.get(0);
			try {
				this.bd.ejecutarInsert("INSERT INTO Asociados(nombre, apellido, dni, calle, numero, ciudad, telefono) "
						+ "VALUES (?,?,?,?,?,?,?);", dtoasociado_nuevo);
				fail("se inserto un usuario repetido sin que lance excepcion");
			}catch(Exception e) {
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	//validar Delete: debe borrar solo al asociado indicado
	public void testEjecutarDeleteBorraSoloElAsociadoIndicado() {
		String delete = "DELETE FROM Asociados WHERE dni=?;";
		try {
			ArrayList<AsociadoDTO> antes = this.DAO.cargarBD();
			AsociadoDTO aBorrar = antes.get(0);

			try {
				this.bd.ejecutarDelete(delete, aBorrar.getDni());
			} catch (SQLException e) {
				fail("No se pudo borrar un asociado existente");
			}

			ArrayList<AsociadoDTO> despues = this.DAO.cargarBD();
			assertEquals("Debería haberse borrado exactamente un asociado", antes.size() - 1, despues.size());
			for (AsociadoDTO a : despues) {
				if (a.getDni().equals(aBorrar.getDni())) {
					fail("El asociado borrado sigue en la BD");
				}
			}
		} catch (SQLException e) {
			fail("No se pudo cargar la BD");
		}
	}

	@Test
	//validar Delete: borrar un asociado inexistente no debe afectar la BD
	public void testEjecutarDeleteInexistenteNoAfectaLaBD() {
		String delete = "DELETE FROM Asociados WHERE dni=?;";
		try {
			int antes = this.DAO.cargarBD().size();
			this.bd.ejecutarDelete(delete, "000000000");
			int despues = this.DAO.cargarBD().size();
			assertEquals("No debería haberse borrado ningún asociado", antes, despues);
		} catch (SQLException e) {
			fail("Borrar un asociado inexistente no debería lanzar excepción");
		}
	}
	@Test
	public void testIniciarBDExitoso() {
		//Este es para testear la inicialización automática y la creación de datos semilla,
		//ambas operaciones se hacen en iniciarBD()
		//solo tengo que chequear que los datos que puse en el escenario se hayan borrado al llamar iniciarBD()
		try {
			this.bd.iniciarBD();
			ArrayList<AsociadoDTO> asociadosBD = this.DAO.cargarBD();
			
			for (AsociadoDTO asociadoEscenario : this.asociados_escenario) {
				for (AsociadoDTO asociadoBD : asociadosBD) {
					if (asociadoBD.getDni().equals(asociadoEscenario.getDni())) {
						fail("No se borraron los datos al iniciar BD");
					}
				}
			}
		} catch (SQLException e) {
			fail("No se pudo cargar la BD luego de llamar a iniciarBD()");
		}
	}
	
	@Test
	public void testInstanciaUnica() {
		//Creamos dos variables BD, ejecutamos una operación en una y verificamos que se ejecute en la otra
		//Es de integración, pues necesitamos que los métodos de operaciones funcionen
		BaseDeDatos segunda_bd = BaseDeDatos.getInstancia();
		
		//primero verificamos que getInstancia() devuelva lo mismo
		assertSame(this.bd, segunda_bd);
		
	}
	@Test
	void conexion_esUnica(){
		//Verificamos que la conexión que ambas instancias nos retornan sean la misma
		BaseDeDatos segunda_bd = BaseDeDatos.getInstancia();
		
		Connection conexion_bd = this.bd.getConexion();
		Connection conexion_segunda_bd = segunda_bd.getConexion();

		assertSame(conexion_bd, conexion_segunda_bd);
	}
}
