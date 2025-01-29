package com.ariofrio.justhelp;
import android.content.Intent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText e_nombre, e_correo, e_prefijo, e_telefono, e_contrasena;
    Button b_registro;
    String s_nombre, s_correo, s_prefijo, s_telefono, s_contrasena;

private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);


        e_nombre = findViewById(R.id.nombre2);
        e_correo = findViewById(R.id.email2);
        e_prefijo = findViewById(R.id.prefijo2);
        e_telefono = findViewById(R.id.tlfn2);
        e_contrasena = findViewById(R.id.passwd2);
        b_registro = findViewById(R.id.botonRegister);

        b_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Comprobar que el correo no exista en BBDD

                s_nombre = e_nombre.getText().toString();

                s_correo = e_correo.getText().toString();


                s_prefijo = e_prefijo.getText().toString();
                s_telefono = e_telefono.getText().toString();
                s_contrasena = e_contrasena.getText().toString();

                if(s_correo.isEmpty()||s_nombre.isEmpty()||s_prefijo.isEmpty()||s_telefono.isEmpty()||s_contrasena.isEmpty()||!validaEmail(s_correo)){
                    Toast.makeText(Registro.this, "Rellena todos los campos.", Toast.LENGTH_SHORT).show();
                }else{
                    //comprueba que no exista el correo previamente
                    db.collection("Usuarios").document(s_correo)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // El documento ya existe
                                    Toast.makeText(Registro.this, "El usuario con este correo ya está registrado", Toast.LENGTH_SHORT).show();
                                } else {
// El documento no existe, puedes proceder a registrarlo
                                    registrarFirebase(s_nombre, s_correo, s_prefijo, s_telefono, s_contrasena);

                                }
                            })
                            .addOnFailureListener(e -> {
                                // Manejar el error al intentar comprobar la existencia
                                Toast.makeText(Registro.this, "Error. Vuelve a intentarlo en unos minutos... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                }


            }

            private void registrarFirebase(String nombre, String correo, String prefijo, String telefono, String contrasena) {
                Map<String, Object> usuario = new HashMap<>();
                //usuario.put("id", 12345); // Número entero
                usuario.put("nombre", s_nombre); // Cadena
                usuario.put("correo", s_correo); // Cadena
                usuario.put("prefijo", s_prefijo); // String
                usuario.put("telefono", s_telefono); // Número entero
                usuario.put("contrasena", s_contrasena); // Cadena (pero recuerda encriptar contraseñas en producción)
                //Posibilidad de subir la fecha de alta ***
                //usuario.put("edad", 30); // Ejemplo adicional de un campo numérico
                //usuario.put("activo", true); // Booleano, puede usarse para indicar si un usuario está activo




                db.collection("Usuarios").document(s_correo)
                        .set(usuario)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Registro.this, "Usuario agregado exitosamente!", Toast.LENGTH_SHORT).show();
                                //Log.d(TAG, "Usuario agregado exitosamente!");
                                Intent intent = new Intent(Registro.this, MainActivity.class);
                                intent.putExtra("correo",s_correo);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Registro.this, "Error al agregar usuario", Toast.LENGTH_SHORT).show();
                                //Log.w(TAG, "Error al agregar usuario", e);
                            }
                        });
            }


        });





        textView=findViewById(R.id.volver);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registro.this, Login.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }




    public static Boolean validaEmail (String email) {
        Pattern pattern = Pattern.compile("^([0-9a-zA-Z]+[-._+&])*[0-9a-zA-Z]+@([-0-9a-zA-Z]+[.])+[a-zA-Z]{2,6}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



    public void goMain(View view) {
        Intent intent = new Intent(Registro.this, MainActivity.class);
        startActivity(intent);
    }
}


//**********************

