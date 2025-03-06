package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private boolean filtroActivo = false;
    private Button boton_new;
    private static final int COLOR_ACTIVO = 0xFF42A5F5; // Azul para marcar el filtro
    private static final int COLOR_ORIGINAL = 0x297350; // Verde clásico de JUSTHELP
    private String usuario;
    private List<String> datalist = new ArrayList<>();
    private List<String> datalist2 = new ArrayList<>();
    private List<String> datalist3 = new ArrayList<>();
    private List<String> imagenes = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private MyAdapter adapter;
    private static final String SUPABASE_URL = "https://gpdsntyatqmierlzjqqk.supabase.co";
    private static final String BUCKET_NAME = "img_users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boton_new = findViewById(R.id.button_nuevas);
        Intent intent = getIntent();
        usuario = intent.getStringExtra("correo");
        searchView = findViewById(R.id.searchView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(datalist, datalist2, datalist3, imagenes);
        recyclerView.setAdapter(adapter);

        cargarTodosLosAnuncios();

        boton_new.setOnClickListener(view -> {
            if (filtroActivo) {
                cargarTodosLosAnuncios();
                boton_new.setBackgroundColor(COLOR_ORIGINAL);
            } else {
                cargarAnunciosRecientes();
                boton_new.setBackgroundColor(COLOR_ACTIVO);
            }
            filtroActivo = !filtroActivo;
        });

        // Configurar el SearchView
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goAnadir(View view) {
        Intent intent = new Intent(MainActivity.this, AniadirO.class);
        intent.putExtra("correo", usuario);
        startActivity(intent);
        finish();
    }

    public void goPerfil(View view) {
        Intent intent = new Intent(MainActivity.this, Perfil.class);
        intent.putExtra("correo", usuario);
        startActivity(intent);
        finish();
    }

    private void cargarTodosLosAnuncios() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();

        db.collection("Anuncios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    procesarDocumentos(queryDocumentSnapshots);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarAnunciosRecientes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();

        db.collection("Anuncios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    procesarDocumentos(queryDocumentSnapshots);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al recuperar los anuncios recientes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    procesarDocumentos(queryDocumentSnapshots);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al buscar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void limpiarListas() {
        datalist.clear();
        datalist2.clear();
        datalist3.clear();
        imagenes.clear();
    }

    private void procesarDocumentos(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
            Map<String, Object> anuncio = document.getData();
            if (anuncio != null) {
                datalist.add((String) anuncio.get("titulo"));
                datalist2.add((String) anuncio.get("direccion"));
                datalist3.add((String) anuncio.get("tipo"));

                String auxi = (String) anuncio.get("correo");
                String filename = auxi + ".jpg";
                String urlImagen = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;
                imagenes.add(urlImagen);
            }
        }
        adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
