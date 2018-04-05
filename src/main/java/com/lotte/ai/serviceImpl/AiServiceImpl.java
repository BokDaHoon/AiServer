package com.lotte.ai.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.lotte.ai.dto.AnalysisImageDTO;
import com.lotte.ai.service.AiService;

import file.Client;
import file.HandlerFile;

@Service
public class AiServiceImpl implements AiService {
	
	private static final int OBJECT_NAME = 1;
	private static final int OBJECT_PERCENT = 0;

	@Override
	public String uploadFile(MultipartHttpServletRequest sourceFile) {
		String filePath = "/home/tensorflow2018/datasets/shoes/fromusers";
		HandlerFile handlerFile = new HandlerFile(sourceFile, filePath);
		Map<String, List<String>> fileNames = handlerFile.getUploadFileName();
		
		// 실제저장파일명과 원본파일명 DB저장처리
		System.err.println(fileNames.toString());
		String fileName = handlerFile.getFileFullPath();
		System.out.println("File Name : " + fileName);
		
		return fileName;
	}

	@Override
	public List<AnalysisImageDTO> fileRecognize(String fileName) {
		// 클라이언트 객체
		Client client = new Client(fileName);
		String receiveData = client.getResult();
		System.out.println("결과 : " + receiveData);
		String[] parseResult = getParseString(receiveData);
		
		return convertStringToDTO(parseResult);
	}
	
	private static String[] getParseString(String str) {
        str = str.substring(1, str.length() - 1).replace("},{", "/");
        str = str.substring(1, str.length() - 1);
        StringTokenizer st = new StringTokenizer(str, "/");
        int tokenLength = st.countTokens();

        String[] resultParam = new String[tokenLength];
        for (int i = 0; i < tokenLength; i++) {
            resultParam[i] = st.nextToken();
        }
        return resultParam;
    }

    private static List<AnalysisImageDTO> convertStringToDTO(String[] result) {
        ArrayList<AnalysisImageDTO> resultParam = new ArrayList<AnalysisImageDTO>();
        int length = result.length;
        for (int i = 0; i < length; i++) {
            String[] recognizedObject = result[i].split(", ");
            resultParam.add(new AnalysisImageDTO().setObjectName(recognizedObject[OBJECT_NAME])
                       							  .setObjectPercent(recognizedObject[OBJECT_PERCENT]));
        }
        return resultParam;
    }

}
