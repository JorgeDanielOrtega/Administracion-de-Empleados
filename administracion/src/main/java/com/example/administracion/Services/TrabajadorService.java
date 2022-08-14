package com.example.administracion.Services;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.administracion.Models.Horario;
import com.example.administracion.Models.Persona;
import com.example.administracion.Models.Trabajador;
import com.example.administracion.Repositories.TrabajadorRepository;

@Service
public class TrabajadorService {

	@Autowired
	TrabajadorRepository trabajadorRepository;
	@Autowired
	DepartamentoService departamentoService;
	@Autowired
	PersonaService personaService;
	@Autowired
	RolService rolService;
	@Autowired
	HorarioService horarioService;
	@Autowired
	EntityManager entityManager;

	// TODO refactorizarlo los hasmap, y cambiarlos por list, utilizando las queries
	public Trabajador getTrabajorById(Long id){
		return trabajadorRepository.findById(id).get();
	}
	public Boolean eliminarTrabajadorPorId(Long id){
		try{
			this.trabajadorRepository.deleteById(id);
			return true;
		}catch (Exception err){
			return false;
		}
	}
	public ArrayList<Trabajador> getTrabajadores(Iterable<Long> ids) {
		return (ArrayList<Trabajador>) trabajadorRepository.findAllById(ids);
	}
	public ArrayList<Trabajador> obtenerTodosTrabajadores(){
		return (ArrayList<Trabajador>) trabajadorRepository.findAll();
	}
	public Trabajador getTrabajadorById(Long id) {
		return trabajadorRepository.findById(id).get();
	}

	public Map<String, Object> getDatosEmpleado(Persona persona, Trabajador trabajador) {
		Map<String, Object> myMap = new HashMap<>();

		myMap.put("id", trabajador.getId());
		myMap.put("nombres", persona.getNombres());
		myMap.put("apellidos", persona.getApellidos());
		myMap.put("direccion", persona.getDireccion());
		myMap.put("estado_civil", persona.getEstadoCivil());
		myMap.put("cedula", persona.getCedula());
		myMap.put("ciudad", persona.getCiudad());
		myMap.put("telefono", persona.getTelefono());
		myMap.put("sexo", String.valueOf(persona.getSexo()));
		myMap.put("fecha_nacimiento", persona.getFechaNacimiento().toString());
		myMap.put("anio_entrada", persona.getAnioEntrada().toString());
		myMap.put("correo_personal", trabajador.getCorreoPersonal());
		myMap.put("correo_empresarial", trabajador.getCorreoe());
		myMap.put("usuario", trabajador.getUsuario());
		myMap.put("contrasenia", trabajador.getPassword());
		myMap.put("id_departamento", trabajador.getIdDepartamento());
		myMap.put("id_rol", trabajador.getIdRol());
		myMap.put("id_horario", trabajador.getIdHorario());

		return myMap;
	}

	public Map<String, Object> getTrabajadorInfo(Long id) {
		Long idPersona = getTrabajadorById(id).getIdPersona();

		Persona persona = personaService.getPersonaById(idPersona);
		Trabajador trabajador = getTrabajadorById(id);
		return getDatosEmpleado(persona, trabajador);
	}

	public Horario getHorarioOfTrabajador(Long idHorario) {
		return horarioService.getHorarioById(idHorario);
	}

	private Map<String, Object> getDataEmpleadosByDepartamento(Persona persona, Trabajador trabajador) {
		Map<String, Object> myMap = new HashMap<>();

		// TODO hacer el campo puestos, quizas
		Long idRol = trabajador.getIdRol();
		Long idHorario = trabajador.getIdHorario();

		myMap.put("id", trabajador.getId());
		myMap.put("nombres", persona.getNombres());
		myMap.put("apellidos", persona.getApellidos());
		myMap.put("Rol", rolService.getRolById(idRol).getNombre());
		myMap.put("Horario", getHorarioOfTrabajador(idHorario).getTipo());

		return myMap;
	}

	private Map<String, Object> getDataEmpleadosForBusqueda(Persona persona, Trabajador trabajador) {
		Map<String, Object> myMap = new HashMap<>();

		// TODO hacer el campo puestos, quizas
		Long idRol = trabajador.getIdRol();
		Long idHorario = trabajador.getIdHorario();
		Long idDepartamento = trabajador.getIdDepartamento();

		myMap.put("id", persona.getId());
		myMap.put("nombres", persona.getNombres());
		myMap.put("apellidos", persona.getApellidos());
		myMap.put("departamento", departamentoService.getDepartamentoById(idDepartamento).getNombre());
		myMap.put("rol", rolService.getRolById(idRol).getNombre());
		myMap.put("horario", getHorarioOfTrabajador(idHorario).getTipo());

		return myMap;
	}

	private ArrayList<HashMap<String, Object>> getDataEmpleadosForBusqueda(List<Trabajador> trabajadorList) {
		ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();

		Long idPersona;
		Long idRol = null;
		Long idHorario = null;
		Long idDepartamento = null;
		Persona persona;

		// TODO hacer el campo puestos, quizas
		for (Trabajador trabajador : trabajadorList) {

			HashMap<String, Object> myMap = new HashMap<>();

			idPersona = trabajador.getIdPersona();
			idRol = trabajador.getIdRol();
			idHorario = trabajador.getIdHorario();
			idDepartamento = trabajador.getIdDepartamento();
			persona = personaService.getPersonaById(trabajador.getIdPersona());

			if (idRol != null && idDepartamento != null && idHorario != null) {

				myMap.put("id", idPersona);
				myMap.put("nombres", persona.getNombres());
				myMap.put("apellidos", persona.getApellidos());
				myMap.put("departamento", departamentoService.getDepartamentoById(idDepartamento).getNombre());
				myMap.put("rol", rolService.getRolById(idRol).getNombre());
				myMap.put("horario", getHorarioOfTrabajador(idHorario).getTipo());

				mapList.add(myMap);
			}
		}
		return mapList;
	}

	public ArrayList<Map<String, Object>> getTrabajadorForBusqueda() {
		List<Long> ids = new LinkedList<>();
		ArrayList<Persona> personaList = new ArrayList<Persona>();
		ArrayList<Map<String, Object>> trabajadorArrayListInfo = new ArrayList<>();
		ArrayList<Trabajador> trabajadorList = new ArrayList<>();

		for (Trabajador trabajador :  trabajadorRepository.findAll()) {
			if (trabajador.getId() != 1l) {
				trabajadorList.add(trabajador);
			}
		}

		trabajadorList.forEach(trabajador -> {
			Long idPersona = trabajador.getIdPersona();
			if	(idPersona != 1){
				ids.add(idPersona);
			}
		});
				
		personaList = personaService.getPersonas((Iterable<Long>) ids);

		for (int i = 0; i < trabajadorList.size(); i++) {
			trabajadorArrayListInfo.add(
					getDataEmpleadosForBusqueda(personaList.get(i), trabajadorList.get(i)));
		}
		return trabajadorArrayListInfo;
	}

	public ArrayList<Map<String, Object>> getTrabajadorByIdDepartamento(Long idDepartamento) {
		// TODO encontrar la manera de retornar el nombre del departamento

		List<Long> ids = new LinkedList<>();
		ArrayList<Persona> personaList = new ArrayList<Persona>();
		ArrayList<Map<String, Object>> trabajadorArrayListInfo = new ArrayList<>();
		ArrayList<Trabajador> trabajadorList = trabajadorRepository.findAllByIdDepartamento(idDepartamento);

		trabajadorList.forEach(trabajador -> {
			Long idPersona = trabajador.getIdPersona();
			ids.add(idPersona);
		});
		personaList = personaService.getPersonas((Iterable<Long>) ids);

		for (int i = 0; i < trabajadorList.size(); i++) {
			trabajadorArrayListInfo.add(
					getDataEmpleadosByDepartamento(personaList.get(i), trabajadorList.get(i)));
		}
		return trabajadorArrayListInfo;
	}

	private String formatearidPersonaList(Object[] idsPersona) {
		String result = "(";
		int size = idsPersona.length;
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				result += idsPersona[i] + ")";
				return result;
			}
			result += idsPersona[i] + ",";
		}
		return result + "0)";
	}

	public String getIdPersonaByNombresApellidos(String nombresApellidos) {
		String sql = "SELECT p.id FROM Persona p WHERE p.nombres LIKE '%" + nombresApellidos
				+ "%' OR p.apellidos LIKE '%" + nombresApellidos + "%'";

		Query query = entityManager.createQuery(sql);

		return formatearidPersonaList(query.getResultList().toArray());
	}

	private String generarQuery(Map<String, Object> fieldsMap) {
		Object[] keys = fieldsMap.keySet().toArray();
		int size = fieldsMap.size();
		String query = "SELECT t FROM Trabajador t WHERE ";
		for (int i = 0; i < size; i++) {
			String key = keys[i].toString();

			if (key.equals("t.idPersona") && fieldsMap.get(key) != "(0)") {
				query += key + " IN " + fieldsMap.get(key);
			} else if (i == size - 1) {
				query += key + "=" + fieldsMap.get(key).toString();
			} else {
				query += key + "=" + fieldsMap.get(key).toString() + " AND ";
			}
		}
		return query;
	}

	public ArrayList<Map<String, Object>> getTrabajadoresByFields(String departamento, String rol, String horario,
			Long id,
			String nombreApellido) {
		Map<String, Object> fieldsMap = new HashMap<>();

		if (departamento != null) {
			fieldsMap.put("t.idDepartamento", departamentoService.getIdDepartamentoByNombre(departamento));
		}
		if (rol != null) {
			fieldsMap.put("t.idRol", rolService.getIdRolByNombre(rol));
		}
		if (horario != null) {
			fieldsMap.put("t.idHorario", horarioService.getIdHorarioByTipo(horario));
		}
		if (nombreApellido != null) {
			fieldsMap.put("t.idPersona", getIdPersonaByNombresApellidos(nombreApellido));
		}
		if (id != null) {
			fieldsMap.put("t.id", id.toString());
		}
		Query query = entityManager.createQuery(generarQuery(fieldsMap));

		return getDataEmpleadosForBusqueda(query.getResultList());

	}
	public Trabajador guardarTrabajador(Trabajador trabajador){
		return trabajadorRepository.save(trabajador);
	}

}