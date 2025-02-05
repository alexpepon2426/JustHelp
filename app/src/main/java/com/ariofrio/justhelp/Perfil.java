package com.ariofrio.justhelp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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




import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Perfil extends AppCompatActivity {

    private static final String CLOUD_NAME = "dntghzeqe";  // Reemplaza con tu Cloud Name
    private static final String UPLOAD_PRESET = "img_users";  // Reemplaza con tu upload preset

    private ImageView imageView;
    private Button uploadButton;



    String correo,auxi;
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

        imageView = findViewById(R.id.imagenperfil);
        uploadButton = findViewById(R.id.botonimagen);

        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });



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


         adapter = new MyAdapter(datalist, datalist2, datalist3);
        recyclerView.setAdapter(adapter);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                File file = new File(getRealPathFromURI(data.getData()));
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);

                uploadImageToCloudinary(file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/
private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                try {
                    android.net.Uri imageUri = result.getData().getData();
                    String filePath = getRealPathFromURI(imageUri);
                    File file = new File(filePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    uploadImageToCloudinary(file);  // Llamamos al método para subir la imagen a Cloudinary
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    private String getRealPathFromURI(android.net.Uri contentUri) {
        // Usar ContentResolver para obtener la ruta real del archivo
        String[] proj = {android.provider.MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(proj[0]);
            return cursor.getString(columnIndex);
        }
        return null;
    }

    private void uploadImageToCloudinary(File imageFile) {
        // Crear Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloudinary.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CloudinaryApiService service = retrofit.create(CloudinaryApiService.class);

        // Crear RequestBody para la imagen
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        // Crear RequestBody para el upload preset
        RequestBody preset = RequestBody.create(MediaType.parse("text/plain"), UPLOAD_PRESET);

        // Hacer la solicitud
        Call<CloudinaryResponse> call = service.uploadImage(body, preset);
        call.enqueue(new Callback<CloudinaryResponse>() {
            @Override
            public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("Cloudinary", "Imagen subida correctamente");
                    String imageUrl = response.body().getSecureUrl(); // Usamos secure_url por ser más confiable
                    Log.d("Cloudinary", "Imagen disponible en: " + imageUrl);
                } else {
                    Log.e("Cloudinary", "Error al subir la imagen");
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                Log.e("Cloudinary", "Error en la conexión", t);
            }
        });
    }



}

/* esto para si cierro sesion se borre el correo del cache
//
//  SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//       SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
        editor.apply();
*/