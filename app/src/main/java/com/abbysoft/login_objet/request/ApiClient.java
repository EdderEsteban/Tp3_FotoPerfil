package com.abbysoft.login_objet.request;

import android.content.Context;
import android.widget.Toast;

import com.abbysoft.login_objet.model.Usuario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ApiClient {
    private static final String FILENAME = "usuario.dat";  // Nombre del archivo

    // Método para guardar el objeto Usuario
    public static void guardar(Context context, Usuario usuario) {
        try {
            // Abrir un FileOutputStream para escribir en el archivo
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Escribir el objeto Usuario en el archivo
            oos.writeObject(usuario);

            // Cerrar streams
            oos.close();
            fos.close();

        } catch (Exception e) {
            Toast.makeText(context, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Método para leer el objeto Usuario desde el archivo
    public static Usuario leer(Context context) {
        Usuario usuario = null;
        try {
            // Abrir un FileInputStream para leer el archivo
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Leer el objeto Usuario desde el archivo
            usuario = (Usuario) ois.readObject();

            // Cerrar streams
            ois.close();
            fis.close();

        } catch (Exception e) {
            Toast.makeText(context, "Error al leer el usuario", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return usuario;
    }

    // Método para validar el login
    public static boolean login(Context context, String mail, String password) {
        Usuario usuario = leer(context);  // Leer el usuario del archivo
        if (usuario != null) {
            // Verificar si los datos ingresados coinciden con los del usuario guardado
            return usuario.getMail().equals(mail) && usuario.getPassword().equals(password);
        }
        return false;
    }
}
