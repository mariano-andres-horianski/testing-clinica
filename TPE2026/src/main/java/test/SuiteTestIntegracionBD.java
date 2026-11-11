package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestBaseDeDatosConDatos.class, TestBaseDeDatosSinConexion.class, TestBaseDeDatosSinDatos.class,
		TestDataAccessObject.class })
public class SuiteTestIntegracionBD {

}
