package test;

import java.util.ArrayList;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

public class EscenarioBDSinDatos {
	private BaseDeDatos bd;
	private ArrayList<AsociadoDTO> asociados = new ArrayList<AsociadoDTO>();
	public EscenarioBDSinDatos() {
		DataAccessObject DAO = new DataAccessObject();
		this.bd = BaseDeDatos.getInstancia();
	}
	
	public BaseDeDatos getBd() {
		return bd;
	}

	public ArrayList<AsociadoDTO> getAsociados() {
		return asociados;
	}
}
