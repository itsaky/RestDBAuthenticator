package com.itsaky.restdbauth;

import android.content.*;
import android.net.*;
import android.view.*;
import android.widget.*;
import java.io.*;

import android.app.ActivityManager;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class AIDEUtils {
    
    private static Context c;
    
    public AIDEUtils(Context c){
        this.c = c;
    }
    
    // these functions can be used by creating an instance of this class
    //you can get a detailed example in your app's MainActivity
    
    //Toast a short message
    public static void toast(String msg){
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }
    
    //Toast a long message
    public static void toastLong(String msg){
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }
    
    //Check if user has an active network connection
    public static boolean isNetworkConnected(){
		ConnectivityManager connectivityManager 
			= (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	//check if a string contains only numbers
	public static boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}
	
	//Write an exception to local storage. It can be used for debugging, but only if you catch an exception
	//requires access to external storage
	public static void writeLogToLocal(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String text = sw.toString();
		FileUtil.writeFile(FileUtil.getExternalStorageDir() + "/log.txt", text);
	}
	
	//format a file size in Bytes, KB, MB, GB, TB
	public static String formatFileSize(long size) {
		String hrSize = null;

		double b = size;
		double k = size/1024.0;
		double m = ((size/1024.0)/1024.0);
		double g = (((size/1024.0)/1024.0)/1024.0);
		double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

		DecimalFormat dec = new DecimalFormat("0.00");

		if ( t>1 ) {
			hrSize = dec.format(t).concat(" TB");
		} else if ( g>1 ) {
			hrSize = dec.format(g).concat(" GB");
		} else if ( m>1 ) {
			hrSize = dec.format(m).concat(" MB");
		} else if ( k>1 ) {
			hrSize = dec.format(k).concat(" KB");
		} else {
			hrSize = dec.format(b).concat(" Bytes");
		}

		return hrSize;
		
	}
}