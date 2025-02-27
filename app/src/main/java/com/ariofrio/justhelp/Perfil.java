package com.ariofrio.justhelp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ariofrio.justhelp.R;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.*;
import com.bumptech.glide.Glide;


public class Perfil extends AppCompatActivity {


    String correo,auxi,nombre;
    private static final String SUPABASE_URL = "https://gpdsntyatqmierlzjqqk.supabase.co"; // Reemplaza con tu URL
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdwZHNudHlhdHFtaWVybHpqcXFrIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTczOTQzODUzOSwiZXhwIjoyMDU1MDE0NTM5fQ.krLfkuIT0o9xnzCzuSzYaZIJ2j-nt7jhuSNknvlrmJ0"; // Reemplaza con tu clave API
    private static final String BUCKET_NAME = "img_users"; // Nombre del bucket en Supabase
    private Uri imageUri;
    List<String>datalist=new ArrayList<>();
    List<String>datalist2=new ArrayList<>();
    List<String>datalist3=new ArrayList<>();
    List<String> imagenes=new ArrayList<>();
    Button boton_ofrezco,boton_necesito;
    MyAdapter adapter;
    TextView e_nombre,e_correo;
    ImageView img_perfil;
    EditText e_direccion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        e_nombre= findViewById(R.id.nombre);
        e_correo= findViewById(R.id.correo);
        e_direccion=findViewById(R.id.direccion);
        e_correo= findViewById(R.id.correo);
        img_perfil = findViewById(R.id.imagenperfil);
        boton_ofrezco=findViewById(R.id.button_ofrezco);
        boton_necesito=findViewById(R.id.button_necesito);






        img_perfil.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_PICK);
            intent2.setType("image/*");

            imagePickerLauncher.launch(intent2);
        });

        Intent intent=getIntent();
        correo=intent.getStringExtra("correo");

        e_correo.setText(correo);

        checkImageExists(); //*********** COMPROBAR SI EXISTE LA IMAGEN EN SUPABASE ******
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

                                auxi = (String) anuncio.get("correo");
                                correo=auxi;
                                String filename =auxi + ".jpg";
                                String urlImagen = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;
                                imagenes.add(urlImagen);


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


         adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, correo, correo);
        recyclerView.setAdapter(adapter);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        boton_ofrezco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // **Limpiar listas antes de agregar nuevos datos**
                datalist.clear();
                datalist2.clear();
                datalist3.clear();
                imagenes.clear();

                db.collection("Anuncios")
                        .whereEqualTo("tipo", "Ofrezco")// **Filtra solo los anuncios de tipo "Ofrezco"**
                        .whereEqualTo("correo", correo)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    Map<String, Object> anuncio = document.getData();
                                    if (anuncio != null) {
                                        // Agregar datos a las listas
                                        datalist.add((String) anuncio.get("titulo"));
                                        datalist2.add((String) anuncio.get("direccion"));
                                        datalist3.add((String) anuncio.get("tipo"));

                                        String auxi = (String) anuncio.get("correo");
                                        String filename = auxi + ".jpg";
                                        String urlImagen = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;
                                        imagenes.add(urlImagen);
                                    }
                                }
                                adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, correo, correo);
                                recyclerView.setAdapter(adapter);
                                // **Actualizar el adaptador después de modificar las listas**
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(Perfil.this, "No hay anuncios de tipo 'Ofrezco'", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Perfil.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        boton_necesito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // **Limpiar listas antes de agregar nuevos datos**
                datalist.clear();
                datalist2.clear();
                datalist3.clear();
                imagenes.clear();

                db.collection("Anuncios")
                        .whereEqualTo("tipo", "Necesito")//
                        .whereEqualTo("correo", correo)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    Map<String, Object> anuncio = document.getData();
                                    if (anuncio != null) {
                                        // Agregar datos a las listas
                                        datalist.add((String) anuncio.get("titulo"));
                                        datalist2.add((String) anuncio.get("direccion"));
                                        datalist3.add((String) anuncio.get("tipo"));

                                        String auxi = (String) anuncio.get("correo");
                                        String filename = auxi + ".jpg";
                                        String urlImagen = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;
                                        imagenes.add(urlImagen);
                                    }
                                }
                                adapter = new MyAdapter(datalist, datalist2, datalist3,imagenes, correo, correo);
                                recyclerView.setAdapter(adapter);
                                // **Actualizar el adaptador después de modificar las listas**
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(Perfil.this, "No hay anuncios de tipo 'Necesito'", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Perfil.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
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

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Obtén el URI de la imagen seleccionada
                    imageUri = result.getData().getData();

                    if (imageUri != null) {
                        // Aquí puedes usar la URI de la imagen seleccionada para mostrarla en un ImageView
                        img_perfil.setImageURI(imageUri);
                        uploadImage();

                    }
                }
            });
    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
            sendToSupabase(base64Image);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToSupabase(String base64Image) {
        String filename = correo + ".jpg";
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;

        RequestBody body = RequestBody.create(
                Base64.decode(base64Image, Base64.DEFAULT),
                MediaType.parse("image/jpeg")
        );

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .header("Content-Type", "image/jpeg")
                .put(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("Supabase", "Imagen subida con éxito: " + url);
                    runOnUiThread(() -> Toast.makeText(this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("Supabase", "Error al subir imagen: " + response.message());
                    runOnUiThread(() -> Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void checkImageExists() {
        String filename = correo + ".jpg"; // Nombre de la imagen
        String url = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + filename;

        // Hacer una solicitud HEAD para verificar si el archivo existe
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .head() // Solicitud HEAD para solo verificar los encabezados
                .build();

        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    // Si la respuesta es exitosa, significa que la imagen ya existe
                    runOnUiThread(() -> {
                        // Obtener la URL pública de la imagen
                        String imageUrl = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + filename;
                        // Usar Glide para cargar la imagen en el ImageView
                        Glide.with(Perfil.this)
                                .load(imageUrl)
                                .transform(new CircleCrop())
                                .into(img_perfil);  // img_perfil es tu ImageView
                        //Toast.makeText(Perfil.this, "Imagen encontrada y cargada", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Perfil.this, "IMAGE_URL: "+imageUrl, Toast.LENGTH_SHORT).show();
                        //Log.d("ImageURL",imageUrl);
                    });
                } else {
                    // Si no existe, mostrar mensaje de error
                    runOnUiThread(() -> {
                        //Toast.makeText(Perfil.this, "No se encontró la imagen", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(Perfil.this, "Error al verificar la imagen", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }



}
