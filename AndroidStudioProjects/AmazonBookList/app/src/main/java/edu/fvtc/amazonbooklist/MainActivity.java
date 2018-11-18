package edu.fvtc.amazonbooklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "MyDEBUG";
    // Needs to be a URL
    public final static String XML_FILE_DIR = "http://itweb.fvtc.edu/foote/Android/AmazonXML/";
    public final static String XML_FILE_LIST = XML_FILE_DIR + "FileList.txt";

    ImageView img;

    ArrayList<String> authors;
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    Spinner spinAuthors;
    Spinner spinTitles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String sAuthor = getContent(XML_FILE_LIST);

        Log.println(Log.DEBUG, TAG, sAuthor);

        spinAuthors = (Spinner)findViewById(R.id.spinAuthors);
        spinAuthors.setOnItemSelectedListener(this);

        spinTitles = (Spinner)findViewById(R.id.spinTitles);
        spinTitles.setOnItemSelectedListener(this);

        ArrayAdapter<String> authorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, authors);
        authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAuthors.setAdapter(authorAdapter);

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTitles.setAdapter(titleAdapter);


        

    }

    public ArrayList<String> getLines(String sInput)
    {
        ArrayList<String> arLines = new ArrayList<String>();

        try
        {
            StringReader stringReader = new StringReader(sInput);
            BufferedReader br = new BufferedReader(stringReader);

            String s;

            while ((s = br.readLine()) != null)
            {
                arLines.add(s);
            }
            br.close();
            stringReader.close();

        }
        catch(Exception ex)
        {
            Toast.makeText(this, "Request Failed : "  + ex.getMessage(), Toast.LENGTH_LONG).show();

        }
        return arLines;

    }


    private String getContent(String xmlFileList) {
        String responseBody;
        try
        {
            HttpClient client = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpGet getMethod = new HttpGet(XML_FILE_DIR);
            responseBody = client.execute(getMethod, responseHandler);
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Request Failed : "  + ex.getMessage(), Toast.LENGTH_LONG).show();
            return "Request Failed : " + ex.getMessage();

        }

        return responseBody;

    }

    public ArrayList<String> getTitles(String sXML)
    {
        ArrayList<String> al = new ArrayList<String>();
        try
        {
            // Parse the XML
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = db.parse(new InputSource(new StringReader(sXML)));
            NodeList nlDetails = doc.getElementsByTagName("Details");
            int numDetails = nlDetails.getLength();
            String str = String.format("%d details", numDetails);

            Log.println(Log.DEBUG, TAG, str);

            // Traverse detail elements
            for (int k= 0; k < numDetails; k++)
            {
                Element nDetails = (Element)nlDetails.item(k);
                NodeList nlProductName = nDetails.getElementsByTagName("ProductName");
                Node nProductName = nlProductName.item(0);
                String sProductName = nProductName.getFirstChild().getNodeValue();

                NodeList nlImage = nDetails.getElementsByTagName("ImageUrlLarge");
                Node nImage = nlImage.item(0);
                String sImage = nImage.getFirstChild().getNodeValue();

                String s = String.format("Title %d is [%s] [%s]", k, sProductName, sImage);
                Log.println(Log.DEBUG, TAG, s);
                al.add(sProductName);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Request Failed : "  + ex.getMessage(), Toast.LENGTH_LONG).show();

        }
        return al;

    }

    public ArrayList<String> getImages(String sXML)
    {
        ArrayList<String> al = new ArrayList<String>();
        try
        {
            // Parse the XML
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = db.parse(new InputSource(new StringReader(sXML)));
            NodeList nlDetails = doc.getElementsByTagName("Details");
            int numDetails = nlDetails.getLength();
            String str = String.format("%d details", numDetails);

            Log.println(Log.DEBUG, TAG, str);

            // Traverse detail elements
            for (int k= 0; k < numDetails; k++)
            {
                Element nDetails = (Element)nlDetails.item(k);

                NodeList nlImage = nDetails.getElementsByTagName("ImageUrlMedium");
                Node nImage = nlImage.item(0);
                String sImage = nImage.getFirstChild().getNodeValue();

                String s = String.format("Title %d is [%s]", k, sImage);
                Log.println(Log.DEBUG, TAG, s);
                al.add(sImage);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Request Failed : "  + ex.getMessage(), Toast.LENGTH_LONG).show();

        }
        return al;

    }




    public Bitmap getImageBitmap(String url)
    {
        Bitmap bm = null;

        try
        {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Error getting bitmap : "  + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.println(Log.DEBUG, TAG, "Error getting bitmap : "  + ex.getMessage());
        }
        return bm;

    }






    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        try
        {

            if (parent.getId() == R.id.spinAuthors)
            {
                // Get the XML
                String sXML = getContent(XML_FILE_DIR + authors.get(position));
                titles = getTitles(sXML);
                images = getImages(sXML);

                ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titles);
                titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinTitles.setAdapter(titleAdapter);

            }
            else
            {

                img = (ImageView)findViewById(R.id.img);
                String sPath = images.get(position);
                Log.println(Log.DEBUG, TAG, sPath);
                img.setImageBitmap(getImageBitmap(sPath));
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Request Failed : "  + ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
