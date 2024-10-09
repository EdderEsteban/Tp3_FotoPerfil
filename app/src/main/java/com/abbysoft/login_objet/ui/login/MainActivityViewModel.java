package com.abbysoft.login_objet.ui.login;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.abbysoft.login_objet.request.ApiClient;
import com.abbysoft.login_objet.ui.registro.RegistroActivity;


public class MainActivityViewModel extends AndroidViewModel {

    private Context context;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();

    }

    public void loginIn(String mail, String password) {
        if (mail.equals("")) {
            Toast.makeText(context, "Ingrese Usuario", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(context, "Ingrese Password", Toast.LENGTH_SHORT).show();
        } else {
            boolean log = ApiClient.login(context, mail, password);
            if (log) {
                Intent intent = new Intent(context, RegistroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("origen", "login");
                context.startActivity(intent);
                Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Usuario o Password Incorrecto", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void goRegistro () {

        Intent intent = new Intent(context, RegistroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("origen", "registro");
        context.startActivity(intent);
    }


}