package com.example.demo.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.example.demo.dao.CommentRepository;
import com.example.demo.dao.FeedRepository;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Feed;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class FeedService {

	@Autowired
	private FeedRepository feedRepository;
	@Value("${kakao.client_id}")
    private String apikey;
	
	//피드 리스팅 
	public List<Feed> listFeed(long groupNo){
		return feedRepository.findByGroupNoOrderByFeedDateDesc(groupNo);
	}
	
	//해당 그룹에서 해당 날짜에 피드 있는지 확인
	public Feed checkFeedExists(long groupNo, LocalDate feedDate) {
		return feedRepository.findByGroupNoAndFeedDate(groupNo, feedDate);
	}
	
	//피드 저장
	public Feed saveFeed(Feed feed) {
		return feedRepository.save(feed);
	}
	
	//피드의 최고 번호+1 가져오기
	public long getNextFeedNo() {
		return feedRepository.getNextFeedNo();
	}
	
	//사진정보의 위치 데이터 가져오기
	public String getLocation(MultipartFile multipartFile) {
	    String result = "";
	    double lat = 0.0;
	    double lon = 0.0;

	    try {
	        byte[] bytes = multipartFile.getBytes();
	        InputStream is = new ByteArrayInputStream(bytes);

	        // 메타데이터 읽기
	        Metadata metadata = ImageMetadataReader.readMetadata(is);
	        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

	        // 위도와 경도 추출
	        if (gpsDirectory != null && 
	            gpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE) && 
	            gpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE)) {
	            lat = gpsDirectory.getGeoLocation().getLatitude();
	            lon = gpsDirectory.getGeoLocation().getLongitude();
	            result = convertToAddr(lon, lat);
	        }
	    } catch (IOException | ImageProcessingException e) {
	        return "오류: " + e.getMessage();
	    }
	    return result;
	}


	
	//역지오코딩: 위도 경도 이용해 주소문자열로 변경하는 메소드 ex) 서울 마포구
	public String convertToAddr(double lon, double lan) {
		String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="+lon+"&y="+lan+"&input_coord=WGS84";
		String addr = "";
		try {
			//카카오api 이용해 url -> json데이터 -> 주소 추출
			addr = getPassingAddress(getJSONData(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addr;
	}
	
	//역지오코딩sub2) json데이터에서 시도, 구단위 추출하는 메소드
	private static String getPassingAddress(String jsonString) {
	    String value = "";
	    JSONObject jObj = new JSONObject(jsonString);
	    JSONObject meta = jObj.getJSONObject("meta");
	    long size = meta.getLong("total_count");

	    if (size > 0) { //응답받은 주소정보가 있다면
	        JSONArray jArray = jObj.getJSONArray("documents");
	        JSONObject subJobj = jArray.getJSONObject(0); //documents의 첫번째 object가 road_address임
	        JSONObject roadAddress = subJobj.optJSONObject("road_address");

	        if (roadAddress == null) { //도로명주소 정보가 없다면 지번주소 정보를 가져옴
	            JSONObject subsubJobj = subJobj.getJSONObject("address");
	            value = subsubJobj.getString("region_1depth_name");
	            value += " "+subsubJobj.getString("region_2depth_name");
	        } else { //도로명주소 정보가 있다면 도로명주소 정보를 가져옴
	            value = roadAddress.getString("region_1depth_name");
	            value += " "+roadAddress.getString("region_2depth_name");
	        }
	    }
	    return value;
	}
	
	//역지오코딩sub1) 카카오api에 요청해 json데이터 반환하는 메소드 
	public String getJSONData(String apiUrl) throws Exception {
		String jsonString = new String();
		String buf;
		URL url = new URL(apiUrl);
		HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		String auth = "KakaoAK "+apikey;
		conn.setRequestMethod("GET");
	    conn.setRequestProperty("X-Requested-With", "curl");
	    conn.setRequestProperty("Authorization", auth);
	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(
	    		conn.getInputStream(), "UTF-8"));
	    while((buf=br.readLine())!=null) {
	    	jsonString += buf;
	    }
	    return jsonString;
		
	}

}
