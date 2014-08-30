package my.com.filebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint({ "ParserError", "ParserError", "ParserError" })
public class AllFiles extends ListActivity{

	FileInputStream fis ;
	ObjectInputStream obs;
	
	private ArrayList<String> HasDuplicates = new ArrayList<String>();
	private static HashMap<String, ArrayList<File> > Dmap2 =new HashMap <String, ArrayList<File>>();
	
	public static ArrayList<File> ListoFiles;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		
			stopService(new Intent(this, BService.class));
			
			deserializeList();
			deserializeMAP();
			//File file[] = getApplicationContext().getFilesDir().listFiles();
				
		/*	for(int i=0;i<file.length;i++){
				Toast.makeText(getApplicationContext(), file[i].getName()+"  "+file[i].length(), 2000).show();
			}*/
		//	stopService(new Intent(this, BgService.class));
			setListAdapter(new OnlyFilesAdapter());
			//Bundle filelist = getIntent().getExtras();
		
			long key = getIntent().getLongExtra("key", 0);
			//Toast.makeText(getApplicationContext(), String.valueOf(key), 2000).show();
			
			super.onCreate(savedInstanceState);
			
			
		}

public void deserializeList(){
	Object tolist =null;
	String filename ="Keys.bin";
	File file = new File(getFilesDir(),filename);

	try {
		
		//fis = openFileInput(filename);
		fis = new FileInputStream(file);
		if(fis!=null){
			obs = new ObjectInputStream(fis);
			tolist = obs.readObject();
			@SuppressWarnings("unchecked")
			ArrayList<String> tolist2 = (ArrayList<String>)tolist;
			HasDuplicates = tolist2;
			obs.close();
			fis.close();
			Toast.makeText(getApplicationContext(), ""+ HasDuplicates.size(), 5000).show();
		}else
			Toast.makeText(getApplicationContext(), "file not available", 2000).show();
		
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}

public void deserializeMAP(){
	Object tolist =null;
	String filename ="DMAP.bin";
	File file = new File(getFilesDir(),filename);

	try {
		
	//	fis = openFileInput(filename);
		fis = new FileInputStream(file);
		if(fis!=null){
			obs = new ObjectInputStream(fis);
			tolist = obs.readObject();
			@SuppressWarnings("unchecked")
			HashMap<String, ArrayList<File>> temp = (HashMap<String, ArrayList<File>>)tolist;
			Dmap2 = temp;
			obs.close();
			fis.close();
			Toast.makeText(getApplicationContext(), ""+ Dmap2.size(), 5000).show();
		}else
			Toast.makeText(getApplicationContext(), "file not available", 2000).show();
		
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	

	
}
		
		public class OnlyFilesAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return HasDuplicates.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.allfile, null);	
				
			
				final TextView filename = (TextView)v.findViewById(R.id.allfile);
				filename.setText(HasDuplicates.get(position));
				filename.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int len = Dmap2.get(filename.getText()).size();
						ListoFiles = Dmap2.get(filename.getText());
							
						for(int i=0;i<len;i++){
							Toast.makeText(getApplicationContext(), ""+ListoFiles.get(position), 2000).show();
						}
					};
				});
				return v;
			}
			
		}

		
		
}
