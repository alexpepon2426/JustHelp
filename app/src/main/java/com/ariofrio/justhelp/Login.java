package com.ariofrio.justhelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ariofrio.justhelp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText e_correo, e_contrasena;
    Button b_login;
    String s_correo, s_contrasena;
    Button s_goRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Inicialización de las vistas
        b_login = findViewById(R.id.boton);
        e_correo = findViewById(R.id.nombreLogin);
        e_contrasena = findViewById(R.id.passwdLogin);
        s_goRegister = findViewById(R.id.texto);

        // Verificar si las vistas son null
        if (b_login == null || e_correo == null || e_contrasena == null || s_goRegister == null) {
            Toast.makeText(this, "Error de inicialización de vistas", Toast.LENGTH_SHORT).show();
            return; // Terminar si alguna vista es null
        }

        // Listener para el botón de registro
        s_goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
            }
        });

        // Listener para el botón de login
        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener los valores de los EditText
                s_correo = e_correo.getText().toString();
                s_contrasena = e_contrasena.getText().toString();

                // Verificar que los campos no estén vacíos
                if (!s_correo.isEmpty() && !s_contrasena.isEmpty()) {
                    db.collection("Usuarios").document(s_correo)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // El documento existe, ahora verificar la contraseña
                                    String contrasenaBuena = documentSnapshot.getString("contrasena");
                                    if (contrasenaBuena != null && contrasenaBuena.equals(s_contrasena)) {
                                        // Contraseña correcta, guardar el correo en SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("correo", s_correo);
                                        editor.apply();

                                        // Intent para la siguiente actividad (MainActivity)
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        intent.putExtra("correo", s_correo);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Contraseña incorrecta
                                        Toast.makeText(Login.this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // El correo no está en la base de datos
                                    Toast.makeText(Login.this, "Este correo no está en la base de datos.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Manejar el error al intentar obtener los datos
                                Toast.makeText(Login.this, "Error. Vuelve a intentarlo en unos minutos... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Si los campos están vacíos
                    Toast.makeText(Login.this, "Por favor rellena los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ajuste del padding para las barras del sistema (si aplica)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
