package nemi.in;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import common.logger.Log;
import in.nemi.ncontrol.R;



/**
 * Created by Aman on 5/3/2016.
 */
public class FragmentBackup extends Fragment {

    private TextView filePath;
    private Button btnBrowse;
    private Button restoreBtn;
    private File selectedFile;
    Uri selectedUri;
    String selectedshow = null;
    String selectedRestrore = null;
    private static final int REQUEST_PICK_FILE = 302;
    private static final int MY_INTENT_CLICK = 302;
    private static final int MY_INTENT_CLICK1 = 301;
    private  static  final int MODE_WORLD_READABLE = 0001;
    DatabaseHelper databaseHelper;
    public Cursor cursor;

    private ListView lsback;
    private backup adapter;
    private List<backupview> mbackuplist;

    String[] result;
    public FragmentBackup() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_backup, container, false);
            super.onCreate(savedInstanceState);
        filePath = (TextView)rootView.findViewById(R.id.file_picker_text);
        btnBrowse = (Button) rootView.findViewById(R.id.btnBackup);
        restoreBtn = (Button)rootView.findViewById(R.id.restorebutton);
        lsback= (ListView) rootView.findViewById(R.id.btnBackuplistview);
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK1);
            }




        });
        restoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK);
            }
        });
        ListView lv = (ListView) rootView.findViewById(R.id.btnBackuplistview);

        // Convert ArrayList to array


               return rootView;
                    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == MY_INTENT_CLICK) {
                if (null == data) return;
                selectedUri = data.getData();
                //MEDIA GALLERY
                selectedRestrore = ImageFilePath.getPath(getActivity(), selectedUri);
                android.util.Log.i("File", "" + selectedRestrore);
                databaseHelper = new DatabaseHelper(getActivity(),null,null,1);
                try {
                    InputStream fis = new FileInputStream(selectedRestrore);
                    if (fis != null) {
                        InputStreamReader chapterReader = new InputStreamReader(fis);
                        BufferedReader buffreader = new BufferedReader(chapterReader);
                        String line;
                        do {
                            line = buffreader.readLine();
                            String[] result = line.split("\\,");
                            if(result.length == 5){
                                databaseHelper.addItem(result[1], result[3], Integer.parseInt(result[2]), result[4]);
                            }
                        } while (line != null);
                    }
                } catch (Exception e) {
                    Log.v("backup",String.valueOf(e));
                }
            }
            if (requestCode == MY_INTENT_CLICK1) {
                if (null == data) return;
                selectedUri = data.getData();
                //MEDIA GALLERY
                selectedshow = ImageFilePath.getPath(getActivity(), selectedUri);
                android.util.Log.i("File111", "" + selectedshow);
                try {
                    // open the file for reading
                    InputStream fis = new FileInputStream(selectedshow);

                    // if file the available for reading
                    if (fis != null) {

                        // prepare the file for reading
                        InputStreamReader chapterReader = new InputStreamReader(fis);
                        BufferedReader buffreader = new BufferedReader(chapterReader);
                        mbackuplist = new ArrayList<>();
                        String line;

                        // read every line of the file into the line-variable, on line at the time
                        do {
                            line = buffreader.readLine();
                            result = line.split("\\,");
                            mbackuplist.add(new backupview(result[0],result[7],result[9],result[8],result[3],result[5],result[6]));
                            adapter=new backup(mbackuplist,getActivity());
                            lsback.setAdapter(adapter);
                            Log.v("backupcheckup",result[3]);
                        } while (line != null);

                    }
                } catch (Exception e) {
                   Log.v("backup",String.valueOf(e));
                }
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackup:
                Log.i("file","btnbackup");
                getActivity().sendBroadcast(new Intent(getActivity(), NetworkChangeReciever.class));

            case R.id.restorebutton:
                Log.i("file1","restore");
                getActivity().sendBroadcast(new Intent(getActivity(), NetworkChangeReciever.class));

        }
    }


}






