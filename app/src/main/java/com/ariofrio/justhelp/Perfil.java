package com.ariofrio.justhelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ariofrio.justhelp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Perfil extends AppCompatActivity {


    String correo,auxi,nombre;
    List<String>datalist=new ArrayList<>();
    List<String>datalist2=new ArrayList<>();
    List<String>datalist3=new ArrayList<>();
    MyAdapter adapter;
    TextView e_nombre,e_correo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        e_nombre= findViewById(R.id.nombre);
         e_correo= findViewById(R.id.correo);


        Intent intent=getIntent();
        correo=intent.getStringExtra("correo");

        e_correo.setText(correo);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios").document(correo)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                               String nombre = documentSnapshot.getString("nombre");

                                e_nombre.setText(nombre);

                            }

                });

        db.collection("Anuncios")
                //añado el where para filtrar por correo ya almacenado en cache una vez registrado
                .whereEqualTo("correo", correo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Lista para almacenar los anuncios recuperados
                        List<Map<String, Object>> listaAnuncios = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Map<String, Object> anuncio = document.getData();

                            // Mostrar en consola los datos de cada anuncio (puedes usar los valores según necesites)
                            if (anuncio != null) {

                                     auxi = (String) anuncio.get("titulo");
                                     datalist.add(auxi);
                                     auxi = (String) anuncio.get("direccion");
                                     datalist2.add(auxi);
                                     auxi = (String) anuncio.get("tipo");
                                     datalist3.add(auxi);

                            }
                            adapter.notifyDataSetChanged();
                            //AÑADO ESTE CODIGO
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Perfil.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });



        RecyclerView recyclerView = findViewById(R.id.recyclerView);
          recyclerView.setLayoutManager(new LinearLayoutManager(this));

       /* List<String> datalist = Arrays.asList("Elemento1","Elemento2","Elemento3","Elemento4","Elemento5","Elemento1","Elemento2","Elemento3","Elemento4","Elemento5");
        List<String> datalist2 = Arrays.asList("DIRnto1","DIRo2","DIR3","DIR4","DIR5","DIRnto1","DIRo2","DIR3","DIR4","DIR5");
        List<String> datalist3 = Arrays.asList("Ofrece","NEcesito","Ofrece","sdad","zasfasf","Ofrece","NEcesito","Ofrece","sdad","zasfasf");*/


         adapter = new MyAdapter(datalist, datalist2, datalist3,correo);
        recyclerView.setAdapter(adapter);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void logOut(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // o el nombre de la clave con la que guardaste el ID
        editor.apply();
        Intent intent = new Intent(Perfil.this,Login.class);
        startActivity(intent);
        finish();
    }
    public void goAnadir(View view){
        Intent intent = new Intent(this,AniadirO.class);
        intent.putExtra("correo",correo);
        startActivity(intent);
        finish();
    }
    public void goMain(View view){
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("correo",correo);
        startActivity(intent);
        finish();
    }
}

/* esto para si cierro sesion se borre el correo del cache

  SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
*/