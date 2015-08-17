/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.uniandes.edu.ecos.plataforma;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author Camilo Marroquín
 */
public class Mapper {

    public void businessMethod() {
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    /**
     * <B>Copia un objeto y todas sus relaciones</B>
     *
     * @param claseDestino clase destino a partir de la cual se creara una
     * instancia y se copiaran sus valores, ejemplo TestDTO.class
     * @param origenInstanciado Bean origen instanciado, si es null, la
     * respuesta a este metodo es null
     */
    public static <T extends Object> T copyCompleto(Object origenInstanciado, Class<T> claseDestino, boolean copiaRecursivaCollections) {

        T destinoInstanciado = null;
        try {
            destinoInstanciado = claseDestino.newInstance();
        } catch (Exception e) {
            return null;
        }

        HashMap<String, Method> camposOrigen = buscarMetodosOrigenFiltrado(origenInstanciado.getClass(), destinoInstanciado.getClass(), "get;is");
        HashMap<String, Method> camposDestino = buscarMetodos(destinoInstanciado.getClass(), "set");

        Set llavesCamposOrigen = camposOrigen.keySet();
        for (Iterator<String> it = llavesCamposOrigen.iterator(); it.hasNext();) {
            String nombreMetodoOrigen = it.next();
            Method metodoOrigen = camposOrigen.get(nombreMetodoOrigen);
            Object[] valorMetodoOrigen = new Object[1];
            try {
                valorMetodoOrigen[0] = metodoOrigen.invoke(origenInstanciado, new Object[0]);
            } catch (Exception e) {
                // Si no logro ejecutar el getter no lo puedo mapear
                continue;
            }

            Method metodoDestino = camposDestino.get(nombreMetodoOrigen.replaceFirst("get", "set"));
            if (metodoDestino == null) {
                // No existe el setter en el destino que corresponda al getter del origen
                metodoDestino = camposDestino.get(nombreMetodoOrigen.replaceFirst("is", "set"));
                if (metodoDestino == null) {
                    continue;
                }
            }
            if (metodoDestino.getParameterTypes().length != 1) {
                // es un setter pero recibe mas de un parametro entonces no lo puedo invocar
                continue;
            }
            if (!metodoDestino.getReturnType().equals(Void.TYPE)) {
                // es un setter que retorna datos, por lo cual no es un setter normal y no se mapea
                continue;
            }

            if ((metodoOrigen.getReturnType().equals(Collection.class)
                    || metodoOrigen.getReturnType().equals(List.class)) && copiaRecursivaCollections) {
                
                List listaDestino = new ArrayList();
                Collection listaOrigen = (Collection) valorMetodoOrigen[0];
                try {
                    if (listaOrigen != null) {
                        for (Object obj : listaOrigen) {
                            ParameterizedType pt = (ParameterizedType) metodoDestino.getGenericParameterTypes()[0];
                            String clase = pt.getActualTypeArguments()[0].toString().replaceFirst("class ", "");
                            Object cd = copyCompleto(obj, Class.forName(clase), false);
                            listaDestino.add(cd);
                        }
                    }
                    if (listaDestino.isEmpty()) {
                        listaDestino = null;
                    }
                    metodoDestino.invoke(destinoInstanciado, listaDestino);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (!metodoOrigen.getReturnType().equals(Collection.class) && !metodoOrigen.getReturnType().equals(List.class)) {
                if (metodoOrigen.getReturnType().equals(metodoDestino.getParameterTypes()[0])) {
                    try {
                        metodoDestino.invoke(destinoInstanciado, valorMetodoOrigen);
                    } catch (Exception ex) {
                        //UtilidadesLog.hacerLog("Error al copiar:" + valorMetodoOrigen[0]);
                    }
                } else {
                    //Si los campos no coinciden en tipo
                    Object destinoRelacion = null;
                    if (valorMetodoOrigen[0] != null) {

                        // ahora copio recursivamente los datos de las dos clases
                        destinoRelacion = copyCompleto(valorMetodoOrigen[0], metodoDestino.getParameterTypes()[0], false);
                    }
                    Object[] parametros = new Object[1];
                    parametros[0] = destinoRelacion;
                    try {
                        metodoDestino.invoke(destinoInstanciado, parametros);
                    } catch (Exception e) {
                        // Si no lo puedo invocar continuo mapeando el siguiente
                        continue;
                    }
                }

            }

        }
        return destinoInstanciado;
    }

    /**
     * Busca los metodos que inician por la cadena indicada
     *
     * @param clase Clase donde se va a buscar
     * @param cadenas Cadena inicial del los metodos a buscar separados por ;
     * @return Metodos dentro de un hashmap cuya llave es el nombre del metodo
     */
    private static HashMap<String, Method> buscarMetodos(Class clase, String cadenas) {
        HashMap<String, Method> respuesta = new HashMap<String, Method>();

        Method[] todos = clase.getMethods();
        String[] inicios = cadenas.split(";");

        int cantidadMetodos = todos.length;
        int cantidadInicios = inicios.length;

        for (int i = 0; i < cantidadMetodos; i++) {
            if (todos[i].getName().equals("getClass")) {
                continue;
            }
            for (int j = 0; j < cantidadInicios; j++) {
                if (todos[i].getName().startsWith(inicios[j])) {
                    respuesta.put(todos[i].getName(), todos[i]);
                }
            }
        }
        return respuesta;
    }

    private static HashMap<String, Method> buscarMetodosOrigenFiltrado(Class claseOrigen, Class claseDestino, String cadenas) {
        HashMap<String, Method> hashDestino = new HashMap<String, Method>();

        Method[] todosDestino = claseDestino.getMethods();
        String[] iniciosDestino = cadenas.split(";");

        int cantidadMetodosDestino = todosDestino.length;
        int cantidadIniciosDestino = iniciosDestino.length;

        for (int i = 0; i < cantidadMetodosDestino; i++) {
            if (todosDestino[i].getName().equals("getClass")) {
                continue;
            }
            for (int j = 0; j < cantidadIniciosDestino; j++) {
                if (todosDestino[i].getName().startsWith(iniciosDestino[j])) {
                    hashDestino.put(todosDestino[i].getName(), todosDestino[i]);
                }
            }
        }

        HashMap<String, Method> respuesta = new HashMap<String, Method>();

        Method[] todos = claseOrigen.getMethods();
        String[] inicios = cadenas.split(";");

        int cantidadMetodos = todos.length;
        int cantidadInicios = inicios.length;

        for (int i = 0; i < cantidadMetodos; i++) {
            if (todos[i].getName().equals("getClass")) {
                continue;
            }
            for (int j = 0; j < cantidadInicios; j++) {
                if (todos[i].getName().startsWith(inicios[j]) && (hashDestino.get(todos[i].getName()) != null)) {
                    respuesta.put(todos[i].getName(), todos[i]);
                }
            }
        }
        return respuesta;

    }
}



