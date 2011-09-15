//////////////////////license & copyright header///////////////////////
//                                                                   //
//                Copyright (c) 1998 by Kevin Kelley                 //
//                                                                   //
// This program is free software; you can redistribute it and/or     //
// modify it under the terms of the GNU General Public License as    //
// published by the Free Software Foundation; either version 2 of    //
// the License, or (at your option) any later version.               //
//                                                                   //
// This program is distributed in the hope that it will be useful,   //
// but WITHOUT ANY WARRANTY; without even the implied warranty of    //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the     //
// GNU General Public License for more details.                      //
//                                                                   //
// You should have received a copy of the GNU General Public License //
// along with this program in the file 'gpl.html'; if not, write to  //
// the Free Software Foundation, Inc., 59 Temple Place - Suite 330,  //
// Boston, MA 02111-1307, USA, or contact the author:                //
//                                                                   //
//                       Kevin Kelley  <kelley@iguana.ruralnet.net>  //
//                                                                   //
////////////////////end license & copyright header/////////////////////
package com.cssweb.android.common;

/**
*   Provides encoding of raw bytes to base64-encoded characters, and
*  decoding of base64 characters to raw bytes.
*
* @author Kevin Kelley (kelley@iguana.ruralnet.net)
* @version 1.0
* @date 06 August 1998
*/
public class Base64 {

/**
* returns an array of base64-encoded characters to represent the
* passed data array.
*
* @param data the array of bytes to encode
* @return base64-coded character array.
*/
public static char[] encode(byte[] data)
{
    char[] out = new char[((data.length + 2) / 3) * 4];

    //
    // 3 bytes encode to 4 chars.  Output is always an even
    // multiple of 4 characters.
    //
    for (int i=0, index=0; i<data.length; i+=3, index+=4) {
        boolean quad = false;
        boolean trip = false;

        int val = (0xFF & (int) data[i]);
        val <<= 8;
        if ((i+1) < data.length) {
            val |= (0xFF & (int) data[i+1]);
            trip = true;
        }
        val <<= 8;
        if ((i+2) < data.length) {
            val |= (0xFF & (int) data[i+2]);
            quad = true;
        }
        out[index+3] = alphabet[(quad? (val & 0x3F): 64)];
        val >>= 6;
        out[index+2] = alphabet[(trip? (val & 0x3F): 64)];
        val >>= 6;
        out[index+1] = alphabet[val & 0x3F];
        val >>= 6;
        out[index+0] = alphabet[val & 0x3F];
    }
    return out;
}

/**
* Returns an array of bytes which were encoded in the passed
* character array.
*
* @param data the array of base64-encoded characters
* @return decoded data array
*/
public static byte[] decode(char[] data) throws Exception
{
    int len = ((data.length + 3) / 4) * 3;
    if (data.length>0 && data[data.length-1] == '=') --len;
    if (data.length>1 && data[data.length-2] == '=') --len;
    byte[] out = new byte[len];

    int shift = 0;   // # of excess bits stored in accum
    int accum = 0;   // excess bits
    int index = 0;

    for (int ix=0; ix<data.length; ix++)
    {
        int value = codes[ data[ix] & 0xFF ];   // ignore high byte of char
        if ( value >= 0 ) {                     // skip over non-code
            accum <<= 6;            // bits shift up by 6 each time thru
            shift += 6;             // loop, with new bits being put in
            accum |= value;         // at the bottom.
            if ( shift >= 8 ) {     // whenever there are 8 or more shifted in,
                shift -= 8;         // write them out (from the top, leaving any
                out[index++] =      // excess at the bottom for next iteration.
                    (byte) ((accum >> shift) & 0xff);
    }   }   }
    if (index != out.length)
        throw new Exception("miscalculated data length!");//Error("miscalculated data length!");

    return out;
}

//
// code characters for values 0..63
//
private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

//
// lookup table for converting base64 characters to value in range 0..63
//
private static  byte[] codes = new byte[256];
static {
    for (int i=0; i<256; i++) codes[i] = -1;
    for (int i = 'A'; i <= 'Z'; i++) codes[i] = (byte)(     i - 'A');
    for (int i = 'a'; i <= 'z'; i++) codes[i] = (byte)(26 + i - 'a');
    for (int i = '0'; i <= '9'; i++) codes[i] = (byte)(52 + i - '0');
    codes['+'] = 62;
    codes['/'] = 63;
}

public static String ByteToHex(byte[] b)
{
	String hs = "";
	String stmp = "";
	for (int n = 0; n < b.length; n++)
	{
		stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
		if (stmp.length() == 1)
			hs = hs + "0" + stmp;
		else
			hs = hs + stmp;

	}
	return hs.toUpperCase();
}

/**
 * 网络传输时需要将+替换为*
 * @param args
 */
	public static void main(String[] args)
	{
		String source = "{\"ext\":\"1\",\"trdpwd\":\"5M80xrkWXoMDtrEZbv9xbA$$\",\"ram\":0.6421510246582329,\"hardinfo\":\"\",\"isSafe\":\"0\",\"KCBP_PARAM_SERVICENAME\":\"410530\",\"KCBP_PARAM_RESERVED\":\"3106\",\"clientip\":\"10.103.35.25\",\"mac\":\"\",\"KCBP_PARAM_PACKETTYPE\":2,\"custid\":\"3515605\",\"funcid\":\"410530\",\"fundid\":\"68008601\",\"custorgid\":\"3106\",\"orgid\":\"3106\",\"netaddr\":\"010103035025\",\"cssweb_type\":\"GET_FUNDS\",\"operway\":\"7\",\"moneytype\":\"\"}";
        //String source = "5Zub5bed5a6c5a6+5pSv5YWs5Y+4";
        String option = "-e";
        try{
			if (option.equals("-e"))
			{
				String output = new String(encode(source.getBytes()));
				System.out.println(output);
			}
			else if (option.equals("-d"))
			{
				String output = new String(decode(source.toCharArray()));
				System.out.println(output);
			}
        }catch(Exception e){
        	
        }
		
	}


}

