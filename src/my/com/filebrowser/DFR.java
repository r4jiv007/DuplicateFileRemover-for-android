package my.com.filebrowser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.Stack;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class to show startup activity 
 * 
 * 
 */


@SuppressLint({ "ParserError", "ParserError", "ParserError", "ParserError", "ParserError" })
public class DFR extends Activity{

	public static  ListView filelist ;
	public static  TextView path,ftlook;
	public static String DIR;
	public static File file0;
	public static File[] filearray;
	public static DFR Active_Instance;
	public static ArrayList<String> prevPath= new ArrayList<String>();
	static Button select,process ;
	static Dialog show_files ;
	static Button back ;
	
	//public static File Dir0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dfr);
		select = (Button)findViewById(R.id.fileselct);
		process =(Button)findViewById(R.id.process);
		ftlook= (TextView)findViewById(R.id.ftlook);
		Active_Instance = this;
		
		ftlook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int n = DataHolder.OnlyFiles.size();
				if(n>0){
					for(int i = 0;i<n;i++){
						Toast.makeText(getApplicationContext(), DataHolder.OnlyFiles.get(i).getFile().getName().toString(), 2000).show();
					}
				}else if(n==0)
					Toast.makeText(getApplicationContext(), "size is 0", 2000).show();
				
				Toast.makeText(getApplicationContext(), String.valueOf(DataHolder.Dcounter), 2000).show();
				
			}
		});
		select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*DataHolder.dirStack.removeAllElements();
				DataHolder.OnlyFiles.clear();*/
				File root = new File("/");
				
				showDialog(root);
			}
		});
		
		
		process.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// new TnC(Active_Instance, Dir0).execute();
				Intent serviceIntent = new Intent(getApplicationContext(), BService.class);
				serviceIntent.putExtra("dir0", DIR.toCharArray());
				startService(serviceIntent);
				//finish();
			}
		});
	
	}
	
	
	
	
	
	
	/**
	 * method to list the content of the dir in a order
	 * @param file2list
	 */
	
	public void allinOrder(File file2list){
		File[] allfiles;
		Runtime.getRuntime().gc();
		ArrayList<File> onlyfiles= new ArrayList<File>();
		ArrayList<File> onlydirs = new ArrayList<File>();
		if(file2list.listFiles()!=null){
			allfiles= file2list.listFiles();
			for(File selfile: allfiles){
				if(selfile.isDirectory())
					onlydirs.add(selfile);
				
			}
			 onlydirs.addAll(onlyfiles);
			 Collections.sort(onlydirs,NameFileComparator.NAME_COMPARATOR);
			 filelist.setAdapter(new FileAdapter(onlydirs,file2list.getAbsolutePath()));
				 
			 
		}
	}
		
	
	/**
	 * method to show directory selection dialog 
	 * accepts an argument to list its content. should be directory 
	 * @param filetolist
	 */
	
	public void showDialog(File filetolist){
		 show_files = new Dialog(DFR.this);
		show_files.requestWindowFeature(Window.FEATURE_NO_TITLE);
		show_files.setContentView(R.layout.browser);
		show_files.setCancelable(true);
		filelist = (ListView)show_files.findViewById(R.id.filelist);
		path = (TextView)show_files.findViewById(R.id.path);
	    back = (Button)show_files.findViewById(R.id.back);
		back.setEnabled(false);
			
		allinOrder(filetolist);
		
		back.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!path.getText().toString().equals("/")){
					//filelist.setAdapter(new FileAdapter(new File(prevPath.get(prevPath.size()-1)).listFiles(), prevPath.get(prevPath.size()-1)));
				allinOrder(new File(prevPath.get(prevPath.size()-1)));
			//super.onBackPressed();
					prevPath.remove(prevPath.size()-1);
					if(path.getText().toString().equals("/")){
						back.setEnabled(false);
					}
			}else{
				show_files.dismiss();
			}
			
			}
		});
	
		
		show_files.show();
	}
	
	
	/**
	 * Adapter class to set content to listview extends abstract BaseAdapter class 
	 * @author rajiv
	 *
	 */
	
	public class FileAdapter extends BaseAdapter{

		//File[] files ;
		ArrayList<File>files;
		public FileAdapter(ArrayList<File>filelist,String fpath){
			path.setText(fpath);
			this.files=filelist;
		}
		/**
		 * 
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return files.toArray().length;
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
			View v=LayoutInflater.from(getApplicationContext()).inflate(R.layout.filename, null);	
			
			ImageView img = (ImageView)v.findViewById(R.id.img);
			TextView filename = (TextView)v.findViewById(R.id.fname);
			LinearLayout namelt=(LinearLayout)v.findViewById(R.id.namelayout);
			
			
			
			namelt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try{
						if(files.get(position).isDirectory()&&files.get(position).canRead()){
							prevPath.add(path.getText().toString());
							//filelist.setAdapter(new FileAdapter(files[position].listFiles(),files[position].getAbsolutePath()));

							Runtime.getRuntime().gc();
							back.setEnabled(true);
							allinOrder(files.get(position));
						}else if(!files.get(position).canRead()&&files.get(position).isDirectory()){ 
							Toast.makeText(getApplicationContext(),"Access Denied", 5000).show();
						}
					}catch(NullPointerException e){
						
						Toast.makeText(getApplicationContext(), "Error opening folder", 5000).show();
						prevPath.remove(prevPath.size()-1);
						//filelist.setAdapter(new FileAdapter(new File(prevPath.get(prevPath.size()-1)).listFiles(), prevPath.get(prevPath.size()-1)));

						Runtime.getRuntime().gc();
						allinOrder(new File(prevPath.get(prevPath.size()-1)));
						
					}
				}
			});
			
			namelt.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					
					final AlertDialog alertDialog = new AlertDialog.Builder(
	                        DFR.this).create();
	 
					alertDialog.setTitle("DFR");
	 
					alertDialog.setMessage("Select "+files.get(position).getName() +" !!");
					alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
					alertDialog.setButton2("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
	                // Write your code here to execute after dialog closed
							DIR=files.get(position).getAbsolutePath();
							
							Toast.makeText(getApplicationContext(), "You selected "+files.get(position).getName(), Toast.LENGTH_LONG).show();
							ftlook.setText(DIR);
							show_files.dismiss();
						}
					});
	 
					// 	Showing Alert Message
						alertDialog.show();
					return false;
				}
			});
			
			filename.setText(files.get(position).getName());
			
			if(files.get(position).isDirectory()){
				img.setImageResource(R.drawable.foldericon);
			}else{
				img.setImageResource(R.drawable.fileicon);
			}
				
			
			return v;
		}
		
	}



	@SuppressWarnings("unused")
	private  class TnC extends AsyncTask<Void,Void,Void>{
		

		public Context _context;
		public File dir0;
		public Stack<File> localStack;
		public ArrayList<FileX> localList;
		FileX file2;
		public  ProgressDialog pgd1;
		 ArrayList<Integer> Index2Del= new ArrayList<Integer>();
		int size;
		public TnC(Context context,File file){
			_context=context;
			dir0=file;
			/*localStack=DataHolder.dirStack;
			localList = DataHolder.OnlyFiles;*/
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pgd1.dismiss();
			Intent intent = new Intent(_context, AllFiles.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			_context.startActivity(intent);
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
			
      	  pgd1=ProgressDialog.show(Active_Instance,"Please Wait", "Loading Files");
     	 
     	 pgd1.setCancelable(false);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//listAllfiles();
			//findDuplicates();
			//displayNotification();
			
			
			
			return null;
		}
		
/*
		public void findDuplicates(){
			boolean Dfound = false;
			int i =0;
			try{
				while(!localList.isEmpty()){
					ArrayList<File>forDmap= new ArrayList<File>();
				size = localList.size();
				FileX file1 = localList.get(0); 
				
				for(i = 0;i<size;i++){
					file2=localList.get(i);
					if(file1.getSize()==file2.getSize()){
						if(file1.isHashed()==false){
							calculateHash(file1);
						}		
						if(file2.isHashed()==false){
							calculateHash(file2);
						}
						
						if(file1.getHash_String().equals(file2.getHash_String())){
							Dfound=true;
							DataHolder.Dcounter++;
							forDmap.add(file2.getFile());
							Index2Del.add(i);
						}
					}
				}
				
				if(Dfound){
					DataHolder.FilesWithD.add(file1.getFile());
					forDmap.add(0,file1.getFile());
					DataHolder.DMAP.put(file1.getFile().getAbsolutePath(), new ArrayList(forDmap));
					forDmap.clear();
					Dfound=false;
					localList.remove(0);
					for(int j = 0;j<Index2Del.size();j++){
						localList.remove(Index2Del.get(j));
					}
					Index2Del.clear();
				}
				else{
					localList.remove(0);
					
				}
				
				
				
			}
			}catch(Exception e){
				
			}
		}
		
		public void listAllfiles(){
			localStack.push(dir0);
			
			while(localStack.size()!=0){
			
				file0 = localStack.pop();
				filearray=file0.listFiles();
				
				for(File lookFile : filearray){
					if(lookFile.isFile() && lookFile.canRead()){
						FileX filetoadd = new FileX(lookFile);
						try {
							DataHolder.calculateHash(filetoadd);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						localList.add(filetoadd);
					}
					else if(lookFile.isDirectory()&& lookFile.canRead()){
						localStack.push(lookFile);
					}
				}
			
			}
		}
		public void displayNotification(){
			NotificationManager nm = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
				Intent i = new Intent(getApplicationContext(),AllFiles.class);
				PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
				Notification nf = new Notification(R.drawable.ic_launcher,"Duplicate File Remover\n"+DataHolder.Dcounter+" duplicate files found",System.currentTimeMillis());
				nf.setLatestEventInfo(getApplicationContext(), "Duplicate File Remover", DataHolder.Dcounter+" duplicate files found",pi);
				nf.vibrate = new long[]{100,250,100,500};
				nm.notify(1,nf);
					
				
		}
		
		public void calculateHash(FileX file) throws Exception{

			MessageDigest algorithm = MessageDigest.getInstance("MD5");
	        FileInputStream     fis = new FileInputStream(file.getFile());
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        DigestInputStream   dis = new DigestInputStream(bis, algorithm);

	        // read the file and update the hash calculation
	        while (dis.read() != -1);

	        // get the hash value as byte array
	        byte[] hash = algorithm.digest();
	        file.setHashed(true);
	        file.setHash_String(byteArray2Hex(hash));
	    }

	    private  String byteArray2Hex(byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash) {
	            formatter.format("%02x", b);
	        }
	        return formatter.toString();
	    }
	}
*/
	}
}