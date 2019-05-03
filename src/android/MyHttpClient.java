package com.iiiinfotech.filemanager;

import android.content.Context;
import android.content.res.AssetManager;


import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;



public class MyHttpClient extends DefaultHttpClient {

    final Context context;

    public MyHttpClient(Context context) {
        this.context = context;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        String result = "";
        AssetManager assManager = context.getAssets();
        SSLContext sc;
        InputStream is2 = null;
        InputStream is3 = null;
        SchemeRegistry registry = new SchemeRegistry();
         try {

          is2 = context.getResources().openRawResource(context.getResources().getIdentifier("zb","raw", context.getPackageName()));
          is3 = context.getResources().openRawResource(context.getResources().getIdentifier("zbmobility","raw", context.getPackageName()));

        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        InputStream caInput = new BufferedInputStream(is3);
        CertificateFactory cf = null;

        java.security.cert.Certificate ca;
        try {
            cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(is2, "infotech@1".toCharArray());
            keyStore.setCertificateEntry("ca", ca);
            //String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            //TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            //tmf.init(keyStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, "infotech@1".toCharArray());


        MySSLSocketFactory sf = new MySSLSocketFactory(keyStore,keyManagerFactory);
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        registry.register(new Scheme("https", (SocketFactory) sf, 8443));
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return new SingleClientConnManager(getParams(), registry);
    }


}