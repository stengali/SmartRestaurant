package com.example.tabexample;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;



public class Menu {

    public   ArrayList<String> hotelNameToMenuItems(String hotelname,HashMap<String,String> hotelNameToMenu) throws Exception {

        ArrayList<String> list = new ArrayList<String>();
        System.out.println(hotelNameToMenu.get(hotelname));

        JSONArray menus = new JSONArray(hotelNameToMenu.get(hotelname));
        for (int i = 0; i < menus.length(); i++) {
            JSONObject menu = (JSONObject) menus.get(i);

            JSONArray sections = new JSONArray(menu.get("sections").toString());
            for (int j = 0; j < sections.length(); j++) {
                JSONObject section = (JSONObject) sections.get(j);
                JSONArray subsections = new JSONArray(section.get("subsections").toString());
                for (int k = 0; k < subsections.length(); k++) {
                    JSONObject subsection = (JSONObject) subsections.get(k);
                    JSONArray contents = new JSONArray(subsection.get("contents").toString());
                    for (int l = 0; l < contents.length(); l++) {
                        try {
                            JSONObject content = (JSONObject) contents.get(l);
                            String price;
                            try {
                                price = content.get("price").toString();
                            } catch (Exception e) {
                                price="Unknown";
                            }
                            String name = content.get("name").toString();
                            // System.out.println("\n\n\n"+price+"\n\n");
                            list.add(name + "|" + price);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                }
            }

        }
        System.out.println(list);
        return list;

    }

    private  HashMap<String, String> hotelNameToMenu = new HashMap<String, String>();

    public  HashMap<String, String> searchHotels(String name, String location) throws Exception {
        if(name.trim().equals(""))name=null;
        if(location.trim().equals(""))location=null;
        HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://api.locu.com/v2/venue/search/")
                .openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/json");
        httpcon.setRequestProperty("Accept", "application/json");
        httpcon.setDoOutput(true);

        httpcon.setRequestMethod("POST");
        httpcon.connect();

        String nameLocationQuery = "{\"api_key\" : \"f188ba270be1809b6af9ad2c68176d43383775a1\",\"fields\" : [ \"name\", \"location\", \"menus\" ],\"venue_queries\" : [{\"name\" : \""
                + name + "\",\"location\":{\"locality\":\"" + location + "\"} }]}";

        String nameQuery = "{\"api_key\" : \"f188ba270be1809b6af9ad2c68176d43383775a1\",\"fields\" : [  \"name\", \"location\", \"menus\" ],\"venue_queries\" : [{\"name\" : \""
                + name + "\"}]}";
        String locationQuery = "{\"api_key\" : \"f188ba270be1809b6af9ad2c68176d43383775a1\",\"fields\" : [ \"name\", \"location\", \"menus\" ],\"venue_queries\" : [{\"location\":{\"locality\":\""
                + location + "\"} }]}";

        OutputStreamWriter out = new OutputStreamWriter(httpcon.getOutputStream());
        if (name != null) {
            if (location != null) {
                System.out.println("nameLocationQuery");
                out.write(nameLocationQuery);

            } else {
                System.out.println("nameQuery");
                out.write(nameQuery);
            }
        } else {
            System.out.println("locationQuery");
            out.write(locationQuery);
        }
        out.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
        StringBuilder jsonResult = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            jsonResult.append(line);
        }
        br.close();

        JSONObject jsonObject = new JSONObject(jsonResult.toString());
        JSONArray venues = (JSONArray) (jsonObject.get("venues"));
        for (int i = 0; i < venues.length(); i++) {

            JSONObject venue = (JSONObject) venues.get(i);
            JSONObject venueObj = new JSONObject(venue.toString());
            String hotelName = venueObj.get("name").toString();
            String locality = new JSONObject(venueObj.get("location").toString()).get("locality").toString();
            try {
                JSONArray menus = (JSONArray) (venueObj.get("menus"));

                if (menus != null && hotelNameToMenu.size() < 10)
                    hotelNameToMenu.put(hotelName + "-" + locality, menus.toString());
            } catch (Exception e) {

            }

        }
        System.out.println(hotelNameToMenu.keySet());

        return hotelNameToMenu;

    }
}