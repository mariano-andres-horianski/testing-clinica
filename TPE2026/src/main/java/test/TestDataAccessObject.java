package test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import modelo.modeloDominio.clinica.Clinica;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

public class TestDataAccessObject {
	private DataAccessObject dataAccessObject = new DataAccessObject();
	
	//guardar(Asociado asociado)
	
	@Test
	public void testGuardarExitoso() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
		} catch(SQLException e) {
			fail("No debería lanzarse una excepción");
		};
		
		try {
			AsociadoDTO asociadoNuevo = dataAccessObject.CrearDTO(asociado);
			ArrayList<AsociadoDTO> asociados = dataAccessObject.cargarBD();
			int encontrado = 0;
			
			for (AsociadoDTO aso : asociados) {
				if (aso.getDni().contentEquals(asociadoNuevo.getDni())) {
					encontrado = 1;
				}
			}
			Assert.assertEquals("No se encontró el asociado creado",encontrado,1);
		}catch(SQLException e) {
		}	
	}
	
	@Test
	public void testGuardarInvalido() {
		try {
			dataAccessObject.guardar(null);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarNombreInvalido() {
		Asociado asociado = new Asociado(null,"Fulanito","1","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarApellidoInvalido() {
		Asociado asociado = new Asociado("Cosme",null,"0","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	
	@Test
	public void testGuardarDNIInvalidoNulo() {
		Asociado asociado = new Asociado("Cosme","Fulanito",null,"2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarDNIInvalidoRepetido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","12345678","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarCiudadInvalido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio(null,1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarCalleInvalido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",1234,null));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarNumeroInvalidoNegativo() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",-1,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void testGuardarNumeroInvalidoCero() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",0,"Av. Siempreviva"));
			
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}

	@Test
	public void testGuardarTelefonoInvalido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0",null,new Domicilio("Springfield",1234,"Av. Siempreviva"));
		
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	//cargarConLimite(int limite)
	
	@Test
	public void TestCargarConLimiteExitoso() {
		try {
			ArrayList<AsociadoDTO> asociadoDTO = dataAccessObject.cargarConLimite(1);
		} catch(AssertionError e) {
			fail("No debería lanzarse una excepción");
		} catch (SQLException e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	@Test
	public void TestCargarConLimiteInvalidoCero() {
		try {
			ArrayList<AsociadoDTO> asociadoDTO = dataAccessObject.cargarConLimite(0);
			fail("Se esperaba un AssertionError");
		} catch(AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void TestCargarConLimiteInvalidoNegativo() {
		try {
			ArrayList<AsociadoDTO> asociadoDTO = dataAccessObject.cargarConLimite(-1);
			fail("Se esperaba un AssertionError");
		} catch(AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	//cargar(int id)
	
	@Test
	public void TestCargarExitoso() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.cargar(1);
		} catch(AssertionError e) {
			fail("No debería lanzarse una excepción");
		} catch (SQLException e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	@Test
	public void TestCargarInvalidoCero() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.cargar(0);
			fail("Se esperaba un AssertionError");
		} catch(AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	@Test
	public void TestCargarInvalidoNegativo() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.cargar(-1);
			fail("Se esperaba un AssertionError");
		} catch(AssertionError e) {
		} catch (SQLException e) {
		}
	}
	
	//cargarBD()
	
	@Test
	public void TestCargarBDExitoso() {
		try {
			ArrayList<AsociadoDTO> asociados = dataAccessObject.cargarBD();
		}catch(SQLException e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	//eliminar(String DNI)
	
	@Test
	public void TestEliminarExitoso() {
		try {
			dataAccessObject.eliminar("12345678");
		}catch(SQLException e) {
			fail("No debería lanzarse una excepción");
		}
		
		try {
			ArrayList<AsociadoDTO> asociados = dataAccessObject.cargarBD();
			int encontrado = 0;
			
			for (AsociadoDTO aso : asociados) {
				if (aso.getDni().contentEquals("12345678")) {
					encontrado = 1;
				}
			}
			Assert.assertEquals("No se eliminó el asociado",encontrado,0);
		}catch(SQLException e) {
		}	
	}
	
	@Test
	public void TestEliminarInvalidoNoExistente() {
		try {
			dataAccessObject.eliminar("999999");
			fail("Se esperaba un AssertionError");
		}catch(AssertionError e) {
		}catch(SQLException e) {
		}
	}
	
	@Test
	public void TestEliminarInvalidoNulo() {
		try {
			dataAccessObject.eliminar(null);
			fail("Se esperaba un AssertionError");
		}catch(AssertionError e) {
		}catch(SQLException e) {
		}
	}
	
	//actualizar(Asociado asociado)
	
	@Test
	public void TestActualizarExitoso() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
		
		try {
			dataAccessObject.actualizar(asociado);
		}catch(AssertionError e){
			fail("No debería lanzarse una excepción");
		}catch(SQLException e){
			fail("No debería lanzarse una excepción");
		}
	}
	
	@Test
	public void TestActualizarInvalido() {	
		try {
			dataAccessObject.actualizar(null);
			fail("Se esperaba un AssertionError");
		}catch(AssertionError e){
		}catch(SQLException e){
		}
	}
	
	//CrearDTO(Asociado asociado)
	
	@Test
	public void TestCrearDTOExitoso() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
		
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("No debería lanzarse una excepción");
		}catch(AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTOInvalido() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(null);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTONombreInvalido() {
		Asociado asociado = new Asociado(null,"Fulanito","1","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTOApellidoInvalido() {
		Asociado asociado = new Asociado("Cosme",null,"0","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	
	@Test
	public void testCrearDTODNIInvalidoNulo() {
		Asociado asociado = new Asociado("Cosme","Fulanito",null,"2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTOCiudadInvalido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio(null,1234,"Av. Siempreviva"));
	
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTOCalleInvalido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",1234,null));
	
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTONumeroInvalidoNegativo() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",-1,"Av. Siempreviva"));
	
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTONumeroInvalidoCero() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0","2231234567",new Domicilio("Springfield",0,"Av. Siempreviva"));
			
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testCrearDTOTelefonoInvalido() {
		Asociado asociado = new Asociado("Cosme","Fulanito","0",null,new Domicilio("Springfield",1234,"Av. Siempreviva"));
		
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	//CrearDTO(int id, String nombre, String apellido, String dni, String calle, int numero, String ciudad, String telefono)
	
	@Test
	public void TestCrearDTODatosExitoso() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito","0","Springfield",1234,"Av. Siempreviva","2231234567");
		}catch(AssertionError e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	@Test
	public void testCrearDTODatosIDInvalidoCero() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(0,"Cosme","Fulanito","0","Springfield",1234,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosIDInvalidoNegativo() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(-1,"Cosme","Fulanito","0","Springfield",1234,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosNombreInvalido() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,null,"Fulanito","0","Springfield",1234,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosApellidoInvalido() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme",null,"0","Springfield",1234,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	
	@Test
	public void testCrearDTODatosDNIInvalidoNulo() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito",null,"Springfield",1234,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosCiudadInvalido() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito","0",null,1234,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosCalleInvalido() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito","0","Springfield",1234,null,"2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosNumeroInvalidoNegativo() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito","0","Springfield",-1,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
	
	@Test
	public void testCrearDTODatosNumeroInvalidoCero() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito","0","Springfield",0,"Av. Siempreviva","2231234567");
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testCrearDTODatosTelefonoInvalido() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(1,"Cosme","Fulanito","0","Springfield",1234,"Av. Siempreviva",null);
			fail("Se esperaba un AssertionError");
		} catch (AssertionError e) {
		}
	}
}
