package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String usuario,nombre;
    List<String> datalist = new ArrayList<>();
    List<String> datalist2= new ArrayList<>();
    List<String> datalist3= new ArrayList<>();
    List<String> imagenes=new ArrayList<>();
    List<String> correoA = new ArrayList<>();
    String auxi;
    MyAdapter adapter;
    TextView tipoColor;
    RecyclerView recyclerView;
    private boolean filtroActivo = false;
    private boolean filtroActivo2 = false;
    private Button boton_new, boton_fav;
    private static final int COLOR_ACTIVO = 0xFF42A5F5; //azul para marcar el filtro
    private static final int COLOR_ORIGINAL = 0x297350; //vuelta al verde clásico identidad de JUSTHELP

    private static final String SUPABASE_URL = "https://gpdsntyatqmierlzjqqk.supabase.co";
    private static final String BUCKET_NAME = "img_users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boton_new = findViewById(R.id.button_nuevas);
        boton_fav = findViewById(R.id.button_favoritas);
        Intent intent = getIntent();
        usuario = intent.getStringExtra("correo");

     //   Toast.makeText(this, "usuario : "+usuario, Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, "bienvenido" + usuario, Toast.LENGTH_SHORT).show(); //Esto podría configurarse para que solo se haga desde Login

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db.collection("Anuncios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Lista para almacenar los anuncios recuperados
                        List<Map<String, Object>> listaAnuncios = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> anuncio = document.getData();

                            // Mostrar en consola los datos de cada anuncio (puedes usar los valores según necesites)
                            if (anuncio != null) {
                                auxi=(String) anuncio.get("titulo");
                                datalist.add(auxi);
                                auxi=(String) anuncio.get("direccion");
                                datalist2.add(auxi);
                                auxi=(String) anuncio.get("tipo");
                                datalist3.add(auxi);

                                auxi=(String) anuncio.get("correo");
                                String filename =auxi + ".jpg";
                                //Toast.makeText(this, "auxi: "+auxi, Toast.LENGTH_SHORT).show();
                                correoA.add(auxi);
                                //Toast.makeText(this, "correoA: "+correoA, Toast.LENGTH_SHORT).show();
                                String urlImagen = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;
                                imagenes.add(urlImagen);





                                /*datalist.add(document.getString("titulo"));
                                datalist2.add(document.getString("direccion"));
                                datalist3.add(document.getString("tipo"));*/
                                listaAnuncios.add(anuncio);
                            }
                            adapter.notifyDataSetChanged(); //Esto va actualizando los datos del adaptador según cambien
                        }

                        //Toast.makeText(AniadirO.this, "Anuncios recuperados exitosamente!", Toast.LENGTH_SHORT).show();

                        // Aquí podrías realizar más operaciones con la lista de anuncios si es necesario.
                    } else {
                        Toast.makeText(MainActivity.this, "No hay anuncios almacenados.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        db.collection("Usuarios").document(usuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nombre = documentSnapshot.getString("nombre");
                    }

                });

/* Para cardviews de ejemplo.
        List<String> datalist = Arrays.asList("Elemento1","Elemento2","Elemento3","Elemento4","Elemento5","Elemento1","Elemento2","Elemento3","Elemento4","Elemento5");
        List<String> datalist2 = Arrays.asList("DIRnto1","DIRo2","DIR3","DIR4","DIR5","DIRnto1","DIRo2","DIR3","DIR4","DIR5");
        List<String> datalist3 = Arrays.asList("Ofrece","NEcesito","Ofrece","sdad","zasfasf","Ofrece","NEcesito","Ofrece","sdad","zasfasf");
*/
        adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, usuario, correoA);

        recyclerView.setAdapter(adapter);

        cargarTodosLosAnuncios();

        boton_new.setOnClickListener(view -> {
            if (filtroActivo) {
                cargarTodosLosAnuncios();

            } else {
                cargarAnunciosRecientes();

            }
            filtroActivo = !filtroActivo;
        });

        boton_fav.setOnClickListener(view -> {
            if (filtroActivo2) {
                cargarTodosLosAnuncios();

            } else {
                cargarAnunciosFavoritos();

            }
            filtroActivo2 = !filtroActivo2;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onResume(){
        super.onResume();
        cargarTodosLosAnuncios();
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
        boton_fav.setBackgroundColor(COLOR_ORIGINAL);
        boton_new.setBackgroundColor(COLOR_ORIGINAL);

        db.collection("Anuncios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    procesarDocumentos(queryDocumentSnapshots);
                    adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, usuario, correoA);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarAnunciosRecientes() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();
        boton_new.setBackgroundColor(COLOR_ACTIVO);

        db.collection("Anuncios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
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
                        adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, usuario, correoA);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "No hay anuncios de tipo 'Ofrezco'", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al recuperar los anuncios recientes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //################### filtro fav

    private void cargarAnunciosFavoritos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        limpiarListas();
        boton_fav.setBackgroundColor(COLOR_ACTIVO);

        db.collection("Usuarios")
                .whereEqualTo("correo", usuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> usuarios = document.getData();
                            if (usuarios != null) {
                                List<String> listaFavoritos = (List<String>) usuarios.get("listaFavoritos");

                                // Verificar si la lista no está vacía
                                if (listaFavoritos != null && !listaFavoritos.isEmpty()) {
                                    // Procesar los elementos de 'listaFavoritos'
                                    FirebaseFirestore db2 = FirebaseFirestore.getInstance();


                                    for (String favorito : listaFavoritos) {
                                        // Hacer algo con los elementos de la lista, por ejemplo:
                                        db2.collection("Anuncios")
                                                .whereEqualTo("id", favorito)
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                                    if (!queryDocumentSnapshots2.isEmpty()) {
                                                        for (DocumentSnapshot document2 : queryDocumentSnapshots2.getDocuments()) {
                                                            Map<String, Object> anuncio = document2.getData();
                                                            if (anuncio != null) {
                                                                datalist.add((String) anuncio.get("titulo"));
                                                                datalist2.add((String) anuncio.get("direccion"));
                                                                datalist3.add((String) anuncio.get("tipo"));

                                                                String auxi2 = (String) anuncio.get("correo");
                                                                correoA.add(auxi2);
                                                                String filename = auxi2 + ".jpg";
                                                                String urlImagen = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;
                                                                imagenes.add(urlImagen);
                                                            }
                                                        }
                                                        adapter = new MyAdapter(datalist, datalist2, datalist3, imagenes, usuario, correoA);
                                                        recyclerView.setAdapter(adapter);
                                                        // **Actualizar el adaptador después de modificar las listas**
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });

                                    }

                                }else{
                                    limpiarListas();
                                }
                            }
                        }
                    }
                });
    }


    //################### filtro fav

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
        adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, usuario, correoA);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
