
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class FicheroService {
    public String nombrefichero;
    public FicheroService() {

    }
    public FicheroService(String nombrefichero) {
        this.nombrefichero = nombrefichero;
    }
    //Escribir los usuarios por consola
    public static void mostrarUsuarios(String nombrefichero) {
        try {
            BufferedReader lector = new BufferedReader(new FileReader(nombrefichero));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }
            lector.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Método para a partir de un usuario y un id añadir al usuario nuevo
    public static void anadirUsuario(String nombrefichero) {
        Scanner teclado = new Scanner(System.in);
        List<String> codigosExistentes = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(nombrefichero))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split("\\s+");
                //Separamos la línea en partes
                //la primera parte son ids y las añadimos a códigos
                if (partes.length > 0) {
                    codigosExistentes.add(partes[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el fichero." + nombrefichero + e.getMessage());
        }
        //Código de usuario sugerido
        String sugerido = sugerirNuevoCodigo(nombrefichero);
        System.out.println("Sugerencia de código: " + sugerido);
        System.out.print("Introduce código de usuario (o pulsa Enter para usar el sugerido): ");
        String codigo = teclado.nextLine().trim();

        //Para colocar el método sugerido directamente
        if (codigo.isEmpty()) {
            codigo = sugerido;
        }

        //Si el UID no es valido se vuelve a pedir
        while (!codigo.matches("U\\d{3}")) {
            System.out.print("Error: Ese formato de Id no es valido: ");
            codigo = teclado.nextLine().trim();
        }

        //Si el código existe se debe volver a escribir
        while (codigosExistentes.contains(codigo)) {
            System.out.print("Error: El código " + codigo + " existe, escribe otro.");
            codigo = teclado.nextLine().trim();
        }

        System.out.print("Introduce aficiones separadas por espacios: ");
        String aficiones = teclado.nextLine().trim();

        //Si no hay aficiones las volvemos a pedir
        while (aficiones.isEmpty()) {
            System.out.print(" Error: Debe tener al menos una afición.");
            aficiones = teclado.nextLine().trim().toUpperCase();
        }

        //Validar si las aficiones no estan repetidas
        while (hayNombresRepetidos(aficiones)) {
            System.out.print("Error: Has introducido un nombre de aficion mas de una vez, Vuelve a escribir las aficiones.");
            aficiones = teclado.nextLine().trim();
        }

        //Pintamos las aficiones y el id del usuario
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombrefichero, true))) {
            bw.write(codigo + " " + aficiones);
            bw.newLine();
            System.out.println(" Usuario " + codigo + " añadido correctamente.");
        } catch (IOException e) {
            System.out.println(" Error al escribir en el fichero " + nombrefichero + e.getMessage());
        }
    }

    //Método para generar las parejas con mismas aficiones
    public static void generarConcordancias(String nombrefichero) {
        Scanner teclado = new Scanner(System.in);
        System.out.print("Introduce el número mínimo de aficiones comunes que deseas: ");
        int minimo = teclado.nextInt();

        //No puede haber menos de una afición común
        while (minimo < 1) {
            System.out.println("Debe ser al menos 1.");
            minimo = teclado.nextInt();
        }

        List<String[]> usuarios = new ArrayList<>();

        //Añadimos las partes a la lista de usuarios
        try (BufferedReader lector = new BufferedReader(new FileReader(nombrefichero))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.trim().split("\\s+");
                usuarios.add(partes);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el fichero.");
            return;
        }

        //Guardamos los usuarios en 2 listas diferentes y vamos comparandolos 1 a 1
        //Si hay 2 con las mismas aficiones y no se esta en la lista de comunes
        //Se añaden a la lista

        List<String> parejas = new ArrayList<>();

        for (int i = 0; i < usuarios.size(); i++) {
            for (int j = i + 1; j < usuarios.size(); j++) {
                String[] u1 = usuarios.get(i);
                String[] u2 = usuarios.get(j);

                List<String> comunes = new ArrayList<>();
                for (int k = 1; k < u1.length; k++) {
                    for (int l = 1; l < u2.length; l++) {
                        if (u1[k].equals(u2[l]) && !comunes.contains(u1[k])) {
                            comunes.add(u1[k]);
                        }
                    }
                }
                //Escribimos las parejas
                if (comunes.size() >= minimo) {
                    String pareja = u1[0] + " - " + u2[0] + " (" + String.join(", ", comunes) + ")";
                    parejas.add(pareja);
                }
            }
        }


        //Ordenar parejas
        parejas.sort(Comparator.naturalOrder());

        if (parejas.isEmpty()) {
            System.out.println("No hay ninguna parejas con al menos " + minimo + " aficiones comunes.");
            return;
        }

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("concordancias.txt"))) {
            for (String pareja : parejas) {
                escritor.write(pareja);
                escritor.newLine();
            }
            System.out.println("Fichero 'concordancias.txt' creado con " + parejas.size() + " parejas, selecciona salir para ver el resultado");
        } catch (IOException e) {
            System.out.println("Error al crear el fichero de concordancias.");
        }
    }

    //Método para comprobar si hay nombres repetidos
    public static boolean hayNombresRepetidos(String aficiones) {
        String[] lista = aficiones.trim().split("\\s+");

        for (int i = 0; i < lista.length; i++) {
            for (int j = i + 1; j < lista.length; j++) {
                if (lista[i].equals(lista[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    //Método para sugerir nuevo código
    public static String sugerirNuevoCodigo(String nombrefichero) {
        int maxId = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(nombrefichero))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.trim().split("\\s+");
                if (partes.length > 0 && partes[0].startsWith("U")) {
                    try {
                        //Recogemos el id y si es el más grande lo guardamos para luego sugerir
                        int id = Integer.parseInt(partes[0].substring(1));
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar códigos que no sigan el formato U###
                    }
                }
            }
        } catch (IOException e) {
            // Si el fichero no existe, simplemente sugerimos U100
        }
        return "U" + (maxId + 1);

    }
}

