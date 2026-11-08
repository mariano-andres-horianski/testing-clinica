package test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import modelo.modeloDominio.clinica.Clinica;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

public class TestDataAccessObject {
	private DataAccessObject dataAccessObject = new DataAccessObject();
	
   @Before
    public void prepararBD() {
        Asociado asociado = new Asociado(
            "Senior", "Bolainas", "99999999", "2231234567",
            new Domicilio("Springfield", 1000, "Av. Mammon")
        );

        try {
            dataAccessObject.guardar(asociado);
        } catch(SQLException e) {
            fail("No se pudo crear el asociado de prueba");
        }
    }

   @After
   public void limpiarBD() {
       try {
           dataAccessObject.eliminar("99999999");
           dataAccessObject.eliminar("0");
       } catch(SQLException e) {
           fail("No se pudieron eliminar los datos de prueba");
       }
   }
	
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
			fail("No debería lanzarse una excepción");
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
		Asociado asociado = new Asociado("Cosme","Fulanito","99999999","2231234567",new Domicilio("Springfield",1234,"Av. Siempreviva"));
	
		try {
			dataAccessObject.guardar(asociado);
			fail("Se esperaba un SQLException");
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
			ArrayList<AsociadoDTO> asociadoDTO = dataAccessObject.cargarConLimite(3);
			System.out.println(asociadoDTO);
			Assert.assertEquals("La cantidad de asociados no es correcta", 3, asociadoDTO.size());
		} catch(AssertionError e) {
			fail("No debería lanzarse una excepción");
		} catch (SQLException e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	
	//cargar(int id)
	
	@Test
	public void TestCargarExitoso() {
		try {
			AsociadoDTO asociadoDTO = dataAccessObject.cargar(1);
			System.out.println(asociadoDTO);
			Assert.assertEquals("El nombre no es correcto", "Juan", asociadoDTO.getNombre());
			Assert.assertEquals("El apellido no es correcto", "State", asociadoDTO.getApellido());
			Assert.assertEquals("El DNI no es correcto", "12345678", asociadoDTO.getDni());
			Assert.assertEquals("La calle no es correcta", "Buenos Aires", asociadoDTO.getCalle());
			Assert.assertEquals("El número no es correcto", 123, asociadoDTO.getNumero());
			Assert.assertEquals("La ciudad no es correcta", "Mar del Plata", asociadoDTO.getCiudad());
			Assert.assertEquals("El teléfono no es correcto", "2235551234", asociadoDTO.getTelefono());
		} catch(AssertionError e) {
			fail("No debería lanzarse una excepción");
		} catch (SQLException e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	
	//cargarBD()
	
	@Test
	public void TestCargarBDExitoso() {
		try {
			ArrayList<AsociadoDTO> asociados = dataAccessObject.cargarBD();
			System.out.println(asociados);
			Assert.assertEquals("La cantidad de asociados no es correcta", 21, asociados.size());
		}catch(SQLException e) {
			fail("No debería lanzarse una excepción");
		}
	}
	
	//eliminar(String DNI)
	
	@Test
	public void TestEliminarExitoso() {
		try {
			dataAccessObject.eliminar("99999999");
		}catch(SQLException e) {
			fail("No debería lanzarse una excepción");
		}
		
		try {
			ArrayList<AsociadoDTO> asociados = dataAccessObject.cargarBD();
			int encontrado = 0;
			
			for (AsociadoDTO aso : asociados) {
				if (aso.getDni().contentEquals("99999999")) {
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
			dataAccessObject.eliminar("000000");
			fail("Se esperaba un SQLException");
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
	
	
	//CrearDTO(Asociado asociado)
	
	@Test
	public void TestCrearDTOExitoso() {
	    Asociado asociado = new Asociado("Cosme", "Fulanito", "0", "2231234567", new Domicilio("Springfield", 1234, "Av. Siempreviva"));

	    AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO(asociado);

	    Assert.assertEquals("El nombre no es correcto", "Cosme", asociadoDTO.getNombre());
	    Assert.assertEquals("El apellido no es correcto", "Fulanito", asociadoDTO.getApellido());
	    Assert.assertEquals("El DNI no es correcto", "0", asociadoDTO.getDni());
	    Assert.assertEquals("El teléfono no es correcto", "2231234567", asociadoDTO.getTelefono());
	    Assert.assertEquals("La calle no es correcta", "Av. Siempreviva", asociadoDTO.getCalle());
	    Assert.assertEquals("El número no es correcto", 1234, asociadoDTO.getNumero());
	    Assert.assertEquals("La ciudad no es correcta", "Springfield", asociadoDTO.getCiudad());
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
	    AsociadoDTO asociadoDTO = dataAccessObject.CrearDTO( 1, "Cosme", "Fulanito", "0", "Springfield", 1234, "Av. Siempreviva", "2231234567");

	    Assert.assertEquals("El nombre no es correcto", "Cosme", asociadoDTO.getNombre());
	    Assert.assertEquals("El apellido no es correcto", "Fulanito", asociadoDTO.getApellido());
	    Assert.assertEquals("El DNI no es correcto", "0", asociadoDTO.getDni());
	    Assert.assertEquals("La calle no es correcta", "Springfield", asociadoDTO.getCalle());
	    Assert.assertEquals("El número no es correcto", 1234, asociadoDTO.getNumero());
	    Assert.assertEquals("La ciudad no es correcta", "Av. Siempreviva", asociadoDTO.getCiudad());
	    Assert.assertEquals("El teléfono no es correcto", "2231234567", asociadoDTO.getTelefono());
	}
	
}