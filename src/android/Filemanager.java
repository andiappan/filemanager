package com.iiiinfotech.filemanager;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;




public class Filemanager extends CordovaPlugin {
    public Context context;
  public Activity mActivity;
  public String dirpath = null;
	private static final String ACTION_SHOWPICKER = "showpicker";
    private CallbackContext PUBLIC_CALLBACKS = null;
	public String urlStr = "https://payments.zb.co.zw:8443/Portal_Mobility/uploadDocument";
    public static final int CAMERA_PIC_REQUEST = 1979;
	public String QuoteNo = null;
	public String scanFilename = null;
    public String ParaName = null;
    public String FlowType = null;
  public String base64ImageData = null;
  public String custId = null;
    public File uploadFile = null;

  public String fileName = null;
  public static String actValue = null;

  public String myFileName;
  private static final boolean IS_AT_LEAST_LOLLIPOP = Build.VERSION.SDK_INT >= 21;
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
      mActivity = this.cordova.getActivity();
		//context=this.cordova.getActivity().getApplicationContext();
      context = IS_AT_LEAST_LOLLIPOP ? cordova.getActivity().getWindow().getContext() : cordova.getActivity().getApplicationContext();
		PUBLIC_CALLBACKS = callbackContext;

      try {


        try {
          dirpath = context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath();
        } catch (Exception e) {
          try {
            dirpath = Environment.getDataDirectory().getAbsolutePath();
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
        checkPermissions();
        actValue = action;
        if (actValue.equalsIgnoreCase("upload")) {
          urlStr = data.getString(0);
          QuoteNo = data.getString(1);
          ParaName = data.getString(2);
          FlowType = data.getString(3);
          //new UploadTask().execute("");
        /*
          Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
          intent.addCategory(Intent.CATEGORY_OPENABLE);

          cordova.startActivityForResult((CordovaPlugin) this,intent, CAMERA_PIC_REQUEST);
            */
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            cordova.startActivityForResult((CordovaPlugin) this,intent, CAMERA_PIC_REQUEST);


          // Send no result, to execute the callbacks later
          //PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
          //pluginResult.setKeepCallback(true); // Keep callback
          return true;

        }else if (actValue.equalsIgnoreCase("savesignature")) {
          //urlStr = data.getString(0);
          base64ImageData = data.getString(0);
          custId = data.getString(1);
          FileOutputStream fos = null;
          try {
              if (base64ImageData != null) {
                  fileName = dirpath+"/signature_"+custId+".png";
                  byte[] decodedString = Base64.decode(base64ImageData.toString(), Base64.DEFAULT);
                  Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                  fos = new FileOutputStream(new File(fileName));
                  decodedImage.compress(Bitmap.CompressFormat.PNG, 90, fos);   
              }
          } catch (Exception e) {
            Log.e("TAG",e.getMessage());
          } finally {
              if (fos != null) {
                  try {
                    fos.flush();
                    fos.close();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
              }
          }
          // Send no result, to execute the callbacks later
          //PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
          //pluginResult.setKeepCallback(true); // Keep callback
          return true;
        }else if (actValue.equalsIgnoreCase("uploadsignature")) {
          urlStr = data.getString(0);
          custId = data.getString(1);
          QuoteNo = data.getString(2);
          FileOutputStream fos = null;
          try {
              if (custId != null) {
                  fileName = dirpath+"/signature_"+custId+".png";
                  
                  cordova.getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        uploadFile =  new File(fileName);
                        new UploadTask().execute("");
                      }
                  });
              }
          } catch (Exception e) {
            Log.e("TAG",e.getMessage());
          } 
          return true;
        }else if (actValue.equalsIgnoreCase("uploadscanner")) {
          urlStr = data.getString(0);
          QuoteNo = data.getString(1);
          ParaName = data.getString(2);
          FlowType = data.getString(3);
		  scanFilename = data.getString(4);
          try {
              if (QuoteNo != null) {

                  fileName = scanFilename;
                  //String profilePathLoc = RealPathUtil.
                          //fileName = "/storage/emulated/0/Android/data/com.zbmobility.salesApp/cache/1540990517177.jpg";
                  cordova.getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          try {
                              uploadFile =  new File(new URI(fileName));
                              new UploadTask().execute("");
                          } catch (URISyntaxException e) {
                              e.printStackTrace();
                          }

                      }
                  });
              }
          } catch (Exception e) {
            Log.e("TAG",e.getMessage());
          } 
          return true;
        } else {
          return false;
        }
      }catch (Exception e){
        Log.e("TAG",e.getMessage());
      }
      return false;
    }

    public String getFilePathFromURI(Uri u,String path) {
        //copy file and send new file path
        String fileName = getFileName(path);

            //File copyFile = new File(context.getExternalCacheDir() + File.separator + fileName);
            File copyFile = new File(dirpath + File.separator + fileName);
            copy(u, copyFile);
            return copyFile.getAbsolutePath();


    }
    public String getFileName(String path) {
        if (path == null) return null;
        String fileName = null;
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }
    public void copy(Uri path, File dstFile) {
        try {
            //InputStream inputStream = new FileInputStream(path);
            InputStream inputStream = context.getContentResolver().openInputStream(path);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(requestCode == CAMERA_PIC_REQUEST && resultCode == cordova.getActivity().RESULT_OK){
            try {
                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    myFileName = getFilePathFromURI(selectedImageUri,selectedImageUri.getPath());
                    uploadFile =  new File(myFileName);
                    if(uploadFile != null){
                        new UploadTask().execute("");
                    }


                }
            }catch(Exception e){

            }
            return;
        }else if(requestCode == CAMERA_PIC_REQUEST && resultCode == cordova.getActivity().RESULT_CANCELED){
            PluginResult resultado = new PluginResult(PluginResult.Status.OK, "canceled action, process this in javascript");
            resultado.setKeepCallback(true);
            PUBLIC_CALLBACKS.sendPluginResult(resultado);
            return;
        }
        // Handle other results if exists.
        super.onActivityResult(requestCode, resultCode, data);
    }

	class UploadTask extends AsyncTask<String, Void, String> {
        private DataOutputStream request;
        private final String boundary =  "*****";
        private final String crlf = "\r\n";
        private final String twoHyphens = "--";
        private Exception exception;
        private ProgressDialog pbar = null;


     @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pbar = createProgressDialog(cordova);
      pbar.setMessage("Please wait!");
      pbar.setCancelable(false);
      pbar.show();

    }
    @SuppressLint("InlinedApi")
    private ProgressDialog createProgressDialog(CordovaInterface cordova) {
      int currentapiVersion = android.os.Build.VERSION.SDK_INT;
      if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        return new ProgressDialog(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
      } else {
        return new ProgressDialog(cordova.getActivity());
      }
    }
    protected String doInBackground(String... urls) {
            String result = "";

            try {
                HttpClient client = new MyHttpClient(context);

                HttpPost get = new HttpPost(urlStr);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                builder.addBinaryBody(
                        "file",
                        new FileInputStream(uploadFile),
                        ContentType.APPLICATION_OCTET_STREAM,
                        uploadFile.getName());
                //builder.addTextBody("fileName", "testfile");
                builder.addTextBody("fileName",uploadFile.getName());
              if (actValue.equalsIgnoreCase("upload") || actValue.equalsIgnoreCase("uploadscanner")) {
                builder.addTextBody("QuoteNo", QuoteNo);
                builder.addTextBody("ParaName", ParaName);
                builder.addTextBody("FlowType", FlowType);
              }else if (actValue.equalsIgnoreCase("uploadsignature")) {
                  builder.addTextBody("custId", custId);
                  builder.addTextBody("QuoteNo", QuoteNo);
              }
//
                HttpEntity entity = builder.build();
                get.setEntity(entity);

                HttpResponse getResponse = client.execute(get);

                HttpEntity responseEntity = getResponse.getEntity();
                result = EntityUtils.toString(responseEntity);

            }catch (Exception e){
                result = e.getMessage();
            }

            return result;
        }

        protected void onPostExecute(String res) {
          if(pbar != null){
            pbar.dismiss();
          }
		  if (actValue.equalsIgnoreCase("uploadsignature")){
			  if(uploadFile != null){
				   if(uploadFile.exists()){
						uploadFile.delete();
					}
			  }
		  }
            if (actValue.equalsIgnoreCase("upload")){
                if(uploadFile != null){
                    if(uploadFile.exists()){
                        uploadFile.delete();
                    }
                }
            }
          Toast.makeText(context,res,Toast.LENGTH_LONG).show();
          PluginResult resultado = new PluginResult(PluginResult.Status.OK, "Success");
          resultado.setKeepCallback(true);
          PUBLIC_CALLBACKS.sendPluginResult(resultado);

        }
    }

	public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> listPermissionsNeeded = new ArrayList<String>();

            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (context.checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }

            if (!listPermissionsNeeded.isEmpty()) {
              cordova.requestPermissions((CordovaPlugin) this,111,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]));;
            }

        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
