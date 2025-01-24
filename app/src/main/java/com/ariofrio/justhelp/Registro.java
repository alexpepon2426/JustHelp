package com.ariofrio.justhelp;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Registro extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("id", 12345); // Número entero
        usuario.put("nombre", "Juan Pérez"); // Cadena
        usuario.put("correo", "juan.perez@example.com"); // Cadena
        usuario.put("telefono", 5551234); // Número entero
        usuario.put("contraseña", "password123"); // Cadena (pero recuerda encriptar contraseñas en producción)
        usuario.put("edad", 30); // Ejemplo adicional de un campo numérico
        usuario.put("activo", true); // Booleano, puede usarse para indicar si un usuario está activo

        db.collection("Usuarios").document("12345")
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Registro.this, "Usuario agregado exitosamente!", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "Usuario agregado exitosamente!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registro.this, "Error al agregar usuario", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error al agregar usuario", e);
                    }
                });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}