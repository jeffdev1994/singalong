package jeffdev.singalong;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;


public class SavePopup extends Activity {

    private MediaPlayer   mPlayer = null;
    public Boolean paused = false;
    public Boolean playing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_popup);


    }


    public void pause(View view){
        mPlayer.pause();
        paused = true;
        playing = false;
    }

    public void play(View view){
        try {
            //if it is not already playing
            //TODO: put an on completion listener, so i can change playing back to false when its done. im sure there will be bugs with that right now
            if(!playing) {
                playing = true;
                if (paused) {
                    mPlayer.start();
                } else {
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong/temp/audiorecordtemp.3gp");
                    mPlayer.prepare();
                    mPlayer.start();
                }
            }
        } catch (IOException e) {
            Log.e("error", "prepare() failed");
        }
        paused = false;
    }

    public void stop(View view){
        if(playing || paused) {
            mPlayer.release();
            mPlayer = null;
            paused = false;
            playing = false;
        }
    }

    public void save(View view){
        //on save, just move the temp file to normal location, and add a name to it.
        //TODO: need to give them a way to add a name, possibly do it through a second popup here

        ListView listView = (ListView) findViewById(R.id.SavedList);
        EditText filename = (EditText) findViewById(R.id.filename);
        //TODO:add some error checking for whether or not they will overwrite another file with that name, and a proper way to re ask for the name
        String newFilename;
        if(!filename.getText().toString().equals("")) {
            newFilename = filename.getText().toString();
        }
        else{
            newFilename = "noname - update this";
        }

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong");
        if (!folder.exists())
            folder.mkdirs();

        File oldfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong/temp/audiorecordtemp.3gp");
        File newFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong", newFilename);

        if (!newFile.exists())
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        oldfile.renameTo(newFile);

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);

    }
}
