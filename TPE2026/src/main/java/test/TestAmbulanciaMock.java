package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import modelo.modeloDominio.ambulancia.Ambulancia;
import modelo.modeloDominio.ambulancia.RetornoAutomatico;
import modelo.modeloDominio.ambulancia.State;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;

public class TestAmbulanciaMock {

    // si un test tarda más de 5 segundos (por un bloqueo), falla automáticamente
    @Rule
    public Timeout limiteGlobal = Timeout.seconds(5);

    private Ambulancia ambulancia;
    private State estadoMock;
    private Asociado asociadoMock;
    private Operario operarioMock;

    @Before
    public void iniciar() {
        ambulancia = new Ambulancia();
        estadoMock = mock(State.class);
        asociadoMock = mock(Asociado.class);
        operarioMock = mock(Operario.class);

        // Se inyecta el mock de State en la ambulancia
        ambulancia.setState(estadoMock);
    }

    @After
    public void finalizar() {
        // Garantiza que ningún hilo quede colgado al terminar una prueba
        ambulancia.setSimulacionActiva(false);
    }

    // DELEGACIÓN DIRECTA Y OCUPACIÓN 
    @Test
    public void testTrasladarALaClinica() {
        ambulancia.trasladarALaClinica(asociadoMock);

        verify(estadoMock, times(1)).SolicitudDeTraslado();
        assertTrue("La ambulancia debería marcarse como ocupada", ambulancia.isOcupado());
    }

    @Test
    public void testAtenderDomicilio() {
        ambulancia.atenderDomicilio(asociadoMock);

        verify(estadoMock, times(1)).SolicitudDeAtencionDomicilio();
        assertTrue("La ambulancia debería marcarse como ocupada", ambulancia.isOcupado());
    }

    @Test
    public void testSolicitarMantenimiento() {
        ambulancia.solicitarMantenimiento(operarioMock);

        verify(estadoMock, times(1)).SolicitudMantenimiento();
        assertTrue("La ambulancia debería marcarse como ocupada", ambulancia.isOcupado());
    }

    @Test
    public void testRetornoAutomatico() {
        RetornoAutomatico retorno = new RetornoAutomatico(ambulancia);

        ambulancia.retornoAutomatico(retorno);

        verify(estadoMock, times(1)).RetornoClinica();
    }

    // RETORNOS Y LIBERACIÓN DEL RECURSO

    @Test
    public void testRegresarSinPaciente() {
        ambulancia.setOcupado(true);

        ambulancia.regresarSinPaciente();

        verify(estadoMock, times(1)).RetornoClinica();
        assertFalse("La ambulancia debería quedar liberada", ambulancia.isOcupado());
    }

    @Test
    public void testVolviendoDelTaller() {
        ambulancia.setOcupado(true);

        ambulancia.volviendoDelTaller();

        verify(estadoMock, times(1)).SolicitudMantenimiento();
        assertFalse("La ambulancia debería quedar liberada", ambulancia.isOcupado());
    }

    // Guardas y Espera Concurrente
    /**
     * Si la simulación finalizó, la ambulancia debe ignorar la solicitud:
     * no debe ocupar el recurso ni invocar al estado.
     */
    @Test
    public void testSimulacionFinalizada() {
        ambulancia.setSimulacionActiva(false);

        ambulancia.trasladarALaClinica(asociadoMock);

        // Verificamos que NUNCA se haya llamado al estado
        verify(estadoMock, never()).SolicitudDeTraslado();
        assertFalse("No debería ocuparse si la simulación está apagada", ambulancia.isOcupado());
    }

    /**
     * Si la ambulancia está ocupada, la solicitud (en otro hilo) debe esperar (wait)
     * sin invocar al estado. Al liberarse la ambulancia, recién ahí el hilo avanza y delega.
     */
    @Test
    public void testEspera() throws Exception {
        ambulancia.setOcupado(true);

        // Un asociado pide atención desde otro hilo
        Thread hiloAsociado = new Thread(() -> ambulancia.atenderDomicilio(asociadoMock));
        hiloAsociado.start();

        Thread.sleep(100); // breve pausa para permitir que el hilo intente entrar
        verify(estadoMock, never()).SolicitudDeAtencionDomicilio();

        // Liberamos la ambulancia: el hilo debe despertar y delegar
        ambulancia.regresarSinPaciente();

        // Con timeout(2000) se espera hasta 2 segundos a que ocurra la llamada
        verify(estadoMock, timeout(2000).times(1)).SolicitudDeAtencionDomicilio();

        hiloAsociado.join(1000);
        assertFalse("El hilo asociado debería haber terminado", hiloAsociado.isAlive());
    }
}
