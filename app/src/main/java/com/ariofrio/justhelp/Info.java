package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Info extends AppCompatActivity {
String nombre,aux;
TextView e_tipo,e_titulo,e_descripcion,e_correo,e_anunciante;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();

        String titulo =intent.getStringExtra("titulo");
       // String direccion = intent.getStringExtra("direccion");
        String tipo = intent.getStringExtra("tipo");
        String correo = intent.getStringExtra("correo");
        e_anunciante = findViewById(R.id.txtvAnunciante);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Anuncios")
                .whereEqualTo("titulo", titulo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String correoAnunciante = document.getString("correo"); // Obtener el correo del creador

                        if (correoAnunciante != null) {
                            db.collection("Usuarios")
                                    .whereEqualTo("correo", correoAnunciante)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                        if (!queryDocumentSnapshots2.isEmpty()) {
                                            DocumentSnapshot document2 = queryDocumentSnapshots2.getDocuments().get(0);
                                            String nombreAnunciante = document2.getString("nombre"); // Obtener el nombre

                                            if (nombreAnunciante != null) {
                                                e_anunciante.setText(nombreAnunciante); // Mostrar en TextView
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error al recuperar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al recuperar el anuncio: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );


        e_tipo = findViewById(R.id.texto_ofrece);
        e_tipo.setText(tipo);

        e_titulo = findViewById(R.id.titulo_anuncio);
        e_titulo.setText(titulo);

        e_correo = findViewById(R.id.correoInfo);
       //e_descripcion = findViewById(R.id.desctipcion_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}