package com.ariofrio.justhelp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Perfil_bu extends AppCompatActivity {
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    private static final String CLOUD_NAME = "dntghzeqe";  // Reemplaza con tu Cloud Name
    private static final String UPLOAD_PRESET = "img_users";  // Reemplaza con tu upload preset

    private ImageView imageView;
    private Button uploadButton;

//PRUEBA
    private TextView fech;

    //PRUEBA

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
        configureCloudinary();
        initCloudinary();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No se necesita el permiso para acceder a las imágenes en Android 10 o superior
            Log.d("***Permisos", "Scoped Storage activo.");
        } else {
            // Solicita el permiso de almacenamiento para versiones anteriores a Android 10
            requestStoragePermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }

            // Verifica si el permiso de lectura está concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("***Permisos", "Permiso de almacenamiento concedido.");
            } else {
                // Si el permiso no está concedido, solicítalo
                requestStoragePermission();
            }




        //requestStoragePermission();

        imageView = findViewById(R.id.imagenperfil);
        uploadButton = findViewById(R.id.botonimagen);
         fech = findViewById(R.id.fecha); //para pruebas??

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
                    Toast.makeText(Perfil_bu.this, "Error al recuperar los anuncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    // Obtén el URI de la imagen seleccionada
                    Uri imageUri = result.getData().getData();

                    if (imageUri != null) {
                        // Aquí puedes usar la URI de la imagen seleccionada para mostrarla en un ImageView
                        imageView.setImageURI(imageUri);

                        // Subir a Cloudinary
                        uploadImageToCloudinary(imageUri); // Llamada al método modificado
                    }
                }
            });




    private String getRealPathFromURI(Uri contentUri) {
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


    private void uploadImageToCloudinary(Uri imageUri) {
        // Usamos 'MediaManager' de Cloudinary para subir la imagen utilizando su Uri
        MediaManager.get().upload(imageUri)  // Usamos directamente el Uri para la carga
                .option("upload_preset", UPLOAD_PRESET)  // Usa tu upload preset aquí
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // Se ha iniciado la subida
                        Log.d("Cloudinary", "Subida comenzada");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Muestra el progreso de la carga
                        Log.d("Cloudinary", "Progreso: " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Subida exitosa
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d("Cloudinary", "Imagen subida con éxito: " + imageUrl);
                        Toast.makeText(Perfil_bu.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                        // Aquí puedes usar la URL de la imagen subida
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        // Si ocurre un error
                        Log.e("Cloudinary", "Error al subir imagen: " + error.getDescription());
                        Toast.makeText(Perfil_bu.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // En caso de que la carga se reprogramada
                        Log.e("Cloudinary", "Reschedule: " + error.getDescription());
                    }
                }).dispatch(); // Realiza la subida
    }




   /* private void uploadImageToCloudinary(File imageFile) {
        // Crear Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloudinary.com/v1_1/dntghzeqe/image/")
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
                    Toast.makeText(Perfil.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                    Log.d("Cloudinary", "Imagen subida correctamente");
                    String imageUrl = response.body().getSecureUrl(); // Usamos secure_url por ser más confiable
                    Log.d("Cloudinary", "Imagen disponible en: " + imageUrl);
                    Toast.makeText(Perfil.this, "Imagen disponible en: " + imageUrl, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Perfil.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("Cloudinary", "Error al subir la imagen");
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                Log.e("Cloudinary", "Error en la conexión: "+t.getMessage(), t);
                Toast.makeText(Perfil.this, "On Failure: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                String err = t.getMessage();
                fech.setText(err);
            }
        });
        Toast.makeText(this, "Después del enqueue", Toast.LENGTH_SHORT).show();
    }*/



    private void requestStoragePermission() {
        // Verifica si el permiso ya está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicita el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Verifica si el permiso de almacenamiento fue concedido
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("***Permisos", "Permiso de almacenamiento concedido.");
            } else {
                // Si el permiso es denegado, muestra un mensaje
                Log.d("***Permisos", "Permiso de almacenamiento denegado.");
                Toast.makeText(this, "Permiso denegado. No puedes seleccionar imágenes.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", CLOUD_NAME);
        MediaManager.init(this, config);
    }

    private void configureCloudinary() {
        // Configuración de Cloudinary

        Map<String, String> configMap = new HashMap<>();
        configMap.put("cloud_name", CLOUD_NAME);
        configMap.put("api_key", "581492492428263");
        configMap.put("api_secret", "beO41p_TWNMIAzcm8");


        // Ahora puedes usar el objeto 'cloudinary' para subir imágenes
        Cloudinary cloudinary = new Cloudinary(configMap);
    }



}

/* esto para si cierro sesion se borre el correo del cache
//
//  SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//       SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
        editor.apply();
*/

