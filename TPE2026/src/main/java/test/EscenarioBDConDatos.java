package test;

import java.sql.SQLException;
import java.util.ArrayList;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

public class EscenarioBDConDatos {
	private BaseDeDatos bd;
	public EscenarioBDConDatos() {
		DataAccessObject DAO = new DataAccessObject();
		this.bd = BaseDeDatos.getInstancia();
		try {
		    DAO.guardar(new Asociado(
		        "Rodrigo", "Bacidalupe", "11111111", "123123123",
		        new Domicilio("Avenida Siempreviva", 123, "Mar del Plata")
		    ));


		    Asociado asociado2 = new Asociado(
		        "Juan", "Perez", "11111112", "123123124",
		        new Domicilio("Avenida Siempreviva", 124, "Mar del Plata")
		    );
		    DAO.guardar(asociado2);

		    Asociado asociado3 = new Asociado(
		        "Maria", "Gomez", "11111113", "123123125",
		        new Domicilio("Independencia", 456, "Mar del Plata")
		    );
		    DAO.guardar(asociado3);

		    Asociado asociado4 = new Asociado(
		        "Lucas", "Fernandez", "11111114", "123123126",
		        new Domicilio("Constitucion", 789, "Buenos Aires")
		    );
		    DAO.guardar(asociado4);

		    Asociado asociado5 = new Asociado(
		        "Sofia", "Martinez", "11111115", "123123127",
		        new Domicilio("Colon", 321, "Santa Clara")
		    );
		    DAO.guardar(asociado5);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}



	public BaseDeDatos getBd() {
		return bd;
	}
	
}
