package test;

import java.lang.reflect.Field;
import java.sql.SQLException;

import persistencia.BaseDeDatos;

/* Escenario: la base de datos existe pero la conexión está cerrada.
 * Todas las operaciones sobre la BD deberían fallar con SQLException.
 */
public class EscenarioBDSinConexion {
	private BaseDeDatos bd;

	public EscenarioBDSinConexion() throws SQLException {
		this.bd = BaseDeDatos.getInstancia();
		this.bd.cerrarConexion();
	}

	public BaseDeDatos getBd() {
		return bd;
	}

	/* Deja el Singleton en un estado sano para los demás tests.
	 * BaseDeDatos no tiene forma pública de reabrir la conexión y getInstancia()
	 * devuelve siempre la misma instancia, así que se descarta la instancia cerrada
	 * por reflexión y se crea una nueva (abre conexión y ejecuta iniciarBD()).
	 */
	public void restaurar() throws Exception {
		Field instancia = BaseDeDatos.class.getDeclaredField("instancia");
		instancia.setAccessible(true);
		instancia.set(null, null);
		BaseDeDatos.getInstancia();
	}
}
