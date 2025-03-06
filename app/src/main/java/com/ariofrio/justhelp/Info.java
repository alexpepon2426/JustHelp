package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Info extends AppCompatActivity {
String nombre,aux,correo;
TextView e_tipo,e_titulo,e_descripcion,e_correo,e_anunciante;
SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);
        Intent intent = getIntent();

        String titulo = intent.getStringExtra("titulo");
        // String direccion = intent.getStringExtra("direccion");
        String tipo = intent.getStringExtra("tipo");

        String usuario = intent.getStringExtra("usuario");
        e_anunciante = findViewById(R.id.txtvAnunciante);
        switchCompat = findViewById(R.id.boton_like);

        Toast.makeText(this, "correoA: " + correo, Toast.LENGTH_SHORT).show();

        e_descripcion = findViewById(R.id.desctipcion_info);
        e_correo = findViewById(R.id.correoInfo);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //ESTO PONE POR DEFECTO EN DESACTIVADO EL SWITCH CORAZÓN
        switchCompat.setThumbTintList(ContextCompat.getColorStateList(this, R.color.thumb_off));
        switchCompat.setTrackTintList(ContextCompat.getColorStateList(this, R.color.gray));

        //##################################################################################################
        //switchCompat.setChecked(); // Hay que coger el campo de la lista de favoritos! #############################
        //##################################################################################   P T E  ##############

        Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
        //Compruebo lista de anuncios del usuario
        db.collection("Usuarios").document(usuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoritos = (List<String>) documentSnapshot.get("listaFavoritos");
                        if (favoritos == null) {
                            favoritos = new ArrayList<>();
                        }

                        Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
                        String id = correo + titulo;
                        Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();
                        if (favoritos.contains(id)) {
                            switchCompat.setChecked(true);
                            switchCompat.setThumbTintList(ContextCompat.getColorStateList(this, R.color.red));
                        } else {
                            switchCompat.setChecked(false);
                        }
                    } else {
                        switchCompat.setChecked(false);
                        switchCompat.setThumbTintList(ContextCompat.getColorStateList(this, R.color.thumb_off));
                    }

                    /*try {
                        // Agregar un nuevo valor a la lista
                        favoritos.add("Nuevo valor");
                    }catch(Exception e){
                        log
                    }

                    // Ahora, actualizamos el documento con la lista modificada
                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("lista_cadenas", listaVacía);*/

                })
                .addOnFailureListener(e -> {
                    // Si ocurre algún error al obtener los datos
                    Toast.makeText(this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    switchCompat.setChecked(false); // Establecer el Switch en el estado apagado
                });

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
                                            String descripcion = document.getString("descripcion");
                                            String correos = document.getString("correo");
                                            correo = document.getString("correo");

                                            if (nombreAnunciante != null) {
                                                e_anunciante.setText(nombreAnunciante); // Mostrar en TextView
                                                e_descripcion.setText(descripcion);
                                                e_correo.setText(correos);
                                            } else {
                                                Toast.makeText(this, "Error al recuperar el nombre del anunciante", Toast.LENGTH_SHORT).show();
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

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String id = correo + titulo;
            db.collection("Usuarios").document(usuario)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Obtenemos la lista de favoritos actual
                            List<String> favoritos = (List<String>) documentSnapshot.get("listaFavoritos");
                            if (favoritos == null) {
                                favoritos = new ArrayList<>();
                            }

                            if (isChecked) {
                                if (!favoritos.contains(id)) {
                                    favoritos.add(id);
                                }
                                switchCompat.setThumbTintList(ContextCompat.getColorStateList(this, R.color.red));
                                //switchCompat.setTrackTintList(ContextCompat.getColorStateList(this, R.color.red));
                            } else {
                                favoritos.remove(id);
                                switchCompat.setThumbTintList(ContextCompat.getColorStateList(this, R.color.thumb_off));
                            }
                            Map<String, Object> updatedData = new HashMap<>();
                            updatedData.put("listaFavoritos", favoritos);
                            db.collection("Usuarios").document(usuario)
                                    .update(updatedData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Operación exitosa, si es necesario, puedes hacer algo aquí
                                        Toast.makeText(this, "Lista de favoritos actualizada", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Si ocurre algún error al actualizar los datos
                                        Toast.makeText(this, "Error al actualizar lista de favoritos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }

                    })
                    .addOnFailureListener(e -> {
                        // Si ocurre algún error al obtener el documento del usuario
                        Toast.makeText(this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            e_correo = findViewById(R.id.correoInfo);
            //e_descripcion = findViewById(R.id.desctipcion_info);



            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });


        });
    }

    public void goMain(View view) {
        finish();
    }
}