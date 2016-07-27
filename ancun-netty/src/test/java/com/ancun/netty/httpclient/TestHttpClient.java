package com.ancun.netty.httpclient;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by dell on 2016/3/18.
 */
public class TestHttpClient {

    public static void main(String[] args) throws Exception {

//        URI uri = new URI("http://192.168.0.125:8089");
//
//        String content = "{\n" +
//                "  \"request\": {\n" +
//                "    \"common\": {\n" +
//                "      \"action\": \"regionarea\",\n" +
//                "      \"reqtime\": \"2016-01-08 09:52:03\",\n" +
//                "      \"asyn\": \"false\"\n" +
//                "    },\n" +
//                "    \"content\": {\n" +
//                "      \"phoneNo\": \"13646829663\"\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";
//
//        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
//
//        ClientHttpRequst client = ClientHttpRequst.bulid().setUri(uri).setMethod(HttpMethod.POST).setRequestContent(bytes).execute();
//
//        byte[] responses = ByteStreams.toByteArray(client.getHttpResponse().getBody());
//
//        System.out.println(new String(responses, Charset.forName("UTF-8")));
//
//        client.close();

//                URI uri = new URI("http://192.168.0.125:8089");

//        String content = "{\n" +
//                "  \"request\": {\n" +
//                "    \"common\": {\n" +
//                "      \"action\": \"regionarea\",\n" +
//                "      \"reqtime\": \"2016-01-08 09:52:03\",\n" +
//                "      \"asyn\": \"false\"\n" +
//                "    },\n" +
//                "    \"content\": {\n" +
//                "      \"phoneNo\": \"13646829663\"\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";
//
//        Charset charset = Charset.forName("UTF-8");
//
//        byte[] bytes = content.getBytes(charset);
//
//        System.out.println(HttpClient.post("http://192.168.0.125:8089", bytes, charset));
//        byte[] bytes = new byte[0];
//        System.out.println(bytes.length);
        String uri = "http://121.40.60.163:8081/message/sendMsg?loginname=279&password=YNTmyvQG3q&needstatus=true&mobile=13646829663&content=你的验证码为601162请勿告诉他人，15分钟内有效!&extNo=0";
//        String uri = "http://192.168.0.208:8080/ResponseServlet?url=123&urlRemark=321&emails=123123";
        Charset charset = Charset.forName("UTF-8");
        System.out.println(HttpClient.get(uri, charset));
//        URI uri1 = new URI(uri);
//
//        String params = uri1.getQuery();
//
//        URI uriGet = null;
//
//        if (params != null) {
//
//            // Prepare the HTTP request.
//            QueryStringEncoder encoder = new QueryStringEncoder(uri);
//
//            Map<String, String> paramMap = Splitter.on("&").withKeyValueSeparator("=").split(params);
//
//            for(Map.Entry<String, String> entry : paramMap.entrySet()) {
//                encoder.addParam(entry.getKey(), entry.getValue());
//            }
//
//            uriGet = new URI(encoder.toString());
//        }
//
////        FullHttpRequest request = new DefaultFullHttpRequest();
//        DefaultFullHttpRequest httpRequest = null;
//
//        System.out.println(uri1.getPath());
//        System.out.println(uri1.getQuery());
//        System.out.println(uri1.getRawQuery());
    }

//    @Test
    public void testGet() {

        String uri = "http://121.40.60.163:8081/message/sendMsg?loginname=279&password=YNTmyvQG3q&needstatus=true&mobile=13646829663&content=你的验证码为601162请勿告诉他人，15分钟内有效!&extNo=0";
        Charset charset = Charset.forName("UTF-8");
        System.out.println(HttpClient.get(uri, charset));

    }

//    @Test
    public void testPost() {
        String content = "{\n" +
                "  \"request\": {\n" +
                "    \"common\": {\n" +
                "      \"action\": \"regionarea\",\n" +
                "      \"reqtime\": \"2016-01-08 09:52:03\",\n" +
                "      \"asyn\": \"false\"\n" +
                "    },\n" +
                "    \"content\": {\n" +
                "      \"phoneNo\": \"13646829663\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Charset charset = Charset.forName("UTF-8");

        byte[] bytes = content.getBytes(charset);

        System.out.println(HttpClient.post("http://192.168.0.125:8089", bytes, charset));
    }

//    @Test
    public void testUri() throws URISyntaxException {
//        String uri = "http://192.168.0.125:8089/abc?a=b&b=c&d=测试";
        String uri = "http://192.168.0.200:7080/";
//        QueryStringEncoder encoder = new QueryStringEncoder(uri);
//        URI uri1 = new URI(encoder.toString());
        URI uri1 = new URI(uri);
        System.out.println(uri1.toASCIIString());
        String path = uri1.getPath();
        System.out.println(uri1.getPath());
        System.out.println(path.substring(1));
        QueryStringDecoder decoder = new QueryStringDecoder(uri1.toASCIIString());
        System.out.println(decoder.path());
        System.out.println(decoder.parameters());
        System.out.println(decoder.uri());
    }

}
