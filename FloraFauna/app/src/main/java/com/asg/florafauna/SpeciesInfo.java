package com.asg.florafauna;

/**
 * Created by steven on 2/8/18.
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.*;

public class SpeciesInfo
{
    String scientificName;
    String commonName;
    String eolLink;
    String description;
    String imageLink;
    String done;
    String dispError;

    //initializer for when only passed name(Search results from bison)
    public SpeciesInfo(final Context context, String name)
    {
        this.scientificName=name;
        setFromEOL(context, name);
    }
    //initializer for when passed eol link, skips first search
    //unused for now
    /*
    public SpeciesInfo(String name, String link)
    {
        this.scientificName=name;
        this.eolLink=link;
    }*/

    //pull relevant info from the search page and from the eol information page
    //all the description="*" lines are for tracking where I am getting to in the program
    private void setFromEOL(final Context context, String name)
    {
        String query=eolQuery(name);
        Log.i("query",query);
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        description="\nIn setFromEOL";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, query, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                description+="\nin onResponse";
                try
                {
                    description+="\nin onResponse try block";

                    //this has been tested, gives the data that I am looking for
                    JSONObject results = response.getJSONArray("results").getJSONObject(0);
                    Log.i("linkResponse",results.getString("link"));
                    eolLink = results.getString("link");

                    //the log updates, but not every time?
                }
                catch(Exception e)
                {
                    Log.e("Error: ", e.toString());
                    dispError=e.toString();
                    description+="\nin onResponse catch block";
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("Error: ", error.toString());
                dispError=error.toString();
                description+="\nin onErrorResponse";
            }
        });
        requestQueue.add(searchRequest);
        //description="finished with searchRequest";

    }
    /*private void setFromEOL(String name)
    {
        try
        {
            String query=eolQuery(name);

            String json = Jsoup.connect(query).ignoreContentType(true).execute().parse().toString();
            eolLink = json.substring(json.indexOf("http"),json.indexOf("?"));

            String page = Jsoup.connect(eolLink).timeout(10000).execute().parse().toString();
            int start = page.indexOf("</h4>",page.indexOf("<h4>Description"))+6;
            int stop = page.indexOf("\n", start);
            description = page.substring(start,stop);

            start = page.indexOf("<title>")+7;
            stop = page.indexOf("-",start);
            commonName = page.substring(start,stop);

            start = page.indexOf("<img alt");
            start = page.indexOf("src",start)+5;
            stop = page.indexOf("\"",start);
            imageLink = page.substring(start,stop);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            error=e.toString();
        }
    }*/

    //format query to search for an animal(exact name) on eol
    private String eolQuery(String name)
    {
        String first="http://eol.org/api/search/1.0.json?q=";
        String last="&page=1&exact=true&filter_by_taxon_concept_id=&filter_by_hierarchy_entry_id=&filter_by_string=&cache_ttl=";
        name=name.replaceAll(" ","+");
        return first+name+last;
    }

    //format query to search for an animal on gbif
    private String gbifQuery(String name)
    {
        String first = "http://api.gbif.org/v1/species?name=";
        name=name.replaceAll(" ","+");
        return first+name;
    }

    //getters to return values from the object
    public String getScientificName()
    {
        return scientificName;
    }
    public String getCommonName()
    {
        return commonName;
    }
    public String getDescription(){return description;}
    public String getImageLink(){return imageLink;}
    public String getEolLink(){return eolLink;}
    public String getError(){return dispError;}
    public String getDone(){return done;}
}
