package com.abbysoft.login_objet.ui.registro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abbysoft.login_objet.databinding.ActivityRegistroBinding;
import com.abbysoft.login_objet.model.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegistroActivity extends AppCompatActivity {
    private ActivityRegistroBinding binding;
    private RegistroActivityViewModel vmR;
    private static final int REQUEST_IMAGE_CAPTURE = 1;  // Código de solicitud para tomar foto
    private Bitmap imageBitmap;  // Almacena la foto tomada

    // Lanzador de actividad para la cámara (Reemplaza a "startActivityForResult" q esta deprecado
    private final ActivityResultLauncher<Intent> tomarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Obtenemos el bitmap de la cámara
                    Bundle extras = result.getData().getExtras();
                    imageBitmap = (Bitmap) extras.get("data");

                    // Mostramos la imagen en el ImageView
                    binding.ivAvatar.setImageBitmap(imageBitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        vmR = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroActivityViewModel.class);

        setContentView(binding.getRoot());

        // Observamos los datos del usuario
        vmR.mUsuario().observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                // Cargar los datos del usuario en los EditText
                binding.etDni.setText(String.valueOf(usuario.getDni()));
                binding.etApellido.setText(usuario.getApellido());
                binding.etNombre.setText(usuario.getNombre());
                binding.etMail.setText(usuario.getMail());
                binding.etPass.setText(usuario.getPassword());

                // Si el usuario tiene una imagen de avatar guardada, la mostramos
                if (usuario.getAvatarUri() != null && !usuario.getAvatarUri().isEmpty()) {
                    // Cargar la imagen desde la URI y mostrarla en el ImageView
                    Uri avatarUri = Uri.parse(usuario.getAvatarUri());
                    binding.ivAvatar.setImageURI(avatarUri);  // Mostrar la imagen guardada
                }
            }
        });

        Intent intent = getIntent();
        String origen = intent.getStringExtra("origen");
        vmR.getUsuario(origen);

        // Acción del botón para abrir la cámara
        binding.btnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamara();
            }
        });

        // Botón para guardar el usuario y la foto
        binding.btnSaveAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dni = Long.parseLong(binding.etDni.getText().toString());
                String apellido = binding.etApellido.getText().toString();
                String nombre = binding.etNombre.getText().toString();
                String mail = binding.etMail.getText().toString();
                String password = binding.etPass.getText().toString();

                String avatarUriString = "";
                if (imageBitmap != null) {
                    // Guardar la imagen en el almacenamiento interno
                    avatarUriString = guardarImagen(imageBitmap);
                }

                // Guardar el usuario junto con la ruta de la imagen
                vmR.crear(dni, apellido, nombre, mail, password, avatarUriString);
            }
        });
    }

    // Método para abrir la cámara
    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Lanzar la cámara con el nuevo método
            tomarFotoLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara.", Toast.LENGTH_SHORT).show();
        }
    }

    // Obtener el resultado de la cámara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // Obtenemos el bitmap de la cámara
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            // Mostramos la imagen en el ImageView
            binding.ivAvatar.setImageBitmap(imageBitmap);
        }
    }

    // Método para guardar la imagen en el almacenamiento interno
    private String guardarImagen(Bitmap bitmap) {
        String filename = "avatar_" + System.currentTimeMillis() + ".png";  // Nombre único para el archivo
        File file = new File(getFilesDir(), filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);  // Guardamos la imagen como PNG
            fos.flush();
            return Uri.fromFile(file).toString();  // Retornamos la URI de la imagen guardada
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
}
