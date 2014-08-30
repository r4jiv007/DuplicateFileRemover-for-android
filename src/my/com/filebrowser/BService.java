package my.com.filebrowser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Stack;

import org.apache.commons.io.comparator.SizeFileComparator;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


@SuppressLint({ "ParserError", "ParserError", "ParserError", "ParserError", "ParserError", "ParserError", "ParserError" })
public class BService extends Service {

	static String DIR0;
	//private static ArrayList<FileX> OnlyFiles = new ArrayList<FileX>();
	
	private static Stack<File> dirStack = new Stack<File>();
	private static File file0;
	private static File[] filearray;
	
	private static int size=0;
	private static FileX file1,file2;
	
	private static ArrayList<File> sOnlyFiles = new ArrayList<File>();
	private ArrayList<File> Duplicates;
	private static String hash1,hash2;
	private  ArrayList<Integer> Index2Del= new ArrayList<Integer>();
	private ArrayList<FileX> localOF = new ArrayList<FileX>();
	private static HashMap<String, ArrayList<File> >Dmap =new HashMap <String, ArrayList<File>>();
			//private Iterator<FileX> baseItr;
	private ArrayList<String> HasDuplicates = new ArrayList<String>();
	private String filename;
	
	private FileOutputStream fos ;
	private FileInputStream fis;
	private ObjectOutputStream obs;
	private ObjectInputStream obsi;
	
	@Override
	
	public void onCreate() {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), "service created",5000).show();
		
		
		super.onCreate();
		
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		DIR0 = new String( intent.getCharArrayExtra("dir0"));
		DataHolder.Dcounter =0;
	//	baseItr = localOF.iterator();
		listAllfiles();
	
				
		Collections.sort(sOnlyFiles, SizeFileComparator.SIZE_COMPARATOR);
		formFilex();
		
		try {
			findDuplicates();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//serializeList("allfiles.bin",localOF);
		serializeList("Keys.bin",HasDuplicates);
		serializeList("DMAP.bin",Dmap);
		
		displayNotification();
		this.stopSelf();
		return super.onStartCommand(intent, flags, startId);
		
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), "service destroyed",5000).show();
		super.onDestroy();
	}

	private void formFilex(){
		int len = sOnlyFiles.size();
	//	Toast.makeText(getApplicationContext(), ""+len, 2000).show();
		for(int i = 0;i<len;i++){
			localOF.add(new FileX(sOnlyFiles.get(i)));
		}
	}


	private void findDuplicates() throws Exception{
		boolean Dfound = false;
		int i =0,j=0;
		int len = 0;
		long file1size=0,file2size=0;
		
		while(localOF.size()>0){
			
			file1=localOF.get(0);
			file1size=file1.getSize();
			len = localOF.size();
			for(j=1;j<len;j++){
				file2 = localOF.get(j);
				file2size= file2.getSize();
				if(file1size!=file2size){
					localOF.remove(0);
					break;
				}
				else if(file1size==file2size){
					
					if(file1.isHashed()==false){
						calculateHash(file1);
					}		
					if(file2.isHashed()==false){
						calculateHash(file2);
					}
					
					if(file1.getHash_String().equals(file2.getHash_String())){
						Dfound = matchByte(file1.getFile(),file2.getFile());
						if(Dfound){
							Duplicates.add(file2.getFile());
							Index2Del.add(Integer.valueOf(j));
							
						}else{
							break;
						}
					}
				}
			}
			
			if(Dfound){
				String key = file1.getFile().getAbsolutePath();
				HasDuplicates.add(key);
				Dmap.put(key, new ArrayList<File>(Duplicates));
			}
		//	i++;
			removeIndex();
		}
	}
	
	
	private void removeIndex(){
		int len = Index2Del.size();
		for(int i = 0;i<len ;i++){
			localOF.remove(Index2Del.get(i).intValue());
		}
		Index2Del.clear();
		Duplicates.clear();
	}
	
	
	private boolean matchByte(File file1,File file2){
		byte[] buff1 = new byte[100];
		byte[] buff2 = new byte[100];
		FileInputStream fin1=null,fin2=null;
		try {
		 fin1 = new FileInputStream(file1);
			fin2 = new FileInputStream(file2);
			while(fin1.read(buff1)!=-1 ){
				fin2.read(buff2);
				if(!Arrays.equals(buff1,buff2)){
					fin2.close();
					fin1.close();
					return false;
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fin1.close();
				fin2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return true;
	}

	
	private void listAllfiles(){
		dirStack.push(new File(DIR0));
		
		while(dirStack.size()!=0){
		
			file0 = dirStack.pop();
			filearray=file0.listFiles();
			
			for(File lookFile : filearray){
				if(lookFile.isFile() && lookFile.canRead()){
					sOnlyFiles.add(lookFile);
					DataHolder.Dcounter++;
				}
				else if(lookFile.isDirectory()&& lookFile.canRead()){
					dirStack.push(lookFile);
				}
			}
		
		}
	}
	
private void serializeList(String filename,Object obj)  {
	
	File dir = getFilesDir();
	File file1 = new File(dir,filename);
	
	try {
		// fos = openFileOutput(fname, MODE_PRIVATE);
		
		fos = new FileOutputStream(file1);
		obs = new ObjectOutputStream(fos);
		obs.writeObject(obj);
		obs.flush();
		obs.close();
		fos.close();
		Toast.makeText(getApplicationContext(), file1.getAbsolutePath()+" "+file1.length(), 5000).show();
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		try{
			obs.close();
			fos.close();
		}catch(Exception e){}
		
	}
}
	
	



	private void displayNotification(){
		NotificationManager nm = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
			Intent i = new Intent(getApplicationContext(),AllFiles.class);
		//	Bundle listfile = new Bundle();
		//listfile.putStringArrayList("listfile", sOnlyFiles);
			//i.putExtra(name, value)
			//i.putExtra("filelist", sOnlyFiles);
			i.putExtra("key", 2225639);
			PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
			Notification nf = new Notification(R.drawable.ic_launcher,"Duplicate File Remover\n"+ DataHolder.Dcounter+"  files found",System.currentTimeMillis());
			
			nf.setLatestEventInfo(getApplicationContext(), "Duplicate File Remover",  DataHolder.Dcounter+" files found",pi);
			nf.vibrate = new long[]{100,250,100,500};
			nm.notify(1,nf);
				
			
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * method to calculate file hash using md5hash algorithm
	 * 
	 */
	private void calculateHash(FileX file) throws Exception{

		MessageDigest algorithm = MessageDigest.getInstance("MD5");
        FileInputStream     fis = new FileInputStream(file.getFile());
        BufferedInputStream bis = new BufferedInputStream(fis);
        DigestInputStream   dis = new DigestInputStream(bis, algorithm);

        // read the file and update the hash calculation
        while (dis.read() != -1);

        // get the hash value as byte array
        byte[] hash = algorithm.digest();
        file.setHash_String((byteArray2Hex(hash)));
    }

    private  String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
	
}
