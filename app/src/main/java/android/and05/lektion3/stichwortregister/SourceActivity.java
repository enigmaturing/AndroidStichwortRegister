package android.and05.lektion3.stichwortregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SourceActivity extends Activity {

    /*Fileds*/
    public static String QUELLEN_ID = "QUELLEN_ID";
    private AutoCompleteTextView textView;
    private StichwortRegisterHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);

        //Instantation of a new helper object that helps with inserting new elements in the db,
        //each time that the button on this view is pressed.
        helper = new StichwortRegisterHelper(this);

        textView = (AutoCompleteTextView) findViewById(R.id.input_kurzbezeichnung);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, helper.getKurzbezeichnungen());
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ausgewahelteKurzbezeichnung = ((TextView) view).getText().toString();
                Log.i("TEST", "Item: " + ausgewahelteKurzbezeichnung);
                HashMap<String, String> hashMapForKurzbezeichnung = helper.getQuelle(ausgewahelteKurzbezeichnung);
                ((TextView) findViewById(R.id.input_titel_der_quelle)).setText(hashMapForKurzbezeichnung.get("titel"));
                ((TextView) findViewById(R.id.input_autoren)).setText(hashMapForKurzbezeichnung.get("autoren"));
                ((TextView) findViewById(R.id.input_verlag)).setText(hashMapForKurzbezeichnung.get("verlag_ort_url"));
                ((TextView) findViewById(R.id.input_publikationsdatum)).setText(hashMapForKurzbezeichnung.get("publikationsdatum"));
            }
        });
    }

    public void onButtonClick(View view) {
        //Retrieve all of the elements from the GUI and store them in string variables
        String kurzbezeichnung = ((TextView) findViewById(R.id.input_kurzbezeichnung)).getText().toString();
        String titel = ((TextView) findViewById(R.id.input_titel_der_quelle)).getText().toString();
        String autoren = ((TextView) findViewById(R.id.input_autoren)).getText().toString();
        String verlagOrtUrl = ((TextView) findViewById(R.id.input_verlag)).getText().toString();
        String publikationsdatum = ((TextView) findViewById(R.id.input_publikationsdatum)).getText().toString();

        //If any of the retrieved strings is blank, just show a message and do not store this in the
        //db by returning from this methode
        if (kurzbezeichnung.length() == 0 ||
                titel.length() == 0 ||
                autoren.length() == 0 ||
                verlagOrtUrl.length() == 0){
            Toast.makeText(this, R.string.quellen_not_null, Toast.LENGTH_SHORT).show();
            return;
        }

        //Insert the retrieved info in the db as a new row
        helper.insertQuelle(kurzbezeichnung, titel, autoren, verlagOrtUrl, publikationsdatum);

        //Go back to the original activity that called this one but before that, put the needed
        //information in the Intent, in order for the original activity to know what happened there
        Intent pushIntent = this.getIntent();
        pushIntent.putExtra(QUELLEN_ID, String.valueOf(textView.getText()));
        setResult(Activity.RESULT_OK, pushIntent);
        finish();
    }



}
