package com.example.taller_2_2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class UserDashBoard extends AppCompatActivity {

    private ListView listView;
    private TextView tvBaseCoct,tvBaseCoct2,tvJugo1,tvJugo2,tvnameUser;
    private Spinner spBase1,spBase2,spJugo1,spJugo2;
    private FloatingActionButton fabAdd;
    private TextInputLayout tilNomCoct;
    private Button btnBuscar,btnFilter;
    private List <String[]> cocteles;
    private List <String> baseCoctel,basesJugo;
    private String nomCoct,base1,base2,jugo1,jugo2;
    private boolean flagFilter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);
        // Referencias WIDGETS es lo mismo que el getElementById de JS para HTML
        listView = (ListView) findViewById(R.id.lvRegistros);
        spBase1 = findViewById(R.id.spBasesCoct);
        spBase2 = findViewById(R.id.spiBasesCoct2);
        spJugo1 = findViewById(R.id.spJugo1);
        spJugo2 = findViewById(R.id.spJugo2);
        tilNomCoct = findViewById(R.id.til_nom_coct);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnFilter = findViewById(R.id.btnFilter);
        fabAdd = findViewById(R.id.fabAdd);
        tvnameUser = findViewById(R.id.nameUser);

        flagFilter = true;


        //LISTA de DATOS
        baseCoctel = new ArrayList<String>();
        baseCoctel.add("Seleccione la opción");
        baseCoctel.add("Ron");
        baseCoctel.add("Tequila");
        baseCoctel.add("Pisco");
        baseCoctel.add("Gin");

        basesJugo = new ArrayList<String>();
        basesJugo.add("Seleccione la opción");
        basesJugo.add("Piña");
        basesJugo.add("Limon");
        basesJugo.add("Frutilla");
        basesJugo.add("Frambuesa");

        //METODO LISTAR DATOS DE LA DB
        cocteles = new ArrayList<String[]>();
        cocteles = listarDatos();


        //INICIALIZAMOS LAS VARIABLES DE SHARED PREFERENCES
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //VERIFICAMOS SI EXISTE UN VALOR ALMACENADO EN LAS SHARED PREFERENCES
        String nameUer = sharedPreferences.getString("nombreUser","");
        String surnameUer = sharedPreferences.getString("apellidoUser","");
        if(!nameUer.equals("") && !surnameUer.equals("")){
            tvnameUser.setText("Bienvenido: "+nameUer+" "+surnameUer);
        }


        //___________________________________________SPINNER_____________________________________
        //GENERAMOS UN ADAPTER PARA CADA SPINNER
        ArrayAdapter<String> adapterBaseCoct = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,baseCoctel);
        ArrayAdapter<String> adapterJugoCoct = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,basesJugo);
        //ASIGNAR ADAPTER A SPINNER
        spBase1.setAdapter(adapterBaseCoct);
        spBase2.setAdapter(adapterBaseCoct);
        spJugo1.setAdapter(adapterJugoCoct);
        spJugo2.setAdapter(adapterJugoCoct);

        //___________________________________________SPINNER_____________________________________



        //ADAPTADOR es la forma visual de renderizar los datos
        //se crea un adapter, un adaptador es la forma de mostrar los datos, como se renderizara (un template de render)
        //__________________________________________INICIO LISTVIEW SIMPLE ADAPTER____________________________________________
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,registros);
        listView.setAdapter(adapter);*/


        //__________________________________________FIN LISTVIEW SIMPLE ADAPTER____________________________________________

        //__________________________________________LISTVIEW CUSTON ADAPTER____________________________________________
        AdaptadorCocteles adaptadorCocteles = new AdaptadorCocteles(this,R.layout.list_item_coctel,cocteles);
        listView.setAdapter(adaptadorCocteles);
        //__________________________________________LISTVIEW CUSTON ADAPTER____________________________________________

        //EVENTO CLICK
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long id) {
                //Toast.makeText(UserDashBoard.this,"Clicked"+registros.get(posicion),Toast.LENGTH_SHORT).show();
                //EJECUCION PARA CAMBIAR DE LAYOUT
                Intent intent = new Intent(view.getContext(),RegistroElement.class);
                intent.putExtra("elemRegistro",cocteles.get(posicion));
                startActivity(intent);
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),NewCoctel.class);
                startActivity(intent);
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base1 = spBase1.getSelectedItem().toString();
                base2 = spBase2.getSelectedItem().toString();
                jugo1 = spJugo1.getSelectedItem().toString();
                jugo2 = spJugo2.getSelectedItem().toString();
                nomCoct = tilNomCoct.getEditText().getText().toString();
                if(flagFilter){
                    buscarDatos(nomCoct);
                }
                else{
                    buscarDatos(nomCoct,base1,base2,jugo1,jugo2);
                }

            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagFilter){
                    flagFilter = false;
                    toggleFilters(true);
                }else{
                    flagFilter = true;
                    toggleFilters(false);
                }
                //Toast.makeText(UserDashBoard.this, "flagFilter"+flagFilter, Toast.LENGTH_SHORT).show();
            }
        });

        //EVENTO KEY UP (TEXT WATCHER)
        tilNomCoct.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                //Toast.makeText(UserDashBoard.this, "beforeTextChanged start:"+start+",before:"+before+",count:"+count, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                //Toast.makeText(UserDashBoard.this, "onTextChanged start:"+start+",count:"+count+",after:"+after, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(tilNomCoct.getEditText().length() >= 0){
                    buscarDatos(tilNomCoct.getEditText().getText().toString());
                    //Toast.makeText(UserDashBoard.this, tilNomCoct.getEditText().getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    public List<String[]> listarDatos(){
        List<String[]> cocteles = new ArrayList<String[]>();
        DBhelper dBhelper =  new DBhelper(this,"DataBase_CIISA",null,1);
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        if(db != null){
            Cursor cr = db.rawQuery("SELECT * FROM tbl_datos",null);
            if(cr.moveToFirst()){
                do{
                    //SE POBLA VECTOR COCTEL
                    String[] arrayCoctel = new String[6];
                    arrayCoctel[0] =  cr.getString(0); //ID COCTEL
                    arrayCoctel[1] =  cr.getString(1); //NOMBRE COCTEL
                    arrayCoctel[2] =  cr.getString(2); //BASE 1
                    arrayCoctel[3] =  cr.getString(3); //BASE 2
                    arrayCoctel[4] =  cr.getString(4); //JUGO 1
                    arrayCoctel[5] =  cr.getString(5); //JUGO 2
                    cocteles.add(arrayCoctel);
                }while(cr.moveToNext());
            }
        }
        return cocteles;
    }
    public void buscarDatos(String nom_coctel){
        List<String[]> cocteles = new ArrayList<String[]>();
        DBhelper dBhelper =  new DBhelper(this,"DataBase_CIISA",null,1);
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        if(db != null){
            Cursor cr = db.rawQuery("SELECT * FROM tbl_datos WHERE nombre_coctel LIKE  '"+nom_coctel+"%'",null);
            if(cr.moveToFirst()){
                do{
                    //SE POBLA VECTOR COCTEL
                    String[] arrayCoctel = new String[6];
                    arrayCoctel[0] =  cr.getString(0); //ID COCTEL
                    arrayCoctel[1] =  cr.getString(1); //NOMBRE COCTEL
                    arrayCoctel[2] =  cr.getString(2); //BASE 1
                    arrayCoctel[3] =  cr.getString(3); //BASE 2
                    arrayCoctel[4] =  cr.getString(4); //JUGO 1
                    arrayCoctel[5] =  cr.getString(5); //JUGO 2
                    cocteles.add(arrayCoctel);
                }while(cr.moveToNext());
            }
        }
        AdaptadorCocteles adaptadorCocteles = new AdaptadorCocteles(this,R.layout.list_item_coctel,cocteles);
        listView.setAdapter(adaptadorCocteles);
    }
    public void buscarDatos(String nom_coctel,String sbase1,String sbase2,String sjugo1,String sjugo2){
        List<String[]> cocteles = new ArrayList<String[]>();
        String sqlBase = "SELECT * FROM tbl_datos WHERE nombre_coctel LIKE  '"+nom_coctel+"%' ";
        if(!sbase1.equals("Seleccione la opción")){
            sqlBase+=" AND base1 = '"+sbase1+"' ";
        }
        if(!sbase2.equals("Seleccione la opción")){
            sqlBase+=" AND base2 = '"+sbase2+"' ";
        }
        if(!sjugo1.equals("Seleccione la opción")){
            sqlBase+=" AND jugo1 = '"+sjugo1+"' ";
        }
        if(!sjugo2.equals("Seleccione la opción")){
            sqlBase+=" AND jugo2 = '"+sjugo2+"' ";
        }
        Toast.makeText(this, sqlBase, Toast.LENGTH_SHORT).show();
        DBhelper dBhelper =  new DBhelper(this,"DataBase_CIISA",null,1);
        SQLiteDatabase db = dBhelper.getReadableDatabase();
        if(db != null){
            Cursor cr = db.rawQuery(sqlBase,null);
            if(cr.moveToFirst()){
                do{
                    //SE POBLA VECTOR COCTEL
                    String[] arrayCoctel = new String[6];
                    arrayCoctel[0] =  cr.getString(0); //ID COCTEL
                    arrayCoctel[1] =  cr.getString(1); //NOMBRE COCTEL
                    arrayCoctel[2] =  cr.getString(2); //BASE 1
                    arrayCoctel[3] =  cr.getString(3); //BASE 2
                    arrayCoctel[4] =  cr.getString(4); //JUGO 1
                    arrayCoctel[5] =  cr.getString(5); //JUGO 2
                    cocteles.add(arrayCoctel);
                }while(cr.moveToNext());
            }
        }
        AdaptadorCocteles adaptadorCocteles = new AdaptadorCocteles(this,R.layout.list_item_coctel,cocteles);
        listView.setAdapter(adaptadorCocteles);
    }
    public void toggleFilters(boolean opcion){
        //REFERENCIAMOS LOS WIDGET
        tvBaseCoct = findViewById(R.id.tvBaseCoct);
        tvBaseCoct2 = findViewById(R.id.tvBaseCoct2);
        tvJugo1 = findViewById(R.id.tvJugo1);
        tvJugo2 = findViewById(R.id.tvJugo2);
        if(opcion){
            tvBaseCoct.setVisibility(View.VISIBLE);
            tvBaseCoct2.setVisibility(View.VISIBLE);
            tvJugo1.setVisibility(View.VISIBLE);
            tvJugo2.setVisibility(View.VISIBLE);
            spBase1.setVisibility(View.VISIBLE);
            spBase2.setVisibility(View.VISIBLE);
            spJugo1.setVisibility(View.VISIBLE);
            spJugo2.setVisibility(View.VISIBLE);
        }
        else{
            tvBaseCoct.setVisibility(View.GONE);
            tvBaseCoct2.setVisibility(View.GONE);
            tvJugo1.setVisibility(View.GONE);
            tvJugo2.setVisibility(View.GONE);
            spBase1.setVisibility(View.GONE);
            spBase2.setVisibility(View.GONE);
            spJugo1.setVisibility(View.GONE);
            spJugo2.setVisibility(View.GONE);
        }
    }


}