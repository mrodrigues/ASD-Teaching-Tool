package com.example.asdteachingtool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;

import android.util.Log;

public class FileUtils {
	
	private static final String LOG_TAG = "FileUtils";
	
	public static boolean copyFile(File src, File dst) {
	    boolean returnValue = true;

	   FileChannel inChannel = null, outChannel = null;

	    try {

	        inChannel = new FileInputStream(src).getChannel();
	        outChannel = new FileOutputStream(dst).getChannel();

	   } catch (FileNotFoundException fnfe) {

	        Log.d(LOG_TAG, "inChannel/outChannel FileNotFoundException");
	        fnfe.printStackTrace();
	        return false;
	   }

	   try {
	       inChannel.transferTo(0, inChannel.size(), outChannel);

	   } catch (IllegalArgumentException iae) {

	         Log.d(LOG_TAG, "TransferTo IllegalArgumentException");
	         iae.printStackTrace();
	         returnValue = false;

	   } catch (NonReadableChannelException nrce) {

	         Log.d(LOG_TAG, "TransferTo NonReadableChannelException");
	         nrce.printStackTrace();
	         returnValue = false;

	   } catch (NonWritableChannelException nwce) {

	        Log.d(LOG_TAG, "TransferTo NonWritableChannelException");
	        nwce.printStackTrace();
	        returnValue = false;

	   } catch (ClosedByInterruptException cie) {

	        Log.d(LOG_TAG, "TransferTo ClosedByInterruptException");
	        cie.printStackTrace();
	        returnValue = false;

	   } catch (AsynchronousCloseException ace) {

	        Log.d(LOG_TAG, "TransferTo AsynchronousCloseException");
	        ace.printStackTrace();
	        returnValue = false;

	   } catch (ClosedChannelException cce) {

	        Log.d(LOG_TAG, "TransferTo ClosedChannelException");
	        cce.printStackTrace(); 
	        returnValue = false;

	    } catch (IOException ioe) {

	        Log.d(LOG_TAG, "TransferTo IOException");
	        ioe.printStackTrace();
	        returnValue = false;


	    } finally {

	         if (inChannel != null)

	            try {

	               inChannel.close();
	           } catch (IOException e) {
	               e.printStackTrace();
	           }

	        if (outChannel != null)
	            try {
	                outChannel.close();
	           } catch (IOException e) {
	                e.printStackTrace();
	           }

	        }

	       return returnValue;
	    }
}
