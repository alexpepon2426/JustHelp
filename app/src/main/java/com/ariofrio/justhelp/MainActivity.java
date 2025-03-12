package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private String usuario;
    private List<String> datalist = new ArrayList<>();
    private List<String> datalist2 = new ArrayList<>();
    private List<String> datalist3 = new ArrayList<>();
    private List<String> imagenes = new ArrayList<>();
    private List<String> correoA = new ArrayList<>();
    private Set<String> anunciosVistos = new HashSet<>();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private MyAdapter adapter;
    private boolean filtroActivo = false;
    private boolean filtroActivo2 = false;
    private Button boton_new, boton_fav;

    private static final String SUPABASE_URL = "https://gpdsntyatqmierlzjqqk.supabase.co";
    private static final String BUCKET_NAME = "img_users";
    private static final int COLOR_ACTIVO = 0xFFD99771;
    private static final int COLOR_ORIGINAL = 0x297350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boton_new = findViewById(R.id.button_nuevas);
        boton_fav = findViewById(R.id.button_favoritas);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);

        usuario = getIntent().getStringExtra("correo");
        adapter = new MyAdapter(datalist, datalist2, datalist3, imagenes, usuario, correoA);
        recyclerView.setAdapter(adapter);

        cargarTodosLosAnuncios();

        boton_new.setOnClickListener(view -> {
            if (filtroActivo) {
                cargarTodosLosAnuncios();
            } else {
                resetBotones();
                cargarAnunciosRecientes();
            }
        });

        boton_fav.setOnClickListener(view -> {
            if (filtroActivo2) {
                cargarTodosLosAnuncios();
            } else {
                resetBotones();
                cargarAnunciosFavoritos();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarEnFirestore(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarEnFirestore(newText);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (datalist.isEmpty()) {
            cargarTodosLosAnuncios();
        }
    }

    private void cargarTodosLosAnuncios() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();
        resetBotones();

        db.collection("Anuncios")
                .get()
                .addOnSuccessListener(this::procesarDocumentos)
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, usuario, correoA);
        recyclerView.setAdapter(adapter);

        // **Actualizar el adaptador después de modificar las listas**
        adapter.notifyDataSetChanged();
    }

    private void cargarAnunciosRecientes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();
        boton_new.setBackgroundColor(COLOR_ACTIVO);
        filtroActivo = true;

        db.collection("Anuncios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(this::procesarDocumentos)
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Error al recuperar los anuncios recientes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void cargarAnunciosFavoritos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();
        boton_fav.setBackgroundColor(COLOR_ACTIVO);
        filtroActivo2 = true;

        db.collection("Usuarios").document(usuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> listaFavoritos = (List<String>) documentSnapshot.get("listaFavoritos");
                        if (listaFavoritos != null && !listaFavoritos.isEmpty()) {
                            db.collection("Anuncios")
                                    .whereIn("id", listaFavoritos)
                                    .get()
                                    .addOnSuccessListener(this::procesarDocumentos);
                        } else {
                            Toast.makeText(this, "No tienes anuncios favoritos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void buscarEnFirestore(String texto) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();

        if (texto.isEmpty()) {
            cargarTodosLosAnuncios();
            return;
        }

        db.collection("Anuncios")
                .whereGreaterThanOrEqualTo("titulo", texto)
                .whereLessThanOrEqualTo("titulo", texto + "\uf8ff")
                .get()
                .addOnSuccessListener(this::procesarDocumentos);
    }

    private void limpiarListas() {
        datalist.clear();
        datalist2.clear();
        datalist3.clear();
        imagenes.clear();
        correoA.clear();
        anunciosVistos.clear();
    }

    private void procesarDocumentos(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
            Map<String, Object> anuncio = document.getData();
            if (anuncio != null) {
                String titulo = (String) anuncio.get("titulo");
                if (!anunciosVistos.contains(titulo)) {
                    anunciosVistos.add(titulo);
                    datalist.add(titulo);
                    datalist2.add((String) anuncio.get("direccion"));
                    datalist3.add((String) anuncio.get("tipo"));
                    String correo = (String) anuncio.get("correo");
                    correoA.add(correo);
                    imagenes.add(SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + correo + ".jpg");
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void resetBotones() {
        filtroActivo = false;
        filtroActivo2 = false;
        boton_fav.setBackgroundColor(COLOR_ORIGINAL);
        boton_new.setBackgroundColor(COLOR_ORIGINAL);
    }
}
