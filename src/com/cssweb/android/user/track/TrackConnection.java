package com.cssweb.android.user.track;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TrackConnection
{
    private final String DEBUG_TAG = "Connection";

    private HttpURLConnection m_conn = null;

    private String req = null;

    private String res = null;

    private JSONObject j = null;

    public TrackConnection(String req)
    {
        this.req = req;
    }

    public void close()
    {
        if (this.m_conn == null)
        {
            return;
        }
        this.m_conn.disconnect();
        this.m_conn = null;
    }

    public boolean connect()
    {
        try
        {
            URL url = new URL(req);
            this.m_conn = (HttpURLConnection) url.openConnection();
            this.m_conn.setConnectTimeout(10000);
            this.m_conn.setReadTimeout(6000);
            this.m_conn.setDoInput(true);
            this.m_conn.setDoOutput(true);
        }
        catch (MalformedURLException e)
        {
            Log.e(DEBUG_TAG, e.toString());
            return false;
        }
        catch (IOException e)
        {
            Log.e(DEBUG_TAG, e.toString());
            return false;
        }
        return true;
    }

    public JSONObject execute()
    {
        if (req == null || req.length() == 0)
        {
            return null;
        }
        InputStreamReader in = null;
        BufferedReader buffer = null;
        StringBuilder text = new StringBuilder();
        try
        {
            in = new InputStreamReader(this.m_conn.getInputStream(), "UTF-8");
            buffer = new BufferedReader(in);
            while ((res = buffer.readLine()) != null)
            {
                //arrayOutputStream.write(res.getBytes());
                text.append(res);
            }
            //byte[] b = arrayOutputStream.toByteArray();
            //String response = new String(b);
            j = new JSONObject(text.toString());
            //arrayOutputStream.close();
            //in.close();
        }
        catch (IOException e)
        {
            j = null;
            in = null;
            buffer = null;
            Log.e(DEBUG_TAG, e.toString());
        }
        catch (JSONException e)
        {
            j = null;
            in = null;
            buffer = null;
            Log.e(DEBUG_TAG, e.toString());
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ignored)
            {
            }
            try
            {
                if (buffer != null)
                {
                    buffer.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
        return j;
    }

    public String getF10Data()
    {
        if (req == null || req.length() == 0)
        {
            return null;
        }
        InputStreamReader in = null;
        BufferedReader buffer = null;
        StringBuilder text = new StringBuilder();
        try
        {
            in = new InputStreamReader(this.m_conn.getInputStream(), "UTF-8");
            buffer = new BufferedReader(in);
            while ((res = buffer.readLine()) != null)
            {
                text.append(res).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(DEBUG_TAG, e.toString());
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ignored)
            {
            }
            try
            {
                if (buffer != null)
                {
                    buffer.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
        return text.toString();
    }

    public String getData()
    {
        if (req == null || req.length() == 0)
        {
            return null;
        }
        try
        {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            InputStreamReader in = new InputStreamReader(this.m_conn.getInputStream(), "UTF-8");
            BufferedReader buffer = new BufferedReader(in);
            String res;
            while ((res = buffer.readLine()) != null)
            {
                arrayOutputStream.write(res.getBytes());
            }
            //in.close();
            byte[] b = arrayOutputStream.toByteArray();
            return new String(b);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(DEBUG_TAG, e.toString());
            return null;
        }
    }

    public String getUserLevel()
    {
        InputStreamReader sr;
        try
        {
            sr = new InputStreamReader(this.m_conn.getInputStream(), "UTF-8");
            StringBuilder builder = new StringBuilder();
            for (int bt; (bt = sr.read()) != -1; )
            {
                builder.append((char) bt);
            }
            sr.close();
            Log.i("#######getUserLevel#######", builder.toString() + "<<<<<<<<<<<<");
            return builder.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject tradeReq()
    {
        if (req == null || req.length() == 0)
        {
            return null;
        }
        InputStreamReader in = null;
        BufferedReader buffer = null;
        StringBuilder text = new StringBuilder();
        try
        {
            in = new InputStreamReader(this.m_conn.getInputStream(), "UTF-8");
            buffer = new BufferedReader(in);
            while ((res = buffer.readLine()) != null)
            {
                //arrayOutputStream.write(res.getBytes());
                text.append(res);
            }
            //byte[] b = arrayOutputStream.toByteArray();
            //String response = new String(b);
            String response = text.toString();
            response = response.replaceAll("\\*", "\\+");
            String tradeRes = "";
            try
            {
                tradeRes = TrackBase64Encoder.decode(response, "gb2312");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (tradeRes.contains("ORA"))
            {
                tradeRes = getMeta("cssweb_msg\":\"", "\",\"item", tradeRes);
                tradeRes = tradeRes.replaceAll("\"", "");
                tradeRes = tradeRes.replaceAll("\n", " ");
                tradeRes =
                    "{\"cssweb_code\":\"error\",\"cssweb_type\":\"STOCK_CANCEL\",\"cssweb_msg\":\"" +
                        tradeRes + "\",\"item\":[]}";
            }
            j = new JSONObject(tradeRes);
            //arrayOutputStream.close();
            //in.close();
        }
        catch (IOException e)
        {
            j = null;
            in = null;
            buffer = null;
            Log.e(DEBUG_TAG, e.toString());
        }
        catch (JSONException e)
        {
            j = null;
            in = null;
            buffer = null;
            Log.e(DEBUG_TAG, e.toString());
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ignored)
            {
            }
            try
            {
                if (buffer != null)
                {
                    buffer.close();
                }
            }
            catch (IOException ignored)
            {
            }
        }
        Log.e("<<<<<<<<<<<柜台返回数据>>>>>>>>", j + "");
        return j;
    }

    @SuppressWarnings({"UnusedAssignment"}) public JSONObject execute(boolean isWrapped)
    {
        StringBuilder data = new StringBuilder();
        if (req == null || req.length() == 0)
        {
            return null;
        }
        BufferedReader buffer;
        ByteArrayOutputStream arrayOutputStream;
        InputStreamReader in;
        try
        {
            in = new InputStreamReader(this.m_conn.getInputStream(), "UTF-8");
            arrayOutputStream = new ByteArrayOutputStream();
            buffer = new BufferedReader(in);
            while ((res = buffer.readLine()) != null)
            {
                arrayOutputStream.write(res.getBytes());
            }
            in.close();
            byte[] b = arrayOutputStream.toByteArray();
            if (isWrapped)
            {
                data.append("{\"data\":[");
            }

            if (b != null)
            {
                data.append(new String(b));
            }

            if (isWrapped)
            {
                data.append("]}");
            }
            j = new JSONObject(data.toString());
        }
        catch (IOException e)
        {
            j = null;
            in = null;
            buffer = null;
            arrayOutputStream = null;
            Log.e(DEBUG_TAG, e.toString());
        }
        catch (JSONException e)
        {
            j = null;
            in = null;
            buffer = null;
            arrayOutputStream = null;
            Log.e(DEBUG_TAG, e.toString());
        }
        return j;
    }

    public static String getMeta(String startStr, String endStr, String srcStr)
    {
        String returnValue = "";
        try
        {
            int index = srcStr.indexOf(startStr) + startStr.length();
            srcStr = srcStr.substring(index);
            returnValue = srcStr.substring(0, srcStr.indexOf(endStr));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return returnValue;
    }
}
