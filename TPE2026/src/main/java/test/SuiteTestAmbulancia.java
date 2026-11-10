package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestAmbulanciaIntegracion.class, TestAmbulanciaMock.class })
public class SuiteTestAmbulancia {

}
