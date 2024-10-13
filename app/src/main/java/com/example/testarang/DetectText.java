package com.example.testarang;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;
import com.google.api.gax.core.FixedCredentialsProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetectText extends Activity {

    private static final String TAG = "DetectText";
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ExecutorService 초기화
        executorService = Executors.newSingleThreadExecutor();

        // activity_detect_text.xml 레이아웃 설정
        setContentView(R.layout.activity_detect_text);

        // 버튼 클릭 이벤트 설정
        Button detectTextButton = findViewById(R.id.detectTextButton);
        detectTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button clicked!");
                // 비동기 작업 실행
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            detectTextFromDrawable();
                        } catch (IOException e) {
                            Log.e(TAG, "Error during text detection: " + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    public void detectTextFromDrawable() throws IOException {
        Log.i(TAG, "Starting text detection");
        // drawable에서 이미지를 불러옵니다.
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.example_img);
        if (bitmap == null) {
            Log.e(TAG, "Error: Bitmap is null");
            return;
        }

        // Bitmap을 ByteString으로 변환
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ByteString imgBytes = ByteString.copyFrom(byteArray);

        // Google Vision API에 요청 준비
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // assets 폴더에서 인증 파일 불러오기
        AssetManager assetManager = getAssets();
        InputStream credentialsStream = assetManager.open("whosee-438207-fa546ccff839.json");

        // GoogleCredentials 객체를 생성하고 ImageAnnotatorSettings에 설정
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // ImageAnnotatorClient 생성 및 요청 실행
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
            Log.i(TAG, "Client created, sending request...");
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    Log.e(TAG, "Error: " + res.getError().getMessage());
                    return;
                }

                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    Log.i(TAG, "Text: " + annotation.getDescription());
                    Log.i(TAG, "Position: " + annotation.getBoundingPoly());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Google Vision API call failed: " + e.getMessage());
        } finally {
            credentialsStream.close();  // 인증 파일 스트림 닫기
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}