package jeffdev.singalong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/*TODO: known issues:
*can click an audio clip to play while recording, which is annoying cuz you get a popup - maybe a feature, not a bug... lol
* if you start playing a clip, and press back or get out of it somehow, it just keeps playing.. maybe there is a way to stop that, or to capture the exit, and stop playing it
* getting to the end of file playing in preview after recording, wont let you replay unless you press pause or stop first. already have to do with what i need to do
* clicking an old clip really slow for some reason. takes awhile to popup, almost long enough to try and touch it again - moving where dialog starts didnt really help much
*/

public class HomeActivity extends Activity {

    Boolean record = false;
    List<String> filenames;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private static String mFileName = null;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //create the needed folder structure if it isnt there. need /singAlong and /singAlong/temp
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File tempfolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong/temp");
        if (!tempfolder.exists()) {
            tempfolder.mkdir();
        }

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //save it into temp, that way i can let them play it back, and save it if they want, if they save it, it will go into just singAlong/
        mFileName += "/singAlong/temp/audiorecordtemp.3gp";


        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/singAlong";
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        filenames = new ArrayList<String>();
        //countdown, so newest are at the top
        for (int i=file.length-1; i > 0; i--){
            //there is a folder in there called temp, ignore that folder
            if(!file[i].getName().equals("temp")) {
                filenames.add(file[i].getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item, android.R.id.text1, filenames);
        ListView listView = (ListView) findViewById(R.id.SavedList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //TODO: implement a longclick to allow them to delete, or update the name of the file
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                mPlayer = new MediaPlayer();
                try {
                    //set up dialog to allow them to stop the click
                    //TODO: make this look nicer. maybe a black theme, and anything else, very plain right now. could also go back to using the playpopup, and sending the mplayer or something
                    AlertDialog.Builder ask = new AlertDialog.Builder(context);
                    ask.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPlayer.release();
                            mPlayer = null;
                        }
                    });
                    ask.setMessage("Clip is playing");
                    ask.create();
                    ask.show();


                    mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/singAlong/" + filenames.get(position));
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e("error", "prepare() failed");
                }
            }
        });
    }


    //launches the default music player, instead of making a music player myself
    public void launchMusic(View view){
        Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
        startActivity(intent);
    }

    //popup some info about stuff they should know
    /*
    music button opens ur specified player
    recommend you play music through headphones, and sing along. then you can review it, and save it if you wish
    other basic stuff they should know
     */
    public void help(View view){
        return;
    }



    public void toggleRecord(View view){
        //when they click the button, flip the value of recording
        record = !record;
        //if it was not recording, then start
        //TODO: make an animation that makes this kinda pulse while its recording
        if(record){

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.d("error", "prepare() failed");
            }
            mRecorder.start();


            Toast toast = Toast.makeText(this,"recording", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(40);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 25);
            toast.show();
            //maybe put in a big toast that says recording
        }
        //if it already recording, then stop and ask if they want to save it
        //also give them a preview play back in the popup or something
        //TODO: make a popup that gives them the option to play it back. and save it as a name, by default it goes into the temp folder, this will move it from the temp folder into the normal folder, with their specified name
        else{
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            Intent intent = new Intent(this,SavePopup.class);
            startActivity(intent);




        }
    }


}
