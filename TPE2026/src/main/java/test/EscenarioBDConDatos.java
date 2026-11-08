package test;

import java.sql.SQLException;
import java.util.ArrayList;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

public class EscenarioBDConDatos {
	private BaseDeDatos bd;
	private ArrayList<AsociadoDTO> asociados = new ArrayList<AsociadoDTO>();
	public EscenarioBDConDatos() {
		DataAccessObject DAO = new DataAccessObject();
		this.bd = BaseDeDatos.getInstancia();
		try {
			//El comportamiento esperado es que estos datos sean borrados por IniciarBD()
		    Asociado asociado1 = new Asociado(
		        "Rodrigo", "Bacidalupe", "11111111", "123123123",
		        new Domicilio("Avenida Siempreviva", 123, "Mar del Plata")
		    );
		    AsociadoDTO DTOasociado1 = DAO.CrearDTO(asociado1);
		    DAO.guardar(asociado1);
		    this.asociados.add(DTOasociado1);


		    Asociado asociado2 = new Asociado(
		        "Juan", "Perez", "11111112", "123123124",
		        new Domicilio("Avenida Siempreviva", 124, "Mar del Plata")
		    );
		    AsociadoDTO DTOasociado2 = DAO.CrearDTO(asociado2);
		    DAO.guardar(asociado2);
		    this.asociados.add(DTOasociado2);

		    Asociado asociado3 = new Asociado(
		        "Maria", "Gomez", "11111113", "123123125",
		        new Domicilio("Independencia", 456, "Mar del Plata")
		    );
		    AsociadoDTO DTOasociado3 = DAO.CrearDTO(asociado3);
		    DAO.guardar(asociado3);
		    this.asociados.add(DTOasociado3);

		    Asociado asociado4 = new Asociado(
		        "Lucas", "Fernandez", "11111114", "123123126",
		        new Domicilio("Constitucion", 789, "Buenos Aires")
		    );
		    AsociadoDTO DTOasociado4 = DAO.CrearDTO(asociado4);
		    DAO.guardar(asociado4);
		    this.asociados.add(DTOasociado4);

		    Asociado asociado5 = new Asociado(
		        "Sofia", "Martinez", "11111115", "123123127",
		        new Domicilio("Colon", 321, "Santa Clara")
		    );
		    AsociadoDTO DTOasociado5 = DAO.CrearDTO(asociado5);
		    DAO.guardar(asociado5);
		    this.asociados.add(DTOasociado5);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}



	public BaseDeDatos getBd() {
		return bd;
	}

	public ArrayList<AsociadoDTO> getAsociados() {
		return asociados;
	}
	
}