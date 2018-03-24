package android.and05.lektion3.stichwortregister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Javier on 02/03/2018.
 */

public class StichwortRegisterHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StichwortRegister";
    private static final int DATABASE_VERSION = 1;

    // statische Konstanten:
    private static final String CREATE_TABLE_QUELLEN = "CREATE TABLE Quellen(" +
            "kurzbezeichnung TEXT PRIMARY KEY, " +
            "titel TEXT NOT NULL, " +
            "autoren TEXT NOT NULL, " +
            "verlag_ort_url TEXT NOT NULL, " +
            "publikationsdatum TEXT);";
    private static final String CREATE_TABLE_STICHWORTE = "CREATE TABLE Stichworte(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "stichwort TEXT NOT NULL, " +
            "quelle TEXT NOT NULL, " +
            "fundstelle TEXT NOT NULL, " +
            "text TEXT NOT NULL, " +
            "CONSTRAINT QuellenFK FOREIGN KEY (quelle) " +
            "REFERENCES Quellen(kurzbezeichnung) " +
            "ON DELETE RESTRICT ON UPDATE CASCADE);";

    public StichwortRegisterHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Die Methode onCreate der Klasse SQLiteOpenHelper wird vom Laufzeitsystem angreufen nur für
    //den (Start-)Fall, dass die Datenbank db nicht existiert. Darin liegt der kern der Hilfe,
    //die die Helper-Klasse zu bieten hat.
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_QUELLEN);
            db.execSQL(CREATE_TABLE_STICHWORTE);
            Log.d(getClass().getSimpleName(), "Datenbank erzeugt in: \"" + db.getPath() + "\"");
        }catch(SQLException ex){
            Log.e(getClass().getSimpleName(), "onCreate: " + ex.toString());
        }
    }

    //This method of the helper is called each time we want to insert a new item in the DB Quellen
    public void insertQuelle(String kurzbezeichnung, String titel, String autoren, String verlagOrtUrl,
                             String publikationsdatum){
        //First, store all of the elements in an object of type ContentValues.
        ContentValues values = new ContentValues(5);
        values.put("kurzbezeichnung", kurzbezeichnung);
        values.put("titel", titel);
        values.put("autoren", autoren);
        values.put("verlag_ort_url", verlagOrtUrl);
        values.put("publikationsdatum", publikationsdatum);

        //Then, store all of the elements in the DB by passig that object of type ContentValues to
        //the method insertOrThrow()
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insertOrThrow("Quellen", null, values);
        }catch(SQLException ex){
            Log.d(this.getClass().getSimpleName(), ex.toString());
        }finally {
            db.close();
        }
    }

    //This method of the helper is called each time we want to insert a new item in the DB Stichworte
    public void insertStichwort(Context context, String stichwort, String quelle, String fundstelle, String text){
        //First, store all of the elements in an object of type ContentValues.
        ContentValues values = new ContentValues(4);
        values.put("stichwort", stichwort);
        values.put("quelle", quelle);
        values.put("fundstelle", fundstelle);
        values.put("text", text);

        //Then, sthore all of the elements in the DB by passing that object of type ContentValues
        //to the method insertOrThrow()
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insertOrThrow("Stichworte", null, values);
            Toast.makeText(context, R.string.eintrag_gespeichert, Toast.LENGTH_SHORT).show();
        }catch(SQLException ex){
            Log.d(this.getClass().getSimpleName(), ex.toString());
            Toast.makeText(context, R.string.eintrag_nicht_gespeichert, Toast.LENGTH_SHORT).show();
        }finally {
            db.close();
        }
    }

    //this method is called in order to get the actual values stored in the column "kurzbezeichnung",
    //so we can populate the dropdown that appears when clicking on the AutoCompleteTextView of the
    //GUI destinated for the Kurzbezeichnung. It returns an ArrayList of Strings, that can be
    //directly passed as parameter to the adapter of the AutoCompleteTextView
    public ArrayList<String> getKurzbezeichnungen(){
        //Create an instance of time SQLiteDatabase, to be able to work with the DB.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("Quellen", new String[] {"kurzbezeichnung"}, null,
                null, null, null, "kurzbezeichnung", null);
        //Delcare an arraylist named kurzbezeichnungen to store all of the kurzbezeichnungen in it
        ArrayList<String> kurzbezeichnungen = new ArrayList<String>();
        //Get all of the kurzbezeichnungen with the help of the cursor return by the method query
        // of the object db of the class SQLiteDatabase.
        while(cursor.moveToNext()){
            kurzbezeichnungen.add(cursor.getString(0));
        }
        //Close the cursor and the db and return the ArrayList containing the kurzbezeichnungen.
        cursor.close();
        db.close();
        return kurzbezeichnungen;
    }

    //This method returns a HashMap containing all of the items for a given kurzbezeichnung in the
    //table "Quellen"
    public HashMap<String, String> getQuelle(String kurzbezeichnung){
        // Create an instance of time SQLiteDatabase, to be able to work with the DB.
        SQLiteDatabase db = getReadableDatabase();
        // To improve readability of this code, we declare a string containing the
        // SELECT option of the Method querry()
        String selectThis = "kurzbezeichnung='" + kurzbezeichnung + "'";
        // Using the created string selectThis, call the query Method to obtain a cursor object
        // so we can use it to iterate through the desired contents of the table "Quellen"
        Cursor cursor = db.query("Quellen",null, selectThis,
                null, null, null, "kurzbezeichnung", null);
        // Delcare a HashMap named tableContents to store all of the contents of the table in itA
        HashMap<String, String> tableContents = new HashMap();
        // Get all of the kurzbezeichnungen with the help of the cursor return by the method query
        // of the object db of the class SQLiteDatabase and store them into the HashMap "tableContents"
        while(cursor.moveToNext()) {
            for (int i = 1; i < cursor.getColumnCount(); i++){
                tableContents.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        // Close the cursor and the db and return the ArrayList containing the kurzbezeichnungen.
        cursor.close();
        db.close();
        // Return the created HashMaps with the contents of the table Quellen in it.
        return tableContents;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
