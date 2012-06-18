/*
 * Solar System Data Retriever
 * Copyright 2010 and beyond, Kévin Ferrare.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.kevinferrare.solarSystemDataRetriever.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProxySelector;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.util.EntityUtils;

/**
 * Stateful HTTP client that keeps cookies data between each request and handles proxy servers.
 * 
 * @author Kévin FERRARE
 * 
 */
public class HttpClientTool {
	private DefaultHttpClient httpclient = new DefaultHttpClient();

	private ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
		public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toByteArray(entity);
			} else {
				return null;
			}
		}
	};

	public HttpClientTool() {
		ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(httpclient.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
		httpclient.setRoutePlanner(routePlanner);
	}

	/**
	 * Sends an HTTP POST request with the given data to the given URL
	 * 
	 * @param url
	 *            the url to send the request
	 * @param nameValuePairs
	 *            the list of parameters to send
	 * @return the response to the request.
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public byte[] postData(String url, List<NameValuePair> nameValuePairs) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		return httpclient.execute(httppost, handler);
	}

	/**
	 * Sends an HTTP GET request to the given URL
	 * 
	 * @param url
	 *            the url to send the request
	 * @return the response to the request.
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public byte[] getData(String url) throws UnsupportedEncodingException, IOException, ClientProtocolException {
		HttpGet httpget = new HttpGet(url);
		return httpclient.execute(httpget, handler);
	}
}
