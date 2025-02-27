package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String usuario,correoA,nombre;
    List<String> datalist = new ArrayList<>();
    List<String> datalist2= new ArrayList<>();
    List<String> datalist3= new ArrayList<>();
    List<String> imagenes=new ArrayList<>();
    String auxi;
    MyAdapter adapter;
    TextView tipoColor;

    private static final String SUPABASE_URL = "https://gpdsntyatqmierlzjqqk.supabase.co"; // Reemplaza con tu URL
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdwZHNudHlhdHFtaWVybHpqcXFrIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTczOTQzODUzOSwiZXhwIjoyMDU1MDE0NTM5fQ.krLfkuIT0o9xnzCzuSzYaZIJ2j-nt7jhuSNknvlrmJ0"; // Reemplaza con tu clave API
    private static final String BUCKET_NAME = "img_users"; // Nombre del bucket en Supabase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        usuario=intent.getStringExtra("correo");

        //Toast.makeText(this, "bienvenido" + usuario, Toast.LENGTH_SHORT).show(); //Esto podría configurarse para que solo se haga desde Login

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
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
                                correoA = auxi;
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



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void goAnadir(View view) {
        Intent intent = new Intent(MainActivity.this,AniadirO.class);
        intent.putExtra("correo",usuario);
        startActivity(intent);
        finish();
    }


    public void goPerfil(View view) {
        Intent intent = new Intent(MainActivity.this,Perfil.class);
        intent.putExtra("correo",usuario);
        startActivity(intent);
        finish();
    }
    //comentario
}