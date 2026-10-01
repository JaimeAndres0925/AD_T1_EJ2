import java.io.File;
import java.util.Scanner;

public class FicheroConcordancia {

    static String nombrefichero;

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        System.out.print("Introduce el nombre del fichero de usuarios (ej. usuarios.txt): ");
        nombrefichero = teclado.nextLine().trim();
        do {
            //Validar nombre y fichero
            File fichero = new File(nombrefichero);
            if (!fichero.exists() || !nombrefichero.endsWith(".txt")) {
                System.out.print("Nombre no válido: el fichero no existe o no es un fichero de texto (.txt):");
                nombrefichero = teclado.nextLine().trim();
            } else if (fichero.length() > 10000) {
                System.out.println("Fichero muy largo de texto");
            } else {
                break;
            }
        } while (true);

        //Menu
        while (true) {
            System.out.println("========== MENÚ PRINCIPAL ==========");
            System.out.println("1. Añadir usuario");
            System.out.println("2. Mostrar usuarios introducidos");
            System.out.println("3. Generar fichero de concordancias");
            System.out.println("5. Salir");
            System.out.println("==============================");
            System.out.print("Seleccione una opción: ");

            String opcion = teclado.nextLine();

            switch (opcion) {
                case "1" -> FicheroService.anadirUsuario(nombrefichero);
                case "2" -> FicheroService.mostrarUsuarios(nombrefichero);
                case "3" -> FicheroService.generarConcordancias(nombrefichero);
                case "5" -> {
                    return;
                }
                default -> System.out.println("Opcion no valida");

            }
        }
    }
}
