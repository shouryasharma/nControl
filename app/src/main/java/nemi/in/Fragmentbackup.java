package nemi.in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import common.logger.Log;
import in.nemi.ncontrol.R;


/**
 * Created by Aman on 5/3/2016.
 */
public class Fragmentbackup extends Fragment {

    private TextView filePath;
    private Button btnBrowse;
    private Button restoreBtn;
    private File selectedFile;
    private static final int REQUEST_PICK_FILE = 1;
    public Fragmentbackup() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_backup, container, false);
            super.onCreate(savedInstanceState);
        filePath = (TextView)rootView.findViewById(R.id.file_picker_text);
        btnBrowse = (Button) rootView.findViewById(R.id.btnBackup);
        Button restorBtn = (Button)rootView.findViewById(R.id.restorebutton);
        restorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "restore buton clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("Documents/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK_FILE);
            }




        });
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Toast.makeText(getActivity().getApplicationContext(), "browse button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("Documents/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK_FILE);
            }
        });

               return rootView;
                    }


                }

