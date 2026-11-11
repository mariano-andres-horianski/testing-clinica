package test;

import static org.junit.Assert.*;


import org.junit.Assert;
import org.junit.Test;


import persistencia.AsociadoDTO;

public class TestAsociadoDTO {

	@Test
	public void testConstructorPorDefecto() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO();
	    Assert.assertNotNull("No se ha creado correctamente la instancia de AsociadoDTO", asociadoDTO);
	}
	
	@Test
	public void testConstructor() {
		AsociadoDTO asociadoDTO= new AsociadoDTO("Cosme","Fulanito","12345678","Springfield","Av. Siempreviva",1234,"2231234567");
		String nombre= asociadoDTO.getNombre();
		String apellido= asociadoDTO.getApellido();
		String dni= asociadoDTO.getDni();
		String ciudad= asociadoDTO.getCiudad();
		String calle= asociadoDTO.getCalle();
		int numero= asociadoDTO.getNumero();
		String telefono= asociadoDTO.getTelefono();
		Assert.assertEquals("El nombre no ha sido registrado correctamente","Cosme", nombre);
		Assert.assertEquals("El apellido no ha sido registrado correctamente","Fulanito", apellido);
		Assert.assertEquals("El DNI no ha sido registrado correctamente","12345678", dni);
		Assert.assertEquals("La ciudad no ha sido registrada correctamente","Springfield", ciudad);
		Assert.assertEquals("La calle no ha sido registrada correctamente","Av. Siempreviva", calle);
		Assert.assertEquals("El numero no ha sido registrado correctamente",1234, numero);
		Assert.assertEquals("El telefono no ha sido registrado correctamente","2231234567", telefono);
	}
	
	@Test
	public void testConstructorNombreNulo() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO(null, "Fulanito", "12345678", "Springfield", "Av. Siempreviva", 1234, "2231234567");
	    Assert.assertNotNull("El nombre no debería ser nulo", asociadoDTO.getNombre());
	}

	@Test
	public void testConstructorApellidoNulo() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", null, "12345678", "Springfield", "Av. Siempreviva", 1234, "2231234567");
	    Assert.assertNotNull("El apellido no debería ser nulo", asociadoDTO.getApellido());
	}

	@Test
	public void testConstructorDniNulo() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", "Fulanito", null, "Springfield", "Av. Siempreviva", 1234, "2231234567");
	    Assert.assertNotNull("El DNI no debería ser nulo", asociadoDTO.getDni());
	}

	@Test
	public void testConstructorCiudadNula() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", "Fulanito", "12345678", null, "Av. Siempreviva", 1234, "2231234567");
	    Assert.assertNotNull("La ciudad no debería ser nula", asociadoDTO.getCiudad());
	}

	@Test
	public void testConstructorCalleNula() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", "Fulanito", "12345678", "Springfield", null, 1234, "2231234567");
	    Assert.assertNotNull("La calle no debería ser nula", asociadoDTO.getCalle());
	}

	@Test
	public void testConstructorNumeroNegativo() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", "Fulanito", "12345678", "Springfield", "Av. Siempreviva", -1, "2231234567");
	    Assert.assertTrue("El número no debería ser negativo", asociadoDTO.getNumero() >= 0);
	}

	@Test
	public void testConstructorNumeroCero() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", "Fulanito", "12345678", "Springfield", "Av. Siempreviva", 0, "2231234567");
	    Assert.assertEquals("El número no ha sido registrado correctamente", 0, asociadoDTO.getNumero());
	}

	@Test
	public void testConstructorTelefonoNulo() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO("Cosme", "Fulanito", "12345678", "Springfield", "Av. Siempreviva", 1234, null);
	    Assert.assertNotNull("El teléfono no debería ser nulo", asociadoDTO.getTelefono());
	}
	
	@Test
	public void testSetNombre() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setNombre("Juan");
		Assert.assertEquals("El nombre no ha sido registrado correctamente","Juan", asociadoDTO.getNombre());
	}
	
	@Test
	public void testSetApellido() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setApellido("Perez");
		Assert.assertEquals("El apellido no ha sido registrado correctamente","Perez", asociadoDTO.getApellido());
	}
	
	@Test
	public void testSetDNI() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setDni("98765432");
		Assert.assertEquals("El DNI no ha sido registrado correctamente","98765432", asociadoDTO.getDni());
	}
	
	@Test
	public void testSetCiudad() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setCiudad("Mar del Plata");
		Assert.assertEquals("La ciudad no ha sido registrada correctamente","Mar del Plata", asociadoDTO.getCiudad());
	}
	
	@Test
	public void testSetCalle() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setCalle("San Martin");
		Assert.assertEquals("La calle no ha sido registrada correctamente","San Martin", asociadoDTO.getCalle());
	}
	
	@Test
	public void testSetNumero() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setNumero(4321);
		Assert.assertEquals("El numero no ha sido registrado correctamente",4321, asociadoDTO.getNumero());
	}
	
	@Test
	public void testSetNumeroCeroValido() {
	    AsociadoDTO asociadoDTO = new AsociadoDTO();
	    asociadoDTO.setNumero(0);
	    Assert.assertEquals("El numero 0 deberia ser valido", 0, asociadoDTO.getNumero());
	}
	
	@Test
	public void testSetTelefono() {
		AsociadoDTO asociadoDTO= new AsociadoDTO();
		asociadoDTO.setTelefono("2239876456");
		Assert.assertEquals("El telefono no ha sido registrado correctamente","2239876456", asociadoDTO.getTelefono());
	}

	@Test
	public void testSetNombreInvalido() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setNombre(null);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testSetApellidoInvalido() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setApellido(null);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testSetDniInvalido() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setDni(null);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testSetCiudadInvalida() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setCiudad(null);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testSetCalleInvalida() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setCalle(null);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testSetNumeroInvalidoNegativo() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setNumero(-1);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}

	@Test
	public void testSetTelefonoInvalido() {
		AsociadoDTO asociadoDTO = new AsociadoDTO();
		try {
			asociadoDTO.setTelefono(null);
			fail("Se esperaba una AssertionError");
		} catch (AssertionError e) {
		}
	}



}

