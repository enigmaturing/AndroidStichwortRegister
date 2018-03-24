package android.and05.lektion3.stichwortregister;

// We import the class android.database.sqlite.SQLiteDatabase So we can create an object helper to
// the databank and use it to get a reference to the databank with the mehtod of the helper object
// called getWritableDatabase
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class KeywordActivity extends Activity {

    private StichwortRegisterHelper helper = null;
    private String aktuelleQuelle = null;
    private static final int RequestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);

        //Create Helper for Databank
        if (helper == null) helper = new StichwortRegisterHelper(this);

        // aktuelleQuelle is read from the SharedPreferences. If there is still no value stored in
        // the SharedPreferences (because the app has been lunched right after its installation)
        // return an empty String
        aktuelleQuelle = getSharedPreferences("preferences", MODE_PRIVATE).getString("aktuelleQuelle", "");

        // Change to SourceActivity if the aktuelleQulle is an empty String. If aktuelleQulle is not
        // an empty String, show its value on the TextView textview_aktuelle_quelle, along with the
        // String from the resources quellen_prefix. This way, we can ensure that there is
        // always a selected Quelle, and that it is not empty.
        if (aktuelleQuelle.equals("") == true) {
            Intent intent = new Intent(this, SourceActivity.class);
            this.startActivityForResult(intent, RequestCode);
            Log.i(getClass().getSimpleName(), "Übergegebene requestCode: " + RequestCode);
        }else{
            ((TextView) findViewById(R.id.textview_aktuelle_quelle)).setText(getString(R.string.quellen_prefix) + aktuelleQuelle);
        }
    }

    //This methode will be run if the button "Literaturquelle erfassen" is pressed.
    public void onButtonClick(View view){
        Intent intent = new Intent(this, SourceActivity.class);
        this.startActivityForResult(intent, RequestCode);
        Log.i(getClass().getSimpleName(), "Übergegebene requestCode: " + RequestCode);
    }

    public void onButtonClickEintragSpeichern(View view){
        //if aktuelleQuelle is null, there has not been a source still selected. Do not let save a
        //stichwort if that is so
        if (aktuelleQuelle == null){
            Toast.makeText(this, R.string.leere_quelle, Toast.LENGTH_SHORT).show();
            return;
        }
        //retrieve all of the data from the text views
        String stichwort = ((TextView) findViewById(R.id.edittext_stichwort)).getText().toString();
        String fundstelle = ((TextView) findViewById(R.id.edittext_fundstelle)).getText().toString();
        String text = ((TextView) findViewById(R.id.edittext_text)).getText().toString();
        //Since all of the fileds must contain something to be inserted in the DB ("NOT NULL"),
        //check if the retrieved data from the editText are not empty strings
        if (stichwort.length() == 0 || fundstelle.length() == 0 || text.length() == 0){
            Toast.makeText(this, R.string.stichworte_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        helper.insertStichwort(this, stichwort, aktuelleQuelle, fundstelle, text);

        ((TextView) findViewById(R.id.edittext_stichwort)).setText("");
        ((TextView) findViewById(R.id.edittext_text)).setText("");
        ((TextView) findViewById(R.id.edittext_fundstelle)).setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent){
        Log.i(getClass().getSimpleName(), "Zurückgegebene requestCode: " + requestCode);
        if (resultCode != Activity.RESULT_OK){
            Log.d(getClass().getSimpleName(), "\"returnIntent\" ist null (Back-Taste gedrückt)");
            return;
        }
        if (requestCode == 0) {
            Bundle extras = returnIntent.getExtras();
            if (extras != null) {
                String quelle = extras.getString(SourceActivity.QUELLEN_ID);
                TextView textView = (TextView) findViewById(R.id.textview_aktuelle_quelle);
                textView.setText(getString(R.string.quellen_prefix) + quelle);
                // refresh the variable aktuelleQuelle and the SahredPreferences "aktuelleQuelle"
                // with the value selected by the user on the SourceActivity
                aktuelleQuelle = quelle;
                getSharedPreferences("preferences", MODE_PRIVATE).edit()
                                    .putString("aktuelleQuelle", quelle).apply();
            }
        }
    }
}
