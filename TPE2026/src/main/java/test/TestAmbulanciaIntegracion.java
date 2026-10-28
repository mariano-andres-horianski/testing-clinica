package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import modelo.modeloDominio.ambulancia.Ambulancia;
import modelo.modeloDominio.ambulancia.AtendiendoEnDomicilioState;
import modelo.modeloDominio.ambulancia.DisponibleState;
import modelo.modeloDominio.ambulancia.EnTallerState;
import modelo.modeloDominio.ambulancia.RegresandoClinicaSinPacienteState;
import modelo.modeloDominio.ambulancia.RegresandoDelTallerState;
import modelo.modeloDominio.ambulancia.RetornoAutomatico;
import modelo.modeloDominio.ambulancia.State;
import modelo.modeloDominio.ambulancia.TrasladandoPacienteClinicaState;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;
import modelo.modeloDominio.util.Domicilio;


//Pruebas de integración y concurrencia para Ambulancia, sus estados y la tarea concurrente RetornoAutomatico.

public class TestAmbulanciaIntegracion {

    @Rule
    public Timeout tiempoMaximo = Timeout.seconds(10);
    private static final long tBloqueo = 100;

    private Ambulancia ambulancia;
    private Asociado primerAsociado;
    private Asociado segundoAsociado;
    private Operario operarioTaller;
    private RetornoAutomatico rutinaRetorno;

    private boolean señalInterrupcion;

    private int activosSimultaneos;
    private int completados;
    private boolean huboSuperposicion;

    @Before
    public void iniciar() {
        ambulancia = new Ambulancia();
        rutinaRetorno = new RetornoAutomatico(ambulancia);

        primerAsociado = mock(Asociado.class);
        segundoAsociado = mock(Asociado.class);
        operarioTaller = mock(Operario.class);

        when(primerAsociado.getNombre()).thenReturn("a");
        when(segundoAsociado.getNombre()).thenReturn("e");
        when(operarioTaller.getNombre()).thenReturn("i");
    }

    @After
    public void finalizar() {
        ambulancia.setSimulacionActiva(false);
    }

    private void comprobarEstado(Class<? extends State> claseEsperada) {
        assertSame(claseEsperada, ambulancia.getEstadoActual().getClass());
    }

    // =========================================================================
    // AMBULANCIA
    // =========================================================================

    @Test
    public void flujoAtencionDomicilio() {
        ambulancia.atenderDomicilio(primerAsociado);
        comprobarEstado(AtendiendoEnDomicilioState.class);
        assertTrue(ambulancia.isOcupado());

        ambulancia.regresarSinPaciente();
        comprobarEstado(RegresandoClinicaSinPacienteState.class);
        assertFalse(ambulancia.isOcupado());

        ambulancia.retornoAutomatico(rutinaRetorno);
        comprobarEstado(DisponibleState.class);
    }

    @Test
    public void flujoDomicilioConRetornoAutomatico() {
        ambulancia.atenderDomicilio(primerAsociado);
        comprobarEstado(AtendiendoEnDomicilioState.class);
        assertTrue(ambulancia.isOcupado());

        ambulancia.retornoAutomatico(rutinaRetorno);
        comprobarEstado(RegresandoClinicaSinPacienteState.class);

        ambulancia.retornoAutomatico(rutinaRetorno);
        comprobarEstado(DisponibleState.class);
        assertFalse("Al volver a estar Disponible mediante retornoAutomatico no debería seguir ocupada", ambulancia.isOcupado());
    }

    @Test
    public void flujoTraslado() {
        ambulancia.trasladarALaClinica(primerAsociado);
        comprobarEstado(TrasladandoPacienteClinicaState.class);
        assertTrue(ambulancia.isOcupado());

        ambulancia.regresarSinPaciente();
        comprobarEstado(DisponibleState.class);
        assertFalse(ambulancia.isOcupado());
    }

    @Test
    public void flujoMantenimiento() {
        ambulancia.solicitarMantenimiento(operarioTaller);
        comprobarEstado(EnTallerState.class);
        assertTrue(ambulancia.isOcupado());

        ambulancia.volviendoDelTaller();
        comprobarEstado(RegresandoDelTallerState.class);
        assertFalse(ambulancia.isOcupado());

        ambulancia.retornoAutomatico(rutinaRetorno);
        comprobarEstado(DisponibleState.class);
    }

    // =========================================================================
    // TRANSICIONES DE ESTADOS
    // =========================================================================

    // --- AtendiendoEnDomicilio -----------------------------------------------

    @Test
    public void atendiendoAnteDomicilio() {
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        comprobarEstado(AtendiendoEnDomicilioState.class);
    }

    @Test
    public void atendiendoAnteTraslado() {
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        comprobarEstado(AtendiendoEnDomicilioState.class);
    }

    @Test
    public void atendiendoAnteMantenimiento() {
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        comprobarEstado(AtendiendoEnDomicilioState.class);
    }

    @Test
    public void atendiendoAnteRetorno() {
        ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
        ambulancia.getEstadoActual().RetornoClinica();
        comprobarEstado(RegresandoClinicaSinPacienteState.class);
    }

    // --- RegresandoClinicaSinPaciente ----------------------------------------

    @Test
    public void regresandoClinicaAnteDomicilio() {
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        comprobarEstado(AtendiendoEnDomicilioState.class);
    }

    @Test
    public void regresandoClinicaAnteTraslado() {
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        comprobarEstado(TrasladandoPacienteClinicaState.class);
    }

    @Test
    public void regresandoClinicaAnteMantenimiento() {
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        comprobarEstado(RegresandoClinicaSinPacienteState.class);
    }

    @Test
    public void regresandoClinicaAnteRetorno() {
        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        ambulancia.getEstadoActual().RetornoClinica();
        comprobarEstado(DisponibleState.class);
    }

    // --- Disponible ----------------------------------------------------------

    @Test
    public void disponibleAnteDomicilio() {
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        comprobarEstado(AtendiendoEnDomicilioState.class);
    }

    @Test
    public void disponibleAnteTraslado() {
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        comprobarEstado(TrasladandoPacienteClinicaState.class);
    }

    @Test
    public void disponibleAnteMantenimiento() {
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        comprobarEstado(EnTallerState.class);
    }

    @Test
    public void disponibleAnteRetorno() {
        ambulancia.getEstadoActual().RetornoClinica();
        comprobarEstado(DisponibleState.class);
    }

    // --- TrasladandoPacienteClinica ------------------------------------------

    @Test
    public void trasladandoAnteDomicilio() {
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        comprobarEstado(TrasladandoPacienteClinicaState.class);
    }

    @Test
    public void trasladandoAnteTraslado() {
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        comprobarEstado(TrasladandoPacienteClinicaState.class);
    }

    @Test
    public void trasladandoAnteMantenimiento() {
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        comprobarEstado(TrasladandoPacienteClinicaState.class);
    }

    @Test
    public void trasladandoAnteRetorno() {
        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        ambulancia.getEstadoActual().RetornoClinica();
        comprobarEstado(DisponibleState.class);
    }

    // --- EnTaller ------------------------------------------------------------

    @Test
    public void enTallerAnteDomicilio() {
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        comprobarEstado(EnTallerState.class);
    }

    @Test
    public void enTallerAnteTraslado() {
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        comprobarEstado(EnTallerState.class);
    }

    @Test
    public void enTallerAnteMantenimiento() {
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        comprobarEstado(RegresandoDelTallerState.class);
    }

    @Test
    public void enTallerAnteRetorno() {
        ambulancia.setState(new EnTallerState(ambulancia));
        ambulancia.getEstadoActual().RetornoClinica();
        comprobarEstado(EnTallerState.class);
    }

    // --- RegresandoDelTaller -------------------------------------------------

    @Test
    public void regresandoDelTallerAnteDomicilio() {
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        comprobarEstado(RegresandoDelTallerState.class);
    }

    @Test
    public void regresandoDelTallerAnteTraslado() {
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        comprobarEstado(RegresandoDelTallerState.class);
    }

    @Test
    public void regresandoDelTallerAnteMantenimiento() {
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        comprobarEstado(RegresandoDelTallerState.class);
    }

    @Test
    public void regresandoDelTallerAnteRetorno() {
        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        ambulancia.getEstadoActual().RetornoClinica();
        comprobarEstado(DisponibleState.class);
    }

    
    // =========================================================================
    // CONCURRENCIA: EXCLUSIÓN MUTUA Y SINCRONIZACIÓN
    // =========================================================================


    private synchronized void empezarServicio() {
        activosSimultaneos++;
        if (activosSimultaneos > 1) {
            huboSuperposicion = true;
        }
    }

    private synchronized void terminarServicio() {
        activosSimultaneos--;
        completados++;
    }

    @Test
    public void asociadosPidenAmbulanciaOcupada() throws InterruptedException {
        ambulancia.atenderDomicilio(mock(Asociado.class));
        assertTrue(ambulancia.isOcupado());

        Thread hiloTraslado = new Thread(() -> ambulancia.trasladarALaClinica(primerAsociado));
        Thread hiloDomicilio = new Thread(() -> ambulancia.atenderDomicilio(segundoAsociado));
        hiloTraslado.start();
        hiloDomicilio.start();

        try {
            Thread.sleep(tBloqueo);
            assertTrue("Ambos hilos de asociados deben quedar bloqueados", hiloTraslado.isAlive() && hiloDomicilio.isAlive());
            // Mientras esperan no deben tocar el estado: sigue el del asociado que la ocupaba
            comprobarEstado(AtendiendoEnDomicilioState.class);

            // Primera liberación: avanza únicamente uno de los dos hilos
            ambulancia.regresarSinPaciente();
            Thread.sleep(200);
            assertTrue("Exactamente un asociado debe haber conseguido la ambulancia", hiloTraslado.isAlive() != hiloDomicilio.isAlive());

            Thread hiloPrimero = hiloTraslado.isAlive() ? hiloDomicilio : hiloTraslado;
            Thread hiloSegundo = (hiloPrimero == hiloTraslado) ? hiloDomicilio : hiloTraslado;
            assertTrue("El otro asociado debe seguir esperando: solo uno puede usar la ambulancia", hiloSegundo.isAlive());
            comprobarEstado(hiloPrimero == hiloTraslado ? TrasladandoPacienteClinicaState.class : AtendiendoEnDomicilioState.class);
            assertTrue("El asociado que despertó debe haber ocupado la ambulancia", ambulancia.isOcupado());

            // Segunda liberación: avanza el hilo restante
            ambulancia.regresarSinPaciente();
            hiloSegundo.join(2000);
            assertFalse("El asociado restante debió completar su atención tras la segunda liberación", hiloSegundo.isAlive());
        } finally {
            ambulancia.setSimulacionActiva(false);
            ambulancia.regresarSinPaciente();
            hiloTraslado.join(2000);
            hiloDomicilio.join(2000);
        }

        assertFalse("El hilo de traslado no finalizó su ejecución", hiloTraslado.isAlive());
        assertFalse("El hilo de atención a domicilio no finalizó su ejecución", hiloDomicilio.isAlive());
    }

    @Test
    public void operarioPideMantenimientoDuranteTraslado() throws InterruptedException {

    	ambulancia.trasladarALaClinica(mock(Asociado.class));
        assertTrue(ambulancia.isOcupado());

        Thread hiloMantenimiento = new Thread(() -> ambulancia.solicitarMantenimiento(operarioTaller));
        hiloMantenimiento.start();

        try {
            Thread.sleep(tBloqueo);
            assertTrue("El hilo de mantenimiento debe permanecer en espera mientras el vehículo está ocupado", hiloMantenimiento.isAlive());

            ambulancia.regresarSinPaciente();

            hiloMantenimiento.join(2000);
            assertFalse("El operario no logró ingresar la ambulancia al taller tras liberarse", hiloMantenimiento.isAlive());
            comprobarEstado(EnTallerState.class);
            assertTrue(ambulancia.isOcupado());
        } finally {
            ambulancia.setSimulacionActiva(false);
            hiloMantenimiento.join(2000);
        }
    }

    @Test
    public void AsociadosCompitenPorLaAmbulancia() throws InterruptedException {
        final int totalSolicitantes = 4;
        final List<Thread> hilosCompetidores = new ArrayList<>();

        ambulancia.atenderDomicilio(mock(Asociado.class));

        for (int i = 0; i < totalSolicitantes; i++) {
            Thread hilo = new Thread(() -> {
                try {
                    ambulancia.atenderDomicilio(primerAsociado);

                    empezarServicio();
                    Thread.sleep(15);
                    terminarServicio();

                    ambulancia.regresarSinPaciente();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilosCompetidores.add(hilo);
            hilo.start();
        }

        Thread.sleep(tBloqueo);
        for (Thread hilo : hilosCompetidores) {
            assertTrue("Los hilos competidores deberían estar en espera", hilo.isAlive());
        }

        ambulancia.regresarSinPaciente();

        for (Thread hilo : hilosCompetidores) {
            hilo.join(3000);
            assertFalse("Uno de los hilos competidores no terminó", hilo.isAlive());
        }

        assertFalse("Se detectó más de un hilo utilizando la ambulancia simultáneamente", huboSuperposicion);
        assertEquals("No se completaron todas las atenciones esperadas", totalSolicitantes, completados);
        assertFalse(ambulancia.isOcupado());
    }

    @Test
    public void AsociadosLleganConAmbulanciaLibre() throws InterruptedException {
        final List<Thread> hilosSolicitantes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Thread hilo = new Thread(() -> ambulancia.atenderDomicilio(primerAsociado));
            hilosSolicitantes.add(hilo);
            hilo.start();
        }

        try {
            Thread.sleep(tBloqueo);
            int quedaronEsperando = 0;
            for (Thread hilo : hilosSolicitantes) {
                if (hilo.isAlive()) {
                    quedaronEsperando++;
                }
            }
            assertEquals("Solo un asociado debe conseguir la ambulancia libre; los otros dos esperan", 2, quedaronEsperando);
        } finally {
            ambulancia.setSimulacionActiva(false);
            for (Thread hilo : hilosSolicitantes) {
                hilo.join(2000);
            }
        }
    }


    @Test
    public void finDeSimulacionConHilosEsperando() throws InterruptedException {
        ambulancia.setOcupado(true);

        Thread hiloAsociado = new Thread(() -> ambulancia.atenderDomicilio(primerAsociado));
        Thread hiloOperario = new Thread(() -> ambulancia.solicitarMantenimiento(operarioTaller));
        hiloAsociado.start();
        hiloOperario.start();

        Thread.sleep(tBloqueo);
        assertTrue("Ambos hilos deberían haber quedado esperando", hiloAsociado.isAlive() && hiloOperario.isAlive());

        ambulancia.setSimulacionActiva(false);

        hiloAsociado.join(1000);
        hiloOperario.join(1000);
        assertFalse("El hilo del asociado debió terminar al cerrarse la simulación", hiloAsociado.isAlive());
        assertFalse("El hilo del operario debió terminar al cerrarse la simulación", hiloOperario.isAlive());
        comprobarEstado(DisponibleState.class);
    }

    @Test
    public void interrupcionDeAsociadoEnEspera() throws InterruptedException {
        ambulancia.setOcupado(true);

        Thread hiloEspera = new Thread(() -> {
            ambulancia.atenderDomicilio(primerAsociado);
            señalInterrupcion = Thread.currentThread().isInterrupted();
        });
        hiloEspera.start();

        Thread.sleep(tBloqueo);
        assertTrue("El hilo debería encontrarse esperando", hiloEspera.isAlive());

        hiloEspera.interrupt();
        hiloEspera.join(1000);

        assertFalse("El hilo interrumpido no finalizó a tiempo", hiloEspera.isAlive());
        assertTrue("No se preservó el flag de interrupción del hilo", señalInterrupcion);
        comprobarEstado(DisponibleState.class);
    }

    @Test
    public void finDeSimulacionSegunSolicitudesPendientes() {
        Domicilio direccion = new Domicilio("wwwww", 1111, "Mar del Plata");

        new Asociado("aaaa", "eeee", "111111", "22222", direccion, 0, ambulancia);
        new Asociado("uuuu", "ooooo", "333333", "4444444", direccion, 2, ambulancia);
        ambulancia.verificarYFinalizarSimulacion();
        assertTrue("La simulación no debe detenerse mientras existan solicitudes sin atender", ambulancia.isSimulacionActiva());

        Ambulancia otraAmbulancia = new Ambulancia();
        new Asociado("iiii", "iiiii", "55555", "66666", direccion, 0, otraAmbulancia);
        otraAmbulancia.verificarYFinalizarSimulacion();
        assertFalse("La simulación debió darse por terminada al no quedar pedidos pendientes", otraAmbulancia.isSimulacionActiva());
    }

    // =========================================================================
    // RetornoAutomatico
    // =========================================================================

    @Test
    public void retornoAutomaticoConSimulacionActiva() throws InterruptedException {
        Thread hiloRetorno = new Thread(rutinaRetorno);
        hiloRetorno.start();

        Thread.sleep(200);

        try {
            assertTrue("El hilo de retorno automático debe permanecer en ejecución con simulación activa", hiloRetorno.isAlive());
        } finally {
            ambulancia.setSimulacionActiva(false);
            hiloRetorno.interrupt();
            hiloRetorno.join(1000);
        }
    }

    @Test
    public void retornoAutomaticoConSimulacionTerminada() throws InterruptedException {
        ambulancia.setSimulacionActiva(false);

        Thread hiloRetorno = new Thread(rutinaRetorno);
        hiloRetorno.start();
        hiloRetorno.join(500);

        try {
            assertFalse("El hilo de retorno automático no debe quedar activo si la simulación ya terminó", hiloRetorno.isAlive());
        } finally {
            hiloRetorno.interrupt();
        }
    }
}
