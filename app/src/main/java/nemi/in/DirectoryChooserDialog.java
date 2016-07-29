package nemi.in;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DirectoryChooserDialog 
{
 private String m_sdcardDirectory = "";
 private Context m_context;
 private TextView m_titleView;
 
 private List<String> m_dirsQueue = new ArrayList<String>();
 private List<String> m_curdirs = null;
 ChosenDirectoryListener m_chosenDirectoryListener = null;
 
 // Callback interface for selected directory
 public interface ChosenDirectoryListener 
 {    
             public void onChosenDir(String chosenDir);
     }
 
 public DirectoryChooserDialog(Context context, ChosenDirectoryListener chosenDirectoryListener)
 {
     m_context = context;
     m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
     m_chosenDirectoryListener = chosenDirectoryListener;
 }
 
 //////////////////////////////////////////////////////////////////////
 // API: chooseDirectory() - load directory chooser dialog_pos_for_set_qty for initial
 // default sdcard directory
 //////////////////////////////////////////////////////////////////////
 
 public void chooseDirectory()
 {
     // Initial directory is sdcard directory
     chooseDirectory(m_sdcardDirectory);
 }
 
 ////////////////////////////////////////////////////////////////////////////////
 // API: chooseDirectory(String dir) - load directory chooser dialog_pos_for_set_qty for initial
 // input 'dir' directory
 ////////////////////////////////////////////////////////////////////////////////
 
 public void chooseDirectory(String dir)
 {
     File dirFile = new File(dir);
             if (! dirFile.exists() || ! dirFile.isDirectory())
             {
                 dir = m_sdcardDirectory;
             }
     
             m_dirsQueue.clear();
     
             try
             {
              final String sdcardCanonicalPath = new File(m_sdcardDirectory).getCanonicalPath();
              String dirPath = new File(dir).getCanonicalPath();
         
              while ( ! dirPath.equals(sdcardCanonicalPath) )
              {
                      m_dirsQueue.add(0, dirPath);
                  dirPath = new File(dirPath).getParent();
              }
         
          m_dirsQueue.add(0, sdcardCanonicalPath);
          m_curdirs = getDirectories(dir);
             }
             catch (IOException ioe)
             {
                 return;
             }
     
             class DirectoryOnClickListener implements OnClickListener
     {
         public void onClick(DialogInterface dialog, int item)
         {
             // Navigate into the sub-directory
             String newDir = m_dirsQueue.get(m_dirsQueue.size() - 1) + "/" +
                 ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
             m_dirsQueue.add(newDir);

             m_curdirs.clear();
             m_curdirs.addAll( getDirectories(newDir) );
             m_titleView.setText(newDir);

             ((ArrayAdapter<String>) ((AlertDialog) dialog).getListView().getAdapter()).notifyDataSetChanged();
         }
     }

             AlertDialog.Builder dialogBuilder =
             createDirectoryChooserDialog(dir, m_curdirs, new DirectoryOnClickListener());

             dialogBuilder.setPositiveButton("OK", new OnClickListener()
         {
         @Override
         public void onClick(DialogInterface dialog, int which)
         {
             // Current directory chosen
             if (m_chosenDirectoryListener != null)
             {
                 // Call registered listener supplied with the chosen directory
                 String chosenDir = m_dirsQueue.get(m_dirsQueue.size() - 1);
                 m_chosenDirectoryListener.onChosenDir(chosenDir);
             }
         }
     }).setNegativeButton("Cancel", new OnClickListener()
         {
         @Override
         public void onClick(DialogInterface dialog, int which)
         {
         }
     });

     final AlertDialog dirsDialog = dialogBuilder.create();

     dirsDialog.setOnKeyListener(new OnKeyListener()
         {
         @Override
         public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
         {
             if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
             {
                 // Back button pressed
                 if (m_dirsQueue.size() == 1)
                 {
                     // The very top level directory, do nothing
                     return false;
                 }
                 else
                 {
                     // Navigate back to an upper directory
                     m_dirsQueue.remove(m_dirsQueue.size() - 1);
                     String newDir = m_dirsQueue.get(m_dirsQueue.size() - 1);

                     m_curdirs.clear();
                     m_curdirs.addAll( getDirectories(newDir) );
                     m_titleView.setText(newDir);

                     ((ArrayAdapter<String>) ((AlertDialog) dialog).getListView().getAdapter()).notifyDataSetChanged();
                 }

                 return true;
             }
             else
             {
                 return false;
             }
         }
     });

     // Show directory chooser dialog_pos_for_set_qty
     dirsDialog.show();
 }

 private List<String> getDirectories(String dir)
 {
     List<String> dirs = new ArrayList<String>();

     try
             {
                 File dirFile = new File(dir);
                 if (! dirFile.exists() || ! dirFile.isDirectory())
                 {
                     return dirs;
                 }

                 for (File file : dirFile.listFiles())
                 {
                     if ( file.isDirectory() )
                     {
                        dirs.add( file.getName() );
                     }
                 }
             }
             catch (Exception e)
             {
             }

             Collections.sort(dirs, new Comparator<String>()
             {
                 public int compare(String o1, String o2)
                 {
                             return o1.compareTo(o2);
                     }
             });

             return dirs;
 }

 private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
         OnClickListener onClickListener)
 {
     AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);
     // Create custom TextView for AlertDialog title (current directory path),
     // thus allowing long titles to be wrapped to multiple line
     m_titleView = new TextView(m_context);
     m_titleView.setTextAppearance(m_context, android.R.style.TextAppearance_Large);
     m_titleView.setTextColor( m_context.getResources().getColor(android.R.color.white) );
     m_titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
     
     dialogBuilder.setCustomTitle(m_titleView);
     m_titleView.setText(title);
 
     ArrayAdapter<String> listAdapter = createListAdapter(listItems);
 
     dialogBuilder.setSingleChoiceItems(listAdapter, -1, onClickListener);
     dialogBuilder.setCancelable(false);
     
     return dialogBuilder;
 }
     
 private ArrayAdapter<String> createListAdapter(List<String> items)
 {
     return new ArrayAdapter<String>(m_context, android.R.layout.select_dialog_item, android.R.id.text1, items)
         {
 
             @Override
             public View getView(int position, View convertView,
                     ViewGroup parent) 
             {
                 View v = super.getView(position, convertView, parent);
                 
                 if (v instanceof TextView)
                 {
                     // Enable list item (directory) text wrapping
                     TextView tv = (TextView) v;
                     tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                     tv.setEllipsize(null);
                 }
                 
                 return v;
             }
         
         };
 }
}